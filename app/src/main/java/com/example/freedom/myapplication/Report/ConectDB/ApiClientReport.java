package com.example.freedom.myapplication.Report.ConectDB;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientReport {

    public static final String BASE_URL = "http://10.80.39.23:2560/";
    private static Retrofit retrofit = null;



    public static Retrofit getRetrofitReport() {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }
}
