package com.example.maerizvi.robotexampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import orbotix.sphero.Sphero;
import orbotix.robot.base.Robot;
import orbotix.robot.base.RobotProvider;
import orbotix.sphero.ConnectionListener;
import orbotix.view.connection.SpheroConnectionView;

import android.bluetooth.BluetoothAdapter;


public class MainActivity extends Activity {

    SpheroConnectionView mSpheroConnectionView;
    Sphero mSphero;
    ConnectionListener mConnectionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find Sphero Connection View from layout file
        mSpheroConnectionView = (SpheroConnectionView) findViewById(R.id.sphero_connection_view);

        // This event listener will notify you when these events occur, it is up to you what you want to do during them
        mConnectionListener = new ConnectionListener() {
            @Override
            // The method to run when a Sphero is connected
            public void onConnected(Robot sphero) {
                // Hides the Sphero Connection View
                mSpheroConnectionView.setVisibility(View.INVISIBLE);
                // Cache the Sphero so we can send commands to it later
                mSphero = (Sphero) sphero;
                // You can add commands to set up the ball here, these are some examples

                // Set the back LED brightness to full
                mSphero.setBackLEDBrightness(1.0f);
                // Set the main LED color to blue at full brightness
                mSphero.setColor(0, 0, 255);

                // End examples
            }

            // The method to run when a connection fails
            @Override
            public void onConnectionFailed(Robot sphero) {
                // let the SpheroConnectionView handle or hide it and do something here...
            }

            // Ran when a Sphero connection drops, such as when the battery runs out or Sphero sleeps
            @Override
            public void onDisconnected(Robot sphero) {
                // Starts looking for robots
                mSpheroConnectionView.startDiscovery();
            }
        };

    }

    @Override
    protected void onResume() {
        // Required by android, this line must come first
        super.onResume();

        // Add the listener to the Sphero Connection View
        mSpheroConnectionView.addConnectionListener(mConnectionListener);

        // This line starts the discovery process which finds Sphero's which can be connected to
       mSpheroConnectionView.startDiscovery();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        mSpheroConnectionView.removeConnectionListener(mConnectionListener);
        if (mSphero != null) {
            mSphero.disconnect(); // Disconnect Robot properly
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void roll( View v )
    {

            drive( );

    }

    private void drive() {
        if(mSphero != null) {
            // Send a roll command to Sphero so it goes forward at full speed.
            mSphero.drive(0.0f, 0.5f);                                           // 1

            // Send a delayed message on a handler
            final Handler handler = new Handler();                               // 2
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // Send a stop to Sphero
                    mSphero.stop();                                               // 3
                }
            }, 500);

        }
    }

    private void blink(final boolean lit){

        if(mSphero != null){

            //If not lit, send command to show blue light, or else, send command to show no light
            if(lit){
                mSphero.setColor(0, 0, 0);                               // 1
            }else{
                mSphero.setColor(0, 0, 255);                             // 2
            }

            //Send delayed message on a handler to run blink again
            final Handler handler = new Handler();                       // 3
            handler.postDelayed(new Runnable() {
                public void run() {
                    blink(!lit);
                }
            }, 1000);
        }
    }
}
