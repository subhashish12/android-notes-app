package com.prodatadoctor.CoolStickyNotes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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

public class DataBackUpActivity extends AppCompatActivity {

    SharedPreferences pref, sharedPreferencesStopAd;
    AdView mAdView1, mAdView2;
    Boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_back_up);


        sharedPreferencesStopAd = this.getSharedPreferences("payment", Context.MODE_PRIVATE);
        check = sharedPreferencesStopAd.getBoolean("check", true);

        showAds();

        findViewById(R.id.takeBackup).setOnClickListener(v -> startActivity(new Intent(DataBackUpActivity.this, BackupActivity.class)));

        findViewById(R.id.restoreData).setOnClickListener(v -> {
            startActivity(new Intent(DataBackUpActivity.this, DataRestoreActivity.class));
        });

    }


    private void showAds() {

        if (check) {

            mAdView1 =  findViewById(R.id.adViewbanner1);
            mAdView2 =  findViewById(R.id.adViewbanner2);

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView1.loadAd(adRequest);
            mAdView2.loadAd(adRequest);

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

            mAdView2.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    mAdView2.setVisibility(View.GONE);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView2.setVisibility(View.VISIBLE);
                }
            });
        }
    }

}
