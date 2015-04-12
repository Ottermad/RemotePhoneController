package me.attwoodthomas.spacehack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class AccelerometerManager {

    private static Context aContext=null;


    /** Accuracy configuration */
    private static int interval = 199;

    private static Sensor sensor;
    private static SensorManager sensorManager;
    // you could use an OrientationListener array instead
    // if you plans to use more than one listener
    private static AccelerometerListener listener;

    /** indicates whether or not Accelerometer Sensor is supported */
    private static Boolean supported;
    /** indicates whether or not Accelerometer Sensor is running */
    private static boolean running = false;

    /**
     * Returns true if the manager is listening to orientation changes
     */
    public static boolean isListening() {
        return running;
    }

    /**
     * Unregisters listeners
     */
    public static void stopListening() {
        running = false;
        try {
            if (sensorManager != null && sensorEventListener != null) {
                sensorManager.unregisterListener(sensorEventListener);
            }
        } catch (Exception e) {}
    }

    /**
     * Returns true if at least one Accelerometer sensor is available
     */
    public static boolean isSupported(Context context) {
        aContext = context;
        if (supported == null) {
            if (aContext != null) {


                sensorManager = (SensorManager) aContext.
                        getSystemService(Context.SENSOR_SERVICE);

                // Get all sensors in device
                List<Sensor> sensors = sensorManager.getSensorList(
                        Sensor.TYPE_ACCELEROMETER);

                supported = new Boolean(sensors.size() > 0);



            } else {
                supported = Boolean.FALSE;
            }
        }
        return supported;
    }

    /**
     * Registers a listener and start listening
     * @param accelerometerListener
     *             callback for accelerometer events
     */
    public static void startListening( AccelerometerListener accelerometerListener )
    {

        sensorManager = (SensorManager) aContext.
                getSystemService(Context.SENSOR_SERVICE);

        // Take all sensors in device
        List<Sensor> sensors = sensorManager.getSensorList(
                Sensor.TYPE_ACCELEROMETER);

        if (sensors.size() > 0) {

            sensor = sensors.get(0);

            // Register Accelerometer Listener
            running = sensorManager.registerListener(
                    sensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_FASTEST);

            listener = accelerometerListener;
        }


    }

    /**
     * The listener that listen to events from the accelerometer listener
     */
    private static SensorEventListener sensorEventListener =
            new SensorEventListener() {

                private float x = 0;
                private float y = 0;
                private float z = 0;
                private float lastX = 0;
                private float lastY = 0;
                private float lastZ = 0;

                public void onAccuracyChanged(Sensor sensor, int accuracy) {}

                public void onSensorChanged(SensorEvent event) {

                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];

                    x = Math.round(x * 10) / 10.0f;
                    y = Math.round(y * 10) / 10.0f;
                    z = Math.round(z * 10) / 10.0f;

                    /*if (interval == 200) {
                        interval = 0;
                        float[] args = {x,y,z- lastZ};
                        new PostData().execute(args);
                    } else {
                        interval += 1;
                    }*/
                    interval = 0;
                    float[] args = {x - lastX,y - lastY,z - lastZ};
                    new PostData().execute(args);
                    // trigger change event
                    listener.onAccelerationChanged(x, y, z);

                    lastX = x;
                    lastY = y;
                    lastZ = z;
                }

            };

}

class PostData extends AsyncTask<float[], Void, Void> {

    protected Void doInBackground(float[]... coords) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://40db8fdf.ngrok.com");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("x", Float.toString(coords[0][0])));
            nameValuePairs.add(new BasicNameValuePair("y", Float.toString(coords[0][1])));
            nameValuePairs.add(new BasicNameValuePair("z", Float.toString(coords[0][2])));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
    }
}