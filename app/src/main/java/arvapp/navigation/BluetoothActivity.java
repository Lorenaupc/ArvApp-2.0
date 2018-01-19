package arvapp.navigation;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity  implements SensorEventListener {

    //Bluetooth dependant variables

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int BLUETOOTH_SEARCH_DEVICE = 2;
    private static final int RECIEVE_MESSAGE = 3;

    //ARVA mode variable, 1 --> TX // 2 --> RX
    private int mode = 1;
    private static int fragmentToDisplay = 0;

    //Multi-victim dependent
    private ArrayList<DVA> dvaArrayList = null;
    private static boolean toDelete = false;
    private int pointingDva = 0;
    private static int current = 0;
    private static int currentMax = 0;

    // Location variables //
    private double ourArvaLatitude = 0.0;
    private double ourArvaLongitude = 0.0;
    private double ourArvaAltitude = 0;

    private double receivingArvaLatitude = 0.0;
    private double receivingArvaLongitude = 0.0;

    private float bearing = 0;
    private float distance = 0;

    private LocationListener locationListener;

    // Orientation sensor variables
    private SensorManager mSensorManager;
    private float firstOrientation = -1;
    private float secondOrientation = -1;
    private float orientation = -1;

    //Bluetooth dependant variables
    private Handler h;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private BluetoothAdapter myBA = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice connectedDevice = null;
    private ConnectedThread mConnectedThread;
    int jaja = 0;

    private ImageView arrow = null;
    private TextView distanceTextView = null;
    private TextView latitude_info = null, longitude_info = null, altitude_info = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBle);
        setSupportActionBar(toolbar);

        // Orientation Sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        dvaArrayList = new ArrayList<>();
        //This is for SnackBar
        RelativeLayout v = (RelativeLayout) findViewById(R.id.relative);

        /*if (myBA == null) {
            final Snackbar snack = Snackbar.make(v, "Bluetooth not supported", Snackbar.LENGTH_LONG);
            snack.setActionTextColor(Color.CYAN);
            snack.setAction("CLOSE", new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    snack.dismiss();
                }
            });
            snack.show();
            //Toast.makeText(this, "Bluetooth not supportted", Toast.LENGTH_LONG).show();
            finish();
        } else {
            if(!myBA.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }*/

        setListener();

    }

    private void setListener(){
        Button aux = (Button) findViewById(R.id.ble_enable_butt);
        aux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), BluetoothDeviceListActivity.class);
                startActivityForResult(i, BLUETOOTH_SEARCH_DEVICE);
            }
        });

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        try{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch (SecurityException se){
            se.printStackTrace();
        }


        //receivePacket();
        Log.i("isds","HI");
        //recalculate();
        Log.i("HI", "AYUDA1");
       /*h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:													                        // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);					                        // create string from bytes array
                        sb.append(strIncom);												                        // append string
                        int endOfLineIndex = sb.indexOf("\r\n");							                        // determine the end-of-line
                        if (endOfLineIndex > 0) {                                                                   // if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);                                       // extract string
                            sb.delete(0, sb.length());                                                              // and clear
                            receivePacket();
                            String aux = "";
                            int checksum = 0;
                            try {
                                aux = sbprint.substring(0, 3);

                                checksum = Integer.parseInt(String.valueOf(sbprint.substring
                                        (endOfLineIndex-2, endOfLineIndex).charAt(0)));
                                int multiplicator = 1;
                                for(int i = 1; i < sbprint.substring(endOfLineIndex-2,
                                        endOfLineIndex).length(); i++){
                                    checksum += Integer.parseInt(String.valueOf
                                            (sbprint.substring(endOfLineIndex-2,
                                                    endOfLineIndex).charAt(i))) *
                                            (multiplicator * 10);
                                    multiplicator *= 10;
                                }

                            }catch (IndexOutOfBoundsException e){
                                e.printStackTrace();
                            }

                            if (aux.equalsIgnoreCase("DVA") && (checksum + 3 == sbprint.length())){
                                receivePacket();
                            }
                        }
                        break;
                }
            }
        };*/
        if (jaja == 0) {
            receivePacket();
            recalculate();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        RelativeLayout v = (RelativeLayout) findViewById(R.id.relative);
        final Snackbar snack;
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                switch(resultCode){
                    case RESULT_CANCELED:
                        //Toast.makeText(this, "Enable Bluetooth", Toast.LENGTH_LONG).show();
                        snack = Snackbar.make(v, "Must enable Bluetooth", Snackbar.LENGTH_LONG);
                        snack.setActionTextColor(Color.CYAN);
                        snack.setAction("CLOSE", new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                snack.dismiss();
                            }
                        });
                        snack.show();
                        break;
                    case RESULT_OK:
                        //Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_LONG).show();
                        snack = Snackbar.make(v, "Bluetooth enabled", Snackbar.LENGTH_LONG);
                        snack.setActionTextColor(Color.CYAN);
                        snack.setAction("CLOSE", new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                snack.dismiss();
                            }
                        });
                        snack.show();
                        break;
                    default:
                        snack = Snackbar.make(v, "Something gone wrong, try again", Snackbar.LENGTH_LONG);
                        snack.setActionTextColor(Color.CYAN);
                        snack.setAction("CLOSE", new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                snack.dismiss();
                            }
                        });
                        snack.show();
                        //Toast.makeText(this, "Something gone wrong, try again", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
            case BLUETOOTH_SEARCH_DEVICE:
                switch(resultCode){
                    case RESULT_CANCELED:
                        //Toast.makeText(this, "Link a DVA device", Toast.LENGTH_LONG).show();
                        snack = Snackbar.make(v, "Link a DVA device", Snackbar.LENGTH_LONG);
                        snack.setActionTextColor(Color.CYAN);
                        snack.setAction("CLOSE", new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                snack.dismiss();
                            }
                        });
                        snack.show();
                        break;
                    case RESULT_OK:
                        Bundle bundle = data.getExtras();
                        connectedDevice = bundle.getParcelable("BluetoothDevice");
                        /*CoordinatorLayout coord = (CoordinatorLayout) findViewById(R.id.coordinatorlayout);
                        snack = Snackbar.make(coord, "Connected", Snackbar.LENGTH_LONG);
                        snack.setActionTextColor(Color.CYAN);
                        snack.setAction("CLOSE", new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                snack.dismiss();
                            }
                        });
                        snack.show();*/
                        //Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        break;
                    default:
                        //Toast.makeText(this, "Something gone wrong, try again", Toast.LENGTH_LONG).show();
                        snack = Snackbar.make(v, "Something gone wrong, try again", Snackbar.LENGTH_LONG);
                        snack.setActionTextColor(Color.CYAN);
                        snack.setAction("CLOSE", new View.OnClickListener(){
                            @Override
                            public void onClick(View view){
                                snack.dismiss();
                            }
                        });
                        snack.show();
                        break;
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        BluetoothDevice device = connectedDevice;
        if(device != null){
            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Toast.makeText(this, "Error, failed to resume the activity", Toast.LENGTH_LONG).show();
                finish();
            }
            myBA.cancelDiscovery();

            // Establish the connection.  This will block until it connects.
            Log.i("connect", "...Connecting...");
            try {
                btSocket.connect();
                Log.i("ok", "....Connection ok...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    Toast.makeText(this, "Error, failed to resume the activity", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            Log.i("create socket", "...Create Socket...");
            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();
        }

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
    }

    // function: onPause -> closes the bluetooth socket
    @Override
    public void onPause() {
        super.onPause();

        if(connectedDevice != null){
            try {
                btSocket.close();
            } catch (IOException e) {
                Toast.makeText(this, "Error, failed to pause the activity", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    // function: onSensorChanged -> When sensor changes do appropriate things with data
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(firstOrientation == -1){
            firstOrientation = Math.round(event.values[0]);
            secondOrientation = -1;
        }
        else {
            secondOrientation = Math.round(event.values[0]);
        }

        if(current != pointingDva){
            recalculate();
        }
        else {
            updateArrow();
        }
        recalculate();
    }

    // function onAccuracyChanged -> not in use
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }


    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(String message) {

            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.i("SOCKET: ", "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }



    /* ---------------------------------------------------------------------------------------------
                                            Private classes

     ---------------------------------------------------------------------------------------------*/
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            ourArvaLatitude = loc.getLatitude();
            ourArvaLongitude = loc.getLongitude();
            Log.i("Ourarvalongitude: ", String.valueOf(ourArvaLongitude));
            Log.i("Ourarvalatitude: ", String.valueOf(ourArvaLatitude));
            ourArvaAltitude = loc.getAltitude();
            if (mConnectedThread != null){
                mConnectedThread.write(String.valueOf(ourArvaLatitude) + ":" + String.valueOf(ourArvaLongitude));
                if(arrow != null && distanceTextView != null){
                    arrow.setVisibility(View.VISIBLE);
                    distanceTextView.setVisibility(View.VISIBLE);
                }
            }
            /*else{
                Toast.makeText(MainActivity.this, String.valueOf(receivingArvaLatitude) + String.valueOf(receivingArvaLongitude), Toast.LENGTH_LONG).show();
            }*/
            recalculate();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle savedInstance){}

        @Override
        public void onProviderDisabled(String provider){}

        @Override
        public void onProviderEnabled(String provider){}
    }

    //Private function: recalculate -> Recalculates the direction of the arrow
    private void recalculate(){
        //Check if we are in the appropiate mode
        //if (current < 0 || current >= dvaArrayList.size()){
        /*if (current < 0){
            current = 0;
        }

        //Check if the actual victim has been rescued (multi-victim system)
       // if(toDelete && !dvaArrayList.isEmpty()){
        if(toDelete){
            //dvaArrayList.remove(current);
            currentMax--;
            current = 0;
        }
        toDelete = false;

        //Check if the user switched victim
        if(pointingDva != current){
            pointingDva = current;
            receivingArvaLongitude = dvaArrayList.get(pointingDva).getLongitude();
            receivingArvaLatitude = dvaArrayList.get(pointingDva).getLatitude();
        }
*/
        if(pointingDva != current){
            pointingDva = current;
            receivingArvaLongitude = dvaArrayList.get(pointingDva).getLongitude();
            receivingArvaLatitude = dvaArrayList.get(pointingDva).getLatitude();
        }

        if(ourArvaLatitude != 0.0 && receivingArvaLatitude != 0.0) {
            Location ourArva = new Location("OurArva");

            ourArva.setLatitude(ourArvaLatitude);
            ourArva.setLongitude(ourArvaLongitude);
            Location receivingArva = new Location("ReceivingArva");
            receivingArva.setLatitude(receivingArvaLatitude);
            receivingArva.setLongitude(receivingArvaLongitude);

            bearing = ourArva.bearingTo(receivingArva);
            distance = ourArva.distanceTo(receivingArva);
            Log.i("bearing", String.valueOf(bearing));
            orientation = (360+((bearing + 360) % 360)-firstOrientation) % 360;
        }
        else {
            firstOrientation = -1;
            secondOrientation = -1;
        }

        updateArrow();
    }

    // private function: updateArrow -> Rotates the R.id.arrow according to the different values we have
    private void updateArrow() {
        arrow = (ImageView) findViewById(R.id.rx_arrow);
        distanceTextView = (TextView) findViewById(R.id.rx_distance_textview);
        latitude_info = (TextView) findViewById(R.id.latitude);
        longitude_info = (TextView) findViewById(R.id.longitude);
        altitude_info = (TextView) findViewById(R.id.altitude);

        if (arrow != null && distanceTextView != null) {
            if (firstOrientation != -1 && secondOrientation != -1 && !dvaArrayList.isEmpty()) {
                if (orientation != -1) {
                    arrow.setRotation(0);
                    arrow.setRotation((orientation + (firstOrientation - secondOrientation)));
                } else {
                    arrow.setRotation(0);
                    arrow.setRotation(bearing);
                }

                if(altitude_info != null && longitude_info != null && latitude_info != null) {
                    altitude_info.setText(String.valueOf(ourArvaAltitude));
                    longitude_info.setText(String.valueOf(ourArvaLongitude));
                    latitude_info.setText(String.valueOf(ourArvaLatitude));
                }
                distanceTextView.setText(String.valueOf(Math.round(distance)) + " m");
            }
            else if(dvaArrayList.isEmpty()){
                distanceTextView.setText("No victims found!");
            }
        }
    }

    //Descomposing packet received
    private void receivePacket(){
        /*DVA newDva = new DVA();
        String id, type;
        int checksum = 0;
        boolean error = false;
        try {
            Log.i("sbprint: ", sbprint);
            type = String.valueOf(sbprint.charAt(3));
            id = sbprint.substring(4, 17);
            receivingArvaLatitude = Double.parseDouble(sbprint.substring(17,25));
            receivingArvaLongitude = Double.parseDouble(sbprint.substring(25,33));
            newDva.setDev_id(id);
            newDva.setLatitude(receivingArvaLatitude);
            newDva.setLongitude(receivingArvaLongitude);

        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            e.printStackTrace();
            error = true;
        }

        if (error == false) {
            addDvaToArray(newDva);
            currentMax++;
            recalculate();
        }*/

        //DVA newDva = new DVA();

        /*newDva.setDev_id("00000000");
        newDva.setLatitude(receivingArvaLatitude);
        newDva.setLongitude(receivingArvaLongitude);
        addDvaToArray(newDva);*/
        //currentMax++;
        //recalculate();
        DVA newDva = new DVA();
        receivingArvaLatitude = Double.parseDouble("41.221937");
        receivingArvaLongitude = Double.parseDouble("1.711072");
        newDva.setDev_id("000000");
        newDva.setLatitude(receivingArvaLatitude);
        newDva.setLongitude(receivingArvaLongitude);

        addDvaToArray(newDva);
        jaja = 1;
        currentMax++;
        recalculate();
    }

    private void addDvaToArray(DVA dvaObject){

        Location ourArva = new Location("Our DVA");
        Location distantArva = new Location("Distant DVA");
        float actDistance = 0, newDistance = 0;

        ourArva.setLatitude(ourArvaLatitude);
        ourArva.setLongitude(ourArvaLongitude);

        distantArva.setLatitude(dvaObject.getLatitude());
        distantArva.setLongitude(dvaObject.getLongitude());

        newDistance = ourArva.distanceTo(distantArva);
        boolean found = false;

        Log.i("dvaObject", dvaObject.getDev_id());

        if (!dvaArrayList.isEmpty()) {
            for (int i = 0; i < dvaArrayList.size() && found == false; i++) {
                if ((dvaArrayList.get(i).getDev_id()).equals(dvaObject.getDev_id())){
                    found = true;
                    dvaArrayList.get(i).setLatitude(dvaObject.getLatitude());
                    dvaArrayList.get(i).setLongitude(dvaObject.getLongitude());
                }
            }
            if (found == false) {
                for (int i = 0; i < dvaArrayList.size() && found == false; i++){

                    distantArva = new Location("distantArva");
                    distantArva.setLatitude((dvaArrayList.get(i).getLatitude()));
                    distantArva.setLongitude((dvaArrayList.get(i).getLongitude()));

                    actDistance = ourArva.distanceTo(distantArva);
                    if (newDistance < actDistance) {
                        dvaArrayList.add(i, dvaObject);
                        currentMax++;
                        found = true;
                        Toast.makeText(this, dvaArrayList.size() + " victims found",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        else if (dvaArrayList.isEmpty() || found == false){
            dvaArrayList.add(dvaObject);
            currentMax++;
            Toast.makeText(this, dvaArrayList.size() + " victims found", Toast.LENGTH_SHORT).show();
        }
    }

}
