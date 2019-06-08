package com.example.bck.sensorsreader;

import java.util.Locale;

public class SensorReadings {

    public float light;
    public float[] accelerometer;
    public float[] magnetometer;

    public SensorReadings(float light, float[] accelerometer, float[] magnetometer) {
        this.light = light;
        this.accelerometer = accelerometer;
        this.magnetometer = magnetometer;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Light: %f | Accelerometer: %f, %f, %f | Magnetometer: %f,%f,%f",
                light, accelerometer[0], accelerometer[1], accelerometer[2], magnetometer[0], magnetometer[1], magnetometer[2]);
    }
}
