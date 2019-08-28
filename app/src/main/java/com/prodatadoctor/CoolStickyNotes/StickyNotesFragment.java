package com.prodatadoctor.CoolStickyNotes;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.ItemClicked;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.OptionSelected;
import com.prodatadoctor.CoolStickyNotes.adapter.StickyNotesAdapter;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.Note;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class StickyNotesFragment extends Fragment implements OptionSelected, ItemClicked {

    private static final String TAG = "StickyNotesFragment";

    public static final String TITLE = "Sticky Notes";

    public static StickyNotesFragment newInstance() {

        return new StickyNotesFragment();
    }


    List<Note> noteListSelected;
    public static boolean isMultiSelect = false;
    public static android.view.ActionMode mActionMode;
    Menu context_menu;

    private AppDatabase db;

    List<Note> notesList;

    RecyclerView recyclerView;
    StickyNotesAdapter adapter;
    Note myFile;

    ImageView emptyView;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_stickt_notes, container, false);

        initPref();
        if (check) {
            showAds();
        }

        setHasOptionsMenu(true);

        db = AppDatabase.getInstance(getActivity());
        myFile = new Note();
        notesList = new ArrayList<>();
        noteListSelected = new ArrayList<>();

        emptyView = view.findViewById(R.id.tvNoImage);
        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }

    @Override
    public void clickedDelete() {
        loadNotes();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }


    @SuppressLint("StaticFieldLeak")
    private void loadNotes() {
        new AsyncTask<Void, Void, List<Note>>() {
            @Override
            protected List<Note> doInBackground(Void... params) {
                return db.getNoteDao().getAll();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);

                notesList = notes;
                loadAndRefreashData(notesList);
            }

        }.execute();
    }


    public void loadAndRefreashData(List<Note> noteList) {
        adapter = new StickyNotesAdapter(getActivity(), noteList, noteListSelected, this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
        //populateRecyclerView();   if want to show sectioned recyclerview
        setEmptyView();


//        if(SharedPrefManager.getInstance(getContext()).isItemInserted())
//        try {
//            MyJsonNote.saveNoteData(MyJsonNote.getNoteJSON(noteList), DataBackUpActivity.INSTANT_BACKUP,"StickyNotes");
//            SharedPrefManager.getInstance(getActivity()).setItemInserted(false);//vhnjdfkj hdfkjjgdfjihgdf ogidfjgo
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }


    private void setEmptyView() {
        if (notesList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
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


    //===============================================================  Action Mode  ===========================================================================

    private void singleClickActionMode(int position) {
        if (isMultiSelect) {
            multi_select(position);
        } else {
            startActivity(new Intent(getActivity(), StickyNotesEditActivity.class)
                    .putExtra("note_position", notesList.get(position))
                    .putExtra("fromEdit",false)
            );
        }
    }


    private void longClickActionMode(int position) {
        if (!isMultiSelect) {
            noteListSelected = new ArrayList<>();
            isMultiSelect = true;

            if (mActionMode == null) {
                mActionMode = ((AppCompatActivity) getActivity()).startActionMode(mActionModeCallback);
            }
        }

        multi_select(position);
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (noteListSelected.contains(notesList.get(position)))
                noteListSelected.remove(notesList.get(position));
            else
                noteListSelected.add(notesList.get(position));

            if (noteListSelected.size() > 0) {
                mActionMode.setTitle(noteListSelected.size() + " item selected");
            } else {
                mActionMode.setTitle("");
                mActionMode.finish();
            }

            refreshAdapter();
        }
    }


    public void refreshAdapter() {
        adapter.aListSelected = noteListSelected;
        adapter.aList = notesList;
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

                                    for (Note note : noteListSelected) {
                                        deleteNote(note);
                                    }

                                    if (noteListSelected.size() > 0) {
                                        for (int i = 0; i < noteListSelected.size(); i++)
                                            notesList.remove(noteListSelected.get(i));

                                            adapter.notifyDataSetChanged();

                                        if (mActionMode != null) {
                                            mActionMode.finish();
                                        }
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
            noteListSelected = new ArrayList<>();
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
    private void deleteNote(Note note) {
        new AsyncTask<Note, Void, Void>() {
            @Override
            protected Void doInBackground(Note... params) {
                db.getNoteDao().deleteAll(params);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute(note);
    }
}
