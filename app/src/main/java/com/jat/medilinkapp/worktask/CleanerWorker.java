package com.jat.medilinkapp.worktask;


import android.content.Context;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.jat.medilinkapp.model.NfcDataRepository;
import com.jat.medilinkapp.model.entity.NfcData;
import com.jat.medilinkapp.util.SupportUI;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

public class CleanerWorker extends Worker {

    NfcDataRepository nfcDataRepository;
    Disposable disposable;


    public CleanerWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);

        nfcDataRepository = new NfcDataRepository(context);

    }

    @Override
    public Result doWork() {
        nfcDataRepository.getList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                disposable = d;
            }
        }).subscribe(new BlockingBaseObserver<List<NfcData>>() {
            @Override
            public void onNext(List<NfcData> list) {
                if (!list.isEmpty()) {
                    SupportUI supportUI = new SupportUI();
                    int count = list.size();
                    ArrayList<NfcData> oldItems = new ArrayList<>();
                    for (NfcData item : list) {
                        if (supportUI.isOldItem(supportUI.fromStringToDate(item.getCreateDate()))) {
                            oldItems.add(item);
                        }
                    }
                    if (!oldItems.isEmpty()) {
                        for (NfcData item : oldItems) {
                            nfcDataRepository.delete(item);
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
            }
        });

        disposable.dispose();
        // Indicate whether the task finished successfully with the Result
        return Result.success();
    }
}
