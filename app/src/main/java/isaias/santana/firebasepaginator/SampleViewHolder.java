package isaias.santana.firebasepaginator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * @author Isaías Santana on 01/07/17.
 *         email: isds.santana@gmail.com
 */

public final class SampleViewHolder extends RecyclerView.ViewHolder
{

    //Your fields of view.
    private TextView myFields;

    public SampleViewHolder(View itemView)
    {
        super(itemView);

        //Set your fields.

        // myFields = (TextView)  itemView.findViewById(R.id.myFields);
    }

    public TextView getMyFields() { return myFields; }
}
