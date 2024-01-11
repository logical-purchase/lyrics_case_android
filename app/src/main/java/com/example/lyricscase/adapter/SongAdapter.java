package com.example.lyricscase.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lyricscase.R;
import com.example.lyricscase.SongDetailActivity;
import com.example.lyricscase.model.Song;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private final List<Song> songs;
    private final Context context;

    public SongAdapter(List<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_title.setText(songs.get(position).getSongTitle());
        holder.tv_artists.setText(songs.get(position).getArtistNames());
        Picasso.get().load(songs.get(position).getSongArtwork()).into(holder.iv_artwork);
        holder.tv_views.setText(songs.get(position).songViewsToString());

        holder.itemView.setOnClickListener(view -> {
            int clickedPosition = holder.getAdapterPosition();
            if (clickedPosition != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(view.getContext(), SongDetailActivity.class);
                intent.putExtra("song_id", songs.get(clickedPosition).getSongId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_artwork;
        private final TextView tv_title;
        private final TextView tv_artists;
        private final TextView tv_views;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_artwork = itemView.findViewById(R.id.iv_artwork);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_artists = itemView.findViewById(R.id.tv_artists);
            tv_views= itemView.findViewById(R.id.tv_views);
        }
    }

    public void updateData(List<Song> newSongs) {
        songs.clear();
        if (newSongs != null) {
            songs.addAll(newSongs);
        }
        notifyDataSetChanged();
    }
}
