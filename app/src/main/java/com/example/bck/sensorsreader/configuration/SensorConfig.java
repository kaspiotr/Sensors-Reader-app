package com.example.bck.sensorsreader.configuration;

import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class SensorConfig implements Serializable {

    public boolean active;
    public boolean mqttActive;
    public double minValue;
    public double maxValue;
    public TimeUnit mqttFrequencyUnit;
    public long mqttFrequency;


    public SensorConfig() {
        minValue = -Double.MAX_VALUE;
        maxValue = Double.MAX_VALUE;
        mqttActive = active = false;
        mqttFrequencyUnit = TimeUnit.SECONDS;
        mqttFrequency = 1;
    }

    public SensorConfig(boolean active, boolean mqttActive, double minValue, double maxValue, TimeUnit mqttFrequencyUnit, long mqttFrequency) {
        this.active = active;
        this.mqttActive = mqttActive;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.mqttFrequencyUnit = mqttFrequencyUnit;
        this.mqttFrequency = mqttFrequency;
    }

    public void readValuesFromViews(Switch acrtiveSwitch, Switch mqqtActiveSwitch, TextView freqText, Spinner freqUnitsSpinner, TextView minText, TextView maxText) {
        active = acrtiveSwitch.isChecked();
        try {
            mqttFrequency = Long.parseLong(freqText.getText().toString());
        } catch (Exception e) {
            mqttFrequency = 1;
        }
        mqttActive = mqqtActiveSwitch.isChecked();
        try {
            minValue = Double.parseDouble(minText.getText().toString());
        } catch (Exception e) {
            minValue = -Double.MAX_VALUE;
        }
        try {
            maxValue = Double.parseDouble(maxText.getText().toString());
        } catch (Exception e) {
            maxValue = Double.MAX_VALUE;
        }
        if (freqUnitsSpinner.getSelectedItem() == null) {
            mqttFrequencyUnit = TimeUnit.SECONDS;
        } else {
            mqttFrequencyUnit = Enum.valueOf(TimeUnit.class, freqUnitsSpinner.getSelectedItem().toString());
        }

    }


}
