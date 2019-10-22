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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jat.medilinkapp.backgroudservice.MyIntentServiceVisitPastDay;
import com.jat.medilinkapp.conf.APIService;
import com.jat.medilinkapp.conf.ApiUtils;
import com.jat.medilinkapp.model.entity.NfcData;
import com.jat.medilinkapp.model.entity.NfcDataTag;
import com.jat.medilinkapp.nfcconf.NfcTagHandler;
import com.jat.medilinkapp.util.IRxActionCallBack;
import com.jat.medilinkapp.util.SharePreferencesUtil;
import com.jat.medilinkapp.util.SupportUI;
import com.jat.medilinkapp.viewmodels.NfcDataHistoryViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.observers.BlockingBaseObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements MyFragmentDialogTasks.DialogListener, MyFragmentDialogHistory.DialogListener {

    public static final String LIST = "list";
    public static final String NOT_ALERT_DIALOG = "notAlertDialog";
    public static final String DIALOG_TAG = "dialog";
    public static final String DIALOG_TAG_HISTORY = "dialog_history";
    public static final String EMPLOYEEID_PREFERENCE = "employeeid";
    public static final String OFFICEID_PREFERENCE = "officeid";
    public static final String CLIENTID_PREFERENCE = "clientid";
    public static final String CHECK_YESTERDAY_UNSENT_VISITS = "CHECK_YESTERDAY_UNSENT_VISITS";
    private String TAG = "MEDILINK TAG";
    private APIService mAPIService;

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

    @BindView(R.id.layout_nfc_card)
    View layoutTvNfc;

    @BindView(R.id.tv_tasks_string)
    TextView tvTasks;

    @BindView(R.id.layout_task)
    View layoutTask;

    @BindView(R.id.bt_history)
    View btHistory;

    private NfcTagHandler nfcTagHandler;
    ArrayList<String> listTasks;
    NfcDataHistoryViewModel viewModel;
    MyFragmentDialogHistory myDialogHistory;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private SharePreferencesUtil sharePreferencesUtil;
    Disposable disposableVisitHistory;
    Disposable disposableUnSentVisits;
    private NfcDataTag nfcDataTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        nfcTagHandler = new NfcTagHandler();
        nfcTagHandler.init(this);

        sharePreferencesUtil = new SharePreferencesUtil(this);
        viewModel = ViewModelProviders.of(this).get(NfcDataHistoryViewModel.class);

        nfcDataTag = new NfcDataTag("","");

        //test
        //AsyncTask.execute(() -> viewModel.deleteAll());
        //sharePreferencesUtil.setValue(CHECK_YESTERDAY_UNSENT_VISITS, new SupportUI().getYesterday());
        checkUnsentVisitsToSendInBackGround();

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

        employeeid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    employeeid.setText("");
                }
            }
        });

        clientid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    clientid.setText("");
                }
            }
        });

        officeid.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    officeid.setText("");
                }
            }
        });

        employeeid.setText(sharePreferencesUtil.getValue(EMPLOYEEID_PREFERENCE, ""));
        clientid.setText(sharePreferencesUtil.getValue(CLIENTID_PREFERENCE, ""));
        officeid.setText(sharePreferencesUtil.getValue(OFFICEID_PREFERENCE, ""));

        if (BuildConfig.DEBUG) {
            // btHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            nfcDataTag = nfcTagHandler.handleIntent(intent);
        }
    }

    private void checkUnsentVisitsToSendInBackGround() {
        SharePreferencesUtil sharePreferencesUtil = new SharePreferencesUtil(this);
        String dateString = sharePreferencesUtil.getValue(CHECK_YESTERDAY_UNSENT_VISITS, "");

        if (!dateString.equals(new SupportUI().fromDateToString(new Date()))) {
            viewModel.getListIsSend(false).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(new Consumer<Disposable>() {
                @Override
                public void accept(Disposable d) throws Exception {
                    disposableUnSentVisits = d;
                }
            }).subscribe(new BlockingBaseObserver<List<NfcData>>() {
                @Override
                public void onNext(List<NfcData> list) {
                    if (!list.isEmpty()) {
                        SupportUI supportUI = new SupportUI();
                        int count = list.size();
                        ArrayList<NfcData> yesterdayList = new ArrayList<>();
                        for (NfcData item : list) {
                            if (supportUI.isYesterday(supportUI.fromStringToDate(item.createDate))) {
                                yesterdayList.add(item);
                            }
                        }
                        if (!yesterdayList.isEmpty()) {
                            supportUI.showDialogInfo(MainActivity.this,
                                    MainActivity.this.getString(R.string.information),
                                    String.format(MainActivity.this.getString(R.string.message_sending_past_visit_background), yesterdayList.size()));

                            MyIntentServiceVisitPastDay.startActionActionResendVisit(MainActivity.this, yesterdayList);
                            sharePreferencesUtil.setValue(CHECK_YESTERDAY_UNSENT_VISITS, supportUI.fromDateToString(new Date()));
                        }
                    }
                    disposableUnSentVisits.dispose();
                }

                @Override
                public void onError(Throwable e) {
                }
            });
        }
    }


    @OnClick(R.id.bt_send)
    void submitForm() {
        submit(new NfcData());
    }

    void submit(NfcData nfcData) {
        if (validate()) {
            nfcData.setEmployeeId(Integer.valueOf(employeeid.getText().toString()));
            nfcData.setClientId(Integer.valueOf(clientid.getText().toString()));
            nfcData.setOfficeid(Integer.valueOf(officeid.getText().toString()));
            nfcData.setCalltype(cbIn.isChecked() ? MainActivity.this.getString(R.string.CALLTYPE_IN) : MainActivity.this.getString(R.string.CALLTYPE_OUT));

            if (TextUtils.isEmpty(nfcData.getCreateDate())) {
                final String date = new SupportUI().fromDateToString(new Date());
                nfcData.setCreateDate(date);
                //test background resend
                //String yesterday = new SupportUI().getYesterday();
                //nfcData.setCreateDate(yesterday);
            }

            nfcData.setNfc(nfcDataTag.serialRecord);
            nfcData.setWs(nfcDataTag.webService);

            nfcData.setTasktype(cbOut.isChecked() ? new SupportUI().getFormatDataSendTasks(listTasks) : "");
            nfcData.setAppSender(this.getString(R.string.android_sender));

            //check is there is a post with almost the same date time.
            int subEnd = nfcData.getCreateDate().length() - 7;
            String subS = nfcData.getCreateDate().substring(0, subEnd);

            //Verify is there is another post in history within 5 minutes, so i wont send post again
            viewModel.getListBySubCreateData(subS + "%")
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.schedulers.Schedulers.trampoline())
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable d) throws Exception {
                            disposableVisitHistory = d;
                        }
                    })
                    .subscribe(new BlockingBaseObserver<List<NfcData>>() {
                        @Override
                        public void onNext(List<NfcData> nfcDatas) {
                            SupportUI supportUI = new SupportUI();
                            if (nfcDatas == null || nfcDatas.isEmpty()) {
                                sendPost(nfcData);
                            } else {
                                boolean send = true;
                                for (NfcData item : nfcDatas) {
                                    long min = supportUI.diffMinutesDateTimes(
                                            supportUI.fromStringToDate(item.createDate),
                                            supportUI.fromStringToDate(nfcData.createDate));
                                    if (min < ConfigValuesApp.MINUTES_WAIT_TO_SEND_AGAIN &&
                                            item.getCalltype().equals(nfcData.getCalltype())
                                            && item.getClientId() == nfcData.getClientId()
                                            && item.getNfc().equals(nfcData.getNfc())) {
                                        runOnUiThread(() -> {
                                            if (BuildConfig.DEBUG) {
                                                new SupportUI().showDialogInfo(MainActivity.this,
                                                        MainActivity.this.getString(R.string.duplicate_visit),
                                                        MainActivity.this.getString(R.string.message_duplicate_visit) + "(" + min + "m ago)\n" + item.getUid() + " - " + item.getCreateDate()
                                                        , () -> showHistoryFragment());
                                            } else {
                                                new SupportUI().showDialogInfo(MainActivity.this,
                                                        MainActivity.this.getString(R.string.duplicate_visit),
                                                        MainActivity.this.getString(R.string.message_duplicate_visit)
                                                        , () -> showHistoryFragment());
                                            }
                                        });
                                        send = false;
                                        break;
                                    }
                                }
                                if (send) {
                                    sendPost(nfcData);
                                }
                            }
                            disposableVisitHistory.dispose();
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    });

            //saving values for next time show defaults
            sharePreferencesUtil.setValue(EMPLOYEEID_PREFERENCE, String.valueOf(nfcData.getEmployeeId()));
            sharePreferencesUtil.setValue(OFFICEID_PREFERENCE, String.valueOf(nfcData.getOfficeid()));
            sharePreferencesUtil.setValue(CLIENTID_PREFERENCE, String.valueOf(nfcData.getClientId()));
        }
    }

    @OnClick({R.id.bt_history, R.id.tv_history_visit_bt})
    void showHistoryFragment() {
        myDialogHistory = new MyFragmentDialogHistory();
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
        MyFragmentDialogTasks dialogFragment = new MyFragmentDialogTasks();

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
        boolean validateEmployee = validateLength(employeeid, 5, 12, this.getString(R.string.label_employee));
        boolean validateClient = validateLength(clientid, 1, 5, this.getString(R.string.label_client));
        boolean validateOffice = validateLength(officeid, 1, 2, this.getString(R.string.label_office));
        return validateEmployee && validateClient && validateOffice;
    }

    private boolean validateLength(EditText editText, int min, int max, String nameField) {
        if (editText.getText().length() < min || editText.getText().length() > max) {
            editText.setError(String.format(this.getString(R.string.error_message_length), nameField, min, max));
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

        if ((TextUtils.isEmpty(nfcDataTag.serialRecord) || TextUtils.isEmpty(nfcDataTag.webService)) && !BuildConfig.DEBUG) {
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
        new SupportUI().checkInternetConnetion(new IRxActionCallBack() {
            @Override
            public void completed() {
                sendDataPost(nfcData);
            }

            @Override
            public void error() {
                new SupportUI().showResponse(MainActivity.this, "Error", "Not internet connection!", false);
                AsyncTask.execute(() -> viewModel.addData(nfcData));
            }
        });
    }

    private void sendDataPost(NfcData nfcData) {
        //clean nfc from view to force user to use card again after submit
        nfcDataTag.clear();
        cleanTvNfc();

        // RxJava
        final Dialog progress = new SupportUI().showProgress(this);
        mAPIService = ApiUtils.getAPIService(nfcData.getWs());
        mAPIService.sendPost(nfcData)
                .subscribeOn(rx.schedulers.Schedulers.newThread())
                .observeOn(rx.schedulers.Schedulers.trampoline())
                .subscribe(new rx.Observer<NfcData>() {
                    @Override
                    public void onCompleted() {
                        progress.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new SupportUI().showResponse(MainActivity.this, "Sent", "Data was sent.", true);
                            }
                        });
                        nfcData.setSend(true);
                        //add to the history
                        AsyncTask.execute(() -> viewModel.addData(nfcData));
                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new SupportUI().showResponse(MainActivity.this, "Error", e.getLocalizedMessage(), false);
                            }
                        });
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
        nfcTagHandler.resume(this);
        //checkUnsentVisitsToSendInBackGround();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcTagHandler.pause(this);
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

    //data to resend
    @Override
    public void onFinishSelectionData(@NotNull NfcData nfcData) {
        if (!nfcData.isSend()) {
            new SupportUI().showResentDialog(this, () -> {
                if (myDialogHistory != null) {
                    myDialogHistory.dismiss();
                }
                //deletede from history in order to dont duplicate
                viewModel.delete(nfcData);
                //AsyncTask.execute(() -> viewModel.delete(nfcData));

                employeeid.setText(String.valueOf(nfcData.getEmployeeId()));
                clientid.setText(String.valueOf(nfcData.getClientId()));
                officeid.setText(String.valueOf(nfcData.getOfficeid()));

                nfcTagHandler.showCheckedNfc(nfcData.getNfc());
                nfcDataTag.serialRecord = nfcData.getNfc();
                nfcDataTag.webService = nfcData.getWs();

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
                submit(nfcData);

                //new SupportUI().showDialogInfo(MainActivity.this, "Data recovered ","Please scan your card again and process to send.");
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

    public void cleanTvNfc() {
        tvNfc.setText(this.getString(R.string.nfc_default_message));
        tvNfc.setError(null);
        tvNfc.setVisibility(View.VISIBLE);
        layoutTvNfc.setVisibility(View.VISIBLE);
        findViewById(R.id.img_nfc_checked).setVisibility(View.GONE);
    }
}
