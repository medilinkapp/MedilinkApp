package com.jat.medilinkapp.model.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Objects;

@Entity(tableName = "NfcData")
public class NfcData2 implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "is_send")
    private boolean isSend;

    @ColumnInfo(name = "created_date")
    @SerializedName("created_date")
    @Expose
    private String createDate;

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

    @ColumnInfo(name = "ws")
    private String ws;

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

    @ColumnInfo(name = "phoneNumber")
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    @ColumnInfo(name = "gps")
    @SerializedName("gps")
    @Expose
    private String gps;

    public NfcData2() {
    }

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

    public String getWs() {
        return ws;
    }

    public void setWs(String ws) {
        this.ws = ws;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NfcData2)) return false;
        NfcData2 nfcData = (NfcData2) o;
        return getUid() == nfcData.getUid() &&
                isSend() == nfcData.isSend() &&
                Objects.equals(getCreateDate(), nfcData.getCreateDate()) &&
                Objects.equals(getCalltype(), nfcData.getCalltype()) &&
                Objects.equals(getClientId(), nfcData.getClientId()) &&
                Objects.equals(getEmployeeId(), nfcData.getEmployeeId()) &&
                Objects.equals(getId(), nfcData.getId()) &&
                Objects.equals(getNfc(), nfcData.getNfc()) &&
                Objects.equals(getWs(), nfcData.getWs()) &&
                Objects.equals(getOfficeid(), nfcData.getOfficeid()) &&
                Objects.equals(getTasktype(), nfcData.getTasktype()) &&
                Objects.equals(getAppSender(), nfcData.getAppSender()) &&
                Objects.equals(getPhoneNumber(), nfcData.getPhoneNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid(), isSend(), getCreateDate(), getCalltype(), getClientId(), getEmployeeId(), getId(), getNfc(), getWs(), getOfficeid(), getTasktype(), getAppSender(), getPhoneNumber());
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
        dest.writeString(this.ws);
        dest.writeValue(this.officeid);
        dest.writeString(this.tasktype);
        dest.writeString(this.appSender);
        dest.writeString(this.phoneNumber);
    }

    protected NfcData2(Parcel in) {
        this.uid = in.readInt();
        this.isSend = in.readByte() != 0;
        this.createDate = in.readString();
        this.calltype = in.readString();
        this.clientId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.employeeId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.nfc = in.readString();
        this.ws = in.readString();
        this.officeid = (Integer) in.readValue(Integer.class.getClassLoader());
        this.tasktype = in.readString();
        this.appSender = in.readString();
        this.phoneNumber = in.readString();
    }

    public static final Creator<NfcData2> CREATOR = new Creator<NfcData2>() {
        @Override
        public NfcData2 createFromParcel(Parcel source) {
            return new NfcData2(source);
        }

        @Override
        public NfcData2[] newArray(int size) {
            return new NfcData2[size];
        }
    };
}
