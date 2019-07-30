package cm.pade.formapps;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
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
import java.util.List;

import cm.pade.formapps.parser.LocationFilter;
import cm.pade.formapps.parser.OptionPanel;

/**
 * Created by Sumbang on 28/09/2016.
 */

public class Recouvrement2Activity extends AppCompatActivity implements LocationListener {

    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ArrayList<Distributeur> cliste;
    private ArrayList<Sortie> lliste;
    private Spinner spinner, spinner2;
    private List<String> spinlist, spinlist2;
    private EditText total, montant;
    private ProgressDialog pd;
    private Button bt1;


    private LocationManager locationManager;
    private LocationFilter locationFilter;
    private Location gpsLocation;
    private double Latitude = 0, Longitude = 0, PLatitude = 0, PLongitude = 0;

    private OptionPanel msgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Request query = new Request(Recouvrement2Activity.this);

        query.open();

        ArrayList<User> luser = query.getAllUser();

        query.close();

        if (luser != null) {

            User u = luser.get(0);

            if (SessionData.getInstance() == null || SessionData.getInstance().getStatus() == 0) {

                SessionData.getInstance().setStatus(u.getStatus());
                SessionData.getInstance().setIduser(u.getIduser());
                SessionData.getInstance().setUsername(u.getUsername());

            }

        } else {

            Intent i = new Intent(Recouvrement2Activity.this, LoginActivity.class);

            startActivity(i);

        }

