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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cm.pade.formapps.parser.OptionPanel;


/**
 * Created by Sumbang on 21/09/2016.
 */
public class SynchroActivity extends AppCompatActivity {

    private ProgressDialog pd;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private ListView liste;
    private TextView txt;
    private Button bt1,bt2;
    private int elt = 0;

    //---MessageBox component
    private OptionPanel msgBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Request query = new Request(SynchroActivity.this);

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

            Intent i = new Intent(SynchroActivity.this,LoginActivity.class);

            startActivity(i);

        }

        /*if(!SessionData.getInstance().getRole().equals("merchant")) {

            Toast.makeText(SynchroActivity.this, "Desole, votre profil n'a pas acces a ce module", Toast.LENGTH_LONG).show();

            Intent i = new Intent(SynchroActivity.this,HomeActivity.class);

            startActivity(i);
        }*/

        setContentView(R.layout.activity_synchro);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbar.setTitle("Synchronisation");
        setTitle("Synchronisation");
        if(toolbarText!=null && toolbar!=null) {
            toolbarText.setText("Synchronisation");
            setSupportActionBar(toolbar);
        }
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                i.putExtra("current",1);
                startActivity(i);
            }
        });

        liste = (ListView)findViewById(R.id.content); txt = (TextView)findViewById(R.id.text);

        query.open();

        List<Point> pts = query.getAllPoint();

        query.close();

        if(pts != null) { elt = 1;

            liste.setVisibility(View.VISIBLE); txt.setVisibility(View.GONE);

            Iterator tab=pts.iterator();

            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

            while(tab.hasNext()){

                Point current = new Point(); current=(Point) tab.next();

                //On d�clare la HashMap qui contiendra les informations pour un item
                HashMap<String, String> map;

                map = new HashMap<String, String>(); String et="";

                map.put("ptid", ""+1); map.put("ptname", current.getNom());

                listItem.add(map);

            }

            SimpleAdapter mSchedule = new SimpleAdapter (this.getBaseContext(), listItem, R.layout.point_item_list,
                    new String[] {"ptid", "ptname"}, new int[] {R.id.ptid, R.id.ptname});

            liste.setAdapter(mSchedule);

            liste.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @SuppressWarnings("unchecked")
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                    HashMap<String, String> map1 = (HashMap<String, String>) liste.getItemAtPosition(position);

                    Intent intent=new Intent(SynchroActivity.this,EditActivity.class);

                    Bundle objetbundle=new Bundle();

                    objetbundle.putString("key",""+map1.get("ptid"));

                    intent.putExtras(objetbundle);

                    startActivity(intent);

                }
            });
        }

        else {

            liste.setVisibility(View.GONE); txt.setVisibility(View.VISIBLE);
        }

        bt1 = (Button)findViewById(R.id.button); //bt2 = (Button)findViewById(R.id.button2);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              if(!isConnected()) Toast.makeText(SynchroActivity.this,"Veuillez vous connecter a internet pour synchroniser",Toast.LENGTH_LONG).show();

              else {

                  if (elt == 1) new HttpAsyncTask().execute();

                  else Toast.makeText(SynchroActivity.this, "Aucun element a synchroniser", Toast.LENGTH_LONG).show();
              }

            }
        });

        msgBox = new OptionPanel(this);

    }

    @Override
    public void onBackPressed(){

        super.onBackPressed(); //Normal behaviour

        Intent i2 = new Intent(SynchroActivity.this,HomeActivity.class);

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

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
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

    public String POST(String lien) throws IOException, JSONException {

        InputStream inputStream = null;

        String result = ""; int op = 0; int total = 0;

        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(lien);

            String json = "";

            final Request q = new Request(SynchroActivity.this);

            q.open();

            List<Point> pts = q.getAllPoint();

            q.close(); total = pts.size();

            if(pts != null) {

                Iterator tab=pts.iterator();

                ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

                while(tab.hasNext()){

                    Point form = new Point(); form =(Point) tab.next();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("name", form.getNom());
                    jsonObject.accumulate("address", form.getAdresse());
                    jsonObject.accumulate("phone_number", form.getPhone());
                    jsonObject.accumulate("email_address", form.getEmail());
                    jsonObject.accumulate("xlocation", form.getXlocation());
                    jsonObject.accumulate("ylocation", form.getYlocation());
                    jsonObject.accumulate("customer_type", form.getType());
                    jsonObject.accumulate("created_by", SessionData.getInstance().getIduser());
                    jsonObject.accumulate("update_by", SessionData.getInstance().getIduser());

                    json = jsonObject.toString();

                    StringEntity se = new StringEntity(json);

                    httpPost.setEntity(se);

                    // 7. Set some headers to inform server about the type of the content
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");

                    // 8. Execute POST request to the given URL
                    HttpResponse httpResponse = httpclient.execute(httpPost);

                    // 9. receive response as inputStream
                    inputStream = httpResponse.getEntity().getContent();

                    // 10. convert inputstream to string
                    if(inputStream != null) {

                        result = convertInputStreamToString(inputStream);

                        Object retour = new JSONTokener(result).nextValue();

                        if(retour instanceof JSONObject) {

                            JSONObject obj = new JSONObject(result);

                            String name = obj.getString("name");

                            if(name.equals("Bad Request")){

                            } else {

                                q.open();

                                q.RemovePoint(form.getId());

                                q.close();

                                op++;
                            }

                        }

                    }

                }
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }


        if(op == total) return "ok";

        else return "nok";

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {

            try {
                return POST(MainActivity.endpoint+"/shops");
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

            if(result == "ok") {

                msgBox.setMessage("Synchronisation terminée avec succès");
                msgBox.openSuccessPane();

                /*Intent i2 = new Intent(getApplicationContext(),SynchroActivity.class);

                i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getBaseContext().startActivity(i2);*/

            }

            else{
                msgBox.setMessage("Synchronisation échouée! Essayer de synchroniser point par point");
                msgBox.openSimplePane();

            }

        }

        @Override
        protected void onPreExecute() {

            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog

            Log.i("TAG", "Recherche des infos");

            pd = new ProgressDialog(SynchroActivity.this);

            pd.setMessage("Envoi des informations");

            pd.setCancelable(false);

            pd.setIndeterminate(true);

            pd.show();

        }
    }
}
