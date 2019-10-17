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
import com.jat.medilinkapp.nfcconf.NfcTagHandler;
import com.jat.medilinkapp.util.SharePreferencesUtil;
import com.jat.medilinkapp.viewmodels.NfcDataHistoryViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.observers.BlockingBaseObserver;

public class MainActivity extends AppCompatActivity implements MyFragmentDialogTasks.DialogListener, MyFragmentDialogHistory.DialogListener {

    public static final String LIST = "list";
    public static final String NOT_ALERT_DIALOG = "notAlertDialog";
    public static final String DIALOG_TAG = "dialog";
    public static final String DIALOG_TAG_HISTORY = "dialog_history";
    public static final String EMPLOYEEID_PREFERENCE = "employeeid";
    public static final String OFFICEID_PREFERENCE = "officeid";
    public static final String CLIENTID_PREFERENCE = "clientid";
    public static final int MINUTES_WAIT_TO_SEND_AGAIN = 5;
    private APIService mAPIService;
    private String TAG = "MEDILINK TAG";
    private NfcTagHandler nfcTagHandler;
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

    @BindView(R.id.layout_nfc_card)
    View layoutTvNfc;

    @BindView(R.id.tv_tasks_string)
    TextView tvTasks;

    @BindView(R.id.layout_task)
    View layoutTask;

    @BindView(R.id.bt_history)
    View btHistory;

    private String nfc_id;

    NfcDataHistoryViewModel viewModel;

    MyFragmentDialogHistory myDialogHistory;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private SharePreferencesUtil sharePreferencesUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAPIService = ApiUtils.getAPIService();

        nfcTagHandler = new NfcTagHandler();
        nfcTagHandler.init(this);

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
            nfc_id = nfcTagHandler.handleIntent(intent);
        }
    }

    @OnClick(R.id.bt_send)
    void submitForm() {
        submit(new NfcData());
    }


    Disposable disposable;

    void submit(NfcData nfcData) {
        if (validate()) {
            nfcData.setEmployeeId(Integer.valueOf(employeeid.getText().toString()));
            nfcData.setClientId(Integer.valueOf(clientid.getText().toString()));
            nfcData.setOfficeid(Integer.valueOf(officeid.getText().toString()));
            nfcData.setCalltype(cbIn.isChecked() ? MainActivity.this.getString(R.string.CALLTYPE_IN) : MainActivity.this.getString(R.string.CALLTYPE_OUT));

            if (TextUtils.isEmpty(nfcData.getCreateDate())) {
                final String date = new SupportUI().fromDateToString(new Date());
                nfcData.setCreateDate(date);
            }

            nfcData.setNfc(tvNfc.getText().toString());
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
                            disposable = d;
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
                                    if (min < MINUTES_WAIT_TO_SEND_AGAIN &&
                                            item.getCalltype().equals(nfcData.getCalltype())
                                            && item.getClientId() == nfcData.getClientId()) {
                                        runOnUiThread(() -> {
                                            if (BuildConfig.DEBUG) {
                                                new SupportUI().showDialogInfo(MainActivity.this, "Visit coul be repeat", "Looks like you have a visit with the send information and almost same time. (" + min + " m ago)  \n " + item.getUid() + " - " + item.getCreateDate() + "\n Try to resend.");
                                            } else {
                                                new SupportUI().showDialogInfo(MainActivity.this, "Visit coul be repeat", "Looks like you have a visit with the send information and almost same time. (" + min + " m ago)" + " \n " + item.getCreateDate() + "\n Try to resend.");
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
                            disposable.dispose();
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
        new SupportUI().checkInternetConnetion().subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                               }

                               @Override
                               public void onNext(Boolean aBoolean) {
                                   if (aBoolean)
                                       sendDataPost(nfcData);
                               }

                               @Override
                               public void onError(Throwable e) {
                                   new SupportUI().showResponse(MainActivity.this, "Error", "Not internet connection!", false);
                                   AsyncTask.execute(() -> viewModel.addData(nfcData));
                               }

                               @Override
                               public void onComplete() {
                               }
                           }
                );
    }

    private void sendDataPost(NfcData nfcData) {
        // RxJava
        final Dialog progress = new SupportUI().showProgress(this);
        mAPIService.savePost(nfcData)
                .subscribeOn(rx.schedulers.Schedulers.newThread())
                .observeOn(rx.schedulers.Schedulers.trampoline())
                .subscribe(new rx.Observer<NfcData>() {
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
                // tvNfc.setText(nfcData.getNfc());

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
}
