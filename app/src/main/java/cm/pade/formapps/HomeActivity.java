package cm.pade.formapps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Sumbang on 22/09/2016.
 */

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Request query = new Request(HomeActivity.this);

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

            Intent i = new Intent(HomeActivity.this,LoginActivity.class);

            startActivity(i);

        }

        setContentView(R.layout.activity_home);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Bienvenue "+SessionData.getInstance().getUsername());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        if(toolbarText!=null && toolbar!=null) {
            toolbarText.setText("Bienvenue "+SessionData.getInstance().getUsername());
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerList = (ListView)findViewById(R.id.left_drawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)

                getSupportActionBar().setTitle("Menu");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed

                getSupportActionBar().setTitle("Bienvenue "+SessionData.getInstance().getUsername());
            }
        };

        // Drawer Toggle Object Made
        drawerLayout.setDrawerListener(drawerToggle); // Drawer Listener set to the Drawer toggle
        // drawerToggle.syncState();

        final MenuItems[] drawerItem = new MenuItems[13];

        drawerItem[0] = new MenuItems(0, "Accueil");
        drawerItem[1] = new MenuItems(0, "Geolocalisation");
        drawerItem[2] = new MenuItems(0, "Synchronisation");
        drawerItem[3] = new MenuItems(0, "Nos produits");
        drawerItem[4] = new MenuItems(0, "Produits concurrent");
        drawerItem[5] = new MenuItems(0, "Prospection");
        drawerItem[6] = new MenuItems(0, "Commande");
        drawerItem[7] = new MenuItems(0, "Sortie Distributeur");
        drawerItem[8] = new MenuItems(0, "Livraison");
        drawerItem[9] = new MenuItems(0, "Recouvrement client");
        drawerItem[10] = new MenuItems(0, "Recouvrement distributeur");
        drawerItem[11] = new MenuItems(0, "Inventaire");
        drawerItem[12] = new MenuItems(0, "Déconnexion");

        MenuItemAdapter adapter1 = new MenuItemAdapter(this,R.layout.drawer_list_item,drawerItem);

        drawerList.setAdapter(adapter1);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                if(position == 0){
                }
                else if(position == 1){
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,MainActivity.class);
                    startActivity(i);
                }
                else if(position == 2){
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,SynchroActivity.class);
                    startActivity(i);
                }
                else if(position == 3){
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,OkFoodsActivity.class);
                    startActivity(i);
                }
                else if(position == 4){
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,NokFoodActivity.class);
                    startActivity(i);
                }
                else if(position == 5){
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,ProspectionActivity.class);
                    startActivity(i);
                }
                else if(position == 6){
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,CmdActivity.class);
                    startActivity(i);
                }
                else if(position == 7){
                    if (!SessionData.getInstance().getRole().equals("wholesaler")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,DistributeurActivity.class);
                    startActivity(i);
                }
                else if(position == 8){
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas acces a ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,LivraisonActivity.class);
                    startActivity(i);
                }
                else if(position == 9){
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,RecouvrementActivity.class);
                    startActivity(i);
                }
                else if(position == 10){
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,Recouvrement2Activity.class);
                    startActivity(i);
                }
                else if(position == 11){
                    if (!SessionData.getInstance().getRole().equals("regional_responsible")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent i = new Intent(HomeActivity.this,ActivityInventaire.class);
                    startActivity(i);
                }
                else {
                    SessionData.getInstance().setStatus(0);

                    final Request r =  new Request(HomeActivity.this);
                    r.open(); r.RemoveUser(SessionData.getInstance().getIduser());
                    r.close();
                    Intent i = new Intent(HomeActivity.this,LoginActivity.class);
                    startActivity(i);
                }

            }

        });

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new GridAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if(position==0){
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,MainActivity.class);
                    startActivity(intent);
                }

                else if(position==1) {
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,UpdateActivity.class); startActivity(intent);
                }

                else if(position==2) {
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,SynchroActivity.class); startActivity(intent);
                }

                else if(position==3) {
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,OkFoodsActivity.class); startActivity(intent);
                }

                else if(position==4) {
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,NokFoodActivity.class); startActivity(intent);
                }

                else if(position==5) {
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,ProspectionActivity.class); startActivity(intent);
                }

                else if(position==6) {
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,CmdActivity.class); startActivity(intent);
                }

                else if(position==7) {
                    if (!SessionData.getInstance().getRole().equals("wholesaler")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,DistributeurActivity.class); startActivity(intent);
                }

                else if(position==8) {
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,LivraisonActivity.class); startActivity(intent);
                }

                else if(position==9) {
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,RecouvrementActivity.class); startActivity(intent);
                }

                else if(position==10) {
                    if (!SessionData.getInstance().getRole().equals("merchant")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,Recouvrement2Activity.class); startActivity(intent);
                }

                else if(position==11) {
                    if (!SessionData.getInstance().getRole().equals("regional_responsible")) {
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Désole, votre profil n'a pas accès à ce module", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        return;
                    }
                    Intent intent=new Intent(HomeActivity.this,ActivityInventaire.class); startActivity(intent);
                }

            }
        });

    }


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


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {

            Intent i2 = new Intent(HomeActivity.this,AboutActivity.class);

            i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i2);

            return true;
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
