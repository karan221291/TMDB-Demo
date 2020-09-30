package com.sg.tmdbmovies.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.sg.tmdbmovies.BuildConfig;
import com.sg.tmdbmovies.R;
import com.sg.tmdbmovies.model.GenresList;
import com.sg.tmdbmovies.model.Movie;
import com.sg.tmdbmovies.service.FetchFirstTimeDataService;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;


public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    public static ArrayList<Movie> movieList = new ArrayList<>();
    private FragmentManager fragmentManager;
    public static int totalPages;
    public static int imageup = 0;
    public static int genreid;
    public static int selected;
    public static ArrayList<GenresList> genresLists;
    public static String queryM;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (BuildConfig.ApiKey.isEmpty()) {
            Toast.makeText(MainActivity.this, "Please get the API key first", Toast.LENGTH_SHORT).show();
        }
        fragmentManager = getSupportFragmentManager();
        progressBar = findViewById(R.id.progressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar.animate().alpha(1).setDuration(500);
        progressBar.setIndeterminate(true);
        FetchFirstTimeDataService fetchFirstTimeDataService = new FetchFirstTimeDataService(progressBar, compositeDisposable, fragmentManager);
        fetchFirstTimeDataService.getDataFirst(MainActivity.this);
    }


    @Override
    public void onBackPressed() {

        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 1) {
            finish();
        } else if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
