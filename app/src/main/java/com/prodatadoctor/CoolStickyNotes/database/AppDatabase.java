package com.prodatadoctor.CoolStickyNotes.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.prodatadoctor.CoolStickyNotes.dao.NoteDao;
import com.prodatadoctor.CoolStickyNotes.dao.NotepadDao;
import com.prodatadoctor.CoolStickyNotes.dao.ToDoDao;
import com.prodatadoctor.CoolStickyNotes.domain.Note;
import com.prodatadoctor.CoolStickyNotes.domain.Notepad;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;


@Database(entities = {Note.class, ToDo.class, Notepad.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "app_db";

    public abstract NoteDao getNoteDao();
    public abstract ToDoDao getToDoDao();
    public abstract NotepadDao getNotepadDao();


    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DB_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3,MIGRATION_3_4)
//                    .addCallback(roomCallback) //for pre populating dummy data
//                    .fallbackToDestructiveMigration()//Allows Room to destructively recreate database tables if Migrations that would migrate old database schemas to the latest schema version are not found.
                    .build();
        }
        return instance;
    }

    static Migration MIGRATION_1_2 =
            new Migration(1, 2) {
                @Override
                public void migrate(@NonNull final SupportSQLiteDatabase database) {

                    //not done anything in 1 to 2 migration so left empty(we made user lost his app data  of course!)
                }
            };


//Added 2 new table
    static Migration MIGRATION_2_3 =
            new Migration(2, 3) {
                @Override
                public void migrate(@NonNull final SupportSQLiteDatabase database) {

                    database.execSQL("CREATE TABLE IF NOT EXISTS `notepad` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT , `description` TEXT , `date` TEXT , `color` INTEGER NOT NULL default 0, `font` INTEGER NOT NULL default 0, `textsize` REAL NOT NULL default 0.0)");

                    database.execSQL("CREATE TABLE IF NOT EXISTS `todo` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `content` TEXT , `date` TEXT , `time` TEXT , `category` TEXT , `datetime`  INTEGER NOT NULL default 0 )");
                }
            };


//Note:
//boolean value added in ToDo write migration...
// As sqlite(room) doesn't support booean as a type, it internally implement boolean as an integer an set value (0 for false) and (1 for true)
// so adding new integer column into room while migrating

    static Migration MIGRATION_3_4 =
            new Migration(3, 4) {
                @Override
                public void migrate(@NonNull final SupportSQLiteDatabase database) {
                        database.execSQL("ALTER TABLE `todo` ADD COLUMN `alarmOn` INTEGER NOT NULL default 1");
                }
            };



}
