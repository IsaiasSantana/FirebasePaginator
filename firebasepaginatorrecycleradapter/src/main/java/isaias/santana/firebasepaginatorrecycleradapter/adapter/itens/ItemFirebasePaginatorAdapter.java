package isaias.santana.firebasepaginatorrecycleradapter.adapter.itens;

import com.google.firebase.database.DataSnapshot;

import isaias.santana.firebasepaginatorrecycleradapter.adapter.interfaces.ViewType;


/**
 * @author Isaías Santana on 07/07/17.
 *         email: isds.santana@gmail.com
 */

public final class ItemFirebasePaginatorAdapter implements ViewType
{
    private final DataSnapshot dataSnapshot;

    public ItemFirebasePaginatorAdapter(DataSnapshot dataSnapshot)
    {
        this.dataSnapshot = dataSnapshot;
    }

    public DataSnapshot getDataSnapshot()
    {
        return dataSnapshot;
    }

    @Override
    public int getViewType()
    {
        return ViewType.VIEW_DATA_DATA_SNAPSHOT_ITEM;
    }
}
