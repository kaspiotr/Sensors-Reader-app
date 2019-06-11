package com.example.bck.sensorsreader;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.apache.edgent.function.Supplier;

public class ProximitySensor implements Supplier<Float>, SensorEventListener, ISensorRegister {

    private final SensorManager sensorManager;
    private float proximityValue;

    public ProximitySensor(Context ctx) {
         sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public Float get() {
        return proximityValue;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.proximityValue = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void register() {
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void unregister() {
        sensorManager.unregisterListener(this);
    }
}
