package me.evis.mobile.util;

import android.util.Log;

public class DateTimeUtil {

    private DateTimeUtil() {
        // No instance.
    }

    public static int calculateSeconds(int[] dhms) {
		if (dhms.length < 4) {
			Log.e(DateTimeUtil.class.getCanonicalName(), 
					"Input parameter does not match dd/hh/mm/ss array format.");
			return 0;
		}
		final int day = dhms[0];
		final int hour = dhms[1];
		final int minute = dhms[2];
		final int second = dhms[3];
		return day * 86400 + hour * 3600 + minute * 60 + second;
	}
    
    public static int[] calculateDhms(int seconds) {
        final int day = seconds / 86400;
        final int hour = seconds % 86400 / 3600;
        final int minute = seconds % 86400 % 3600 / 60;
        final int second = seconds % 86400 % 3600 % 60;
        return new int[] {day, hour, minute, second};
    }
}
