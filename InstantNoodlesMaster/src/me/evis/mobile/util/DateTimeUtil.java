package me.evis.mobile.util;

import me.evis.mobile.noodle.R;
import android.content.Context;
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
    
    public static String getLocalizedTimeString(Context context, int seconds) {
        final int[] dhms = calculateDhms(seconds);
        final int day = dhms[0];
        final int hour = dhms[1];
        final int minute = dhms[2];
        final int second = dhms[3];
        
        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(context.getString(R.string.time_days, day));
        }
        if (hour > 0) {
            sb.append(context.getString(R.string.time_hours, hour));
        }
        if (minute > 0) {
            sb.append(context.getString(R.string.time_minutes, minute));
        }
        if (second > 0 || seconds == 0) {
            sb.append(context.getString(R.string.time_seconds, second));
        }
        return sb.toString();
    }
}
