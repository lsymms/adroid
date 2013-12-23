package com.example.tracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: lsymms
 * Date: 12/18/13
 * Time: 1:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class TrackerAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Track.isAndroidIdNull()) {
            String androidId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.i("TrackerAlarm", "Android ID: " + androidId);
            Track.setAndroiId(androidId);
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");

        wl.acquire();

        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);



        Criteria criteria = new Criteria();
        List<String> providers = locationManager.getAllProviders();
        Location bestLocation = null;
        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            locationManager.requestSingleUpdate(provider, PendingIntent.getActivity(context, 0, new Intent(), 0));
            bestLocation = getBestLocation(location, bestLocation);
            Log.i("TrackerAlarm", "got location for " + provider + " " + location);

        }
        Track.setLocation(bestLocation);
        if (bestLocation != null) {
            Log.i("TrackerAlarm", "updated location: " + bestLocation.toString());
            Track.sendLocationAsynch();
        }

        wl.release();
    }

    private Location getBestLocation(Location newLocation, Location prevLocation) {
        Location bestLocation = null, mostRecent, leastRecent;

        if (prevLocation == null) {
            bestLocation = newLocation;
        } else if (newLocation == null) {
            bestLocation = prevLocation;
        } else {
            // time in ms
            if (newLocation.getTime() > prevLocation.getTime()) {
                mostRecent = newLocation;
                leastRecent = prevLocation;
            } else {
                mostRecent = prevLocation;
                leastRecent = newLocation;
            }

            if (mostRecent.getAccuracy() <= newLocation.getAccuracy()) {
                bestLocation = mostRecent;
            } else {

                float distance = mostRecent.distanceTo(leastRecent);

                if (distance > leastRecent.getAccuracy() + mostRecent.getAccuracy()) {
                    bestLocation = mostRecent;
                } else {

                    float timeDiff = mostRecent.getTime() - leastRecent.getTime();

                    // range = distance you could have traveled at 65 between location times
                    // 5 mph ~ 2.2 m/s = .0022 m/ms
                    double range = timeDiff * .0022
                            ;
                    // leastRecent is most accurate
                    if (range > leastRecent.getAccuracy()) {
                        bestLocation = mostRecent;
                    } else {
                        bestLocation = leastRecent;
                    }

                }

            }


        }

        return bestLocation;
    }

    public void SetAlarm(Context context)
    {
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        for (String provider : providers) {
            // empty pending intent
            locationManager.requestSingleUpdate(provider, PendingIntent.getActivity(context, 0, new Intent(), 0));
        }

        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, TrackerAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, 5000, pi); // times in ms
        Log.i("TrackerAlarm", "Set repeating alarm to keep sending location");
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, TrackerAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Log.i("TrackerAlarm", "Stopped location alarm");
    }
}
