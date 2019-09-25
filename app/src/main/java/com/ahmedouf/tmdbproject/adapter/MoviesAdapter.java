package com.ahmedouf.tmdbproject.adapter;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmedouf.tmdbproject.callbacks.MovieFragmentData;
import com.ahmedouf.tmdbproject.models.Movie;
import com.ahmedouf.tmdbproject.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyOwnHolder> {

    private List<Movie.ResultsBean> moviesPassed = new ArrayList<>();
    private List<Movie.ResultsBean> moviesPassedFiltered = new ArrayList<>();
    private String filterDate;
    MovieFragmentData<Movie.ResultsBean> callback;
    private String category = "now_playing";

    @NonNull
    @Override
    public MyOwnHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater myInflater = LayoutInflater.from(parent.getContext());
        View myOwnView = myInflater.inflate(R.layout.movies_overview, parent, false);
        return new MyOwnHolder(myOwnView);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.MyOwnHolder holder, final int position)  {
        holder.movieTitleOverview.setText(moviesPassedFiltered.get(position).getOriginal_title());
        holder.movieTitleOverview.setSelected(true);
        holder.movieRatingOverview.setText(String.format("Rating: %s \u2605", moviesPassedFiltered.get(position).getVote_average()));
        holder.movieReleaseDateOverview.setText(moviesPassedFiltered.get(position).getRelease_date());
        String url = "https://image.tmdb.org/t/p/w500";
        Picasso.get().load(url + moviesPassedFiltered.get(position).getPoster_path()).into(holder.moviePoster);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.movieClicked(moviesPassedFiltered.get(position));
                }
            }
        });
    }

    public void setCallback(MovieFragmentData<Movie.ResultsBean> callback) {
        this.callback = callback;
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
//        fragmentCommunication.respond(moviesPassedFiltered);
        Log.d("MoviesPassedFilter:", String.valueOf(moviesPassed.size()));
        Log.d("MoviesPassedFilteredF:", String.valueOf(moviesPassedFiltered.size()));
        notifyDataSetChanged();

    }


    public List<Movie.ResultsBean> getMoviesPassedFiltered() {
        return moviesPassedFiltered;
    }

    public void addAll(List<Movie.ResultsBean> movies, String category) {
        if (category.equals(this.category)) {
            moviesPassed.addAll(movies);
            filter(filterDate);
        } else {
            int prevSize = moviesPassed.size();
            moviesPassed.clear();
            this.category = category;
            moviesPassed.addAll(movies);
            moviesPassedFiltered.clear();
            moviesPassedFiltered.addAll(movies);
            notifyItemRangeChanged(0, movies.size());
            if (movies.size() < prevSize) {
                notifyItemRangeRemoved(movies.size(), prevSize - movies.size());
            } else {
                notifyItemRangeInserted(movies.size(), movies.size() - prevSize);
            }
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category=category;
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