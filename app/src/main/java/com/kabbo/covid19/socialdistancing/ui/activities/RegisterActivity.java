package com.kabbo.covid19.socialdistancing.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.kabbo.covid19.socialdistancing.R;
import com.kabbo.covid19.socialdistancing.ui.fragments.SigninFragment;
import com.kabbo.covid19.socialdistancing.ui.fragments.SignupFragment;

public class RegisterActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    public static boolean onResetPasswordFragment = false;
    public static boolean setSignUpFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        frameLayout = findViewById(R.id.register_frame_layout);

        if(setSignUpFragment) {
            setSignUpFragment = false;
            setDefaultFragment(new SignupFragment());
        } else {
            setDefaultFragment(new SigninFragment());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(onResetPasswordFragment) {
                onResetPasswordFragment = false;
                setFragment(new SigninFragment());
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

}