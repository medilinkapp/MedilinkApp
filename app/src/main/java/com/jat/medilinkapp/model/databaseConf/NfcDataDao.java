package com.jat.medilinkapp.model.databaseConf;

import com.jat.medilinkapp.model.entity.NfcData;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;

@Dao
public interface NfcDataDao {


    @Query("select * from NfcData")
    Observable<List<NfcData>> getNfcDataList();

    @Insert
    long addData(NfcData nfcData);

    @Query("DELETE FROM NfcData")
    void deleteAll();

    @Delete
    void delete(NfcData nfcData);

    @Query("SELECT * FROM NfcData where created_date like :createDate")
    Observable<List<NfcData>> getListBySubCreateData(String createDate);

}
