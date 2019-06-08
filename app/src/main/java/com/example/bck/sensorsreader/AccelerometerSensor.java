package com.example.bck.sensorsreader;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.apache.edgent.function.Supplier;

public class AccelerometerSensor implements Supplier<float[]>, SensorEventListener {

    private float[] accelerometer;

    public AccelerometerSensor(Context ctx) {
        SensorManager sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public float[] get() {
        return accelerometer;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.accelerometer = event.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
