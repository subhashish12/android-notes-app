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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prodatadoctor.CoolStickyNotes.AdapterCallback.AlarmCallback;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.ItemClicked;
import com.prodatadoctor.CoolStickyNotes.AdapterCallback.OptionSelected;
import com.prodatadoctor.CoolStickyNotes.Notifications.Receivers.MyAlarmReceiver;
import com.prodatadoctor.CoolStickyNotes.R;
import com.prodatadoctor.CoolStickyNotes.ToDoFragment;
import com.prodatadoctor.CoolStickyNotes.database.AppDatabase;
import com.prodatadoctor.CoolStickyNotes.domain.ToDo;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BackupFilesAdapter extends RecyclerView.Adapter<BackupFilesAdapter.MyViewHolder> {  //implements Filterable

    Context context;

    public List<File> aList;
    private AppDatabase db;

    private FileClicked fileClicked;

    public BackupFilesAdapter(Context context, List<File> aList,FileClicked fileClicked) {
        this.context = context;
        this.aList = aList;
        this.fileClicked=fileClicked;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton imageButtonReset,imageButtonShare,imageButtonDelete;
        TextView textView;


        public MyViewHolder(View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.imgFol);
            imageButtonReset=itemView.findViewById(R.id.restoreBackup);
            imageButtonShare=itemView.findViewById(R.id.sharebackup);
            imageButtonDelete=itemView.findViewById(R.id.deletebackup);
            textView=itemView.findViewById(R.id.nameFol);
        }
    }

    @Override
    public BackupFilesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.backup_files_row, parent, false);
        return new BackupFilesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BackupFilesAdapter.MyViewHolder holder, final int position) {

        String name=aList.get(position).getName().substring(0,aList.get(position).getName().lastIndexOf("."));

        long date_created=aList.get(position).lastModified();
        Date date=new Date(date_created);
        
        SimpleDateFormat df2 = new SimpleDateFormat("ddMMMyyyy, HH:mm a");
        String dateText = df2.format(date);
        StringBuilder stringBuilder=new StringBuilder(name).append("("+dateText+")");

        holder.textView.setText(name);

        holder.imageView.setBackgroundResource(R.drawable.ic_file);

        holder.imageButtonReset.setOnClickListener(v->fileClicked.fileOperationPerformed(aList.get(position).getPath(),"Restore"));
        holder.imageButtonShare.setOnClickListener(v->fileClicked.fileOperationPerformed(aList.get(position).getPath(),"Share"));
        holder.imageButtonDelete.setOnClickListener(v -> {deleteBackUp(aList.get(position).getPath(),position); });
    }


    @Override
    public int getItemCount() {
        return aList.size();
    }


    public interface FileClicked{
        void fileOperationPerformed(String path,String operation);
    }


    private void deleteBackUp(String path, int position) {
        new android.support.v7.app.AlertDialog.Builder(context)
                .setMessage("This action is irrecoverable, Do you want to delete backup?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    File file = new File(path);
                    file.delete();
                    aList.remove(position);
                    notifyDataSetChanged();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
