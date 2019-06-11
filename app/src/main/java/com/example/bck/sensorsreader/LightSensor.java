package com.example.bck.sensorsreader;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.apache.edgent.function.Supplier;

public class LightSensor implements Supplier<Float>, SensorEventListener, ISensorRegister {

    private final SensorManager sensorManager;
    private float lightValue;

    public LightSensor(Context ctx) {
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        register();
    }

    @Override
    public Float get() {
        return lightValue;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.lightValue = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void register() {
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void unregister() {
        sensorManager.unregisterListener(this);
    }
}
