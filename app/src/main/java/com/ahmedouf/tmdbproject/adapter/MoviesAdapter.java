package com.ahmedouf.tmdbproject.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmedouf.tmdbproject.fragments.HomeFragment;
import com.ahmedouf.tmdbproject.models.Movie;
import com.ahmedouf.tmdbproject.R;
import com.ahmedouf.tmdbproject.fragments.MovieDetailsFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyOwnHolder> {

    private List<Movie.ResultsBean> moviesPassed = new ArrayList<>();
    private List<Movie.ResultsBean> moviesPassedFiltered = new ArrayList<>();
    private String filterDate;

    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater myInflater = LayoutInflater.from(parent.getContext());
        View myOwnView = myInflater.inflate(R.layout.movies_overview, parent, false);
        return new MyOwnHolder(myOwnView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.MyOwnHolder holder, final int position) {
        holder.movieTitleOverview.setText(moviesPassedFiltered.get(position).getOriginal_title());
        holder.movieTitleOverview.setSelected(true);
        holder.movieRatingOverview.setText(String.format("Rating: %s \u2605", moviesPassedFiltered.get(position).getVote_average()));
        holder.movieReleaseDateOverview.setText(moviesPassedFiltered.get(position).getRelease_date());
        String url = "https://image.tmdb.org/t/p/w500";
        Picasso.get().load(url + moviesPassedFiltered.get(position).getPoster_path()).into(holder.moviePoster);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMovieFragment(view, position);
            }
        });
    }

    private void openMovieFragment(View view, int position) {
        Log.d("Clicked on movie :", String.valueOf(moviesPassedFiltered.get(position)));
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", moviesPassedFiltered.get(position));
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        MovieDetailsFragment myFragment = new MovieDetailsFragment();
        myFragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().add(R.id.container, myFragment).addToBackStack(null).commit();
    }

    @Override
    public int getItemCount() {
        return moviesPassedFiltered.size();
    }

    public void filter(@Nullable String date) {
        filterDate = date;
        moviesPassedFiltered.clear();
        if (TextUtils.isEmpty(date)) {
            moviesPassedFiltered.addAll(moviesPassed);
        } else {
            for ( Movie.ResultsBean movie : moviesPassed ) {
                if (!moviesPassedFiltered.contains(movie)) {
                    if (movie.getRelease_date().contains(date)) {
                        moviesPassedFiltered.add(movie);
                    }
                }
            }
        }
        Log.d("MoviesPassedFilter:", String.valueOf(moviesPassed.size()));

        Log.d("MoviesPassedFilteredF:", String.valueOf(moviesPassedFiltered.size()));

        notifyDataSetChanged();
    }


    public void addAll(List<Movie.ResultsBean> movies) {
        moviesPassed.addAll(movies);
        Log.d("MoviesPassed:", String.valueOf(moviesPassed.size()));
        if(movies==null){
            Log.d("ALERT!!", "No more movies to display");
        }
        filter(filterDate);
    }

    class MyOwnHolder extends RecyclerView.ViewHolder {
        TextView movieTitleOverview, movieReleaseDateOverview, movieRatingOverview;
        ImageView moviePoster;

        MyOwnHolder(@NonNull View itemView) {
            super(itemView);
            movieTitleOverview = itemView.findViewById(R.id.moiveTitleOverview);
            movieReleaseDateOverview = itemView.findViewById(R.id.moiveReleaseDateOverview);
            movieRatingOverview = itemView.findViewById(R.id.movieRatingOverview);
            moviePoster = itemView.findViewById(R.id.moviePoster);
        }
    }

}