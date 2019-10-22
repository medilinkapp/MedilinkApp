package com.jat.medilinkapp.model.entity;

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
