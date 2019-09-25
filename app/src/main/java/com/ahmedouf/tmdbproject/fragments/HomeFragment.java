package com.ahmedouf.tmdbproject.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmedouf.tmdbproject.R;
import com.ahmedouf.tmdbproject.activities.MainActivity;
import com.ahmedouf.tmdbproject.adapter.MoviesAdapter;
import com.ahmedouf.tmdbproject.callbacks.MovieFragmentData;
import com.ahmedouf.tmdbproject.dialogs.MonthYearPickerDialog;
import com.ahmedouf.tmdbproject.models.Movie;
import com.ahmedouf.tmdbproject.services.CheckConnectivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements MonthYearPickerDialog.OnDateSetListener {
    private RecyclerView moviesRecyclerView;
    private MoviesAdapter moviesOverviewAdapter;
    private boolean loadingPage;
    private CheckConnectivity checkConnectivity;
    private String date;
    private String category;
    private List<Movie.ResultsBean> moviesList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu, menu);
        inflater.inflate(R.menu.categories_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.dateFilter) {
            filterByDate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterByDate() {
        MonthYearPickerDialog pd = new MonthYearPickerDialog();
        pd.setListener(this);
        pd.show(getChildFragmentManager(), "MonthYearPickerDialog");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
        String date = dateFormat.format(c.getTime());
        filter(date);
        this.date = date;
    }

    @Override
    public void onClear() {
        filter(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        checkConnectivity = new CheckConnectivity();
        initializeRecyclerView(view);
        initializeAdapter();
    }

    private void initializeAdapter() {
        moviesOverviewAdapter = new MoviesAdapter();
        if (getActivity() instanceof MovieFragmentData) {
            moviesOverviewAdapter.setCallback(((MovieFragmentData<Movie.ResultsBean>) getActivity()));
        }
        moviesRecyclerView.setAdapter(moviesOverviewAdapter);
        moviesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    loadPage();
                }
            }
        });
    }

    private void initializeRecyclerView(View view) {
        moviesRecyclerView = view.findViewById(R.id.moviesOverviewRecyclerView);
        moviesRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), RecyclerView.VERTICAL));
        moviesRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), RecyclerView.HORIZONTAL));
    }

    public void addMovies(List<Movie.ResultsBean> movies, String category) {
        loadingPage = false;
        this.category = category;
        if (moviesOverviewAdapter != null) {
            if (!ObjectsCompat.equals(category, moviesOverviewAdapter.getCategory())) {
                moviesRecyclerView.smoothScrollToPosition(0);
            }
            moviesOverviewAdapter.addAll(movies, category);
        }
    }

    private void filter(String date) {
        if (moviesOverviewAdapter != null) {
            moviesOverviewAdapter.filter(date);
        }
    }

    private void loadPage() {
        if (loadingPage) return;
        loadingPage = true;
        ((MainActivity) getActivity()).loadPage();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("date", date);
        outState.putString("category", category);
        int position = moviesRecyclerView.getScrollY();
        outState.putInt("position", position);
        outState.putParcelableArrayList("moviesList", new ArrayList<>(moviesOverviewAdapter.getMoviesPassedFiltered()));

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            date = savedInstanceState.getString("date");
            this.category = savedInstanceState.getString("category");
            moviesOverviewAdapter.setCategory(category);
            moviesList = savedInstanceState.getParcelableArrayList("moviesList");
            filter(date);
            addMovies(moviesList, category);
            moviesRecyclerView.scrollTo(0, savedInstanceState.getInt("position"));
        } else {
            loadPage();
        }
    }
}
