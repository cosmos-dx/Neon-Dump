package com.ached.dalima;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AudioService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "AudioChannel";
    private MediaPlayer mediaPlayer;
    private ScreenUnlockReceiver screenUnlockReceiver;
    private ScreenLockReceiver screenLockReceiver;
    private Handler handler;
    private Runnable audioRunnable;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.test);
        mediaPlayer.setLooping(true);

        screenUnlockReceiver = new ScreenUnlockReceiver();
        screenLockReceiver = new ScreenLockReceiver();

        handler = new Handler();

        audioRunnable = new Runnable() {
            @Override
            public void run() {
                mediaPlayer.start();
            }
        };


        registerReceivers();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        Intent notificationIntent = new Intent(this, MainActivity.class);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Audio Service")
                .setContentText("Playing audio in the background")
                .setSmallIcon(R.drawable.ic_notification)
                .build();

        startForeground(NOTIFICATION_ID, notification);

        handler.post(audioRunnable);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAudio();
        unregisterReceivers();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManagerCompat.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        handler.removeCallbacks(audioRunnable);
        stopSelf();
    }

    class ScreenUnlockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                // Handle screen unlock event
                handler.post(audioRunnable);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.pause();
                    }
                }, 3000);
            }
        }
    }

    class ScreenLockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                mediaPlayer.pause(); //gadbadi when stop
            }
        }
    }

    private void registerReceivers() {
        IntentFilter unlockFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        IntentFilter lockFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);

        registerReceiver(screenUnlockReceiver, unlockFilter);
        registerReceiver(screenLockReceiver, lockFilter);
    }

    private void unregisterReceivers() {
        unregisterReceiver(screenUnlockReceiver);
        unregisterReceiver(screenLockReceiver);
    }
}
