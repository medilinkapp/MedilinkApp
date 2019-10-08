package com.jat.medilinkapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.jat.medilinkapp.conf.APIService;
import com.jat.medilinkapp.conf.ApiUtils;
import com.jat.medilinkapp.model.NfcData;
import com.jat.medilinkapp.nfcconf.NfcTag;

import java.net.InetAddress;

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
                cbIn.setError(null);
                cbOut.setError(null);
            }
        });

        cbOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    cbIn.setChecked(false);
                cbIn.setError(null);
                cbOut.setError(null);
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
        if (validate()) {
            NfcData nfcData = new NfcData();
            nfcData.setEmployeeId(Integer.valueOf(employeeid.getText().toString()));
            nfcData.setClientId(Integer.valueOf(clientid.getText().toString()));
            nfcData.setOfficeid(Integer.valueOf(officeid.getText().toString()));
            nfcData.setCalltype(cbIn.isChecked() ? MainActivity.this.getString(R.string.CALLTYPE_IN) : MainActivity.this.getString(R.string.CALLTYPE_OUT));
            nfcData.setNfc(nfc_id);
            nfcData.setTasktype("12|45|77");

            sendPost(nfcData);
        }
    }

    private boolean validate() {
        if (!validateEmpty()) {
            return false;
        }

        return true;

    }

    private boolean validateEmpty() {
        EditText[] editTexts = {employeeid, clientid, officeid, nfc};
        boolean validate = true;
        for (EditText ed : editTexts) {
            if (TextUtils.isEmpty(ed.getText().toString().trim())) {
                ed.setError("Field is empty!");
                validate = false;
            }
        }

        if (!cbIn.isChecked() && !cbOut.isChecked()) {
            cbIn.setError("In or Out should be select!");
            cbOut.setError("In or Out should be select!");
            validate = false;
        }
        return validate;
    }

    private void sendPost(NfcData nfcData) {
        if (isInternetAvailable()) {
            // RxJava
            final Dialog progress = showProgress();
            mAPIService.savePost(nfcData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<NfcData>() {
                        @Override
                        public void onCompleted() {
                            progress.dismiss();
                            showResponse("Sent", "Data was sent.", true)
                            ;
                        }
                        @Override
                        public void onError(Throwable e) {
                            progress.dismiss();
                            showResponse("Error", e.getLocalizedMessage(), false);
                        }
                        @Override
                        public void onNext(NfcData nfcData) {

                        }
                    });
        } else {
            showResponse("Error", "Not internet connection!", false);
        }
    }

    public void showResponse(String titleMsg, String message, boolean success) {
        // custom dialog
        final Dialog dialogWF;
        dialogWF = new Dialog(this, R.style.dialogStyle);
        dialogWF.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWF.setContentView(R.layout.dialog_info);
        dialogWF.setCanceledOnTouchOutside(false);
        dialogWF.setCancelable(false);
        // Setting dialogview
        Window window = dialogWF.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView tvTitle = dialogWF.findViewById(R.id.tv_title);
        TextView tvTitleMessage = dialogWF.findViewById(R.id.tv_title_message);
        TextView tvMsgDialog = dialogWF.findViewById(R.id.tv_msg_dialog);
        Button btOK = dialogWF.findViewById(R.id.bt_ok);

        tvTitleMessage.setText(titleMsg);
        tvMsgDialog.setText(message);

        if (success) {
            tvTitle.setTextColor(Color.GREEN);
        } else {
            tvTitle.setTextColor(Color.RED);
        }

        btOK.setOnClickListener(v -> {
            if (success) {
                dialogWF.dismiss();
                finish();
            } else {
                dialogWF.dismiss();
            }
        });
        dialogWF.show();
    }


    private Dialog showProgress() {
        // custom dialog
        final Dialog dialogWF;
        dialogWF = new Dialog(this, R.style.dialogStyle);
        dialogWF.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWF.setContentView(R.layout.dialog_info_progress);
        dialogWF.setCanceledOnTouchOutside(false);
        dialogWF.setCancelable(false);
        // Setting dialogview
        Window window = dialogWF.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView tv_msg_bible = dialogWF.findViewById(R.id.tv_msg_dialog);
        tv_msg_bible.setText("Request is being processed.");

        dialogWF.show();
        return dialogWF;
    }

    private boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
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
