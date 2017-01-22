package com.gvozditskiy.watermeter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.gvozditskiy.watermeter.interfaces.SendErrorCallback;

/**
 * Created by Alexey on 22.01.2017.
 */

public class SmsStatusReciever extends BroadcastReceiver {
    SendErrorCallback sendErrorCallback;

    public SmsStatusReciever(SendErrorCallback sendErrorCallback) {
        this.sendErrorCallback = sendErrorCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(context, "SMS sent successfully", Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                if (sendErrorCallback!=null) {
                    sendErrorCallback.onSend();
                }
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                if (sendErrorCallback!=null) {
                    sendErrorCallback.onSend();
                }
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                if (sendErrorCallback!=null) {
                    sendErrorCallback.onSend();
                }
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                if (sendErrorCallback!=null) {
                    sendErrorCallback.onSend();
                }
                break;
        }
    }
}