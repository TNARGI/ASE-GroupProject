package com.example.toshiba.locationfinder;

import android.location.Address;
import android.location.Geocoder;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

/**
 * Created by Owner on 09/11/2016.
 */
public class MainActivityTest {

    @Mock
    Geocoder geocoder = Mockito.mock(Geocoder.class);

    @InjectMocks private MainActivity mainActivity;

    @Test
    public void testMock() throws IOException {

        // Create predetermined output for geocoder
        Address address = Mockito.mock(Address.class);
        // Change return value to something expected
        when(address.getLatitude()).thenReturn(23.2);
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);

        // Define behaviour for mock geocoder
        when(geocoder.getFromLocationName("BN3 2RQ", 10)).thenReturn(addresses);

        mainActivity = new MainActivity();

        System.out.println(addresses.size());
        System.out.println(geocoder.getFromLocationName("BN3 2RQ", 10).get(0).getLatitude());
        // Problem here; complaining that getFromLocationName isn't mocked?
        double latitude = mainActivity.getLatitudeFromPostcode("BN3 2RQ");
        System.out.println(latitude);

        // Last parameter of assertEquals is degree of precision
        assertEquals(23.2, address.getLatitude(), 0);
        assertEquals(0.0, address.getLongitude(), 0);
        //assertEquals(mainActivity.getLatitudeFromPostcode("BN3 2RQ"), 23.2, 0);
    }
}