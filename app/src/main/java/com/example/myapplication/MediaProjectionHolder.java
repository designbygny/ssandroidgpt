package com.example.myapplication;

import android.app.Application;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.content.Context;
import android.content.Intent;

public class MediaProjectionHolder extends Application {
    private static MediaProjection mediaProjection;
    private static MediaProjectionManager mediaProjectionManager;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public static MediaProjection getMediaProjection() {
        return mediaProjection;
    }

    public static void setMediaProjection(MediaProjection projection) {
        mediaProjection = projection;
    }

    public static MediaProjectionManager getMediaProjectionManager() {
        return mediaProjectionManager;
    }

    public static Context getAppContext() {
        return appContext;
    }
}