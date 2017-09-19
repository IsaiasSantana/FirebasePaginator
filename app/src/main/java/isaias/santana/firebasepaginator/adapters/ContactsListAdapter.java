package isaias.santana.firebasepaginator.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.google.firebase.database.Query;

import isaias.santana.firebasepaginator.adapters.viewHolders.ContactListViewHolder;
import isaias.santana.firebasepaginator.models.Contact;
import isaias.santana.firebasepaginatorrecycleradapter.adapter.FirebasePaginatorRecyclerAdapter;

/**
 * @author Isaías Santana on 10/07/17.
 *         email: isds.santana@gmail.com
 */

public final class ContactsListAdapter extends FirebasePaginatorRecyclerAdapter<Contact, ContactListViewHolder> {
    private final Context context;

    /**
     * @param modelClass       A classe de modelo que será utilizada para recuperar os dados.
     * @param layout           O recurso de layout utilizado para exibir os itens.
     * @param viewHolderClass  O ViewHolder para exibir os dados.
     * @param query            A referência para onde os dados estão.
     * @param totalDataPerPage o total de dados que será recuperado
     */
    public ContactsListAdapter(Class<Contact> modelClass,
                               @LayoutRes int layout,
                               Class<ContactListViewHolder> viewHolderClass,
                               Query query,
                               int totalDataPerPage,
                               Context context) {
        super(modelClass, layout, viewHolderClass, query, totalDataPerPage,true);
        this.context = context;
        super.setToAddNewItemAddedToFirebase(true);
    }

    @Override
    protected void populateViewHolder(ContactListViewHolder viewHolder,
                                      Contact model,
                                      int position) {
        final int color = getRandomMaterialColor("400");

        viewHolder.getLabel().setText(Character.toString(model.getContactName().toUpperCase().charAt(0)));
        DrawableCompat.setTint(DrawableCompat.wrap(viewHolder.getLabel().getBackground()), color);
        viewHolder.getContactName().setText(model.getContactName());
        viewHolder.getContactPhone().setText(model.getPhoneNumber());
    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = context.getResources()
                .getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }
}
