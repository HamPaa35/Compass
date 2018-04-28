package com.example.frostbite.compass;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    //The deceleration of the variables for the two Views in the app
    ImageView compassNeedle;
    TextView compassDegree;

    //The initialisation of the instances of the classes used to manage the sensors
    private static SensorManager sensorService;
    private Sensor sensor;

    //The initialisation of the variable keeping track of the current number of degrees
    private float currentDegree = 0f;

    //A boolean that keeps track of the current status of the accuracy alert dialog,
    // to prevent more than one alert
    boolean accuracyAlert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The sensor is connected
        sensorService = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // The orientation sensor is connected
        sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        //The Activity view are connected to the layout components
        compassNeedle = (ImageView) findViewById(R.id.compass_needle);
        compassDegree = (TextView) findViewById(R.id.compass_degree);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // The app checks if an orientation sensor is present, and if not it alerts the user
        if(sensor != null){
            sensorService.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
        else {
            // The user gets an dialog alert that the app is not supported, and when pressed the app closes
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("NOT SUPPORTED");
            alertDialog.setMessage("THE DEVICE DOES NOT SUPPORT A COMPASS APPLICATION");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            alertDialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //The sensor is disconnected
        sensorService.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        //The value of the ORIENTATION sensor is stores in a variable
        int degree = Math.round(sensorEvent.values[0]);

        //The number of degrees is shown on screen
        compassDegree.setText(Integer.toString(degree) + " \u00b0");

        //A new instance of the RotateAnimation class is made, using the sensor values as parameters
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setDuration(0);
        ra.setFillAfter(true);

        // The ra animation i applied to the ImageView of the compass_rose
        compassNeedle.startAnimation(ra);

        currentDegree = -degree;

        //The app checks the accuracy of the sensor and if it is low or unreliable, it creates a dialog
        // and instructs the user on what to do in order to improve accuracy
        if(sensorEvent.accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW ||
                sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE && !accuracyAlert) {
            accuracyAlert = true;
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Accuracy Change");
            alertDialog.setMessage("Move the phone in a figure 8, with the top away from you");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            accuracyAlert = false;
                        }
                    });
            alertDialog.show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //Do nothing
    }
}
