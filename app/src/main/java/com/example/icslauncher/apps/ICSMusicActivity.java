package com.example.icslauncher.apps;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.icslauncher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Reimplementation of the ICS Music player. Lists audio from MediaStore and plays a selection
 * with MediaPlayer. Requires READ_MEDIA_AUDIO (API 33+) / READ_EXTERNAL_STORAGE (API <= 32).
 */
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
        setContentView(R.layout.activity_music);

        listView = findViewById(R.id.music_list);
        emptyView = findViewById(R.id.music_empty);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new java.util.ArrayList<>());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> playSong(position));

        ensureAudioPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                emptyView.setText(R.string.perm_denied);
            }
        }
    }

    private void ensureAudioPermission() {
        String perm = audioPermission();
        if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
            loadSongs();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(perm)) {
                    Toast.makeText(this, R.string.perm_storage_rationale, Toast.LENGTH_LONG).show();
                }
                ActivityCompat.requestPermissions(this, new String[]{perm}, REQ_AUDIO);
            } else {
                loadSongs();
            }
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
            emptyView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
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

    private static class Song {
        final long id;
        final String title;
        final String artist;
        Song(long id, String title, String artist) {
            this.id = id; this.title = title; this.artist = artist;
        }
    }
}
