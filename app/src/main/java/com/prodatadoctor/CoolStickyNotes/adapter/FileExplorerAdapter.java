package com.prodatadoctor.CoolStickyNotes.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prodatadoctor.CoolStickyNotes.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileExplorerAdapter extends ArrayAdapter<String> {

    private List<String> names;
    private Integer[] imageid;
    private Activity context;


    public FileExplorerAdapter(Activity context, List<String> names) {
        super(context, R.layout.backup_files_row, names);
        this.context = context;
        this.names = names;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.file_explorer_row, null, true);
        TextView textViewName = listViewItem.findViewById(R.id.nameFol);
        ImageView image =  listViewItem.findViewById(R.id.imgFol);

        ArrayList<String> alName=new ArrayList<>();
        for(String name:names){
            File f=new File(name);
            alName.add(f.getName());
        }


        textViewName.setText(alName.get(position));

        if(alName.get(position).endsWith(".json")){
            image.setImageResource(R.drawable.ic_file);
        }else {
            image.setImageResource(R.drawable.folder);
        }

        return  listViewItem;
    }

}
