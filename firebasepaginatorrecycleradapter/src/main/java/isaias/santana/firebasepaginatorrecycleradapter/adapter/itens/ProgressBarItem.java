package isaias.santana.firebasepaginatorrecycleradapter.adapter.itens;


import isaias.santana.firebasepaginatorrecycleradapter.adapter.interfaces.ViewType;

/**
 * @author Isaías Santana on 07/07/17.
 *         email: isds.santana@gmail.com
 */

public final class ProgressBarItem implements ViewType
{
    @Override
    public int getViewType()
    {
        return ViewType.VIEW_PROGRESS_BAR_ITEM;
    }
}
