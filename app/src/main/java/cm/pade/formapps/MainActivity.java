package cm.pade.formapps;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import cm.pade.formapps.parser.HttpRequest;
import cm.pade.formapps.parser.LocationFilter;
import cm.pade.formapps.parser.OptionPanel;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private EditText nom, adresse, email, phone, lon, lat;
    private Button valider, vider, capture;
    private double Latitude = 0, Longitude = 0, PLatitude = 0, PLongitude = 0;
    String responseServer = "";
    Form form;
    private ProgressDialog pd;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private final int PERDIODE = 1000;

    //public static final String endpoint = "http://vps321700.ovh.net/salesmgt_test/web";
    public static final String endpoint = "http://vps321700.ovh.net/salesmgt/web";

    private String jsonURL = MainActivity.endpoint + "/rws-customer-type";
    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;
    private ArrayList<CategorieUser> modelDataArrayList;
    private ArrayList<String> names = new ArrayList<String>();
    private Spinner spinType;

    private LocationManager locationManager;
    private LocationFilter locationFilter;
    private Location gpsLocation;

    private String provider;

    //---MessageBox component
    private OptionPanel msgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Request query = new Request(MainActivity.this);
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
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbar.setTitle("Nouveau point de vente");
        setTitle("Nouveau point de vente");
        if(toolbarText!=null && toolbar!=null) {
            toolbarText.setText("Nouveau point de vente");
            setSupportActionBar(toolbar);
        }
        //setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(0xFFFFFFFF);
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


        nom = (EditText) findViewById(R.id.editText1);
        adresse = (EditText) findViewById(R.id.editText2);
        email = (EditText) findViewById(R.id.editText4);
        phone = (EditText) findViewById(R.id.editText3);
        spinType = (Spinner) findViewById(R.id.spinTypeCusto);
        valider = (Button) findViewById(R.id.button);
        lon = (EditText) findViewById(R.id.editText6);
        lat = (EditText) findViewById(R.id.editText5);
        vider = (Button) findViewById(R.id.button2);

        modelDataArrayList = new ArrayList<CategorieUser>();
        names = new ArrayList<String>();

        //Loading data in customerType spinner
        loadJSON();

        // rafraichissement des donnees GPS
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();

        locationFilter = new LocationFilter(this, gpsLocation);
        gpsLocation = locationFilter.getBestLocation();

        // Initialize the location fields
        if (gpsLocation != null) {
            Toast.makeText(MainActivity.this, "Provider gps has been selected.", Toast.LENGTH_LONG).show();
            onLocationChanged(gpsLocation);
        } else {
            Toast.makeText(MainActivity.this, "Position GPS non determinee", Toast.LENGTH_LONG).show();
        }

        vider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nom.setText("");
                adresse.setText("");
                email.setText("");
                phone.setText("");
                spinType.setSelection(0);
            }
        });

        //-----
        this.msgBox = new OptionPanel(this);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nom.getText().toString();
                String adress = adresse.getText().toString();
                String emai = email.getText().toString();
                String fone = phone.getText().toString();
                String type = spinType.getSelectedItem().toString();
                String la = lat.getText().toString();
                String lo = lon.getText().toString();

                String userid = "" + SessionData.getInstance().getIduser();
                int typev = 0;

                for (int l = 0; l < names.size(); l++) {

                    if (names.get(l).equals(type)) typev = modelDataArrayList.get(l).getId();
                }

                //Toast.makeText(getApplicationContext(), "Categorie : " + type, Toast.LENGTH_LONG).show();

                if (name.isEmpty() || name.length()>50) {
                    msgBox.setMessage("Le nom ne peut etre vide et doit contenir 50 caracteres maximum");
                    msgBox.openSimplePane();

                } else if (adress.isEmpty() || adress.length()>50) {
                    msgBox.setMessage("L'adresse ne peut etre vide et doit contenir 50 caracteres maximum");
                    msgBox.openSimplePane();

                } else if (fone.isEmpty() || fone.length()>9) {
                    msgBox.setMessage("Le numero de telephone ne peut etre vide et doit contenir 9 caracteres maximum");
                    msgBox.openSimplePane();
                }
                else if(!fone.startsWith("69") && !fone.startsWith("68") && !fone.startsWith("67") && !fone.startsWith("66") && !fone.startsWith("65") && !fone.startsWith("64") && !fone.startsWith("233")){
                    msgBox.setMessage("Le numero de telephone ne correspond pas au format local GSM");
                    msgBox.openSimplePane();
                }
                else if (la.isEmpty() || lo.isEmpty()) {
                    msgBox.setMessage("Le téléphone n'a pas pu obtenir votre position. Veuillez reéssayer dans 10 secondes");
                    msgBox.openSimplePane();
                }
                else if (la.equals("0") || lo.equals("0")) {
                    msgBox.setMessage("Le téléphone n'a pas pu obtenir votre position. Veuillez reéssayer dans 10 secondes");
                    msgBox.openSimplePane();
                }
                else if (la == null || lo == null) {
                    msgBox.setMessage("Le téléphone n'a pas pu obtenir votre position. Veuillez reéssayer dans 10 secondes");
                    msgBox.openSimplePane();
                }
                else if (typev == 0) {
                    msgBox.setMessage("Veuillez choisir un type de client");
                    msgBox.openSimplePane();
                }
                else if (!isConnected()) {

                    Point p = new Point();

                    p.setNom(name);
                    p.setAdresse(adress);
                    p.setPhone(fone);
                    p.setEmail(emai);
                    p.setType(typev);

                    p.setXlocation(la);
                    p.setYlocation(lo);

                    final Request query = new Request(MainActivity.this);

                    query.open();

                    query.insertPoint(p);

                    query.close();

                    Log.e("Choix", "" + spinType);
                    msgBox.setMessage("Impossible de se connecter au serveur, les donnees ont été enregistrées localement");
                    msgBox.openSimplePane();
                }
                else {

                    String mchoix = "" + typev;

                    if (SessionData.getInstance().getLatdata() == null && SessionData.getInstance().getLongdata() == null) {

                        new HttpAsyncTask().execute(name, adress, emai, fone, la, lo, mchoix);
                    } else {

                        if (SessionData.getInstance().getLongdata().equals(lo) && SessionData.getInstance().getLatdata().equals(la)) {
                            msgBox.setMessage("Les coordonnees GPS sont les memes que pour le point precedent, êtes-vous au meme endroit?");
                            msgBox.openSimplePane();

                        } else {

                            new HttpAsyncTask().execute(name, adress, emai, fone, la, lo, mchoix);
                        }

                    }

                }
            }
        });
    }

    //---------------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    private void loadJSON() {

        showSimpleProgressDialog(this, "", "Chargement des types de clients", false);

        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void[] params) {
                String response = "";
                HashMap<String, String> map = new HashMap<>();
                try {
                    HttpRequest req = new HttpRequest(jsonURL);
                    response = req.prepare(HttpRequest.Method.GET).sendAndReadString();
                } catch (Exception e) {
                    response = e.getMessage();
                }
                return response;
            }

            protected void onPostExecute(String result) {
                //do something with response
                onTaskCompleted(result, jsoncode);
            }
        }.execute();
    }

    public void onTaskCompleted(String response, int serviceCode) {
        Log.d("responsejson", response.toString());
        switch (serviceCode) {
            case jsoncode:
               // if (isSuccess(response)) {
                    removeSimpleProgressDialog();  //will remove progress dialog

                    modelDataArrayList = parseInfo(response);
                    // Application of the Array to the Spinner

                    for (int i = 0; i < modelDataArrayList.size(); i++) {
                        names.add(modelDataArrayList.get(i).getName().toString());
                    }

                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    spinType.setAdapter(spinnerArrayAdapter);

        }
    }

    public ArrayList<CategorieUser> parseInfo(String response) {
        ArrayList<CategorieUser> userModelArrayList = new ArrayList<>();
        try {
            //JSONObject jsonObject = new JSONObject(response);
            //if (jsonObject.getString("status").equals("true")) {

            //JSONArray dataArray = jsonObject.getJSONArray("data");
            JSONArray dataArray = new JSONArray(response);

            for (int i = 0; i < dataArray.length(); i++) {

                CategorieUser userModel = new CategorieUser();
                JSONObject dataobj = dataArray.getJSONObject(i);
                userModel.setId(dataobj.getInt("id"));
                userModel.setCode(dataobj.getString("code"));
                userModel.setName(dataobj.getString("name"));
                userModelArrayList.add(userModel);

            }
            //}

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userModelArrayList;
    }

    public boolean isSuccess(String response) {

        try {
            JSONArray dataArray = new JSONArray(response);
            if (dataArray.length() > 0) {
                return true;
            } else {
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //-------------------------------------

    @Override
    public void onBackPressed() {

        super.onBackPressed(); //Normal behaviour

        Intent i2 = new Intent(MainActivity.this, HomeActivity.class);

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

        /*if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {

        Latitude = location.getLatitude();
        lat.setText(String.valueOf(location.getLatitude()));
        Longitude = location.getLongitude();
        lon.setText(String.valueOf(location.getLongitude()));
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
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onProviderDisabled(String provider) {

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Veuillez activer le GPS s'il vous plait ",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public static String POST(String lien, Form form) throws IOException, JSONException {

        InputStream inputStream = null;
        String result = "";
        try {

            HttpRequest req = new HttpRequest();
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

            json = jsonObject.toString();
            Log.e("DATA SENT", json);

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
            result = EntityUtils.toString(httpEntity, "UTF-8");

        } catch (Exception e) {
            Log.d("InputStream", e.getCause().getMessage());
        }
        // 11. return result
        Log.e("Resulat du post: ", result);
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            try {

                form = new Form();
                form.setName(args[0]);
                form.setAdresse(args[1]);
                form.setEmail(args[2]);
                form.setPhone(args[3]);
                form.setLatitude(args[4]);
                form.setLongitude(args[5]);
                form.setType(args[6]);

                return POST(endpoint+"/shops",form);
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

                //Object retour = new JSONObject(result);
                //JSONObject retour = new JSONObject(result);
                Object retour = new JSONTokener(result).nextValue();

                if(retour instanceof JSONObject) {

                    JSONObject obj = new JSONObject(result);

                    if(obj.has("message")) {

                        String ms1 = obj.getString("message");
                        msgBox.setMessage("Erreur serveur: "+ ms1);
                        msgBox.openSimplePane();
                    }
                    else {
                        msgBox.setMessage("Enregistrement effectué avec succès");
                        msgBox.openSuccessPane();
                    }
                }
                //else if(retour instanceof JSONArray) {
                else{
                    JSONArray tab = new JSONArray(result);
                    String ms = "Erreur d'enregistrement : ";

                    for(int i = 0; i<tab.length();i++){

                        JSONObject v = tab.getJSONObject(i);
                        String vn = v.getString("message");

                        if(i == 0) ms+=vn; else ms+=", "+vn;
                    }
                    msgBox.setMessage(ms);
                    msgBox.openSimplePane();
                }

            } catch (JSONException e) {
                Log.d("RESULTAT NEW ", result);
                msgBox.setMessage("Anomalie système: " + result + "\n " + e.getLocalizedMessage());
                msgBox.openSimplePane();
            }
        }

        @Override
        protected void onPreExecute() {

            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(MainActivity.this);

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

}
