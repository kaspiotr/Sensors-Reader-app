package com.example.bck.sensorsreader;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import java.util.function.Consumer;

public class CustomButtonConfigurationPopup implements android.view.View.OnClickListener {

    private PopupWindow popupWindow;
    private Context context;
    private View background;
    private Consumer<MqttConfButton> handlerMethod;

    CustomButtonConfigurationPopup(Context context, View background, Consumer<MqttConfButton> handlerMethod) {
        this.context = context;
        this.background = background;
        this.handlerMethod = handlerMethod;
    }


    public void showWindow(int buttonId) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.configuration_popup, null);
        popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Button yes = popupView
                .findViewById(R.id.btn_yes);

        yes.setOnClickListener(v -> {
            EditText a =  popupView.findViewById(R.id.conf_button_name);
            View aaa = (View) background.getParent();
            ViewGroup linearLayout =  aaa.findViewById(R.id.rootContainer);
            Button bt = new Button(context);
            bt.setText(a.getText().toString());
            bt.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            bt.setId(buttonId);
            bt.setOnClickListener(cl -> {
                MqttConfButton confButton = new MqttConfButton(popupView, buttonId);
                handlerMethod.accept(confButton);

            });

            linearLayout.addView(bt);
            popupWindow.dismiss();
        });

        Button no = popupView
                .findViewById(R.id.btn_no);
        no.setOnClickListener(v -> popupWindow.dismiss());


        popupWindow.showAtLocation(background, Gravity.CENTER, Gravity.CENTER,
                Gravity.CENTER);
        popupWindow.setAnimationStyle(android.R.style.Animation_Toast);
        popupWindow.setFocusable(true);
        popupWindow.update();

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        popupWindow.dismiss();
    }


}

