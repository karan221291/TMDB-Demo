package com.sg.tmdbmovies.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.sg.tmdbmovies.db.Database;
import com.sg.tmdbmovies.db.FavouriteMoviesDAO;
import com.sg.tmdbmovies.model.Movie;

import java.util.List;

public class Repository {
    private FavouriteMoviesDAO favouriteMoviesDAO;

    public Repository(Application application) {
        Database database = Database.getInstance(application);
        favouriteMoviesDAO = database.getFDAO();
    }

    public LiveData<List<Movie>> getAllFMovies() {
        return favouriteMoviesDAO.getAllFMovies();
    }

    public Movie getMovie(String id) {
        return favouriteMoviesDAO.getMovie(id);
    }

    public void AddMovie(Movie movie) {

        new AddFMovie(favouriteMoviesDAO).execute(movie);
    }

    public void DeleteMovie(Movie movie) {
        new DeleteFMovie(favouriteMoviesDAO).execute(movie);
    }

}
