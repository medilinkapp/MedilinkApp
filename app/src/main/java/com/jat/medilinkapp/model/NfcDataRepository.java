package com.jat.medilinkapp.model;

import android.app.Application;

import com.jat.medilinkapp.model.databaseConf.MyDataBase;
import com.jat.medilinkapp.model.entity.NfcData;

import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;

public class NfcDataRepository {

    private MyDataBase appDatabase;

    public NfcDataRepository(@NonNull Application application) {
        appDatabase = MyDataBase.getDatabase(application);
    }

    public Observable<List<NfcData>> getList() {
        return appDatabase.nfcDataDao().getNfcDataList();
    }

    public Observable<List<NfcData>> getListBySubCreateData(String subDate) {
        return appDatabase.nfcDataDao().getListBySubCreateData(subDate);
    }

    public Long addData(NfcData modelClass) {
        return appDatabase.nfcDataDao().addData(modelClass);
    }

    public void deleteAll() {
        appDatabase.nfcDataDao().deleteAll();
    }

    public void delete(NfcData nfcData) {
        appDatabase.nfcDataDao().delete(nfcData);
    }


}
