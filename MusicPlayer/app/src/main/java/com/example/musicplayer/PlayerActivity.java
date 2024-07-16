package com.example.musicplayer;

import static com.example.musicplayer.AlbumDetailsAdapter.albumFiles;
import static com.example.musicplayer.MainActivity.musicFiles;
import static com.example.musicplayer.MainActivity.repeat;
import static com.example.musicplayer.MainActivity.shuffle;
import com.example.musicplayer.AlbumDetailsAdapter;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name, artist_name, duration_played, duration_total;
    ImageView cover_art, nextBtn, prevBtn, shuffleBtn, repeatBtn, backBtn;
    FloatingActionButton playPausedBtn;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<MusicFiles> listOfSongs = new ArrayList<>();

    static Uri uri;
    static MediaPlayer mediaPlayer;

    private Handler handler = new Handler();

    //thread pentru butoane
    private Thread playThread, prevThread, nextThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();

        getIntentMethod();

     song_name.setText(listOfSongs.get(position).getTitle());
     artist_name.setText(listOfSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser)
                {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null)
                {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }

                    handler.postDelayed(this, 1000);
            }
        });


        shuffleBtn .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffle)
                {
                    shuffle = false;
                    shuffleBtn.setImageResource(R.drawable.shuffle_off_svgrepo);
                }
                else {
                    shuffle = true;
                    shuffleBtn.setImageResource(R.drawable.shuffle_on_svgrepo);
                }
            }
        });
                    repeatBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(repeat)
                            {
                                repeat = false;
                                repeatBtn.setImageResource(R.drawable.repeat_off_minimalistic_svgrepo);
                            }
                            else {
                                repeat = true;
                                repeatBtn.setImageResource(R.drawable.repeat_svgrepo);
                            }
                        }
                    });
    }

    @Override
    protected void onPostResume() {

        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();

        super.onPostResume();
    }

    private void prevThreadBtn() {

        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();

    }

    private void prevBtnClicked() {

        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffle && !repeat)
            {
                position = getRandom(listOfSongs.size() -1);
            }
            else if (!shuffle && !repeat)
            {
                position = ((position - 1) < 0 ? (listOfSongs.size()  - 1) : (position -1) );
            }

            uri = Uri.parse(listOfSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listOfSongs.get(position).getTitle());
            artist_name.setText(listOfSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }

                    handler.postDelayed(this, 1000);

                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPausedBtn.setBackgroundResource(R.drawable.pause_button_svgrepo);
            mediaPlayer.start();

        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffle && !repeat)
            {
                position = getRandom(listOfSongs.size() -1);
            }
            else if (!shuffle && !repeat)
            {
                position = ((position - 1) < 0 ? (listOfSongs.size() - 1) : (position - 1)); //ternary operator, scadem melodia, verificam daca am ajuns la 0
            }

            uri = Uri.parse(listOfSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listOfSongs.get(position).getTitle());
            artist_name.setText(listOfSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }

                    handler.postDelayed(this, 1000);

                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPausedBtn.setBackgroundResource(R.drawable.play_button_svgrepo);
        }
        
    }

    private void nextThreadBtn()
    {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();   /////
                    }
                });
            }
        };
        nextThread.start();

    }

    private void nextBtnClicked() {

        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffle && !repeat)
            {
                    position = getRandom(listOfSongs.size() -1);
            }
            else if (!shuffle && !repeat)
            {
                position = ((position + 1) % listOfSongs.size());
            }

            uri = Uri.parse(listOfSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);

            song_name.setText(listOfSongs.get(position).getTitle());
            artist_name.setText(listOfSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }

                    handler.postDelayed(this, 1000);

                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPausedBtn.setBackgroundResource(R.drawable.pause_button_svgrepo);
            mediaPlayer.start();

        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffle && !repeat)
            {
                position = getRandom(listOfSongs.size() -1);
            }
            else if (!shuffle && !repeat)
            {
                position = ((position + 1) % listOfSongs.size());
            }


            uri = Uri.parse(listOfSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listOfSongs.get(position).getTitle());
            artist_name.setText(listOfSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }

                    handler.postDelayed(this, 1000);

                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPausedBtn.setBackgroundResource(R.drawable.play_button_svgrepo);
        }
    }

    private int getRandom(int i)
    {
        Random rand = new Random();
        return rand.nextInt(i+1);
    }

    private void playThreadBtn() {

        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                playPausedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPausedBtnClicked();
                    }
                });
            }
        };
    playThread.start();
    }

    private void playPausedBtnClicked()
    {
        if(mediaPlayer.isPlaying())
        {
            playPausedBtn.setImageResource(R.drawable.play_button_svgrepo);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }

                    handler.postDelayed(this, 1000);

                }
            });
        }

        else
        {
            playPausedBtn.setImageResource(R.drawable.pause_button_svgrepo);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);

        totalOut = minutes + ":" +seconds;
        totalNew = minutes + ":" + "0" + seconds;

        if(seconds.length() == 1)
        {
            return  totalNew;
        }
        else {
              return totalOut;
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");

        if(sender != null && sender.equals("albumDetails"))           //!! assert
        {
            listOfSongs = albumFiles;

        }
        else {
            listOfSongs = musicFiles;
        }
        
        if(listOfSongs != null)
        {
            playPausedBtn.setImageResource(R.drawable.pause_button_svgrepo);
            uri = Uri.parse(listOfSongs.get(position).getPath());
        }
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }

        seekBar.setMax(mediaPlayer.getDuration() / 1000) ;  //millisecond by default

        metaData(uri);
    }

    private void initViews() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        playPausedBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
    }

    private void metaData(Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());

        int durationTotal = Integer.parseInt(String.valueOf(listOfSongs.get(position).getDuration())) / 1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        if(art != null)
        {
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(cover_art);
        }
        else
        {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.icon)
                    .into(cover_art);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if(mediaPlayer != null)
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}