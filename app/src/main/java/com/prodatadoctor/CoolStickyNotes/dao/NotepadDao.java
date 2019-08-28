package com.prodatadoctor.CoolStickyNotes.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.prodatadoctor.CoolStickyNotes.domain.Notepad;

import java.util.List;

@Dao
public interface NotepadDao {

    @Insert
    void insertAll(Notepad... notes);

    @Update
    void updateAll(Notepad... notes);

    @Query("SELECT * FROM notepad")
    List<Notepad> getAll();


    @Delete
    void deleteAll(Notepad... notes);

}
