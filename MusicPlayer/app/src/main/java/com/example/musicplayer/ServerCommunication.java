package com.example.musicplayer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class ServerCommunication {

    private static final String SERVER_IP = "10.191.31.194";    // 192.168.0.133 wifi   192.168.0.152 lan   10.191.206.167 umfst?  10.191.210.185 umfst student
    private static final int SERVER_PORT = 8000;

    public interface MessageCallback {
        void onMessageReceived(String message);
    }

    public static void sendMessage(final String Title, final MessageCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Socket socket = new Socket(SERVER_IP, SERVER_PORT);

                    // Send data to the server
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(Title);

                    // Receive data from the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String receivedMessage = in.readLine();

                    // Close the connection
                    socket.close();

                    return receivedMessage;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (callback != null) {
                    callback.onMessageReceived(result);
                }
            }
        }.execute();
    }
}
