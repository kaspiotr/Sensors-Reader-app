package com.example.bck.sensorsreader;

import android.content.Context;

import org.apache.edgent.function.Supplier;

import java.util.Locale;
import java.util.Objects;

public class CustomSensorReader implements Supplier<String>  {

    private ProximitySensor proximitySensor;
    private MagnetometerSensor magnetometerSensor;
    private AccelerometerSensor accelerometerSensor;
    private LightSensor lightSensor;


    public CustomSensorReader(Context ctx, MqttConfButton mqttConfButton) {
        magnetometerSensor = mqttConfButton.isUseMag() ? new MagnetometerSensor(ctx): null;
        accelerometerSensor = mqttConfButton.isUseAcc() ?  new AccelerometerSensor(ctx): null;
        lightSensor = mqttConfButton.isUseLight() ? new LightSensor(ctx): null;
        proximitySensor = mqttConfButton.isUseProx() ? new ProximitySensor(ctx) : null;
    }


    @Override
    public String get() {
        StringBuilder buffer = new StringBuilder();
        if(Objects.nonNull(lightSensor)) {
            buffer.append("Light: ").append(lightSensor.get()).append(" | ");
        }

        if(Objects.nonNull(proximitySensor)) {
            buffer.append("Proximity: ").append(proximitySensor.get()).append(" | ");
        }

        if(Objects.nonNull(accelerometerSensor)) {
            float[] var = accelerometerSensor.get();
            buffer.append("Accelerometer: ").append(String.format(Locale.US, "%f, %f, %f", var[0], var[1], var[2])).append(" | ");
        }


        if(Objects.nonNull(magnetometerSensor)) {
            float[] var = magnetometerSensor.get();
            buffer.append("Magnetometer: ").append(String.format(Locale.US, "%f, %f, %f", var[0], var[1], var[2]));
        }

        return buffer.toString();
    }
}
