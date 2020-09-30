package com.sg.tmdbmovies.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.sg.tmdbmovies.model.Movie;
import com.sg.tmdbmovies.paginationlibrary.DataSource;
import com.sg.tmdbmovies.paginationlibrary.DataSourceFactory;
import com.sg.tmdbmovies.repository.Repository;
import com.sg.tmdbmovies.service.network.MovieDataService;
import com.sg.tmdbmovies.service.network.RetrofitInstance;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {
    private Repository repository;
    private LiveData<DataSource> dataSourceLiveData;
    private Executor executor;
    private LiveData<PagedList<Movie>> pagedListLiveData;

    @SuppressWarnings("unchecked")
    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
        MovieDataService movieDataService = RetrofitInstance.getService();
        DataSourceFactory dataSourceFactory = new DataSourceFactory(movieDataService, application);
        dataSourceLiveData = dataSourceFactory.getDataSourceMutableLiveData();
        PagedList.Config config = (new PagedList.Config.Builder())
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(10)
                .setPageSize(20)
                .setPrefetchDistance(4)
                .build();
        executor = Executors.newFixedThreadPool(5);
        pagedListLiveData = (new LivePagedListBuilder<Long, Movie>(dataSourceFactory, config)).setFetchExecutor(executor).build();
    }

    public Movie getMovie(String id) {
        return repository.getMovie(id);
    }

    public LiveData<List<Movie>> getAllMovies() {
        return repository.getAllFMovies();
    }

    public void AddMovie(Movie movie) {

        repository.AddMovie(movie);
    }

    public void DeleteMovie(Movie movie) {
        repository.DeleteMovie(movie);
    }

}
