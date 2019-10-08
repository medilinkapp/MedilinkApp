package com.jat.medilinkapp.nfcconf;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.jat.medilinkapp.MainActivity;
import com.jat.medilinkapp.R;


public class NfcTag {

    private TextView nfc_text;
    private NfcAdapter nfc_adapter;
    private PendingIntent pending_intent;
    private IntentFilter[] write_tag_filters;
    private static final String TAG = "MEDILINK";

    public NfcTag() {
    }

    public void init(MainActivity activity) {
        nfc_adapter = NfcAdapter.getDefaultAdapter(activity);
        nfc_text = (EditText) activity.findViewById(R.id.et_nfc);

        if (nfc_adapter == null) {
            nfc_text.setText("NFC not supported.");
        } else {
            if (!nfc_adapter.isEnabled()) {
                nfc_text.setText("NFC is disabled.");
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
            nfc_text.setText(id);
            return id;
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
    }

    public void pause(MainActivity activity) {
        Log.d(TAG, "pause()");
        nfc_adapter.disableForegroundDispatch(activity);
    }


}

