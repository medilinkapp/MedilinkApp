package com.jat.medilinkapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jat.medilinkapp.model.ClientGpsDataRepository
import com.jat.medilinkapp.model.entity.ClientGps
import io.reactivex.Observable

class ClientGpsViewModal(application: Application) : AndroidViewModel(application) {

    var clientGpsDataRepository = ClientGpsDataRepository(application)

    val list: Observable<List<ClientGps>>
        get() = clientGpsDataRepository.list

    fun getObservableListByClientID(clientId: Int): Observable<List<ClientGps>> {
        return clientGpsDataRepository.getObservableListByClientID(clientId)
    }

    fun getListByClientID(clientId: Int): List<ClientGps> {
        return clientGpsDataRepository.getListByClientID(clientId)
    }

    fun getByClientID(clientId: Int): ClientGps {
        return clientGpsDataRepository.getByClientID(clientId)
    }

    fun getListByLatitude(latitude: Long): Observable<List<ClientGps>> {
        return clientGpsDataRepository.getListByLatitude(latitude)
    }

    fun addData(modelClass: ClientGps): Long? {
        return clientGpsDataRepository.addData(modelClass)
    }

    fun deleteAll() {
        clientGpsDataRepository.deleteAll()
    }

    fun delete(clientGps: ClientGps) {
        clientGpsDataRepository.delete(clientGps)
    }
}