package com.prodatadoctor.CoolStickyNotes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.prodatadoctor.CoolStickyNotes.SharedPrefManager.SharedPrefManager;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.Note;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StickyNotesEditActivity extends AppCompatActivity implements View.OnClickListener {


    private AppDatabase db;
    private Note note;


    RelativeLayout relativeLayoutSt;
    EditText editTextSt, editTextTitle;

    Button buttonDone, buttonColor, buttonSticker, buttonFont, buttonSize;
    FloatingActionButton buttonEdit;

    SeekBar seekBarTextSize;
    LinearLayout linearLayoutSeekbar, linearLayoutTools;

    HorizontalScrollView hsvColor, hsvSticker, hsvFont;
    ImageView noteColor1, noteColor2, noteColor3, noteColor4, noteColor5, noteColor6, noteColor7, noteColor8, noteColor9, noteColor10, noteColor11, noteColor12, noteColor13, noteColor14, noteColor15,
            imageSticker, noteSticker1, noteSticker2, noteSticker3, noteSticker4, noteSticker5, noteSticker6, noteSticker7, noteSticker8, noteSticker9, noteSticker10;

    Typeface typeface1, typeface2, typeface3, typeface4, typeface5, typeface6, typeface7, typeface8, typeface9, typeface10;
    TextView font1, font2, font3, font4, font5, font6, font7, font8, font9, font10;

    int noteColor = 0, noteSticker = 0, noteFont = 0;
    float textSize;

    boolean edited;

    SharedPreferences pref, sharedPreferencesStopAd;
    int rate;


    AdView mAdView1;
    InterstitialAd mInterstitialAd;
    Boolean check;


    void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(StickyNotesEditActivity.this, color));
        }
    }

    private void initAds() {
        mInterstitialAd = new InterstitialAd(this);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticky_notes_edit);


        getSupportActionBar().setTitle("Sticky Notes");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        setStatusBarColor(R.color.colorPrimaryDark2);

        db = AppDatabase.getInstance(this);

        initPref();
        initAds();
        if (check) {
            showAds();
        }

        initViews();

        note = (Note) getIntent().getSerializableExtra("note_position");
        boolean fromEdit=getIntent().getBooleanExtra("fromEdit",false);
        if (note != null && fromEdit) {

            linearLayoutTools.setVisibility(View.VISIBLE);
            buttonEdit.setVisibility(View.GONE);

            editTextTitle.setFocusableInTouchMode(true);
            editTextSt.setFocusableInTouchMode(true);
            edited = true;

            noteColor = note.getColor();
            noteSticker = note.getImage();
            noteFont = note.getFont();
            setNoteProperties(note);

            textSize = note.getTextsize();

        }
        if (note != null && !fromEdit) {

            linearLayoutTools.setVisibility(View.GONE);
            buttonEdit.setVisibility(View.VISIBLE);

            editTextTitle.setFocusableInTouchMode(false);
            editTextSt.setFocusableInTouchMode(false);

            noteColor = note.getColor();
            noteSticker = note.getImage();
            noteFont = note.getFont();
            setNoteProperties(note);

            textSize = note.getTextsize();

        } else {

            linearLayoutTools.setVisibility(View.VISIBLE);
            buttonEdit.setVisibility(View.GONE);

//            editTextSt.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);


            //-------- default text size
            float px = editTextSt.getTextSize();//getting pixel
            float scaledDensity = getResources().getDisplayMetrics().scaledDensity;//getting density
            textSize = px / scaledDensity;//converting px to sp
        }


        seekBarTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int min = 10;
                if (progress > min) {

                    if (fromUser) {
                        if (!editTextTitle.hasFocus()) {//-------------------------------------------
                            editTextSt.setTextSize(progress);
                            textSize = progress;
                        }
                    }
                }
            }
        });

    }


    private void initPref() {

        pref = StickyNotesEditActivity.this.getSharedPreferences("MyPref", StickyNotesEditActivity.this.MODE_PRIVATE);
        rate = pref.getInt("key", 0);


        sharedPreferencesStopAd = StickyNotesEditActivity.this.getSharedPreferences("payment", Context.MODE_PRIVATE);
        check = sharedPreferencesStopAd.getBoolean("check", true);

        if (rate == 2) {
            saveBox1();
        }
    }

    private void setNoteProperties(Note note) {

        editTextTitle.setText(note.getTitle());
        editTextSt.setText(note.getDescription());
        getNoteFont(note.getFont());
        getNoteColor(note.getColor());
        getNoteSticker(note.getImage());
        editTextSt.setTextSize(note.getTextsize());
    }


    @Override
    public void onBackPressed() {

        if (editTextTitle.getText().toString().trim().equals("") && editTextSt.getText().toString().trim().equals("")) {
            finish();
        } else {
            if (!edited && note != null) {
                finish();
            } else {
                if (rate == 0) {
                    pref = StickyNotesEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    rate = 1;
                    editor.putInt("key", rate);
                    editor.apply();
                } else if (rate == 1) {
                    pref = StickyNotesEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    rate = 2;
                    editor.putInt("key", rate);
                    editor.apply();
                }

                saveNote();
            }

        }
    }


    @SuppressLint("StaticFieldLeak")
    private void saveNote() {

        if (note == null) {
            note = new Note();
        }

        note.setTitle(editTextTitle.getText().toString());
        note.setDescription(editTextSt.getText().toString());
        note.setColor(noteColor);
        note.setImage(noteSticker);
        note.setFont(noteFont);
        note.setTextsize(textSize);

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat(" d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        note.setDate(date);


        new AsyncTask<Note, Void, Void>() {
            @Override
            protected Void doInBackground(Note... params) {

                Note saveNote = params[0];

                if (saveNote.getId() > 0) {
                    db.getNoteDao().updateAll(saveNote);
                } else {
                    SharedPrefManager.getInstance(StickyNotesEditActivity.this).setItemInserted(true);
                    db.getNoteDao().insertAll(saveNote);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                setResult(RESULT_OK);

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    finish();
                }
            }
        }.execute(note);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnDone:

                if (editTextTitle.getText().toString().trim().equals("") && editTextSt.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Write something", Toast.LENGTH_SHORT).show();
                } else {

                    if (rate == 0) {
                        pref = StickyNotesEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        rate = 1;
                        editor.putInt("key", rate);
                        editor.apply();
                    } else if (rate == 1) {
                        pref = StickyNotesEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        rate = 2;
                        editor.putInt("key", rate);
                        editor.apply();
                    }

                    saveNote();
                }

                break;

            case R.id.btnColor:
                hsvSticker.setVisibility(View.GONE);
                hsvFont.setVisibility(View.GONE);
                hsvColor.setVisibility(View.VISIBLE);
                linearLayoutSeekbar.setVisibility(View.GONE);
                break;

            case R.id.btnSticker:
                hsvSticker.setVisibility(View.VISIBLE);
                hsvColor.setVisibility(View.GONE);
                hsvFont.setVisibility(View.GONE);
                linearLayoutSeekbar.setVisibility(View.GONE);
                break;

            case R.id.btnFont:
                hsvSticker.setVisibility(View.GONE);
                hsvColor.setVisibility(View.GONE);
                hsvFont.setVisibility(View.VISIBLE);
                linearLayoutSeekbar.setVisibility(View.GONE);
                break;

            case R.id.btnSize:

                if (editTextTitle.hasFocus()) {
                    Toast.makeText(getApplicationContext(), "Title size can't increased", Toast.LENGTH_SHORT).show();
                } else {
                    hsvSticker.setVisibility(View.GONE);
                    hsvColor.setVisibility(View.GONE);
                    hsvFont.setVisibility(View.GONE);
                    linearLayoutSeekbar.setVisibility(View.VISIBLE);
                }

                break;

            case R.id.btnEdit:

                edited = true;

                linearLayoutTools.setVisibility(View.VISIBLE);
                buttonEdit.setVisibility(View.GONE);

                editTextTitle.setFocusableInTouchMode(true);
                editTextSt.setFocusableInTouchMode(true);

                break;


            case R.id.NoteColor1:
                getNoteColor(1);
                noteColor = 1;
                break;

            case R.id.NoteColor2:
                getNoteColor(2);
                noteColor = 2;
                break;

            case R.id.NoteColor3:
                getNoteColor(3);
                noteColor = 3;
                break;

            case R.id.NoteColor4:
                getNoteColor(4);
                noteColor = 4;
                break;

            case R.id.NoteColor5:
                getNoteColor(5);
                noteColor = 5;
                break;

            case R.id.NoteColor6:
                getNoteColor(6);
                noteColor = 6;
                break;

            case R.id.NoteColor7:
                getNoteColor(7);
                noteColor = 7;
                break;

            case R.id.NoteColor8:
                getNoteColor(8);
                noteColor = 8;
                break;

            case R.id.NoteColor9:
                getNoteColor(9);
                noteColor = 9;
                break;

            case R.id.NoteColor10:
                getNoteColor(10);
                noteColor = 10;
                break;

            case R.id.NoteColor11:
                getNoteColor(11);
                noteColor = 11;
                break;

            case R.id.NoteColor12:
                getNoteColor(12);
                noteColor = 12;
                break;

            case R.id.NoteColor13:
                getNoteColor(13);
                noteColor = 13;
                break;

            case R.id.NoteColor14:
                getNoteColor(14);
                noteColor = 14;
                break;

            case R.id.NoteColor15:
                getNoteColor(15);
                noteColor = 15;
                break;


            case R.id.NoteSticker1:
                getNoteSticker(1);
                noteSticker = 1;
                break;

            case R.id.NoteSticker2:
                getNoteSticker(2);
                noteSticker = 2;
                break;

            case R.id.NoteSticker3:
                getNoteSticker(3);
                noteSticker = 3;
                break;

            case R.id.NoteSticker4:
                getNoteSticker(4);
                noteSticker = 4;
                break;

            case R.id.NoteSticker5:
                getNoteSticker(5);
                noteSticker = 5;
                break;

            case R.id.NoteSticker6:
                getNoteSticker(6);
                noteSticker = 6;
                break;

            case R.id.NoteSticker7:
                getNoteSticker(7);
                noteSticker = 7;
                break;

            case R.id.NoteSticker8:
                getNoteSticker(8);
                noteSticker = 8;
                break;

            case R.id.NoteSticker9:
                getNoteSticker(9);
                noteSticker = 9;
                break;

            case R.id.NoteSticker10:
                getNoteSticker(10);
                noteSticker = 10;
                break;


            case R.id.NoteFont1:
                getNoteFont(1);
                noteFont = 1;
                break;

            case R.id.NoteFont2:
                getNoteFont(2);
                noteFont = 2;
                break;

            case R.id.NoteFont3:
                getNoteFont(3);
                noteFont = 3;
                break;

            case R.id.NoteFont4:
                getNoteFont(4);
                noteFont = 4;
                break;

            case R.id.NoteFont5:
                getNoteFont(5);
                noteFont = 5;
                break;

            case R.id.NoteFont6:
                getNoteFont(6);
                noteFont = 6;
                break;

            case R.id.NoteFont7:
                getNoteFont(7);
                noteFont = 7;
                break;

            case R.id.NoteFont8:
                getNoteFont(8);
                noteFont = 8;
                break;

            case R.id.NoteFont9:
                getNoteFont(9);
                noteFont = 9;
                break;

            case R.id.NoteFont10:
                getNoteFont(10);
                noteFont = 10;
                break;
        }
    }


    private void initViews() {

        relativeLayoutSt = findViewById(R.id.relativeSticky);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextSt = findViewById(R.id.editTextText);
        imageSticker = findViewById(R.id.imageSticker);

        buttonColor = findViewById(R.id.btnColor);
        buttonSticker = findViewById(R.id.btnSticker);
        buttonFont = findViewById(R.id.btnFont);
        buttonDone = findViewById(R.id.btnDone);
        buttonSize = findViewById(R.id.btnSize);
        buttonEdit = findViewById(R.id.btnEdit);

        hsvColor = findViewById(R.id.horizontalColor);
        hsvFont = findViewById(R.id.horizontalFont);
        hsvSticker = findViewById(R.id.horizontalStickers);

        hsvColor.setVisibility(View.GONE);
        hsvFont.setVisibility(View.GONE);
        hsvSticker.setVisibility(View.GONE);

        buttonColor.setOnClickListener(this);
        buttonSticker.setOnClickListener(this);
        buttonFont.setOnClickListener(this);
        buttonSize.setOnClickListener(this);
        buttonDone.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);


        noteColor1 =  findViewById(R.id.NoteColor1);
        noteColor2 =  findViewById(R.id.NoteColor2);
        noteColor3 =  findViewById(R.id.NoteColor3);
        noteColor4 =  findViewById(R.id.NoteColor4);
        noteColor5 =  findViewById(R.id.NoteColor5);
        noteColor6 =  findViewById(R.id.NoteColor6);
        noteColor7 =  findViewById(R.id.NoteColor7);
        noteColor8 =  findViewById(R.id.NoteColor8);
        noteColor9 =  findViewById(R.id.NoteColor9);
        noteColor10 =  findViewById(R.id.NoteColor10);
        noteColor11 =  findViewById(R.id.NoteColor11);
        noteColor12 =  findViewById(R.id.NoteColor12);
        noteColor13 =  findViewById(R.id.NoteColor13);
        noteColor14 =  findViewById(R.id.NoteColor14);
        noteColor15 =  findViewById(R.id.NoteColor15);


        noteColor1.setOnClickListener(this);
        noteColor2.setOnClickListener(this);
        noteColor3.setOnClickListener(this);
        noteColor4.setOnClickListener(this);
        noteColor5.setOnClickListener(this);
        noteColor6.setOnClickListener(this);
        noteColor7.setOnClickListener(this);
        noteColor8.setOnClickListener(this);
        noteColor9.setOnClickListener(this);
        noteColor10.setOnClickListener(this);
        noteColor11.setOnClickListener(this);
        noteColor12.setOnClickListener(this);
        noteColor13.setOnClickListener(this);
        noteColor14.setOnClickListener(this);
        noteColor15.setOnClickListener(this);

        imageSticker =  findViewById(R.id.imageSticker);
        noteSticker1 =  findViewById(R.id.NoteSticker1);
        noteSticker2 =  findViewById(R.id.NoteSticker2);
        noteSticker3 =  findViewById(R.id.NoteSticker3);
        noteSticker4 =  findViewById(R.id.NoteSticker4);
        noteSticker5 =  findViewById(R.id.NoteSticker5);
        noteSticker6 =  findViewById(R.id.NoteSticker6);
        noteSticker7 =  findViewById(R.id.NoteSticker7);
        noteSticker8 =  findViewById(R.id.NoteSticker8);
        noteSticker9 =  findViewById(R.id.NoteSticker9);
        noteSticker10 =  findViewById(R.id.NoteSticker10);

        noteSticker1.setOnClickListener(this);
        noteSticker2.setOnClickListener(this);
        noteSticker3.setOnClickListener(this);
        noteSticker4.setOnClickListener(this);
        noteSticker5.setOnClickListener(this);
        noteSticker6.setOnClickListener(this);
        noteSticker7.setOnClickListener(this);
        noteSticker8.setOnClickListener(this);
        noteSticker9.setOnClickListener(this);
        noteSticker10.setOnClickListener(this);

        font1 = (TextView) findViewById(R.id.NoteFont1);
        font2 = (TextView) findViewById(R.id.NoteFont2);
        font3 = (TextView) findViewById(R.id.NoteFont3);
        font4 = (TextView) findViewById(R.id.NoteFont4);
        font5 = (TextView) findViewById(R.id.NoteFont5);
        font6 = (TextView) findViewById(R.id.NoteFont6);
        font7 = (TextView) findViewById(R.id.NoteFont7);
        font8 = (TextView) findViewById(R.id.NoteFont8);
        font9 = (TextView) findViewById(R.id.NoteFont9);
        font10 = (TextView) findViewById(R.id.NoteFont10);

        font1.setOnClickListener(this);
        font2.setOnClickListener(this);
        font3.setOnClickListener(this);
        font4.setOnClickListener(this);
        font5.setOnClickListener(this);
        font6.setOnClickListener(this);
        font7.setOnClickListener(this);
        font8.setOnClickListener(this);
        font9.setOnClickListener(this);
        font10.setOnClickListener(this);

        linearLayoutSeekbar = (LinearLayout) findViewById(R.id.linearLayoutSeekbar);
        linearLayoutSeekbar.setVisibility(View.GONE);
        linearLayoutTools = findViewById(R.id.linearTools);


        seekBarTextSize = findViewById(R.id.seekbarTexrSize);

        setupFonts();

    }


    public void setupFonts() {

        typeface1 = Typeface.createFromAsset(getAssets(), "fonts/font1.ttf");
        typeface2 = Typeface.createFromAsset(getAssets(), "fonts/font2.otf");
        typeface3 = Typeface.createFromAsset(getAssets(), "fonts/font3.ttf");
        typeface4 = Typeface.createFromAsset(getAssets(), "fonts/font4.ttf");
        typeface5 = Typeface.createFromAsset(getAssets(), "fonts/font5.ttf");
        typeface6 = Typeface.createFromAsset(getAssets(), "fonts/font6.ttf");
        typeface7 = Typeface.createFromAsset(getAssets(), "fonts/font7.ttf");
        typeface8 = Typeface.createFromAsset(getAssets(), "fonts/font8.ttf");
        typeface9 = Typeface.createFromAsset(getAssets(), "fonts/font9.ttf");
        typeface10 = Typeface.createFromAsset(getAssets(), "fonts/font10.ttf");

        font1.setTypeface(typeface1);
        font2.setTypeface(typeface2);
        font3.setTypeface(typeface3);
        font4.setTypeface(typeface4);
        font5.setTypeface(typeface5);
        font6.setTypeface(typeface6);
        font7.setTypeface(typeface7);
        font8.setTypeface(typeface8);
        font9.setTypeface(typeface9);
        font10.setTypeface(typeface10);
    }

    private void getNoteColor(int noteColor) {
        if (noteColor == 1) {
            relativeLayoutSt.setBackgroundResource(R.color.note1);
        } else if (noteColor == 2) {
            relativeLayoutSt.setBackgroundResource(R.color.note2);
        } else if (noteColor == 3) {
            relativeLayoutSt.setBackgroundResource(R.color.note3);
        } else if (noteColor == 4) {
            relativeLayoutSt.setBackgroundResource(R.color.note4);
        } else if (noteColor == 5) {
            relativeLayoutSt.setBackgroundResource(R.color.note5);
        } else if (noteColor == 6) {
            relativeLayoutSt.setBackgroundResource(R.color.note6);
        } else if (noteColor == 7) {
            relativeLayoutSt.setBackgroundResource(R.color.note7);
        } else if (noteColor == 8) {
            relativeLayoutSt.setBackgroundResource(R.color.note8);
        } else if (noteColor == 9) {
            relativeLayoutSt.setBackgroundResource(R.color.note9);
        } else if (noteColor == 10) {
            relativeLayoutSt.setBackgroundResource(R.color.note10);
        } else if (noteColor == 11) {
            relativeLayoutSt.setBackgroundResource(R.color.note11);
        } else if (noteColor == 12) {
            relativeLayoutSt.setBackgroundResource(R.color.note12);
        } else if (noteColor == 13) {
            relativeLayoutSt.setBackgroundResource(R.color.note13);
        } else if (noteColor == 14) {
            relativeLayoutSt.setBackgroundResource(R.color.note14);
        } else if (noteColor == 15) {
            relativeLayoutSt.setBackgroundResource(R.color.note15);
        }
    }

    private void getNoteSticker(int noteSticker) {
        if (noteSticker == 1) {
            imageSticker.setBackgroundResource(R.drawable.sticker1);
        } else if (noteSticker == 2) {
            imageSticker.setBackgroundResource(R.drawable.sticker2);
        } else if (noteSticker == 3) {
            imageSticker.setBackgroundResource(R.drawable.sticker3);
        } else if (noteSticker == 4) {
            imageSticker.setBackgroundResource(R.drawable.sticker4);
        } else if (noteSticker == 5) {
            imageSticker.setBackgroundResource(R.drawable.sticker5);
        } else if (noteSticker == 6) {
            imageSticker.setBackgroundResource(R.drawable.sticker6);
        } else if (noteSticker == 7) {
            imageSticker.setBackgroundResource(R.drawable.sticker7);
        } else if (noteSticker == 8) {
            imageSticker.setBackgroundResource(R.drawable.sticker8);
        } else if (noteSticker == 9) {
            imageSticker.setBackgroundResource(R.drawable.sticker9);
        } else if (noteSticker == 10) {
            imageSticker.setBackgroundResource(R.drawable.sticker10);
        }

    }


    private void getNoteFont(int noteFont) {

        if (noteFont == 1) {
            editTextTitle.setTypeface(typeface1);
            editTextSt.setTypeface(typeface1);
        } else if (noteFont == 2) {
            editTextTitle.setTypeface(typeface2);
            editTextSt.setTypeface(typeface2);
        } else if (noteFont == 3) {
            editTextTitle.setTypeface(typeface3);
            editTextSt.setTypeface(typeface3);
        } else if (noteFont == 4) {
            editTextTitle.setTypeface(typeface4);
            editTextSt.setTypeface(typeface4);
        } else if (noteFont == 5) {
            editTextTitle.setTypeface(typeface5);
            editTextSt.setTypeface(typeface5);
        } else if (noteFont == 6) {
            editTextTitle.setTypeface(typeface6);
            editTextSt.setTypeface(typeface6);
        } else if (noteFont == 7) {
            editTextTitle.setTypeface(typeface7);
            editTextSt.setTypeface(typeface7);
        } else if (noteFont == 8) {
            editTextTitle.setTypeface(typeface8);
            editTextSt.setTypeface(typeface8);
        } else if (noteFont == 9) {
            editTextTitle.setTypeface(typeface9);
            editTextSt.setTypeface(typeface9);
        } else if (noteFont == 10) {
            editTextTitle.setTypeface(typeface10);
            editTextSt.setTypeface(typeface10);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_activities, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.gift:
                startActivity(new Intent(StickyNotesEditActivity.this, GiftsAndOffers.class));
                break;

            case R.id.shareapp:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, I found this cool app \"" + getResources().getString(R.string.app_name) + "\" on play store.\n"
                        + "Download this FREE app here:\n\n " + getResources().getString(R.string.applink));
                intent.setType("text/plain");
                startActivity(intent);
                break;

            case R.id.youtube:
                youTubeSubscribed();

//                boolean subscribed = pref.getBoolean("subscribed", false);
//                if (!subscribed) {
//                    showYoutubeDialog();
//                } else {
//                    youTubeSubscribed();
//                }
                break;

        }
        return true;
    }


    private void showYoutubeDialog() {

        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        dialog.setContentView(R.layout.youtube_share_dialog);

        dialog.setCancelable(false);
        dialog.setTitle("");
        Button notNow = dialog.findViewById(R.id.notNow);
        Button subscribeNow = dialog.findViewById(R.id.subscribeNow);

        notNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        subscribeNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                youTubeSubscribed();
            }
        });
    }

    void youTubeSubscribed() {
        SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
        editor.putBoolean("subscribed", true);
        editor.apply();

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/TarunTyagi"));
        startActivity(intent);
    }


    private void showAds() {
        MobileAds.initialize(this, getString(R.string.appid));
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                finish();
            }

        });

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


    public void saveBox1() {

        final Dialog dialog = new AlertDialog.Builder(StickyNotesEditActivity.this).create();
        dialog.show();
        dialog.setContentView(R.layout.rateus);
        dialog.setCancelable(false);
        dialog.setTitle("");
        Button bawesome = (Button) dialog.findViewById(R.id.bawesome);
        Button breason = (Button) dialog.findViewById(R.id.breason);
        Button blater = (Button) dialog.findViewById(R.id.blater);


        bawesome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 3;
                dialog.dismiss();
                pref = StickyNotesEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("key", rate);
                editor.apply();

                Uri uri = Uri.parse(getResources().getString(R.string.applink)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });

        blater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        breason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rate = 3;
                dialog.dismiss();
                pref = StickyNotesEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("key", rate);
                editor.apply();

                Intent i = new Intent(Intent.ACTION_SEND);
                //   i.setType("message/rfc822");
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@prodatadoctor.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "I did not like your App " + getResources().getString(R.string.app_name));
                i.putExtra(Intent.EXTRA_TEXT, "Hey There! \n" +
                        "I downloaded your App " + getResources().getString(R.string.app_name) + " and it doesn’t merit a 5 star rating from me due to following reasons. Please help:");
                try {
                    startActivity(Intent.createChooser(i, "Perform action using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(StickyNotesEditActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