        setContentView(R.layout.activity_recrouvement2);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbar.setTitle("Versement Distributeur");
        setTitle("Versement Distributeur");
        if(toolbarText!=null && toolbar!=null) {
            toolbarText.setText("Versement Distributeur");
            setSupportActionBar(toolbar);
        }
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                i.putExtra("current", 1);
                startActivity(i);
            }
        });

        total = (EditText) findViewById(R.id.editText6);
        montant = (EditText) findViewById(R.id.editText4);

        spinner = (Spinner) findViewById(R.id.spinner);
        bt1 = (Button) findViewById(R.id.button);

        cliste = new ArrayList<Distributeur>();
        spinlist = new ArrayList<String>();

        lliste = new ArrayList<Sortie>();
        spinlist2 = new ArrayList<String>();

        spinner2 = (Spinner) findViewById(R.id.spinner2);


        // rafraichissement des donnees GPS

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        locationFilter = new LocationFilter(this, gpsLocation);
        gpsLocation = locationFilter.getBestLocation();

        // Initialize the location fields
        if (gpsLocation != null) {
            Toast.makeText(Recouvrement2Activity.this, "Provider gps has been selected.", Toast.LENGTH_LONG).show();
            onLocationChanged(gpsLocation);
        } else {
            Toast.makeText(Recouvrement2Activity.this, "Position GPS non determinee", Toast.LENGTH_LONG).show();
        }

        if (isConnected()) new HttpAsyncTask().execute("" + SessionData.getInstance().getIduser());

        else
            Toast.makeText(Recouvrement2Activity.this, "Veuillez vous connecter a internet.", Toast.LENGTH_LONG).show();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String current = spinlist.get(i);
                Distributeur distributeur = new Distributeur();
                int select = 0;

                for (int k = 0; k < cliste.size(); k++) {

                    if (cliste.get(k).getName().equals(current)) {

                        select = cliste.get(k).getId();
                        distributeur = cliste.get(k);
                    }
                }

                spinlist2.clear();
                lliste.clear();

                new HttpAsyncTask2().execute("" + SessionData.getInstance().getIduser(), "" + select);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        msgBox = new OptionPanel(this);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int idselect = 0;

                if (!cliste.isEmpty()) {

                    String selectspin = spinner.getSelectedItem().toString();

                    Distributeur current = new Distributeur();

                    for (int k = 0; k < cliste.size(); k++) {

                        if (cliste.get(k).getName().equals(selectspin)) {

                            idselect = cliste.get(k).getId();
                            current = cliste.get(k);
                        }
                    }

                }

                int idselect2 = 0;
                String montantt = "";

                if (lliste.size() != 0) {

                    String selectspin2 = spinner2.getSelectedItem().toString();

                    Sortie sortie = new Sortie();

                    for (int k = 0; k < lliste.size(); k++) {

                        String cp = lliste.get(k).getDate() + " | " + lliste.get(k).getMontant();

                        if (cp.equals(selectspin2)) {

                            idselect2 = lliste.get(k).getId();
                            sortie = lliste.get(k);
                        }
                    }

                    montantt = sortie.getMontant();

                }

                if (montant.getText().toString().isEmpty()) {
                    msgBox.setMessage("Veuillez saisir le montant paye");
                    msgBox.openSimplePane();

                } else if (idselect2 == 0) {
                    msgBox.setMessage("Veuillez choisir une sortie");
                    msgBox.openSimplePane();

                } else if (idselect == 0) {
                    msgBox.setMessage("Veuillez choisir un distributeur");
                    msgBox.openSimplePane();

                } else if (Latitude == 0 || Longitude == 0) {
                    msgBox.setMessage("Le téléphone n'a pas pu obtenir votre position, veuillez reéssayer dans 10 secondes");
                    msgBox.openSimplePane();

                } else
                    new HttpAsyncTask1().execute(montant.getText().toString(), montantt, "" + Latitude, "" + Longitude, "" + idselect, "" + idselect2);
            }
        });
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed(); //Normal behaviour

        Intent i2 = new Intent(Recouvrement2Activity.this, HomeActivity.class);

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


    @Override
    public void onLocationChanged(Location location) {

        Longitude = location.getLongitude();
        Latitude = location.getLatitude();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 1, this);
    }

    @Override
    public void onPause() {
        locationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Veuillez activer le GPS s'il vous plait ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public String GET(String key) {

        int id = Integer.parseInt(key);

        HttpClient Client = new DefaultHttpClient();

        String lien = MainActivity.endpoint+"/rws-wholesaler?merchant="+id;

        Log.e("LIEN",lien);

        try
        {
            String SetServerString = "";

            // Create Request to server and get response

            HttpGet httpget = new HttpGet(lien);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            SetServerString = Client.execute(httpget, responseHandler);

            // Show response on activity

            return SetServerString;
        }
        catch(Exception ex)
        {
            return null;
        }

    }

    public String GET(String merchant,String customer) {

        int idm = Integer.parseInt(merchant); int idc = Integer.parseInt(customer);

        HttpClient Client = new DefaultHttpClient();

        String lien = MainActivity.endpoint+"/rws-wholesaler-product-out/get-wholesaler-product-outs-not-paid?merchantId="+idm+"&wholesalerId="+idc;

        Log.e("LIEN",lien);

        try
        {
            String SetServerString = "";

            // Create Request to server and get response

            HttpGet httpget = new HttpGet(lien);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            SetServerString = Client.execute(httpget, responseHandler);

            // Show response on activity

            return SetServerString;
        }
        catch(Exception ex)
        {
            return null;
        }

    }

    public static String POST(String montant, String total,String xlocation,String ylocation,String user,String sortie) throws IOException, JSONException {

        String lien = MainActivity.endpoint+"/rws-wholesaler-payment/create"; String retour = "";

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
            jsonObject.accumulate("wholesaler", Integer.parseInt(user));
            jsonObject.accumulate("amount_to_pay", Integer.parseInt(total));
            jsonObject.accumulate("wholesaler_product_out", Integer.parseInt(sortie));
            jsonObject.accumulate("amount_paid", Integer.parseInt(montant));
            jsonObject.accumulate("created_by", SessionData.getInstance().getIduser());

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
                retour = convertInputStreamToString(inputStream);
            else
                retour = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return retour;
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

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            String re2 = GET(args[0]);

            return re2;

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if (pd!=null) { pd.dismiss(); }

            Log.e("RES",result);

            try {

                JSONArray tab1 = new JSONArray(result);

                for(int i=0; i< tab1.length(); i++){

                    JSONObject cu = tab1.getJSONObject(i);

                    Distributeur distributeur = new Distributeur();

                    distributeur.setId(cu.getInt("id")); distributeur.setName(cu.getString("name"));

                    distributeur.setPhone(cu.getString("phone_number")); distributeur.setAdresse(cu.getString("address"));

                    distributeur.setEmail(cu.getString("email_address"));

                    cliste.add(distributeur); spinlist.add(cu.getString("name"));

                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Recouvrement2Activity.this, android.R.layout.simple_spinner_item, spinlist);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(adapter);

            } catch (JSONException e) {

                e.printStackTrace();
            }


            Log.e("Error","Le resultat est : "+result);
        }

        @Override
        protected void onPreExecute() {

            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(Recouvrement2Activity.this);

            pd.setMessage("Chargement des distributeurs ...");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }

    private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            try {
                return POST(args[0],args[1],args[2],args[3], args[4], args[5]);
            } catch (IOException e) {
                e.printStackTrace();
                return  null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
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

                    if(obj.has("message")) {

                        String ms1 = obj.getString("message");

                        msgBox.setMessage("Erreur serveur: " + ms1);
                        msgBox.openSimplePane();
                    }

                    else {
                        msgBox.setMessage("Enregistrement effectué avec succès");
                        msgBox.openSuccessPane();
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

            Log.e("Error","Le resultat est : "+result);
        }

        @Override
        protected void onPreExecute() {

            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(Recouvrement2Activity.this);

            pd.setMessage("Enregistrement en cours ...");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }

    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            String re = GET(args[0],args[1]);

            return re;

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if (pd!=null) { pd.dismiss(); }

            Log.e("RES",result);

            try {

                JSONArray tab1 = new JSONArray(result);

                for(int i=0; i< tab1.length(); i++){

                    JSONObject cu = tab1.getJSONObject(i);

                    Sortie sortie = new Sortie();

                    sortie.setId(cu.getInt("id")); sortie.setDate(cu.getString("date")); sortie.setMontant(cu.getString("amount_to_pay"));

                    spinlist2.add(cu.getString("date")+" | "+cu.getString("amount_to_pay"));

                    lliste.add(sortie);

                }

                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(Recouvrement2Activity.this, android.R.layout.simple_spinner_item, spinlist2);

                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner2.setAdapter(adapter2);

            } catch (JSONException e) {

                e.printStackTrace();
            }


            Log.e("Error","Le resultat est : "+result);
        }

        @Override
        protected void onPreExecute() {

            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(Recouvrement2Activity.this);

            pd.setMessage("Chargement des sorties ...");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }
}

