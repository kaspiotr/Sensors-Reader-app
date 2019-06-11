package com.example.bck.sensorsreader;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class MqttConfButton {
    private String buttonText;
    private int buttonId;
    private long seconds;
    private boolean useLight;
    private boolean useAcc;
    private boolean useMag;
    private boolean useProx;


    public MqttConfButton(View popupView, int buttonId) {
        EditText editText = popupView.findViewById(R.id.conf_button_name);
        EditText seconds = popupView.findViewById(R.id.conf_seconds);
        this.buttonId = buttonId;
        this.buttonText = editText.getText().toString();
        this.seconds = Long.valueOf(seconds.getText().toString());
        this.useLight = ((CheckBox) popupView.findViewById(R.id.lightCheckbox)).isChecked();
        this.useAcc = ((CheckBox) popupView.findViewById(R.id.accCheckbox)).isChecked();
        this.useMag = ((CheckBox) popupView.findViewById(R.id.magnCheckbox)).isChecked();
        this.useProx = ((CheckBox) popupView.findViewById(R.id.proxCheckbox)).isChecked();
    }

    public String getButtonText() {
        return buttonText;
    }

    public int getButtonId() {
        return buttonId;
    }

    public long getSeconds() {
        return seconds;
    }

    public boolean isUseLight() {
        return useLight;
    }

    public boolean isUseAcc() {
        return useAcc;
    }

    public boolean isUseMag() {
        return useMag;
    }

    public boolean isUseProx() {
        return useProx;
    }
}

