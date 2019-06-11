package com.example.bck.sensorsreader;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.apache.edgent.connectors.mqtt.MqttConfig;
import org.apache.edgent.connectors.mqtt.MqttStreams;
import org.apache.edgent.execution.Job;
import org.apache.edgent.providers.direct.DirectProvider;
import org.apache.edgent.topology.TStream;
import org.apache.edgent.topology.Topology;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MqttSensorServiceCustom extends Service {

    private Future<Job> customMqttJob = null;
    private MqttConfig customMqttConfig;
    private Topology customTopology;
    private DirectProvider customDp;
    private Properties customProps = new Properties();

    private Map<Integer, EffectivelyFinalBooleanBox> customButtons = new HashMap<>();

    @Override
    public void onDestroy() {
        try {
            customMqttJob.get().stateChange(Job.Action.CLOSE);
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
        if (this.customMqttJob != null && this.customMqttJob.get().getCurrentState() == Job.State.RUNNING) {
            Log.i("Mqtt", "Edgent is already running.");
            return;
        }

        Log.i("Mqtt", "No Edgent job is running. Starting new Edgent job.");

        customDp = new DirectProvider();
        customTopology = customDp.newTopology();

        customProps.load(getBaseContext().getAssets().open("mqtt.properties"));
        customMqttConfig = MqttConfig.fromProperties(customProps);

        this.customMqttJob = customDp.submit(customTopology);
    }

    //Sensor getters
    private CustomSensorReader getSensorReadingsSupplier(MqttConfButton mqttConfButton) {
        return new CustomSensorReader(this, mqttConfButton);
    }

    //Binding
    private IBinder binder = new LocalBinder();


    class LocalBinder extends Binder {
        MqttSensorServiceCustom getService() {
            return MqttSensorServiceCustom.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void handleCustomJob(MqttConfButton mqttConfButton) {
        if (this.customButtons.get(mqttConfButton.getButtonId()) != null) {
            EffectivelyFinalBooleanBox effbb = this.customButtons.get(mqttConfButton.getButtonId());
            effbb.notSoFinalBoolean = true;
            this.customButtons.put(mqttConfButton.getButtonId(), effbb);

        } else {
            customButtons.put(mqttConfButton.getButtonId(), new EffectivelyFinalBooleanBox(true));
            TStream<String> bufferedStream = customTopology.poll(getSensorReadingsSupplier(mqttConfButton), 100, TimeUnit.MILLISECONDS)
                    .last(mqttConfButton.getSeconds(), TimeUnit.SECONDS, readings -> 0)
                    .batch((readings, integer) -> {

                        EffectivelyFinalBooleanBox effbb = this.customButtons.get(mqttConfButton.getButtonId());
                        List<String> result = effbb.notSoFinalBoolean ?
                                readings : new LinkedList<>();
                        effbb.notSoFinalBoolean = false;
                        this.customButtons.put(mqttConfButton.getButtonId(), effbb);
                        return result;
                    })
                    .filter(readings -> readings.size() > 0)
                    .map(readings -> {
                        StringBuilder sb = new StringBuilder();
                        for (String reading : readings) {
                            sb.append(reading).append("\n");
                        }
                        return sb.toString();
                    });
            MqttStreams customStream = new MqttStreams(customTopology, () -> customMqttConfig);

            customStream.publish(bufferedStream, customProps.getProperty("mqtt.customTopic"), 0, false);


            this.customMqttJob = customDp.submit(customTopology);
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
