package com.example.toshiba.locationfinder;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.TextView;
import android.widget.Toast;

public class Client extends AsyncTask<Void, Void, Void> {

    String dstAddress = "";
    int dstPort = 0;
    String phoneMac = "";
    String postcode = "";
    //String lat = "";
    //String lon = "";
    //TextView textResponse;


    ////// Create a HashMap to store each of the back end's query results
    //HashMap hm = new HashMap();



    public Client(String addr, int port, String phoneMac, String postcode) {
        dstAddress = addr;
        dstPort = port;
        this.phoneMac = phoneMac;
        this.postcode = postcode;

        //this.lat = lat;
        //this.lon = lon;
        //this.textResponse = textResponse;



    }

    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int bytesRead;
            InputStream inputStream = socket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            ////// Send current postcode (converted from lat/lon) to back end
            out.println(postcode);



            ////// Receive data from back end
            System.out.println("*****SYSTEM OUT********---->" + in.readLine() + "<----********SYSTEM OUT****************");


            //hm.put("PRICE", new String(in.readLine()));
            //hm.put("POSTCODE", new String(in.readLine()));
            //hm.put("ADDRESS", new String(in.readLine()));
            //hm.put("DATE", new String(in.readLine()));




            //Set set = hm.entrySet();
            //Iterator i = set.iterator();

            //while(i.hasNext())
            //{
            //    Map.Entry me = (Map.Entry) i.next();
            //    System.out.print(me.getKey() + ": ");
            //    System.out.println(me.getValue());
            //}



         /*
          * notice: inputStream.read() will block if no data return
          */
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                //response += byteArrayOutputStream.toString("UTF-8");
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        //textResponse.setText(response);
        super.onPostExecute(result);
    }


    /*
    public void test()
    {
        System.out.println(hm.get("POSTCODE") + "<--******HASHMAP*****");
    }
    */


}