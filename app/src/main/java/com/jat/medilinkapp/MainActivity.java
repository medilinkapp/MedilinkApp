package com.jat.medilinkapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.jat.medilinkapp.conf.APIService;
import com.jat.medilinkapp.conf.ApiUtils;
import com.jat.medilinkapp.model.NfcData;
import com.jat.medilinkapp.nfcconf.NfcTag;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private APIService mAPIService;
    private String TAG = "MEDILINK TAG";
    private NfcTag nfc_tag;

    @BindView(R.id.et_employeeid)
    EditText employeeid;
    @BindView(R.id.et_clientid)
    EditText clientid;
    @BindView(R.id.et_officeid)
    EditText officeid;

    @BindView(R.id.cb_in)
    CheckBox cbIn;
    @BindView(R.id.cb_out)
    CheckBox cbOut;

    @BindView(R.id.et_nfc)
    EditText nfc;

    private String nfc_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAPIService = ApiUtils.getAPIService();

        nfc_tag = new NfcTag();
        nfc_tag.init(this);

        cbIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    cbOut.setChecked(false);
            }
        });

        cbOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    cbIn.setChecked(false);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            nfc_id = nfc_tag.handleIntent(intent);
        }
    }

    @OnClick(R.id.bt_send)
    void submit() {

        NfcData nfcData = new NfcData();
        nfcData.setEmployeeId(Integer.valueOf(employeeid.getText().toString()));
        nfcData.setClientId(Integer.valueOf(clientid.getText().toString()));
        nfcData.setOfficeid(Integer.valueOf(officeid.getText().toString()));
        nfcData.setCalltype(cbIn.isChecked() ? MainActivity.this.getString(R.string.CALLTYPE_IN) : MainActivity.this.getString(R.string.CALLTYPE_OUT));
        nfcData.setNfc(nfc_id);
        nfcData.setTasktype("12|45|77");

        sendPost(nfcData);
    }

    public void sendPost(NfcData nfcData) {
        // RxJava
        mAPIService.savePost(nfcData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NfcData>() {
                    @Override
                    public void onCompleted() {
                        showResponse("Message", "Data was sent.");
                    }

                    @Override
                    public void onError(Throwable e) {
                        showResponse("Error", e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(NfcData nfcData) {

                    }
                });
    }

    public void showResponse(String title, String message) {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog2.setTitle(title);
        alertDialog2.setMessage(message);
        alertDialog2.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog2.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfc_tag.resume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfc_tag.pause(this);
    }
}
