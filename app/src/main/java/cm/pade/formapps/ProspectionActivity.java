package cm.pade.formapps;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cm.pade.formapps.parser.LocationFilter;
import cm.pade.formapps.parser.OptionPanel;

/**
 * Created by Sumbang on 28/09/2016.
 */

public class ProspectionActivity extends AppCompatActivity implements LocationListener {

    private EditText nom, but;
    private ProgressDialog pd;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private Button bt1;
    private ArrayList<Customer> cliste;
    private Spinner spinner;
    private SearchView searchView;
    private ArrayList<String> spinlist;
    private String currentuser = "";


    private LocationManager locationManager;
    private LocationFilter locationFilter;
    private Location gpsLocation;
    private double Latitude = 0, Longitude = 0, PLatitude = 0, PLongitude = 0;

    //-- MessageBox component
    private OptionPanel msgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final Request query = new Request(ProspectionActivity.this);

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

            Intent i = new Intent(ProspectionActivity.this, LoginActivity.class);

            startActivity(i);

        }

        /*if (!SessionData.getInstance().getRole().equals("merchant")) {

            Toast.makeText(ProspectionActivity.this, "Desole, votre profil n'a pas acces a ce module", Toast.LENGTH_LONG).show();

            Intent i = new Intent(ProspectionActivity.this, HomeActivity.class);

            startActivity(i);
        }*/

        setContentView(R.layout.activity_propection);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbar.setTitle("Prospection");
        setTitle("Prospection");
        if(toolbarText!=null && toolbar!=null) {
            toolbarText.setText("Prospection");
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

        but = (EditText) findViewById(R.id.editText2);

        bt1 = (Button) findViewById(R.id.button);
        cliste = new ArrayList<Customer>();
        spinlist = new ArrayList<String>();

        searchView = (SearchView) findViewById(R.id.searchView);


        // rafraichissement des donnees GPS

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();

        locationFilter = new LocationFilter(this, gpsLocation);
        gpsLocation = locationFilter.getBestLocation();

        // Initialize the location fields
        if (gpsLocation != null) {
            Toast.makeText(ProspectionActivity.this, "Provider gps has been selected.", Toast.LENGTH_LONG).show();
            onLocationChanged(gpsLocation);
        } else {
            Toast.makeText(ProspectionActivity.this, "Position GPS non determinee", Toast.LENGTH_LONG).show();
        }

        if (isConnected()) new HttpAsyncTask().execute("" + SessionData.getInstance().getIduser());

        else
            Toast.makeText(ProspectionActivity.this, "Veuillez vous connecter a internet.", Toast.LENGTH_LONG).show();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                Log.e("SEARCH", s);

                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                loadData(s, spinlist);

                Log.e("SEARCH", s);

                return false;
            }
        });

        msgBox = new OptionPanel(this);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = getDataPosition(currentuser, spinlist);

                int idselect = cliste.get(pos).getId();

                if (currentuser.isEmpty()) {
                    msgBox.setMessage("Veuillez saisir le client");
                    msgBox.openSimplePane();
                }
                else if (but.getText().toString().isEmpty()) {
                    msgBox.setMessage("Veuillez saisir le but de la prospection");
                    msgBox.openSimplePane();
                }
                else if (Latitude == 0 || Longitude == 0) {
                    msgBox.setMessage("Le téléphone n'a pas pu obtenir votre position, veuillez reéssayer dans 10 secondes");
                    msgBox.openSimplePane();

                } else {

                    int idcustomer = idselect;
                    String buts = but.getText().toString();

                    String xlocation = "" + Latitude;
                    String ylocation = "" + Longitude;

                    String chaine = "" + idcustomer + "@@" + buts + "@@" + xlocation + "@@" + ylocation;

                    new HttpAsyncTask1().execute(chaine);

                }

            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed(); //Normal behaviour

        Intent i2 = new Intent(ProspectionActivity.this, HomeActivity.class);

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
        locationManager.removeUpdates(this);
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

    public String GET(String key) {

        int id = Integer.parseInt(key);

        HttpClient Client = new DefaultHttpClient();

        String lien = MainActivity.endpoint+"/rws-customer?merchant="+id;

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

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
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

                    Customer co = new Customer(cu.getInt("id"),cu.getString("name"),cu.getString("xlocation"),cu.getString("ylocation"));

                    cliste.add(co); spinlist.add(cu.getString("name"));

                }

                //ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProspectionActivity.this, android.R.layout.simple_spinner_item, spinlist);

                //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                //spinner.setAdapter(adapter);

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

            pd = new ProgressDialog(ProspectionActivity.this);

            pd.setMessage("Chargement des clients");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }

    public static String POST(String chaine) throws IOException, JSONException {

        String lien = MainActivity.endpoint+"/rws-customer-visit/create";

        String[] chaitab = chaine.split("@@"); String retour = "";

        String customer = chaitab[0]; String but = chaitab[1];

        String xlo = chaitab[2]; String ylo = chaitab[3];


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
                jsonObject.accumulate("customer", Integer.parseInt(customer));
                jsonObject.accumulate("merchant", SessionData.getInstance().getIduser());
                jsonObject.accumulate("purpose", but);
                jsonObject.accumulate("created_by", SessionData.getInstance().getIduser());
                jsonObject.accumulate("created_on", xlo);
                jsonObject.accumulate("updated_on", ylo);

                // 4. convert JSONObject to JSON to String
                json = jsonObject.toString();

                Log.e("DATA SENT", json);

                // ** Alternative way to convert Person object to JSON string usin Jackson Lib
                // ObjectMapper mapper = new ObjectMapper();
                // json = mapper.writeValueAsString(person);

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(json, "UTF-8");

                // 6. set httpPost Entity
                httpPost.setEntity(se);

                // 7. Set some headers to inform server about the type of the content
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json; charset=UTF-8");

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                retour = EntityUtils.toString(httpEntity, "UTF-8");

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }



        return retour;
    }

    private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            try {
                return POST(args[0]);
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

            pd = new ProgressDialog(ProspectionActivity.this);

            pd.setMessage("Enregistrement en cours ...");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }

    private int getDataPosition(String pos, ArrayList<String> li) {
        int retour = 0;
        for (int i = 0; i < li.size(); i++) {
            if (((String) li.get(i)).equals(pos)) {
                retour = i;
            }
        }
        return retour;
    }

    private void loadData(String query, final ArrayList<String> uliste) {

        final ArrayList<String> verif = new ArrayList();

        final MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "text"});

        int j = 0;

        for (int i = 0; i < uliste.size(); i++) {

            if (isPresent(query, (String) uliste.get(i)).booleanValue()) {

                Object[] temp = new Object[]{Integer.valueOf(j), uliste.get(i)};

                Log.e("UPDATE", query + "-" + i);

                cursor.addRow(temp);

                j++;

                verif.add(uliste.get(i));

            } else {

                Log.e("UPDATE", "no present " + i);

            }
        }

        final SearchAdapter adaptersearch = new SearchAdapter(this, cursor, verif);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final SearchView search = (SearchView) findViewById(R.id.searchView);

        search.setSuggestionsAdapter(adaptersearch);

        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override

            public boolean onSuggestionSelect(int i) {

                String select = verif.get(i);

                Log.e("VEF",select);

                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {

                String select = verif.get(i);

                Log.e("VEF",select);

                search.setQuery(select, false); currentuser = select;

                return true;
            }
        });

    }

    Boolean isPresent(String query, String s) {
        return s.toLowerCase().contains(query.toLowerCase()) ? Boolean.valueOf(true) : Boolean.valueOf(false);
    }

}
