package com.example.bck.sensorsreader;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.connectors.mqtt.MqttStreams;
import org.apache.edgent.execution.Job;
import org.apache.edgent.function.Supplier;
import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MqttSensorService extends Service {

    private Future<Job> mqttJob = null;
    private EffectivelyFinalBooleanBox marker5s = new EffectivelyFinalBooleanBox(false);
    private EffectivelyFinalBooleanBox flagManual = new EffectivelyFinalBooleanBox(false);

    @Override
    public void onDestroy() {
        try {
            mqttJob.get().stateChange(Job.Action.CLOSE);
        } catch (InterruptedException | ExecutionException | NullPointerException ignored) {
        }

        Log.i("Mqtt", "Destroyed service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("Mqtt", "started service");
        try {
            createNewJob();
        } catch (IOException e) {
            Log.e("Mqtt", "properties read failure", e);
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Mqtt", "Job get fail", e);
        }

        return START_STICKY;
    }


    private void createNewJob() throws IOException, ExecutionException, InterruptedException {

        if (this.mqttJob != null && this.mqttJob.get().getCurrentState() == Job.State.RUNNING) {
            Log.i("Mqtt", "Edgent is already running.");
            return;
        }

        Log.i("Mqtt", "No Edgent job is running. Starting new Edgent job.");

        DirectProvider dp = new DirectProvider();
        Topology topology = dp.newTopology();


        Properties props = new Properties();
        props.load(getBaseContext().getAssets().open("mqtt.properties"));
        MqttConfig mqttConfig = MqttConfig.fromProperties(props);


        TStream<String> manualStream = topology.poll(getSensorReadingsSupplier(), 1, TimeUnit.SECONDS)
                .filter(x -> this.flagManual.notSoFinalBoolean)
                .map(SensorReadings::toString);

        TStream<String> bufferedStream = topology.poll(getSensorReadingsSupplier(), 100, TimeUnit.MILLISECONDS)
                .last(5, TimeUnit.SECONDS, readings -> 0)
                .batch((readings, integer) -> {
                    List<SensorReadings> result = this.marker5s.notSoFinalBoolean ?
                            readings : new LinkedList<SensorReadings>();
                    this.marker5s.notSoFinalBoolean = false;
                    return result;
                })
                .filter(readings -> readings.size() > 0)
                .map(readings -> {
                    StringBuilder sb = new StringBuilder();
                    for (SensorReadings reading : readings) {
                        sb.append(reading.toString()).append("\n");
                    }
                    return sb.toString();
                });


        MqttStreams mqtt5sStream = new MqttStreams(topology, () -> mqttConfig);
        MqttStreams mqttManualStream = new MqttStreams(topology, () -> mqttConfig);

        mqtt5sStream.publish(bufferedStream, props.getProperty("mqtt.5sBufferTopic"), 0, false);
        mqttManualStream.publish(manualStream, props.getProperty("mqtt.manualTopic"), 0, false);


        this.mqttJob = dp.submit(topology);
    }

    //Sensor getters
    private SensorReader getSensorReadingsSupplier() {
        return new SensorReader(this);
    }

    private Supplier<Float> getLightSupplier() {
        return new LightSensor(this);
    }

    private Supplier<float[]> getAccelerometerSupplier() {
        return new AccelerometerSensor(this);
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


    //Class used as workaround for effectively final compilation error in lambdas
    private class EffectivelyFinalBooleanBox {
        boolean notSoFinalBoolean;

        EffectivelyFinalBooleanBox(boolean notSoFinalBoolean) {
            this.notSoFinalBoolean = notSoFinalBoolean;
        }
    }
}
