package com.gvozditskiy.watermeter.activityNfragments;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.interfaces.SendErrorCallback;
import com.gvozditskiy.watermeter.interfaces.RegisterIntents;
import com.gvozditskiy.watermeter.interfaces.RegisterInterface;

public class EnterIndicationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RegisterInterface,
        RegisterIntents {

    private final static String TAG_INDFRAG = "EnterIndicFragment";
    private final static String TAG_STATFRAG = "StatisticsFragment";
    private final static String TAG_ABOUTFRAG = "AboutFragment";
    public static final int PERMISSION_REQ = 101;
    SendErrorCallback sendErrorCallback;
    FloatingActionButton fab;
    private int mItemId;
    boolean hasBundle;
    NavigationView navigationView;
    PendingIntent sentPendingIntent;
    PendingIntent deliveredPendingIntent;
    boolean hasPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_indications);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (hasPermission) {
//                    sendErrorCallback.onSend();
//                } else {
//                    Snackbar.make(view, "Нет разрешения на отправку сообщений", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
////
//            }
//        });
//        fab.hide();
//        fab.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState != null) {
            hasBundle = true;
            mItemId = savedInstanceState.getInt("id", 0);
        }

        registerBroadcastReciever();
        requestPermission();


    }

    private void registerBroadcastReciever() {
        String SMS_SENT = "SMS_SENT";
        String SMS_DELIVERED = "SMS_DELIVERED";

// For when the SMS has been sent
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        sendErrorCallback.onSend();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                        sendErrorCallback.onSend();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                        sendErrorCallback.onSend();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                        sendErrorCallback.onSend();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

// For when the SMS has been delivered
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        sendErrorCallback.onSend();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));
    }

    private void requestPermission() {
        //request permissions
        if (ActivityCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the mUser grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            }
            hasPermission = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQ);
        } else {
            hasPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ:
                if (grantResults.length <= 0) {
                    hasPermission = false;
                } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true;
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasBundle) {
            FragmentManager fm = getSupportFragmentManager();
            EneterIndicFragment fragment = new EneterIndicFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_enter_indications, fragment, TAG_INDFRAG);
            ft.commit();
            mItemId = 0;
            navigationView.setCheckedItem(R.id.nav_indications);

        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (mItemId == 0) {
                EneterIndicFragment fragment = (EneterIndicFragment) fm.findFragmentByTag(TAG_INDFRAG);
                if (fragment!=null) {
                    ft.replace(R.id.content_enter_indications, fragment, TAG_INDFRAG);
                    ft.commit();
//                    fab.show();
                    mItemId = 0;
                    navigationView.setCheckedItem(R.id.nav_indications);
                }
            } else if (mItemId == 1) {
                StatisticsFragment fragment = (StatisticsFragment) fm.findFragmentByTag(TAG_STATFRAG);
                if (fragment!=null) {
                    ft.replace(R.id.content_enter_indications, fragment, TAG_STATFRAG);
//                ft.addToBackStack(null);
                    ft.commit();
//                    fab.hide();
                    mItemId = 1;
                    navigationView.setCheckedItem(R.id.nav_statistics);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enter_indications, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_profile) {
            Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_statistics) {
            StatisticsFragment fragment = new StatisticsFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_enter_indications, fragment, TAG_STATFRAG);
            ft.addToBackStack(null);
            ft.commit();
//            fab.hide();
            mItemId = 1;
        } else if (id == R.id.nav_indications) {
            EneterIndicFragment fragment = new EneterIndicFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_enter_indications, fragment, TAG_INDFRAG);
            ft.addToBackStack(null);
            ft.commit();
//            fab.show();
            mItemId = 0;
        } else if (id == R.id.nav_info) {
            AboutDialogFragment fragment = new AboutDialogFragment();
            fragment.show(getSupportFragmentManager(), TAG_ABOUTFRAG);

        } else if (id == R.id.nav_send) {
            Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
            mailIntent.setData(Uri.parse("mailto:gvozditskiy@gmail.com"));
            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Счетчики воды. Минск");
            if (mailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mailIntent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("id", mItemId);
        Log.d("savestate", "ok");
    }

    @Override
    public void onRegisterInterface(SendErrorCallback sendErrorCallback) {
        this.sendErrorCallback = sendErrorCallback;
//        fab.show();
    }

    @Override
    public void onRegisterIntent(PendingIntent sentPendingIntent, PendingIntent deliveredPendingIntent) {
        this.sentPendingIntent = sentPendingIntent;
        this.deliveredPendingIntent = deliveredPendingIntent;
    }
}
