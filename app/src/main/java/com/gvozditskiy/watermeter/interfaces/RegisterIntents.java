package com.gvozditskiy.watermeter.interfaces;

import android.app.PendingIntent;
import android.content.Intent;

/**
 * Created by Alexey on 26.12.2016.
 */

public interface RegisterIntents {
    void onRegisterIntent(PendingIntent sentPendingIntent, PendingIntent deliveredPendingIntent);
}
