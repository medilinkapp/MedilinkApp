package com.jat.medilinkapp.model;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import com.jat.medilinkapp.model.databaseConf.MyDataBase;
import com.jat.medilinkapp.model.entity.NfcData;
import io.reactivex.Observable;
import java.util.List;

public class NfcDataRepository {

    private MyDataBase appDatabase;

    public NfcDataRepository(@NonNull Application application) {
        appDatabase = MyDataBase.Companion.getDatabase(application);
    }

    public NfcDataRepository(@NonNull Context application) {
        appDatabase = MyDataBase.Companion.getDatabase(application);
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

    public Observable<List<NfcData>> getListIsSend(boolean send) {
        return appDatabase.nfcDataDao().getListIsSend(send);
    }


}
