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
import com.google.android.gms.ads.InterstitialAd;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.ItemClicked;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.OptionSelected;
import com.prodatadoctor.CoolStickyNotes.SharedPrefManager.SharedPrefManager;
import com.prodatadoctor.CoolStickyNotes.adapter.NotepadAdapter;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.Notepad;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotepadFragment extends Fragment implements OptionSelected, ItemClicked {

    public static final String TITLE = "Notepad";

    public static NotepadFragment newInstance() {
        return new NotepadFragment();
    }


    private AppDatabase db;

    List<Notepad> notepadList;
    List<Notepad> notepadListSelected;
    public static boolean isMultiSelect = false;
    public static android.view.ActionMode mActionMode;
    Menu context_menu;

    RecyclerView recyclerView;
    NotepadAdapter notepadAdapter;
    Notepad todo;

    ImageView emptyView;

    public static boolean linear;

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notepad, container, false);

        setHasOptionsMenu(true);

        initPref();
        if (check) {
            showAds();
        }

        linear = SharedPrefManager.getInstance(getActivity()).getLayoutDesignLinear();

        db = AppDatabase.getInstance(getActivity());
        todo = new Notepad();
        notepadList = new ArrayList<>();
        notepadListSelected = new ArrayList<>();


        emptyView = view.findViewById(R.id.tvNoImage);
        recyclerView = view.findViewById(R.id.recyclerView);

        return view;
    }


    @Override
    public void clickedDelete() {
        loadNotepad();
    }


    @Override
    public void onResume() {
        super.onResume();

        getActivity().invalidateOptionsMenu();

        loadNotepad();
    }


    //================================================================ Database Operations ==================================================================================
    @SuppressLint("StaticFieldLeak")
    private void loadNotepad() {
        new AsyncTask<Void, Void, List<Notepad>>() {
            @Override
            protected List<Notepad> doInBackground(Void... params) {
                return db.getNotepadDao().getAll();
            }

            @Override
            protected void onPostExecute(List<Notepad> notes) {
                super.onPostExecute(notes);

                notepadList = notes;
                loadAndRefreashData();

//                if(SharedPrefManager.getInstance(getContext()).isItemInsertedNotepad())
//                    try {
//
//                        MyJsonNotepad.saveNotepadData(MyJsonNotepad.getNotepadJSON(notes), DataBackUpActivity.INSTANT_BACKUP,"Notepad");
//                        SharedPrefManager.getInstance(getActivity()).setItemInsertedNotepad(false);//vhnjdfkj hdfkjjgdfjihgdf ogidfjgo
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
            }

        }.execute();
    }


    public void loadAndRefreashData() {

        if (!linear) {
            setLayoutManagerAdapter(2);
        } else {
            setLayoutManagerAdapter(1);
        }
    }

    public void setLayoutManagerAdapter(int num) {

        notepadAdapter = new NotepadAdapter(getContext(), notepadList, notepadListSelected, num, this, this);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), num));
        recyclerView.setAdapter(notepadAdapter);

        setEmptyView();

    }


    private void setEmptyView() {
        if (notepadList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    //================================================================================================================================================================


    SharedPreferences pref, sharedPreferencesStopAd;
    int rate = 0;
    static Boolean check;
    AdView mAdView1, mAdView2;
    InterstitialAd mInterstitialAd;

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

//        mAdView2.setAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//                mAdView2.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                mAdView2.setVisibility(View.VISIBLE);
//            }
//        });

    }


    public void changeItemsView(MenuItem item) {
        if (!linear) {
            linear = true;
            SharedPrefManager.getInstance(getActivity()).setLayoutDesignLinear(linear);
            setLayoutManagerAdapter(1);
            item.setIcon(R.drawable.grid);
        } else {
            linear = false;
            SharedPrefManager.getInstance(getActivity()).setLayoutDesignLinear(linear);
            setLayoutManagerAdapter(2);
            item.setIcon(R.drawable.linear);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.fragment_notepad_menu, menu);  // Use filter.xml from step 1
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuItem viewIcon = menu.findItem(R.id.changeAdapter);
        if (!linear) {
            viewIcon.setIcon(R.drawable.linear);
        } else {
            viewIcon.setIcon(R.drawable.grid);
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.promo:
                startActivity(new Intent(getActivity(), GiftsAndOffers.class));
                break;

            case R.id.changeAdapter:
                changeItemsView(item);
                break;
        }
        return super.onOptionsItemSelected(item);

    }


    //===============================================================  Action Mode  ===========================================================================


    private void singleClickActionMode(int position) {
        if (isMultiSelect) {
            multi_select(position);
        } else {
            startActivity(new Intent(getActivity(), NotepadEditActivity.class)
                    .putExtra("notepad_position", notepadList.get(position))
                    .putExtra("fromEdit",false)
            );
        }

    }


    private void longClickActionMode(int position) {
        if (!isMultiSelect) {
            notepadListSelected = new ArrayList<>();
            isMultiSelect = true;

            if (mActionMode == null) {
                mActionMode = ((AppCompatActivity) getActivity()).startActionMode(mActionModeCallback);
            }
        }

        multi_select(position);
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (notepadListSelected.contains(notepadList.get(position)))
                notepadListSelected.remove(notepadList.get(position));
            else
                notepadListSelected.add(notepadList.get(position));

            if (notepadListSelected.size() > 0) {
                mActionMode.setTitle(notepadListSelected.size() + " item selected");
            } else {
                mActionMode.setTitle("");
                mActionMode.finish();
            }

            refreshAdapter();

        }
    }


    public void refreshAdapter() {
        notepadAdapter.aListSelected = notepadListSelected;
        notepadAdapter.aList = notepadList;
        notepadAdapter.notifyDataSetChanged();
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

                                    for (Notepad notepad : notepadListSelected) {
                                        deleteNotepad(notepad);
                                    }

                                    if (notepadListSelected.size() > 0) {
                                        for (int i = 0; i < notepadListSelected.size(); i++)
                                            notepadList.remove(notepadListSelected.get(i));

                                        notepadAdapter.notifyDataSetChanged();

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
            notepadListSelected = new ArrayList<Notepad>();
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
    private void deleteNotepad(Notepad note) {
        new AsyncTask<Notepad, Void, Void>() {
            @Override
            protected Void doInBackground(Notepad... params) {
                db.getNotepadDao().deleteAll(params);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute(note);
    }

}
