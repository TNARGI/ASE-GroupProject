package com.example.toshiba.locationfinder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{

    ////// Create variables for xml widgets
    private MapFragment fragmentMap;
    private GoogleMap googleMap;
    private TextView latitude;
    private TextView longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ////// Cast variables onto layout xml
        latitude = (TextView) findViewById(R.id.latitude);
        longitude = (TextView) findViewById(R.id.longitude);
        fragmentMap = (MapFragment) getFragmentManager().findFragmentById(R.id.map_fragment);

        ////// Initialise the map
        fragmentMap.getMapAsync(this);



        ////// Create a LocationManager and LocationListener for location updates
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener listener = new LocationListener()
        {

            @Override
            public void onLocationChanged(android.location.Location loc)
            {
                double lat = loc.getLatitude();
                double lon = loc.getLongitude();
                addMapMarker(lat, lon);

                latitude.setText("Latitude: " + lat);
                longitude.setText("Longitude: " + lon);

                //String sLat = Double.toString(lat);
                //String sLon = Double.toString(lon);

                /*
                try
                {
                    sendRecord(sLat,sLon);
                } catch (Exception e)
                {
                   Toast.makeText(MainActivity.this, "i am error", Toast.LENGTH_LONG).show();
                }

                //Timestamp date = new Timestamp();

                //Toast.makeText(MainActivity.this,date.getDate(),Toast.LENGTH_LONG).show();
                */
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {
            }

            @Override
            public void onProviderEnabled(String s)
            {
            }

            @Override
            public void onProviderDisabled(String s)
            {
                /*
                final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);

                dialog.setTitle("Enable Location Data")
                        .setMessage("Location data has disabled.\nPlease enable location data to use this app.")
                        .setPositiveButton("Go to Location Settings", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt)
                            {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                dialog.show();
                */
            }
        };





        ////// Check if location permission has been given and request if not
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            makeRequest();
            return;
        }



        ////// Request location updates from the LocationManager after location permission has been given
        locationManager.requestLocationUpdates("gps", 2000, 0, listener);



    } // End of onCreate




    ////// Add a map marker and zoom to current location
    void addMapMarker(Double latitude, Double longitude)
    {
        googleMap.clear();
        LatLng loc = new LatLng(latitude,longitude);
        googleMap.addMarker(new MarkerOptions().position(loc).title("current location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));
    } // End of addMapMarker





    ////// Called automatically when the map is ready to use
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap  = googleMap;
    } // End of onMapReady




    ////// Request location permission
    protected void makeRequest()
    {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 2);
    } // End of makeRequest



    ////// Called automatically upon permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Application cannot function without location permission.\n\nApplication terminated.", Toast.LENGTH_LONG).show();
            finish();
        }

        return;
    } // End of onRequestPermissionResult



    /*
    public void sendRecord(String sendLongitude, String sendLatitude) throws Exception {
        String phoneTime = Long.toString(System.currentTimeMillis());
        System.out.println("################################\n"+phoneTime+"\n############################");
        String phoneMac = "0.0.0.8";
        System.out.println("################################\n"+phoneMac+"\n############################");
        InetAddress hostName = InetAddress.getByName("10.1.42.254");
        System.out.println("################################\n"+hostName+"\n############################");
        int portNumber = 5432;
        System.out.println("################################\n"+portNumber+"\n############################");

        try (
                Socket appSocket = new Socket(hostName, portNumber);
                //PrintWriter out = new PrintWriter(appSocket.getOutputStream(), true);
        ) {
            System.out.println("################################\n"+"socket built"+"\n############################");
            String outString = phoneMac + "\n" + phoneTime + "\n" + sendLatitude + "\n" + sendLongitude;
                //out.print(outString);
        } catch (UnknownHostException e) {
            Toast.makeText(MainActivity.this, "Don't know about host " + hostName, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Couldn't get I/O for the connection to " + hostName, Toast.LENGTH_LONG).show();
        }
    }
    */

} // End of MainActivity