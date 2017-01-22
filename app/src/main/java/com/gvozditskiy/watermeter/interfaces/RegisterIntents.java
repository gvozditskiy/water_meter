package com.gvozditskiy.watermeter.interfaces;

import android.app.PendingIntent;

/**
 * Created by Alexey on 26.12.2016.
 */

public interface RegisterIntents {
    void onRegisterIntent(PendingIntent sentPendingIntent, PendingIntent deliveredPendingIntent);
}
