package com.prodatadoctor.CoolStickyNotes.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.prodatadoctor.CoolStickyNotes.domain.ToDo;

import java.util.Date;
import java.util.List;

@Dao
public interface ToDoDao {

    @Insert
    void insertAll(ToDo... todo);

    @Update
    void updateAll(ToDo... todo);

    @Query("SELECT * FROM todo")
    List<ToDo> getAll();


    @Delete
    void deleteAll(ToDo... todo);

    @Query("SELECT * FROM todo WHERE id = :id")
    ToDo getItem(long id);


    @Query("UPDATE todo SET alarmOn=:alarm WHERE id = :id")
    void updateAlarm(long id,boolean alarm);
}
