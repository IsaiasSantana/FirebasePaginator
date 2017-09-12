package isaias.santana.firebasepaginatorrecycleradapter.adapter.delegates;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * @param <T> o tipo da fonte de dados para esse delegate.
 *            thanks HANNES DORFMANN for the idea.
 * @author Isaías Santana on 07/07/17.
 *         email: isds.santana@gmail.com
 */

public interface AdapterDelegate<T> {

    /**
     * Para indicar se este delegate é responsável pelo elemento do tipo de passado.
     *
     * @param items    a fonte de dados para o adapatador, uma lista ou outro objeto por exemplo.
     * @param position a posição do elemento na fonte de dados.
     * @return true se este delegate é reponsável por este tipo de dado. Falso qualquer outro caso.
     */
    boolean isForViewType(@NonNull T items, int position);

    /**
     * Cria o view holder para o tipo de dado passad
     *
     * @param parent o viewGroup pai para este tipo de dado.
     * @return a instância do view holder.
     */
    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * Para adicionar os elementos da fonte de dados ao view holder.
     *
     * @param items
     * @param position
     */
    void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @NonNull T items, int position);
}
