package com.jat.medilinkapp.model

import android.app.Application
import android.content.Context
import com.jat.medilinkapp.model.databaseConf.MyDataBase
import com.jat.medilinkapp.model.entity.ClientGps
import io.reactivex.Observable

class ClientGpsDataRepository {

    private var appDatabase: MyDataBase

    val list: Observable<List<ClientGps>>
        get() = appDatabase.clientGpsDao().clientGpsList

    constructor(application: Application) {
        appDatabase = MyDataBase.getDatabase(application)!!
    }

    constructor(application: Context) {
        appDatabase = MyDataBase.getDatabase(application)!!
    }

    fun addData(modelClass: ClientGps): Long? {
        return appDatabase.clientGpsDao().addData(modelClass)
    }

    fun deleteAll() {
        appDatabase.clientGpsDao().deleteAll()
    }

    fun delete(clientGps: ClientGps) {
        appDatabase.clientGpsDao().delete(clientGps)
    }

    fun getListByLatitude(latitude: Long): Observable<List<ClientGps>> {
        return appDatabase.clientGpsDao().getListByLatitude(latitude)
    }

    fun getListByClientID(clientId: Int): Observable<List<ClientGps>> {
        return appDatabase.clientGpsDao().getListByClientID(clientId)
    }


}
