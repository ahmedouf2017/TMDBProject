package com.ahmedouf.tmdbproject.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.ahmedouf.tmdbproject.models.Movie;
import com.ahmedouf.tmdbproject.dialogs.MonthYearPickerDialog;
import com.ahmedouf.tmdbproject.adapter.MoviesAdapter;
import com.ahmedouf.tmdbproject.R;
import com.ahmedouf.tmdbproject.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements MonthYearPickerDialog.OnDateSetListener {
    private RecyclerView moviesRecyclerView;
    private MoviesAdapter moviesOverviewAdapter;

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
    }

    @Override
    public void onClear() {
        filter(null);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeRecyclerView(view);
        initializeAdapter();
    }

    private void initializeAdapter() {
        moviesOverviewAdapter = new MoviesAdapter();
        Log.d("Adapter set", "Adapter set");
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

    public void addMovies(List<Movie.ResultsBean> movies) {
        if (moviesOverviewAdapter != null) {
            moviesOverviewAdapter.addAll(movies);
        }
    }

    private void filter(String date) {
        if (moviesOverviewAdapter != null) {
            moviesOverviewAdapter.filter(date);
        }
    }

    private void loadPage() {
        ((MainActivity) getActivity()).checkNetwork();
    }

}
