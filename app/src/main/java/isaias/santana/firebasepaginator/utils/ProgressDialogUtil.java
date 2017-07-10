package isaias.santana.firebasepaginator.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * @author Isaías Santana on 06/06/17.
 *         email: isds.santana@gmail.com
 */

public final class ProgressDialogUtil
{
    private ProgressDialog progressDialog;

    public final void showProgressDialog(Context context, int mensagem)
    {
        if(progressDialog == null)
        {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(mensagem));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public final void hideProgressDialog()
    {
        if(progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
    }
}
