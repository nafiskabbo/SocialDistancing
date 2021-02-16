package com.kabbo.covid19.socialdistancing.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kabbo.covid19.socialdistancing.data.repositories.DBQueries;
import com.kabbo.covid19.socialdistancing.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 99;
    private static final int REQUEST_DISCOVER_BT = 56;
    private static final int REQUEST_ENABLE_GPS = 1001;
    public static List<String> deviceTempList = new ArrayList<>();

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    private BluetoothAdapter bluetoothAdapter;
    private TextView totalCount;
    private Button showDetailsBtn;
    private Dialog loadingDialog, signOutDialog;
    public Handler handler, avoidRepeatedHandler;
    public Runnable runnable, avoidRepeatedRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ////// loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////// loading dialog

        //////////// SIGN OUT DIALOG
        signOutDialog = new Dialog(this);
        signOutDialog.setContentView(R.layout.sign_out_dialog);
        signOutDialog.setCancelable(false);
        signOutDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        signOutDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //////////// SIGN OUT DIALOG

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        totalCount = findViewById(R.id.total_count);
        showDetailsBtn = findViewById(R.id.show_details_btn);

        /// TODO: load the Total Count Text from Database
        DBQueries.setTotalCount(MainActivity.this, totalCount);

        handler = new Handler();
        avoidRepeatedHandler = new Handler();

        ////// TODO: Task Scheduler using Handler and Runnable to repeat task to avoid Repeat of device
        avoidRepeatedRunnable = new Runnable() {
            @Override
            public void run() {
                deviceTempList.clear();      //// TODO: deleting temp list which have been detected after 10 minutes

                avoidRepeatedHandler.postDelayed(this, 600000);
            }
        };
        avoidRepeatedHandler.post(avoidRepeatedRunnable);
        ////// TODO: Task Scheduler using Handler and Runnable to repeat task to avoid Repeat of device


        ///////////// TODO: Task Scheduler using Handler and Runnable to repeat task to detect device
        runnable = new Runnable() {
            @Override
            public void run() {

                // TODO: checking if bluetooth is available or not
                if (bluetoothAdapter == null) {
                    // TODO: device doesn't support Bluetooth. So just go to main activity and do nothing as device can't detect anything.

                } else {

                    //// TODO: if bluetooth is available, then checking bluetooth is enabled or not
                    if (!bluetoothAdapter.isEnabled()) {
                        /////// TODO: if bluetooth is not enabled, then request to enable bluetooth
                        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);

                    } else {
                        ///// TODO: if bluetooth is enabled, then check bluetooth own device discovery or visibility is on or not
                        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

                            ////////// TODO: if own device discoverable is off, request to discover or visible own device!
                            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                            startActivityForResult(discoverableIntent, REQUEST_DISCOVER_BT);

                        } else {
                            ////////// TODO: if own device discoverable is on, check gps is enabled or not?
                            if (isGPSEnabled()) {
                                ///// TODO: if GPS is enabled, then go to detectContacts() method
                                detectContacts();
                            }
                        }

                    }

                }

                handler.postDelayed(this, 8000);  /// TODO: 8000 in milliseconds is the time when the loop with start again

            }
        };
        handler.post(runnable);
        ///////////// TODO: Task Scheduler using Handler and Runnable to repeat task to detect device


        showDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: go to history page
                Intent showContactsIntent = new Intent(MainActivity.this, ContactListActivity.class);
                startActivity(showContactsIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        //// TODO: sign out MENU
        if (id == R.id.sign_out) {
            signOutDialog.show();

            signOutDialog.findViewById(R.id.no_btn)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signOutDialog.dismiss();
                        }
                    });

            signOutDialog.findViewById(R.id.yes_btn)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signOutDialog.dismiss();

                            loadingDialog.show();

                            FirebaseAuth.getInstance().signOut();
                            DBQueries.clearData();

                            loadingDialog.dismiss();

                            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                            startActivity(registerIntent);
                            finish();
                        }
                    });
        }
        return true;
    }

    private boolean isGPSEnabled() {
        ////// TODO: checking GPS edibility on or off
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabled) {
            return true;
        } else {
            //// TODO: if GPS is off, then show Alert Dialog to enable GPS,
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work correctly. Please enable GPS.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, REQUEST_ENABLE_GPS);
                        }
                    }).setCancelable(false)
                    .show();
        }
        return false;
    }

    private void checkBluetoothPermissions() {
        /// TODO: check version of device and check then permissions
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissioncheck = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissioncheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                permissioncheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            }
            if (permissioncheck != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 499);
                }
            }

        } else {
            /// nothing to do
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {

            if (resultCode == RESULT_OK) {

                if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

                    ////////// TODO: if own device discoverable is off, request to discover or visible own device!
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivityForResult(discoverableIntent, REQUEST_DISCOVER_BT);

                } else {
                    if (isGPSEnabled()) {
                        ///// TODO: if GPS is enabled, then go to detectContacts() method
                        detectContacts();
                    }
                }

            } else {   // TODO: RESULT_CANCELED
                Toast.makeText(this, "Failed to turn on Bluetooth! Try again later!", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_DISCOVER_BT) {

            if (resultCode == RESULT_OK) {

                if (isGPSEnabled()) {
                    ///// TODO: if GPS is enabled, then go to detectContacts() method
                    detectContacts();
                }

            } else {   // RESULT_CANCELED
                Log.d("main", "Failed to get permission! Try again later!");
            }

        } else if (requestCode == REQUEST_ENABLE_GPS) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (providerEnabled) { /// TODO : result is permitted
                detectContacts();

            } else {   // TODO : RESULT_CANCELED
                Toast.makeText(this, "GPS not enabled! Unable to show user location!", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void detectContacts() {

        ///// TODO : first check if bluetooth is discovering other devices or not
        if (!bluetoothAdapter.isDiscovering()) {
            /* TODO: if bluetooth is not discovering other devices, firstly check BT permissions in manifest and then
                start discovery of bluetooth
           */

            checkBluetoothPermissions();

            bluetoothAdapter.startDiscovery();
            Log.d("main", "discovery started!");

            IntentFilter discoverDevicesFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, discoverDevicesFilter);
        }

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            /// TODO: if Device Found
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                /// TODO: Get the BluetoothDevice object from the Intent
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                final String name;
                String name1;

                assert device != null;
                name1 = device.getName(); ///TODO: device name

                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa 'at' dd.MM.yyyy");

                Calendar c1 = Calendar.getInstance();
                final String time = dateFormat.format(c1.getTime());

                /// TODO: recheck device name
                if (name1 == null) {
                    if (device.getName() != null) {
                        name1 = device.getName();
                    } else {
                        name1 = "Name unavailable!";
                    }
                }

                name = name1;

                /// TODO: get Location now!
                getCurrentLocation(name, time);

            }
        }
    };

    private void getCurrentLocation(final String name, final String time) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(2000);
        locationRequest.setInterval(4000);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();

                    if (location != null) {

                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                        try {
                            final String address, featureName, subLocality, locality, subAdminArea, adminArea, country, addressLine;

                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                            featureName = addresses.get(0).getFeatureName();
                            subLocality = addresses.get(0).getSubLocality();
                            locality = addresses.get(0).getLocality();
                            subAdminArea = addresses.get(0).getSubAdminArea();
                            adminArea = addresses.get(0).getAdminArea();
                            country = addresses.get(0).getCountryName();
                            addressLine = addresses.get(0).getAddressLine(0);

                            if (country != null) {
                                if (adminArea != null) {
                                    if (subAdminArea != null) {
                                        if (locality != null) {
                                            if (subLocality != null) {
                                                if (featureName != null) {
                                                    address = featureName + ", " + subLocality + ", " + locality + ", " + subAdminArea + ", " + adminArea + ", " + country;
                                                } else {
                                                    address = subLocality + ", " + locality + ", " + subAdminArea + ", " + adminArea + ", " + country;
                                                }
                                            } else {
                                                if (locality.equals(subAdminArea)) {
                                                    address = subAdminArea + ", " + adminArea + ", " + country;
                                                } else {
                                                    address = locality + ", " + subAdminArea + ", " + adminArea + ", " + country;
                                                }
                                            }
                                        } else {
                                            address = subAdminArea + ", " + adminArea + ", " + country;
                                        }
                                    } else {
                                        address = adminArea + ", " + country;
                                    }
                                } else {
                                    address = country;
                                }
                            } else {
                                if (addressLine == null || addressLine.isEmpty()) {
                                    address = "NOT FOUND!";
                                } else {
                                    address = addressLine;
                                }
                            }
                            // TODO: after getting location, go to databaseProcess() method to write on database
                            databaseProcess(name, address, time);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // do nothing as it will never come!
                    }

                }
            }, getMainLooper());

        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
            // TODO: It will never happen as we have gained access already from Working Process Fragment!
        }
    }

    private void databaseProcess(final String name, final String address, final String time) {
    /* TODO : checking into database that the device which have been detected is connected to our database or not by
        matching bluetooth device name captured in sign up process.
       If name matches with database Name, the further methods will be called.
       If name doesn't match, then no process will executed further.
    */

        FirebaseFirestore.getInstance()
                .collection("USERS")
                .whereEqualTo("btDeviceName", name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        long index = 0;

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            index++;

                                    /* TODO: index is used to avoid exception of founding twice account of same bluetooth device name.
                                        index will ensure to run the for loop for only one time
                                    */
                            if (index == 1) {

                                        /* TODO : Bluetooth device name matches with database name,
                                            then onSuccess, we will get information of that account email and userName from our database
                                        */

                                final String contactName, contactEmail;
                                contactName = snapshot.getString("fullname");
                                contactEmail = snapshot.getString("email");

                                final DocumentReference documentReference = FirebaseFirestore.getInstance()
                                        .collection("USERS")
                                        .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .collection("USER_DATA")
                                        .document("CONTACT_LIST");

                                // TODO: we will get the current USER Contact List now to write data on Firebase Firestore Database

                                documentReference.get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if (task.isSuccessful()) {
                                                    long list_size = task.getResult().getLong("list_size");

                                                    ///// TODO: we will use Map to write data on Database!
                                                    Map<String, Object> contactInfo = new HashMap<>();
                                                    contactInfo.put("contact_name_" + list_size, contactName);
                                                    contactInfo.put("contact_email_" + list_size, contactEmail);
                                                    contactInfo.put("contact_location_" + list_size, address);
                                                    contactInfo.put("contact_time_" + list_size, time);
                                                    contactInfo.put("list_size", (list_size + 1));

                                                    /* TODO: now we will check if the detected device is already in the detectedList in previous 10 minutes or not.
                                                    If it is detected in previous 10 minutes, it will not write anything to database to avoid repeat of one device.
                                                    */
                                                    if (!deviceTempList.contains(contactName)) {
                                                        documentReference.update(contactInfo)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {

                                                                            /// TODO: if the device is first time detected in last 10 minutes, then we will add it to detection list of HISTORY

                                                                            final Vibrator vibe = (Vibrator) (MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE));
                                                                            vibe.vibrate(800);
                                                                            Toast.makeText(MainActivity.this, contactName + " is around you!", Toast.LENGTH_LONG).show();

                                                                            deviceTempList.add(contactName); // TODO: temp list to avoid repeat detection

                                                                            DBQueries.setTotalCount(MainActivity.this, totalCount); // TODO: update totalCount Text

                                                                        } else {
                                                                            String error = task.getException().getMessage();
                                                                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error = e.getMessage();
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }


}