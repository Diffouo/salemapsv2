package cm.pade.formapps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Sumbang on 19/10/2016.
 */

@SuppressLint("ValidFragment")
public class ConfirmDialog3 extends DialogFragment {

    public String mot;

    @SuppressLint("ValidFragment")
    public ConfirmDialog3(String mot1){
        mot = mot1;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mot)
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent i3 = new Intent(getActivity(),MainActivity.class);

                        startActivity(i3);
                    }
                })
                .setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!

                        SessionData.getInstance().setLongdata(null);

                        SessionData.getInstance().setLatdata(null);

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
