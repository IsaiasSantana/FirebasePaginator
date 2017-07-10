package isaias.santana.firebasepaginator.mvp.presenter;

/**
 * @author Isaías Santana on 10/07/17.
 *         email: isds.santana@gmail.com
 */

public interface PresenterInterface
{
    interface ToModel
    {
        void showMessage(int message);
        void showProgressDialog();
        void hideProgressDialog();
    }

    interface ToView
    {
        void addContact(String contactName, String phoneNumber);
    }
}
