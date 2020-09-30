package com.sg.tmdbmovies.repository;

import android.os.AsyncTask;

import com.sg.tmdbmovies.db.FavouriteMoviesDAO;
import com.sg.tmdbmovies.model.Movie;

public class DeleteFMovie extends AsyncTask<Movie, Void, Void> {
    private FavouriteMoviesDAO favouriteMoviesDAO;

    public DeleteFMovie(FavouriteMoviesDAO favouriteMoviesDAO) {
        this.favouriteMoviesDAO = favouriteMoviesDAO;
    }

    @Override
    protected Void doInBackground(Movie... movies) {
        favouriteMoviesDAO.deleteFMovie(movies[0]);
        return null;
    }
}
