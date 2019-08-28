package com.prodatadoctor.CoolStickyNotes.Notifications.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.prodatadoctor.CoolStickyNotes.Notifications.Worker.BootCompleteWorker;

import java.util.Calendar;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class DeviceBootReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        final OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(BootCompleteWorker.class).build();
        WorkManager.getInstance().enqueue(request);

        startWeeklyBackup(context);
    }


    private void startWeeklyBackup(Context context) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent1 = new Intent(context, FirstRunReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 10005, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY*7, pendingIntent);
    }
}
