package in.dsardy.choreoapp3;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BulksmsFragment.OnFragmentInteractionListener,ChatroomFragment.OnFragmentInteractionListener,IdeasFragment.OnFragmentInteractionListener,MapsFragment.OnFragmentInteractionListener{

    public Location mLastLocation = null;
    String mLastUpdateTime;
    private ProgressDialog progressDialog;
    private String locationText;
    SharedPreferences userPref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //splash
        setContentView(R.layout.activity_main);

        //incriment online people
        userPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        DatabaseReference referenceOnline = FirebaseDatabase.getInstance().getReference().child("online");
        if(userPref.getInt("sex",1)==1){
            DatabaseReference boys = referenceOnline.child("boys");

            boys.runTransaction(new Transaction.Handler() {

                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                    String bo = mutableData.getValue(String.class);
                    String boNew = ""+(Integer.parseInt(bo));
                    mutableData.setValue(boNew);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });
        }else {

            DatabaseReference boys = referenceOnline.child("girls");
            boys.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                    String go = mutableData.getValue(String.class);
                    String goNew = ""+(Integer.parseInt(go));
                    mutableData.setValue(goNew);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }
            });


        }




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        // initially go to map frag
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragments_container, new MapsFragment() ).commit();




    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_location) {
            // Handle the map action
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragments_container, new MapsFragment() ).commit();
        } else if (id == R.id.nav_chatroom) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragments_container, new ChatroomFragment() ).commit();

        } else if (id == R.id.nav_bulksms) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragments_container, new BulksmsFragment() ).commit();

        } else if (id == R.id.nav_ideas) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragments_container, new IdeasFragment() ).commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_info) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //location callbacks






    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
