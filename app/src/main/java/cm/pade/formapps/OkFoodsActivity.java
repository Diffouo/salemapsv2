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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
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

import cm.pade.formapps.parser.HttpRequest;
import cm.pade.formapps.parser.LocationFilter;
import cm.pade.formapps.parser.OptionPanel;

/**
 * Created by Sumbang on 27/09/2016.
 */

public class OkFoodsActivity extends AppCompatActivity implements LocationListener {

    private EditText nom;
    private ProgressDialog pd;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private ListView liste;
    private Button bt1;
    private ArrayList<Produit> mliste;
    ProduitListAdapter padater = null;
    private ArrayList<Customer> cliste;
    private Spinner spinner;
    private SearchView searchView;
    private ArrayList<String> spinlist;
    private String currentuser = "";

    private LocationManager locationManager;
    private Location gpsLocation;
    private double Latitude = 0, Longitude = 0, PLatitude = 0, PLongitude = 0;

    //-- MessageBox component
    private OptionPanel msgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Request query = new Request(OkFoodsActivity.this);

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

            Intent i = new Intent(OkFoodsActivity.this, LoginActivity.class);

            startActivity(i);

        }

        /*if (!SessionData.getInstance().getRole().equals("merchant")) {

            Toast.makeText(OkFoodsActivity.this, "Desole, votre profil n'a pas acces a ce module", Toast.LENGTH_LONG).show();

            Intent i = new Intent(OkFoodsActivity.this, HomeActivity.class);

            startActivity(i);
        }*/

        setContentView(R.layout.activity_okfood);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbar.setTitle("Presence de nos produits");
        setTitle("PPresence de nos produits");
        if(toolbarText!=null && toolbar!=null) {
            toolbarText.setText("Presence de nos produits");
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
        liste = (ListView) findViewById(R.id.content);
        nom = (EditText) findViewById(R.id.editText1);

        mliste = new ArrayList<Produit>();
        cliste = new ArrayList<Customer>();
        spinlist = new ArrayList<String>();

        bt1 = (Button) findViewById(R.id.button);
        searchView = (SearchView) findViewById(R.id.searchView);


        // rafraichissement des donnees GPS

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        gpsLocation = new LocationFilter(this, gpsLocation).getBestLocation();

        // Initialize the location fields
        if (gpsLocation != null) {
            Toast.makeText(OkFoodsActivity.this, "Provider gps has been selected.", Toast.LENGTH_LONG).show();
            onLocationChanged(gpsLocation);
        } else {
            Toast.makeText(OkFoodsActivity.this, "Position GPS non determinee", Toast.LENGTH_LONG).show();
        }

        if (isConnected()) new HttpAsyncTask().execute("" + SessionData.getInstance().getIduser());

        else
            Toast.makeText(OkFoodsActivity.this, "Veuillez vous connecter a internet.", Toast.LENGTH_LONG).show();

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

                StringBuffer responseText = new StringBuffer();

                ArrayList<Produit> produitsList = padater.produitList;
                String chaine = "";
                int q = 0;

                for (int i = 0; i < produitsList.size(); i++) {

                    Produit p = produitsList.get(i);

                    int idproduit = p.getId();
                    int idcustomer = idselect;
                    int ispresent = 0;

                    if (p.isSelected()) ispresent = 1;
                    else ispresent = 0;

                    String xlocation = "" + Latitude;
                    String ylocation = "" + Longitude;

                    if (q == 0)
                        chaine += "" + idcustomer + ";" + idproduit + ";" + ispresent + ";" + xlocation + ";" + ylocation;

                    else
                        chaine += "@@" + idcustomer + ";" + idproduit + ";" + ispresent + ";" + xlocation + ";" + ylocation;

                    q++;

                }

                if (currentuser.isEmpty()) {
                    msgBox.setMessage("Veuillez saisir le client");
                    msgBox.openSimplePane();

                } else if (Latitude == 0 || Longitude == 0) {
                    msgBox.setMessage("Le téléphone n'a pas pu obtenir votre position, veuillez reéssayer dans 10 secondes");
                    msgBox.openSimplePane();
                } else
                    new HttpAsyncTask1().execute(chaine);

                ///Toast.makeText(getApplicationContext(), "Operation terminee", Toast.LENGTH_LONG).show();

            }
        });

    }


    @Override
    public void onBackPressed() {

        super.onBackPressed(); //Normal behaviour

        Intent i2 = new Intent(OkFoodsActivity.this, HomeActivity.class);

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

    public String GET() {

        //HttpClient Client = new DefaultHttpClient();

        String lien = MainActivity.endpoint+"/rws-product?own_or_competitor=0";

        Log.e("LIEN",lien);

        try
        {
            String SetServerString = "";

            // Create Request to server and get response
            HttpRequest req = new HttpRequest(lien);
            SetServerString = req.prepare(HttpRequest.Method.GET).sendAndReadString();

            /*HttpGet httpget = new HttpGet(lien);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            SetServerString = Client.execute(httpget, responseHandler);*/

            Log.e("GETTED LIST ", SetServerString);
            // Show response on activity
            return SetServerString;
        }
        catch(Exception ex)
        {
            Log.e("GETTED EXCEPTION ", ex.getMessage());
            return null;
        }

    }

    public String GET(String key) {

        int id = Integer.parseInt(key);

        //HttpClient Client = new DefaultHttpClient();

        String lien = MainActivity.endpoint+"/rws-customer?merchant="+id;

        Log.e("LIEN",lien);

        try
        {
            String SetServerString = "";

            // Create Request to server and get response
            HttpRequest req = new HttpRequest(lien);
            SetServerString = req.prepare(HttpRequest.Method.GET).sendAndReadString();

            /*HttpGet httpget = new HttpGet(lien);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            SetServerString = Client.execute(httpget, responseHandler);*/

            // Show response on activity

            return SetServerString;
        }
        catch(Exception ex)
        {
            Log.e("GETTED EXCEPTION ", ex.getMessage());
            return null;
        }

    }


    public static String POST(String chaine) throws IOException, JSONException {

        String lien = MainActivity.endpoint+"/rws-product-presence/create";

        String[] chaitab = chaine.split("@@"); String retour = "";

        for(int k=0; k<chaitab.length; k++){

            String[] items = chaitab[k].split(";");

            String customer = items[0]; String product = items[1]; String present = items[2];

            String xlo = items[3]; String ylo = items[4];

            InputStream inputStream = null;
            String result = "";
            try {


                //HttpRequest req = new HttpRequest();
                // 1. create HttpClient
                HttpClient httpclient = new DefaultHttpClient();

                // 2. make POST request to the given URL
                HttpPost httpPost = new HttpPost(lien);

                String json = "";

                // 3. build jsonObject
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("customer", Integer.parseInt(customer));
                jsonObject.accumulate("product", Integer.parseInt(product));
                jsonObject.accumulate("is_present", Integer.parseInt(present));
                jsonObject.accumulate("created_by", SessionData.getInstance().getIduser());
                jsonObject.accumulate("created_on", xlo);
                jsonObject.accumulate("updated_on", ylo);

                json = "{\"customer\":\""+String.valueOf(Integer.parseInt(customer))+"\", \"product\":\""+String.valueOf(Integer.parseInt(product))+"\", \"is_present\":\""+String.valueOf(Integer.parseInt(present))+"\"," +
                        " \"created_by\":\""+String.valueOf(SessionData.getInstance().getIduser())+"\", \"created_on\":\""+String.valueOf(xlo)+"\", \"updated_on\":\""+String.valueOf(xlo)+"\"}";
                //result = req.sendJSON(lien, json);
                // ** Alternative way to convert Person object to JSON string usin Jackson Lib
                // ObjectMapper mapper = new ObjectMapper();
                // json = mapper.writeValueAsString(person);

                // 5. set json to StringEntity
                StringEntity se = new StringEntity(jsonObject.toString());

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
                Log.d("Voici le resultat", result);
                Log.d("InputStream", e.getMessage());
            }

            retour+=result;
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

            String re1 = GET();
            String re2 = GET(args[0]);
            Log.e("ARGUMENT 1", args[0]);
            Log.e("RESSOURCES = ", re1+"@@"+re2);
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
                        mliste.add(p);

                    }

                    padater = new ProduitListAdapter(OkFoodsActivity.this,R.layout.produit_list,mliste);

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

                        Customer co = new Customer(cu.getInt("id"),cu.getString("name"),cu.getString("xlocation"),cu.getString("ylocation"));

                        cliste.add(co); spinlist.add(cu.getString("name"));

                    }

                   // ArrayAdapter<String> adapter = new ArrayAdapter<String>(OkFoodsActivity.this, android.R.layout.simple_spinner_item, spinlist);

                    //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                   // spinner.setAdapter(adapter);

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

            pd = new ProgressDialog(OkFoodsActivity.this);

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
            Log.e("Final result", result);
            //if(result.equals("") && null!=result) {
            try {

                Object retour = new JSONTokener(result).nextValue();
                //JSONObject retour = new JSONObject(result);
                if (retour instanceof JSONObject) {

                    JSONObject obj = new JSONObject(result);

                    if (obj.has("message")) {

                        String ms1 = obj.getString("message");

                        msgBox.setMessage("Erreur serveur: " + ms1);
                        msgBox.openSimplePane();
                    }
                    else {
                        msgBox.setMessage("Enregistrement effectué avec succès");
                        msgBox.openSuccessPane();
                    }
                }

                //else if(retour instanceof JSONArray) {
                else {
                    JSONArray tab = new JSONArray(result);
                    String ms = "Erreur d'enregistrement : ";

                    for (int i = 0; i < tab.length(); i++) {

                        JSONObject v = tab.getJSONObject(i);
                        String vn = v.getString("message");

                        if (i == 0) ms += vn;
                        else ms += ", " + vn;
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

            pd = new ProgressDialog(OkFoodsActivity.this);

            pd.setMessage("Enregistrement en cours ...");

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
