package com.jat.medilinkapp.backgroudservice;

import android.content.Context;
import com.jat.medilinkapp.conf.APIService;
import com.jat.medilinkapp.conf.ApiUtils;
import com.jat.medilinkapp.model.NfcDataRepository;
import com.jat.medilinkapp.model.entity.NfcData;
import java.util.ArrayList;

public class TaskReSendVisit {

    private ArrayList<NfcData> list;
    private Context context;
    private APIService mAPIService;
    private NfcDataRepository nfcDataRepository;

    public TaskReSendVisit(ArrayList<NfcData> list, Context context) {
        this.list = list;
        this.context = context;
        nfcDataRepository = new NfcDataRepository(context);
        mAPIService = ApiUtils.getAPIService();
    }

    public void start() {
        for (NfcData nfcData : list) {
            sendDataPost(nfcData);
        }
    }

    private void sendDataPost(NfcData nfcData) {
        //delete fromo history to dont duplicate
        nfcDataRepository.delete(nfcData);
        // RxJava post
        mAPIService.sendPost(nfcData)
                .subscribeOn(rx.schedulers.Schedulers.newThread())
                .observeOn(rx.schedulers.Schedulers.trampoline())
                .subscribe(new rx.Observer<NfcData>() {
                    @Override
                    public void onCompleted() {
                        nfcData.setSend(true);
                        //add to the history
                        nfcDataRepository.addData(nfcData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        nfcData.setSend(false);
                        //add to the history
                        nfcDataRepository.addData(nfcData);
                    }

                    @Override
                    public void onNext(NfcData nfcData) {

                    }
                });
    }
}
