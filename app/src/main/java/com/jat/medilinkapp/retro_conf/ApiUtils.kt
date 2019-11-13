package com.jat.medilinkapp.retro_conf

object ApiUtils {
    val BASE_URL = "http://%s/medilinkWS/"

    val apiService: APIService
        get() {
            //val wsAddress = "216.114.104.227:21450"
            val wsAddress ="Medi-EVV.shepconnect.net:21450"
            //val wsAddress = "192.168.43.2:8080";
            val baseURL = String.format(BASE_URL, wsAddress)
            return RetrofitClient.getClient(baseURL)!!.create(APIService::class.java)
        }
}
