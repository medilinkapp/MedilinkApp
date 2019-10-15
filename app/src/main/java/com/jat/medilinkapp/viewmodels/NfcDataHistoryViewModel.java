package com.jat.medilinkapp.viewmodels;

import android.app.Application;

import com.jat.medilinkapp.model.NfcDataRepository;
import com.jat.medilinkapp.model.entity.NfcData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import io.reactivex.Observable;


public class NfcDataHistoryViewModel extends AndroidViewModel {

    NfcDataRepository nfcDataRepository;

    public NfcDataHistoryViewModel(@NonNull Application application) {
        super(application);
        nfcDataRepository = new NfcDataRepository(application);
    }

    public Observable<List<NfcData>> getList() {
        return nfcDataRepository.getList();
    }

    public Long addData(NfcData modelClass) {
        return nfcDataRepository.addData(modelClass);
    }

    public void deleteAll() {
        nfcDataRepository.deleteAll();
    }


}
