package com.jat.medilinkapp.backgroudservice;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.jat.medilinkapp.util.SupportUI;
import com.jat.medilinkapp.model.entity.NfcData;
import com.jat.medilinkapp.util.IRxActionCallBack;
import java.util.ArrayList;

public class MyIntentServiceVisitPastDay extends IntentService {
    private static final String ACTION_RESEND_VISIT = "com.jat.medilinkapp.backgroudservice.action.ACTION_RESEND_VISIT";

    private static final String EXTRA_LIST_VISITS = "com.jat.medilinkapp.backgroudservice.extra.LIST_VISITS";

    public MyIntentServiceVisitPastDay() {
        super("MyIntentServiceVisitPastDay");
    }

    /**
     * the service is already performing a task this action will be queued.
     */
    public static void startActionActionResendVisit(Context context, ArrayList<NfcData> list) {
        Intent intent = new Intent(context, MyIntentServiceVisitPastDay.class);
        intent.setAction(ACTION_RESEND_VISIT);
        intent.putParcelableArrayListExtra(EXTRA_LIST_VISITS, list);
        context.startService(intent);
        Log.i(TaskReSendVisit.class.getSimpleName(), "Server Init.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RESEND_VISIT.equals(action)) {
                final ArrayList<NfcData> list = intent.getParcelableArrayListExtra(EXTRA_LIST_VISITS);
                handleActionResendVisit(list);
            }
        }
    }

    /**
     * Handle action ACTION_RESEND_VISIT in the provided background thread with the provided
     * parameters.
     */
    private void handleActionResendVisit(ArrayList<NfcData> list) {
        new SupportUI().checkInternetConnetion(new IRxActionCallBack() {
            @Override
            public void completed() {
                new TaskReSendVisit(list, MyIntentServiceVisitPastDay.this).start();
            }

            @Override
            public void error() {
            }
        });

    }
}
