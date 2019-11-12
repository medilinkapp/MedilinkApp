package com.jat.medilinkapp.model.databaseConf

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jat.medilinkapp.model.entity.ClientGps
import com.jat.medilinkapp.model.entity.NfcData


@Database(entities = [NfcData::class, ClientGps::class], version = 1)
abstract class MyDataBase : RoomDatabase() {

    abstract fun nfcDataDao(): NfcDataDao
    abstract fun clientGpsDao(): ClientGpsDao

    companion object {
        val NFCDB = "NFCDB"
        private var instance: MyDataBase? = null

        fun getDatabase(context: Context): MyDataBase? {
            if (instance == null) {
                instance = Room.databaseBuilder(context, MyDataBase::class.java, NFCDB).allowMainThreadQueries().build()
            }
            return instance
        }
    }

}
