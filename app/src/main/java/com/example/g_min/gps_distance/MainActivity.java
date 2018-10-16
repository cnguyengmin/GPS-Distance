package com.example.g_min.gps_distance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {

    private TextView tv_instaV;
    private TextView tv_2pointsV;
    private TextView tv_overallV;
    private TextView tv_lat;
    private TextView tv_lon;
    private TextView tv_2pointsD;
    private TextView tv_totalD;

    private Button recordBttn;

    private double initialLat = 200.0;//dummy number because the range is btw -180 & 180
    private double initialLon= 200.0;
    private double prevLat = 200.0;
    private double prevLon = 200.0;
    private double currentLat, currentLon;
    private float btwDistance, totalDistance;


    private boolean permission_granted;
    private final static String LOGTAG =
            MainActivity.class.getSimpleName();

    private Observable location;
    private Location currLoc, prevLoc, initLoc;
    private LocationHandler handler = null;
    private final static int PERMISSION_REQUEST_CODE = 999;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tv_instaV = findViewById(R.id.tv_instaV);
        this.tv_2pointsV = findViewById(R.id.tv_2pointsV);
        this.tv_2pointsD = findViewById(R.id.tv_2pointsD);
        this.tv_overallV = findViewById(R.id.tv_overallV);
        this.tv_totalD = findViewById(R.id.tv_totalD);
        this.tv_lat = findViewById(R.id.tv_lat);
        this.tv_lon = findViewById(R.id.tv_lon);

        if (handler == null) {
            this.handler = new LocationHandler(this);
            this.handler.addObserver(this);
            System.out.println("TEST1");
        }


        // permissions
        if (checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_REQUEST_CODE
            );
        }


        this.recordBttn = findViewById(R.id.recordBttn);

        this.recordBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevLat = currentLat;
                prevLon = currentLon;
                System.out.println(prevLat);
                System.out.println(prevLon);
                System.out.println("Clicked");
            }
        });
    }

    public boolean isPermissions_granted(){ return permission_granted; }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // we have only asked for FINE LOCATION
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.permission_granted = true;
                Log.i(LOGTAG, "Fine location permisssion granted.");
            }
            else {
                this.permission_granted = false;
                Log.i(LOGTAG, "Fine location permisssion not granted.");
            }
        }

    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof LocationHandler) {
            currLoc = (Location) o;
            currentLat = currLoc.getLatitude();
            currentLon = currLoc.getLongitude();

            System.out.println("TEST2");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("TEST3");
                    if (initialLat == 200.0 && initialLon == 200.0){
                        System.out.println("TEST4");
                        initialLat = currentLat;
                        initialLon = currentLon;
                        initLoc = new Location("Initial Location");
                        initLoc.setLatitude(initialLat);
                        initLoc.setLongitude(initialLon);
                    }

                    if (prevLat == 200.0 && prevLon == 200.0){
                        btwDistance = 0.0f;
                        totalDistance = 0.0f;
                    } else {
                        prevLoc = new Location("Previous Location");
                        prevLoc.setLongitude(prevLon);
                        prevLoc.setLatitude(prevLat);
                        btwDistance = currLoc.distanceTo(prevLoc);
                        totalDistance = currLoc.distanceTo(initLoc);
                        System.out.println("got Previous");
                    }
                    System.out.println("chi");
                    tv_lat.setText(Double.toString(currentLat));
                    tv_lon.setText(Double.toString(currentLon));
                    tv_2pointsD.setText(Float.toString(btwDistance));
                    tv_totalD.setText(Float.toString(totalDistance));
                }
            });
        }

    }
}
