package com.prodatadoctor.CoolStickyNotes.AppDataBackup.BackupWorker;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.NonNull;

import com.prodatadoctor.CoolStickyNotes.AppDataBackup.JsonOperations.JsonGenerator;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackupWorker extends Worker {

    private AppDatabase db;
    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        db = Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DB_NAME).build();
    }

    @NonNull
    @Override
    public Result doWork() {

        DateFormat df = new SimpleDateFormat("ddMMMyyyy");
        String fileName = df.format(Calendar.getInstance().getTime());
        fileName="Weekly_Backup"+"("+fileName+")";

        try {
            JsonGenerator.writeJsonAndSave(JsonGenerator.getJSONObject(db.getNoteDao().getAll(),
                    db.getNotepadDao().getAll(),
                    db.getToDoDao().getAll()),
                    fileName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Result.success();
    }
}
