package com.example.bck.sensorsreader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.edgent.function.Supplier;

import java.util.Date;


public class Light extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener, Supplier<String> {

    private float ligthValue;
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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
        getMenuInflater().inflate(R.menu.home, menu);
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

        switch (id) {
            case R.id.nav_home:
                Intent homeIntent = new Intent(Light.this, Home.class);
                startActivity(homeIntent);
                break;
            case R.id.nav_accelerometer:
                Intent accelerometerIntent = new Intent(Light.this, Accelerometer.class);
                startActivity(accelerometerIntent);
                break;
            case R.id.nav_magnetometer:
                Intent magnetometerIntent = new Intent(Light.this, Magnetometer.class);
                startActivity(magnetometerIntent);
                break;
            case R.id.nav_light:
                Intent lightIntent = new Intent(Light.this, Light.class);
                startActivity(lightIntent);
                break;
            case R.id.nav_proximity:
                Intent proximityIntent = new Intent(Light.this, Proximity.class);
                startActivity(proximityIntent);
                break;
            case R.id.nav_config:
                Intent configIntent = new Intent(Light.this, Config.class);
                startActivity(configIntent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.ligthValue = event.values[0];
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            long date = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;
            StringBuffer buffer = new StringBuffer();
            buffer.append(new Date(date))
                    .append("\n")
                    .append("value: ")
                    .append(event.values[0])
                    .append("\n");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public String get() {
        return Float.toString(ligthValue);
    }

    //Service bindings
    MqttSensorServiceCustom mService;
    boolean mBound = false;


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MqttSensorServiceCustom.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MqttSensorServiceCustom.LocalBinder binder = (MqttSensorServiceCustom.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    //CustomConfiguration
    public void createNewConfiguration(View view) {
        CustomButtonConfigurationPopup customButtonConfigurationPopup = new CustomButtonConfigurationPopup(this, view, this::customButtonClickListener);
        id++;
        customButtonConfigurationPopup.showWindow(id);
    }


    public void customButtonClickListener(MqttConfButton mqttConfButton) {
        mService.handleCustomJob(mqttConfButton);
        Toast.makeText(this, "Sent last " + mqttConfButton.getSeconds() + " seconds to server", Toast.LENGTH_SHORT).show();
    }
}

