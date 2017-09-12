package isaias.santana.firebasepaginatorrecycleradapter.adapter.interfaces;

/**
 * Use this interface to create a generic type for the AdapaterDelegate. Util for add more views in recyclerView.
 * See ProgressBarDelegate, ItemFirebasePaginatorAdapter, ProgressBarItem and FirebasePaginatorRecyclerAdapter
 * constructor to see usage.
 *
 * @author Isaías Santana on 07/07/17.
 *         email: isds.santana@gmail.com
 */

public interface ViewType {
    int VIEW_DATA_DATA_SNAPSHOT_ITEM = 0;
    int VIEW_PROGRESS_BAR_ITEM = 1;

    int getViewType();
}
