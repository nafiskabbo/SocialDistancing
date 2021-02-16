package com.kabbo.covid19.socialdistancing.ui.fragments;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.kabbo.covid19.socialdistancing.R;
import com.kabbo.covid19.socialdistancing.ui.activities.MainActivity;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class PermissionsFragment extends Fragment {

    public PermissionsFragment() {
        // Required empty public constructor
    }

    private SwitchCompat bluetoothSwitch, locationSwitch, gpsSwitch;

    private static final int REQUEST_ENABLE_BT = 101;
    private static final int REQUEST_LOCATION_PERMISSION = 111;
    private static final int REQUEST_ENABLE_GPS = 121;
    private BluetoothAdapter bluetoothAdapter;

    private FrameLayout frameLayout;
    private ImageButton forwardButton, previousBtn;

    private Handler handler;
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permissions, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        frameLayout = getActivity().findViewById(R.id.info_frame_layout);
        forwardButton = view.findViewById(R.id.forward_btn);
        previousBtn = view.findViewById(R.id.previous_btn);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        bluetoothSwitch = view.findViewById(R.id.bluetooth_switch);
        locationSwitch = view.findViewById(R.id.location_switch);
        gpsSwitch = view.findViewById(R.id.gps_switch);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                /// TODO: BLUETOOTH
                if (bluetoothAdapter == null) {
                    bluetoothSwitch.setClickable(false);
                } else {
                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothSwitch.setChecked(false);
                        bluetoothSwitch.setClickable(true);

                    } else {
                        bluetoothSwitch.setChecked(true);
                        bluetoothSwitch.setClickable(false);
                    }

                }
                /// TODO: BLUETOOTH

                /// TODO: LOCATION
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationSwitch.setChecked(true);
                    locationSwitch.setClickable(false);
                } else {
                    locationSwitch.setChecked(false);
                    locationSwitch.setClickable(true);
                }
                /// TODO: LOCATION

                /// TODO: GPS
                final LocationManager locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
                final boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (providerEnabled) {
                    gpsSwitch.setChecked(true);
                    gpsSwitch.setClickable(false);
                } else {
                    gpsSwitch.setChecked(false);
                    gpsSwitch.setClickable(true);
                }
                /// TODO: GPS
                handler.postDelayed(this, 8000);
            }
        };
        handler.post(runnable);


        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
                }
            }
        });

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                }
            }
        });

        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
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
            }
        });


        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothSwitch.isChecked() && locationSwitch.isChecked() && gpsSwitch.isChecked()) {
                    mainIntent();
                } else {
                    Toast.makeText(getContext(), "Kindly allow all permissions!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new WorkingProcessFragment());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {

            if (resultCode == RESULT_OK) {
                bluetoothSwitch.setChecked(true);
                bluetoothSwitch.setClickable(false);

            } else {   // RESULT_CANCELED
                Toast.makeText(getContext(), "Failed to turn on Bluetooth! Try again later!", Toast.LENGTH_SHORT).show();
                bluetoothSwitch.setChecked(false);
                bluetoothSwitch.setClickable(true);

            }

        } else if (requestCode == REQUEST_LOCATION_PERMISSION) {

            if (resultCode == RESULT_OK) {
                locationSwitch.setChecked(true);
                locationSwitch.setClickable(false);
            } else {   // RESULT_CANCELED
                Toast.makeText(getContext(), "Failed to get location permission! Try again later!", Toast.LENGTH_SHORT).show();
                locationSwitch.setChecked(false);
                locationSwitch.setClickable(true);
            }

        } else if (requestCode == REQUEST_ENABLE_GPS) {

            final LocationManager locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
            final boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (providerEnabled) {
                Toast.makeText(getContext(), "GPS is now enabled!", Toast.LENGTH_SHORT).show();
                gpsSwitch.setChecked(true);
                gpsSwitch.setClickable(false);
            } else {
                Toast.makeText(getContext(), "GPS not enabled! Unable to show user location!", Toast.LENGTH_SHORT).show();
                gpsSwitch.setChecked(false);
                gpsSwitch.setClickable(true);
            }

        }

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void mainIntent() {
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
        getActivity().finish();
    }

}