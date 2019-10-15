package com.jat.medilinkapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "NfcData")
public class NfcData implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "is_send")
    private boolean isSend;

    @ColumnInfo(name = "created_date")
    public String createDate;
    //final String date = DateFormat.getDateInstance().format(new Date());

    @ColumnInfo(name = "calltype")
    @SerializedName("calltype")
    @Expose
    private String calltype;

    @ColumnInfo(name = "client_id")
    @SerializedName("client_id")
    @Expose
    private Integer clientId;

    @ColumnInfo(name = "employee_id")
    @SerializedName("employee_id")
    @Expose
    private Integer employeeId;

    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    private Integer id;

    @ColumnInfo(name = "nfc")
    @SerializedName("nfc")
    @Expose
    private String nfc;

    @ColumnInfo(name = "office_id")
    @SerializedName("office_id")
    @Expose
    private Integer officeid;

    @ColumnInfo(name = "tasktype")
    @SerializedName("tasktype")
    @Expose
    private String tasktype;

    @ColumnInfo(name = "app_sender")
    @SerializedName("app_sender")
    @Expose
    private String appSender;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

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

    public String getAppSender() {
        return appSender;
    }

    public void setAppSender(String appSender) {
        this.appSender = appSender;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public NfcData() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NfcData)) return false;
        NfcData nfcData = (NfcData) o;
        return getUid() == nfcData.getUid() &&
                isSend() == nfcData.isSend() &&
                Objects.equals(createDate, nfcData.createDate) &&
                Objects.equals(getCalltype(), nfcData.getCalltype()) &&
                Objects.equals(getClientId(), nfcData.getClientId()) &&
                Objects.equals(getEmployeeId(), nfcData.getEmployeeId()) &&
                Objects.equals(getId(), nfcData.getId()) &&
                Objects.equals(getNfc(), nfcData.getNfc()) &&
                Objects.equals(getOfficeid(), nfcData.getOfficeid()) &&
                Objects.equals(getTasktype(), nfcData.getTasktype()) &&
                Objects.equals(getAppSender(), nfcData.getAppSender());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid(), isSend(), createDate, getCalltype(), getClientId(), getEmployeeId(), getId(), getNfc(), getOfficeid(), getTasktype(), getAppSender());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.uid);
        dest.writeByte(this.isSend ? (byte) 1 : (byte) 0);
        dest.writeString(this.createDate);
        dest.writeString(this.calltype);
        dest.writeValue(this.clientId);
        dest.writeValue(this.employeeId);
        dest.writeValue(this.id);
        dest.writeString(this.nfc);
        dest.writeValue(this.officeid);
        dest.writeString(this.tasktype);
        dest.writeString(this.appSender);
    }

    protected NfcData(Parcel in) {
        this.uid = in.readInt();
        this.isSend = in.readByte() != 0;
        this.createDate = in.readString();
        this.calltype = in.readString();
        this.clientId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.employeeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.nfc = in.readString();
        this.officeid = (Integer) in.readValue(Integer.class.getClassLoader());
        this.tasktype = in.readString();
        this.appSender = in.readString();
    }

    public static final Creator<NfcData> CREATOR = new Creator<NfcData>() {
        @Override
        public NfcData createFromParcel(Parcel source) {
            return new NfcData(source);
        }

        @Override
        public NfcData[] newArray(int size) {
            return new NfcData[size];
        }
    };
}
