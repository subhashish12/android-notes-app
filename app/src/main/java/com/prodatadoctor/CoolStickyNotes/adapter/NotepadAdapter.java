package com.prodatadoctor.CoolStickyNotes.adapter;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prodatadoctor.CoolStickyNotes.AdapterCallback.ItemClicked;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.OptionSelected;
import com.prodatadoctor.CoolStickyNotes.NotepadEditActivity;
import com.prodatadoctor.CoolStickyNotes.NotepadFragment;
import com.prodatadoctor.CoolStickyNotes.R;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.Notepad;

import java.util.List;


public class NotepadAdapter extends RecyclerView.Adapter<NotepadAdapter.MyViewHolder> {  //implements Filterable

    Context context;
    String[] list;
    public List<Notepad> aList;
    public List<Notepad> aListSelected;
    int num;

    private OptionSelected optionSelected;

    private ItemClicked itemClicked;

    private AppDatabase db;

    public NotepadAdapter(Context context, List<Notepad> aList, List<Notepad> aListSelected, int num, OptionSelected optionSelected, ItemClicked itemClicked) {
        this.context = context;
        this.aList = aList;
        this.aListSelected = aListSelected;
        this.num = num;
        this.optionSelected = optionSelected;
        this.itemClicked=itemClicked;

        db = AppDatabase.getInstance(context);

    }

//    public NotepadAdapter(Context context, List<Notepad> aList, List<Notepad> aListSelected, int num, OptionSelected optionSelected) {
//        this.context = context;
//        this.aList = aList;
//        this.aListSelected = aListSelected;
//        this.num = num;
//        this.optionSelected = optionSelected;
//
//        db = Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DB_NAME).build();
//
//    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayoutRoot;
        CardView cardView;
        RelativeLayout relativeLayout;
        TextView textViewTitle, textViewDate;
        EditText editTextContent;
        ImageButton imageButtonOptionMenu;

        public MyViewHolder(View itemView) {
            super(itemView);

            linearLayoutRoot = itemView.findViewById(R.id.linearRoot);

            editTextContent = itemView.findViewById(R.id.textViewC);
            cardView = itemView.findViewById(R.id.cardView);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);

