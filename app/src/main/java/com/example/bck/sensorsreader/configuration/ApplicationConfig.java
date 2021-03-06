package com.example.bck.sensorsreader.configuration;

import java.io.Serializable;

public class ApplicationConfig implements Serializable {

    public static ApplicationConfig defaultConfig = new ApplicationConfig();

    public SensorConfig lightConfig;
    public SensorConfig proximityConfig;
    public SensorConfig accelerometerConfig;
    public SensorConfig magnetometerConfig;


    public ApplicationConfig() {
        lightConfig = new SensorConfig();
        proximityConfig = new SensorConfig();
        accelerometerConfig = new SensorConfig();
        magnetometerConfig = new SensorConfig();
    }
}
