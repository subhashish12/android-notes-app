package com.prodatadoctor.CoolStickyNotes.Notifications.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.prodatadoctor.CoolStickyNotes.AppDataBackup.BackupWorker.BackupWorker;

import java.util.Collections;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class FirstRunReceiver extends BroadcastReceiver {

    private static final String TAG = "FirstRunReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "onReceive: " );
        final OneTimeWorkRequest requestNoteBackup = new OneTimeWorkRequest.Builder(BackupWorker.class).build();
        WorkManager.getInstance().
                beginWith(Collections.singletonList(requestNoteBackup))
                .enqueue();//start work manager to take wekly backup

    }
}
