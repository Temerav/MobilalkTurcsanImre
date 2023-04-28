package com.example.myapplication.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        new NotificationHandler(context).send("Nezd, ideje lenne megnezni hogy kedvelted e mar a kedvenc villanyhelyeidet :)");

    }
}