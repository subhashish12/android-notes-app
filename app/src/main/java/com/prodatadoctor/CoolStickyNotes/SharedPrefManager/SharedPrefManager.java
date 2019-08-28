package com.prodatadoctor.CoolStickyNotes.SharedPrefManager;


import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefManager {
    private static final String TAG_ITEM_INSERTED = "TAG_ITEM_INSERTED";

    private static final String TAG_ITEM_FROM__NOTIFICATION = "TAG_ITEM_FROM__NOTIFICATION";

    private static final String TAG_SWITCH_STATE = "TAG_SWITCH_STATE";
    private static final String TAG_APP_FIRST_RUN = "TAG_APP_FIRST_RUN";

    private static SharedPrefManager mInstance;
    private SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        String SHARED_PREFERENCE = "SHARED_PREFERENCE";
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
//----------------------------------------------------------------------------------------------------------

    public boolean isFirstRun() {
        return sharedPreferences.getBoolean(TAG_APP_FIRST_RUN, false);
    }

    public void setFirstRun(boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TAG_APP_FIRST_RUN, state);
        editor.apply();
    }




    public boolean isItemInserted() {
        return sharedPreferences.getBoolean(TAG_ITEM_INSERTED, false);
    }

    public void setItemInserted(boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TAG_ITEM_INSERTED, state);
        editor.apply();
    }


    public boolean getLayoutDesignLinear() {
        return sharedPreferences.getBoolean(TAG_SWITCH_STATE, false);
    }

    public void setLayoutDesignLinear(boolean state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TAG_SWITCH_STATE, state);
        editor.apply();
    }



    public int getItemIdFromNotification() {
        return sharedPreferences.getInt(TAG_ITEM_FROM__NOTIFICATION, -1);
    }

    public void setItemIdFromNotification(int  item_id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(TAG_ITEM_FROM__NOTIFICATION, item_id);
        editor.apply();
    }

}
