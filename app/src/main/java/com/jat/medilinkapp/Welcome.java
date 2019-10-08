package com.jat.medilinkapp;

import android.content.Intent;
import android.os.Bundle;

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
