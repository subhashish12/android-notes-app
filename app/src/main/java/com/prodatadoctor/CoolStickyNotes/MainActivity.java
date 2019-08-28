package com.prodatadoctor.CoolStickyNotes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.prodatadoctor.CoolStickyNotes.AppDataBackup.JsonOperations.JsonGenerator;
import com.prodatadoctor.CoolStickyNotes.Notifications.Receivers.FirstRunReceiver;
import com.prodatadoctor.CoolStickyNotes.SharedPrefManager.SharedPrefManager;
import com.prodatadoctor.CoolStickyNotes.adapter.ViewPagerAdapter;
import com.prodatadoctor.CoolStickyNotes.dao.NoteDao;
import com.prodatadoctor.CoolStickyNotes.dao.NotepadDao;
import com.prodatadoctor.CoolStickyNotes.dao.ToDoDao;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.Note;
import com.prodatadoctor.CoolStickyNotes.domain.Notepad;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;
import com.prodatadoctor.CoolStickyNotes.util.IabHelper;
import com.prodatadoctor.CoolStickyNotes.util.IabResult;
import com.prodatadoctor.CoolStickyNotes.util.Inventory;
import com.prodatadoctor.CoolStickyNotes.util.Purchase;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    TabLayout mTabLayout;

    private FloatingActionButton fab;

    SharedPreferences pref, sharedPreferencesStopAd;
    int rate = 0;
    static Boolean check;

    IabHelper mHelper;
    static final String ITEM_SKU = "stop.ads";

    InterstitialAd mInterstitialAd;

    private void initAds() {
        mInterstitialAd = new InterstitialAd(this);
    }


    List<Note> noteList;
    List<Notepad> notepadList;
    List<ToDo> toDoList;


    boolean fromSplash;


