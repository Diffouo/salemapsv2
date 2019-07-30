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
import android.widget.EditText;
import android.widget.SearchView;
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

public class RecouvrementActivity extends AppCompatActivity implements LocationListener {

    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ArrayList<Customer> cliste;
    private ArrayList<Livraison> lliste;
    private Spinner spinner, spinner2;
    private List<String> spinlist2;
    private EditText total, montant;
    private ProgressDialog pd;
    private Button bt1;
    private SearchView searchView;
    private ArrayList<String> spinlist;
    private String currentuser = "";


    private LocationManager locationManager;
    private LocationFilter locationFilter;
    private Location gpsLocation;
    private double Latitude = 0, Longitude = 0, PLatitude = 0, PLongitude = 0;

    private OptionPanel msgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Request query = new Request(RecouvrementActivity.this);

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

            Intent i = new Intent(RecouvrementActivity.this, LoginActivity.class);

            startActivity(i);

        }

        setContentView(R.layout.activity_recrouvement);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbar.setTitle("Recouvrement client");
        setTitle("Recouvrement client");
        if(toolbarText!=null && toolbar!=null) {
            toolbarText.setText("Recouvrement client");
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

        montant = (EditText) findViewById(R.id.editText4);

        searchView = (SearchView) findViewById(R.id.searchView);
        bt1 = (Button) findViewById(R.id.button);

        cliste = new ArrayList<Customer>();
        spinlist = new ArrayList<String>();

        lliste = new ArrayList<Livraison>();
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
            Toast.makeText(RecouvrementActivity.this, "Provider gps has been selected.", Toast.LENGTH_LONG).show();
            onLocationChanged(gpsLocation);
        } else {
            Toast.makeText(RecouvrementActivity.this, "Position GPS non determinee", Toast.LENGTH_LONG).show();
        }

        if (isConnected()) new HttpAsyncTask().execute("" + SessionData.getInstance().getIduser());

        else
            Toast.makeText(RecouvrementActivity.this, "Veuillez vous connecter a internet.", Toast.LENGTH_LONG).show();


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

                int idselect2 = 0;
                String montantt = "";

                if (lliste.size() != 0) {

                    String selectspin2 = spinner2.getSelectedItem().toString();

                    Livraison livraison = new Livraison();

                    for (int k = 0; k < lliste.size(); k++) {

                        String cp = lliste.get(k).getDate() + " | " + lliste.get(k).getMontant();

                        if (cp.equals(selectspin2)) {

                            idselect2 = lliste.get(k).getId();
                            livraison = lliste.get(k);
                        }
                    }

                    montantt = livraison.getMontant();

                }

                if (currentuser.isEmpty()) {
                    msgBox.setMessage("Veuillez saisir le client");
                    msgBox.openSimplePane();

                } else if (montant.getText().toString().isEmpty()) {
                    msgBox.setMessage("Veuillez saisir le montant payé");
                    msgBox.openSimplePane();

                } else if (idselect2 == 0) {
                    msgBox.setMessage("Veuillez choisir une livraison");
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

        Intent i2 = new Intent(RecouvrementActivity.this, HomeActivity.class);

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


    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
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

    public String GET(String merchant,String customer) {

        int idm = Integer.parseInt(merchant); int idc = Integer.parseInt(customer);

        HttpClient Client = new DefaultHttpClient();

        String lien = MainActivity.endpoint+"/rws-delivery/get-deliveries-not-paid?merchantId="+idm+"&customerId="+idc;

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

    public static String POST(String montant, String total,String xlocation,String ylocation,String user,String delivery) throws IOException, JSONException {

        String lien = MainActivity.endpoint+"/rws-money-recovery/create"; String retour = "";

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
            jsonObject.accumulate("customer", Integer.parseInt(user));
            jsonObject.accumulate("amount_to_pay", Integer.parseInt(total));
            jsonObject.accumulate("amount_paid", Integer.parseInt(montant));
            jsonObject.accumulate("delivery", Integer.parseInt(delivery));
            jsonObject.accumulate("created_by", SessionData.getInstance().getIduser());
            jsonObject.accumulate("created_on", xlocation);
            jsonObject.accumulate("updated_on", ylocation);

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

                        Customer co = new Customer(cu.getInt("id"),cu.getString("name"),cu.getString("xlocation"),cu.getString("ylocation"));

                        cliste.add(co); spinlist.add(cu.getString("name"));

                    }

                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecouvrementActivity.this, android.R.layout.simple_spinner_item, spinlist);

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

            pd = new ProgressDialog(RecouvrementActivity.this);

            pd.setMessage("Chargement des clients ...");

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

            pd = new ProgressDialog(RecouvrementActivity.this);

            pd.setMessage("Enregistrement  en cours ...");

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

                    Livraison livraison = new Livraison();

                    livraison.setId(cu.getInt("id")); livraison.setDate(cu.getString("date")); livraison.setMontant(cu.getString("amount_to_pay"));

                    spinlist2.add(cu.getString("date")+" | "+cu.getString("amount_to_pay"));

                     lliste.add(livraison);

                }

                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(RecouvrementActivity.this, android.R.layout.simple_spinner_item, spinlist2);

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

            Log.i("TAG", "Recherche des informations");

            pd = new ProgressDialog(RecouvrementActivity.this);

            pd.setMessage("Chargement des livraisons ...");

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

                spinlist2.clear(); lliste.clear();

                new HttpAsyncTask2().execute(""+SessionData.getInstance().getIduser(),""+select2);

                return true;
            }
        });

    }

    Boolean isPresent(String query, String s) {
        return s.toLowerCase().contains(query.toLowerCase()) ? Boolean.valueOf(true) : Boolean.valueOf(false);
    }

}

