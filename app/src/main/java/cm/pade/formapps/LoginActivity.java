package cm.pade.formapps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cm.pade.formapps.parser.HttpRequest;
import cm.pade.formapps.parser.OptionPanel;

/**
 * Created by Sumbang on 20/09/2016.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText username,pwd;
    private Button valider;
    private ProgressDialog pd;

    //---MessageBox component
    private OptionPanel msgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       final Request query = new Request(LoginActivity.this);

        query.open();

        ArrayList<User> luser = query.getAllUser();

        query.close();

        if(luser != null){

            User u = luser.get(0);

            SessionData.getInstance().setStatus(u.getStatus()); SessionData.getInstance().setIduser(u.getIduser());

            SessionData.getInstance().setUsername(u.getUsername()); SessionData.getInstance().setRole(u.getRole());

            Intent i = new Intent(LoginActivity.this,HomeActivity.class);

            startActivity(i);

        } else {

            setContentView(R.layout.activity_login);

            username = findViewById(R.id.editText1);
            pwd = findViewById(R.id.editText2);

            valider = findViewById(R.id.button);

            valider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (username.getText().toString().isEmpty()) {

                        Toast.makeText(LoginActivity.this, "Username Obligatoire", Toast.LENGTH_LONG).show();
                    } else if (pwd.getText().toString().isEmpty()) {

                        Toast.makeText(LoginActivity.this, "Mot de passe Obligatoire", Toast.LENGTH_LONG).show();
                    } else {

                        if (isConnected())
                            new HttpAsyncTask().execute(username.getText().toString().trim(), pwd.getText().toString().trim());

                        else
                            Toast.makeText(LoginActivity.this, "Veuillez vous connecter a internet.", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        msgBox = new OptionPanel(this);
    }
    //----------------------------------------------------------------

    @Override
    public void onBackPressed(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Fermer l'application ?");
        alertDialogBuilder
                .setMessage("Cliquez Oui pour fermer !")
                .setCancelable(false)
                .setPositiveButton("Oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static String GET(String username,String pwd) {

        String lien = MainActivity.endpoint+"/rws-user/login?username="+username+"&password="+pwd;
        String SetServerString = "";
        /*HttpURLConnection con = null;
        InputStream is = null;*/

        try {
            /*con = (HttpURLConnection) ( new URL(lien)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Lecture du resultat sous format Json
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while (  (line = br.readLine()) != null )
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();
            return buffer.toString();*/
            HttpRequest req = new HttpRequest(lien);
            SetServerString = req.prepare(HttpRequest.Method.GET).sendAndReadString();
            return SetServerString;

        }
        catch(Exception ex)
        {
            Log.e("LOGIN EXCEPTION ", ex.getMessage());
            return null;
        }
        /*catch(Throwable t) {
            t.printStackTrace();
        }
        finally {
            try { is.close(); } catch(Throwable t) {}
            try { con.disconnect(); } catch(Throwable t) {}
        }*/

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            return GET(args[0],args[1]);

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if (pd!=null) { pd.dismiss(); }

            //Log.e("RETOUR",result);

            if(result != null){

                try {

                    JSONObject retour = new JSONObject(result);

                    String pass = retour.getString("password");

                    String id = retour.getString("id"); int iduser = Integer.parseInt(id);

                    String username = retour.getString("username");

                    if(pass.equals("LOGIN_OK")) {

                        String role = retour.getString("user_role");

                        Log.e("Error","role : "+role);

                            // recherche si l'utilisateur est deja dans la bd

                            final Request query = new Request(getApplicationContext());

                            query.open();

                            User user = query.getUser(iduser);

                            if (user == null) {

                                User u = new User(iduser, username, 1); u.setRole(role);

                                query.insertUser(u);

                                SessionData.getInstance().setStatus(1);
                                SessionData.getInstance().setIduser(iduser);
                                SessionData.getInstance().setRole(role);
                                SessionData.getInstance().setUsername(username);

                            } else {

                                SessionData.getInstance().setStatus(1);
                                SessionData.getInstance().setIduser(iduser);
                                SessionData.getInstance().setRole(role);
                                SessionData.getInstance().setUsername(username);
                            }

                            query.close();

                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);

                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            getApplicationContext().startActivity(i);

                    }

                    else  {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Utilisateur inconnu", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                } catch (JSONException e) {
                    msgBox.setMessage("Compte ou mot de passe incorrect");
                    msgBox.openSimplePane();
                    Log.e("Error","Le resultat PostExcecute est : "+e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }

           // Toast.makeText(getBaseContext(), "Data Sent : "+result, Toast.LENGTH_LONG).show();

            //Log.e("Error","Le resultat PostExcecute est : "+result);
        }

        @Override
        protected void onPreExecute() {

            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(LoginActivity.this);

            //pd.setTitle("Connexion");

            pd.setMessage("Connexion en cours ...");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
