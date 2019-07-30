package cm.pade.formapps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cm.pade.formapps.parser.OptionPanel;

/**
 * Created by Sumbang on 22/09/2016.
 */

public class EditActivity extends AppCompatActivity {

    private EditText nom,adresse,email,phone,lon,lat;
    private Spinner choix;
    private Button valider,delete;
    GPSTracker gps;
    private double Latitude = 0, Longitude = 0, PLatitude = 0, PLongitude = 0;
    String responseServer = "";
    Form form;
    private ProgressDialog pd;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private int key;

    //---MessageBox component
    private OptionPanel msgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Request query = new Request(EditActivity.this);

        query.open();

        ArrayList<User> luser = query.getAllUser();

        query.close();

        if(luser != null){

            User u = luser.get(0);

            if(SessionData.getInstance() == null || SessionData.getInstance().getStatus() == 0) {

                SessionData.getInstance().setStatus(u.getStatus());
                SessionData.getInstance().setIduser(u.getIduser());
                SessionData.getInstance().setUsername(u.getUsername());

            }

        }  else {

            Intent i = new Intent(EditActivity.this,LoginActivity.class);

            startActivity(i);

        }

        /*if(!SessionData.getInstance().getRole().equals("merchant")) {

            Toast.makeText(EditActivity.this, "Desole, votre profil n'a pas acces a ce module", Toast.LENGTH_LONG).show();

            Intent i = new Intent(EditActivity.this,HomeActivity.class);

            startActivity(i);
        }*/

