package me.evis.mobile.util;

import android.content.Context;
import android.os.PowerManager;

public class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context ctx, String tag) {
        if (wakeLock != null) {
            wakeLock.release();
        }

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, tag);
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null) {
            wakeLock.release(); wakeLock = null;
        }
    }
}