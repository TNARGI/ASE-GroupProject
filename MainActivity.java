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
    //public LatLng globalLoc = new LatLng(0,0);


    ////// Trial an attribute to identify when the client has finished querying the database
    private static boolean queryComplete;


    ////// Array list to store back end query response - ULTIMATELY USELESS BECAUSE IT ONLY CONTAINS LATLNG ELEMENTS, no prices etc.
    //private static ArrayList<LatLng> arrayListOfNearbyLatLon = new ArrayList<LatLng>();


    ////// Array list to store back end results in full address format
    public ArrayList<ArrayList<String>> outerArrayListOfNearbyAddresses = new ArrayList<>();
    public ArrayList<ArrayList<String>> alal = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //testArrayList.add("Hello");

        /*
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println(getPostcodeFromLatLon(50.86167011893622,-0.1255038719215335));
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        */

        ////// Dummy data for Mehmet's database
        //tempLat = 5.0;
        //tempLon = 5.0;
        //DB_Connections db=new DB_Connections().insert_data(2,"32.44","44.55","20OCT");


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
                // Clear nearby location array list
                outerArrayListOfNearbyAddresses.clear();

                // Fetch latitude/longitude and add a map marker
                double lat = loc.getLatitude();
                double lon = loc.getLongitude();
                addUserLocMapMarker(new LatLng(lat, lon)); //Commented out to test the map markers for back end query results


                // Set TextView widgets to display coordinates
                latitude.setText("Latitude: " + lat);
                longitude.setText("Longitude: " + lon);

                // Convert location coordinates to Strings for easier manipulation
                String sLat = Double.toString(lat);
                String sLon = Double.toString(lon);

                // Convert lat/lon to postcode
                String postcode = getPostcodeFromLatLon(lat, lon);


                // Create client to connect to sever
                Client myClient = new Client("35.160.161.98", 23456, getPhoneMac(), postcode);
                myClient.execute();


                // thread.sleep seemed to be the cause of map marker delay/map freezing
                /*
                try
                {
                    Thread.sleep(10000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                */



                if (myClient.alal.size() == 0)
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
                    /*
                    System.out.println("Building Array List!");
                    ArrayList<String> innerArrayListOfNearbyAddresses = new ArrayList<String>();
                    for(int i=0; i < myClient.alal.size(); i++)
                    {
                        System.out.println("i = " + i + "; alal size = " + myClient.alal.size());
                        for(int j=0; j<myClient.alal.get(i).size(); j++)
                        {
                            System.out.println("i = " + i + "; j = " + j + "; alal(i) size = " + myClient.alal.get(i).size());
                            System.out.println(myClient.alal.get(i).get(j));
                            innerArrayListOfNearbyAddresses.add(myClient.alal.get(i).get(j));
                        }
                        outerArrayListOfNearbyAddresses.add(innerArrayListOfNearbyAddresses);
                        System.out.println("inner: " + innerArrayListOfNearbyAddresses);
                        innerArrayListOfNearbyAddresses.clear();
                    }
                    for(int z = 0; z < outerArrayListOfNearbyAddresses.size(); z++){
                        System.out.println("outer " + z + ": " + outerArrayListOfNearbyAddresses.get(z));
                    }
                    */
                    alal = myClient.getAlal();
                    for(int i = 0; i < alal.size(); i++){
                        for(int j = 0; j < alal.get(i).size(); j ++){
                            System.out.println(alal.get(i).get(j));
                        }
                    }











                        //==========================================================================
                        // OLD APPROACH - Must be replaced in order to access ALL address elements (id, price, postcode etc.)

                        /*
                        ////// Print out all address elements in a list
                        for(int j = 0; j < myClient.alal.get(i).size(); j++){
                            System.out.println(words[j] + ": " + myClient.alal.get(i).get(j));
                            //results = results + (words[j] + ": " + myClient.alal.get(i).get(j) + "\n");
                        }


                        ////// Print out postcodes in a list
                        //System.out.println("Postcode "+ i + ": " + myClient.alal.get(i).get(2));

                        try
                        {
                            double nearbyLat = Double.parseDouble(getLatitudeFromPostcode(myClient.alal.get(i).get(2)));
                            double nearbyLon = Double.parseDouble(getLongitudeFromPostcode(myClient.alal.get(i).get(2)));

                        LatLng nearbyLatLng = new LatLng(nearbyLat, nearbyLon);
                        arrayListOfNearbyLatLon.add(nearbyLatLng);




                        //System.out.println("SIZE OF LATLNG LIST ---> " + arrayListOfNearbyLatLon.size());
                        //System.out.println(arrayListOfNearbyLatLon.get(i));



                        System.out.println("Lat: " + nearbyLat + "\t" + "Lon: " + nearbyLon);
                        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^");


                        }
                        catch (NumberFormatException ex)
                        {
                            System.out.println("X-X-X  NUMBER FORMAT EXCEPTION  X-X-X");
                        }
                        catch(ArrayStoreException ex)
                        {
                            System.out.println("X-X-X  ARRAY STORE EXCEPTION  X-X-X");
                        }
                        catch(IllegalArgumentException ex)
                        {
                            System.out.println("X-X-X  ILLEGAL ARGUMENT EXCEPTION  X-X-X");
                        }
                        catch(IllegalStateException ex)
                        {
                            System.out.println("X-X-X  ILLEGAL STATE EXCEPTION  X-X-X");
                        }
                        catch(NullPointerException ex)
                        {
                            System.out.println("X-X-X  NULL POINTER EXCEPTION  X-X-X");
                        }
                        catch(UnsupportedOperationException ex)
                        {
                            System.out.println("X-X-X  UNSUPPORTED OPERATION EXCEPTION  X-X-X");
                        }

                        */
                        //==========================================================================



                    }


                //testPrint();
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


        ////// Check if location permission has been given, and request if not
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            makeRequest();
            return;
        }


        ////// Request location updates from the LocationManager after location permission has been given
        locationManager.requestLocationUpdates("gps", 60000, 0, listener);


    } // End of onCreate


    ////// Add a map marker and zoom to current location
    public void addUserLocMapMarker(LatLng loc)
    {
        googleMap.clear();
        //LatLng loc = new LatLng(latitude, longitude);
        //LatLng foo = new LatLng(0,0);
        googleMap.addMarker(new MarkerOptions()
                .position(loc)
                .title("current location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .visible(true));
        ////// Temporarily commented out to avoid app trying to zoom and centre on every nearby postcode
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 12));
    } // End of addUserLocMapMarker



    ////// Attempting to separate out the other markers into dedicated method
    public void addNearbyLocMapMarkers()
    {

        if (alal.size() != 0)
        {
            for (int i = 0; i < 50; i++) // Currently set to 10 so as not to overload app. Should be i < alal.size()
            {
                {
                    //googleMap.clear();
                    String tPostcode = alal.get(i).get(2);
                    String tPrice = alal.get(i).get(1);
                    double tLat = getLatitudeFromPostcode(tPostcode);
                    double tLon = getLongitudeFromPostcode(tPostcode);
                    LatLng tLL = new LatLng(tLat,tLon);

                    if(Integer.parseInt(tPrice) < 250000)
                    {
                        googleMap.addMarker(new MarkerOptions()
                                .position(tLL)
                                .title("Postcode: " + alal.get(i).get(2) + ", " + "Price: £" + alal.get(i).get(1))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                                .visible(true));
                    }
                    else if(Integer.parseInt(tPrice) < 500000)
                    {
                        googleMap.addMarker(new MarkerOptions()
                                .position(tLL)
                                .title("Postcode: " + alal.get(i).get(2) + ", " + "Price: £" + alal.get(i).get(1))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                .visible(true));
                    }
                    else
                    {
                        googleMap.addMarker(new MarkerOptions()
                                .position(tLL)
                                .title("Postcode: " + alal.get(i).get(2) + ", " + "Price: £" + alal.get(i).get(1))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .visible(true));
                    }

                }
            }
        }
        else
        {
            System.out.println("alal EMPTY");
        }
    }



    /*
    ////// Temporary method to test whether address elements are being correctly loaded into outerArrayListOfNearbyAddresses
    public void testPrint()
    {

        if(outerArrayListOfNearbyAddresses.size() != 0)
        {

            for (int i = 0; i < outerArrayListOfNearbyAddresses.size(); i++)
            {
                for (int j = 0; j < outerArrayListOfNearbyAddresses.get(i).size(); j++)
                {
                    System.out.println(outerArrayListOfNearbyAddresses.get(i).get(j));
                    System.out.println(i + " " + j);
                }
                System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            }
        }
        else
        {
            System.out.println("CURRENTLY EMPTY");
        }


    }
    */


    ////// Called automatically when the map is ready to use
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(0,0)).title("current location"));
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

    } // End of onRequestPermissionResult


    /// Grant's Phone Mac = 02:00:00:00:00:00
    /// Grant's Android ID = f8f064a2a84700f1


    ////// Not currently being used - Functionality replaced by Client class
    /*
    public void sendRecord(String sendLongitude, String sendLatitude) throws Exception {
        String phoneTime = Long.toString(System.currentTimeMillis());
        String phoneMac = getPhoneMac();
        String hostName = "35.160.161.98";
        int portNumber = 23456;

        Socket appSocket = null;

        try
        {
            appSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(appSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(appSocket.getInputStream()));

            out.println(phoneMac);
            out.println(phoneTime);
            out.println(sendLatitude);
            out.println(sendLongitude);
        } catch (UnknownHostException e) {
            System.out.println("Don't know about host ");
        } catch (IOException e) {
            System.out.println("Couldn't get I/O for the connection to ");
        } finally {
            if (appSocket != null) {
                try {
                    appSocket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    } // End of sendRecord
    */


    public String getPhoneMac()
    {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String wlanMacAdd = wifiManager.getConnectionInfo().getMacAddress();
        return ("" + wlanMacAdd);
    } // End of getPhoneMac


    ////// POSTCODE HANDLING METHODS ///////////////////////////////////////////////////////////////
    public double getLatitudeFromPostcode(String postcode)
    {
        Geocoder geoCoder = new Geocoder(MainActivity.this, Locale.getDefault());
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
        List<Address> address = new ArrayList<>(); // Changed to ArrayList to try and solve map marker issue - may need to revert
        String returnResult = null;

        //if (geoCoder != null)
        //{
            try
            {
                address = geoCoder.getFromLocation(latitude, longitude, 5); // 5 indicates the number of results (houses) for that postcode
            } catch (IOException e1)
            {
                e1.printStackTrace();
            }
            if (address.size() > 0)
            {
                returnResult = address.get(2).getPostalCode();
                //return address.size();
            }
        //}
        return returnResult;
    } // End of getPostcodeFromLatLon


    // END OF POSTCODE HANDLING METHODS ////////////////////////////////////////////////////////////


} // End of MainActivity