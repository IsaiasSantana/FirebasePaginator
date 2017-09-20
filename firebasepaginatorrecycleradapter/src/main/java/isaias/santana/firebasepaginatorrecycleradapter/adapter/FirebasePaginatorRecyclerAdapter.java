package isaias.santana.firebasepaginatorrecycleradapter.adapter;

import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import isaias.santana.firebasepaginatorrecycleradapter.adapter.delegates.AdapterDelegate;
import isaias.santana.firebasepaginatorrecycleradapter.adapter.delegates.AdapterDelegatesManager;
import isaias.santana.firebasepaginatorrecycleradapter.adapter.delegates.ProgressBarDelegate;
import isaias.santana.firebasepaginatorrecycleradapter.adapter.interfaces.ViewType;
import isaias.santana.firebasepaginatorrecycleradapter.adapter.itens.ItemFirebasePaginatorAdapter;
import isaias.santana.firebasepaginatorrecycleradapter.adapter.itens.ProgressBarItem;


/**
 * Um adpatador genérico para paginar os dados em um nó no Firebase database.
 *
 * @author Isaías Santana on 28/06/17.
 *         email: isds.santana@gmail.com
 */

public abstract class FirebasePaginatorRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter {
    private static final String TAG = FirebasePaginatorRecyclerAdapter.class.getSimpleName();

    /* Limite padrão de dados recuperado do banco */
    private static final int DEFAULT_LIMIT_PER_PAGE = 21;

    /* número total de dados que será buscado no banco por vez */
    private final int totalDataPerPage;

    /* Initial ChildEventListener */
    private final ChildEventListener mChildEventListener;

    /* Guarda as referências de todos os ChildEventListener */
    private final ArrayList<ChildEventListener> mChildEventListeners = new ArrayList<>();

    /* Layout que será inflado */
    protected int layout;

    /* O tipo do objeto que deve ser convertido quando recuperado do banco de dados */
    private Class<T> modelClass;

    /* O ViewHolder que será utilizado para exibir os objetos */
    private Class<VH> viewHolderClass;

    /* Guardas os delegates */
    private ArrayList<ViewType> viewTypes = new ArrayList<>();

    /* Gerencia qual tipo de view neste adapater deve ser exibida*/
    private AdapterDelegatesManager<List<ViewType>> manager = new AdapterDelegatesManager<>();

    /* Guarda a chave do próximo elemento que deve ser buscado do banco */
    private String nextKey;

    /* Guarda a última armazenada por nextKey */
    private String previewsKey;

    /* Esta Query escuta os eventos sobre um nó utlizando o listener OnChildEventListener*/
    private Query queryListener;

    /*Flag para indicar se está buscando dados ou não */
    private boolean isLoading;

    /* Flag que indica se é para inserir um item no adaptador, quando um novo item é adicionado ao Firebase */
    private boolean isToAddNewItemAddedToFirebase;

    /* Flag que indica para remover um item quando esse é removido do Firebase */
    private boolean isToRemoveItemRemovedFromFirebase;

    /* is first call ? */
    private boolean isInitialLoad;

    /* is ascending order */
    private boolean isAscendingOrder;

    /* callback */
    private OnLoadDone onLoadDone;

