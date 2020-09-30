package com.sg.tmdbmovies.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.sg.tmdbmovies.BuildConfig;
import com.sg.tmdbmovies.R;
import com.sg.tmdbmovies.adapter.CastsAdapter;
import com.sg.tmdbmovies.databinding.ActivityMoviesInfoBinding;
import com.sg.tmdbmovies.model.Cast;
import com.sg.tmdbmovies.model.CastsList;
import com.sg.tmdbmovies.model.Movie;
import com.sg.tmdbmovies.service.network.MovieDataService;
import com.sg.tmdbmovies.service.network.RetrofitInstance;
import com.sg.tmdbmovies.viewmodel.MainViewModel;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MoviesInfo extends AppCompatActivity {
    private Movie movie;
    private Boolean bool;
    private ActivityMoviesInfoBinding activityMoviesInfoBinding;
    private MainViewModel mainViewModel;
    private Observable<CastsList> castsList;
    private final MovieDataService movieDataService = RetrofitInstance.getService();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String ApiKey = BuildConfig.ApiKey;
    private CastsList casts = new CastsList();
    private RecyclerView recyclerViewCasts;
    private View parentlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.heart_primary_color));
        parentlayout = findViewById(android.R.id.content);
        mainViewModel = new ViewModelProvider(MoviesInfo.this).get(MainViewModel.class);
        activityMoviesInfoBinding = DataBindingUtil.setContentView(MoviesInfo.this, R.layout.activity_movies_info);
        casts.setCast(new ArrayList<Cast>());
        recyclerViewCasts = activityMoviesInfoBinding.secondaryLayout.rvCasts;
        recyclerViewCasts.setLayoutManager(new LinearLayoutManager(MoviesInfo.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCasts.setItemAnimator(new DefaultItemAnimator());
        Intent i = getIntent();
        if (i.hasExtra("movie")) {
            movie = i.getParcelableExtra("movie");
            bool = i.getBooleanExtra("boolean", false);
            if (MainActivity.imageup <= 2) {
                MainActivity.imageup++;
            }
            if (mainViewModel.getMovie(movie.getTitle()) != null) {
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(true);
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setActiveImage(R.drawable.ic_heart_on);
            } else {
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(false);
                activityMoviesInfoBinding.secondaryLayout.sparkButton.setInactiveImage(R.drawable.ic_heart_off);
            }
            activityMoviesInfoBinding.setMovie(movie);
            activityMoviesInfoBinding.secondaryLayout.setLocale(new Locale(movie.getOriginalLanguage()).getDisplayLanguage(Locale.ENGLISH));
        }
        getParcelableData();
        getCasts();

        activityMoviesInfoBinding.secondaryLayout.sparkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((SparkButton) v).isChecked()) {
                    mainViewModel.DeleteMovie(movie);
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.playAnimation();
                    Snackbar.make(v, "Unmarked as Favourite", Snackbar.LENGTH_SHORT).show();
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setInactiveImage(R.drawable.ic_heart_off);
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(false);
                } else {
                    ArrayList<Cast> arrCasts = new ArrayList<Cast>(casts.getCast());
                    movie.setCastsList(arrCasts);
                    mainViewModel.AddMovie(movie);
                    Snackbar.make(v, "Marked as Favourite", Snackbar.LENGTH_SHORT).show();
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.playAnimation();
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setInactiveImage(R.drawable.ic_heart_on);
                    activityMoviesInfoBinding.secondaryLayout.sparkButton.setChecked(true);
                }
            }
        });

        activityMoviesInfoBinding.fabBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(MoviesInfo.this, TicketBookingActivity.class);
            startActivity(intent);
        });
    }


    public void getParcelableData() {
        Intent i = getIntent();
        if (i.hasExtra("movie")) {
            movie = i.getParcelableExtra("movie");
            if (movie.getCastsList() != null) {
                casts.setCast(movie.getCastsList());
            }
            recyclerViewCasts.setAdapter(new CastsAdapter(MoviesInfo.this, casts));
        }
    }

    public void getCasts() {
        castsList = movieDataService.getCasts(movie.getId(), ApiKey).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                getParcelableData();
            }
        });
        compositeDisposable.add(castsList.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(
                new DisposableObserver<CastsList>() {
                    @Override
                    public void onNext(CastsList castsList) {
                        if (castsList != null && castsList.getCast() != null) {
                            casts = castsList;
                            recyclerViewCasts.setAdapter(new CastsAdapter(MoviesInfo.this, casts));
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                }
        ));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
