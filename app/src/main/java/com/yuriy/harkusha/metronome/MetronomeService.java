package com.yuriy.harkusha.metronome;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;

public class MetronomeService extends Service {
    static final public String MESSAGE = "com.yuriy.harkusha.metronome.MetronomeService.MSG";
    static final public String RESULT = "com.yuriy.harkusha.metronome.MetronomeService.RSLT";
    public Context context = this;
    private boolean status;
    private int bpm;
    private int milliseconds = 0;
    private boolean vibrationOn;
    private boolean lightOn;
    private boolean soundOn;
    private int vibrationDuration = 100;
    private boolean serviceStatus;
    private LocalBroadcastManager broadcaster;

    public void onCreate() {
        serviceStatus = true;
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    public void onDestroy() {
        serviceStatus = false;
    }

    public void runMetronome() {
        Timer timer = new Timer("MetronomeTimer", true);
        TimerTask metronomeTimer = new TimerTask() {
            @Override
            public void run() {
                if (serviceStatus) {
                    sendSignalOnIndicator("on");
                    if (soundOn) {
                        sound();
                    }
                    if (vibrationOn) {
                        vibrate(vibrationDuration);
                    }
                    if (lightOn) {
                        light();
                    }
                } else ;
            }
        };
        if (milliseconds != 0) {
            timer.scheduleAtFixedRate(metronomeTimer, milliseconds, milliseconds);
        }
    }

    public int bmpToMilliseconds(int bmp) {
        int msInMinute = 60000;
        if (bpm != 0) {
            milliseconds = msInMinute / bmp;
        } else {
            milliseconds = 0;
        }
        return milliseconds;
    }

    public void vibrate(int duration) {
        Vibrator vibration = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibration.vibrate(duration);
    }

    public void light() {
        if (!status) {
            Camera camera = Camera.open();
            Camera.Parameters cameraParameters = camera.getParameters();
            cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(cameraParameters);
            camera.startPreview();
            status = true;
            camera.stopPreview();
            camera.release();
            status = false;
        }
    }

    public void sound() {
        MediaPlayer player = MediaPlayer.create(this, R.raw.tic);
        player.setLooping(false);
        player.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            bpm = intent.getIntExtra("bpm", 0);
            vibrationOn = intent.getBooleanExtra("vibrationOn", false);
            lightOn = intent.getBooleanExtra("lightOn", false);
            soundOn = intent.getBooleanExtra("soundOn", false);
            milliseconds = bmpToMilliseconds(bpm);
            saveSettings();
        } catch (NullPointerException ignored) {
            loadSettings();
        }
        runMetronome();
        return START_NOT_STICKY;   ////Or START_STICKY if you want to restart service after you close Activity
    }

    public void saveSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("bpm", bpm);
        editor.putBoolean("vibrationOn", vibrationOn);
        editor.putBoolean("lightOn", lightOn);
        editor.putBoolean("soundOn", soundOn);
        editor.commit();
    }

    public void loadSettings() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        bpm = settings.getInt("bpm", 0);
        vibrationOn = settings.getBoolean("vibrationOn", false);
        lightOn = settings.getBoolean("lightOn", false);
        soundOn = settings.getBoolean("soundOn", false);
    }

    public void sendSignalOnIndicator(String message) {
        Intent intent = new Intent(RESULT);
        if (message != null)
            intent.putExtra(MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }
}
