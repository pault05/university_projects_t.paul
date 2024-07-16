package com.example.ovbha;
import android.telephony.SmsManager;

public class MySmsSender {

    public static void sendSmsInBackground(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}