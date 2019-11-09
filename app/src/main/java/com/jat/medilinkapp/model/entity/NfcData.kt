package com.jat.medilinkapp.model.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "NfcData")
data class NfcData  (

    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0,

    @ColumnInfo(name = "is_send")
    var isSend: Boolean = false,

    @ColumnInfo(name = "created_date")
    @SerializedName("created_date")
    @Expose
    var createDate: String? = null,

    @ColumnInfo(name = "calltype")
    @SerializedName("calltype")
    @Expose
    var calltype: String? = null,

    @ColumnInfo(name = "client_id")
    @SerializedName("client_id")
    @Expose
    var clientId: Int? = null,

    @ColumnInfo(name = "employee_id")
    @SerializedName("employee_id")
    @Expose
    var employeeId: Int? = null,

    @ColumnInfo(name = "id")
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @ColumnInfo(name = "nfc")
    @SerializedName("nfc")
    @Expose
    var nfc: String? = null,

    @ColumnInfo(name = "ws")
    var ws: String? = null,

    @ColumnInfo(name = "office_id")
    @SerializedName("office_id")
    @Expose
    var officeid: Int? = null,

    @ColumnInfo(name = "tasktype")
    @SerializedName("tasktype")
    @Expose
    var tasktype: String? = null,

    @ColumnInfo(name = "app_sender")
    @SerializedName("app_sender")
    @Expose
    var appSender: String? = null,

    @ColumnInfo(name = "phoneNumber")
    @SerializedName("phoneNumber")
    @Expose
    var phoneNumber: String? = null,

    @ColumnInfo(name = "gps")
    @SerializedName("gps")
    @Expose
    var gps: String? = null

) : Parcelable {
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<NfcData> = object : Parcelable.Creator<NfcData> {
            override fun createFromParcel(source: Parcel): NfcData = NfcData(source)
            override fun newArray(size: Int): Array<NfcData?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel): this(
    source.readInt(),
    1 == source.readInt(),
            source.readString(),
    source.readString(),
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readString(),
    source.readString(),
    source.readValue(Int::class.java.classLoader) as Int?,
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(uid)
        writeInt((if (isSend) 1 else 0))
        writeString(createDate)
        writeString(calltype)
        writeValue(clientId)
        writeValue(employeeId)
        writeValue(id)
        writeString(nfc)
        writeString(ws)
        writeValue(officeid)
        writeString(tasktype)
        writeString(appSender)
        writeString(phoneNumber)
        writeString(gps)
    }
}
