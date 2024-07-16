package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;


//mai avem un MyViewHolder - pentru musicAdapter
public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {
    private Context mContext;
    static ArrayList<MusicFiles> albumFiles;
    View view;  //noul view, cu albume
    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles) {
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.album_name.setText(albumFiles.get(position).getTitle());
        byte[] image = getAlbumArt(albumFiles.get(position).getPath());
        if(image != null)
        {
            Glide.with(mContext)
                    .asBitmap()
                    .load(image)
                    .into(holder.album_image);
        }
        else
        {
            Glide.with(mContext)
                    .load(R.drawable.icon)
                    .into(holder.album_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                    intent.putExtra("sender", "albumDetails");
                    intent.putExtra("position", position);
                    mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        TextView album_name;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
        }
    }

    private byte[] getAlbumArt(String uri) {
        byte[] art = new byte[0];
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            art = retriever.getEmbeddedPicture();
            retriever.release();
            return art;
        } catch (Exception ex) {
            Log.e("Error", "Album art error");
        }
        return art;
    }

}
