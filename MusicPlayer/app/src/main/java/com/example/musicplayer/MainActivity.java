package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.sql.Array;
import java.util.ArrayList;
import javax.net.ssl.TrustManager;

//pt server C#
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import android.os.AsyncTask;


public class MainActivity extends AppCompatActivity {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;



    public static final int REQUEST_CODE = 1;
    static ArrayList<MusicFiles> musicFiles;

    static boolean shuffle = false;
    static boolean repeat = false;

    static ArrayList<MusicFiles> albums = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initViewPager();  //pt fragmente
        permission();  //permisiune accesare fisiere

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        try{
            if(socket != null && socket.isClosed())
            {
                socket.close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void permission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
        else
        {
            //Toast.makeText(this, "Permission Granted !", Toast.LENGTH_SHORT).show();
            musicFiles = getAllAudio(this);
            initViewPager();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //whatever
               // Toast.makeText(this, "Permission Granted !", Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(this);
                initViewPager();
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    private void initViewPager()
    {
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
        viewPager.setAdapter(viewPagerAdapter);
       tabLayout.setupWithViewPager(viewPager);
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title)
        {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public static ArrayList<MusicFiles> getAllAudio(Context context)        //pt baza de date
    {

        ArrayList<String> duplicate = new ArrayList<>();
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, //path
                MediaStore.Audio.Media.ARTIST,
        };

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if(cursor != null)
        {
            while(cursor.moveToNext())
            {
                String Album = cursor.getString(0);
                String Title = cursor.getString(1);
                String Duration = cursor.getString(2);
                String Path = cursor.getString(3);
                String Artist = cursor.getString(4);

                MusicFiles musicFiles = new MusicFiles(Path, Title, Artist, Album, Integer.parseInt(Duration));
                Log.i("Path : " + Path, "Album : " + Album);
                tempAudioList.add(musicFiles);

                if(!duplicate.contains(Album))
                {
                    albums.add(musicFiles);
                    duplicate.add(Album);
                }

                String messageToBeSent = Artist + "-" + Title;

                Log.d("MainActivity", "Sending parameters: " + messageToBeSent);

                ServerCommunication.sendMessage(messageToBeSent, new ServerCommunication.MessageCallback() {
                    @Override
                    public void onMessageReceived(String param1) {
                        Log.d("MainActivity", "Received from server: " + param1);

                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, param1, duration);
                        toast.show();
                    }
                });

            }
            cursor.close();
        }
            return tempAudioList;
    }

}



