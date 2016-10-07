package com.example.toshiba.geolocationfromscratch2;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{

    ////// Creating variables for the Button and TextViews
    private Button button;
    private TextView textView;



    ////// Creating variables for the location data. Don't fully understand the location classes.
    private LocationManager locationManager;
    private LocationListener locationListener;




    ////// Method called upon starting the app for the first time or after it was destroyed
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ////// Casting variables button and textView onto XML equivalent widgets
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.text_view);


        // Instantiating the locationManager
        locationManager = (LocationManager) MainActivity.this.getSystemService(MainActivity.this.LOCATION_SERVICE);


        // Method to check if location data is enabled and offering the option to turn it on
        isLocationEnabled();


        // Method to configure the button widget's onClick effects
        configureButton();


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








    ///// Give user the ability to turn on 'location' settings
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



    ////// Configuring the Button's function
    public void configureButton()
    {
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
        });

    }



} // End of MainActivity
