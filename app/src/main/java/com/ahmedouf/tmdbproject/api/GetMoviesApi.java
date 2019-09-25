package com.ahmedouf.tmdbproject.api;

import com.ahmedouf.tmdbproject.models.Movie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetMoviesApi {
    @GET("3/movie/{category}")
    Call<Movie> getMovies(
            @Path("category") String category,
            @Query("api_key") String api_key,
            @Query("page") int page);
}
