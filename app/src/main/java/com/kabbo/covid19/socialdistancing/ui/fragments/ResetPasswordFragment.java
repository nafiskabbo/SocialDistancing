package com.kabbo.covid19.socialdistancing.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.kabbo.covid19.socialdistancing.R;

public class ResetPasswordFragment extends Fragment {

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    private FrameLayout parentFrameLayout;

    private TextView forgotPasswordGoBack, emailIconText;
    private EditText forgotPasswordRegisteredEmail;
    private Button resetPasswordBtn;

    private ViewGroup emailIconContainer;
    private ImageView emailIcon;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        parentFrameLayout = getActivity().findViewById(R.id.register_frame_layout);

        forgotPasswordRegisteredEmail = view.findViewById(R.id.forgot_password_email);
        forgotPasswordGoBack = view.findViewById(R.id.tv_forgot_password_go_back);
        resetPasswordBtn = view.findViewById(R.id.reset_password_btn);

        emailIconContainer = view.findViewById(R.id.forgot_password_email_icon_container);
        emailIconText = view.findViewById(R.id.forgot_password_email_icon_text);
        emailIcon = view.findViewById(R.id.forgot_password_email_icon);
        progressBar = view.findViewById(R.id.forgot_password_progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        forgotPasswordRegisteredEmail.addTextChangedListener(new TextWatcher() {
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

        forgotPasswordGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigninFragment());
            }
        });

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIconText.setVisibility(View.GONE);

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                resetPasswordBtn.setEnabled(false);
                resetPasswordBtn.setTextColor(Color.argb(50, 255,255,255));

                firebaseAuth.sendPasswordResetEmail(forgotPasswordRegisteredEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0,emailIcon.getWidth()/2,emailIcon.getHeight()/2);
                                    scaleAnimation.setDuration(100);
                                    scaleAnimation.setInterpolator(new AccelerateInterpolator());
                                    scaleAnimation.setRepeatMode(Animation.REVERSE);
                                    scaleAnimation.setRepeatCount(1);
                                    scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            emailIconText.setText("Recovery email sent successfully! Check your Inbox.");
                                            emailIconText.setTextColor(getResources().getColor(R.color.successGreen));
                                            TransitionManager.beginDelayedTransition(emailIconContainer);
                                            emailIconText.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                            emailIcon.setImageResource(R.mipmap.green_email);
                                        }
                                    });


                                    emailIcon.startAnimation(scaleAnimation);
                                } else {
                                    String error = task.getException().getMessage();

                                    emailIcon.setImageResource(R.mipmap.red_email);
                                    resetPasswordBtn.setEnabled(true);
                                    resetPasswordBtn.setTextColor(Color.rgb(255,255,255));

                                    emailIconText.setText(error);
                                    emailIconText.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailIconText.setVisibility(View.VISIBLE);

                                }
                                progressBar.setVisibility(View.GONE);

                            }
                        });
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if(!TextUtils.isEmpty(forgotPasswordRegisteredEmail.getText())) {
            resetPasswordBtn.setEnabled(true);
            resetPasswordBtn.setTextColor(Color.rgb(255,255,255));

        } else {
            resetPasswordBtn.setEnabled(false);
            resetPasswordBtn.setTextColor(Color.argb(50, 255,255,255));
        }
    }

}