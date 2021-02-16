package com.kabbo.covid19.socialdistancing.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
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
import com.kabbo.covid19.socialdistancing.R;
import com.kabbo.covid19.socialdistancing.ui.activities.InformationActivity;
import com.kabbo.covid19.socialdistancing.ui.activities.RegisterActivity;

public class SigninFragment extends Fragment {

    public SigninFragment() {
        // Required empty public constructor
    }

    private FrameLayout parentFrameLayout;
    private TextView dontHaveAnAccount, forgotPassword;
    private EditText emailID, password;
    private Button signInBtn;
    private ProgressBar signInProgressBar;
    private FirebaseAuth firebaseAuth;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        parentFrameLayout = getActivity().findViewById(R.id.register_frame_layout);
        dontHaveAnAccount = view.findViewById(R.id.tv_dont_have_an_account);
        forgotPassword = view.findViewById(R.id.sign_in_forgot_password);
        emailID = view.findViewById(R.id.sign_in_email);
        password = view.findViewById(R.id.sign_in_password);
        signInBtn = view.findViewById(R.id.sign_in_btn);
        signInProgressBar = view.findViewById(R.id.sign_in_progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignupFragment());
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
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

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPasswords();
            }
        });


    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(emailID.getText())) {
            if (!TextUtils.isEmpty((password.getText()))) {
                signInBtn.setEnabled(true);
                signInBtn.setTextColor(Color.parseColor("#ffffff"));

            } else {
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            signInBtn.setEnabled(false);
            signInBtn.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    private void checkEmailAndPasswords() {
        if (emailID.getText().toString().matches(emailPattern)) {
            if (password.length() >= 8) {

                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50, 255, 255, 255));

                signInProgressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(emailID.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mainIntent();
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                                    signInBtn.setEnabled(true);
                                    signInBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                                    signInProgressBar.setVisibility(View.INVISIBLE);

                                }
                            }
                        });

            } else {
                Toast.makeText(getActivity(), "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
        }
    }

    private void mainIntent() {
        Intent mainIntent = new Intent(getActivity(), InformationActivity.class);
        startActivity(mainIntent);
        getActivity().finish();
    }
}