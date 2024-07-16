package com.example.ovbha;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.HashMap;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivityCheckList extends AppCompatActivity {

    CheckBox ck1;
    CheckBox ck2;
    CheckBox ck3;
    CheckBox ck4;
    CheckBox ck5;
    CheckBox ck6;
    CheckBox ck7;
    CheckBox ck8;
    CheckBox ck9;

    SharedPreferences sharedPreferences;
    private static final String ITEMS_NAME = "myItems";
    //keys for checkbox
    private static final String CB_1 = "checkbox1";
    private static final String CB_2 = "checkbox2";
    private static final String CB_3 = "checkbox3";
    private static final String CB_4 = "checkbox4";
    private static final String CB_5 = "checkbox5";
    private static final String CB_6 = "checkbox6";
    private static final String CB_7 = "checkbox7";
    private static final String CB_8 = "checkbox8";
    private static final String CB_9 = "checkbox9";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_check_list);

        sharedPreferences = getSharedPreferences(ITEMS_NAME, MODE_PRIVATE);


        ck1 = findViewById(R.id.checkBox1);
        ck2 = findViewById(R.id.checkBox2);
        ck3 = findViewById(R.id.checkBox3);
        ck4 = findViewById(R.id.checkBox4);
        ck5 = findViewById(R.id.checkBox5);
        ck6 = findViewById(R.id.checkBox6);
        ck7 = findViewById(R.id.checkBox7);
        ck8 = findViewById(R.id.checkBox8);
        ck9 = findViewById(R.id.checkBox9);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        ck1.setChecked(sharedPreferences.getBoolean(CB_1, false));
        ck2.setChecked(sharedPreferences.getBoolean(CB_2, false));
        ck3.setChecked(sharedPreferences.getBoolean(CB_3, false));
        ck4.setChecked(sharedPreferences.getBoolean(CB_4, false));
        ck5.setChecked(sharedPreferences.getBoolean(CB_5, false));
        ck6.setChecked(sharedPreferences.getBoolean(CB_6, false));
        ck7.setChecked(sharedPreferences.getBoolean(CB_7, false));
        ck8.setChecked(sharedPreferences.getBoolean(CB_8, false));
        ck9.setChecked(sharedPreferences.getBoolean(CB_9, false));

        ck1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(CB_1, true);
                editor.apply();
            }
        });

        ck2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(CB_2, true);
                editor.apply();
            }
        });

        ck3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(CB_3, true);
                editor.apply();
            }
        });

        ck4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(CB_4, true);
                editor.apply();
            }
        });

        ck5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(CB_5, true);
                editor.apply();
            }
        });

        ck6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(CB_6, true);
                editor.apply();
            }
        });

        ck7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(CB_7, true);
                editor.apply();
            }
        });

        ck8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(CB_8, true);
                editor.apply();
            }
        });

        ck9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean(CB_9, true);
                editor.apply();
            }
        });


        // ck1.setOnCheckedChangeListener((buttonView, isChecked) -> Toast.makeText(getApplication().getBaseContext(), "Water is checked!", Toast.LENGTH_SHORT).show());
    }
}