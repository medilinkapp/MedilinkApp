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
import com.jat.medilinkapp.model.entity.NfcDataTag;
import com.jat.medilinkapp.util.Effects;
import com.jat.medilinkapp.util.ISingleActionCallBack;
import com.jat.medilinkapp.util.SupportUI;
import java.io.UnsupportedEncodingException;
import org.jetbrains.annotations.NotNull;


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

        Intent intent = activity.getIntent();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            handleIntent(intent);
        }

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

    public NfcDataTag handleIntent(Intent intent) {
        String id = "";
        String ws = "";
        Log.d(TAG, "handleIntent() " + intent.toString());

        ws = getWsFromNFC(intent);

        //use it to read the serial number in the card
        id = getSerialRecordFromNfc(intent);

        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(ws)) {
            showCheckedNfc(id);
            Effects.getInstance().playSoundShort(Effects.Sound.CLICK);
            return new NfcDataTag(id, ws);
        } else {
            showErrorTvNfc("invalid card!");
            Effects.getInstance().playSoundShort(Effects.Sound.WRONG);
        }
        return new NfcDataTag("", "");
    }

    @NotNull
    private String getSerialRecordFromNfc(Intent intent) {
        String id;
        byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        String hexdump = new String();
        for (int i = 0; i < tagId.length; i++) {
            String x = Integer.toHexString(((int) tagId[i] & 0xff));
            if (x.length() == 1) {
                x = '0' + x;
            }
            hexdump += x;
        }
        Log.i("id", hexdump);
        id = hexdump.toUpperCase();

        if (id.length() >= 8) {
            id = id.substring(id.length() - 8);
        }
        return id;
    }

    private String getWsFromNFC(Intent intent) {
        String ws = "";
        if (intent.hasExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)) {
            try {
                ws = readMessagesNFC(intent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return ws;
    }

    private String readMessagesNFC(Intent intent) throws UnsupportedEncodingException {
        Parcelable[] rawMsgs =
                intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMsgs != null && rawMsgs.length > 0) {
            NdefMessage msg = (NdefMessage) rawMsgs[0];

            byte[] payload = msg.getRecords()[0].getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = 0;
            String ws = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            return ws;
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
        new SupportUI().verifyIfNfcOn(activity, new ISingleActionCallBack() {
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

