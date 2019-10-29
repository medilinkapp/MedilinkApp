package com.jat.medilinkapp.retro_conf;

public class ApiUtils {
    private ApiUtils() {
    }

    //public static final String BASE_URL = "http://192.168.43.2:8080/medilinkWS/";
    public static final String BASE_URL = "http://%s/medilinkWS/";

    public static APIService getAPIService(String wsFromNfc) {
        //wsFromNfc = "216.114.104.227:21450";
        //wsFromNfc = "192.168.43.2:8080";

        String baseURL = String.format(BASE_URL, wsFromNfc);
        return RetrofitClient.getClient(baseURL).create(APIService.class);
    }
}
