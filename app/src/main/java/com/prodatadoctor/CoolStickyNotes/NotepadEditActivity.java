package com.prodatadoctor.CoolStickyNotes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.prodatadoctor.CoolStickyNotes.SharedPrefManager.SharedPrefManager;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.Notepad;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotepadEditActivity extends AppCompatActivity implements View.OnClickListener {

    private AppDatabase db;
    private Notepad notepad;

    EditText editTextTitle, editTextContent;

    ImageButton buttonDone, buttonColor, buttonFont, buttonSize;

    FloatingActionButton buttonEdit;

    SeekBar seekBarTextSize;
    LinearLayout linearLayoutNotepadColor, linearLayoutSeekbar, linearLayoutTools;

    HorizontalScrollView hsvColor, hsvFont;
    ImageView noteColorDef, noteColor1, noteColor2, noteColor3, noteColor4, noteColor5, noteColor6, noteColor7, noteColor8, noteColor9, noteColor10,
            noteColor11, noteColor12, noteColor13, noteColor14, noteColor15;

    Typeface typeface1, typeface2, typeface3, typeface4, typeface5, typeface6, typeface7, typeface8, typeface9, typeface10;
    TextView font1, font2, font3, font4, font5, font6, font7, font8, font9, font10;

    int noteColor = 0, noteFont = 0;
    float textSize;

    boolean edited, keypadOpened;

    SharedPreferences pref, sharedPreferencesStopAd;
    int rate;


    AdView mAdView1;
    InterstitialAd mInterstitialAd;
    Boolean check;


    void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(NotepadEditActivity.this, color));
        }
    }


    private void initAds() {
        mInterstitialAd = new InterstitialAd(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad_edit);


        getSupportActionBar().setTitle("Notepad");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary3)));
        setStatusBarColor(R.color.colorPrimaryDark3);

        db = AppDatabase.getInstance(this);

        initPref();
        initAds();
        if (check) {
            showAds();
        }

        initViews();
        setNotepadPropertiesIfAvialable();

        seekBarTextSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int min=10;
                if(progress>min) {
                    if (fromUser) {
                        if (!editTextTitle.hasFocus()) {
                            if (progress <= 10) {
                                progress = 10;
                            }
                            editTextContent.setTextSize(progress);
                            textSize = progress;
                        }
                    }
                }

            }
        });


