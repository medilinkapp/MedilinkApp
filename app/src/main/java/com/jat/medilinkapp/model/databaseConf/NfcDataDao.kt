package com.jat.medilinkapp.model.databaseConf

import com.jat.medilinkapp.model.entity.NfcData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Observable

@Dao
interface NfcDataDao {

    @get:Query("select * from NfcData")
    val nfcDataList: Observable<List<NfcData>>

    @Insert
    fun addData(nfcData: NfcData): Long

    @Query("DELETE FROM NfcData")
    fun deleteAll()

    @Delete
    fun delete(nfcData: NfcData)

    @Query("SELECT * FROM NfcData where created_date like :createDate")
    fun getListBySubCreateData(createDate: String): Observable<List<NfcData>>

    @Query("SELECT * FROM NfcData where is_send like :send")
    fun getListIsSend(send: Boolean): Observable<List<NfcData>>

}
