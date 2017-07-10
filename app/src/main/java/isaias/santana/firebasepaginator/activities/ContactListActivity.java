package isaias.santana.firebasepaginator.activities;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import isaias.santana.firebasepaginator.R;
import isaias.santana.firebasepaginator.adapters.ContactsListAdapter;
import isaias.santana.firebasepaginator.mvp.presenter.ContactListActivityPresenter;
import isaias.santana.firebasepaginator.mvp.view.ContactListActivityView;

public class ContactListActivity extends MvpAppCompatActivity implements ContactListActivityView
{
    private ContactsListAdapter contactsListAdapter;

    @InjectPresenter
    ContactListActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        findViewById(R.id.my_progress_bar).setVisibility(View.VISIBLE);

        contactsListAdapter = presenter.getAdapter(this);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        final LinearLayoutManager llm =
                                  new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);



        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(contactsListAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                final int totalItemCount =  llm.getItemCount();
                final int lastVisibleItem = llm.findLastCompletelyVisibleItemPosition();


                if(!contactsListAdapter.isLoading() && totalItemCount <= (lastVisibleItem + 2))
                {
                    contactsListAdapter.loadMore();
                }
            }
        });
    }


    @Override
    public void hideProgressBar()
    {
        findViewById(R.id.my_progress_bar).setVisibility(View.GONE);
    }

}
