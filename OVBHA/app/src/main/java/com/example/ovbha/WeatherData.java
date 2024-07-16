package com.example.ovbha;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {

    private String mTemp, mCity, mWeatherType;
    private int mCondition;

    public static WeatherData fromJson(JSONObject jsonObject)
    {
        try{

            WeatherData weatherD = new WeatherData();

            weatherD.mCity= jsonObject.getString("name");
            weatherD.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherD.mWeatherType= jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15; // kelvin - > celsius

            int roundedValue= (int)Math.rint(tempResult);
            weatherD.mTemp=Integer.toString(roundedValue);

            return weatherD;

        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    public String getmTemp() {
        return mTemp + "°C";
    }

    public String getmCity() {
        return mCity;
    }

    public String getmWeatherType() {
        return mWeatherType;
    }
}
