package cm.pade.formapps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Sumbang on 30/09/2016.
 */

@SuppressLint("ValidFragment")
public class ConfirmDialog extends DialogFragment {

    public String mot;

    @SuppressLint("ValidFragment")
    public ConfirmDialog(String mot1){
        mot = mot1;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
       // Use the Builder class for convenient dialog construction
       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       builder.setMessage(mot)
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
              // FIRE ZE MISSILES!

               Intent i3 = new Intent(getActivity(),SynchroActivity.class);

               startActivity(i3);

              }
           });
       // Create the AlertDialog object and return it
       return builder.create();
        }

}
