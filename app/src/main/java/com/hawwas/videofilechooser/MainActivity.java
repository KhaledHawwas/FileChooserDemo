package com.hawwas.videofilechooser;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.Intent.ACTION_OPEN_DOCUMENT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hawwas.videofilechooser.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final int REQUEST_CODE = 1;
    Uri tempUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE}, 1);
        }
        binding.button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("video/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_CODE);
        });
        Uri videoUri = getSavedUri();
        if (videoUri != null) {
            Cursor cursor = getContentResolver().query(videoUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int s= cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (s == -1) {
                    Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                String displayName = cursor.getString(s);
                cursor.close();
                binding.fileInfoTv.setText(displayName);
            }
        }
        binding.shareBtn.setOnClickListener(v -> accessVideo(getSavedUri()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri videoUri = data.getData();

tempUri=videoUri;
            // Persist permission
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            getContentResolver().takePersistableUriPermission(videoUri, takeFlags);
            // Save the URI for future access
            saveUri(videoUri);


        }

    }

    private void saveUri(Uri uri) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("videoUri", uri.toString());
        editor.apply();
    }
    private Uri getSavedUri() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String uriString = sharedPreferences.getString("videoUri", null);
        return uriString != null ? Uri.parse(uriString) : null;
    }

    private void accessVideo(Uri videoUri ) {
        if (videoUri != null) {
            shareVideo(videoUri);
        } else {
            Toast.makeText(this, "Video not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareVideo(Uri videoUri) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, videoUri);
        shareIntent.setPackage("com.whatsapp");

        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show();
        }
    }
}