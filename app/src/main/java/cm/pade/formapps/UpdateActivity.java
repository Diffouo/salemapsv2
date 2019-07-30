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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cm.pade.formapps.parser.LocationFilter;
import cm.pade.formapps.parser.OptionPanel;

/**
 * Created by Sumbang on 08/11/2016.
 */

public class UpdateActivity extends AppCompatActivity implements LocationListener {

    private EditText nom, adresse, email, phone, lon, lat;
    private Spinner choix, spinner1;
    private Button valider, vider, capture;
    String responseServer = "";
    Form form;
    private ProgressDialog pd;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private ArrayList<Customer> cliste;
    static final String endpoint = "http://vps311697.ovh.net/salesmgt/web";
    private CheckBox check;
    private String oldx = "";
    private String oldy = "";
    private String currentuser = "";
    private SearchView searchView;
    private ArrayList<String> spinlist;
    private ArrayList<CategorieUser> cuser;
    private ArrayList<String> spinlist2;

    private LocationManager locationManager;
    private LocationFilter locationFilter;
    private Location gpsLocation;
    private double Latitude = 0, Longitude = 0, PLatitude = 0, PLongitude = 0;

    private OptionPanel msgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Request query = new Request(UpdateActivity.this);

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

            Intent i = new Intent(UpdateActivity.this, LoginActivity.class);

