package com.prodatadoctor.CoolStickyNotes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.prodatadoctor.CoolStickyNotes.SharedPrefManager.SharedPrefManager;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;

public class ToDoEditActivity extends AppCompatActivity implements View.OnClickListener {

    private AppDatabase db;
    ToDo toDo, receivedFromNotificationToDo;

    Spinner spinCategory;
    ArrayAdapter<String> adapter;

    TextView textViewCategory;
    EditText editTextTask, editTextDate, editTextTime;

    ImageButton imageButtonRemoveDate, imageButtonRemoveTime, imageButtonCalendar, imageButtonClock;
    Button buttonSaveToDo,buttonCancelToDo;

    LinearLayout linearLayoutDate, linearLayoutTime;
    int mYear, mMonth, mDay, mHour, mMinute;


    TextView textViewReminder;
    Switch aSwitchReminder;

    SharedPreferences pref, sharedPreferencesStopAd;
    int rate;

    AdView mAdView1, mAdView2;
    InterstitialAd mInterstitialAd;
    Boolean check;


    void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(ToDoEditActivity.this, color));
        }
    }


    private void initAds() {
        mInterstitialAd = new InterstitialAd(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_edit);

        // initializeWorker();

        getSupportActionBar().setTitle("Memo and To-Do Task");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary2)));
        setStatusBarColor(R.color.colorPrimaryDark);

        db = AppDatabase.getInstance(this);

        initPref();
        initAds();

        if (check) {
            showAds();
        }


        initViews();


        receivedFromNotificationToDo = (ToDo) getIntent().getSerializableExtra("notification_item");//receiving intent data from notification

        if (receivedFromNotificationToDo != null) {

            toDo = receivedFromNotificationToDo;

            buttonSaveToDo.setVisibility(View.GONE);
            buttonCancelToDo.setVisibility(View.GONE);

            editTextTask.setFocusable(false);

            editTextDate.setEnabled(false);
            editTextTime.setEnabled(false);
            spinCategory.setEnabled(false);
            aSwitchReminder.setEnabled(false);
            imageButtonCalendar.setEnabled(false);
            imageButtonClock.setEnabled(false);
            imageButtonRemoveDate.setEnabled(false);
            imageButtonRemoveTime.setEnabled(false);

            Log.e("noti code", "" + receivedFromNotificationToDo.getId());
        } else {
            toDo = (ToDo) getIntent().getSerializableExtra("todo_position");//receiving intent data from previous activity
        }


        if (toDo != null) {
            editTextTask.setText(toDo.getContent());
            editTextDate.setText(toDo.getDate());
            editTextTime.setText(toDo.getTime());
            textViewCategory.setText(toDo.getCategory());
            toDo.setAlarmOn(toDo.isAlarmOn());
            aSwitchReminder.setChecked(toDo.isAlarmOn());

//            if (toDo.getTime().equals("23:59")) {
//                editTextTime.setText("");
//            }

            String string = toDo.getCategory();
            int spinnerPosition = adapter.getPosition(string);
            spinCategory.setSelection(spinnerPosition);
        }
    }


    private void initPref() {

        pref = ToDoEditActivity.this.getSharedPreferences("MyPref", ToDoEditActivity.this.MODE_PRIVATE);
        rate = pref.getInt("key", 0);


        sharedPreferencesStopAd = ToDoEditActivity.this.getSharedPreferences("payment", Context.MODE_PRIVATE);
        check = sharedPreferencesStopAd.getBoolean("check", true);

        if (rate == 2) {
            saveBox1();
        }
    }


    @Override
    public void onBackPressed() {

        if (receivedFromNotificationToDo != null) {
            startActivity(new Intent(ToDoEditActivity.this, MainActivity.class)
                    .putExtra("from_spalsh", false)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else {

            if (editTextTask.getText().toString().trim().equals("") && editTextDate.getText().toString().trim().equals("")) {
                finish();
            } else {

                if (rate == 0) {
                    pref = ToDoEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    rate = 1;
                    editor.putInt("key", rate);
                    editor.apply();
                } else if (rate == 1) {
                    pref = ToDoEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    rate = 2;
                    editor.putInt("key", rate);
                    editor.apply();
                }

                if (editTextDate.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "Date field required", Toast.LENGTH_SHORT).show();
                } else if (editTextTime.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "Time field required", Toast.LENGTH_SHORT).show();
                } else if (editTextTask.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "Task not entered.", Toast.LENGTH_SHORT).show();
                }else {
                    saveToDo();
                }
            }
        }

    }


    @SuppressLint("StaticFieldLeak")
    private void saveToDo() {

        if (toDo == null) {
            toDo = new ToDo();
        }

        toDo.setContent(editTextTask.getText().toString());

        toDo.setDate(editTextDate.getText().toString());

        String time = null;
        if (editTextTime.getText().toString().equals("")) {
            time = "23:59";
        } else {
            time = editTextTime.getText().toString();
        }
        toDo.setTime(time);

        toDo.setDatetime(getDateTimeSumLong());
        toDo.setCategory(textViewCategory.getText().toString());

        toDo.setAlarmOn(aSwitchReminder.isChecked());

        new AsyncTask<ToDo, Void, Void>() {
            @Override
            protected Void doInBackground(ToDo... params) {

                ToDo saveTask = params[0];

                if (saveTask.getId() > 0) {
                    db.getToDoDao().updateAll(saveTask);
                } else {
                    SharedPrefManager.getInstance(ToDoEditActivity.this).setItemInserted(true);
                    db.getToDoDao().insertAll(saveTask);
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
        }.execute(toDo);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.saveToDo:

                if (editTextTask.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "Task not entered.", Toast.LENGTH_SHORT).show();
                } else {

                    if (rate == 0) {

                        pref = ToDoEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        rate = 1;
                        editor.putInt("key", rate);
                        editor.apply();

                    } else if (rate == 1) {

                        pref = ToDoEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        rate = 2;
                        editor.putInt("key", rate);
                        editor.apply();
                    }

                    if (editTextDate.getText().toString().trim().equals("")) {
                        Toast.makeText(ToDoEditActivity.this, "Date field required", Toast.LENGTH_SHORT).show();
                    } else if (editTextTime.getText().toString().trim().equals("")) {
                        Toast.makeText(ToDoEditActivity.this, "Time field required", Toast.LENGTH_SHORT).show();
                    } else {
                        saveToDo();
                    }
                }

                break;

            case R.id.cancelToDo:
                finish();
                break;

            case R.id.ic_calendar:
                showDatePicker();
                break;

            case R.id.ic_clock:
                showTimePicker();
                break;

            case R.id.removeDate:
                removeDateAndTime();
                break;

            case R.id.removeTime:
                removeTime();
                break;

            case R.id.etDateToDo:
                showDatePicker();
                break;

            case R.id.etTimeToDo:
                showTimePicker();
                break;
        }
    }

    private void removeTime() {
        editTextTime.setText("");
    }

    private void removeDateAndTime() {
        editTextDate.setText("");
        editTextTime.setText("");
    }


    private void showDatePicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {

                        editTextDate.setText(convertDate(dayOfMonth) + "-" + convertDate(monthOfYear + 1) + "-" + year);
                        linearLayoutTime.setVisibility(View.VISIBLE);
                        //imageButtonRemoveDate.setVisibility(View.VISIBLE);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }


    private void showTimePicker() {

        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    editTextTime.setText(convertDate(hourOfDay) + ":" + convertDate(minute));
                    //imageButtonRemoveTime.setVisibility(View.VISIBLE);
                }, mHour, mMinute, false);

        timePickerDialog.setCancelable(false);
        timePickerDialog.show();
    }


    public String convertDate(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }


    private void initViews() {

        textViewReminder = findViewById(R.id.tvReminder);
        aSwitchReminder = findViewById(R.id.switchReminder);
        aSwitchReminder.setChecked(true);
        aSwitchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                Toast.makeText(ToDoEditActivity.this, "Reminder On", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ToDoEditActivity.this, "Reminder Off", Toast.LENGTH_SHORT).show();
            }
        });


        buttonSaveToDo = findViewById(R.id.saveToDo);
        buttonSaveToDo.setOnClickListener(this);
        buttonCancelToDo = findViewById(R.id.cancelToDo);
        buttonCancelToDo.setOnClickListener(this);

        imageButtonRemoveDate = findViewById(R.id.removeDate);
        imageButtonRemoveDate.setVisibility(View.GONE);
        imageButtonRemoveTime = findViewById(R.id.removeTime);
        imageButtonRemoveTime.setVisibility(View.GONE);
        imageButtonCalendar = findViewById(R.id.ic_calendar);
        imageButtonClock = findViewById(R.id.ic_clock);

        editTextTask = findViewById(R.id.etTaskToDo);
        editTextDate = findViewById(R.id.etDateToDo);
        editTextTime = findViewById(R.id.etTimeToDo);
        linearLayoutDate = findViewById(R.id.linearDateToDo);
        linearLayoutTime = findViewById(R.id.linearTimeToDo);

        imageButtonRemoveDate.setOnClickListener(this);
        imageButtonRemoveTime.setOnClickListener(this);
        imageButtonCalendar.setOnClickListener(this);
        imageButtonClock.setOnClickListener(this);

        editTextDate.setOnClickListener(this);
        editTextTime.setOnClickListener(this);

        textViewCategory = findViewById(R.id.tvCategory);

        spinCategory = findViewById(R.id.spinToDo);//fetch the spinner from layout file
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources()
                .getStringArray(R.array.todo_category_array)) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };//setting the country_array to spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCategory.setAdapter(adapter);

