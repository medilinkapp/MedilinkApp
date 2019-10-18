package com.jat.medilinkapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.jat.medilinkapp.model.entity.NfcData;
import com.jat.medilinkapp.viewmodels.NfcDataHistoryViewModel;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;

public class Welcome extends AppCompatActivity {

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

        AppCenter.start(getApplication(), "e219356b-48da-4271-a40c-0609d2a9c4f3",
                Analytics.class, Crashes.class);

        viewModel = ViewModelProviders.of(this).get(NfcDataHistoryViewModel.class);


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