            startActivity(i);

        }

        setContentView(R.layout.activity_update);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbar.setTitle("Modifier un point de vente");
        setTitle("Modifier un point de vente");
        if(toolbarText!=null && toolbar!=null) {
            toolbarText.setText("Modifier un point de vente");
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

        adresse = (EditText) findViewById(R.id.editText2);
        email = (EditText) findViewById(R.id.editText4);
        phone = (EditText) findViewById(R.id.editText3);
        choix = (Spinner) findViewById(R.id.spinner);
        valider = (Button) findViewById(R.id.button);
        lon = (EditText) findViewById(R.id.editText6);
        lat = (EditText) findViewById(R.id.editText5);
        vider = (Button) findViewById(R.id.button2);
        // spinner1 = (Spinner)findViewById(R.id.spinner1);
        check = (CheckBox) findViewById(R.id.check);

        searchView = (SearchView) findViewById(R.id.searchView);

        cliste = new ArrayList<Customer>();
        spinlist = new ArrayList<String>();

        cuser = new ArrayList<CategorieUser>();
        spinlist2 = new ArrayList<String>();


        // rafraichissement des donnees GPS

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        locationFilter = new LocationFilter(this, gpsLocation);
        gpsLocation = locationFilter.getBestLocation();

        // Initialize the location fields
        if (gpsLocation != null) {
            Toast.makeText(UpdateActivity.this, "Provider gps has been selected.", Toast.LENGTH_LONG).show();
            onLocationChanged(gpsLocation);
        } else {
            Toast.makeText(UpdateActivity.this, "Position GPS non determinee", Toast.LENGTH_LONG).show();
        }

        if (isConnected()) {
            new HttpAsyncTask().execute("" + SessionData.getInstance().getIduser());
        } else
            Toast.makeText(UpdateActivity.this, "Veuillez vous connecter a internet.", Toast.LENGTH_LONG).show();

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

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    lat.setText("" + Latitude);
                    lon.setText("" + Longitude);
                } else {

                    lat.setText(oldx);
                    lon.setText(oldy);
                }

            }
        });


        vider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adresse.setText("");
                email.setText("");
                phone.setText("");

                choix.setSelection(0);
                lon.setText("");
                lat.setText("");

            }
        });


        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = getDataPosition(currentuser, spinlist);

                int idselect = cliste.get(pos).getId();

                String la = "";
                String lo = "";

                String adress = adresse.getText().toString();
                String emai = email.getText().toString();
                String fone = phone.getText().toString();
                String type = choix.getSelectedItem().toString();

                la = lat.getText().toString();
                lo = lon.getText().toString();

                if (la.isEmpty() || lo.isEmpty()) {
                    msgBox.setMessage("Le téléphone n'a pas pu obtenir votre position, veuillez reéssayer dans 10 secondes");
                    msgBox.openSimplePane();
                } else if (la.equals("0") || lo.equals("0")) {
                    msgBox.setMessage("Le téléphone n'a pas pu obtenir votre position, veuillez reéssayer dans 10 secondes");
                    msgBox.openSimplePane();

                } else if (isConnected()) {

                    new HttpAsyncTask2().execute(la, lo, "" + idselect);
                }

            }
        });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed(); //Normal behaviour

        Intent i2 = new Intent(UpdateActivity.this, HomeActivity.class);

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

    public String GET2(String key) {

        int id = Integer.parseInt(key);

        HttpClient Client = new DefaultHttpClient();

        String lien = MainActivity.endpoint+"/rws-customers/"+id;

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

    public String GET() {

        HttpClient Client = new DefaultHttpClient();

        String lien = MainActivity.endpoint+"/rws-customer-type";

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


    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            String re1 = GET();

            return re1;

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            if (pd!=null) { pd.dismiss(); }

            if(result != null){

                Log.e("RES",result);

                try {

                    JSONArray tab = new JSONArray(result);

                    for(int i=0; i< tab.length(); i++){

                        JSONObject pd = tab.getJSONObject(i);

                        CategorieUser categorieUser = new CategorieUser();

                        categorieUser.setId(pd.getInt("id")); categorieUser.setName(pd.getString("name"));

                        categorieUser.setCode(pd.getString("code"));  cuser.add(categorieUser);

                        spinlist.add(pd.getString("name"));

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateActivity.this, android.R.layout.simple_spinner_item, spinlist);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    choix.setAdapter(adapter);


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

            pd = new ProgressDialog(UpdateActivity.this);

            pd.setMessage("Chargement des types de client ...");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }

    public static String PUT(String xlocation,String ylocation,String customer) throws IOException, JSONException {

        int cle = Integer.parseInt(customer);

        String lien = MainActivity.endpoint+"/rws-customers/"+cle; String retour = "";

        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make PUT request to the given URL
            HttpPut httpPut = new HttpPut(lien);

            Log.e("LIEN", lien);

            String json = "";

            // 3. build jsonObject

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("xlocation", xlocation);
            jsonObject.accumulate("ylocation", ylocation);
            jsonObject.accumulate("updated_by", ""+SessionData.getInstance().getIduser());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            Log.e("DATA SENT", json);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json, "UTF-8");

            // 6. set httpPost Entity
            httpPut.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json; charset=UTF-8");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPut);
            HttpEntity httpEntity = httpResponse.getEntity();
            retour = EntityUtils.toString(httpEntity, "UTF-8");

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return retour;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

         String re2 = GET(args[0]); String re1 = GET();

            return re2+"@@"+re1;

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Log.e("RES",result);

            if (pd!=null) { pd.dismiss(); }

            String[] t = result.split("@@");

            if(t[0] != null){

                Log.e("RES",t[0]);

                try {

                    JSONArray tab1 = new JSONArray(t[0]);

                    for(int i=0; i< tab1.length(); i++){

                        JSONObject cu = tab1.getJSONObject(i);

                        Customer co = new Customer(cu.getInt("id"),cu.getString("name"),cu.getString("xlocation"),cu.getString("ylocation"));

                        cliste.add(co); spinlist.add(cu.getString("name"));

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }

            if(t[1]  != null){

                Log.e("RES",t[1]);

                try {

                    JSONArray tab = new JSONArray(t[1]);

                    for(int i=0; i< tab.length(); i++){

                        JSONObject pd = tab.getJSONObject(i);

                        CategorieUser categorieUser = new CategorieUser();

                        categorieUser.setId(pd.getInt("id")); categorieUser.setName(pd.getString("name"));

                        categorieUser.setCode(pd.getString("code"));  cuser.add(categorieUser);

                        spinlist2.add(pd.getString("name"));

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateActivity.this, android.R.layout.simple_spinner_item, spinlist2);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    choix.setAdapter(adapter);


                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

            //Log.e("Error","Le resultat est : "+result);
        }

        @Override
        protected void onPreExecute() {

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(UpdateActivity.this);
            pd.setMessage("Chargement des informations ...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();

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

    private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            return GET2(args[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            Log.e("RETOUR",result);

            if (pd!=null) { pd.dismiss(); }

            if(result != null){

                try {

                    JSONObject jsonObject = new JSONObject(result);

                    String name = jsonObject.getString("name");

                    if(!name.equals("Not Found")) {

                        oldx = jsonObject.getString("xlocation"); oldy = jsonObject.getString("ylocation");

                        adresse.setText(jsonObject.getString("address")); phone.setText(jsonObject.getString("phone_number"));

                        email.setText(jsonObject.getString("email_address")); lon.setText(jsonObject.getString("ylocation"));

                        lat.setText(jsonObject.getString("xlocation")); choix.setSelection(jsonObject.getInt("customer_type")-1);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected void onPreExecute() {

            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(UpdateActivity.this);
            pd.setMessage("Chargement du client");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }

    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            try {
                return PUT(args[0],args[1],args[2]);
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
                        msgBox.setMessage("Modification effectuée avec succès");
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

                e.printStackTrace();
            }


            Log.e("Error","Le resultat est : "+result);
        }

        @Override
        protected void onPreExecute() {

            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(UpdateActivity.this);

            pd.setMessage("Modificatoin des informations");

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

                 int select2 = 0;

                for(int k = 0; k < cliste.size(); k++ ){

                    if(cliste.get(k).getName().equals(select)) {

                        select2 = cliste.get(k).getId();
                    }
                }

                new HttpAsyncTask1().execute(""+select2);

                return true;
            }
        });

    }

    Boolean isPresent(String query, String s) {
        return s.toLowerCase().contains(query.toLowerCase()) ? Boolean.valueOf(true) : Boolean.valueOf(false);
    }

}
