package com.jat.medilinkapp;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.jat.medilinkapp.conf.APIService;
import com.jat.medilinkapp.conf.ApiUtils;
import com.jat.medilinkapp.model.NfcData;
import com.jat.medilinkapp.nfcconf.NfcTag;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MyDialog.DialogListener {

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
            nfcData.setNfc(nfc.getText().toString());
            nfcData.setTasktype("12|45|77");
            nfcData.setAppSender(this.getString(R.string.android_sender));

            sendPost(nfcData);
        }
    }

    @OnClick(R.id.bt_add_tasks)
    void showDialogAddTasks() {
        MyDialog dialogFragment = new MyDialog();

        Bundle bundle = new Bundle();
        bundle.putBoolean("notAlertDialog", true);
        dialogFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, "dialog");
    }

    private boolean validate() {
        if (!validateEmpty()) {
            return false;
        }

        if (!isValidateLength()) {
            return false;
        }
        return true;
    }

    private boolean isValidateLength() {
        boolean validateEmployee = validateLength(employeeid, 5, 12, "Employee Id");
        boolean validateClient = validateLength(clientid, 1, 5, "Client Id");
        boolean validateOffice = validateLength(officeid, 1, 2, "Office Id");
        return validateEmployee && validateClient && validateOffice;
    }

    private boolean validateLength(EditText editText, int min, int max, String nameField) {
        if (editText.getText().length() < min || editText.getText().length() > max) {
            editText.setError(nameField + ": should be between " + min + " and " + max + " digits!");
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
        new SupportUI().checkInternetConnetion().subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        new SupportUI().showResponse(MainActivity.this, "Error", "Not internet connection!", false);
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean)
                            sendDataPost(nfcData);
                    }
                });
    }

    private void sendDataPost(NfcData nfcData) {
        // RxJava
        final Dialog progress = new SupportUI().showProgress(this);
        mAPIService.savePost(nfcData).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NfcData>() {
                    @Override
                    public void onCompleted() {
                        progress.dismiss();
                        new SupportUI().showResponse(MainActivity.this, "Sent", "Data was sent.", true)
                        ;
                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.dismiss();
                        new SupportUI().showResponse(MainActivity.this, "Error", e.getLocalizedMessage(), false);
                    }

                    @Override
                    public void onNext(NfcData nfcData) {

                    }
                });
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

    @Override
    public void onFinishEditDialog(@NotNull ArrayList<String> list) {

    }

}
