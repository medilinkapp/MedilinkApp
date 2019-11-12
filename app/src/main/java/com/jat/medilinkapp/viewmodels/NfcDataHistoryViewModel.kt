package com.jat.medilinkapp.viewmodels

import android.app.Application

import com.jat.medilinkapp.model.NfcDataRepository
import com.jat.medilinkapp.model.entity.NfcData
import androidx.lifecycle.AndroidViewModel
import io.reactivex.Observable


class NfcDataHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private var nfcDataRepository = NfcDataRepository(application)

    val list: Observable<List<NfcData>>
        get() = nfcDataRepository.list

    fun getListBySubCreateData(subDate: String): Observable<List<NfcData>> {
        return nfcDataRepository.getListBySubCreateData(subDate)
    }

    fun getListIsSend(send: Boolean): Observable<List<NfcData>> {
        return nfcDataRepository.getListIsSend(send)
    }

    fun addData(modelClass: NfcData): Long? {
        return nfcDataRepository.addData(modelClass)
    }

    fun deleteAll() {
        nfcDataRepository.deleteAll()
    }

    fun delete(nfcData: NfcData) {
        nfcDataRepository.delete(nfcData)
    }
}
