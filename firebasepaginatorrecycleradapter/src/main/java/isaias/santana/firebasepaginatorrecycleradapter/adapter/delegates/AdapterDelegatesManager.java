package isaias.santana.firebasepaginatorrecycleradapter.adapter.delegates;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;


/**
 * @author Isaías Santana on 07/07/17.
 *         email: isds.santana@gmail.com
 *         <p>
 *         Based in this project: https://github.com/sockeqwe/AdapterDelegates
 *         site hannesdorfmann:  http://hannesdorfmann.com/android/adapter-delegates
 */

public final class AdapterDelegatesManager<T> {
    /**
     * O número máximo de tipos de view que podem ser adicionados.
     */
    private static final int MAX_DELEGATE_VIEW_TYPE = Integer.MAX_VALUE - 1;

    private final SparseArrayCompat<AdapterDelegate<T>> delegates = new SparseArrayCompat<>();
    private final AdapterDelegate<T> fallbackDelegate = null;

    public AdapterDelegatesManager<T> addDelegate(@NonNull AdapterDelegate<T> delegate,
                                                  final int viewType) {

        if (delegates.get(viewType) != null)
            throw new IllegalArgumentException(
                    "Já existe um adapterDelegate registrado com o viewType " + viewType + " passado");


        if (delegates.size() == MAX_DELEGATE_VIEW_TYPE)
            throw new IllegalArgumentException(
                    "Número máximo de viewTypes atingido. Está limitado para Integer.MAX_VALUE");


        delegates.append(viewType, delegate);

        return this;
    }

    /**
     * Removes the adapterDelegate for the given view types.
     */
    public AdapterDelegatesManager<T> removeDelegate(int viewType) {
        delegates.remove(viewType);
        return this;
    }


    public int getItemViewType(@NonNull T items, final int position) {

        for (int i = 0; i < delegates.size(); i++) {
            final AdapterDelegate<T> delegate = delegates.valueAt(i);

            if (delegate.isForViewType(items, position)) {
                return delegates.keyAt(i);
            }
        }

        throw new NullPointerException(
                "Nenhum AdapterDelegate encontrado para a posição " + position);
    }

    /* This method must be called in {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
   * @param parent the parent
   * @param viewType the view type
   * @return The new created ViewHolder
   * @throws NullPointerException if no AdapterDelegate has been registered for ViewHolders
   * viewType
   */
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterDelegate<T> delegate = delegates.get(viewType, fallbackDelegate);

        if (delegate == null)
            throw new NullPointerException("No AdapterDelegate added for ViewType " + viewType);

        RecyclerView.ViewHolder vh = delegate.onCreateViewHolder(parent, viewType);

        if (vh == null) {
            throw new NullPointerException("ViewHolder returned from AdapterDelegate "
                    + delegate
                    + " for ViewType ="
                    + viewType
                    + " is null!");
        }

        return vh;
    }


    /**
     * @param items      Adapter's data source
     * @param position   the position in data source
     * @param viewHolder the ViewHolder to bind
     * @throws NullPointerException if no AdapterDelegate has been registered for ViewHolders
     *                              viewType
     */
    public void onBindViewHolder(@NonNull T items, int position,
                                 @NonNull RecyclerView.ViewHolder viewHolder) {

        AdapterDelegate<T> delegate = delegates.get(viewHolder.getItemViewType(), fallbackDelegate);

        if (delegate == null) {
            throw new NullPointerException("No delegate found for item at position = "
                    + position
                    + " for viewType = "
                    + viewHolder.getItemViewType());
        }

        delegate.onBindViewHolder(viewHolder, items, position);
    }


}
