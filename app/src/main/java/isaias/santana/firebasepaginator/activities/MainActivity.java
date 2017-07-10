package isaias.santana.firebasepaginator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import isaias.santana.firebasepaginator.utils.ProgressDialogUtil;
import isaias.santana.firebasepaginator.R;
import isaias.santana.firebasepaginator.mvp.presenter.MainActivityPresenter;
import isaias.santana.firebasepaginator.mvp.view.MainActivityView;

public class MainActivity extends MvpAppCompatActivity implements MainActivityView
{
    private final ProgressDialogUtil progressDialogUtil = new ProgressDialogUtil();

    @InjectPresenter
    MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnAddContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final String contactName = ((EditText) findViewById(R.id.contactName)).getText().toString();
                final String contactPhone = ((EditText) findViewById(R.id.contactPhone)).getText().toString();

                presenter.addContact(contactName,contactPhone);
            }
        });

        findViewById(R.id.btnGotoActivityList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this,ContactListActivity.class));
            }
        });

    }

    public void showToast(int message)
    {
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressDialog(int message) {
        progressDialogUtil.showProgressDialog(this,message);
    }

    @Override
    public void hideProgressDialog()
    {
        progressDialogUtil.hideProgressDialog();
    }

    @Override
    public void clearFields()
    {
        ((EditText) findViewById(R.id.contactName)).setText("");
        ((EditText) findViewById(R.id.contactPhone)).setText("");
    }
}
