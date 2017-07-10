package isaias.santana.firebasepaginatorrecycleradapter.adapter;

import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * @author Isaías Santana on 28/06/17.
 *         email: isds.santana@gmail.com
 */

public abstract class FirebasePaginatorRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter
{
    private static final String TAG = FirebasePaginatorRecyclerAdapter.class.getSimpleName();

    /* Layout que será inflado */
    protected int layout;

    /* O tipo do objeto que deve ser convertido quando recuperado do banco de dados */
    private Class<T> modelClass;

    /* O ViewHolder que será utilizado para exibir os objetos */
    private Class<VH> viewHolderClass;

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

    /* número total de dados que será buscado no banco por vez */
    private final int totalDataPerPage;

    /* Limite padrão de dados recuperado do banco */
    private static final int DEFAULT_LIMIT_PER_PAGE = 21;

    private OnLoadDone onLoadDone;

    private FirebasePaginatorRecyclerAdapter(Class<T> modelClass,
                                             @LayoutRes int layout,
                                             Class<VH> viewHolderClass,
                                             int totalDataPerPage)
    {
        this.modelClass = modelClass;
        this.layout = layout;
        this.viewHolderClass = viewHolderClass;

        if(totalDataPerPage <= 0)
            this.totalDataPerPage = DEFAULT_LIMIT_PER_PAGE;
        else
            this.totalDataPerPage  = totalDataPerPage + 1;

        isLoading = false;
        nextKey = "";
        previewsKey = "";
        manager = new AdapterDelegatesManager<>();
        manager.addDelegate(new ProgressBarDelegate(), ViewType.VIEW_PROGRESS_BAR_ITEM)
                .addDelegate(new UserAdapterDelegate(),ViewType.VIEW_DATA_DATA_SNAPSHOT_ITEM);
    }

    /**
     *
     * @param modelClass A classe de modelo que será utilizada para recuperar os dados.
     * @param layout O recurso de layout utilizado para exibir os itens.
     * @param viewHolderClass O ViewHolder para exibir os dados.
     * @param query A referência para onde os dados estão.
     * @param  totalDataPerPage o total de dados que será recuperado
     */
    public FirebasePaginatorRecyclerAdapter(final Class<T> modelClass,
                                            @LayoutRes int layout,
                                            Class<VH> viewHolderClass,
                                            Query query,
                                            final int totalDataPerPage)
    {
        this(modelClass,layout,viewHolderClass,totalDataPerPage);
        this.queryListener = query;

        Log.d(TAG,"totalDataPerPage "+this.totalDataPerPage);

        //Get the first data.
        query.limitToLast(this.totalDataPerPage)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(onLoadDone != null)
                            onLoadDone.hadSuccessLoad();

                        if(dataSnapshot == null) return;

                        Deque<DataSnapshot> snapshots;
                        snapshots = new ArrayDeque<>(FirebasePaginatorRecyclerAdapter.this.totalDataPerPage);

                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        {
                            snapshots.addFirst(snapshot);
                        }

                        if(snapshots.isEmpty())
                        {
                            return;
                        }

                        // next key to load more data.
                        nextKey = snapshots.getLast().getKey();

                        Log.d(TAG,"initial load. nextKey: "+nextKey);


                        //ASCENDIG order
                        while (!snapshots.isEmpty())
                        {
                            DataSnapshot snapshot = snapshots.removeFirst();
                            FirebasePaginatorRecyclerAdapter.this
                                    .viewTypes
                                    .add(new ItemFirebasePaginatorAdapter(snapshot));

                            notifyItemInserted(FirebasePaginatorRecyclerAdapter.this.viewTypes.size() - 1);

                            Log.d(TAG, snapshot.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if(onLoadDone != null)
                        {
                            onLoadDone.error();
                        }
                    }
                });

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return manager.onCreateViewHolder(parent,viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        manager.onBindViewHolder(viewTypes,position,viewHolder);
    }

    @Override
    public int getItemViewType(int position)
    {
        return manager.getItemViewType(viewTypes,position);
    }


    @Override
    public int getItemCount() {
        return viewTypes.size();
    }