    /**
     * Constructor.
     *
     * @param modelClass       Your data model class.
     * @param layout           your layout to inflate the viewHolder
     * @param viewHolderClass  Your viewHolder class.
     * @param totalDataPerPage The number of data recovered from database.
     */
    private FirebasePaginatorRecyclerAdapter(Class<T> modelClass,
                                             @LayoutRes int layout,
                                             Class<VH> viewHolderClass,
                                             final int totalDataPerPage) {
        this.modelClass = modelClass;
        this.layout = layout;
        this.viewHolderClass = viewHolderClass;

        if (totalDataPerPage <= 0) {
            this.totalDataPerPage = DEFAULT_LIMIT_PER_PAGE;
        } else {
            this.totalDataPerPage = totalDataPerPage + 1;
        }

        isLoading = false;
        isInitialLoad = true;
        nextKey = "";
        previewsKey = "";

        manager = new AdapterDelegatesManager<>();
        manager.addDelegate(new ProgressBarDelegate(), ViewType.VIEW_PROGRESS_BAR_ITEM)
                .addDelegate(new UserAdapterDelegate(), ViewType.VIEW_DATA_DATA_SNAPSHOT_ITEM);

        mChildEventListener = new ChildEventListener() {
            int contador = 0;

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (isInitialLoad && !isAscendingOrder) {
                    viewTypes.add(new ItemFirebasePaginatorAdapter(dataSnapshot));
                    notifyItemInserted(viewTypes.size() - 1);
                    nextKey = dataSnapshot.getKey();
                    contador += 1;
                    if (contador == totalDataPerPage) {
                        isInitialLoad = false;
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final int index = getIndexKey(dataSnapshot.getKey());
                if (index != -1) {
                    viewTypes.set(index, new ItemFirebasePaginatorAdapter(dataSnapshot));
                    notifyItemChanged(index);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final int index = getIndexKey(dataSnapshot.getKey());
                if (index != -1) {
                    viewTypes.remove(index);
                    notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //Ignored for now.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (onLoadDone != null) {
                    onLoadDone.error();
                }
            }
        };
    }

    /**
     * @param modelClass       A classe de modelo que será utilizada para recuperar os dados.
     * @param layout           O recurso de layout utilizado para exibir os itens.
     * @param viewHolderClass  O ViewHolder para exibir os dados.
     * @param query            A referência para onde os dados estão.
     * @param totalDataPerPage o total de dados que será recuperado
     */
    public FirebasePaginatorRecyclerAdapter(final Class<T> modelClass,
                                            @LayoutRes int layout,
                                            Class<VH> viewHolderClass,
                                            Query query,
                                            final int totalDataPerPage,
                                            final boolean isAscendingOrder) {

        this(modelClass, layout, viewHolderClass, totalDataPerPage);
        if (query == null) {
            throw new NullPointerException("The query is null");
        }

        this.queryListener = query;
        this.isAscendingOrder = isAscendingOrder;
        this.isToAddNewItemAddedToFirebase = false;
        this.isToRemoveItemRemovedFromFirebase = false;
        if (isAscendingOrder) {
            //Get the first data.
            queryListener.limitToLast(this.totalDataPerPage)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (onLoadDone != null) {
                                onLoadDone.hadSuccessLoad();
                            }

                            if (dataSnapshot == null) return;

                            final Deque<DataSnapshot> snapshots;
                            snapshots = new ArrayDeque<>(FirebasePaginatorRecyclerAdapter.this.totalDataPerPage);

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshots.addFirst(snapshot);
                            }

                            if (snapshots.isEmpty()) {
                                return;
                            }

                            // next key to load more data.
                            nextKey = snapshots.getLast().getKey();

                            //ASCENDIG order
                            while (!snapshots.isEmpty()) {
                               final DataSnapshot snapshot = snapshots.removeFirst();
                                FirebasePaginatorRecyclerAdapter.this
                                        .viewTypes
                                        .add(new ItemFirebasePaginatorAdapter(snapshot));

                                notifyItemInserted(FirebasePaginatorRecyclerAdapter.this.viewTypes.size() - 1);

                                Log.d(TAG, snapshot.getKey());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            if (onLoadDone != null) {
                                onLoadDone.error();
                            }
                        }
                    });   //[End-Listener]
            queryListener.limitToLast(this.totalDataPerPage).addChildEventListener(mChildEventListener);
            mChildEventListeners.add(mChildEventListener);

        } else {
            queryListener.limitToFirst(totalDataPerPage).addChildEventListener(mChildEventListener);
            mChildEventListeners.add(mChildEventListener);
        }

    }


    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return manager.onCreateViewHolder(parent, viewType);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        manager.onBindViewHolder(viewTypes, position, viewHolder);
    }

    @Override
    public final int getItemViewType(int position) {
        return manager.getItemViewType(viewTypes, position);
    }


    @Override
    public final int getItemCount() {
        return viewTypes.size();
    }

    @Override
    public final long getItemId(int position) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return ((ItemFirebasePaginatorAdapter) viewTypes.get(position))
                .getDataSnapshot()
                .getKey()
                .hashCode();
    }

    /**
     * Load more data if it exist.
     */
    public final void loadMore() {
        if (isInitialLoad) {
            isInitialLoad = false;
        }

        if (!isLoading && !previewsKey.equals(nextKey)) {

            isLoading = true;
            previewsKey = nextKey;

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    //Adiciona o Progress bar
                    //Add the progress bar.
                    viewTypes.add(new ProgressBarItem());
                    notifyItemInserted(viewTypes.size() - 1);
                }
            });
            if (isAscendingOrder) {

                final ChildEventListener listener = queryListener
                        .orderByKey()
                        .endAt(nextKey)
                        .limitToLast(totalDataPerPage)
                        .addChildEventListener(getListenerForAscendingOrder());

                mChildEventListeners.add(listener);

            } else {
                final ChildEventListener listener = queryListener
                        .orderByKey()
                        .startAt(nextKey.equals("") ? null : nextKey)
                        .limitToFirst(totalDataPerPage)
                        .addChildEventListener(getListenerForDescendingOrder());

                mChildEventListeners.add(listener);
            }

        }//[End-IF]
    }


    /**
     * Check if is loading more data.
     */
    public final boolean isLoading() {
        return isLoading;
    }

    /**
     * Remove all childEventListener.
     */
    public final void removeListeners() {
        for (ChildEventListener cel : mChildEventListeners) {
            if (queryListener != null && cel != null) {
                queryListener.removeEventListener(cel);
            }
        }
    }

    public final void setToAddNewItemAddedToFirebase(boolean toAddNewItemAddedToFirebase) {
        isToAddNewItemAddedToFirebase = toAddNewItemAddedToFirebase;
    }

    public final void setToRemoveItemRemovedFromFirebase(boolean toRemoveItemRemovedFromFirebase) {
        isToRemoveItemRemovedFromFirebase = toRemoveItemRemovedFromFirebase;
    }

    /**
     * Portuguese Brazilian:
     * Implemente este método para popular os dados que serão exibidos. Toda vez que um evento
     * ocorrer em uma dada localização no Firebase esse método será chamado para cada item que for
     * modificado. O comportamento padrão é apenas chamar este método toda vez que um dado for
     * alterado, removido ou quando for buscar mais páginas de dados.
     *
     * @param viewHolder A view para popular, essa view corresponde ao layout passado na realização da instância do objeto.
     * @param model      O objeto do model que será usado para popular viewHolder.
     * @param position   a posição do elemento na view que está sendo populado.
     */
    abstract protected void populateViewHolder(VH viewHolder, T model, int position);

    /**
     * Util to check if the data was loaded.
     *
     * @param onLoadDone the listener to check.
     */
    public final void setOnLoadDone(OnLoadDone onLoadDone) {
        if (onLoadDone == null)
            throw new NullPointerException("OnLoadDone não pode ser nulo.");

        this.onLoadDone = onLoadDone;
    }

    private int getIndexKey(String searchKey) {
        int contador = 0;
        for (ViewType vt : viewTypes) {
            if (((ItemFirebasePaginatorAdapter) vt).getDataSnapshot().getKey().equals(searchKey)) {
                return contador;
            } else {
                contador += 1;
            }
        }
        return -1;
    }

    private ChildEventListener getListenerForAscendingOrder() {
        return new ChildEventListener() {
            final ArrayDeque<DataSnapshot> snapshots = new ArrayDeque<>(totalDataPerPage);
            int counter = 0;

            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "getListenerForAscendingOrder() -> isToAdd: out " + dataSnapshot.getKey());
                Log.d(TAG, "isLoading: " + isLoading + " isToAddItemAddedToFirebase: " + isToAddNewItemAddedToFirebase);
                if (isInitialLoad) {
                    return;
                }
                if (!isLoading && isToAddNewItemAddedToFirebase) {
                    Log.d(TAG, "isToAdd inside: " + dataSnapshot.getKey());
                    viewTypes.add(new ItemFirebasePaginatorAdapter(dataSnapshot));
                    notifyItemInserted(viewTypes.size() - 1);
                    return;
                }

                if (counter == 0) {
                    counter += 1;
                    snapshots.addFirst(dataSnapshot);
                    nextKey = dataSnapshot.getKey();
                    final int lastPosition = viewTypes.size() - 1;
                    viewTypes.remove(lastPosition);
                    notifyItemRemoved(lastPosition);
                } else {
                    counter += 1;
                    snapshots.addFirst(dataSnapshot);
                }

                if (counter == totalDataPerPage) {
                    while (!snapshots.isEmpty()) {
                        final DataSnapshot snapshot = snapshots.removeFirst();
                        if (snapshot.getKey().equals(previewsKey)) {
                            continue;
                        }
                        FirebasePaginatorRecyclerAdapter.this
                                .viewTypes
                                .add(new ItemFirebasePaginatorAdapter(snapshot));

                        notifyItemInserted(
                                FirebasePaginatorRecyclerAdapter.this.viewTypes.size() - 1);

                        Log.d(TAG, snapshot.getKey());
                    }
                    isLoading = false;
                } else if (previewsKey.equals(dataSnapshot.getKey())) {
                                /*
                                    Has less data than totalDataPerPage
                                 */
                    while (!snapshots.isEmpty()) {
                        final DataSnapshot snapshot = snapshots.removeFirst();
                        if (snapshot.getKey().equals(previewsKey)) {
                            continue;
                        }
                        FirebasePaginatorRecyclerAdapter.this
                                .viewTypes
                                .add(new ItemFirebasePaginatorAdapter(snapshot));

                        notifyItemInserted(
                                FirebasePaginatorRecyclerAdapter.this.viewTypes.size() - 1);

                        Log.d(TAG, snapshot.getKey());
                    }
                    isLoading = false;
                }
                Log.d(TAG, "isLoading is: " + isLoading);
            }//[End-onChildAdded]

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
                final int index = getIndexKey(dataSnapshot.getKey());
                if (index != -1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewTypes.set(index, new ItemFirebasePaginatorAdapter(dataSnapshot));
                            notifyItemChanged(index);
                        }
                    }, 200);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "ChildRemoved: " + dataSnapshot.getKey());
                if (!isToRemoveItemRemovedFromFirebase) {
                    return;
                }
                final int index = getIndexKey(dataSnapshot.getKey());
                if (index != -1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewTypes.remove(index);
                            notifyItemRemoved(index);
                        }
                    }, 200);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // ignored for now.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (onLoadDone != null) {
                    onLoadDone.error();
                }
            }
        };
    }

    private ChildEventListener getListenerForDescendingOrder() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!isLoading && !isToAddNewItemAddedToFirebase) {
                    return;
                }
                viewTypes.add(new ItemFirebasePaginatorAdapter(dataSnapshot));
                notifyItemInserted(viewTypes.size() - 1);
                nextKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final int index = getIndexKey(dataSnapshot.getKey());
                if (index != -1) {
                    viewTypes.set(index, new ItemFirebasePaginatorAdapter(dataSnapshot));
                    notifyItemChanged(index);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (!isToRemoveItemRemovedFromFirebase) {
                    return;
                }
                final int index = getIndexKey(dataSnapshot.getKey());
                if (index != -1) {
                    viewTypes.remove(index);
                    notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //Ignored for now.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (onLoadDone != null) {
                    onLoadDone.error();
                }
            }
        };
    }

    public interface OnLoadDone {
        /**
         * Success, data loaded.
         */
        void hadSuccessLoad();

        /**
         * An error occurred.
         */
        void error();
    }

    /**
     * Portuguese Brazilian:
     * Classe que é responsável inflar a view do usuário.
     * <p>
     * Inflate the user view.
     */
    private class UserAdapterDelegate implements AdapterDelegate<List<ViewType>> {

        @Override
        public boolean isForViewType(@NonNull List<ViewType> items, int position) {
            return items.get(position).getViewType() == ViewType.VIEW_DATA_DATA_SNAPSHOT_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            try {
                Constructor<VH> constructor = viewHolderClass.getConstructor(View.class);
                return constructor.newInstance(view);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                     @NonNull List<ViewType> items,
                                     int position) {
            try {
                VH viewHolderUser = viewHolderClass.cast(holder);
                ItemFirebasePaginatorAdapter ifpa = (ItemFirebasePaginatorAdapter) items.get(position);
                T model = ifpa.getDataSnapshot().getValue(modelClass);

                populateViewHolder(viewHolderUser, model, position);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

        }
    }

}