package com.prodatadoctor.CoolStickyNotes;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.prodatadoctor.CoolStickyNotes.AppDataBackup.JsonOperations.JsonGenerator;
import com.prodatadoctor.CoolStickyNotes.adapter.FileExplorerAdapter;
import com.prodatadoctor.CoolStickyNotes.dao.NoteDao;
import com.prodatadoctor.CoolStickyNotes.dao.NotepadDao;
import com.prodatadoctor.CoolStickyNotes.dao.ToDoDao;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.Note;
import com.prodatadoctor.CoolStickyNotes.domain.Notepad;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;

import org.json.JSONException;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class BackupActivity extends AppCompatActivity {

    SharedPreferences pref, sharedPreferencesStopAd;
    AdView mAdView1;
    Boolean check;


    List<Note> noteList;
    List<Notepad> notepadList;
    List<ToDo> toDoList;

    String path;


    ImageView imageViewDone;
    Dialog mOverlayDialog;
    private ProgressBar progressBar;
    TextView textViewProgress,textViewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        sharedPreferencesStopAd = this.getSharedPreferences("payment", Context.MODE_PRIVATE);
        check = sharedPreferencesStopAd.getBoolean("check", true);

        showAds();

        textViewProgress=findViewById(R.id.textViewProgress);
        textViewInfo=findViewById(R.id.textViewBackupInfo);
        textViewInfo.setVisibility(View.GONE);
        imageViewDone=findViewById(R.id.fileDoneImage);
        imageViewDone.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressbar);
        mOverlayDialog = new Dialog(BackupActivity.this, android.R.style.Theme_Panel);
        mOverlayDialog.setCancelable(false);
        mOverlayDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageViewDone.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                textViewProgress.setText("Backup Completed.");

                DateFormat df = new SimpleDateFormat("ddMMMyyyy, HH:mm:ss");
                String fileName = df.format(Calendar.getInstance().getTime());
                fileName = "Manual_Backup" + "(" + fileName + ")";

                DateFormat df1 = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
                String date = df1.format(Calendar.getInstance().getTime());

                StringBuilder stringBuilder=new StringBuilder();
                stringBuilder.append("File Name : "+fileName+"\n");
                stringBuilder.append("Date Created : "+date+"\n");
                stringBuilder.append("Permission : Read Only");

                textViewInfo.setText(stringBuilder);
                textViewInfo.setVisibility(View.VISIBLE);

                mOverlayDialog.dismiss();

                new NoteBackupAsyncTask(AppDatabase.getInstance(BackupActivity.this).getNoteDao()).execute();
            }

        }, 2500);

        findViewById(R.id.shareBackup).setOnClickListener(v -> shareBackup(path));
      //  findViewById(R.id.sendEmail).setOnClickListener(v->sendBackupEmail(path));
        findViewById(R.id.browseBackup).setOnClickListener(v -> startActivity(new Intent(BackupActivity.this,DataRestoreActivity.class)));
    }

    private void sendBackupEmail(String filePath) {

        File f = new File(filePath);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, f.getName());
        i.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider",
                new File(filePath)));
        try {
            startActivity(Intent.createChooser(i, "Share File By:"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(BackupActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


    private void shareBackup(String filePath) {

        File f = new File(filePath);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_SUBJECT, "Backup from "+getResources().getString(R.string.app_name));
        share.setType("file/*");
        share.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider",
                new File(filePath)));
        startActivity(Intent.createChooser(share, "Share File By:"));
    }


    private class NoteBackupAsyncTask extends AsyncTask<Void, Void, List<Note>> {
        private NoteDao noteDao;

        private NoteBackupAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected List<Note> doInBackground(Void... params) {
            return noteDao.getAll();
        }

        @Override
        protected void onPostExecute(List<Note> notes) {
            super.onPostExecute(notes);

            noteList = notes;
            new NotepadBackupAsyncTask(AppDatabase.getInstance(BackupActivity.this).getNotepadDao()).execute();
        }
    }


    private class NotepadBackupAsyncTask extends AsyncTask<Void, Void, List<Notepad>> {
        private NotepadDao noteDao;

        private NotepadBackupAsyncTask(NotepadDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected List<Notepad> doInBackground(Void... params) {
            return noteDao.getAll();
        }

        @Override
        protected void onPostExecute(List<Notepad> notes) {
            super.onPostExecute(notes);

            notepadList = notes;
            new ToDoBackupAsyncTask(AppDatabase.getInstance(BackupActivity.this).getToDoDao()).execute();
        }
    }


    private class ToDoBackupAsyncTask extends AsyncTask<Void, Void, List<ToDo>> {
        private ToDoDao noteDao;

        private ToDoBackupAsyncTask(ToDoDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected List<ToDo> doInBackground(Void... params) {
            return noteDao.getAll();
        }

        @Override
        protected void onPostExecute(List<ToDo> notes) {
            super.onPostExecute(notes);

            toDoList = notes;

            DateFormat df = new SimpleDateFormat("ddMMMyyyy, HH:mm:ss");
            String fileDate = df.format(Calendar.getInstance().getTime());
            String fileName = "Manual_Backup" + "(" + fileDate + ")";

            try {
                path = JsonGenerator.writeJsonAndSave(JsonGenerator.getJSONObject(noteList, notepadList, toDoList), fileName);
                Log.e("path: ", path);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(BackupActivity.this, "Backup Created", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAds() {

        if (check) {

            mAdView1 =  findViewById(R.id.adViewbanner1);

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
