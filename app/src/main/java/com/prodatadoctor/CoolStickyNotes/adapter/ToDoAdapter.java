package com.prodatadoctor.CoolStickyNotes.adapter;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prodatadoctor.CoolStickyNotes.AdapterCallback.AlarmCallback;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.ItemClicked;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.OptionSelected;
import com.prodatadoctor.CoolStickyNotes.Notifications.Receivers.MyAlarmReceiver;
import com.prodatadoctor.CoolStickyNotes.R;
import com.prodatadoctor.CoolStickyNotes.SharedPrefManager.SharedPrefManager;
import com.prodatadoctor.CoolStickyNotes.ToDoFragment;
import com.prodatadoctor.CoolStickyNotes.dao.ToDoDao;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {  //implements Filterable

    Context context;

    public List<ToDo> aList;
    public List<ToDo> aListSelected;
    private AppDatabase db;

    private OptionSelected optionSelected;
    private ItemClicked itemClicked;
    private AlarmCallback alarmCallback;


    public ToDoAdapter(Context context, List<ToDo> aList, List<ToDo> aListSelected, OptionSelected optionSelected, ItemClicked itemClicked, AlarmCallback alarmCallback) {
        this.context = context;
        this.aList = aList;
        this.aListSelected = aListSelected;
        this.optionSelected = optionSelected;
        this.itemClicked = itemClicked;
        this.alarmCallback = alarmCallback;

        db = AppDatabase.getInstance(context);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayoutRoot;
        CheckBox checkBox;
        ImageButton imageButtonAlarm, imageButtonDelate, imageButtonShare;
        TextView textView, textViewDate, textViewTime, textViewCategory;

        public MyViewHolder(View itemView) {
            super(itemView);

            linearLayoutRoot = itemView.findViewById(R.id.rootViewItem);
            checkBox = itemView.findViewById(R.id.checkbox);
            imageButtonAlarm = itemView.findViewById(R.id.imbAlarm);
            imageButtonDelate = itemView.findViewById(R.id.imbDelete);
            imageButtonShare = itemView.findViewById(R.id.imbShare);

            textView = itemView.findViewById(R.id.tvToDoText);
            textViewDate = itemView.findViewById(R.id.tvToDoDate);
            textViewTime = itemView.findViewById(R.id.tvToDoTime);
            textViewCategory = itemView.findViewById(R.id.tvToDoCategory);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view_to_do, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return aList.size();
    }


    public String currentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date strDate = null;
        Date curDate = null;
        try {
            strDate = sdf.parse(aList.get(position).getDate() + " " + aList.get(position).getTime());
            curDate = sdf.parse(currentDateTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (aListSelected.contains(aList.get(position)))
            holder.linearLayoutRoot.setBackgroundResource(R.color.selected_item_color);
        else
            holder.linearLayoutRoot.setBackgroundResource(R.drawable.itemborder);


        if (!aList.get(position).getDate().equals("")) {
            if (curDate.getTime() == strDate.getTime()) {
                holder.linearLayoutRoot.setBackgroundResource(R.color.currentTaskColor);
            }
        }


        if (!aList.get(position).getDate().equals("")) {
            if (aList.get(position).isAlarmOn() && curDate.getTime() <= strDate.getTime()) {//check for on/off reminder
                holder.imageButtonAlarm.setBackgroundResource(R.drawable.alarm_on);
            } else {
                holder.imageButtonAlarm.setBackgroundResource(R.drawable.alarm_off);
            }
        }else {
            holder.imageButtonAlarm.setBackgroundResource(R.drawable.alarm_off);
        }


        if (aList.get(position).getContent().length() > 30) {
            holder.textView.setText(aList.get(position).getContent().substring(0, 30) + "...");
        } else {
            holder.textView.setText(aList.get(position).getContent());
        }


        holder.textViewCategory.setText(aList.get(position).getCategory());


        if (aList.get(position).getDate().equals("")) {
            holder.textViewDate.setText("No Date");
            holder.textViewDate.setTextColor(Color.BLACK);
            holder.textViewTime.setText("");
        } else {
            if (curDate.getTime() > strDate.getTime()) {

                    holder.textViewDate.setText(aList.get(position).getDate());
                    holder.textViewDate.setTextColor(Color.RED);

                    holder.textViewTime.setText(" " + aList.get(position).getTime() + " (overdue)");
                    holder.textViewTime.setTextColor(Color.RED);

                    new UpdateAlarmTask().execute(new MyTaskParams(aList.get(position).getId(), false));

            } else {
                holder.textViewDate.setText(aList.get(position).getDate());
                holder.textViewDate.setTextColor(Color.BLACK);
                holder.textViewTime.setText(" " + aList.get(position).getTime());
                holder.textViewTime.setTextColor(Color.BLACK);
            }
        }

        Date finalCurDate = curDate;
        Date finalStrDate = strDate;

        holder.imageButtonAlarm.setOnClickListener(view -> {


            if (!aList.get(position).getDate().equals("")) {

                if (finalCurDate.getTime() < finalStrDate.getTime()) {

                    if (!aList.get(position).isAlarmOn()) {//for updating recyclerview item
                        aList.get(position).setAlarmOn(true);
                        holder.imageButtonAlarm.setBackgroundResource(R.drawable.alarm_on);

                        Toast.makeText(context, "Reminder On", Toast.LENGTH_SHORT).show();
                        alarmCallback.setAlarm(aList.get(position));

                        new UpdateAlarmTask().execute(new MyTaskParams(aList.get(position).getId(), aList.get(position).isAlarmOn()));

                    } else {
                        aList.get(position).setAlarmOn(false);
                        holder.imageButtonAlarm.setBackgroundResource(R.drawable.alarm_off);

                        Toast.makeText(context, "Reminder Off", Toast.LENGTH_SHORT).show();
                        alarmCallback.setAlarm(aList.get(position));

                        new UpdateAlarmTask().execute(new MyTaskParams(aList.get(position).getId(), aList.get(position).isAlarmOn()));
                    }
                } else {
                    Toast.makeText(context, "Time has passed, Can't set Reminder", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, "Set date and time first", Toast.LENGTH_SHORT).show();
            }

        });


        holder.imageButtonDelate.setOnClickListener(v -> {
            if (!ToDoFragment.isMultiSelect) {
                showConfirmationDialog(position);
            }
        });

        holder.imageButtonShare.setOnClickListener(v -> {
            if (!ToDoFragment.isMultiSelect) {
                shareItem(position);
            }
        });

        holder.linearLayoutRoot.setOnClickListener(v -> itemClicked.singleClick(position));

        holder.linearLayoutRoot.setOnLongClickListener(v -> {
            itemClicked.longClick(position);
            return true;
        });
    }


    private void showConfirmationDialog(final int position) {

        new android.support.v7.app.AlertDialog.Builder(context)
                .setMessage("Want to delete?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteToDo(aList.get(position));
                    cancelAlarmForItem(aList.get(position));
                    optionSelected.clickedDelete();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void shareItem(int position) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name));

        if (aList.get(position).getTime().equals("23:59")) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, " Due date :  " + aList.get(position).getDate() + "\n\n\t\t\t\t\t" + aList.get(position).getContent());
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, " Due date :  " + aList.get(position).getDate() + " @ " + aList.get(position).getTime() + "\n\n\t\t\t\t\t" + aList.get(position).getContent());
        }

        context.startActivity(Intent.createChooser(shareIntent, "Select App to Share..."));
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


    public void cancelAlarmForItem(ToDo todo) {
        Long x = todo.getId();
        int reqC = x.intValue();

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(context, MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqC, myIntent, 0);
        am.cancel(pendingIntent);
    }


    public class UpdateAlarmTask extends AsyncTask<MyTaskParams, Void, Void> {
        @Override
        protected Void doInBackground(MyTaskParams... myTaskParams) {
            long id = myTaskParams[0].id;
            boolean alarmOn = myTaskParams[0].alarmOn;
            db.getToDoDao().updateAlarm(id, alarmOn);
            return null;
        }
    }


    private static class MyTaskParams {
        long id;
        boolean alarmOn;

        MyTaskParams(long id, boolean alarmOn) {
            this.id = id;
            this.alarmOn = alarmOn;
        }
    }

}

