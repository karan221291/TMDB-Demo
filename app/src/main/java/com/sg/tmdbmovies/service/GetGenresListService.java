package com.sg.tmdbmovies.service;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sg.tmdbmovies.BuildConfig;
import com.sg.tmdbmovies.model.DiscoverDBResponse;
import com.sg.tmdbmovies.model.GenresList;
import com.sg.tmdbmovies.model.GenresListDBResponse;
import com.sg.tmdbmovies.model.MovieDBResponse;
import com.sg.tmdbmovies.service.network.MovieDataService;
import com.sg.tmdbmovies.service.network.RetrofitInstance;
import com.sg.tmdbmovies.view.MainActivity;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class GetGenresListService {
    private Observable<MovieDBResponse> observableMovie;
    private Observable<DiscoverDBResponse> observableDB;
    private Observable<GenresListDBResponse> genresListObservable;
    private Context context;
    private ProgressBar progressBar;
    private CompositeDisposable compositeDisposable;
    private FetchFirstTimeDataService fetchFirstTimeDataService;

    public GetGenresListService(Context context, CompositeDisposable compositeDisposable, FetchFirstTimeDataService fetchFirstTimeDataService, ProgressBar progressBar) {
        this.context = context;
        this.compositeDisposable = compositeDisposable;
        this.fetchFirstTimeDataService = fetchFirstTimeDataService;
        this.progressBar = progressBar;
    }

    public void getGenresList() {
        final MovieDataService movieDataService = RetrofitInstance.getService();
        String ApiKey = BuildConfig.ApiKey;
        genresListObservable = movieDataService.getGenresList(ApiKey);
        compositeDisposable.add(
                genresListObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<GenresListDBResponse>() {
                            @Override
                            public void onNext(GenresListDBResponse genresListDBResponse) {
                                if (genresListDBResponse != null && genresListDBResponse.getGenresLists() != null) {
                                    MainActivity.genresLists = (ArrayList<GenresList>) genresListDBResponse.getGenresLists();
                                    String[] a = new String[MainActivity.genresLists.size()];
                                    for (int i = 0; i < MainActivity.genresLists.size(); i++) {
                                        a[i] = MainActivity.genresLists.get(i).getName();
                                    }
                                    new MaterialDialog.Builder(context).title("Choose a Category").items(a).itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                            MainActivity.selected = which;
                                            MainActivity.genreid = MainActivity.genresLists.get(which).getId();
                                            fetchFirstTimeDataService.getFirstPageData(context);
                                            return true;
                                        }
                                    }).canceledOnTouchOutside(false).cancelable(false).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setIndeterminate(false);
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

}
