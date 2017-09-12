package isaias.santana.firebasepaginatorrecycleradapter.adapter.delegates;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import isaias.santana.firebasepaginatorrecycleradapter.R;
import isaias.santana.firebasepaginatorrecycleradapter.adapter.interfaces.ViewType;


/**
 * @author Isaías Santana on 07/07/17.
 *         email: isds.santana@gmail.com
 */

public class ProgressBarDelegate implements AdapterDelegate<List<ViewType>> {
    @Override
    public boolean isForViewType(@NonNull List<ViewType> items, int position) {
        return items.get(position).getViewType() == ViewType.VIEW_PROGRESS_BAR_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.progress_bar, parent, false);

        return new ProgressBarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 @NonNull List<ViewType> items,
                                 int position) {
    }


    public static final class ProgressBarViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public ProgressBarViewHolder(View view) {
            super(view);

            progressBar = (ProgressBar) view.findViewById(R.id.my_progress_bar);
        }

        public ProgressBar getProgressBar() {
            return progressBar;
        }
    }
}
