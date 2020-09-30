package com.sg.tmdbmovies.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sg.tmdbmovies.R;
import com.sg.tmdbmovies.adapter.MoviesAdapter;
import com.sg.tmdbmovies.databinding.FragmentMoviesBinding;
import com.sg.tmdbmovies.model.Movie;
import com.sg.tmdbmovies.service.FetchFirstTimeDataService;
import com.sg.tmdbmovies.service.FetchMoreDataService;
import com.sg.tmdbmovies.utils.PaginationScrollListener;
import com.sg.tmdbmovies.view.MainActivity;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Movies #newInstance} factory method to
 * create an instance of this fragment.
 */
public class Movies extends Fragment {
    private ArrayList<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FragmentMoviesBinding fragmentMoviesBinding;
    private int selectedItem = 0;
    private PaginationScrollListener paginationScrollListener;
    GridLayoutManager gridLayoutManager;
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FetchMoreDataService fetchMoreDataService;

    private FetchFirstTimeDataService firstTimeData;

    public Movies(FetchFirstTimeDataService fetchFirstTimeDataService) {
        firstTimeData = fetchFirstTimeDataService;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String[] a = new String[MainActivity.genresLists.size()];
        for (int i = 0; i < MainActivity.genresLists.size(); i++) {
            a[i] = MainActivity.genresLists.get(i).getName();
        }
        selectedItem = MainActivity.selected;
        new AlertDialog.Builder(getContext()).setSingleChoiceItems(a, selectedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firstTimeData.getFirstPageData(getContext());
                selectedItem = which;
                dialog.dismiss();
                for (int i = 0; i <= getActivity().getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }

            }
        }).show();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMoviesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_movies, container, false);
        return fragmentMoviesBinding.getRoot();
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = fragmentMoviesBinding.rv2;
        swipeRefreshLayout = fragmentMoviesBinding.swiperefresh2;
        getActivity().setTitle("Movies");
        context = getContext();
        movieList = MainActivity.movieList;

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.DKGRAY, Color.RED, Color.GREEN, Color.MAGENTA, Color.BLACK, Color.CYAN);
        moviesAdapter = new MoviesAdapter(context, movieList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        gridLayoutManager.scrollToPosition(0);
                        gridLayoutManager.scrollToPositionWithOffset(0, 0);
                    }
                }, 4000);
            }
        });
        gridLayoutManager = new GridLayoutManager(getContext(), 1);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (moviesAdapter.getItemViewType(position)) {
                    case 0:
                        return 1;
                    case 1:
                        return 2;
                    default:
                        return -1;
                }
            }
        });
        gridLayoutManager.scrollToPosition(0);
        gridLayoutManager.scrollToPositionWithOffset(0, 0);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(moviesAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        moviesAdapter.notifyDataSetChanged();
        fetchMoreDataService = new FetchMoreDataService(recyclerView, movieList, compositeDisposable, getActivity(), moviesAdapter);
        paginationScrollListener = new PaginationScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (page + 1 <= MainActivity.totalPages) {
                    fetchMoreDataService.loadMore(page + 1, swipeRefreshLayout);
                }
            }
        };
        recyclerView.addOnScrollListener(paginationScrollListener);
    }


    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}

