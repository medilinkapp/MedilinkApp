package com.jat.medilinkapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.jat.medilinkapp.viewmodels.NfcDataHistoryViewModel;
import com.jat.medilinkapp.worktask.CleanerWorker;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Welcome extends AppCompatActivity {

    public static final String TAG_CLEANUP = "cleanup";
    public static final String OPEN_VISIT = "open_visit";

    @BindView(R.id.tv_info_unsent_visits)
    TextView tvInfoUnSent;

    @BindView(R.id.layout_visits)
    View layoutVisit;

    Unbinder unbinder;
    NfcDataHistoryViewModel viewModel;
    CompositeDisposable disposable = new CompositeDisposable();

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
                    Log.i("PeriodicWorkRequest", "There is a task with the tag: " + TAG_CLEANUP + " : " + workInfos.get(0).getState().toString());
                }
            }
        });

        layoutVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, MainActivity.class);
                intent. putExtra(OPEN_VISIT, true);
                startActivity(intent);
            }
        });
    }

    private void checkUnsentVisits() {
        disposable.add(viewModel.getListIsSend(false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(nfcData -> {
                            int count = nfcData.size();
                            tvInfoUnSent.setText(String.format(Welcome.this.getString(R.string.count_message_visits_unsent), count));
                        }
                )
        );
    }

    @OnClick(R.id.bt_log_visit)
    void startLogVisit() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
        unbinder.unbind();
    }
}
