package de.timbolender.fefereader.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

/**
 * Broadcast receiver starting the update service. Intended for automatic launching of service after boot.
 */
public class BootServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = Objects.requireNonNull(intent.getAction());
        if(!action.equals("android.intent.action.BOOT_COMPLETED") &&
           !action.equals("android.intent.action.MY_PACKAGE_REPLACED"))
            return;

        UpdateService.startManualUpdate(context);
    }
}
