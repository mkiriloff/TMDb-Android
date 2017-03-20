package com.kiriloff.max.loyaltyplant.Interfaces;

import com.kiriloff.max.loyaltyplant.model.FullMovie;
import com.kiriloff.max.loyaltyplant.model.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMoviesNextPage(@Query("api_key") String apiKey, @Query("page") int page);

    @GET("movie/{id}")
    Call<FullMovie> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);

}