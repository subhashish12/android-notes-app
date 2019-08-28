package com.prodatadoctor.CoolStickyNotes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MinuteElapseReceiver extends BroadcastReceiver {

    private SystemTimeChanged callback;

    public MinuteElapseReceiver() {
    }

    public MinuteElapseReceiver(SystemTimeChanged callback) {
        this.callback=callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        callback.minutechanged(intent);
       // Toast.makeText(context,"Minute changed",Toast.LENGTH_LONG).show();
    }

    public interface SystemTimeChanged{
        void minutechanged(Intent intent);
    }
}
