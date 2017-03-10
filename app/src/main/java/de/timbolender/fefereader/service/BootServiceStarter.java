package de.timbolender.fefereader.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver starting the update service. Intended for automatic launching of service after boot.
 */
public class BootServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UpdateService.startManualUpdate(context);
    }
}
