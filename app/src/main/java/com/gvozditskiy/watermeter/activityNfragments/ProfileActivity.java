package com.gvozditskiy.watermeter.activityNfragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gvozditskiy.watermeter.R;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG_PROF = "ProfileFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        FragmentManager fm = getSupportFragmentManager();
        ProfileFragment fragment;
        FragmentTransaction ft = fm.beginTransaction();
        if (savedInstanceState==null) {
            fragment = new ProfileFragment();
        } else {
            fragment = (ProfileFragment) fm.findFragmentByTag(TAG_PROF);
        }
        ft.replace(R.id.activity_profile_container, fragment, TAG_PROF);
        ft.commit();
    }
}
