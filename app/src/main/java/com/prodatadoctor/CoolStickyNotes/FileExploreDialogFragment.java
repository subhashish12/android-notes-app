package com.prodatadoctor.CoolStickyNotes;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.prodatadoctor.CoolStickyNotes.adapter.FileExplorerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileExploreDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    Button buttonHome,buttonUp,buttonOk,buttonCancel;
    TextView textFolder;
    File root;
    File curFolder;
    private List<String> fileList = new ArrayList<String>();
    ListView mylist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_file_explorer, null, false);

        root = new File(Environment
                .getExternalStorageDirectory()
                .getAbsolutePath());
        curFolder = root;

        textFolder = (TextView)view.findViewById(R.id.folder);
        buttonHome= (Button) view.findViewById(R.id.home);
        buttonUp = (Button)view.findViewById(R.id.up);
        buttonOk= (Button) view.findViewById(R.id.done);
        buttonCancel= (Button) view.findViewById(R.id.cancel);
        mylist = (ListView) view.findViewById(R.id.dialoglist);

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDir(root);
            }
        });
        buttonUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ListDir(curFolder.getParentFile());
            }});

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return view;
    }


    @Override
    public void onStart()//<--------- to make DialogFragment full screen
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListDir(curFolder);
        mylist.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File selected = new File(fileList.get(position));
        if(selected.isDirectory()){
            ListDir(selected);
        }else if(selected.getName().endsWith(".json")){
            mListener.onComplete(selected.getPath());
            dismiss();
        }
    }

    void ListDir(File f){
        if(f.equals(root)){
            buttonUp.setEnabled(false);
        }else{
            buttonUp.setEnabled(true);
        }

        curFolder = f;
        textFolder.setText(f.getPath());

        File[] files = f.listFiles();
        fileList.clear();
        for (File file : files){
            if(file.isDirectory() || file.getName().endsWith(".json")){    //to show only folders. remove this line  if want to show files too.
                fileList.add(file.getPath());
                Log.e("Path",file.getName());
            }
        }

        FileExplorerAdapter fileExplorerAdapter =new FileExplorerAdapter(getActivity(),fileList);
        mylist.setAdapter(fileExplorerAdapter);
    }

    private OnCompleteListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnCompleteListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    public  interface OnCompleteListener {
        void onComplete(String path);
    }
}
