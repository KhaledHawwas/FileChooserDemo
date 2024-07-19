package com.hawwas.videofilechooser;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hawwas.videofilechooser.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final int REQUEST_CODE = 1;

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
        binding.button.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("video/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE || resultCode != RESULT_OK) {
            return;
        }
        if (data.getData() == null) {
            return;
        }
        String name = "";
        long size = 0;

        Cursor cr = getContentResolver().query(data.getData(), null, null, null, null);
        if (cr == null)
            return;
        if (cr.moveToFirst()) {
            int nameIndex = cr.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = cr.getColumnIndex(OpenableColumns.SIZE);
            if (nameIndex != -1) {
                name = cr.getString(nameIndex);
            }
            if (sizeIndex != -1) {
                size = cr.getLong(sizeIndex);
            }
        }
        binding.fileInfoTv.setText(name + " \n" + Util.getSize(size));
        shareVideo(data.getData());
        cr.close();

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