// for observing softkey of device whether it is opened or closed

        linearLayoutNotepadColor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {  //  linearLayoutNotepadColor is root layout of activity

                Rect r = new Rect();
                linearLayoutNotepadColor.getWindowVisibleDisplayFrame(r);
                int screenHeight = linearLayoutNotepadColor.getRootView().getHeight();
                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened

                    linearLayoutTools.setVisibility(View.GONE);

                } else {
                    // keyboard is closed

                    if (buttonEdit.getVisibility() == View.VISIBLE) {
                        linearLayoutTools.setVisibility(View.GONE);
                    } else {
                        linearLayoutTools.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


    }


    private void initPref() {

        pref = NotepadEditActivity.this.getSharedPreferences("MyPref", NotepadEditActivity.this.MODE_PRIVATE);
        rate = pref.getInt("key", 0);


        sharedPreferencesStopAd = NotepadEditActivity.this.getSharedPreferences("payment", Context.MODE_PRIVATE);
        check = sharedPreferencesStopAd.getBoolean("check", true);

        if (rate == 2) {
            saveBox1();
        }
    }

    private void setNotepadPropertiesIfAvialable() {

        notepad = (Notepad) getIntent().getSerializableExtra("notepad_position");

        boolean fromEdit=getIntent().getBooleanExtra("fromEdit",false);

        if (notepad != null && fromEdit) {

            buttonEdit.setVisibility(View.GONE);
            editTextTitle.setFocusableInTouchMode(true);
            editTextContent.setFocusableInTouchMode(true);
            edited = true;

            editTextTitle.setText(notepad.getTitle());
            editTextContent.setText(notepad.getDescription());


            noteColor = notepad.getColor();
            noteFont = notepad.getFont();
            textSize = notepad.getTextsize();
            seekBarTextSize.setProgress((int) notepad.getTextsize());
            setNotepadProperties(notepad);

        }
        if (notepad != null && !fromEdit) {
            buttonEdit.setVisibility(View.VISIBLE);
            editTextTitle.setFocusableInTouchMode(false);
            editTextContent.setFocusableInTouchMode(false);

            editTextTitle.setText(notepad.getTitle());
            editTextContent.setText(notepad.getDescription());


            noteColor = notepad.getColor();
            noteFont = notepad.getFont();
            textSize = notepad.getTextsize();
            seekBarTextSize.setProgress((int) notepad.getTextsize());
            setNotepadProperties(notepad);

        } else {

            linearLayoutTools.setVisibility(View.VISIBLE);

            buttonEdit.setVisibility(View.GONE);
            editTextTitle.setFocusableInTouchMode(true);
            editTextContent.setFocusableInTouchMode(true);

            float px = editTextContent.getTextSize();//getting pixel
            float scaledDensity = getResources().getDisplayMetrics().scaledDensity;//getting density
            textSize = px / scaledDensity;//converting px to sp
        }

    }


    private void setNotepadProperties(Notepad note) {

        editTextTitle.setText(note.getTitle());
        editTextContent.setText(note.getDescription());
        getNoteFont(note.getFont());
        getNoteColor(note.getColor());
        editTextContent.setTextSize(note.getTextsize());
    }


    @Override
    public void onBackPressed() {

        if (editTextTitle.getText().toString().trim().equals("")
                && editTextContent.getText().toString().trim().equals("")) {
            finish();
        } else {
            if (!edited && notepad != null) {
                finish();
            } else {
                new android.support.v7.app.AlertDialog.Builder(NotepadEditActivity.this)
                        .setMessage("Want to save?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (rate == 0) {
                                    pref = NotepadEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    rate = 1;
                                    editor.putInt("key", rate);
                                    editor.apply();
                                } else if (rate == 1) {
                                    pref = NotepadEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    rate = 2;
                                    editor.putInt("key", rate);
                                    editor.apply();
                                }

                                saveNotepad();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }

        }
    }


    @SuppressLint("StaticFieldLeak")
    private void saveNotepad() {

        if (notepad == null) {
            notepad = new Notepad();
        }

        notepad.setTitle(editTextTitle.getText().toString());
        notepad.setDescription(editTextContent.getText().toString());
        notepad.setColor(noteColor);
        notepad.setFont(noteFont);
        notepad.setTextsize(textSize);

        @SuppressLint("SimpleDateFormat")
        DateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        notepad.setDate(date);


        new AsyncTask<Notepad, Void, Void>() {
            @Override
            protected Void doInBackground(Notepad... params) {

                Notepad saveNote = params[0];

                if (saveNote.getId() > 0) {
                    db.getNotepadDao().updateAll(saveNote);
                } else {
                    SharedPrefManager.getInstance(NotepadEditActivity.this).setItemInserted(true);
                    db.getNotepadDao().insertAll(saveNote);
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
        }.execute(notepad);
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
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad1);
        } else if (noteColor == 2) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad2);
        } else if (noteColor == 3) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad3);
        } else if (noteColor == 4) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad4);
        } else if (noteColor == 5) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad5);
        } else if (noteColor == 6) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad6);
        } else if (noteColor == 7) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad7);
        } else if (noteColor == 8) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad8);
        } else if (noteColor == 9) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad9);
        } else if (noteColor == 10) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad10);
        } else if (noteColor == 11) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad11);
        } else if (noteColor == 12) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad12);
        } else if (noteColor == 13) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad13);
        } else if (noteColor == 14) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad14);
        } else if (noteColor == 15) {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepad15);
        } else {
            linearLayoutNotepadColor.setBackgroundResource(R.color.notepadDefault);
        }
    }


    private void getNoteFont(int noteFont) {

        if (noteFont == 1) {
            editTextTitle.setTypeface(typeface1);
            editTextContent.setTypeface(typeface1);
        } else if (noteFont == 2) {
            editTextTitle.setTypeface(typeface2);
            editTextContent.setTypeface(typeface2);
        } else if (noteFont == 3) {
            editTextTitle.setTypeface(typeface3);
            editTextContent.setTypeface(typeface3);
        } else if (noteFont == 4) {
            editTextTitle.setTypeface(typeface4);
            editTextContent.setTypeface(typeface4);
        } else if (noteFont == 5) {
            editTextTitle.setTypeface(typeface5);
            editTextContent.setTypeface(typeface5);
        } else if (noteFont == 6) {
            editTextTitle.setTypeface(typeface6);
            editTextContent.setTypeface(typeface6);
        } else if (noteFont == 7) {
            editTextTitle.setTypeface(typeface7);
            editTextContent.setTypeface(typeface7);
        } else if (noteFont == 8) {
            editTextTitle.setTypeface(typeface8);
            editTextContent.setTypeface(typeface8);
        } else if (noteFont == 9) {
            editTextTitle.setTypeface(typeface9);
            editTextContent.setTypeface(typeface9);
        } else if (noteFont == 10) {
            editTextTitle.setTypeface(typeface10);
            editTextContent.setTypeface(typeface10);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ibDone:

                if (editTextTitle.getText().toString().trim().equals("") && editTextContent.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Write something", Toast.LENGTH_SHORT).show();
                } else {

                    if (rate == 0) {
                        pref = NotepadEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        rate = 1;
                        editor.putInt("key", rate);
                        editor.apply();
                    } else if (rate == 1) {
                        pref = NotepadEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        rate = 2;
                        editor.putInt("key", rate);
                        editor.apply();
                    }

                    saveNotepad();
                }

                break;

            case R.id.ibTextColor:
                hsvFont.setVisibility(View.GONE);
                hsvColor.setVisibility(View.VISIBLE);
                linearLayoutSeekbar.setVisibility(View.GONE);
                break;


            case R.id.ibTextFont:
                hsvColor.setVisibility(View.GONE);
                hsvFont.setVisibility(View.VISIBLE);
                linearLayoutSeekbar.setVisibility(View.GONE);
                break;

            case R.id.ibTextSize:

                if (editTextTitle.hasFocus()) {
                    Toast.makeText(getApplicationContext(), "Title size can't increased", Toast.LENGTH_SHORT).show();
                } else {
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
                editTextContent.setFocusableInTouchMode(true);

                break;


            case R.id.NoteColorDefault:
                getNoteColor(0);
                noteColor = 0;
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


        buttonEdit = findViewById(R.id.btnEdit);
        buttonEdit.setVisibility(View.GONE);
        editTextTitle = findViewById(R.id.titleNotepad);
        editTextContent = findViewById(R.id.contentNotepad);


        buttonColor = findViewById(R.id.ibTextColor);
        buttonFont = findViewById(R.id.ibTextFont);
        buttonDone = findViewById(R.id.ibDone);
        buttonSize = findViewById(R.id.ibTextSize);


        hsvColor = findViewById(R.id.horizontalColor);
        hsvFont = findViewById(R.id.horizontalFont);


        hsvColor.setVisibility(View.GONE);
        hsvFont.setVisibility(View.GONE);


        buttonColor.setOnClickListener(this);
        buttonFont.setOnClickListener(this);
        buttonSize.setOnClickListener(this);
        buttonDone.setOnClickListener(this);
        buttonEdit.setOnClickListener(this);


        noteColorDef = (ImageView) findViewById(R.id.NoteColorDefault);
        noteColor1 = (ImageView) findViewById(R.id.NoteColor1);
        noteColor2 = (ImageView) findViewById(R.id.NoteColor2);
        noteColor3 = (ImageView) findViewById(R.id.NoteColor3);
        noteColor4 = (ImageView) findViewById(R.id.NoteColor4);
        noteColor5 = (ImageView) findViewById(R.id.NoteColor5);
        noteColor6 = (ImageView) findViewById(R.id.NoteColor6);
        noteColor7 = (ImageView) findViewById(R.id.NoteColor7);
        noteColor8 = (ImageView) findViewById(R.id.NoteColor8);
        noteColor9 = (ImageView) findViewById(R.id.NoteColor9);
        noteColor10 = (ImageView) findViewById(R.id.NoteColor10);
        noteColor11 = (ImageView) findViewById(R.id.NoteColor11);
        noteColor12 = (ImageView) findViewById(R.id.NoteColor12);
        noteColor13 = (ImageView) findViewById(R.id.NoteColor13);
        noteColor14 = (ImageView) findViewById(R.id.NoteColor14);
        noteColor15 = (ImageView) findViewById(R.id.NoteColor15);


        noteColorDef.setOnClickListener(this);
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

        linearLayoutNotepadColor = findViewById(R.id.linearNotepadColor);

        linearLayoutSeekbar = (LinearLayout) findViewById(R.id.linearLayoutSeekbar);
        linearLayoutSeekbar.setVisibility(View.GONE);
        linearLayoutTools = findViewById(R.id.linearTools);


        seekBarTextSize = (SeekBar) findViewById(R.id.seekbarTexrSize);

        setupFonts();
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
                startActivity(new Intent(NotepadEditActivity.this, GiftsAndOffers.class));
                break;

            case R.id.shareapp:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, I found this cool app \""+getResources().getString(R.string.app_name)+"\" on play store.\n"
                        +"Download this FREE app here:\n\n "+getResources().getString(R.string.applink));
                intent.setType("text/plain");
                startActivity(intent);
                break;

            case R.id.youtube:

                youTubeSubscribed();

//                boolean  subscribed = pref.getBoolean("subscribed", false);
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

    void youTubeSubscribed(){
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

        mAdView1 = (AdView) findViewById(R.id.adViewbanner1);

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

        final Dialog dialog = new AlertDialog.Builder(NotepadEditActivity.this).create();
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
                pref = NotepadEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
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
                pref = NotepadEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
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
                    Toast.makeText(NotepadEditActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
