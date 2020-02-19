package com.example.lookweather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface Getdataservice {

    @GET("{geoID}")
    Call<Weather> getWeather(@Path("geoID") int geoId);

}
