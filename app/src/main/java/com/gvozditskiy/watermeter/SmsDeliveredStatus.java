package com.gvozditskiy.watermeter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gvozditskiy.watermeter.interfaces.SendErrorCallback;

/**
 * Created by Alexey on 22.01.2017.
 */

public class SmsDeliveredStatus extends BroadcastReceiver {
    SendErrorCallback sendErrorCallback;
    Context context;

    public SmsDeliveredStatus(SendErrorCallback sendErrorCallback, Context context) {
        this.sendErrorCallback = sendErrorCallback;
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                sendErrorCallback.onSend();
                break;
        }
    }
}
