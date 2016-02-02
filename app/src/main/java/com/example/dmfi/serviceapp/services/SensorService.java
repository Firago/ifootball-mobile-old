package com.example.dmfi.serviceapp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.example.dmfi.serviceapp.R;
import com.example.dmfi.serviceapp.rest.HttpStatusUpdateTask;
import com.example.dmfi.serviceapp.model.SensorData;
import com.example.dmfi.serviceapp.model.StatusMessage;

import java.util.concurrent.atomic.AtomicInteger;

public class SensorService extends Service implements SensorEventListener {

    private static final String TAG = SensorService.class.getSimpleName();
    private SharedPreferences preferences;
    private static String DEVICE_ID;
    // minimum vibration amplitude
    private static float VIBRATION_RMS_THRESHOLD;
    // single measurement micro-period (for RMS)
    private static long MEASUREMENT_INTERVAL;
    // status update period (milliseconds)
    private static long HANDLER_INTERVAL;
    // suspicions per period threshold
    private static int SUSPICION_THRESHOLD;
    // number of calm periods before changing status to free
    private static int CALM_PERIODS_THRESHOLD;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    // measurement micro-period start time
    private long startMeasurementTime;
    // micro-period sensor data
    private SensorData sensorData;

    // status
    private Boolean isFree;
    // number of "suspicions" - high amplitude vibrations
    private AtomicInteger suspicionCount;
    // number of "calm" periods
    private int calmPeriods;
    // handler for StatusMonitor runnable
    private Handler handler;
    // StatusMonitor runnable
    private StatusMonitor statusMonitor;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        initialize();
    }

    private void initialize() {
        Log.d(TAG, "initialize");
        // initialize shared preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // if preferences change, reload all parameters
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                loadParameters();
            }
        });

        loadParameters();
        // get android device id
        DEVICE_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        // by default, status is "free"
        isFree = Boolean.TRUE;
        // suspicionCount will be accessed from multiple threads, therefor it is an atomic integer
        suspicionCount = new AtomicInteger(0);
        // handler will execute StatusMonitor runnable periodically
        handler = new Handler();
        statusMonitor = new StatusMonitor();
        handler.postDelayed(statusMonitor, HANDLER_INTERVAL);
        // sensor data storage
        sensorData = new SensorData();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    private void loadParameters() {
        VIBRATION_RMS_THRESHOLD = Float.valueOf(preferences.getString(getString(R.string.vibration_rms_threshold_key), "0.1f"));
        MEASUREMENT_INTERVAL = Integer.valueOf(preferences.getString(getString(R.string.measurement_interval_key), "100"));
        HANDLER_INTERVAL = Integer.valueOf(preferences.getString(getString(R.string.handler_interval_key), "15000"));
        SUSPICION_THRESHOLD = Integer.valueOf(preferences.getString(getString(R.string.suspicion_threshold_key), "15"));
        CALM_PERIODS_THRESHOLD = Integer.valueOf(preferences.getString(getString(R.string.calm_periods_threshold_key), "3"));
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        // unregister accelerometer
        sensorManager.unregisterListener(this);
        // disable handler
        handler.removeCallbacks(statusMonitor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        // if sensor type is accelerometer, proceed to data processing
        if (Sensor.TYPE_ACCELEROMETER == sensor.getType()) {
            sensorData.addSensorValues(event.values);
            long currentTime = System.currentTimeMillis();
            // if measurement process runs longer than MEASUREMENT_INTERVAL, close the interval and process its data
            if (currentTime - startMeasurementTime > MEASUREMENT_INTERVAL) {
                // get maximum root mean square of amplitude
                double rms = sensorData.getRms();
                Log.d(TAG, String.format("RMS: %.3f", rms));
                // if amplitude is greater than threshold, note it as a "suspicion"
                if (rms > VIBRATION_RMS_THRESHOLD) {
                    int suspicions = suspicionCount.incrementAndGet();
                    Log.d(TAG, String.format("Vibration suspicions: %d", suspicions));
                }
                sensorData.clear();
                startMeasurementTime = currentTime;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class StatusMonitor implements Runnable {

        @Override
        public void run() {
            // update status, if needed
            updateStatus();
            // re-post runnable
            handler.postDelayed(this, HANDLER_INTERVAL);
        }
    }

    private synchronized void updateStatus() {
        // get and reset suspicionCount from previous period
        int lastSuspicionCount = suspicionCount.getAndSet(0);
        // check if it is higher than threshold
        boolean vibrationDetected = lastSuspicionCount >= SUSPICION_THRESHOLD;
        if (isFree && vibrationDetected) {
            handleVibration();
        } else if (!isFree && !vibrationDetected) {
            handleCalmness();
        }
        // update status using rest service
        sendStatus();
    }

    private void handleVibration() {
        changeStatus(Boolean.FALSE);
        calmPeriods = 0;
    }

    private void handleCalmness() {
        if (++calmPeriods > CALM_PERIODS_THRESHOLD) {
            changeStatus(Boolean.TRUE);
        }
    }

    private void changeStatus(Boolean isFree) {
        Log.d(TAG, String.format("changeStatus={%s}", isFree));
        this.isFree = isFree;
    }

    private void sendStatus() {
        // prepare status update message
        StatusMessage message = new StatusMessage(DEVICE_ID, isFree ? 1 : 0);
        // send status update request to the server
        new HttpStatusUpdateTask().execute(message);
    }
}
