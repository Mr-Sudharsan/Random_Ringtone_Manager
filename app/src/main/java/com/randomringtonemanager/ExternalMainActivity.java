package com.randomringtonemanager;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ExternalMainActivity extends AppCompatActivity {

    private Button btn_choose_ring = findViewById(R.id.button_choose_ring_external);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_main);

        btn_choose_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ExternalMainActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("audio/mp3"); // specify "audio/mp3" to filter only mp3 files
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri audio = data.getData();
            Log.d("media", "onActivityResult: " + audio);
//            ringtones.add(audio);
//            ringtoneAdaptor.notifyDataSetChanged();
            RingtoneManager.setActualDefaultRingtoneUri(ExternalMainActivity.this,
                    RingtoneManager.TYPE_RINGTONE,audio);
        }
    }
}