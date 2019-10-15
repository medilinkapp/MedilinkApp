package com.jat.medilinkapp;

import android.content.Intent;
import android.os.Bundle;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Welcome extends AppCompatActivity {

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        unbinder = ButterKnife.bind(this);
        AppCenter.start(getApplication(), "e219356b-48da-4271-a40c-0609d2a9c4f3",
                Analytics.class, Crashes.class);

    }

    @OnClick(R.id.bt_log_visit)
    void startLogVisit() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
