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
import android.widget.ListView;
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

public class DistributeurActivity extends AppCompatActivity implements LocationListener {

    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ArrayList<Merchant> cliste;
    private Spinner spinner;
    private Button bt1;
    private ArrayList<Distribution> mliste;
    private EditText total, qte;
    private ProgressDialog pd;
    private ListView liste;
    private List<String> spinlist;
    DistributionAdapter padater = null;


    private LocationManager locationManager;
    LocationFilter locationFilter;
    Location gpsLocation;
    private double Latitude = 0, Longitude = 0, PLatitude = 0, PLongitude = 0;

    //-- MessageBox component
    private OptionPanel msgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Request query = new Request(DistributeurActivity.this);

        query.open();

        ArrayList<User> luser = query.getAllUser();

        query.close();

        if (luser != null) {

            User u = luser.get(0);

            if (SessionData.getInstance() == null || SessionData.getInstance().getStatus() == 0) {

                SessionData.getInstance().setStatus(u.getStatus());
                SessionData.getInstance().setIduser(u.getIduser());
                SessionData.getInstance().setUsername(u.getUsername());
                SessionData.getInstance().setRole(u.getRole());

            }

        } else {

            Intent i = new Intent(DistributeurActivity.this, LoginActivity.class);

            startActivity(i);

        }
        setContentView(R.layout.activity_distributeur);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        if(toolbarText!=null && toolbar!=null) {
            toolbarText.setText("Sortie Distributeur");
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


        qte = (EditText) findViewById(R.id.editText3);
        bt1 = (Button) findViewById(R.id.button);

        total = (EditText) findViewById(R.id.editText4);

        bt1 = (Button) findViewById(R.id.button);
        mliste = new ArrayList<Distribution>();

        liste = (ListView) findViewById(R.id.content);
        spinner = (Spinner) findViewById(R.id.spinner);

        spinlist = new ArrayList<String>();
        cliste = new ArrayList<Merchant>();


        // rafraichissement des donnees GPS

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        locationFilter = new LocationFilter(this, gpsLocation);
        gpsLocation = locationFilter.getBestLocation();

        // Initialize the location fields
        if (gpsLocation != null) {
            Toast.makeText(DistributeurActivity.this, "Provider gps has been selected.", Toast.LENGTH_LONG).show();
            onLocationChanged(gpsLocation);
        } else {
            Toast.makeText(DistributeurActivity.this, "Position GPS non determinee", Toast.LENGTH_LONG).show();
        }

        if (isConnected()) new HttpAsyncTask().execute("" + SessionData.getInstance().getIduser());

        else
            Toast.makeText(DistributeurActivity.this, "Veuillez vous connecter a internet.", Toast.LENGTH_LONG).show();


        msgBox = new OptionPanel(this);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int merchant = 0;

                if (!spinlist.isEmpty()) {

                    String selectspin = spinner.getSelectedItem().toString();

                    Merchant merchant1 = new Merchant();

                    for (int k = 0; k < cliste.size(); k++) {

                        if (cliste.get(k).getName().equals(selectspin)) {

                            merchant = cliste.get(k).getId();

                            merchant1 = cliste.get(k);
                        }
                    }

                }

                StringBuffer responseText = new StringBuffer();

                ArrayList<Distribution> produitsList = padater.produitList;
                String chaine = "";
                int q = 0;

                int qtezero = 0;
                int prixzero = 0;

                for (int i = 0; i < produitsList.size(); i++) {

                    Distribution p = produitsList.get(i);

                    int idproduit = p.getProduit().getId();

                    if (p.getProduit().isSelected()) {

                        if (q == 0)
                            chaine += "" + idproduit + ";" + p.getQte() + ";" + p.getPrice() + ";" + p.getUnite();

                        else
                            chaine += "@@" + idproduit + ";" + p.getQte() + ";" + p.getPrice() + ";" + p.getUnite();

                        q++;

                        if (p.getQte() == 0) qtezero = 1;

                        if (p.getPrice() == 0) prixzero = 1;

                    }

                }

                if (q == 0) {
                    msgBox.setMessage("Veuillez choisir au moins un produit");
                    msgBox.openSimplePane();

                } else if (merchant == 0) {
                    msgBox.setMessage("Veuillez choisir au moins un marchand");
                    msgBox.openSimplePane();

                } else if (qtezero == 1) {
                    msgBox.setMessage("Certains de vos produits ont des quantites nulles");
                    msgBox.openSimplePane();

                } else if (prixzero == 1) {
                    msgBox.setMessage("Certains de vos produits ont des prix nuls");
                    msgBox.openSimplePane();

                } else if (Latitude == 0 || Longitude == 0) {
                    msgBox.setMessage("Le téléphone n'a pas pu obtenir votre position, veuillez reéssayer dans 10 secondes");
                    msgBox.openSimplePane();
                } else
                    new HttpAsyncTask1().execute(chaine, "" + Latitude, "" + Longitude, "" + merchant);
            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed(); //Normal behaviour

        Intent i2 = new Intent(DistributeurActivity.this, HomeActivity.class);

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

    public String GET() {

        HttpClient Client = new DefaultHttpClient();

        String lien = MainActivity.endpoint+"/rws-product";

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

    public String GET(String key) {

        int id = Integer.parseInt(key);

        HttpClient Client = new DefaultHttpClient();

        String lien = MainActivity.endpoint+"/rws-wholesaler/get-assigned-merchants?wholesalerId="+id;

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


    public static String POST(String chaine,String xlocation,String ylocation,String merchant) throws IOException, JSONException {

        String lien = MainActivity.endpoint+"/rws-wholesaler-product-out/create";

        String[] chaitab = chaine.split("@@"); String retour = "";

        // creation de l'objet JSon contenant la liste des produits

        JSONArray jsonArray = new JSONArray();

        for(int a = 0; a < chaitab.length; a++) {

            JSONObject json1 = new JSONObject();

            String[] currentab = chaitab[a].split(";");

            json1.accumulate("product", currentab[0]);
            json1.accumulate("quantity", currentab[1]);
            json1.accumulate("unit_price", currentab[2]);
            json1.accumulate("unity_or_box", currentab[3]);

            jsonArray.put(json1);

        }


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
            jsonObject.accumulate("merchant", Integer.parseInt(merchant));
            jsonObject.accumulate("wholesalerProductOutLines", jsonArray);
            jsonObject.accumulate("created_by",SessionData.getInstance().getIduser());

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

            String re1 = GET(); String re2 = GET(args[0]);

            return re1+"@@"+re2;

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if (pd!=null) { pd.dismiss(); }

            String[] t = result.split("@@");

            if(t[0] != null){

                Log.e("RES",t[0]);

                try {

                    JSONArray tab = new JSONArray(t[0]);

                    for(int i=0; i< tab.length(); i++){

                        JSONObject pd = tab.getJSONObject(i);

                        Produit p = new Produit(pd.getInt("id"),pd.getString("name"),0,false);

                        Distribution distribution = new Distribution();

                        distribution.setProduit(p); distribution.setQte(0); distribution.setPrice(0);

                        mliste.add(distribution);

                    }

                    padater = new DistributionAdapter(DistributeurActivity.this,R.layout.produit_list,mliste);

                    liste.setAdapter(padater);

                    liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // When clicked, show a toast with the TextView text
                            Produit pt = (Produit) parent.getItemAtPosition(position);
                            //Toast.makeText(getApplicationContext(), "Clicked on Row: " + pt.getName(), Toast.LENGTH_LONG).show();
                        }
                    });


                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

            if(t[1] != null){

                Log.e("RES",t[1]);

                try {

                    JSONArray tab1 = new JSONArray(t[1]);

                    for(int i=0; i< tab1.length(); i++){

                        JSONObject cu = tab1.getJSONObject(i);

                        Merchant merchant = new Merchant();

                        merchant.setId(cu.getInt("id")); merchant.setName(cu.getString("merchant_name"));

                        cliste.add(merchant); spinlist.add(cu.getString("merchant_name"));

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(DistributeurActivity.this, android.R.layout.simple_spinner_item, spinlist);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinner.setAdapter(adapter);

                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }

            Log.e("Error","Le resultat est : "+result);
        }

        @Override
        protected void onPreExecute() {

            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(DistributeurActivity.this);

            pd.setTitle("Connexion");

            pd.setMessage("Chargement des produits");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }

    private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            try {
                return POST(args[0],args[1],args[2],args[3]);
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

            pd = new ProgressDialog(DistributeurActivity.this);

            pd.setMessage("Enregistrement en cours ...");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }
}
