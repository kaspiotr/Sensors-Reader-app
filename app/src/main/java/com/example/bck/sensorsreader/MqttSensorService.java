package com.example.bck.sensorsreader;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.bck.sensorsreader.configuration.ApplicationConfig;
import com.example.bck.sensorsreader.configuration.SensorConfig;

import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.connectors.mqtt.MqttStreams;
import org.apache.edgent.execution.Job;
import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.providers.direct.DirectTopology;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MqttSensorService extends Service {

    private Future<Job> mqttJob = null;
    private EffectivelyFinalBooleanBox marker5s = new EffectivelyFinalBooleanBox(false);
    private EffectivelyFinalBooleanBox flagManual = new EffectivelyFinalBooleanBox(false);
    private ApplicationConfig config;
    private LightSensor lightSensor;
    private AccelerometerSensor accelerometerSensor;
    private MagnetometerSensor magnetometerSensor;
    private ProximitySensor proximitySensor;


    @Override
    public void onDestroy() {
        stopJob();
        Log.i("Mqtt", "Destroyed service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lightSensor = new LightSensor(this);
        magnetometerSensor = new MagnetometerSensor(this);
        accelerometerSensor = new AccelerometerSensor(this);
        this.config = readConfig();

        Log.i("Mqtt", "started service");
        startJob();
        return START_STICKY;
    }

    private ApplicationConfig readConfig() {

        try {
            FileInputStream fis = getApplicationContext().openFileInput("config.cfg");
            ObjectInputStream is = new ObjectInputStream(fis);
            ApplicationConfig config = (ApplicationConfig) is.readObject();
            is.close();
            fis.close();
            return config;
        } catch (IOException | ClassNotFoundException e) {
            Log.w("Config", "Error reading config file faling back to default");
            return ApplicationConfig.defaultConfig;
        }
    }


    private void stopJob() {
        try {
            mqttJob.get().stateChange(Job.Action.CLOSE);
        } catch (InterruptedException | ExecutionException | NullPointerException ignored) {
        }
    }

    private void startJob() {
        try {
            startNewJob();
        } catch (IOException e) {
            Log.e("Mqtt", "properties read failure", e);
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Mqtt", "Job get fail", e);
        }

    }

    private void startNewJob() throws IOException, ExecutionException, InterruptedException {

        if (this.mqttJob != null && this.mqttJob.get().getCurrentState() == Job.State.RUNNING) {
            Log.i("Mqtt", "Edgent is already running.");
            return;
        }

        Log.i("Mqtt", "No Edgent job is running. Starting new Edgent job.");

        DirectProvider dp = new DirectProvider();

        Properties props = new Properties();
        props.load(getBaseContext().getAssets().open("mqtt.properties"));

        DirectTopology topology = recreateContinuousStreams(dp, props);

        this.mqttJob = dp.submit(topology);
    }

    private DirectTopology recreateContinuousStreams(DirectProvider dp, Properties props) {
        DirectTopology topology = dp.newTopology();

        TStream<String> accStream = accelerometerStream(topology);
        TStream<String> lightStream = lightStream(topology);
        TStream<String> magStream = magnetometerStream(topology);

        MqttConfig mqttConfig = MqttConfig.fromProperties(props);
        MqttStreams mqqtStreams = new MqttStreams(topology, () -> mqttConfig);

        mqqtStreams.publish(accStream, props.getProperty("mqtt.accTopic"), 0, false);
        mqqtStreams.publish(lightStream, props.getProperty("mqtt.lightTopic"), 0, false);
        mqqtStreams.publish(magStream, props.getProperty("mqtt.magTopic"), 0, false);

        return topology;
    }

    private boolean checkIfValid(float[] x, SensorConfig config) {
        return config.mqttActive &&
                (x[0] > config.minValue || x[0] < config.maxValue ||
                        x[1] > config.minValue || x[1] < config.maxValue ||
                        x[2] > config.minValue || x[2] < config.maxValue);
    }

    private boolean checkIfValid(float x, SensorConfig config) {
        return config.mqttActive &&
                (x > config.minValue || x < config.maxValue);
    }


    ///Create continuous streams
    private TStream<String> accelerometerStream(Topology topology) {
        return topology.poll(accelerometerSensor, config.accelerometerConfig.mqttFrequency, config.accelerometerConfig.mqttFrequencyUnit)
                .filter(x -> checkIfValid(x, config.accelerometerConfig))
                .map(x -> String.format(Locale.US, "Accelerometer: %f, %f, %f", x[0], x[1], x[2]));
    }


    private TStream<String> magnetometerStream(Topology topology) {
        return topology.poll(magnetometerSensor, config.magnetometerConfig.mqttFrequency, config.magnetometerConfig.mqttFrequencyUnit)
                .filter(x -> checkIfValid(x, config.magnetometerConfig))
                .map(x -> String.format(Locale.US, "Magnetometer: %f, %f, %f", x[0], x[1], x[2]));
    }


    private TStream<String> lightStream(Topology topology) {
        return topology.poll(lightSensor, config.lightConfig.mqttFrequency, config.lightConfig.mqttFrequencyUnit)
                .filter(x -> checkIfValid(x, config.magnetometerConfig))
                .map(x -> String.format(Locale.US, "Light: %f", x));

    }

    //CONFIG
    public void changeConfig(ApplicationConfig config) {
        this.config = config;
        stopJob();
        startJob();
    }

    //Binding
    private IBinder binder = new LocalBinder();

    class LocalBinder extends Binder {
        MqttSensorService getService() {
            return MqttSensorService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    //Methods used in button listeners
    public boolean ToggleManualStreamFlag() {
        this.flagManual.notSoFinalBoolean = !this.flagManual.notSoFinalBoolean;
        return this.flagManual.notSoFinalBoolean;
    }


    public void Create5sMarker() {
        this.marker5s.notSoFinalBoolean = true;
    }

    public void SetConfig(ApplicationConfig config) {
        this.config = config;
        stopJob();
        try {
            startNewJob();
        } catch (IOException e) {
            Log.e("Mqtt", "properties read failure", e);
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Mqtt", "Job get fail", e);
        }
    }


    //Class used as workaround for effectively final compilation error in lambdas
    private class EffectivelyFinalBooleanBox {
        boolean notSoFinalBoolean;

        EffectivelyFinalBooleanBox(boolean notSoFinalBoolean) {
            this.notSoFinalBoolean = notSoFinalBoolean;
        }
    }
}
