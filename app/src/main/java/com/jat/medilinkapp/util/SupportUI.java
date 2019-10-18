package com.jat.medilinkapp.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.jat.medilinkapp.MainActivity;
import com.jat.medilinkapp.R;
import com.jat.medilinkapp.model.entity.NfcData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SupportUI {


    public static final String IN = "In";
    public static final String OUT = "Out";
    public static final DateFormat DATE_TIME_INSTANCE = DateFormat.getDateTimeInstance();

    public void showResponse(MainActivity activity, String titleMsg, String message, boolean success) {
        // custom dialog
        final Dialog dialogWF;
        dialogWF = new Dialog(activity, R.style.dialogStyle);
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

        TextView tvTitle = dialogWF.findViewById(R.id.tv_title_layout);
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
                activity.finish();
            } else {
                dialogWF.dismiss();
            }
        });
        dialogWF.show();
    }


    public Dialog showProgress(MainActivity activity) {
        // custom dialog
        final Dialog dialogWF;
        dialogWF = new Dialog(activity, R.style.dialogStyle);
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

    public void checkInternetConnetion(IRxActionCallBack callBack) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            try {
                InetAddress ipAddr = InetAddress.getByName("google.com");
                emitter.onNext(!ipAddr.equals(""));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                               @Override
                               public void onSubscribe(Disposable d) {
                               }

                               @Override
                               public void onNext(Boolean aBoolean) {
                                   if (aBoolean) {
                                       callBack.completed();
                                   }
                               }

                               @Override
                               public void onError(Throwable e) {
                                   callBack.error();
                               }

                               @Override
                               public void onComplete() {
                               }
                           }
                );
    }

    public String getFormatDataSendTasks(ArrayList<String> list) {
        if (list != null && !list.isEmpty()) {
            String taskString = "";
            for (String s : list) {
                taskString = taskString + "|" + s;
            }
            return taskString.substring(1, taskString.length());
        }
        return "";
    }


    public void showResentDialog(MainActivity activity, ISingleActionCallBack iSingleActionCallBack, NfcData nfcData) {
        // custom dialog
        final Dialog dialogWF;
        dialogWF = new Dialog(activity, R.style.dialogStyle);
        dialogWF.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWF.setContentView(R.layout.dialog_resent);
        dialogWF.setCanceledOnTouchOutside(false);
        dialogWF.setCancelable(false);
        // Setting dialogview
        Window window = dialogWF.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView tvEmployee = dialogWF.findViewById(R.id.tv_employee);
        TextView tvClient = dialogWF.findViewById(R.id.tv_client);
        TextView tvOffice = dialogWF.findViewById(R.id.tv_office);
        TextView tvInOut = dialogWF.findViewById(R.id.tv_in_out);
        TextView tvTasks = dialogWF.findViewById(R.id.tv_tasks);
        TextView tvDate_time = dialogWF.findViewById(R.id.tv_date_time);

        tvEmployee.setText(String.valueOf(nfcData.getEmployeeId()));
        tvClient.setText(String.valueOf(nfcData.getClientId()));
        tvOffice.setText(String.valueOf(nfcData.getOfficeid()));
        tvInOut.setText(nfcData.getCalltype().equals(activity.getString(R.string.CALLTYPE_IN)) ? IN : OUT);
        if (nfcData.getCalltype().equals("I")) {
            dialogWF.findViewById(R.id.tv_label_tasks).setVisibility(View.GONE);
            tvTasks.setVisibility(View.GONE);
        } else {
            tvTasks.setText(nfcData.getTasktype().replace("|", ","));
        }

        tvDate_time.setText(nfcData.getCreateDate());

        Button btResend = dialogWF.findViewById(R.id.bt_resend);
        btResend.setOnClickListener(v -> {
            dialogWF.dismiss();
            iSingleActionCallBack.callBack();
        });

        Button btCancel = dialogWF.findViewById(R.id.bt_cancel);
        btCancel.setOnClickListener(v -> {
            dialogWF.dismiss();
        });

        dialogWF.show();
    }


    public void showDialogInfo(MainActivity activity, String titleMsg, String message) {
        showDialogInfo(activity, titleMsg, message, null);
    }

    public void showDialogInfo(MainActivity activity, String titleMsg, String message, ISingleActionCallBack callBack) {
        // custom dialog
        final Dialog dialogWF;
        dialogWF = new Dialog(activity, R.style.dialogStyle);
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

        TextView tvTitle = dialogWF.findViewById(R.id.tv_title_layout);
        TextView tvTitleMessage = dialogWF.findViewById(R.id.tv_title_message);
        TextView tvMsgDialog = dialogWF.findViewById(R.id.tv_msg_dialog);
        Button btOK = dialogWF.findViewById(R.id.bt_ok);

        tvTitleMessage.setText(titleMsg);
        tvMsgDialog.setText(message);

        btOK.setOnClickListener(v -> {
            if (callBack != null) {
                callBack.callBack();
            }
            dialogWF.dismiss();
        });
        dialogWF.show();
    }


    public void verifyIfNfcOn(MainActivity context, ISingleActionCallBack callBack) {
        android.nfc.NfcAdapter mNfcAdapter = android.nfc.NfcAdapter.getDefaultAdapter(context);

        if (mNfcAdapter != null && !mNfcAdapter.isEnabled()) {
            showDialogInfo(context, context.getString(R.string.nfc_not_enabled), context.getString(R.string.message_turn_on_nfc), new ISingleActionCallBack() {
                @Override
                public void callBack() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        context.startActivity(intent);
                    }
                }
            });
        } else {
            if (callBack != null)
                callBack.callBack();
        }
    }

    public static void showKeyboard(EditText mEtSearch, Context context) {
        mEtSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public Date fromStringToDate(String sDate) {
        try {
            Date d = DATE_TIME_INSTANCE.parse(sDate);
            return d;
        } catch (ParseException ex) {
            Log.v("Exception", ex.getLocalizedMessage());
        }
        return null;
    }

    public String fromDateToString(Date date) {
        return DATE_TIME_INSTANCE.format(date);
    }

    public long diffMinutesDateTimes(Date dateStart, Date dateEnd) {
        long diffInMillisec = dateEnd.getTime() - dateStart.getTime();
        long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMillisec);
        return diffInMin;
    }

    public boolean isYesterday(Date myDate) {
        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        Calendar c2 = Calendar.getInstance();
        c2.setTime(myDate); // your date

        if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
            return true;
        }
        return false;
    }

    public String getYesterday() {
        Calendar c1 = Calendar.getInstance(); // today
        c1.add(Calendar.DAY_OF_YEAR, -1); // yesterday
        return fromDateToString(c1.getTime());
    }


}
