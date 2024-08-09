package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import java.nio.ByteBuffer;

public class ScreenshotService extends Service {
    private static final String CHANNEL_ID = "screenshot_service_channel";
    private static final long SCREENSHOT_INTERVAL = 3000; // 3 saniye
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private Handler handler;
    private Runnable screenshotRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // Bildirim oluşturarak foreground service olarak başlatma
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Screenshot Service")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.ic_notification) // Bildirim ikonunu buraya ekleyin
                .build();

        startForeground(1, notification);

        // MediaProjection ve VirtualDisplay kurulumu
        initializeMediaProjection();
    }

    private void initializeMediaProjection() {
        mediaProjection = MediaProjectionHolder.getMediaProjection();
        if (mediaProjection == null) {
            Log.e("ScreenshotService", "MediaProjection not available.");
            stopSelf();
            return;
        }

        setUpVirtualDisplay();

        handler = new Handler();
        screenshotRunnable = new Runnable() {
            @Override
            public void run() {
                takeScreenshot();
                handler.postDelayed(this, SCREENSHOT_INTERVAL);
            }
        };
        handler.post(screenshotRunnable);
    }

    private void setUpVirtualDisplay() {
        int displayWidth = 1080; // Ekran genişliği
        int displayHeight = 1920; // Ekran yüksekliği
        int screenDensity = getResources().getDisplayMetrics().densityDpi;

        imageReader = ImageReader.newInstance(displayWidth, displayHeight, ImageFormat.YUV_420_888, 2);
        imageReader.setOnImageAvailableListener(this::processImage, null);

        virtualDisplay = mediaProjection.createVirtualDisplay(
                "ScreenshotService",
                displayWidth, displayHeight, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), null, null
        );
    }

    private void takeScreenshot() {
        // Bu metodun içi boş; ekran görüntüsünü işlemek için processImage metodunu kullanıyoruz
        // Ekran görüntüsünü almak için imageReader kullanılarak otomatik olarak işleme yapılacak
    }

    private void processImage(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        if (image != null) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int width = image.getWidth();
            int height = image.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            buffer.rewind();
            bitmap.copyPixelsFromBuffer(buffer);

            image.close();

            // Görsel eşleştirme ve tıklama işlemi
            if (findImageInBitmap(bitmap, R.drawable.target_image)) {
                // Tıklama işlemi yapın
                clickAt(100, 200); // Örneğin (100, 200) koordinatına tıklayın
            }
        }
    }

    private boolean findImageInBitmap(Bitmap screenshot, int targetImageResId) {
        Bitmap targetBitmap = BitmapFactory.decodeResource(getResources(), targetImageResId);

        // Görsel eşleştirme işlemi yapın
        // Bu örnekte basit bir eşleştirme işlemi yapılmamaktadır. Burada kendi eşleştirme algoritmanızı kullanın.

        return false; // Eşleşme bulunduysa true döndürmelisiniz
    }

    private void clickAt(int x, int y) {
        try {
            Runtime.getRuntime().exec("input tap " + x + " " + y);
        } catch (Exception e) {
            Log.e("ScreenshotService", "Click failed: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
        if (imageReader != null) {
            imageReader.close();
        }
        if (handler != null && screenshotRunnable != null) {
            handler.removeCallbacks(screenshotRunnable);
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Foreground service başlatıldığında yapılacak işlemler
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Screenshot Service Channel";
            String description = "Channel for Screenshot Service";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
