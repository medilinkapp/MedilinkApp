package com.jat.medilinkapp.viewmodels;

import android.app.Application;

import com.jat.medilinkapp.model.MyDataBase;
import com.jat.medilinkapp.model.NfcData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;


public class NfcDataHistoryViewModel extends AndroidViewModel {

    private MyDataBase appDatabase;

    public NfcDataHistoryViewModel(@NonNull Application application)     {
        super(application);
        appDatabase = MyDataBase.getDatabase(application);
    }

    public Observable<List<NfcData>> getList() {
        return appDatabase.nfcDataDao().getNfcDataList();
    }

    long addData(NfcData modelClass) {
        return appDatabase.nfcDataDao().addData(modelClass);
    }

}