    @Override
    public long getItemId(int position)
    {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return ((ItemFirebasePaginatorAdapter)viewTypes.get(position))
                .getDataSnapshot()
                .getKey()
                .hashCode();
    }

    public  void loadMore()
    {

        if(!isLoading && !previewsKey.equals(nextKey))
        {
            Log.d(TAG,"loadMore() tem mais dados");
            isLoading = true;
            previewsKey = nextKey;

            new Handler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    //Adiciona o Progress bar
                    viewTypes.add(new ProgressBarItem());
                    notifyItemInserted(viewTypes.size()-1);
                }
            });

            queryListener
                    .orderByKey()
                    .endAt(nextKey)
                    .limitToLast(totalDataPerPage)
                    .addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            //Remove o progress bar antes de inserir.
                            final int lastPosition = viewTypes.size()-1;
                            viewTypes.remove(lastPosition);
                            notifyItemRemoved(lastPosition);

                            if(dataSnapshot == null) return;

                            final Deque<DataSnapshot> snapshots;
                            snapshots = new ArrayDeque<>(
                                    FirebasePaginatorRecyclerAdapter.this.totalDataPerPage);

                            for(DataSnapshot snapshot : dataSnapshot.getChildren())
                            {
                                if(nextKey.equals(snapshot.getKey()))
                                    continue;

                                snapshots.addFirst(snapshot);
                            }

                            if(snapshots.isEmpty())
                            {
                                Log.d(TAG,"Deque vazio. Fim dos dados.");
                                isLoading = false;
                                return;
                            }

                            // next key to load more data.
                            nextKey = snapshots.getLast().getKey();
                            Log.d(TAG,"loadMore() nextKey: "+nextKey);

                            //ASCENDIG order
                            while (!snapshots.isEmpty())
                            {
                                DataSnapshot snapshot = snapshots.removeFirst();
                                FirebasePaginatorRecyclerAdapter.this
                                        .viewTypes
                                        .add(new ItemFirebasePaginatorAdapter(snapshot));

                                notifyItemInserted(
                                        FirebasePaginatorRecyclerAdapter.this.viewTypes.size() - 1);

                                Log.d(TAG, snapshot.getKey());
                            }

                            isLoading = false;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}

                    }); //[End-Listener]

        }//[End-IF]
    }


    public boolean isLoading(){ return isLoading; }

    /**
     * Implemente este método para popular os dados que serão exibidos. Toda vez que um evento
     * ocorrer em uma dada localização no Firebase esse método será chamado para cada item que for
     * modificado. O comportamento padrão é apenas chamar este método toda vez que um dado for
     * alterado, removido ou quando for buscar mais páginas de dados.
     * @param viewHolder A view para popular, essa view corresponde ao layout passado na realização da instância do objeto.
     * @param model      O objeto do model que será usado para popular viewHolder.
     * @param position   a posição do elemento na view que está sendo populado.
     */
    abstract protected void populateViewHolder(VH viewHolder, T model, int position);


    public void setOnLoadDone(OnLoadDone onLoadDone)
    {
        if(onLoadDone == null)
            throw new NullPointerException("OnLoadDone não pode ser nulo.");

        this.onLoadDone = onLoadDone;
    }

    public interface OnLoadDone
    {
        void hadSuccessLoad();
        void error();
    }

    /**
     * Classe que é responsável inflar a view do usuário.
     */
    private class UserAdapterDelegate implements AdapterDelegate<List<ViewType>>
    {

        @Override
        public boolean isForViewType(@NonNull List<ViewType> items, int position)
        {
            return items.get(position).getViewType() == ViewType.VIEW_DATA_DATA_SNAPSHOT_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            try {
                Constructor<VH> constructor = viewHolderClass.getConstructor(View.class);
                return constructor.newInstance(view);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                     @NonNull List<ViewType> items,
                                     int position)
        {
            try
            {
                VH viewHolderUser = viewHolderClass.cast(holder);
                ItemFirebasePaginatorAdapter ifpa = (ItemFirebasePaginatorAdapter) items.get(position);
                T model = ifpa.getDataSnapshot().getValue(modelClass);

                populateViewHolder(viewHolderUser,model,position);
            }
            catch (ClassCastException e){
                e.printStackTrace();
            }

        }
    }

}