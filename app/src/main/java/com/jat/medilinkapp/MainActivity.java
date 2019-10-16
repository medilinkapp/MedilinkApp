package com.jat.medilinkapp;

import android.app.Dialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.jat.medilinkapp.conf.APIService;
import com.jat.medilinkapp.conf.ApiUtils;
import com.jat.medilinkapp.model.entity.NfcData;
import com.jat.medilinkapp.nfcconf.NfcTag;
import com.jat.medilinkapp.util.SharePreferencesUtil;
import com.jat.medilinkapp.viewmodels.NfcDataHistoryViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MyDialog.DialogListener, MyDialogHistory.DialogListener {

    public static final String LIST = "list";
    public static final String NOT_ALERT_DIALOG = "notAlertDialog";
    public static final String DIALOG_TAG = "dialog";
    public static final String DIALOG_TAG_HISTORY = "dialog_history";
    public static final String EMPLOYEEID_PREFERENCE = "employeeid";
    public static final String OFFICEID_PREFERENCE = "officeid";
    public static final String CLIENTID_PREFERENCE = "clientid";
    private APIService mAPIService;
    private String TAG = "MEDILINK TAG";
    private NfcTag nfc_tag;
    ArrayList<String> listTasks;

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

    @BindView(R.id.tv_nfc)
    TextView tvNfc;

    @BindView(R.id.tv_tasks_string)
    TextView tvTasks;

    @BindView(R.id.layout_task)
    View layoutTask;

    @BindView(R.id.bt_history)
    View btHistory;

    private String nfc_id;

    NfcDataHistoryViewModel viewModel;

    MyDialogHistory myDialogHistory;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private SharePreferencesUtil sharePreferencesUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAPIService = ApiUtils.getAPIService();

        nfc_tag = new NfcTag();
        nfc_tag.init(this);

        sharePreferencesUtil = new SharePreferencesUtil(this);
        viewModel = ViewModelProviders.of(this).get(NfcDataHistoryViewModel.class);
        //AsyncTask.execute(() -> viewModel.deleteAll());
        initUI();
    }

    private void initUI() {
        cbIn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbOut.setChecked(false);
                layoutTask.setVisibility(View.GONE);
            }

            cbIn.setError(null);
            cbOut.setError(null);
        });

        cbOut.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbIn.setChecked(false);
                layoutTask.setVisibility(View.VISIBLE);
            }
            cbIn.setError(null);
            cbOut.setError(null);
        });

        employeeid.setText(sharePreferencesUtil.getValue(EMPLOYEEID_PREFERENCE, ""));
        clientid.setText(sharePreferencesUtil.getValue(CLIENTID_PREFERENCE, ""));
        officeid.setText(sharePreferencesUtil.getValue(OFFICEID_PREFERENCE, ""));
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

            final String date = DateFormat.getDateTimeInstance().format(new Date());
            nfcData.setCreateDate(date);

            nfcData.setNfc(tvNfc.getText().toString());
            nfcData.setTasktype(cbOut.isChecked() ? new SupportUI().getFormatDataSendTasks(listTasks) : "");
            nfcData.setAppSender(this.getString(R.string.android_sender));

            sendPost(nfcData);

            //saving values for next time show defaults
            sharePreferencesUtil.setValue(EMPLOYEEID_PREFERENCE, String.valueOf(nfcData.getEmployeeId()));
            sharePreferencesUtil.setValue(OFFICEID_PREFERENCE, String.valueOf(nfcData.getOfficeid()));
            sharePreferencesUtil.setValue(CLIENTID_PREFERENCE, String.valueOf(nfcData.getClientId()));
        }
    }

    @OnClick(R.id.bt_history)
    void showHistoryFragment() {
        myDialogHistory = new MyDialogHistory();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(DIALOG_TAG_HISTORY);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        myDialogHistory.show(ft, DIALOG_TAG);
    }

    @OnClick(R.id.bt_add_tasks)
    void showDialogAddTasks() {
        MyDialog dialogFragment = new MyDialog();

        Bundle bundle = new Bundle();
        bundle.putBoolean(NOT_ALERT_DIALOG, true);
        if (listTasks != null) {
            bundle.putStringArrayList(LIST, listTasks);
        }
        dialogFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialogFragment.show(ft, DIALOG_TAG);
    }


    private boolean validate() {
        boolean validate = true;
        if (!validateEmpty()) {
            validate = false;
        }

        if (!isValidateLength()) {
            validate = false;
        }
        return validate;
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
        EditText[] editTexts = {employeeid, clientid, officeid};
        boolean validate = true;
        for (EditText ed : editTexts) {
            if (TextUtils.isEmpty(ed.getText().toString().trim())) {
                ed.setError(this.getString(R.string.field_is_empty));
                validate = false;
            }
        }
        if (TextUtils.isEmpty(tvTasks.getText().toString()) && cbOut.isChecked()) {
            tvTasks.setError(this.getString(R.string.field_is_empty));
            validate = false;
        }

        if (TextUtils.isEmpty(tvNfc.getText().toString()) && !BuildConfig.DEBUG) {
            tvNfc.setError(this.getString(R.string.field_is_empty));
            tvNfc.setVisibility(View.VISIBLE);
            findViewById(R.id.img_nfc_checked).setVisibility(View.GONE);
            validate = false;
        }

        if (!cbIn.isChecked() && !cbOut.isChecked()) {
            cbIn.setError(this.getString(R.string.should_be_selected));
            cbOut.setError(this.getString(R.string.should_be_selected));
            validate = false;
        }
        return validate;
    }

    private void sendPost(NfcData nfcData) {
        //check is there is intenet connection
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
                        new SupportUI().showResponse(MainActivity.this, "Sent", "Data was sent.", true);
                        nfcData.setSend(true);
                        //add to the history
                        AsyncTask.execute(() -> viewModel.addData(nfcData));

                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.dismiss();
                        new SupportUI().showResponse(MainActivity.this, "Error", e.getLocalizedMessage(), false);
                        nfcData.setSend(false);
                        //add to the history
                        AsyncTask.execute(() -> viewModel.addData(nfcData));
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
        listTasks = list;
        if (!list.isEmpty()) {
            String taskString = "";
            for (String s : list) {
                taskString = taskString + "," + s;
            }
            tvTasks.setText(taskString.substring(1));
            tvTasks.setError(null);
        } else {
            tvTasks.setText("");
            tvTasks.setError(this.getString(R.string.field_is_empty));
        }
    }

    @Override
    public void onFinishSelectionData(@NotNull NfcData nfcData) {
        if (!nfcData.isSend()) {
            new SupportUI().showResentDialog(this, () -> {
                if (myDialogHistory != null) {
                    myDialogHistory.dismiss();
                }

                employeeid.setText(String.valueOf(nfcData.getEmployeeId()));
                clientid.setText(String.valueOf(nfcData.getClientId()));
                officeid.setText(String.valueOf(nfcData.getOfficeid()));

                if (nfcData.getCalltype().equals(MainActivity.this.getString(R.string.CALLTYPE_IN))) {
                    cbIn.setChecked(true);
                } else {
                    cbOut.setChecked(true);
                    tvTasks.setText(nfcData.getTasktype().replace("|", ","));
                    if (!TextUtils.isEmpty(nfcData.getTasktype())) {
                        listTasks = new ArrayList<>();
                        for (String task : nfcData.getTasktype().split("\\|")) {
                            listTasks.add(task);
                        }
                    }
                }
                tvNfc.setText("");

                new SupportUI().showDialogInfo(MainActivity.this, "Data recovered ","Please scan your card again and process to send.");
            }, nfcData);
        }
    }

    @Override
    public void onDeleteData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new SupportUI().showResponse(MainActivity.this, "Deleted", "Data was Deleted.", true);
                ;
            }
        });
    }
}
