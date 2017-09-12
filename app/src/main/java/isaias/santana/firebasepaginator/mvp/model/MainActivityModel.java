package isaias.santana.firebasepaginator.mvp.model;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import isaias.santana.firebasepaginator.R;
import isaias.santana.firebasepaginator.models.Contact;
import isaias.santana.firebasepaginator.mvp.presenter.PresenterInterface;
import isaias.santana.firebasepaginator.utils.Constants;

/**
 * @author Isaías Santana on 10/07/17.
 *         email: isds.santana@gmail.com
 */

public final class MainActivityModel {
    private final PresenterInterface.ToModel presenter;
    private final DatabaseReference mRef;

    public MainActivityModel(PresenterInterface.ToModel presenter) {
        this.presenter = presenter;
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    public void createContact(Contact contact) {
        presenter.showProgressDialog();

        final String newContactKey = mRef.child(Constants.NODE_CONTACTS).push().getKey();
        final HashMap<String, Object> updates = new HashMap<>();
        updates.put(newContactKey, contact.toMap());

        mRef.child(Constants.NODE_CONTACTS)
                .updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        presenter.hideProgressDialog();

                        if (task.isSuccessful())
                            presenter.showMessage(R.string.success);
                        else
                            presenter.showMessage(R.string.error);
                    }
                });
    }
}
