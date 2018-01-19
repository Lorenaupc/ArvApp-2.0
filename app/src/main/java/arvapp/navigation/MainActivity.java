package arvapp.navigation;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.support.v4.app.FragmentTransaction;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.nav_mode) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_flockMode) {
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, new FlockFragment()).commit();
                }

                if (menuItem.getItemId() == R.id.nav_help) {

                }

                if (menuItem.getItemId() == R.id.nav_call) {

                }

                return false;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

        listeners();
    }

    public void listeners() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(R.drawable.boto_112)
                        .setTitle("Emergency Services Alert")
                        .setMessage("Proceed calling 112 for an emergency?")
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                callForHelp(v);
                            }
                        })
                        .show();
            }
        });
    }

    private void callForHelp(final View v) {
        SmsManager smsManager = SmsManager.getDefault();
        //smsManager.sendTextMessage("112", null,
        // "DVA Automated message! A DVA device has detected an emergency in Latitude: "
        // + String.valueOf(ourArvaLatitude) + " longitude: " +
        // String.valueOf(ourArvaLongitude), null, null);
        //Toast.makeText(this, "Sending message to 112", Toast.LENGTH_LONG).show();
        final Snackbar snack = Snackbar.make(v, "Sending message to 112", Snackbar.LENGTH_LONG);
        snack.setActionTextColor(Color.CYAN);
        snack.setAction("CLOSE", new View.OnClickListener(){
            @Override
            public void onClick(View view){
                snack.dismiss();
            }
        });
        snack.show();
    }

}