        setContentView(R.layout.activity_edit);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(0xFFFFFFFF);


        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Enregistrer un nouveau point de vente");
        setTitle("Enregistrer un nouveau point de vente");


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), SynchroActivity.class);
                i.putExtra("current",1);
                startActivity(i);
            }
        });

        // recuperation de l'intent

        Bundle objetbunble  = this.getIntent().getExtras();

        if (objetbunble != null && objetbunble.containsKey("key")) {

            key = Integer.parseInt(this.getIntent().getStringExtra("key"));

        }else {

            key =0;
        }

        // affectation des variables

        nom = (EditText)findViewById(R.id.editText1);
        adresse = (EditText)findViewById(R.id.editText2);
        email = (EditText)findViewById(R.id.editText4);
        phone = (EditText)findViewById(R.id.editText3);
        choix = (Spinner)findViewById(R.id.spinner);
        valider = (Button)findViewById(R.id.button);
        lon = (EditText)findViewById(R.id.editText6);
        lat = (EditText)findViewById(R.id.editText5);
        delete = (Button)findViewById(R.id.button2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choix.setAdapter(adapter);

        // connexion a la BD

        query.open();

        Point pt = query.getPoint(key);

        query.close();

        if(pt != null){

            nom.setText(pt.getNom()); adresse.setText(pt.getAdresse()); email.setText(pt.getEmail());

            phone.setText(pt.getPhone()); lon.setText(pt.getYlocation()); lat.setText(pt.getXlocation());

            choix.setSelection(pt.getType()+1);


        } else {

            nom.setText(""); adresse.setText(""); email.setText("");

            phone.setText(""); lon.setText(""); lat.setText("");

            choix.setSelection(0);
        }

        msgBox = new OptionPanel(this);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nom.getText().toString(); String adress = adresse.getText().toString();
                String emai = email.getText().toString(); String fone = phone.getText().toString();
                String type = choix.getSelectedItem().toString();
                String la = lat.getText().toString(); String lo = lon.getText().toString();

                String userid = ""+SessionData.getInstance().getIduser();

                int typev = 0;

                if(type.equals("Boutique")) typev = 1; else if(type.equals("Tablier")) typev = 2;

                else if(type.equals("Kiosque")) typev = 3;  else if(type.equals("SuperMarche")) typev = 4;

                else if(type.equals("Station Service")) typev = 5; else if(type.equals("SUPERETTE")) typev = 6;

                else typev = 0;

                String typv = ""+typev;

                if(name.isEmpty() || name.length()>50) {
                    msgBox.setMessage("Le nom ne peut etre vide et doit contenir 50 caracteres maximum");
                    msgBox.openSimplePane();

                }
                else if(adress.isEmpty() || adress.length()>50) {
                    msgBox.setMessage("L'adresse ne peut etre vide et doit contenir 50 caracteres maximum");
                    msgBox.openSimplePane();
                }
                else if (fone.isEmpty() || fone.length()>9) {
                    msgBox.setMessage("Le numero de telephone ne peut etre vide et doit contenir 9 caracteres maximum");
                    msgBox.openSimplePane();
                }
                else if(!fone.startsWith("69") && !fone.startsWith("68") && !fone.startsWith("67") && !fone.startsWith("66") && !fone.startsWith("65") && !fone.startsWith("64") && !fone.startsWith("233")){
                    msgBox.setMessage("Le numero de telephone ne correspond pas au format local");
                    msgBox.openSimplePane();
                }
                else if(typev == 0) {
                    msgBox.setMessage("Veuillez choisir un type de client");
                    msgBox.openSimplePane();

                }

                else if(!isConnected()){

                    Point p = new Point();

                    p.setNom(name); p.setAdresse(adress); p.setPhone(fone); p.setEmail(emai); p.setType(typev);

                    p.setXlocation(la); p.setYlocation(lo);

                    final Request query = new Request(EditActivity.this);

                    query.open();

                    query.updatePoint(p,key);

                    query.close();

                    msgBox.setMessage("Impossible de se connecter au serveur, les donnees ont été enregistrées localement");
                    msgBox.openSimplePane();

                }

                else {

                    new HttpAsyncTask().execute(name, adress, emai, fone, la, lo, typv);

                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                query.open();

                query.RemovePoint(key);

                query.close();

                Intent i2 = new Intent(EditActivity.this,SynchroActivity.class);

                i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i2);

                Toast.makeText(EditActivity.this,"Suppression ok",Toast.LENGTH_LONG).show();

            }
        });

    }


    @Override
    public void onBackPressed(){

        super.onBackPressed(); //Normal behaviour

        Intent i2 = new Intent(EditActivity.this,SynchroActivity.class);

        i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(i2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {
            return true;
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static String POST(String lien, Form form) throws IOException, JSONException {

        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(lien);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("name", form.getName());
            jsonObject.accumulate("address", form.getAdresse());
            jsonObject.accumulate("phone_number", form.getPhone());
            jsonObject.accumulate("email_address", form.getEmail());
            jsonObject.accumulate("xlocation", form.getLatitude());
            jsonObject.accumulate("ylocation", form.getLongitude());
            jsonObject.accumulate("customer_type", form.getType());
            jsonObject.accumulate("created_by", SessionData.getInstance().getIduser());
            jsonObject.accumulate("update_by", SessionData.getInstance().getIduser());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            Log.e("DATA SENT", json);

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }



        // 11. return result
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            // name,adress,emai,fone,latitude,longitude,typv

            form = new Form();
            form.setName(args[0]);
            form.setAdresse(args[1]);
            form.setEmail(args[2]);
            form.setPhone(args[3]);
            form.setLatitude(args[4]);
            form.setLongitude(args[5]);
            form.setType(args[6]);

            try {
                return POST(MainActivity.endpoint+"/shops",form);
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if (pd!=null) { pd.dismiss(); }

            try {

                Object retour = new JSONTokener(result).nextValue();

                if(retour instanceof JSONObject) {

                    JSONObject obj = new JSONObject(result);

                    String name = obj.getString("name");

                    if(name.equals("Bad Request")){

                        String ms1 = obj.getString("message");
                        msgBox.setMessage("Erreur du serveur: " + ms1);
                        msgBox.openSimplePane();

                    } else {

                        final Request q = new Request(getApplicationContext());

                        q.open();

                        q.RemovePoint(key);

                        q.close();

                        msgBox.setMessage("Modification effecctuée avec succès");
                        msgBox.openSimplePane();

                        nom.setText("");
                        adresse.setText("");
                        email.setText("");
                        phone.setText("");
                        choix.setSelection(0);

                    }
                }

                else if(retour instanceof JSONArray) {

                    JSONArray tab = new JSONArray(result); String ms = "Erreur d'enregistrement : ";

                    for(int i = 0; i<tab.length();i++){

                        JSONObject v = tab.getJSONObject(i); String vn = v.getString("message");

                        if(i == 0) ms+=vn; else ms+=", "+vn;
                    }

                    msgBox.setMessage(ms);
                    msgBox.openSimplePane();
                }

            } catch (JSONException e) {

                msgBox.setMessage("Anomalie système: " + e.getLocalizedMessage());
                msgBox.openSimplePane();
                e.printStackTrace();
            }


            Log.e("Error",result);
        }

        @Override
        protected void onPreExecute() {

            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(EditActivity.this);

            pd.setTitle("Connexion");

            pd.setMessage("Envoi des informations");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }


    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
