package com.kabbo.covid19.socialdistancing.ui.fragments;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kabbo.covid19.socialdistancing.R;
import com.kabbo.covid19.socialdistancing.ui.activities.InformationActivity;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class SignupFragment extends Fragment {

    public SignupFragment() {
        // Required empty public constructor
    }

    private static final int REQUEST_ENABLE_BT = 1;
    private TextView alreadyHaveAnAccount;
    private FrameLayout parentframeLayout;
    private EditText fullName, emailID, password, confirmPassword;
    private Button signupbtn;
    private ProgressBar signupprogressbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private Dialog loadingDialog;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        ////// loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ////// loading dialog

        parentframeLayout = getActivity().findViewById(R.id.register_frame_layout);
        alreadyHaveAnAccount = view.findViewById(R.id.tv_already_have_an_account);
        fullName = view.findViewById(R.id.sign_up_full_name);
        emailID = view.findViewById(R.id.sign_up_email);
        password = view.findViewById(R.id.sign_up_password);
        confirmPassword = view.findViewById(R.id.sign_up_confirm_password);
        signupbtn = view.findViewById(R.id.sign_in_btn);
        signupprogressbar = view.findViewById(R.id.sign_up_progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigninFragment());
            }
        });

        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        emailID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null) {
                    checkBluetoothPermissions();

                } else {
                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
                    } else {
                        checkBluetoothPermissions();
                    }
                }
            }
        });
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentframeLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(fullName.getText())) {
            if (!TextUtils.isEmpty(emailID.getText())) {
                if (!TextUtils.isEmpty(password.getText()) && password.length() >= 8) {
                    if (!TextUtils.isEmpty(confirmPassword.getText())) {
                        signupbtn.setEnabled(true);
                        signupbtn.setTextColor(Color.parseColor("#ffffff"));
                    } else {
                        signupbtn.setEnabled(false);
                        signupbtn.setTextColor(Color.argb(50, 255, 255, 255));
                    }
                } else {
                    signupbtn.setEnabled(false);
                    signupbtn.setTextColor(Color.argb(50, 255, 255, 255));
                }
            } else {
                signupbtn.setEnabled(false);
                signupbtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            signupbtn.setEnabled(false);
            signupbtn.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    private void checkEmailAndPassword() {

        Drawable customErrorIcon = getResources().getDrawable(R.mipmap.custom_error_icon);
        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getIntrinsicHeight());

        if (emailID.getText().toString().matches(emailPattern)) {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {

                signupprogressbar.setVisibility(View.VISIBLE);

                signupbtn.setEnabled(false);
                signupbtn.setTextColor((Color.argb(50, 255, 255, 255)));

                firebaseAuth.createUserWithEmailAndPassword(emailID.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    loadingDialog.show();

                                    Map<String, Object> userdata = new HashMap<>();
                                    userdata.put("fullname", fullName.getText().toString());
                                    userdata.put("email", emailID.getText().toString());
                                    userdata.put("password", password.getText().toString());
                                    if (bluetoothAdapter != null) {
                                        userdata.put("btDeviceName", bluetoothAdapter.getName());
                                    } else {
                                        userdata.put("btDeviceName", "unsupported");
                                    }

                                    firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                            .set(userdata)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        ////////////////// MAPS
                                                        Map<String, Object> contactList = new HashMap<>();
                                                        contactList.put("list_size", (long) 0);
                                                        ////////////////// MAPS

                                                        firebaseFirestore.collection("USERS")
                                                                .document(firebaseAuth.getUid())
                                                                .collection("USER_DATA")
                                                                .document("CONTACT_LIST")
                                                                .set(contactList)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            loadingDialog.dismiss();
                                                                            mainIntent();

                                                                        } else {
                                                                            signupprogressbar.setVisibility(View.INVISIBLE);
                                                                            signupbtn.setEnabled(true);
                                                                            signupbtn.setTextColor(Color.parseColor("#ffffff"));
                                                                            String error = task.getException().getMessage();
                                                                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
//                                                        }


                                                    } else {
                                                        signupprogressbar.setVisibility(View.INVISIBLE);
                                                        signupbtn.setEnabled(true);
                                                        signupbtn.setTextColor(Color.parseColor("#ffffff"));
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                } else {
                                    signupprogressbar.setVisibility(View.INVISIBLE);
                                    signupbtn.setEnabled(true);
                                    signupbtn.setTextColor(Color.parseColor("#ffffff"));

                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.show();
                            }
                        });

            } else {
                confirmPassword.setError("Password doesn't match!", customErrorIcon);
            }
        } else {
            emailID.setError("Invaild Email!", customErrorIcon);
        }
    }

    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissioncheck = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissioncheck = getContext().checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                permissioncheck += getContext().checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            }
            if (permissioncheck != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                }
            }

            checkEmailAndPassword();


        } else {
            /// nothing to do
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {  // Match the request code
            if (resultCode == RESULT_OK) {
                checkBluetoothPermissions();

            } else {   // RESULT_CANCELED
                Toast.makeText(getContext(), "Error! Failed to turn on bluetooth! Try again later!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void mainIntent() {
        Intent mainIntent = new Intent(getActivity(), InformationActivity.class);
        startActivity(mainIntent);
        getActivity().finish();
    }

}