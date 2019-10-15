package com.jat.medilinkapp.model.databaseConf;

import com.jat.medilinkapp.model.entity.NfcData;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;

@Dao
public interface NfcDataDao {


    @Query("select * from NfcData")
    Observable<List<NfcData>> getNfcDataList();

    @Insert
    Observable<Long> addData(NfcData nfcData);

}
