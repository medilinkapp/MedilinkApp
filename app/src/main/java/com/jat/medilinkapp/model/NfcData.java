package com.jat.medilinkapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NfcData {

    @SerializedName("calltype")
    @Expose
    private String calltype;
    @SerializedName("client_id")
    @Expose
    private Integer clientId;
    @SerializedName("employee_id")
    @Expose
    private Integer employeeId;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nfc")
    @Expose
    private String nfc;
    @SerializedName("office_id")
    @Expose
    private Integer officeid;
    @SerializedName("tasktype")
    @Expose
    private String tasktype;

    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNfc() {
        return nfc;
    }

    public void setNfc(String nfc) {
        this.nfc = nfc;
    }

    public Integer getOfficeid() {
        return officeid;
    }

    public void setOfficeid(Integer officeid) {
        this.officeid = officeid;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

}