//    int rq;
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if(rq!=-1){
//            startActivity(new Intent(MainActivity.this,ToDoEditActivity.class).putExtra("rqcode",rq));
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  rq=getIntent().getIntExtra("notification_item",-1);

        fromSplash = getIntent().getBooleanExtra("from_spalsh", false);

        noteList = new ArrayList<>();
        notepadList = new ArrayList<>();
        toDoList = new ArrayList<>();


        if (!SharedPrefManager.getInstance(this).isFirstRun()) {

            startWeeklyBackup();

            SharedPrefManager.getInstance(this).setFirstRun(true);
        }
        initPref();
        initAds();
        //showAds();


        fab = findViewById(R.id.fabAdd);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ToDoFragment.mActionMode != null) ToDoFragment.mActionMode.finish();
                if (StickyNotesFragment.mActionMode != null)
                    StickyNotesFragment.mActionMode.finish();
                if (NotepadFragment.mActionMode != null) NotepadFragment.mActionMode.finish();

                if (mViewPager.getCurrentItem() == 0) {
                    startActivity(new Intent(MainActivity.this, StickyNotesEditActivity.class));
                } else if (mViewPager.getCurrentItem() == 1) {
                    startActivity(new Intent(MainActivity.this, ToDoEditActivity.class));
                } else if (mViewPager.getCurrentItem() == 2) {
                    startActivity(new Intent(MainActivity.this, NotepadEditActivity.class));
                }
            }
        });


        setViewPager();
        if (!fromSplash) {
            mViewPager.setCurrentItem(1);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mViewPagerAdapter.notifyDataSetChanged();
                animateFab(position);

                if (position == 0) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                    mTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    setStatusBarColor(R.color.colorPrimaryDark);
                } else if (position == 1) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary2)));
                    mTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary2));
                    setStatusBarColor(R.color.colorPrimaryDark2);
                } else {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary3)));
                    mTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary3));
                    setStatusBarColor(R.color.colorPrimaryDark3);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (ToDoFragment.mActionMode != null) ToDoFragment.mActionMode.finish();
                if (StickyNotesFragment.mActionMode != null) StickyNotesFragment.mActionMode.finish();
                if (NotepadFragment.mActionMode != null) NotepadFragment.mActionMode.finish();
            }
        });

    }

    private void startWeeklyBackup() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent1 = new Intent(getApplicationContext(), FirstRunReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 10005, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }


    void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, color));
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (ToDoFragment.mActionMode != null) ToDoFragment.mActionMode.finish();
        if (StickyNotesFragment.mActionMode != null) StickyNotesFragment.mActionMode.finish();
        if (NotepadFragment.mActionMode != null) NotepadFragment.mActionMode.finish();
    }

    @Override
    public void onBackPressed() {

        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                .setMessage("Want to close the app?")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void setViewPager() {

        mViewPager = findViewById(R.id.pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);//for not to referesh tab fragment each time we swipe page or tap on tab.


        mTabLayout = findViewById(R.id.tab);
//        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//making tablayout scrollable
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager, true);

        setDividerInTabLayout();
    }


    public void setDividerInTabLayout() {

        View root = mTabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.colorCheckRegular));
            drawable.setSize(4, 1);
            ((LinearLayout) root).setDividerPadding(10);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
    }


    int[] colorIntArray = {R.color.colorPrimary, R.color.colorPrimary2, R.color.colorPrimary3};
    int[] iconIntArray = {R.drawable.plus, R.drawable.plus, R.drawable.plus};


    protected void animateFab(final int position) {
        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }


            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[position]));
                fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), iconIntArray[position]));


                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        fab.startAnimation(shrink);
    }


    private void initPref() {

        sharedPreferencesStopAd = MainActivity.this.getSharedPreferences("payment", MODE_PRIVATE);
        check = sharedPreferencesStopAd.getBoolean("check", true);

        pref = MainActivity.this.getSharedPreferences("MyPref", MODE_PRIVATE);
        rate = pref.getInt("key", 0);

        String base64EncodedPublicKey = getResources().getString(R.string.base64EncodedPublicKey);

        try {
            mHelper = new IabHelper(MainActivity.this, base64EncodedPublicKey);
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

                @Override
                public void onIabSetupFinished(IabResult result) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.stop_ad:

                try {
                    mHelper.launchPurchaseFlow(MainActivity.this, ITEM_SKU, 10001, mPurchaseFinishedListener, "mypurchasetoken");
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                    Toast.makeText(MainActivity.this, "Please add your google account", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.about_us:
                startActivity(new Intent(MainActivity.this, AboutUs.class));
                break;

            case R.id.gift:
                startActivity(new Intent(MainActivity.this, GiftsAndOffers.class));
                break;

            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, I found this cool app \"" + getResources().getString(R.string.app_name) + "\" on play store.\n"
                        + "Download this FREE app here:\n\n " + getResources().getString(R.string.applink));
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share By:"));
                break;

            case R.id.rate_us:
                Uri uri = Uri.parse(getResources().getString(R.string.applink)); // missing 'http://' will cause crashed
                Intent intent3 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent3);
                break;

            case R.id.privacy_policy:
                Uri uri2 = Uri.parse("http://www.prodatadoctor.com/prodata/privacy-policy.html");
                Intent intent2 = new Intent(Intent.ACTION_VIEW, uri2);
                startActivity(intent2);
                break;

            case R.id.tech_support:
                startActivity(new Intent(MainActivity.this, TechnicianActivity.class));
                break;

            case R.id.backup:
                startActivity(new Intent(MainActivity.this, DataBackUpActivity.class));
                break;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (SharedPrefManager.getInstance(MainActivity.this).isItemInserted()) {
            new NoteBackupAsyncTask(AppDatabase.getInstance(MainActivity.this).getNoteDao()).execute();
            SharedPrefManager.getInstance(MainActivity.this).setItemInserted(false);
        }
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
            new NotepadBackupAsyncTask(AppDatabase.getInstance(MainActivity.this).getNotepadDao()).execute();
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
            new ToDoBackupAsyncTask(AppDatabase.getInstance(MainActivity.this).getToDoDao()).execute();
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
            String fileName = "Recent_Backup";
            try {
                JsonGenerator.writeJsonAndSave(JsonGenerator.getJSONObject(noteList, notepadList, toDoList), fileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void showAds() {

        if (check) {
            MobileAds.initialize(MainActivity.this, getString(R.string.appid));
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());

                    if (ToDoFragment.mActionMode != null) ToDoFragment.mActionMode.finish();
                    if (StickyNotesFragment.mActionMode != null)
                        StickyNotesFragment.mActionMode.finish();
                    if (NotepadFragment.mActionMode != null) NotepadFragment.mActionMode.finish();


                    if (mViewPager.getCurrentItem() == 0) {
                        startActivity(new Intent(MainActivity.this, StickyNotesEditActivity.class));
                    } else if (mViewPager.getCurrentItem() == 1) {
                        startActivity(new Intent(MainActivity.this, ToDoEditActivity.class));
                    } else if (mViewPager.getCurrentItem() == 2) {
                        startActivity(new Intent(MainActivity.this, NotepadEditActivity.class));
                    }
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                return;
            } else {
                consumeItem();
            }
        }
    };


    public void consumeItem() {
        Toast.makeText(getApplicationContext(), "consumeItem", Toast.LENGTH_SHORT).show();
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {
            } else {
                mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {

                        SharedPreferences.Editor editor = sharedPreferencesStopAd.edit();
                        editor.putBoolean("check", false);
                        editor.apply();
                        Toast.makeText(MainActivity.this, "Ads are disabled.", Toast.LENGTH_SHORT).show();

                    } else {
                    }
                }
            };

}
