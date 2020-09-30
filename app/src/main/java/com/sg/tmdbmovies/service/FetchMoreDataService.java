package com.sg.tmdbmovies.service;

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sg.tmdbmovies.BuildConfig;
import com.sg.tmdbmovies.adapter.MoviesAdapter;
import com.sg.tmdbmovies.model.Discover;
import com.sg.tmdbmovies.model.DiscoverDBResponse;
import com.sg.tmdbmovies.model.Movie;
import com.sg.tmdbmovies.model.MovieDBResponse;
import com.sg.tmdbmovies.service.network.MovieDataService;
import com.sg.tmdbmovies.service.network.RetrofitInstance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class FetchMoreDataService {
    private Observable<MovieDBResponse> observableMovie;
    private Observable<DiscoverDBResponse> observableDB;
    private RecyclerView recyclerView;
    private ArrayList<Movie> movieList;
    private int totalPages;
    private int totalPagesGenre;
    private ArrayList<Discover> discovers = new ArrayList<>();
    private CompositeDisposable compositeDisposable;
    private MoviesAdapter moviesAdapter;
    private Context context;

    public FetchMoreDataService(RecyclerView recyclerView, ArrayList<Movie> movieList, CompositeDisposable compositeDisposable, Context context, MoviesAdapter moviesAdapter) {
        this.recyclerView = recyclerView;
        this.movieList = movieList;
        this.compositeDisposable = compositeDisposable;
        this.context = context;
        this.moviesAdapter = moviesAdapter;
    }

    public void loadMore(int pageNum, SwipeRefreshLayout swiper) {
        String startDate = "";
        Calendar calendar = Calendar.getInstance();
        //not adding 1 to start from previous month
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int date = 1;
        calendar.set(year, month - 1, date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        startDate = sdf.format(calendar.getTime());

        final MovieDataService movieDataService = RetrofitInstance.getService();
        String ApiKey = BuildConfig.ApiKey;
        observableDB = movieDataService.discover(ApiKey,
                startDate,
                false,
                pageNum,
                "primary_release_date.asc")
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
        compositeDisposable.add(observableDB.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<DiscoverDBResponse>() {
                    @Override
                    public void onNext(DiscoverDBResponse movieDBResponse) {
                        if (swiper != null && swiper.isRefreshing())
                            swiper.setRefreshing(false);
                        if (movieDBResponse != null && movieDBResponse.getResults() != null) {
                            if (pageNum == 1) {
                                movieList = (ArrayList<Movie>) movieDBResponse.getResults();
                                totalPages = movieDBResponse.getTotalPages();
                                recyclerView.setAdapter(moviesAdapter);
                            } else {
                                ArrayList<Movie> movies = (ArrayList<Movie>) movieDBResponse.getResults();
                                for (Movie movie : movies) {
                                    movieList.add(movie);
                                    moviesAdapter.notifyItemInserted(movieList.size() - 1);
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (swiper != null && swiper.isRefreshing())
                            swiper.setRefreshing(false);
                        Toast.makeText(context, "Error! " + e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }
}
