package isaias.santana.firebasepaginator;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;

import com.google.firebase.database.Query;

import isaias.santana.firebasepaginatorrecycleradapter.adapter.FirebasePaginatorRecyclerAdapter;


/**
 * @author Isaías Santana on 28/06/17.
 *         email: isds.santana@gmail.com
 */

public final class SampleAdapter extends FirebasePaginatorRecyclerAdapter<SampleModel,SampleViewHolder>
{

    private static String TAG = SampleAdapter.class.getSimpleName();
    private Context context;

    /**
     * @param modelClass       A classe de modelo que será utilizada para recuperar os dados.
     * @param layout           O recurso de layout utilizado para exibir os itens.
     * @param viewHolderClass  O ViewHolder para exibir os dados.
     * @param query            A referência para onde os dados estão.
     * @param totalDataPerPage o total de dados que será recuperado
     */
    public SampleAdapter(Class<SampleModel> modelClass, // your model class.
                         @LayoutRes int layout, // your resource layout to this view holder.
                         Class<SampleViewHolder> viewHolderClass, // your view holder.
                         Query query, // reference to your data on firebase
                         int totalDataPerPage, // number of data per page.
                         Context context) // the activity
    {
        super(modelClass, layout, viewHolderClass, query, totalDataPerPage);
        this.context = context;
    }

    @Override
    public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //with this, paginate the data.
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected void populateViewHolder(final SampleViewHolder viewHolder,
                                      SampleModel model,
                                      int position)
    {
        //Set data of Model to your view holder
        //The base adapter has the list of data.
        viewHolder.getMyFields().setText(model.getUserName());
    }
}
