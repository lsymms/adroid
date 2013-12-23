package com.example.tracker;


import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class MyActivity extends Activity {
    private ScrollView scrollView;
    private TextView console;
    private View.OnClickListener msgTapListener;
    private static final String LOG_TAG = "Android Tracker: ";




    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        init();
    }

    private void init() {
//        refresh = defaultRefresh;
        scrollView = (ScrollView) this.findViewById(R.id.ScrollView);
        console = (TextView) findViewById(R.id.console);
        console.setMovementMethod(new ScrollingMovementMethod());
//
//
//        // Define and attach listeners
//        msgTapListener = new View.OnClickListener()  {
//            public void onClick(View v) {
//                //pauseToggle();
//            }
//        };
        console.setOnClickListener(msgTapListener);
        console.setText("Initialized");

        TrackerAlarm trackerAlarm = new TrackerAlarm();
        trackerAlarm.SetAlarm(getApplicationContext());
    }



    public void closeButtonClicked(View view) {
        TrackerAlarm trackerAlarm = new TrackerAlarm();
        trackerAlarm.CancelAlarm(getApplicationContext());
        finish();
    }


    private void log(String text) {

        Message msg = new Message();
        msg.obj = text;
        logHandler.sendMessage(msg);
    }

    Handler logHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String text = (String)msg.obj;
            console.append("\n" + text);
            Log.i(LOG_TAG, text);
            scrollView.post(new Runnable()
            {
                public void run()
                {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    };

}
