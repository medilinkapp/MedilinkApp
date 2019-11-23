package com.jat.medilinkapp.model.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity(tableName = "ClientGps")
data class ClientGps(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "uid")
        var uid: Int = 0,

        @ColumnInfo(name = "client_id")
        var clientId: Int? = null,

        @ColumnInfo(name = "latitude")
        var latitude: Double? = null,

        @ColumnInfo(name = "longitude")
        var longitude: Double? = null) : Parcelable {

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ClientGps> = object : Parcelable.Creator<ClientGps> {
            override fun createFromParcel(source: Parcel): ClientGps = ClientGps(source)
            override fun newArray(size: Int): Array<ClientGps?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readInt(),
            source.readValue(Int::class.java.classLoader) as Int?,
            source.readValue(Int::class.java.classLoader) as Double?,
            source.readValue(Int::class.java.classLoader) as Double?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(uid)
        writeValue(clientId)
        writeValue(latitude)
        writeValue(longitude)
    }
}