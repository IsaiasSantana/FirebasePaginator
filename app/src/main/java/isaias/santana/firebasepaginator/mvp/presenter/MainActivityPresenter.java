package isaias.santana.firebasepaginator.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import isaias.santana.firebasepaginator.R;
import isaias.santana.firebasepaginator.models.Contact;
import isaias.santana.firebasepaginator.mvp.model.MainActivityModel;
import isaias.santana.firebasepaginator.mvp.view.MainActivityView;

/**
 * @author Isaías Santana on 10/07/17.
 *         email: isds.santana@gmail.com
 */

@InjectViewState
public final class MainActivityPresenter extends MvpPresenter<MainActivityView>
        implements PresenterInterface.ToModel,
        PresenterInterface.ToView {
    private final MainActivityModel model;

    public MainActivityPresenter() {
        model = new MainActivityModel(this);
    }

    @Override
    public void showMessage(int message) {
        getViewState().showToast(message);
        getViewState().clearFields();
    }

    @Override
    public void showProgressDialog() {
        getViewState().showProgressDialog(R.string.creating_contact);
    }

    @Override
    public void hideProgressDialog() {
        getViewState().hideProgressDialog();
    }

    @Override
    public void addContact(String contactName, String phoneNumber) {
        final Contact contact = new Contact(contactName, phoneNumber);

        model.createContact(contact);
    }
}
