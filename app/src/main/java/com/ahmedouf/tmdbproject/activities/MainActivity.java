package com.ahmedouf.tmdbproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.ahmedouf.tmdbproject.models.Movie;
import com.ahmedouf.tmdbproject.fragments.HomeFragment;
import com.ahmedouf.tmdbproject.api.GetMoviesApi;
import com.ahmedouf.tmdbproject.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
     GetMoviesApi getMoviesApi;
     HomeFragment fragment;
     ProgressBar progressBar;

    public  int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addHomeFragment();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progressBar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        getMoviesApi = retrofit.create(GetMoviesApi.class);
        checkNetwork();
    }

    public void checkNetwork() {
        if (isNetworkConnected()) {
            loadPage();
        } else {
            showInternetDialog();
        }
    }

    private void showInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Please allow internet connection")
                .setMessage("You must be connected to an active internet connection to run this application")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkNetwork();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void addHomeFragment() {
        fragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }


    private void loadPage() {
        progressBar.setVisibility(View.VISIBLE);
        Call<Movie> call = getMoviesApi.getMovies("now_playing", "18896291bbd95b82694de192576c852b", page);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {

                if (!response.isSuccessful()) {
                    Log.d("Error", String.valueOf(response.body()));
                    return;
                }

                Movie movie = response.body();
                List<Movie.ResultsBean> moviesList = null;
                if (movie != null) {
                    moviesList = movie.getResults();
                }
                fragment.addMovies(moviesList);
                page +=1;
                Log.d("Page : ", String.valueOf(page));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable t) {
                Log.d("Error", "Error");
            }
        });
    }


}
