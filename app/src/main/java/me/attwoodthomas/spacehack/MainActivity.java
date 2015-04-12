package me.attwoodthomas.spacehack;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements AccelerometerListener{

    protected Button mStartStopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartStopButton = (Button) findViewById(R.id.button);

        mStartStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccelerometerManager.isSupported(MainActivity.this)) {
                    if (stopAccelerometer()) {
                        //Start Accelerometer Listening
                        AccelerometerManager.startListening(MainActivity.this);
                        mStartStopButton.setText("STOP");
                    }
                }
            }
        });
        // Check onResume Method to start accelerometer listener
    }

    public void onAccelerationChanged(float x, float y, float z) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStop() {
        super.onStop();
        stopAccelerometer();


    }

    private boolean stopAccelerometer() {
        //Check device supported Accelerometer sensor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();
            mStartStopButton.setText("START");
            return false;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Sensor", "Service  distroy");

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Stop Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(getBaseContext(), "onDestroy Accelerometer Stopped",
                    Toast.LENGTH_SHORT).show();
        }

    }

}