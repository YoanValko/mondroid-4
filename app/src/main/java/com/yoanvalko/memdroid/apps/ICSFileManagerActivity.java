package com.yoanvalko.memdroid.apps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yoanvalko.memdroid.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Reimplementation of a Nexus-era file manager. Browses the device's external storage
 * using only public APIs and scoped-storage-compatible paths (external storage root,
 * Downloads, DCIM). No root required.
 */
public class ICSFileManagerActivity extends AppCompatActivity {

    private ListView listView;
    private TextView pathView;
    private List<FileItem> items = new ArrayList<>();
    private ArrayAdapter<FileItem> adapter;
    private File currentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        pathView = new TextView(this);
        pathView.setTextSize(14);
        pathView.setPadding(0, 0, 0, 8);
        root.addView(pathView);

        listView = new ListView(this);
        adapter = new ArrayAdapter<FileItem>(this, android.R.layout.simple_list_item_1, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                FileItem item = getItem(position);
                tv.setText(item.name);
                tv.setCompoundDrawablesWithIntrinsicBounds(
                        item.isDirectory ? R.drawable.ic_folder : R.drawable.ic_file, 0, 0, 0);
                tv.setCompoundDrawablePadding(8);
                return tv;
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            FileItem item = items.get(position);
            if (item.isDirectory) {
                enterDirectory(item.file);
            } else {
                openFile(item.file);
            }
        });
        root.addView(listView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        Button btnUp = new Button(this);
        btnUp.setText("Up");
        btnUp.setOnClickListener(v -> goUp());
        root.addView(btnUp);

        Button btnHome = new Button(this);
        btnHome.setText("Home (External Storage)");
        btnHome.setOnClickListener(v -> enterDirectory(
                Environment.getExternalStorageDirectory()));
        root.addView(btnHome);

        setContentView(root);

        enterDirectory(Environment.getExternalStorageDirectory());
    }

    private void enterDirectory(File dir) {
        currentDir = dir;
        pathView.setText(dir.getAbsolutePath());
        items.clear();
        File parent = dir.getParentFile();
        if (parent != null && !parent.equals(dir)) {
            items.add(new FileItem("..", parent, true));
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                items.add(new FileItem(f.getName(), f, f.isDirectory()));
            }
            // Sort: directories first, then alphabetically.
            items.sort((a, b) -> {
                if (a.isDirectory != b.isDirectory) return a.isDirectory ? -1 : 1;
                return a.name.compareToIgnoreCase(b.name);
            });
        }
        adapter.notifyDataSetChanged();
    }

    private void goUp() {
        if (currentDir != null) {
            File parent = currentDir.getParentFile();
            if (parent != null) enterDirectory(parent);
        }
    }

    private void openFile(File file) {
        Toast.makeText(this, "Opening: " + file.getName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(android.net.Uri.fromFile(file), "*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No app to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    private static class FileItem {
        final String name;
        final File file;
        final boolean isDirectory;
        FileItem(String name, File file, boolean isDirectory) {
            this.name = name; this.file = file; this.isDirectory = isDirectory;
        }
    }
}
