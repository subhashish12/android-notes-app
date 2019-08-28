package com.prodatadoctor.CoolStickyNotes.AppDataBackup.JsonOperations;

import android.os.Environment;

import com.prodatadoctor.CoolStickyNotes.DataBackUpActivity;
import com.prodatadoctor.CoolStickyNotes.domain.Note;
import com.prodatadoctor.CoolStickyNotes.domain.Notepad;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

public class JsonGenerator {

    public static JSONObject getJSONObject(List<Note> noteList, List<Notepad> notepadList, List<ToDo> todoList) throws JSONException {
        JSONObject finalJsonObject = new JSONObject();
        finalJsonObject.put("note", getNoteJSON(noteList));
        finalJsonObject.put("notepad", getNotepadJSON(notepadList));
        finalJsonObject.put("todo", getToDoJSON(todoList));
        return finalJsonObject;
    }


    public static JSONArray getNoteJSON(List<Note> list) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Note note : list) {
            JSONObject formDetailsJson = new JSONObject();
            formDetailsJson.put("id", note.getId());
            formDetailsJson.put("title", note.getTitle());
            formDetailsJson.put("description", note.getDescription());
            formDetailsJson.put("color", note.getColor());
            formDetailsJson.put("image", note.getImage());
            formDetailsJson.put("font", note.getFont());
            formDetailsJson.put("textsize", note.getTextsize());

            jsonArray.put(formDetailsJson);
        }
        return jsonArray;
    }

    public static JSONArray getNotepadJSON(List<Notepad> list) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Notepad note : list) {
            JSONObject formDetailsJson = new JSONObject();
            formDetailsJson.put("id", note.getId());
            formDetailsJson.put("title", note.getTitle());
            formDetailsJson.put("description", note.getDescription());
            formDetailsJson.put("color", note.getColor());
            formDetailsJson.put("font", note.getFont());
            formDetailsJson.put("textsize", note.getTextsize());

            jsonArray.put(formDetailsJson);
        }
        return jsonArray;
    }

    public static JSONArray getToDoJSON(List<ToDo> list) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (ToDo note : list) {
            JSONObject formDetailsJson = new JSONObject();
            formDetailsJson.put("id", note.getId());
            formDetailsJson.put("content", note.getContent());
            formDetailsJson.put("date", note.getDate());
            formDetailsJson.put("time", note.getTime());
            formDetailsJson.put("category", note.getCategory());
            formDetailsJson.put("datetime", note.getDatetime());
            formDetailsJson.put("alarmOn",note.isAlarmOn());////////////////////

            jsonArray.put(formDetailsJson);
        }

        return jsonArray;
    }


    public static String writeJsonAndSave(JSONObject mJsonResponse, String fileName) {
        String directory = null;
        directory = Environment.getExternalStorageDirectory() + "/.Cool Sticky Notes Rich Look Reminder Chits/Backup/";
        File mfile = new File(directory);
        if (!mfile.exists()) {
            mfile.mkdirs();
        }

        try {
            Writer output;
            File file = new File(directory + fileName + ".json");
            if (file.isDirectory()) {
                file.delete();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            output = new BufferedWriter(new FileWriter(file));
            output.write(mJsonResponse.toString());
            output.close();
            return file.getPath();
        } catch (Exception e) {
        }
        return null;
    }

}
