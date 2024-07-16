package com.example.ovbha;

import static android.provider.UserDictionary.Words.APP_ID;

import static com.example.ovbha.MySmsSender.sendSmsInBackground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


//import com.example.ovbha.model.WeatherResponse;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String CHANNEL_ID = "CheckNotification";
    Button button_location;
    TextView textView_location;
    TextView textLatitude;
    TextView textLongitude;
    LocationManager locationManager;
    Button btn_list;
    Button btn_veh_diag;
   private MySmsSender sendSmsInBackground;

    //weather
    private TextView cityNameTextView;
    private TextView temperatureTextView;
    private EditText number_sos;
    private TextView weatherDescriptionTextView;
    private String cityName;
    private String apiKey = "657c6bbc863ac5274a61a7e7d1fd9da0";
    private String temp_message;
    private String conditions_message;
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;
    String Location_Provider = LocationManager.GPS_PROVIDER;

    LocationManager mLocationManager;
    LocationListener mLocationListener;


    String latSMS;
    String longSMS;
    String message;

    //sms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "CL Reminder", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_attention)
                .setContentTitle("CHECKLIST REMINDER")
                .setContentText("Check for missing gear.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        managerCompat.notify(1, builder.build());

        textView_location = findViewById(R.id.text_location);
        button_location = findViewById(R.id.button_location);
        textLatitude = findViewById(R.id.text_latitude);
        textLongitude = findViewById(R.id.text_longitude);
        number_sos = findViewById(R.id.number_for_sos);


        Button btnSOS = (Button) findViewById(R.id.btnSOS);
        btnSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS(v);
            }
        });

        //runtime permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        button_location.setOnClickListener(v -> {
            getLocation();  //metoda pt locatie
        });

        //weather

        cityNameTextView = findViewById(R.id.textView_cityName);
        temperatureTextView = findViewById(R.id.textView_temperature);
        weatherDescriptionTextView = findViewById(R.id.textView_description);


        btn_list = findViewById(R.id.btn_checklist);
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivityCheckList.class);
                startActivity(intent);
            }
        });


        btn_veh_diag = findViewById(R.id.veh_diag_btn);
        btn_veh_diag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivityVehicleStatus.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        getWeatherForCurrentLocation();
    }


    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                String lat_weather = String.valueOf(location.getLatitude());
                String long_weather = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams();
                params.put("lat", lat_weather);
                params.put("long", long_weather);
                params.put("appid", APP_ID);

                letsDoSomeNetworking(params);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Weather location loaded succesfully", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            }
            else
            {
                //denied
                Toast.makeText(MainActivity.this, "Weather location denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

private void letsDoSomeNetworking(RequestParams params)
{
    AsyncHttpClient client = new AsyncHttpClient();

    String weather_URL = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + ",ro&APPID=657c6bbc863ac5274a61a7e7d1fd9da0";

    client.get(weather_URL, params, new JsonHttpResponseHandler(){

        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            Toast.makeText(MainActivity.this, "Data succes", Toast.LENGTH_SHORT).show();

            WeatherData weatherD = WeatherData.fromJson(response);

            updateUI(weatherD);

            //super.onSuccess(statusCode, headers, response);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
           // super.onFailure(statusCode, headers, throwable, errorResponse);
        }
    });
}

private void updateUI(WeatherData weather)
{
    temperatureTextView.setText(weather.getmTemp());
    cityNameTextView.setText(weather.getmCity());
    weatherDescriptionTextView.setText(weather.getmWeatherType());

    temp_message = weather.getmTemp();
    conditions_message = weather.getmWeatherType();
}


    @Override
    protected void onPause() {
        super.onPause();

        if(mLocationManager != null)
        {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }


    //sms
    private void sendSMS(View v) {
        String number = number_sos.getText().toString();
         Uri uri = Uri.parse("smsto:"+number);
         Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", message + " Temperature: " + temp_message + "Conditions: " + conditions_message);
        startActivity(it);
       // sendSmsInBackground(number, message);
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, MainActivity.this);
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    //latitude & longitude
    @Override
    public void onLocationChanged(@NonNull Location location) {
        System.out.println("Lat = " + location.getLatitude());
        System.out.println("Long = " + location.getLongitude());

        String latitude = String.format("%.5f", location.getLatitude());
        String longitude = String.format("%.5f", location.getLongitude());

        latSMS = String.format("%.5f", location.getLatitude());
        longSMS = String.format("%.5f", location.getLongitude());
        message = "lat: " + latSMS + " " + "long: " + longSMS;

        //live location to contact

        Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // Toast.makeText(MainActivity.this, "am trimis pozitia", Toast.LENGTH_SHORT).show();
                            sendSmsInBackground(number_sos.getText().toString(), message);
                        }
                    });
                }
            }, 0, 10000);


        textLatitude.setText(latitude);
        textLongitude.setText(longitude);
        //textLatitude.setText(String.valueOf(location.getLatitude()));
        //textLongitude.setText(String.valueOf(location.getLongitude()));

        //Toast.makeText(this, ""+location.getLatitude()+","+ location.getLongitude(), Toast.LENGTH_SHORT).show();

        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            assert addresses != null;
            String address = addresses.get(0).getAddressLine(0);
            Log.e("btn", "apasat");
            cityName = addresses.get(0).getLocality();
            Log.e("city", cityName);
            textView_location.setText(address);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    @Override
    public void onProviderEnabled(@NonNull String provider) {}
    @Override
    public void onProviderDisabled(@NonNull String provider) {}
}