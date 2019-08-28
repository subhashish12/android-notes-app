package com.prodatadoctor.CoolStickyNotes.Notifications.Worker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.prodatadoctor.CoolStickyNotes.Notifications.Receivers.MyAlarmReceiver;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class BootCompleteWorker extends Worker {

    AppDatabase db;
    AlarmManager am;

    public BootCompleteWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        db = Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DB_NAME).build();
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {
        passListForAlarm(db.getToDoDao().getAll());
        return Result.success();
    }

    private void passListForAlarm(List<ToDo> list1) {

        for (ToDo birthday:list1) {
            setAlarm(birthday);
        }
    }

    private void setAlarm(ToDo todo) {

        Long x = todo.getId();
        int reqC = x.intValue();

        String strDate = todo.getDate() + " " + todo.getTime();

        Date date = null;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        try {
            date = dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            if (System.currentTimeMillis() < date.getTime()) {
                if (todo.isAlarmOn()) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    Bundle args = new Bundle();
                    args.putSerializable("birthday", todo);

                    Intent intent1 = new Intent(getApplicationContext(), MyAlarmReceiver.class).putExtra("DATA", args);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reqC, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                    am.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

                } else {
                    am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    Intent myIntent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reqC, myIntent, 0);
                    am.cancel(pendingIntent);
                }
            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
