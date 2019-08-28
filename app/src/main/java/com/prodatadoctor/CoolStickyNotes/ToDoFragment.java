package com.prodatadoctor.CoolStickyNotes;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.prodatadoctor.CoolStickyNotes.AdapterCallback.AlarmCallback;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.ItemClicked;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.OptionSelected;
import com.prodatadoctor.CoolStickyNotes.Notifications.Receivers.MyAlarmReceiver;
import com.prodatadoctor.CoolStickyNotes.adapter.ToDoAdapter;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class ToDoFragment extends Fragment implements OptionSelected, ItemClicked, AlarmCallback {

    public static final String TITLE = "To-Do List";

    public static ToDoFragment newInstance() {

        return new ToDoFragment();
    }


    List<ToDo> todoListSelected;
    public static boolean isMultiSelect = false;
    public static android.view.ActionMode mActionMode;
    Menu context_menu;


    String[] category;

    private AppDatabase db;

    List<ToDo> todoList;

    RecyclerView recyclerView;
    ToDoAdapter adapter;
    ToDo todo;

    ImageView emptyView;

    View view;

    //very minute load data and refresh recyclerview
    BroadcastReceiver receiver;
    @Override
    public void onStart() {
        super.onStart();
        receiver = new MinuteElapseReceiver(intent -> {
            //loadTodoList();
            adapter.notifyDataSetChanged();
        });

        getActivity().registerReceiver(receiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_to_do, container, false);


        initPref();
        if (check) {
            showAds();
        }

        setHasOptionsMenu(true);

        db = AppDatabase.getInstance(getActivity());
        todo = new ToDo();
        todoList = new ArrayList<>();
        todoListSelected = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.tvNoImage);

        return view;
    }


    @Override
    public void clickedDelete() {
        loadTodoList();
    }


    @Override
    public void onResume() {
        super.onResume();

        loadTodoList();
    }


    @SuppressLint("StaticFieldLeak")
    public void loadTodoList() {
        new AsyncTask<Void, Void, List<ToDo>>() {
            @Override
            protected List<ToDo> doInBackground(Void... params) {
                return db.getToDoDao().getAll();
            }

            @Override
            protected void onPostExecute(List<ToDo> notes) {
                super.onPostExecute(notes);
                todoList.clear();
                todoList = notes;
                loadAndRefreashData(todoList);
            }

        }.execute();
    }


    public void loadAndRefreashData(List<ToDo> todoList) {
        orderByDateAndTime(todoList);

        for (ToDo item : todoList) {
            setAlarmForItems(item);
        }

        adapter = new ToDoAdapter(getActivity(), todoList, todoListSelected, this, this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setAdapter(adapter);
        setEmptyView();

//        if(SharedPrefManager.getInstance(getContext()).isItemInsertedToDo())
//            try {
//                MyJsonToDo.saveToDoData(MyJsonToDo.getToDoJSON(todoList), DataBackUpActivity.INSTANT_BACKUP,"ToDoTask");
//                SharedPrefManager.getInstance(getActivity()).setItemInsertedToDo(false);//vhnjdfkj hdfkjjgdfjihgdf ogidfjgo
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
    }


    private void setEmptyView() {
        if (todoList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }


    public static void orderByDateAndTime(List<ToDo> Items) {

        //this is the date format stored in database(date + time)
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

        //sorting the date according to recent time from now, if date and time are null return -1
        Collections.sort(Items, new Comparator() {

            public int compare(Object o1, Object o2) {

                String x1 = ((ToDo) o1).getDate() + " " + ((ToDo) o1).getTime();
                String x2 = ((ToDo) o2).getDate() + " " + ((ToDo) o2).getTime();

                // return compare(x1,x2);

                if (((ToDo) o1).getDate().equals("")) {
                    return -1;
                }
                try {
                    return dateFormat.parse(x1).compareTo(dateFormat.parse(x2));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return 0;
            }
        });
    }


    SharedPreferences pref, sharedPreferencesStopAd;
    int rate = 0;
    static Boolean check;
    AdView mAdView1;

    private void initPref() {

        sharedPreferencesStopAd = getActivity().getSharedPreferences("payment", MODE_PRIVATE);
        check = sharedPreferencesStopAd.getBoolean("check", true);

        pref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        rate = pref.getInt("key", 0);
    }

    private void showAds() {

        mAdView1 = view.findViewById(R.id.adViewbanner1);
        //   mAdView2 = view.findViewById(R.id.adViewbanner2);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest);
        //    mAdView2.loadAd(adRequest);

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


    //===============================================================  Action Mode  ===========================================================================

    private void singleClickActionMode(int position) {
        if (isMultiSelect) {
            multi_select(position);
        } else {
            startActivity(new Intent(getActivity(), ToDoEditActivity.class).putExtra("todo_position", todoList.get(position)));
        }
    }


    private void longClickActionMode(int position) {
        if (!isMultiSelect) {
            todoListSelected = new ArrayList<>();
            isMultiSelect = true;

            if (mActionMode == null) {
                mActionMode = getActivity().startActionMode(mActionModeCallback);
            }
        }

        multi_select(position);
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (todoListSelected.contains(todoList.get(position)))
                todoListSelected.remove(todoList.get(position));
            else
                todoListSelected.add(todoList.get(position));

            if (todoListSelected.size() > 0) {
                mActionMode.setTitle(todoListSelected.size() + " item selected");
            } else {
                mActionMode.setTitle("");
                mActionMode.finish();
            }

            refreshAdapter();
        }
    }


    public void refreshAdapter() {
        adapter.aListSelected = todoListSelected;
        adapter.aList = todoList;
        adapter.notifyDataSetChanged();
    }


    private android.view.ActionMode.Callback mActionModeCallback = new android.view.ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_actionmode_multi_select, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:

                    new android.support.v7.app.AlertDialog.Builder(getContext())
                            .setMessage("Want to delete selected items?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    for (ToDo toDo : todoListSelected) {
                                        deleteToDo(toDo);
                                        cancelAlarmForItem(todo);
                                    }

                                    if (todoListSelected.size() > 0) {
                                        for (int i = 0; i < todoListSelected.size(); i++)
                                            todoList.remove(todoListSelected.get(i));

                                        adapter.notifyDataSetChanged();

                                        if (mActionMode != null) {
                                            mActionMode.finish();
                                        }

                                        loadTodoList();//refreshing in case of ToDo only
                                    }
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            todoListSelected = new ArrayList<>();
            refreshAdapter();
        }
    };

    @Override
    public void singleClick(int position) {
        singleClickActionMode(position);
    }

    @Override
    public void longClick(int position) {
        longClickActionMode(position);
    }


    @SuppressLint("StaticFieldLeak")
    private void deleteToDo(ToDo note) {
        new AsyncTask<ToDo, Void, Void>() {
            @Override
            protected Void doInBackground(ToDo... params) {
                db.getToDoDao().deleteAll(params);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute(note);
    }


    @Override
    public void setAlarm(ToDo todo) {

        Long x = todo.getId();
        int reqC = x.intValue();

        String strDate = todo.getDate() + " " + todo.getTime();

        Date date = null;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        try {
            date = dateFormat.parse(strDate);
            Log.e("Date", "" + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

            if (System.currentTimeMillis() < date.getTime()) {
                if (todo.isAlarmOn()) {
                    //    Toast.makeText(getContext(), "Reminder on", Toast.LENGTH_SHORT).show();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    Log.e("date", "year=" + calendar.get(Calendar.YEAR) + " month=" + calendar.get(Calendar.MONTH) + " day=" + calendar.get(Calendar.DAY_OF_MONTH)
                            + " hour=" + calendar.get(Calendar.HOUR_OF_DAY) + " min=" + calendar.get(Calendar.MINUTE));

                    Bundle args = new Bundle();
                    args.putSerializable("birthday", todo);

                    Intent intent1 = new Intent(getContext(), MyAlarmReceiver.class).putExtra("DATA", args);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), reqC, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                    am.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

                } else {
                    // Toast.makeText(getContext(), "Reminder off", Toast.LENGTH_SHORT).show();
                    cancelAlarmForItem(todo);
                }
            } else {
                //Toast.makeText(getContext(), "Reminder time has been passed!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAlarmForItems(ToDo todo) {
        Long x = todo.getId();
        int reqC = x.intValue();

        String strDate = todo.getDate() + " " + todo.getTime();

        Date date = null;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        try {
            date = dateFormat.parse(strDate);
            Log.e("Date", "" + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

            if (System.currentTimeMillis() < date.getTime()) {
                if (todo.isAlarmOn()) {
                //    Toast.makeText(getContext(), "Reminder on", Toast.LENGTH_SHORT).show();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    Log.e("date", "year=" + calendar.get(Calendar.YEAR) + " month=" + calendar.get(Calendar.MONTH) + " day=" + calendar.get(Calendar.DAY_OF_MONTH)
                            + " hour=" + calendar.get(Calendar.HOUR_OF_DAY) + " min=" + calendar.get(Calendar.MINUTE));

                    Bundle args = new Bundle();
                    args.putSerializable("birthday", todo);

                    Intent intent1 = new Intent(getContext(), MyAlarmReceiver.class).putExtra("DATA", args);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), reqC, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

                    am.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

                } else {
                 //   Toast.makeText(getContext(), "Reminder off", Toast.LENGTH_SHORT).show();
                    cancelAlarmForItem(todo);
                }
            } else {
              //  Toast.makeText(getContext(), "Reminder time has been passed!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void cancelAlarmForItem(ToDo todo) {
        Long x = todo.getId();
        int reqC = x.intValue();

        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getContext(), MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), reqC, myIntent, 0);
        am.cancel(pendingIntent);
    }

}
