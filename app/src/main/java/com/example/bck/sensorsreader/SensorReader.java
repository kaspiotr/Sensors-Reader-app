package com.example.bck.sensorsreader;


import android.content.Context;

import org.apache.edgent.function.Supplier;

public class SensorReader implements Supplier<SensorReadings> {

    private MagnetometerSensor magnetometerSensor;
    private AccelerometerSensor accelerometerSensor;
    private LightSensor lightSensor;


    public SensorReader(Context ctx) {
        magnetometerSensor = new MagnetometerSensor(ctx);
        accelerometerSensor = new AccelerometerSensor(ctx);
        lightSensor = new LightSensor(ctx);
    }


    @Override
    public SensorReadings get() {
        return new SensorReadings(
                lightSensor.get(),
                accelerometerSensor.get(),
                magnetometerSensor.get()
        );
    }
}
