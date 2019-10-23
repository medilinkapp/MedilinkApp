package com.jat.medilinkapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.jat.medilinkapp.model.entity.NfcData;
import com.jat.medilinkapp.viewmodels.NfcDataHistoryViewModel;
import com.jat.medilinkapp.worktask.CleanerWorker;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Welcome extends AppCompatActivity {

    public static final String TAG_CLEANUP = "cleanup";
    @BindView(R.id.tv_info_unsent_visits)
    TextView tvInfoUnSent;

    Unbinder unbinder;
    NfcDataHistoryViewModel viewModel;
    Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        unbinder = ButterKnife.bind(this);

        AppCenter.start(getApplication(), this.getString(R.string.idAppCenter),
                Analytics.class, Crashes.class);

        viewModel = ViewModelProviders.of(this).get(NfcDataHistoryViewModel.class);

        checkUnsentVisits();

        //Task for clean data base
        PeriodicWorkRequest cleanerWorkRequest =
                new PeriodicWorkRequest.Builder(CleanerWorker.class, 1, TimeUnit.DAYS)
                        .addTag(TAG_CLEANUP)
                        .build();

        WorkManager.getInstance(this).getWorkInfosByTagLiveData(TAG_CLEANUP).observe(this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos.isEmpty()) {
                    WorkManager.getInstance(Welcome.this).enqueue(cleanerWorkRequest);
                } else {
                    Log.i("PeriodicWorkRequest", "There is a task with the tag: " + TAG_CLEANUP+" : "+ workInfos.get(0).getState().toString());
                }
            }
        });
    }

    private void checkUnsentVisits() {
        viewModel.getListIsSend(false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable d) throws Exception {
                disposable = d;
            }
        }).subscribe(new BlockingBaseObserver<List<NfcData>>() {
            @Override
            public void onNext(List<NfcData> nfcData) {
                int count = nfcData.size();
                tvInfoUnSent.setText(String.format(Welcome.this.getString(R.string.count_message_visits_unsent), count));
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    @OnClick(R.id.bt_log_visit)
    void startLogVisit() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        unbinder.unbind();
    }
}
