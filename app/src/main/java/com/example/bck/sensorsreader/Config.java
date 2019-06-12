package com.example.bck.sensorsreader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bck.sensorsreader.configuration.ApplicationConfig;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Config extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private ApplicationConfig applicationConfig =  new ApplicationConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_config);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private ApplicationConfig readSavedConfig() {
        //todo read from file or other storage if possible, create new if not.
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.lightConfig.mqttFrequency = 9;
        return applicationConfig;
    }



    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MqttSensorService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void setConfig(ApplicationConfig config) {
        Spinner lightSpinner = findViewById(R.id.lightUnits);
        Spinner accSpinner = findViewById(R.id.accelerometerUnits);
        Spinner magnetSpinner = findViewById(R.id.magnetometerUnits);
        Spinner proxSpinner = findViewById(R.id.proximityUnits);

        ArrayAdapter<TimeUnit> timeUnits = new ArrayAdapter<TimeUnit>(this, android.R.layout.simple_spinner_item,
                Arrays.copyOfRange(TimeUnit.values(), 2, 6));

        lightSpinner.setAdapter(timeUnits);
        accSpinner.setAdapter(timeUnits);
        proxSpinner.setAdapter(timeUnits);
        magnetSpinner.setAdapter(timeUnits);

        lightSpinner.setSelection(config.lightConfig.mqttFrequencyUnit.ordinal() - 2);
        accSpinner.setSelection(config.accelerometerConfig.mqttFrequencyUnit.ordinal() - 2);
        proxSpinner.setSelection(config.proximityConfig.mqttFrequencyUnit.ordinal() - 2);
        magnetSpinner.setSelection(config.magnetometerConfig.mqttFrequencyUnit.ordinal() - 2);


        ((TextView ) findViewById(R.id.accelerometerFreqValue)).setText(Long.toString(config.accelerometerConfig.mqttFrequency));
        ((TextView ) findViewById(R.id.magnetometerFreqValue)).setText(Long.toString(config.magnetometerConfig.mqttFrequency));
        ((TextView ) findViewById(R.id.proximityFreqValue)).setText(Long.toString(config.proximityConfig.mqttFrequency));
        ((TextView ) findViewById(R.id.lightFreqValue)).setText(Long.toString(config.lightConfig.mqttFrequency));

        ((Switch ) findViewById(R.id.accelerometerMqttActive)).setChecked(config.accelerometerConfig.mqttActive);
        ((Switch ) findViewById(R.id.magnetometerMqttActive)).setChecked(config.magnetometerConfig.mqttActive);
        ((Switch ) findViewById(R.id.proximityMqttActive)).setChecked(config.proximityConfig.mqttActive);
        ((Switch ) findViewById(R.id.lightMqttActive)).setChecked(config.lightConfig.mqttActive);

        ((Switch ) findViewById(R.id.accelerometerActive)).setChecked(config.accelerometerConfig.active);
        ((Switch ) findViewById(R.id.magnetometerActive)).setChecked(config.magnetometerConfig.active);
        ((Switch ) findViewById(R.id.proximityActive)).setChecked(config.proximityConfig.active);
        ((Switch ) findViewById(R.id.lightActive)).setChecked(config.lightConfig.active);

        ((TextView ) findViewById(R.id.accelerometerMin)).setText(Double.toString(config.accelerometerConfig.minValue));
        ((TextView ) findViewById(R.id.magnetometerMin)).setText(Double.toString(config.magnetometerConfig.minValue));
        ((TextView ) findViewById(R.id.proximityMin)).setText(Double.toString(config.proximityConfig.minValue));
        ((TextView ) findViewById(R.id.lightMin)).setText(Double.toString(config.lightConfig.minValue));

        ((TextView ) findViewById(R.id.accelerometerMax)).setText(Double.toString(config.accelerometerConfig.maxValue));
        ((TextView ) findViewById(R.id.magnetometerMax)).setText(Double.toString(config.magnetometerConfig.maxValue));
        ((TextView ) findViewById(R.id.proximityMax)).setText(Double.toString(config.proximityConfig.maxValue));
        ((TextView ) findViewById(R.id.lightMax)).setText(Double.toString(config.lightConfig.maxValue));
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



    public void SaveConfig(View but){
        Spinner accUnits = findViewById(R.id.accelerometerUnits);
        Spinner magUnits = findViewById(R.id.magnetometerUnits);
        Spinner proUnits = findViewById(R.id.proximityUnits);
        Spinner ligUnits = findViewById(R.id.lightUnits);
        
        TextView accFreq = findViewById(R.id.accelerometerFreqValue);
        TextView magFreq = findViewById(R.id.magnetometerFreqValue);
        TextView proFreq = findViewById(R.id.proximityFreqValue);
        TextView ligFreq = findViewById(R.id.lightFreqValue);
        
        Switch accActive = findViewById(R.id.accelerometerActive);
        Switch magActive = findViewById(R.id.magnetometerActive);
        Switch proActive = findViewById(R.id.proximityActive);
        Switch ligActive = findViewById(R.id.lightActive);
        
        TextView accMin = findViewById(R.id.accelerometerMin);
        TextView magMin = findViewById(R.id.magnetometerMin);
        TextView proMin = findViewById(R.id.proximityMin);
        TextView ligMin = findViewById(R.id.lightMin);
        
        TextView accMax = findViewById(R.id.accelerometerMax);
        TextView magMax = findViewById(R.id.magnetometerMax);
        TextView proMax = findViewById(R.id.proximityMax);
        TextView ligMax = findViewById(R.id.lightMax);
        
        Switch accMqttActive = findViewById(R.id.accelerometerMqttActive);
        Switch magMqttActive = findViewById(R.id.magnetometerMqttActive);
        Switch proMqttActive = findViewById(R.id.proximityMqttActive);
        Switch ligMqttActive = findViewById(R.id.lightMqttActive);
        
        applicationConfig.lightConfig.readValuesFromViews(ligActive,ligMqttActive, ligFreq, ligUnits, ligMin, ligMax);
        applicationConfig.accelerometerConfig.readValuesFromViews(accActive,accMqttActive, accFreq, accUnits, accMin, accMax);
        applicationConfig.magnetometerConfig.readValuesFromViews(magActive,magMqttActive, magFreq, magUnits, magMin, magMax);
        applicationConfig.proximityConfig.readValuesFromViews(proActive,proMqttActive, proFreq, proUnits, proMin, proMax);

        mService.changeConfig(applicationConfig);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                Intent homeIntent = new Intent(Config.this, Home.class);
                startActivity(homeIntent);
                break;
            case R.id.nav_accelerometer:
                Intent accelerometerIntent = new Intent(Config.this, Accelerometer.class);
                startActivity(accelerometerIntent);
                break;
            case R.id.nav_magnetometer:
                Intent magnetometerIntent = new Intent(Config.this, Magnetometer.class);
                startActivity(magnetometerIntent);
                break;
            case R.id.nav_light:
                Intent lightIntent = new Intent(Config.this, Light.class);
                startActivity(lightIntent);
                break;
            case R.id.nav_proximity:
                Intent proximityIntent = new Intent(Config.this, Proximity.class);
                startActivity(proximityIntent);
                break;
            case R.id.nav_config:
                Intent configIntent = new Intent(Config.this, Config.class);
                startActivity(configIntent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    //Service bindings
    MqttSensorService mService;
    boolean mBound = false;


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
            MqttSensorService.LocalBinder binder = (MqttSensorService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            setConfig(mService.getConfig());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
