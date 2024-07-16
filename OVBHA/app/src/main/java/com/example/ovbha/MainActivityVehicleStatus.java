package com.example.ovbha;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivityVehicleStatus extends AppCompatActivity {

    final private int OIL_TEMP_THRESHOLD = 110;
    final private int COOLANT_TEMP_THRESHOLD = 91;
    final private int FUEL_LEVEL_THRESHOLD = 25;
    final private int TIRE_PRESSURE_THRESHOLD_LOW = 1;
    final private int TIRE_PRESSURE_THRESHOLD_HIGH = 4;


    private TextView oil_temp_value;
    private TextView coolant_temp_value;
    private TextView fuel_level_value;
    private TextView broken_light_value;
    private TextView tire_pressure_value;
    private TextView breaking_sys_value;
    private TextView steering_sys_value;

    private String[] brakesConditions = {"Good", "Fair", "Poor"};
    private String[] steeringConditions = {"Responsive", "Stiff", "Loose"};
    private String[] lightsConditions = {"Functional", "Broken", "Flickering"};


    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_vehicle_status);

        oil_temp_value = findViewById(R.id.oil_temp_value);
        coolant_temp_value = findViewById(R.id.coolant_temp_value);
        fuel_level_value = findViewById(R.id.fuel_level_value);
        broken_light_value = findViewById(R.id.lights_status_value);
        tire_pressure_value = findViewById(R.id.tire_status_value);
        breaking_sys_value = findViewById(R.id.break_status_value);
        steering_sys_value = findViewById(R.id.steering_status_value);

        startUpdatingParameters();
    }

    private void startUpdatingParameters()
    {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                int oilTemp = generateRandomValue(75, 120);
                int coolantTemp = generateRandomValue(60, 120);
                int fuelLevel = generateRandomValue(0, 100);
                int tirePressure = generateRandomValue(1, 4);

                String brakesCondition = getRandomCondition(brakesConditions);
                String steeringCondition = getRandomCondition(steeringConditions);
                String lightsCondition = getRandomCondition(lightsConditions);

                checkThresholds(brakesCondition, steeringCondition, lightsCondition);
                checkThresholdsINT(oilTemp, coolantTemp, fuelLevel, tirePressure);

                runOnUiThread(() ->{
                    oil_temp_value.setText(String.format(String.valueOf(oilTemp)));
                    coolant_temp_value.setText(String.format(String.valueOf(coolantTemp)));
                    fuel_level_value.setText(String.format(String.valueOf(fuelLevel)));
                    tire_pressure_value.setText(String.format(String.valueOf(tirePressure)));

                    breaking_sys_value.setText(String.format(brakesCondition));
                    steering_sys_value.setText(String.format(steeringCondition));
                    broken_light_value.setText(String.format(lightsCondition));

                });

            }
        }, 0, 10000); //10s

    }



    private void checkThresholds(String brakesCondition, String steeringCondition, String lightsCondition) {
        // Check brakes condition threshold
        if (brakesCondition.equals("Poor")) {
            showWarning("Brakes condition is poor. Please check your brakes.");
        }

        // Check steering condition threshold
        if (steeringCondition.equals("Stiff")) {
            showWarning("Steering condition is stiff. Please check your steering system.");
        }

        // Check lights condition threshold
        if (lightsCondition.equals("Broken")) {
            showWarning("One or more lights are broken. Please check your lights.");
        }
    }

    private void checkThresholdsINT(int oilTemp, int coolantTemp, int fuelLevel, int tirePressure) {
        // Check oil temperature threshold
        if (oilTemp > OIL_TEMP_THRESHOLD) {
            showWarning("Oil temperature is high. Please check your engine.");
        }

        // Check coolant temperature threshold
        if (coolantTemp > COOLANT_TEMP_THRESHOLD) {
            showWarning("Coolant temperature is high. Please check your cooling system.");
        }

        // Check fuel level threshold
        if (fuelLevel < FUEL_LEVEL_THRESHOLD) {
            showWarning("Fuel level is low. Please refuel your vehicle.");
        }

        // Check tire pressure threshold
        if (tirePressure < TIRE_PRESSURE_THRESHOLD_LOW || tirePressure > TIRE_PRESSURE_THRESHOLD_HIGH) {
            showWarning("Tire pressure is out of recommended range. Please check your tires.");
        }
    }


    private int generateRandomValue(int min, int max)
    {
        return random.nextInt(max - min + 1) + min;
    }

    private String getRandomCondition(String[] conditions)
    {
        int index = random.nextInt(conditions.length);
        return conditions[index];
    }


    private void showWarning(String message)
    {
        runOnUiThread(() -> {
            Toast.makeText(MainActivityVehicleStatus.this, message, Toast.LENGTH_LONG).show();
        });
    }

}