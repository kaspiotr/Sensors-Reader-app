package com.example.bck.sensorsreader;


import android.content.Context;

import com.example.bck.sensorsreader.configuration.ApplicationConfig;

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
    
    public void applyConfig(ApplicationConfig config){
        if (config.accelerometerConfig.active) 
            accelerometerSensor.register();
        else
            accelerometerSensor.unregister();
        if (config.lightConfig.active) 
            lightSensor.register();
        else
            lightSensor.unregister();
        if (config.magnetometerConfig.active) 
            magnetometerSensor.register();
        else
            magnetometerSensor.unregister();
    }
    
}
