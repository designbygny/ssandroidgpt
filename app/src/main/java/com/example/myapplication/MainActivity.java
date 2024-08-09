package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.media.projection.MediaProjection;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private MediaProjectionManager mediaProjectionManager;
    private ActivityResultLauncher<Intent> projectionActivityResultLauncher;
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        projectionActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent intent = new Intent(this, ScreenshotService.class);
                        intent.putExtra("EXTRA_RESULT_CODE", result.getResultCode());
                        intent.putExtra("EXTRA_RESULT_INTENT", result.getData());
                        startForegroundService(intent);  // Service'i foreground olarak başlatın
                    }
                });

        startMediaProjectionRequest();
    }

    private void startMediaProjectionRequest() {
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        projectionActivityResultLauncher.launch(intent);
    }

    private void startScreenshotService() {
        Intent serviceIntent = new Intent(this, ScreenshotService.class);
        startForegroundService(serviceIntent);
    }
}