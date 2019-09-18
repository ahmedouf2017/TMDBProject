package com.ahmedouf.tmdbproject.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ahmedouf.tmdbproject.models.Movie;
import com.ahmedouf.tmdbproject.R;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment {
    private TextView movieTitleMovieDetails, votingRateMovieDetails, releaseDataMovieDate, movieDetailsTextView;
    private ImageView imageMovieDetails, posterMovieDetails;
    String url = "https://image.tmdb.org/t/p/w500";
ProgressBar progressBar1,progressBar2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        movieTitleMovieDetails = view.findViewById(R.id.movieTitleMovieDetails);
        votingRateMovieDetails = view.findViewById(R.id.votingRateMovieDetails);
        releaseDataMovieDate = view.findViewById(R.id.releaseDataMovieDate);
        movieDetailsTextView = view.findViewById(R.id.movieDetailsTextView);
        imageMovieDetails = view.findViewById(R.id.imageMovieDetails);
        posterMovieDetails = view.findViewById(R.id.posterMovieDetails);
        progressBar1=view.findViewById(R.id.progressBar2);
        progressBar2=view.findViewById(R.id.progressBar3);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Movie.ResultsBean movieToDisplay = (Movie.ResultsBean) bundle.get("data");
            movieTitleMovieDetails.setText(movieToDisplay.getTitle());
            votingRateMovieDetails.setText(String.format("Rating: %s \u2605", movieToDisplay.getVote_average()));
            releaseDataMovieDate.setText(movieToDisplay.getRelease_date());
            movieDetailsTextView.setText(movieToDisplay.getOverview());
            if(movieToDisplay.getPoster_path()== null){
                progressBar1.setVisibility(view.VISIBLE);
            }else{
                Picasso.get().load(url + movieToDisplay.getPoster_path()).into(posterMovieDetails);
            }
            if(movieToDisplay.getBackdrop_path()== null){
                progressBar2.setVisibility(view.VISIBLE);
            }else{
                Picasso.get().load(url + movieToDisplay.getBackdrop_path()).into(imageMovieDetails);
            }
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

}
