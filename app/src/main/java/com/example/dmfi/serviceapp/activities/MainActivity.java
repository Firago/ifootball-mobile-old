package com.example.dmfi.serviceapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.dmfi.serviceapp.R;
import com.example.dmfi.serviceapp.services.SensorService;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unlockScreen();

        ToggleButton toggle = (ToggleButton) findViewById(R.id.serviceToggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(MainActivity.this, SensorService.class));
                    Toast.makeText(MainActivity.this, "SensorService START", Toast.LENGTH_SHORT).show();
                } else {
                    stopService(new Intent(MainActivity.this, SensorService.class));
                    Toast.makeText(MainActivity.this, "SensorService STOP", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PrefsActivity.class);
                startActivity(intent);
            }
        });

        toggle.setChecked(true);
    }

    private void unlockScreen() {
        //Get the window from the context
        Window window = this.getWindow();
        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        stopService(new Intent(MainActivity.this, SensorService.class));
    }
}
