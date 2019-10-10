package com.jat.medilinkapp;

import android.app.Dialog;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;

import rx.Observable;
import rx.Subscriber;

public class SupportUI {

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


    public  Dialog showProgress(MainActivity activity) {
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
}
