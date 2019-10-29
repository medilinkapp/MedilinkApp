package com.jat.medilinkapp.retro_conf;

import com.jat.medilinkapp.model.entity.NfcData;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface APIService {
    @POST("r/n")
    Observable<NfcData> sendPost(@Body NfcData post);
}
