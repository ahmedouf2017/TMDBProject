package com.ahmedouf.tmdbproject.api;

import android.util.Log;
import com.ahmedouf.tmdbproject.models.Movie;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private String BASE_URL = "https://api.themoviedb.org/";

    public void fetchData(String category, String API_KEY, int page, final MoviesCallback callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetMoviesApi getMoviesApi = retrofit.create(GetMoviesApi.class);
        Call<Movie> call = getMoviesApi.getMovies(category, API_KEY, page);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (!response.isSuccessful()) {
                    Log.d("Error", String.valueOf(response.body()));
                    return;
                }
                Movie movie = response.body();
                List<Movie.ResultsBean> moviesList = null;
                int totalPages = movie.getTotal_pages();
                if (movie != null) {
                    moviesList = movie.getResults();
                }
                callback.onSuccess(moviesList,totalPages);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d("Error", "Error");
                callback.onFailure(t.getLocalizedMessage());
            }
        });
    }
}
