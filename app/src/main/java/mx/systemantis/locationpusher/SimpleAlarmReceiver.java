package mx.systemantis.locationpusher;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class SimpleAlarmReceiver extends WakefulBroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    private static long INTERVAL_THREE_MINUTES = AlarmManager.INTERVAL_FIFTEEN_MINUTES/5L;
    @Override
    public void onReceive(Context context, Intent intent) {
        // BEGIN_INCLUDE(alarm_onreceive)
        Intent service = new Intent(context, SimpleSchedulingService.class);

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
        // END_INCLUDE(alarm_onreceive)
    }

    // BEGIN_INCLUDE(set_alarm)
    /**
     * Sets a repeating alarm. When the alarm fires,
     * the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void setAlarm(Context context) {
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SimpleAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Wake up the device to fire the alarm in 3 minutes, and every 3 minutes
        // after that.

        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SimpleAlarmReceiver.INTERVAL_THREE_MINUTES,
                SimpleAlarmReceiver.INTERVAL_THREE_MINUTES, alarmIntent);

        // Enable {@code SimpleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, SimpleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, SimpleAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        // If the alarm has been set, cancel it.
        if (alarmManager!= null) {
            alarmManager.cancel(pendingIntent);
            Log.i("cancelAlarm(...)", "Alarm has been set. It should be cancelled now");
        }

        // Disable {@code SimpleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, SimpleBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    // END_INCLUDE(cancel_alarm)
}
