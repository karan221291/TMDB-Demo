package com.sg.tmdbmovies.repository;

import android.os.AsyncTask;

import com.sg.tmdbmovies.db.FavouriteMoviesDAO;
import com.sg.tmdbmovies.model.Movie;

public class AddFMovie extends AsyncTask<Movie, Void, Void> {
    private FavouriteMoviesDAO favouriteMoviesDAO;

    public AddFMovie(FavouriteMoviesDAO favouriteMoviesDAO) {
        this.favouriteMoviesDAO = favouriteMoviesDAO;
    }

    @Override
    protected Void doInBackground(Movie... movies) {
        favouriteMoviesDAO.insertFMovie(movies[0]);
        return null;
    }
}