            imageButtonOptionMenu=itemView.findViewById(R.id.ibOptionMenuNotepad);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder myViewHolder = null;
        if (num == 2) {
            View view = LayoutInflater.from(context).inflate(R.layout.card_view_notepad, parent, false);
            myViewHolder = new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.card_view_notepad2, parent, false);
            myViewHolder = new MyViewHolder(view);
        }

        return myViewHolder;
    }

    @Override
    public int getItemCount() {
        return aList.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        if (!aList.get(position).getTitle().trim().equals("")) {
            holder.textViewTitle.setText(aList.get(position).getTitle());
        } else {
            if (aList.get(position).getDescription().length() > 17) {
                holder.textViewTitle.setText(aList.get(position).getDescription().substring(0, 17) + "...");
            } else {
                holder.textViewTitle.setText(aList.get(position).getDescription());
            }
        }


        if (num == 1) {

            if(aList.get(position).getTitle().equals("")){
                if (aList.get(position).getDescription().length() > 35) {
                    holder.editTextContent.setText(aList.get(position).getDescription().substring(0, 35) + "...");
                } else {
                    holder.editTextContent.setText(aList.get(position).getDescription());
                }
            }else {
                holder.editTextContent.setText(aList.get(position).getTitle());
            }
        } else {
            holder.editTextContent.setText(aList.get(position).getDescription());
        }


        holder.textViewDate.setText(aList.get(position).getDate());

        if (num == 2) {
            if (aListSelected.contains(aList.get(position)))
                holder.linearLayoutRoot.setBackgroundResource(R.color.selected_item_color);
            else
                holder.linearLayoutRoot.setBackgroundResource(R.drawable.itemborder);
        }else {
            if (aListSelected.contains(aList.get(position)))
                holder.linearLayoutRoot.setBackgroundResource(R.color.selected_item_color);
            else
                holder.linearLayoutRoot.setBackgroundResource(R.drawable.itemborder);
        }



        holder.imageButtonOptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!NotepadFragment.isMultiSelect){
                    showOptionMenu(position,holder.imageButtonOptionMenu);
                }

            }
        });


        holder.editTextContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClicked.singleClick(position);
            }
        });

        holder.editTextContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClicked.longClick(position);
                return true;
            }
        });


        holder.linearLayoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClicked.singleClick(position);
            }
        });

        holder.linearLayoutRoot.setOnLongClickListener(v -> {
            itemClicked.longClick(position);
            return true;
        });



        int colorNum = aList.get(position).getColor();

        if(num==2){
            if (colorNum == 1) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad1);
            } else if (colorNum == 2) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad2);
            } else if (colorNum == 3) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad3);
            } else if (colorNum == 4) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad4);
            } else if (colorNum == 5) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad5);
            } else if (colorNum == 6) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad6);
            } else if (colorNum == 7) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad7);
            } else if (colorNum == 8) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad8);
            } else if (colorNum == 9) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad9);
            } else if (colorNum == 10) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad10);
            } else if (colorNum == 11) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad11);
            } else if (colorNum == 12) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad12);
            } else if (colorNum == 13) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad13);
            } else if (colorNum == 14) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad14);
            } else if (colorNum == 15) {
                holder.relativeLayout.setBackgroundResource(R.color.notepad15);
            } else {
                holder.relativeLayout.setBackgroundResource(R.color.notepadDefault);
            }
        }else {

            if (colorNum == 1) {
                holder.relativeLayout.setBackgroundResource(R.color.note1);
            } else if (colorNum == 2) {
                holder.relativeLayout.setBackgroundResource(R.color.note2);
            } else if (colorNum == 3) {
                holder.relativeLayout.setBackgroundResource(R.color.note3);
            } else if (colorNum == 4) {
                holder.relativeLayout.setBackgroundResource(R.color.note4);
            } else if (colorNum == 5) {
                holder.relativeLayout.setBackgroundResource(R.color.note5);
            } else if (colorNum == 6) {
                holder.relativeLayout.setBackgroundResource(R.color.note6);
            } else if (colorNum == 7) {
                holder.relativeLayout.setBackgroundResource(R.color.note7);
            } else if (colorNum == 8) {
                holder.relativeLayout.setBackgroundResource(R.color.note8);
            } else if (colorNum == 9) {
                holder.relativeLayout.setBackgroundResource(R.color.note9);
            } else if (colorNum == 10) {
                holder.relativeLayout.setBackgroundResource(R.color.note10);
            } else if (colorNum == 11) {
                holder.relativeLayout.setBackgroundResource(R.color.note11);
            } else if (colorNum == 12) {
                holder.relativeLayout.setBackgroundResource(R.color.note12);
            } else if (colorNum == 13) {
                holder.relativeLayout.setBackgroundResource(R.color.note13);
            } else if (colorNum == 14) {
                holder.relativeLayout.setBackgroundResource(R.color.note14);
            } else if (colorNum == 15) {
                holder.relativeLayout.setBackgroundResource(R.color.note15);
            } else {
                holder.relativeLayout.setBackgroundResource(R.color.notepadDefault);
            }
        }

    }

    private void showOptionMenu(final int position, View view) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.options_menu_recycler_items);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit:
                    openNotepad(position);
                    break;
                case R.id.share:
                    shareNotepad(position);
                    break;
                case R.id.delete:
                    showConfirmationDialog(position);
                    break;
            }
            return false;
        });
        popup.show();
    }

    private void showConfirmationDialog(final int position) {
        new android.support.v7.app.AlertDialog.Builder(context)
                .setMessage("Want to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNotepad(aList.get(position));
                        optionSelected.clickedDelete();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void openNotepad(int position) {
        Intent intent0 = new Intent(context, NotepadEditActivity.class);
        intent0.putExtra("notepad_position", aList.get(position));
        intent0.putExtra("fromEdit",true);
        context.startActivity(intent0);
    }

    private void shareNotepad(int position) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, aList.get(position).getTitle());
        shareIntent.setType("text/plain");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_TEXT, aList.get(position).getDescription());
        context.startActivity(Intent.createChooser(shareIntent, "Select App to Share..."));
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
            protected void onPostExecute(Void aVoid) {}
        }.execute(note);
    }

}

