package com.kabbo.covid19.socialdistancing.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.kabbo.covid19.socialdistancing.R;
import com.kabbo.covid19.socialdistancing.ui.fragments.SocialDistanceInfoFragment;

public class InformationActivity extends AppCompatActivity {

    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        frameLayout = findViewById(R.id.info_frame_layout);

        setDefaultFragment(new SocialDistanceInfoFragment());
    }

    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }


}