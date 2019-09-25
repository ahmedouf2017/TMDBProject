package com.ahmedouf.tmdbproject.api;

import com.ahmedouf.tmdbproject.models.Movie;

import java.util.List;

public interface MoviesCallback {
    void onSuccess(List<Movie.ResultsBean> Movies , int totalPages);
    void onFailure(String error);
}