//
//        if (toDo != null) {
//            String string = toDo.getCategory();
//            int spinnerPosition = adapter.getPosition(string);
//            spinCategory.setSelection(spinnerPosition);
//        }


//if you want to set any action you can do in this listener
        spinCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

                if (position == 0) {

                } else {
                    textViewCategory.setText(adapter.getItem(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


//-------------------------------------------------------------------------------------------------------------------------------------------------------------


    public long getDateTimeSumLong() {

        long num = 0;

        if (!TextUtils.isEmpty(editTextDate.getText().toString())) {

            String str = editTextDate.getText().toString() + editTextTime.getText().toString();
            str = str.replaceAll("[^\\d.]", "");

            num = Long.parseLong(str);
        }
        return num;
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
                startActivity(new Intent(ToDoEditActivity.this, GiftsAndOffers.class));
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

        subscribeNow.setOnClickListener(v -> {
            dialog.dismiss();
            youTubeSubscribed();
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
        mAdView2 = findViewById(R.id.adViewbanner2);


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


    public void saveBox1() {

        final Dialog dialog = new AlertDialog.Builder(ToDoEditActivity.this).create();
        dialog.show();
        dialog.setContentView(R.layout.rateus);
        dialog.setCancelable(false);
        dialog.setTitle("");
        Button bawesome = dialog.findViewById(R.id.bawesome);
        Button breason = dialog.findViewById(R.id.breason);
        Button blater = dialog.findViewById(R.id.blater);


        bawesome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate = 3;
                dialog.dismiss();
                pref = ToDoEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
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
                pref = ToDoEditActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("key", rate);
                editor.apply();

                Intent i = new Intent(Intent.ACTION_SEND);
                //   i.setType("message/rfc822");
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@prodatadoctor.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "I did not like your App " + getResources().getString(R.string.app_name));
                i.putExtra(Intent.EXTRA_TEXT, "Hey There! \n" +
                        "I downloaded your App " + getResources().getString(R.string.app_name) + " and it doesn’t merit a 5 star rating from me due to following reasons. Please help:\n");
                try {
                    startActivity(Intent.createChooser(i, "Perform action using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ToDoEditActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
