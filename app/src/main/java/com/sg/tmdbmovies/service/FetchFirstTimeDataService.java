package com.sg.tmdbmovies.service;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sg.tmdbmovies.BuildConfig;
import com.sg.tmdbmovies.R;
import com.sg.tmdbmovies.fragments.Movies;
import com.sg.tmdbmovies.model.DiscoverDBResponse;
import com.sg.tmdbmovies.model.Movie;
import com.sg.tmdbmovies.model.MovieDBResponse;
import com.sg.tmdbmovies.service.network.MovieDataService;
import com.sg.tmdbmovies.service.network.RetrofitInstance;
import com.sg.tmdbmovies.view.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class FetchFirstTimeDataService {
    private ProgressBar progressBar;
    private Observable<MovieDBResponse> observableMovie;
    private Observable<DiscoverDBResponse> observableDB;
    private CompositeDisposable compositeDisposable;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    public FetchFirstTimeDataService(ProgressBar progressBar, CompositeDisposable compositeDisposable, FragmentManager fragmentManager) {
        this.progressBar = progressBar;
        this.compositeDisposable = compositeDisposable;
        this.fragmentManager = fragmentManager;
        this.fragmentTransaction = fragmentManager.beginTransaction();
    }

    public void getDataFirst(final Context context) {

        String startDate = "";
        Calendar calendar = Calendar.getInstance();
        //not adding 1 to start from previous month
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int date = 1;
        calendar.set(year, month-1, date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        startDate = sdf.format(calendar.getTime());

        final MovieDataService movieDataService = RetrofitInstance.getService();
        String ApiKey = BuildConfig.ApiKey;
        observableDB = movieDataService.discover(ApiKey,
                startDate,
                false,
                1,
                "primary_release_date.asc")
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        progressBar.setIndeterminate(false);
                    }
                });
        getFirstPageData(context);
    }

    public void getFirstPageData(final Context context) {
        compositeDisposable.add(
                observableDB.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<DiscoverDBResponse>() {
                            @Override
                            public void onNext(DiscoverDBResponse discoverDBResponse) {
                                if (discoverDBResponse != null && discoverDBResponse.getResults() != null) {
                                    MainActivity.movieList = (ArrayList<Movie>) discoverDBResponse.getResults();
                                    MainActivity.totalPages = discoverDBResponse.getTotalPages();
                                    if (progressBar != null) {
                                        progressBar.setIndeterminate(false);
                                    }
                                    if (fragmentManager.getFragments().isEmpty()) {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.add(R.id.frame_layout, new Movies(FetchFirstTimeDataService.this)).commitAllowingStateLoss();
                                    } else {
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.replace(R.id.frame_layout, new Movies(FetchFirstTimeDataService.this)).commitAllowingStateLoss();
                                    }
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "Error! " + e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                                progressBar.setIndeterminate(false);
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

}
