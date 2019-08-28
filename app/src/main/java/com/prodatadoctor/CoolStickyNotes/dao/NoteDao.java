package com.prodatadoctor.CoolStickyNotes.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.prodatadoctor.CoolStickyNotes.domain.Note;

import java.util.List;

/**
 * @author acampbell
 */
@Dao
public interface NoteDao {

    @Insert
    void insertAll(Note... notes);

    @Update
    void updateAll(Note... notes);

    @Query("SELECT * FROM note")
    List<Note> getAll();


    @Delete
    void deleteAll(Note... notes);
}
