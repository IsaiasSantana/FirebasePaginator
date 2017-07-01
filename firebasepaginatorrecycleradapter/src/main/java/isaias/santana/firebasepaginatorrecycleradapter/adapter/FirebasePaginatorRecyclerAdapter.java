package isaias.santana.firebasepaginatorrecycleradapter.adapter;

import android.support.annotation.LayoutRes;
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

/**
 * Um adpatador genérico para paginar os dados em um nó no Firebase database.
 * @author Isaías Santana on 28/06/17.
 *         email: isds.santana@gmail.com
 */

public abstract class FirebasePaginatorRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
                                                             extends RecyclerView.Adapter<VH>
{
    private static final String TAG = FirebasePaginatorRecyclerAdapter.class.getSimpleName();

    /* Layout que será inflado */
    protected int layout;

    /* O tipo do objeto que deve ser convertido quando recuperado do banco de dados */
    private Class<T> modelClass;

    /* O ViewHolder que será utilizado para exibir os objetos */
    private Class<VH> viewHolderClass;

    /* Os Snapshots dos dados recuperados durante a busca */
    protected ArrayList<DataSnapshot> dataSnapshots;

    /* Guarda a chave do próximo elemento que deve ser buscado do banco */
    private String nextKey;

    /* Guarda a última armazenada por nextKey */
    private String previewsKey;

    /* A consulta utilizada para buscar os dados */
    private Query query;

    /* Esta Query escuta os eventos sobre um nó utlizando o listener OnChildEventListener*/
    private Query queryListener;

    /*Flag para indicar se está buscando dados ou não */
    private boolean isLoading;

    /* número total de dados que será buscado no banco por vez */
    private final int totalDataPerPage;

    /* Limite padrão de dados recuperado do banco */
    private static final int DEFAULT_LIMIT_PER_PAGE = 21;

    /* Listener para escutar os eventos que ocorrem no nó */
    private ChildEventListener mListener;


    private FirebasePaginatorRecyclerAdapter(Class<T> modelClass,
                                             @LayoutRes int layout,
                                             Class<VH> viewHolderClass,
                                             ArrayList<DataSnapshot> dataSnapshots,
                                             int totalDataPerPage)
    {
        this.modelClass = modelClass;
        this.layout = layout;
        this.viewHolderClass = viewHolderClass;
        this.dataSnapshots = dataSnapshots;

        if(totalDataPerPage <= 0)
            this.totalDataPerPage = DEFAULT_LIMIT_PER_PAGE;
        else
            this.totalDataPerPage  = totalDataPerPage + 1;

        isLoading = false;
        nextKey = "";
        previewsKey = "";

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
        this(modelClass,layout,viewHolderClass, new ArrayList<DataSnapshot>(),totalDataPerPage);
        this.queryListener = query;
        this.query = query.limitToLast(1);
        initListener();
        this.query.addChildEventListener(mListener);

        //Get the first data.
        query.limitToLast(this.totalDataPerPage)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot == null) return;

                Deque<DataSnapshot> snapshots;
                snapshots = new ArrayDeque<>(FirebasePaginatorRecyclerAdapter.this.totalDataPerPage);

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    snapshots.addFirst(snapshot);
                }

                if(snapshots.isEmpty()) return;

                // next key to load more data.
                nextKey = snapshots.getLast().getKey();

                Log.d(TAG,"initial load. nextKey: "+nextKey);


                //ASCENDIG order
                while (!snapshots.isEmpty())
                {
                    DataSnapshot snapshot = snapshots.removeFirst();
                    FirebasePaginatorRecyclerAdapter.this.dataSnapshots.add(snapshot);
                    notifyItemInserted(FirebasePaginatorRecyclerAdapter.this.dataSnapshots.size() - 1);

                    Log.d(TAG, snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
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
    public void onBindViewHolder(VH viewHolder, int position)
    {
        T model = dataSnapshots.get(position).getValue(modelClass);

        populateViewHolder(viewHolder, model, position);
    }

    @Override
    public int getItemViewType(int position) {
        return layout;
    }

    @Override
    public int getItemCount() {
        return dataSnapshots.size();
    }

    @Override
    public long getItemId(int position) {
        // http://stackoverflow.com/questions/5100071/whats-the-purpose-of-item-ids-in-android-listview-adapter
        return dataSnapshots.get(position).getKey().hashCode();
    }

    public void loadMore()
    {
        if(!isLoading && !previewsKey.equals(nextKey))
        {
            isLoading = true;
            previewsKey = nextKey;

            queryListener.orderByKey()
                    .endAt(nextKey)
                    .limitToLast(totalDataPerPage)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot == null) return;

                            Deque<DataSnapshot> snapshots;
                            snapshots = new ArrayDeque<>(FirebasePaginatorRecyclerAdapter.this.totalDataPerPage);

                            for (DataSnapshot snapshot : dataSnapshot.getChildren())
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
                                FirebasePaginatorRecyclerAdapter.this.dataSnapshots.add(snapshot);
                                notifyItemInserted(FirebasePaginatorRecyclerAdapter.this.dataSnapshots.size() - 1);

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

    public void removeListener()
    {
        if(mListener != null)
            queryListener.removeEventListener(mListener);
    }

    /**
     * Implemente este método para popular os dados que serão exibidos. Toda vez que um evento ocorrer em uma dada
     * localização no Firebase esse método será chamado para cada item que for modificado. O comportamento padrão é
     * apenas chamar este método toda vez que um dado for alterado, removido ou quando for buscar mais páginas de dados.
     *
     * @param viewHolder A view para popular, essa view corresponde ao layout passado na realização da instância do objeto.
     * @param model      O objeto do model que será usado para popular viewHolder.
     * @param position   a posição do elemento na view que está sendo populado.
     */
    abstract protected void populateViewHolder(VH viewHolder, T model, int position);


    private int getIndexByKey(DataSnapshot snapshot)
    {
        int index = 0;
        for(DataSnapshot ds : dataSnapshots)
            if(ds.getKey().equals(snapshot.getKey()))
                return index;
            else
                index++;

        return -1;
    }

    private void initListener()
    {
        mListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG,"onChildAdded: "+dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Log.d(TAG,"onChildChanged: "+dataSnapshot.getKey());
                int index = getIndexByKey(dataSnapshot);

                if(index >= 0)
                {
                    Log.d(TAG,"onChildChanged: "+dataSnapshot.getKey());
                    dataSnapshots.set(index,dataSnapshot);
                    notifyItemChanged(index);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                int index = getIndexByKey(dataSnapshot);

                if(index >= 0)
                {
                    Log.d(TAG,"onChildRemoved: "+dataSnapshot.getKey());
                    dataSnapshots.remove(index);
                    notifyItemRemoved(index);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
    }
}
