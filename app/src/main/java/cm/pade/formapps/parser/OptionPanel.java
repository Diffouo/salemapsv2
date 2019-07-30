package cm.pade.formapps.parser;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import cm.pade.formapps.HomeActivity;

public class OptionPanel {

    private AlertDialog.Builder alertBox;
    private String message;
    private String title;
    private Activity activity;
    private String btnYes;
    private String btnNo;
    private boolean hasTitle;

    public OptionPanel(Activity activity) {
        this.activity = activity;
        this.alertBox = new AlertDialog.Builder(activity);
        this.alertBox.setCancelable(false);

    }

    public void openSimplePane() {
        this.alertBox.setMessage(this.getMessage());
        this.setBtnYes("Ok");
        this.alertBox.setPositiveButton(this.getBtnYes(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Toast.makeText(getActivity(),"You clicked yes button",Toast.LENGTH_LONG);}
            }
        });
        AlertDialog alert = this.alertBox.create();
        alert.show();
    }

    public void openSuccessPane() {
        Log.e("Entree: ", "Bonjour action");
        this.alertBox.setMessage(this.getMessage());
        this.setBtnYes("Ok");
        this.alertBox.setPositiveButton(this.getBtnYes(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity().getApplicationContext(), HomeActivity.class);
                getActivity().getApplicationContext().startActivity(intent);
            }
        });
        AlertDialog alert = this.alertBox.create();
        alert.show();
        Log.e("Sortie: ", "Aurevoir action");
    }

    public AlertDialog.Builder getAlertBox() {
        return alertBox;
    }

    public void setAlertBox(AlertDialog.Builder alertBox) {
        this.alertBox = alertBox;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getBtnYes() {
        return btnYes;
    }

    public void setBtnYes(String btnYes) {
        this.btnYes = btnYes;
    }

    public String getBtnNo() {
        return btnNo;
    }

    public void setBtnNo(String btnNo) {
        this.btnNo = btnNo;
    }

    public boolean isHasTitle() {
        return hasTitle;
    }

    public void setHasTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
    }
}
