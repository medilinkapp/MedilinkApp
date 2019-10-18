package com.jat.medilinkapp.conf;

public class ApiUtils {
    private ApiUtils() {
    }

    //public static final String BASE_URL = "http://192.168.43.2:8080/medilinkWS/";
    public static final String BASE_URL = "http://216.114.104.227:15802/medilinkWS/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
