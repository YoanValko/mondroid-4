package com.yoanvalko.memdroid.apps;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yoanvalko.memdroid.R;

import java.util.ArrayList;
import java.util.List;

public class ICSMusicActivity extends AppCompatActivity {

    private static final int REQ_AUDIO = 1001;

    private ListView listView;
    private TextView emptyView;
    private final List<Song> songs = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView hint = new TextView(this);
        hint.setText("Select a song to play:");
        hint.setTextSize(14);
        hint.setPadding(0, 0, 0, 8);
        root.addView(hint);

        listView = new ListView(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> playSong(position));
        root.addView(listView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

        setContentView(root);

        ensureAudioPermission();
    }

    private void ensureAudioPermission() {
        String perm = audioPermission();
        if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
            loadSongs();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{perm}, REQ_AUDIO);
        }
    }

    private String audioPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return android.Manifest.permission.READ_MEDIA_AUDIO;
        }
        return android.Manifest.permission.READ_EXTERNAL_STORAGE;
    }

    private void loadSongs() {
        songs.clear();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " = 1";
        Cursor c = null;
        try {
            c = getContentResolver().query(uri, projection, selection, null,
                    MediaStore.Audio.Media.TITLE + " ASC");
            if (c != null) {
                while (c.moveToNext()) {
                    long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String title = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    songs.add(new Song(id, title != null ? title : "Unknown",
                            artist != null ? artist : "Unknown"));
                }
            }
        } finally {
            if (c != null) c.close();
        }

        List<String> titles = new ArrayList<>();
        for (Song s : songs) titles.add(s.title + " — " + s.artist);
        adapter.clear();
        adapter.addAll(titles);
        adapter.notifyDataSetChanged();

        if (songs.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No music found");
            empty.setTextSize(16);
            empty.setPadding(0, 24, 0, 0);
            ((LinearLayout) findViewById(android.R.id.content)).addView(empty);
        }
    }

    private void playSong(int position) {
        if (position < 0 || position >= songs.size()) return;
        Song s = songs.get(position);
        Uri contentUri = android.content.ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, s.id);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        try {
            mediaPlayer = MediaPlayer.create(this, contentUri);
            if (mediaPlayer != null) {
                mediaPlayer.start();
                Toast.makeText(this, "Playing: " + s.title, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Cannot play " + s.title, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private static class Song {
        final long id;
        final String title;
        final String artist;
        Song(long id, String title, String artist) {
            this.id = id; this.title = title; this.artist = artist;
        }
    }
}
