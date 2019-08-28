package com.prodatadoctor.CoolStickyNotes.domain;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "todo")
public class ToDo implements Serializable,Comparable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;


    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "date")
    private String date;


    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "category")
    private String category;


    @ColumnInfo(name = "alarmOn")
    private boolean alarmOn;

    public boolean isAlarmOn() {
        return alarmOn;
    }

    public void setAlarmOn(boolean alarmOn) {
        this.alarmOn = alarmOn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @ColumnInfo(name = "datetime")
    private long datetime;

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

//    @Override
//    public int compare(Object o1, Object o2) {
//        return Long.compare(this.getDatetime(), this.getDatetime());
//    }


    @Override
    public int compareTo(@NonNull Object o) {
//        int compareTo=((ToDo )o).getDatetime();
//        /* For Ascending order*/
//        return this.datetime-compareTo;
//
//        /* For Descending order do like this */
//        //return compareage-this.rating;




        return this.datetime < ((ToDo )o).getDatetime()?-1 : this.datetime>((ToDo )o).getDatetime()?1:0;
    }
}