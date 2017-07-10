package isaias.santana.firebasepaginator.mvp.view;

import com.arellomobile.mvp.MvpView;

/**
 * @author Isaías Santana on 10/07/17.
 *         email: isds.santana@gmail.com
 */

public interface MainActivityView extends MvpView
{
    void showToast(int message);
    void showProgressDialog(int message);
    void hideProgressDialog();
    void clearFields();
}
