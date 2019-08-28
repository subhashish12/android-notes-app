package com.prodatadoctor.CoolStickyNotes.Notifications.Receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.prodatadoctor.CoolStickyNotes.MainActivity;
import com.prodatadoctor.CoolStickyNotes.MinuteElapseReceiver;
import com.prodatadoctor.CoolStickyNotes.R;
import com.prodatadoctor.CoolStickyNotes.ToDoEditActivity;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;

import java.util.Calendar;


public class MyAlarmReceiver extends BroadcastReceiver{

    String TAG="MyAlarmReceiver";
    int reqC;
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "onReceive:");
        Bundle args = intent.getBundleExtra("DATA");
        ToDo todo = (ToDo) args.getSerializable("birthday");

        Long x = todo.getId();
        int reqC = x.intValue();
        String title="You have new task!";
        String content = todo.getContent();
        if(content.length()>40){
            content=content.substring(0,37)+"...";
        }

        displayNotification(context, reqC, title, content,todo);//display notification
    }


    private void displayNotification(Context context, int rq, String title,String content,ToDo toDo) {

        Intent intent2 = new Intent(context.getApplicationContext(), ToDoEditActivity.class);
        intent2.setAction(Intent.ACTION_MAIN);
        intent2.addCategory(Intent.CATEGORY_LAUNCHER);
        intent2.putExtra("notification_item",toDo);

        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context.getApplicationContext(), rq, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);

       // Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_tone);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();


            NotificationChannel channel = new NotificationChannel("xyz", "xyz", NotificationManager.IMPORTANCE_DEFAULT);
         //   channel.setSound(soundUri,audioAttributes);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "xyz")
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingNotificationIntent)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        manager.notify(rq, builder.build());
    }
}
