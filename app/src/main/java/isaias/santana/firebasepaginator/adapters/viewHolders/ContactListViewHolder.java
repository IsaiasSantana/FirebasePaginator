package isaias.santana.firebasepaginator.adapters.viewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import isaias.santana.firebasepaginator.R;

/**
 * @author Isaías Santana on 10/07/17.
 *         email: isds.santana@gmail.com
 */

public final class ContactListViewHolder extends RecyclerView.ViewHolder {
    private TextView label;
    private TextView contactName;
    private TextView contactPhone;

    public ContactListViewHolder(View view) {
        super(view);
        label = (TextView) view.findViewById(R.id.labelFirstLetterRow);
        contactName = (TextView) view.findViewById(R.id.tvContactNameRow);
        contactPhone = (TextView) view.findViewById(R.id.tvContactPhoneRow);
    }

    public TextView getLabel() {
        return label;
    }

    public TextView getContactName() {
        return contactName;
    }

    public TextView getContactPhone() {
        return contactPhone;
    }
}
