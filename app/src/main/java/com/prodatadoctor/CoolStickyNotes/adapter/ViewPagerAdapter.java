package com.prodatadoctor.CoolStickyNotes.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.prodatadoctor.CoolStickyNotes.NotepadFragment;
import com.prodatadoctor.CoolStickyNotes.StickyNotesFragment;
import com.prodatadoctor.CoolStickyNotes.ToDoFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return StickyNotesFragment.newInstance();

            case 1:
                return ToDoFragment.newInstance();
            case 2:
                return NotepadFragment.newInstance();

        }
        return null;

    }


    @Override
    public int getCount() {
        return 3;// number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return StickyNotesFragment.TITLE;
            case 1:
                return ToDoFragment.TITLE;
            case 2:
                return NotepadFragment.TITLE;

        }
        return super.getPageTitle(position);
    }

}
