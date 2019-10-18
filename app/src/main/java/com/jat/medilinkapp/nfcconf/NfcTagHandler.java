package com.jat.medilinkapp.nfcconf;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jat.medilinkapp.MainActivity;
import com.jat.medilinkapp.R;
import com.jat.medilinkapp.SupportUI;
import com.jat.medilinkapp.util.Effects;
import com.jat.medilinkapp.util.ISingleActionCallBack;


public class NfcTagHandler {

    private TextView nfc_text;
    private View layoutNfc_text;


    private NfcAdapter nfc_adapter;
    private ImageView nfc_checked_img;
    private PendingIntent pending_intent;
    private IntentFilter[] write_tag_filters;
    private static final String TAG = "MEDILINK";

    public NfcTagHandler() {
    }

    public void init(MainActivity activity) {
        nfc_adapter = NfcAdapter.getDefaultAdapter(activity);
        nfc_text = activity.findViewById(R.id.tv_nfc);
        layoutNfc_text = activity.findViewById(R.id.layout_nfc_card);
        nfc_checked_img = activity.findViewById(R.id.img_nfc_checked);

        if (nfc_adapter == null) {
            showErrorTvNfc("NFC not supported");
        } else {
            if (!nfc_adapter.isEnabled()) {
                //showErrorTvNfc("NFC is disabled");
            }
        }

        handleIntent(activity.getIntent());

        pending_intent = PendingIntent.getActivity(
                activity,
                0,
                new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);

        IntentFilter tag_discovered =
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        tag_discovered.addCategory(Intent.CATEGORY_DEFAULT);
        write_tag_filters = new IntentFilter[]{tag_discovered};

        Effects.getInstance().init(activity);
    }

    public String handleIntent(Intent intent) {
        String action = intent.getAction();

        Log.d(TAG, "handleIntent() " + intent.toString());

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.d(TAG, "NDEF DISCOVERED");
            if (intent.hasExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)) {
                return readMessagesNFC(intent);
            }
            return "";
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.d(TAG, "TECH DISCOVERED");

            return "";
        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            if (intent.hasExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)) {
                return readMessagesNFC(intent);
            } else {
                showErrorTvNfc("invalid card!");
                Effects.getInstance().playSoundShort(Effects.Sound.WRONG);
            }
        }

//        } else if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//            Parcelable[] rawMessages =
//                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//            if (rawMessages != null) {
//                NdefMessage[] messages = new NdefMessage[rawMessages.length];
//                for (int i = 0; i < rawMessages.length; i++) {
//                    messages[i] = (NdefMessage) rawMessages[i];
//                }
//
//                // Process the messages array.
//            }
//        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
//            Log.d(TAG, "TAG_DISCOVERED");
//
//            Parcelable[] rawMessages =
//                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_TAG);
//
//            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//
//            if (tag != null) {
//                Log.d(TAG, tag.toString());
//            }
//
//            if (intent.hasExtra(NfcAdapter.EXTRA_ID)) {
//                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
//                int n;
//                int number = 0;
//                String id_string = "";
//
//                for (n = 0; n < id.length; n++) {
//                    number = number << 8;
//                    number |= ((int) id[n]) & 0xff;
//
//                    if (n != 0) {
//                        id_string += ".";
//                    }
//                    String hex = Integer.toHexString(((int) id[n]) & 0xff);
//
//                    if (hex.length() == 1) {
//                        hex = "0" + hex;
//                    }
//                    id_string += hex;
//                }
//                Log.d(TAG, "id: " + number);
//                nfc_text.setText(id_string);
//            }
//            return true;
//        }
        return "";
    }

    private String readMessagesNFC(Intent intent) {
        Parcelable[] rawMsgs =
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null && rawMsgs.length > 0) {
            NdefMessage msg = (NdefMessage) rawMsgs[0];
            String id = new String(msg.getRecords()[0].getPayload());
            //if size >= 8 taking last 8dig
            if (id.length() >= 8) {
                id = id.substring(id.length() - 8, id.length());
            }
            showCheckedNfc(id);
            Effects.getInstance().playSoundShort(Effects.Sound.RIGHT);
            return id;
        } else {
            showErrorTvNfc("Card with empty value!");
            Effects.getInstance().playSoundShort(Effects.Sound.WRONG);
        }
        return "";
    }

    public void resume(MainActivity activity) {
        Log.d(TAG, "resume()");
        nfc_adapter.enableForegroundDispatch(
                activity,
                pending_intent,
                write_tag_filters,
                null);
        new SupportUI().verifyNfcOn(activity, new ISingleActionCallBack() {
            @Override
            public void callBack() {
                if (nfc_adapter.isEnabled() && TextUtils.isEmpty(nfc_text.getText())) {
                    nfc_text.setText(activity.getString(R.string.nfc_default_message));
                    nfc_text.setError(null);
                }
            }
        });

    }

    public void pause(MainActivity activity) {
        Log.d(TAG, "pause()");
        nfc_adapter.disableForegroundDispatch(activity);
    }

    public void showErrorTvNfc(String s) {
        nfc_text.setText(s);
        nfc_text.setError(s);
        nfc_text.setVisibility(View.VISIBLE);
        layoutNfc_text.setVisibility(View.VISIBLE);
        nfc_checked_img.setVisibility(View.GONE);
    }

    public void showCheckedNfc(String id) {
        nfc_text.setText(id);
        nfc_text.setVisibility(View.INVISIBLE);
        layoutNfc_text.setVisibility(View.INVISIBLE);
        nfc_checked_img.setVisibility(View.VISIBLE);
        nfc_text.setError(null);
    }

}

