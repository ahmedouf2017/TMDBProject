package com.ahmedouf.tmdbproject.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ahmedouf.tmdbproject.callbacks.MovieFragmentData;
import com.ahmedouf.tmdbproject.fragments.MovieDetailsFragment;
import com.ahmedouf.tmdbproject.services.CheckConnectivity;
import com.ahmedouf.tmdbproject.api.Client;
import com.ahmedouf.tmdbproject.api.MoviesCallback;
import com.ahmedouf.tmdbproject.models.Movie;
import com.ahmedouf.tmdbproject.fragments.HomeFragment;
import com.ahmedouf.tmdbproject.api.GetMoviesApi;
import com.ahmedouf.tmdbproject.R;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieFragmentData<Movie.ResultsBean> {
    private GetMoviesApi getMoviesApi;
    private HomeFragment fragment;
    private MovieDetailsFragment movieDetailsFragment;
    private ProgressBar progressBar;
    private CheckConnectivity checkConnectivity;
    List<Movie.ResultsBean> moviesList;
    String category = "now_playing";

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progressBar);
        checkConnectivity = new CheckConnectivity();
        if (savedInstanceState == null) {
            addHomeFragment();
            loadPage();
        } else {
            fragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HomeFragment");
        }
    }


    private void showInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Please allow internet connection")
                .setMessage("You must be connected to an active internet connection to run this application")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        loadPage();

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void addHomeFragment() {
        fragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, fragment, "HomeFragment")
                .commit();
    }


    public void loadPage() {
        Client client = new Client();
        if (checkConnectivity.isNetworkConnected(this)) {
            progressBar.setVisibility(View.VISIBLE);
            client.fetchData(category, "18896291bbd95b82694de192576c852b", page, new MoviesCallback() {
                @Override
                public void onSuccess(List<Movie.ResultsBean> Movies, int totalPages) {
                    List<Movie.ResultsBean> moviesList = Movies;
                    if (moviesList != null) {
                        fragment.addMovies(moviesList, category);
                        page += 1;
                    }
                    if (page >= totalPages + 1) {
                        Toast.makeText(getApplicationContext(), "No more movies to load", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(String error) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Error !!!")
                            .setMessage("Couldn't load movies due to connection error ").
                            setNeutralButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    loadPage();
                                }
                            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });


        } else {
            showInternetDialog();
        }
    }


    @Override
    public void movieClicked(Movie.ResultsBean movie) {
        Log.d("Clicked on movie :", String.valueOf(movie));
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", movie);
        movieDetailsFragment = new MovieDetailsFragment();
        movieDetailsFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, movieDetailsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.categories_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.now_playing:
                category = "now_playing";
                page = 1;
                loadPage();
                getSupportActionBar().setTitle(R.string.now_playing);
                break;
            case R.id.popular:
                category = "popular";
                page = 1;
                loadPage();
                getSupportActionBar().setTitle(R.string.popular);
                break;
            case R.id.top_rated:
                category = "top_rated";
                page = 1;
                loadPage();
                getSupportActionBar().setTitle(R.string.top_rated);
                break;
            case R.id.upcoming:
                category = "upcoming";
                page = 1;
                loadPage();
                getSupportActionBar().setTitle(R.string.upcoming);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("page",page);
        outState.putString("category",category);
        outState.putString("title", (String) getSupportActionBar().getTitle());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        page=savedInstanceState.getInt("page");
        category=savedInstanceState.getString("category");
        getSupportActionBar().setTitle(savedInstanceState.getString("title"));
    }
}
