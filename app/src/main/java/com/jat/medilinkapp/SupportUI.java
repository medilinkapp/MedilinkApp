package com.jat.medilinkapp;

import android.app.Dialog;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jat.medilinkapp.model.entity.NfcData;
import com.jat.medilinkapp.util.ISingleActionCallBack;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

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

    public Observable<Boolean> checkInternetConnetion() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    InetAddress ipAddr = InetAddress.getByName("google.com");
                    subscriber.onNext(!ipAddr.equals(""));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
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
            dialogWF.dismiss();
        });
        dialogWF.show();
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
        return DATE_TIME_INSTANCE.format(new Date());
    }

    public long diffMinutesDateTimes(Date dateStart, Date dateEnd) {
        long diffInMillisec = dateEnd.getTime() - dateStart.getTime();
        long diffInMin = TimeUnit.MILLISECONDS.toMinutes(diffInMillisec);
        return diffInMin;
    }


}
