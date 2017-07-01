package isaias.santana.firebasepaginator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

public final class MainActivity extends AppCompatActivity
{

    private SampleAdapter sampleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        //.......

          sampleAdapter = new SampleAdapter(SampleModel.class,
                R.layout.main_activity_layout,
                SampleViewHolder.class,
                FirebaseDatabase.getInstance().getReference("Your_data_reference"),
                10 /*totalDataPerPage*/,
                this);

        final  LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(sampleAdapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.addOnScrollListener(initScrollListener());

    }


    /**
     * Adiciona o listener de scroll para o recycler view.
     */
    private RecyclerView.OnScrollListener initScrollListener()
    {
        return  new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisible = llm.findLastCompletelyVisibleItemPosition() + 1;

                if(recyclerView.getAdapter().getItemCount() == lastVisible && !sampleAdapter.isLoading())
                {
                    //fetch more data.
                    sampleAdapter.loadMore();
                }
            }
        };
    }

}
