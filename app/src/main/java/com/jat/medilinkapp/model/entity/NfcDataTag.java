package com.jat.medilinkapp.model.entity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.jat.medilinkapp.MainActivity;

public class NfcDataTag {
    public String serialRecord;
    public String webService;

    public NfcDataTag(String serialRecord, String webService) {
        this.serialRecord = serialRecord;
        this.webService = webService;
    }

    public void clear() {
        serialRecord = "";
        webService = "";
    }
}
