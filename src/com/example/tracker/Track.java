package com.example.tracker;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;

import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/18/13
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Track {
    private static final String RESOLVED_IP = "128.8.108.113";
    private static final int PORT = 8087;

    private static Location location = null;

    private static String androidId;


    public static void setAndroiId(String ID) {
        if (ID != null) {
            androidId = ID;
        }
    }

    public static boolean isAndroidIdNull() {
        return androidId == null;
    }
    
    public static void sendLocationAsynch() {
        AsyncLocFireAndForget task = new AsyncLocFireAndForget();
        task.execute();
    }

    
    private static class AsyncLocFireAndForget extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Log.i("TrackerAsync: ", "doInBackground");
            sendLocation();

            return null;
        }
    }

    public static void sendLocation() {
        Socket client;
        PrintWriter printwriter;

        if (location != null) {
            Log.i("Track","location = " + location);
            try {
                Log.i("Track","creating connection to server at " + RESOLVED_IP + ":" + PORT + "...");
                client = new Socket();
                client.setSoTimeout(1000); //ms
                client.connect(new InetSocketAddress(RESOLVED_IP, PORT));  //connect to server
                Log.i("Track","established connection to server");
                printwriter = new PrintWriter(client.getOutputStream(),true);

                String msg = androidId + ",update," + location.getLatitude() + "," + location.getLongitude() + "," + location.getTime();
                printwriter.write(msg);  //write the message to output stream
                Log.i("Track","sending message: '" + msg +"'...");
                printwriter.flush();
                printwriter.close();
                client.close();   //closing the connection
                Log.i("Track","Message sent succesfully, communication ended");
            } catch (SocketTimeoutException e) {
                Log.i("Track","timeout connecting to server, check your server, network and firewall");
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Track","unknown error sending message.  Error: " + e.getMessage());
            }
        } else {
            Log.i("Track","location is null");
        }
    }

    public static void setLocation(Location newLocation) {
        location = newLocation;
        if (location != null) {
            Log.i("Track", "set location to:" + location.toString());
        }
    }

}
