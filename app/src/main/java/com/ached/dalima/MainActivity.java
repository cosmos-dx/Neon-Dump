package com.ached.dalima;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button unlockButton;
    private Button startServiceButton;
    private Button stopServiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startServiceButton = findViewById(R.id.startServiceButton);
        stopServiceButton = findViewById(R.id.stopServiceButton);

        // Start AudioService when "Start Service" button is clicked
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAudioService();
            }
        });

        // Stop AudioService when "Stop Service" button is clicked
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAudioService();
            }
        });
    }

    private void startAudioService() {
        Intent serviceIntent = new Intent(this, AudioService.class);
        startService(serviceIntent);
        Toast.makeText(this, "Audio Service started", Toast.LENGTH_SHORT).show();
    }

    private void stopAudioService() {
        Intent serviceIntent = new Intent(this, AudioService.class);
        stopService(serviceIntent);
        Toast.makeText(this, "Audio Service stopped", Toast.LENGTH_SHORT).show();
    }
}
