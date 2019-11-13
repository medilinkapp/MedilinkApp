package com.jat.medilinkapp.model.databaseConf

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.jat.medilinkapp.model.entity.ClientGps
import io.reactivex.Observable

@Dao
interface ClientGpsDao {

    @get:Query("select * from ClientGps")
    val clientGpsList: Observable<List<ClientGps>>

    @Insert
    fun addData(clientGps: ClientGps): Long

    @Query("DELETE FROM ClientGps")
    fun deleteAll()

    @Delete
    fun delete(clientGps: ClientGps)

    @Query("SELECT * FROM ClientGps where client_id like :clientId")
    fun getObservableListByClientID(clientId: Int): Observable<List<ClientGps>>

    @Query("SELECT * FROM ClientGps where client_id like :clientId")
    fun getListByClientID(clientId: Int): List<ClientGps>

    @Query("SELECT * FROM ClientGps where latitude like :latitude")
    fun getListByLatitude(latitude: Long): Observable<List<ClientGps>>

}
