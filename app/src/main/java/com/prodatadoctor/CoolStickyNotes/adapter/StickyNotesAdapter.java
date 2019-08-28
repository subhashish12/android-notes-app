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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.prodatadoctor.CoolStickyNotes.AdapterCallback.ItemClicked;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.OptionSelected;
import com.prodatadoctor.CoolStickyNotes.R;
import com.prodatadoctor.CoolStickyNotes.StickyNotesEditActivity;
import com.prodatadoctor.CoolStickyNotes.StickyNotesFragment;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.Note;

import java.util.List;


public class StickyNotesAdapter extends RecyclerView.Adapter<StickyNotesAdapter.MyViewHolder> {  //implements Filterable

    Context context;
    String[] list;
    public List<Note> aList;

    public List<Note> aListSelected;


    private OptionSelected optionSelected;
    private ItemClicked itemClicked;

    private AppDatabase db;

//    public StickyNotesAdapter(Context context, List<Note> aList, OptionSelected optionSelected) {
//        this.context = context;
//        this.aList = aList;
//        this.optionSelected = optionSelected;
//
//        db = Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DB_NAME).build();
//
//    }


    public StickyNotesAdapter(Context context, List<Note> aList, List<Note> aListSelected, OptionSelected optionSelected, ItemClicked itemClicked) {
        this.context = context;
        this.aList = aList;
        this.aListSelected=aListSelected;
        this.optionSelected = optionSelected;
        this.itemClicked=itemClicked;

        db = AppDatabase.getInstance(context);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayoutRoot;
        CardView cardView;
        RelativeLayout relativeLayout;
        TextView textViewTitle,textView,textViewDate,textViewId;
        ImageView imageView;
        ImageButton imageButtonOptionMenu;

        public MyViewHolder(View itemView) {
            super(itemView);
            linearLayoutRoot=itemView.findViewById(R.id.linearRoot);
            cardView = itemView.findViewById(R.id.cardView);
            relativeLayout= itemView.findViewById(R.id.relativeLayout);
            textViewTitle= itemView.findViewById(R.id.textViewTitle);
            textView = itemView.findViewById(R.id.textViewC);
            textViewDate= itemView.findViewById(R.id.tvDate);
            imageView = itemView.findViewById(R.id.imageViewC);

            imageButtonOptionMenu=itemView.findViewById(R.id.ibOptionMenuStickyNotes);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_sticky_notes, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return aList.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        if (aListSelected.contains(aList.get(position)))
            holder.linearLayoutRoot.setBackgroundResource(R.color.selected_item_color);
        else
            holder.linearLayoutRoot.setBackgroundResource(R.color.not_selected_item_color);


        holder.textViewTitle.setText(aList.get(position).getTitle());
        holder.textView.setText(aList.get(position).getDescription());
        holder.textViewDate.setText(aList.get(position).getDate());


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  openNotepad(position);
                itemClicked.singleClick(position);
            }
        });

        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClicked.longClick(position);
                return true;
            }
        });


        holder.imageButtonOptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StickyNotesFragment.isMultiSelect){
                    showOptionMenu(position,holder.imageButtonOptionMenu);
                }
            }
        });


        int colorNum=aList.get(position).getColor();
        if(colorNum==1)
        {holder.relativeLayout.setBackgroundResource(R.color.note1);}
        else if(colorNum==2)
        {holder.relativeLayout.setBackgroundResource(R.color.note2);}
        else if(colorNum==3)
        {holder.relativeLayout.setBackgroundResource(R.color.note3);}
        else if(colorNum==4)
        {holder.relativeLayout.setBackgroundResource(R.color.note4);}
        else if(colorNum==5)
        {holder.relativeLayout.setBackgroundResource(R.color.note5);}
        else if(colorNum==6)
        {holder.relativeLayout.setBackgroundResource(R.color.note6);}
        else if(colorNum==7)
        {holder.relativeLayout.setBackgroundResource(R.color.note7);}
        else if(colorNum==8)
        {holder.relativeLayout.setBackgroundResource(R.color.note8);}
        else if(colorNum==9)
        {holder.relativeLayout.setBackgroundResource(R.color.note9);}
        else if(colorNum==10)
        {holder.relativeLayout.setBackgroundResource(R.color.note10);}
        else if(colorNum==11)
        {holder.relativeLayout.setBackgroundResource(R.color.note11);}
        else if(colorNum==12)
        {holder.relativeLayout.setBackgroundResource(R.color.note12);}
        else if(colorNum==13)
        {holder.relativeLayout.setBackgroundResource(R.color.note13);}
        else if(colorNum==14)
        {holder.relativeLayout.setBackgroundResource(R.color.note14);}
        else if(colorNum==15)
        {holder.relativeLayout.setBackgroundResource(R.color.note15);}
        else
        {holder.relativeLayout.setBackgroundResource(R.color.note1);}


        int stickerNum=aList.get(position).getImage();
        if(stickerNum==1)
        {holder.imageView.setBackgroundResource(R.drawable.sticker1);}
        else if(stickerNum==2)
        {holder.imageView.setBackgroundResource(R.drawable.sticker2);}
        else if(stickerNum==3)
        {holder.imageView.setBackgroundResource(R.drawable.sticker3);}
        else if(stickerNum==4)
        {holder.imageView.setBackgroundResource(R.drawable.sticker4);}
        else if(stickerNum==5)
        {holder.imageView.setBackgroundResource(R.drawable.sticker5);}
        else if(stickerNum==6)
        {holder.imageView.setBackgroundResource(R.drawable.sticker6);}
        else if(stickerNum==7)
        {holder.imageView.setBackgroundResource(R.drawable.sticker7);}
        else if(stickerNum==8)
        {holder.imageView.setBackgroundResource(R.drawable.sticker8);}
        else if(stickerNum==9)
        {holder.imageView.setBackgroundResource(R.drawable.sticker9);}
        else if(stickerNum==10)
        {holder.imageView.setBackgroundResource(R.drawable.sticker10);}
        else
        {holder.imageView.setBackgroundResource(R.drawable.sticker1);}
    }




    private void showOptionMenu(final int position, View view) {

        //creating a popup menu
        PopupMenu popup = new PopupMenu(context, view);
        //inflating menu from xml resource
        popup.inflate(R.menu.options_menu_recycler_items);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        openNotepad(position);
                        break;
                    case R.id.share:
                        shareNotepad(position);
                        break;
                    case R.id.delete:

                        showConfirmationDialog(position);

//                        deleteNotepad(aList.get(position));
//                        optionSelected.clickedDelete();
                        break;
                }
                return false;
            }
        });
        //displaying the popup
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
        Intent intent0 = new Intent(context, StickyNotesEditActivity.class);
        intent0.putExtra("note_position", aList.get(position));
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
    private void deleteNotepad(Note note) {
        new AsyncTask<Note, Void, Void>() {
            @Override
            protected Void doInBackground(Note... params) {
                db.getNoteDao().deleteAll(params);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {}
        }.execute(note);
    }



}

