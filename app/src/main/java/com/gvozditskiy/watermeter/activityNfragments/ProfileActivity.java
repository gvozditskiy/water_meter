package com.gvozditskiy.watermeter.activityNfragments;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gvozditskiy.watermeter.R;
import com.gvozditskiy.watermeter.interfaces.OnSaveListener;
import com.gvozditskiy.watermeter.interfaces.RegisterInterface;
import com.gvozditskiy.watermeter.interfaces.RegisterSaveInterface;

public class ProfileActivity extends AppCompatActivity implements RegisterSaveInterface {


    private static final String TAG_PROF = "ProfileFragment";
    OnSaveListener onSaveListener;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Данные плательщика");
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
        btn = (Button) findViewById(R.id.activity_profile_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveListener.onSave();
            }
        });
    }


    @Override
    public void onRegisterSaveInterface(OnSaveListener onSaveListener) {
        this.onSaveListener = onSaveListener;
    }
}
