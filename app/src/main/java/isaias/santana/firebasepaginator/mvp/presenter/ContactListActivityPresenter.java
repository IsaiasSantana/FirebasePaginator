package isaias.santana.firebasepaginator.mvp.presenter;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import isaias.santana.firebasepaginator.utils.Constants;
import isaias.santana.firebasepaginator.R;
import isaias.santana.firebasepaginator.adapters.ContactsListAdapter;
import isaias.santana.firebasepaginator.adapters.viewHolders.ContactListViewHolder;
import isaias.santana.firebasepaginator.models.Contact;
import isaias.santana.firebasepaginator.mvp.view.ContactListActivityView;
import isaias.santana.firebasepaginatorrecycleradapter.adapter.FirebasePaginatorRecyclerAdapter;

/**
 * @author Isaías Santana on 10/07/17.
 *         email: isds.santana@gmail.com
 */

@InjectViewState
public final class ContactListActivityPresenter extends MvpPresenter<ContactListActivityView>
                                                implements ContactListPresenterInterface.ToModel,
                                                           ContactListPresenterInterface.ToView,
                                                         FirebasePaginatorRecyclerAdapter.OnLoadDone
{
    private  ContactsListAdapter contactsListAdapter;

    public ContactListActivityPresenter()
    {

    }

    public ContactsListAdapter getAdapter(Context context)
    {
        final DatabaseReference reference = FirebaseDatabase.getInstance()
                                                            .getReference()
                                                            .child(Constants.NODE_CONTACTS);

        contactsListAdapter = new ContactsListAdapter(Contact.class,
                R.layout.row_contact,
                ContactListViewHolder.class,
                reference,
                10/*totalDataPerPage*/,
                context);
        contactsListAdapter.setOnLoadDone(this);

       return contactsListAdapter;
    }

    @Override
    public void hadSuccessLoad() {
         getViewState().hideProgressBar();
    }

    @Override
    public void error()
    {
        getViewState().hideProgressBar();
    }
}
