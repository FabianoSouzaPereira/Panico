package br.com.panico;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.media.RemoteController;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;


public abstract class NotificationService
        extends NotificationListenerService implements RemoteController.OnClientUpdateListener {
    private static final int VERSION_SDK_INT = Build.VERSION.SDK_INT;

    public NotificationService(){}
    public static boolean supportsNotificationListenerSettings()
    {
        return VERSION_SDK_INT >= 19;
    }
    @SuppressLint("InlinedApi")
    @TargetApi(19)
    public static Intent getIntentNotificationListenerSettings(){
        final String ACTION_NOTIFICATION_LISTENER_SETTINGS;
        if (VERSION_SDK_INT >= 21) {
            ACTION_NOTIFICATION_LISTENER_SETTINGS = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
        } else
        ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

        return new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS);
    }

}
