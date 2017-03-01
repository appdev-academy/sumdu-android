package igor.contentparce;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;


public class DialogOnLongClick extends DialogFragment implements OnClickListener {

    String TAG = "DialogOnLongClick";

    MainActivity mainActivity = new MainActivity();
    Context context;


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Історія").setPositiveButton(R.string.delete_element, this)
                .setNegativeButton(R.string.clean_history, this)
                .setMessage(R.string.body_text);
        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        int i = 0;
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                i = R.string.delete_element;

                   mainActivity.deleteElement();

                break;
            case Dialog.BUTTON_NEGATIVE:
                i = R.string.clean_history;

                mainActivity.cleanHistory();

                break;

        }
        if (i > 0)
            Log.d(TAG, "DialogOnLongClick: " + getResources().getString(i));
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(TAG, "DialogOnLongClick: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG, "DialogOnLongClick: onCancel");
    }
}