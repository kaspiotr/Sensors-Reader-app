package com.example.bck.sensorsreader;

import com.example.bck.sensorsreader.configuration.ApplicationConfig;

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

    public String toString(ApplicationConfig config) {
        StringBuilder sb = new StringBuilder();
        if (config.accelerometerConfig.mqttActive)
            sb.append(String.format(Locale.US, "Accelerometer: %f, %f, %f", accelerometer[0], accelerometer[1], accelerometer[2]));
        if (config.lightConfig.mqttActive)
            sb.append(String.format(Locale.US, "Light: %f", light));
        if (config.magnetometerConfig.mqttActive)
            sb.append(String.format(Locale.US, "Magnetometer: %f, %f, %f", magnetometer[0], magnetometer[1], magnetometer[2]));

        return sb.toString();
    }


    @Override
    public String toString() {
        return String.format(Locale.US, "Light: %f | Accelerometer: %f, %f, %f | Magnetometer: %f,%f,%f",
                light, accelerometer[0], accelerometer[1], accelerometer[2], magnetometer[0], magnetometer[1], magnetometer[2]);
    }
}
