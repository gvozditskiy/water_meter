package com.gvozditskiy.watermeter.activityNfragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;
import com.gvozditskiy.watermeter.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomScreen extends OnboarderActivity {
    List<OnboarderPage> onboarderPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboarderPages = new ArrayList<OnboarderPage>();

        // Create your first page
        OnboarderPage onboarderPage1 = new OnboarderPage(R.string.welcom_1_title, R.string.welcom_1_text, R.drawable.welcome_flats);
        OnboarderPage onboarderPage2 = new OnboarderPage(R.string.welcom_2_title, R.string.welcom_2_text, R.drawable.welcome_stats);
        OnboarderPage onboarderPage3 = new OnboarderPage(R.string.welcom_3_title, R.string.welcom_3_text, R.drawable.welcome_send);

        // You can define title and description colors (by default white)
//        onboarderPage1.setTitleColor(R.color.black);
//        onboarderPage1.setDescriptionColor(R.color.white);

        // Don't forget to set background color for your page
        onboarderPage1.setBackgroundColor(R.color.first_screen);
        onboarderPage2.setBackgroundColor(R.color.second_screen);
        onboarderPage3.setBackgroundColor(R.color.third_screen);

        // Add your pages to the list
        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);
        onboarderPages.add(onboarderPage3);

        // And pass your pages to 'setOnboardPagesReady' method
        setOnboardPagesReady(onboarderPages);

    }

    @Override
    public void onSkipButtonPressed() {
        // Optional: by default it skips onboarder to the end
        super.onSkipButtonPressed();
        // Define your actions when the user press 'Skip' button
    }

    @Override
    public void onFinishButtonPressed() {
        SharedPreferences sp = getSharedPreferences("welcome",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("watches",1);
        editor.commit();
        finish();
    }
}
