package com.example.toshiba.geolocationfromscratch2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{

    ////// Creating variables for the Button and TextViews
    private TextView textView1;
    private TextView textView2;


    ////// Creating variables for the location data. Don't fully understand the location classes.
    private LocationManager locationManager;
    private LocationListener locationListener;


    ////// Method called upon starting the app for the first time or after it was destroyed
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ////// Casting textViews onto XML equivalent widgets
        textView1 = (TextView) findViewById(R.id.text_view1);
        textView2 = (TextView) findViewById(R.id.text_view2);


        // Instantiating the locationManager
        locationManager = (LocationManager) MainActivity.this.getSystemService(MainActivity.this.LOCATION_SERVICE);

        //initialising location display
        Location location = new Location(locationManager.getLastKnownLocation("gps"));
        textView1.setText("Latitude: " + location.getLatitude());
        textView2.setText("Longitude: " + location.getLongitude());


        // Method to check if location data is enabled and offering
        // the option to turn it on using the showAlert method
        isLocationEnabled();





        // Instantiating the locationListener
        locationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                textView1.setText("Latitude: "+location.getLatitude());
                textView2.setText("Longitude: "+location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled(String provider)
            {

            }

            @Override
            public void onProviderDisabled(String provider)
            {

            }
        }; // End of locationListener











        ////// Requesting location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.INTERNET,
                        }, 10);
                return;
            }
        }



        ////// Requesting location updates
        locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);




    } // End of onCreate






    ////// Check if 'location' is enabled
    // Method Works
    private void isLocationEnabled()
    {
        if(!(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)))
        {
            showAlert();
        }
    } // End of isLocationEnabled






    ///// Give user the ability to turn on location data settings
    // Method Works
    private void showAlert()
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Enable Location").setMessage("Your location settings are 'off'\n" +
                "Please enable location data to use this app").setPositiveButton("Location Settings",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt)
                    {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface paramDialogInteface, int paramInt)
            {

            }
        });
        dialog.show();
    } // End of showAlert



} // End of MainActivity
