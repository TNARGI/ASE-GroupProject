package com.example.toshiba.locationfinder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{

    ////// Create variables for xml widgets
    private MapFragment fragmentMap;
    private GoogleMap googleMap;
    private TextView latitude;
    private TextView longitude;

    public ArrayList<ArrayList<String>> nearbyProperties = new ArrayList<>();

    private Geocoder geoCoder;

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

        geoCoder = new Geocoder(MainActivity.this, Locale.getDefault());

        ////// Create a LocationManager and LocationListener for location updates
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationListener listener = new LocationListener()
        {
            @Override
            public void onLocationChanged(android.location.Location loc)
            {
                // Fetch latitude/longitude and add a map marker (Refactor this out into separate method for testing)
                double curLatitude = loc.getLatitude();
                double curLongitude = loc.getLongitude();
                addUserLocMapMarker(new LatLng(curLatitude, curLongitude));

                // Set TextView widgets to display coordinates
                latitude.setText("Latitude: " + curLatitude);
                longitude.setText("Longitude: " + curLongitude);

                // Convert location coordinates to Strings for easier manipulation
                String sLat = Double.toString(curLatitude);
                String sLon = Double.toString(curLongitude);

                // Convert lat/lon to postcode
                String postcode = getPostcodeFromLatLon(curLatitude, curLongitude);

                // Create client to connect to server 35.160.161.98 for main server
                Client myClient = new Client("35.163.88.99", 23456, getPhoneMac(), postcode);
                // Will terminate without doing anything while server is offline
                myClient.execute();

                //Won't be called while offilne (list initialised but nothing added)
                if (myClient.nearbyProperties.size() == 0)
                {
                    System.out.println("||| ARRAY LIST EMPTY |||");
                } else
                {
                    System.out.println("||| ARRAY LIST FULL |||");

                    String[] words = new String[5];
                    //words[0] = "OMNOMNOM";
                    words[0] = "id";
                    words[1] = "price";
                    words[2] = "postcode";
                    words[3] = "address";
                    words[4] = "date";

                    String results = new String("");

                    nearbyProperties = myClient.getNearbyProperties();
                    for(int i = 0; i < nearbyProperties.size(); i++){
                        for(int j = 0; j < nearbyProperties.get(i).size(); j ++){
                            System.out.println(nearbyProperties.get(i).get(j));
                        }
                    }
                }
                System.out.println("LOCATION CHANGED.");
                addNearbyLocMapMarkers();
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
            }
        };




        ////// Check if location permission has been given, and request if not
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            makeRequest();
            return;
        }



        ////// Request location updates from the LocationManager after location permission has been given
        locationManager.requestLocationUpdates("gps", 2000, 0, listener);



    } // End of onCreate


    ////// Add a map marker and zoom to current location
    public void addUserLocMapMarker(LatLng loc)
    {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(loc)
                .title("current location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .visible(true));
        ////// Temporarily commented out to avoid app trying to zoom and centre on every nearby postcode
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));
    } // End of addUserLocMapMarker



    ////// Attempting to separate out the other markers into dedicated method
    public void addNearbyLocMapMarkers()
    {

        if (nearbyProperties.size() != 0)
        {
            for (int i = 0; i < 50; i++) // Currently set to 10 so as not to overload app. Should be i < alal.size()
            {
                {
                    //googleMap.clear();
                    String tPostcode = nearbyProperties.get(i).get(2);
                    String tPrice = nearbyProperties.get(i).get(1);
                    double tLat = getLatitudeFromPostcode(tPostcode);
                    double tLon = getLongitudeFromPostcode(tPostcode);
                    LatLng tLL = new LatLng(tLat,tLon);

                    if(Integer.parseInt(tPrice) < 250000)
                    {
                        googleMap.addMarker(new MarkerOptions()
                                .position(tLL)
                                .title("Postcode: " + nearbyProperties.get(i).get(2) + ", " + "Price: £" + nearbyProperties.get(i).get(1))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                .visible(true));
                    }
                    else if(Integer.parseInt(tPrice) < 500000)
                    {
                        googleMap.addMarker(new MarkerOptions()
                                .position(tLL)
                                .title("Postcode: " + nearbyProperties.get(i).get(2) + ", " + "Price: £" + nearbyProperties.get(i).get(1))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                .visible(true));
                    }
                    else
                    {
                        googleMap.addMarker(new MarkerOptions()
                                .position(tLL)
                                .title("Postcode: " + nearbyProperties.get(i).get(2) + ", " + "Price: £" + nearbyProperties.get(i).get(1))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .visible(true));
                    }

                }
            }
        }
        else
        {
            System.out.println("nearbyProperties EMPTY");
        }
    }




    ////// Called automatically when the map is ready to use
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
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



    /// Grant's Phone Mac = 02:00:00:00:00:00
    /// Grant's Android ID = f8f064a2a84700f1



    public String getPhoneMac()
    {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String wlanMacAdd = wifiManager.getConnectionInfo().getMacAddress();
        return ("" + wlanMacAdd);
    } // End of getPhoneMac


    ////// POSTCODE HANDLING METHODS ///////////////////////////////////////////////////////////////
    public double getLatitudeFromPostcode(String postcode)
    {
        List<Address> address = new ArrayList<>(); // Changed to ArrayList to try and solve map marker issue - may need to revert
        String returnResult = null;
        double lat = 0.0;

        //if (geoCoder != null)
        //{
        try
        {
            address = geoCoder.getFromLocationName(postcode, 10);
        } catch (IOException e1)
        {
            e1.printStackTrace();
        }
        if (address.size() > 0)
        {
            Address first = address.get(0);
            lat = first.getLatitude();
            String latString = Double.toString(lat);
            returnResult = latString;

        }
        //}
        //return returnResult;
        return lat;
    } // End of getLatitudeFromPostcode


    public double getLongitudeFromPostcode(String postcode)
    {
        Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> address = new ArrayList<>(); // Changed to ArrayList to try and solve map marker issue - may need to revert
        String returnResult = null;
        double lon = 0.0;

        //if (geoCoder != null)
        //{
        try
        {
            address = geoCoder.getFromLocationName(postcode, 10);
        } catch (IOException e1)
        {
            e1.printStackTrace();
        }
        if (address.size() > 0)
        {
            Address first = address.get(0);
            lon = first.getLongitude();
            String lonString = Double.toString(lon);
            returnResult = lonString;
        }
        //}
        //return returnResult;
        return lon;
    } // End of getLongitudeFromPostcode


    public String getPostcodeFromLatLon(double latitude, double longitude)
    {
        Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<Address> address = null;
        String returnResult = null;

        if (geoCoder != null)
        {
            try
            {
                //Returns list of addresses that describe current location (are closest)
                address = geoCoder.getFromLocation(latitude, longitude, 5); // 5 indicates the number of results (houses) for that postcode
            } catch (IOException e1)
            {
                e1.printStackTrace();
            }
            if (address.size() > 0)
            {
                returnResult = address.get(2).getPostalCode().toString();
                // ADDED IN TO RETURN A POSTCODE FROM LIST
                System.out.println("Current postcode: " + returnResult);
                return returnResult;
                //return address.size();
            }
        }
        return returnResult;
    } // End of getPostcodeFromLatLon

    // END OF POSTCODE HANDLING METHODS ////////////////////////////////////////////////////////////

    void setGeoCoder(Geocoder geoCoder) { }

} // End of MainActivity