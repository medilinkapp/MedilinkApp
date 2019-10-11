package com.jat.medilinkapp.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NfcData.class}, version = 1)
public abstract class MyDataBase extends RoomDatabase {

    private static MyDataBase instance;

    public static MyDataBase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, MyDataBase.class, "NFCDB").build();
        }
        return instance;
    }


    public abstract NfcDataDao nfcDataDao();

}
