package isaias.santana.firebasepaginator.mvp.model;

import isaias.santana.firebasepaginator.models.Contact;
import isaias.santana.firebasepaginator.mvp.presenter.ContactListPresenterInterface;

/**
 * @author Isaías Santana on 10/07/17.
 *         email: isds.santana@gmail.com
 */

public final class ContactListActivityModel {
    private final ContactListPresenterInterface.ToModel presenter;

    public ContactListActivityModel(ContactListPresenterInterface.ToModel presenter) {
        this.presenter = presenter;
    }


    public void deleteContact(Contact contact) {
        //TODO- delete the contact from database.
    }
}
