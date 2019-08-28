package com.prodatadoctor.CoolStickyNotes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.prodatadoctor.CoolStickyNotes.adapter.BackupFilesAdapter;
import com.prodatadoctor.CoolStickyNotes.dao.NoteDao;
import com.prodatadoctor.CoolStickyNotes.dao.NotepadDao;
import com.prodatadoctor.CoolStickyNotes.dao.ToDoDao;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.Note;
import com.prodatadoctor.CoolStickyNotes.domain.Notepad;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DataRestoreActivity extends AppCompatActivity implements BackupFilesAdapter.FileClicked, FileExploreDialogFragment.OnCompleteListener {

    SharedPreferences pref, sharedPreferencesStopAd;
    AdView mAdView1;
    Boolean check;


    ImageView imageViewRecycler;
    RecyclerView recyclerView;
    BackupFilesAdapter fileExplorerAdapter;

    Boolean validFile;
    private ProgressBar progressBar;

    String mResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_restore);

        sharedPreferencesStopAd = this.getSharedPreferences("payment", Context.MODE_PRIVATE);
        check = sharedPreferencesStopAd.getBoolean("check", true);

        showAds();

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.INVISIBLE);


        File directory = null;
        String path = Environment.getExternalStorageDirectory() + "/.Cool Sticky Notes Rich Look Reminder Chits/Backup/";
        File mTempFile = new File(path);
        if (!mTempFile.exists()) {
            mTempFile.mkdirs();
        }
        directory = new File(path);
        File[] files = directory.listFiles();
        List<File> fileList = new ArrayList(Arrays.asList(files));


        imageViewRecycler = findViewById(R.id.emptyImage);
        recyclerView = findViewById(R.id.recyclerViewBackupFile);
        imageViewRecycler.setVisibility(View.INVISIBLE);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 5));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fileExplorerAdapter = new BackupFilesAdapter(this, fileList, this);
        recyclerView.setAdapter(fileExplorerAdapter);

        findViewById(R.id.browseFromDevice).setOnClickListener(v -> browseFile());
    }

    private void browseFile() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FileExploreDialogFragment dialog = new FileExploreDialogFragment();
        dialog.setCancelable(false);
        dialog.show(fragmentManager, "dialog");
    }


    @Override
    public void fileOperationPerformed(String path, String operation) {
        if (operation.equals("Restore")) {

            new RestoreDataTask().execute(path);
            showResetDialog();

        } else if (operation.equals("Share")) {

            File f = new File(path);
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_SUBJECT, "Backup from "+getResources().getString(R.string.app_name));
            share.setType("file/*");
            share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(path)));
            startActivity(Intent.createChooser(share, "Share File By:"));
        }
    }

    @Override
    public void onComplete(String path) {
        showResetDialog();
        new RestoreDataTask().execute(path);
    }


    //---------------------------------------------  restore-----------------------------------------------------------


    private class RestoreDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... path) {

            FileInputStream stream = null;
            try {
                stream = new FileInputStream(new File(path[0]));

                String jString = null;
                try {
                    FileChannel fc = stream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    jString = Charset.defaultCharset().decode(bb).toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    stream.close();
                }
                return jString;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String response) {
            mResponse = response;
            new ValidateJsonTask().execute(response);
        }
    }


    class ValidateJsonTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                if (jsonObject.has("note") && jsonObject.has("notepad") && jsonObject.has("todo")) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            super.onPostExecute(valid);

            validFile=valid;

            Log.e("valid", "" + valid);

            if (valid) {
                new ParseNoteTask().execute(mResponse);
                new ParseNotepadTask().execute(mResponse);
                new ParseToDoTask().execute(mResponse);
            }
        }
    }

    class ParseNoteTask extends AsyncTask<String, Void, List<Note>> {
        @Override
        protected List<Note> doInBackground(String... result) {

            JSONArray jsonArrays = null;
            try {
                JSONObject jsonObject = new JSONObject(result[0]);
                jsonArrays = jsonObject.getJSONArray("note");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<Note> noteList = new ArrayList<>();
            for (int i = 0; i <= jsonArrays.length(); i++) {

                JSONObject jObject = null;
                try {
                    jObject = jsonArrays.getJSONObject(i);

                    long id = Long.parseLong(jObject.getString("id"));
                    String title = jObject.getString("title");
                    String description = jObject.getString("description");
                    int color = Integer.parseInt(jObject.getString("color"));
                    int image = Integer.parseInt(jObject.getString("image"));
                    int font = Integer.parseInt(jObject.getString("font"));
                    float textsize = Float.parseFloat(jObject.getString("textsize"));

                    Note note = new Note();
                    //  note.setId(id);
                    note.setTitle(title);
                    note.setDescription(description);
                    note.setColor(color);
                    note.setImage(image);
                    note.setFont(font);
                    note.setTextsize(textsize);

                    @SuppressLint("SimpleDateFormat")
                    DateFormat df = new SimpleDateFormat(" d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    note.setDate(date);

                    noteList.add(note);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return noteList;
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            super.onPostExecute(notes);
            new NoteInsertAsyncTask(AppDatabase.getInstance(DataRestoreActivity.this).getNoteDao()).execute(notes);
        }
    }

    class ParseNotepadTask extends AsyncTask<String, Void, List<Notepad>> {
        @Override
        protected List<Notepad> doInBackground(String... result) {
            JSONArray jsonArrays = null;
            try {
                JSONObject jsonObject = new JSONObject(result[0]);
                jsonArrays = jsonObject.getJSONArray("notepad");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<Notepad> notepadList = new ArrayList<>();
            for (int i = 0; i <= jsonArrays.length(); i++) {

                JSONObject jObject = null;
                try {
                    jObject = jsonArrays.getJSONObject(i);

                    long id = Long.parseLong(jObject.getString("id"));
                    String title = jObject.getString("title");
                    String description = jObject.getString("description");
                    int color = Integer.parseInt(jObject.getString("color"));
                    int font = Integer.parseInt(jObject.getString("font"));
                    float textsize = Float.parseFloat(jObject.getString("textsize"));

                    Notepad note = new Notepad();
                    //  note.setId(id);
                    note.setTitle(title);
                    note.setDescription(description);
                    note.setColor(color);
                    note.setFont(font);
                    note.setTextsize(textsize);

                    @SuppressLint("SimpleDateFormat")
                    DateFormat df = new SimpleDateFormat(" d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    note.setDate(date);

                    notepadList.add(note);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return notepadList;
        }

        @Override
        protected void onPostExecute(List<Notepad> notepadList) {
            super.onPostExecute(notepadList);

            new NotepadInsertAsyncTask(AppDatabase.getInstance(DataRestoreActivity.this).getNotepadDao()).execute(notepadList);
        }
    }


    class ParseToDoTask extends AsyncTask<String, Void, List<ToDo>> {

        @Override
        protected List<ToDo> doInBackground(String... result) {

            JSONArray jsonArrays = null;
            try {
                JSONObject jsonObject = new JSONObject(result[0]);
                jsonArrays = jsonObject.getJSONArray("todo");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<ToDo> ToDoList = new ArrayList<>();

            for (int i = 0; i <= jsonArrays.length(); i++) {

                JSONObject jObject = null;
                try {
                    jObject = jsonArrays.getJSONObject(i);

                    long id = Long.parseLong(jObject.getString("id"));
                    String content = jObject.getString("content");
                    String date = jObject.getString("date");
                    String time = jObject.getString("time");
                    String category = jObject.getString("category");
                    long datetime = Long.parseLong(jObject.getString("datetime"));
                    boolean isAlarm=jObject.getBoolean("alarmOn");////////////////////

                    ToDo todo = new ToDo();
                    //  note.setId(id);
                    todo.setContent(content);
                    todo.setDate(date);
                    todo.setTime(time);
                    todo.setCategory(category);
                    todo.setDatetime(datetime);
                    todo.setAlarmOn(isAlarm);////////////////////////

                    ToDoList.add(todo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return ToDoList;
        }

        @Override
        protected void onPostExecute(List<ToDo> toDoList) {
            super.onPostExecute(toDoList);

            new ToDoInsertAsyncTask(AppDatabase.getInstance(DataRestoreActivity.this).getToDoDao()).execute(toDoList);
        }
    }


    private class NoteInsertAsyncTask extends AsyncTask<List<Note>, Void, Void> {
        private NoteDao noteDao;

        private NoteInsertAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(List<Note>... notes) {
            for (Note note : notes[0]) {
                noteDao.insertAll(note);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }


    private class NotepadInsertAsyncTask extends AsyncTask<List<Notepad>, Void, Void> {
        private NotepadDao notepadDao;

        private NotepadInsertAsyncTask(NotepadDao notepadDao) {
            this.notepadDao = notepadDao;
        }

        @Override
        protected Void doInBackground(List<Notepad>... notes) {
            for (Notepad note : notes[0]) {
                notepadDao.insertAll(note);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class ToDoInsertAsyncTask extends AsyncTask<List<ToDo>, Void, Void> {
        private ToDoDao todoDao;

        private ToDoInsertAsyncTask(ToDoDao todoDao) {
            this.todoDao = todoDao;
        }

        @Override
        protected Void doInBackground(List<ToDo>... notes) {
            for (ToDo note : notes[0]) {
                todoDao.insertAll(note);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }



    public void showResetDialog() {

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_restore);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button  buttonOk;
        ProgressBar progressBar;
        TextView textView;
        ImageView imageView;

        buttonOk = dialog.findViewById(R.id.ok);
        buttonOk.setVisibility(View.INVISIBLE);
        progressBar=dialog.findViewById(R.id.progressDialog);
        textView=dialog.findViewById(R.id.text_dialog);
        imageView=dialog.findViewById(R.id.imageDialog);
        imageView.setVisibility(View.INVISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.VISIBLE);
                buttonOk.setVisibility(View.VISIBLE);

                if(validFile){
                    textView.setText("Data Restore Completed.");
                }else {
                    textView.setText("Invalid backup file. Can't restore.");
                }
            }

        }, 2500);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validFile){
                    startActivity(new Intent(DataRestoreActivity.this,MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                    dialog.dismiss();
                }else {
                    dialog.dismiss();
                }
            }
        });


        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void showAds() {

        if (check) {
            mAdView1 = findViewById(R.id.adViewbanner1);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView1.loadAd(adRequest);

            mAdView1.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    mAdView1.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView1.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
