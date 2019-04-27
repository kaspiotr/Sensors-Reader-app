package com.example.bck.sensorsreader;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.apache.edgent.function.Supplier;

public class MagnetometerSensor implements Supplier<float[]>, SensorEventListener {

    private float[] magnetometerValue;

    public MagnetometerSensor(Context ctx) {
        SensorManager sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        Sensor magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public float[] get() {
        return magnetometerValue;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        this.magnetometerValue = event.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
