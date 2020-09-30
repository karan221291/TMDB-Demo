package com.sg.tmdbmovies.service.network;

import com.sg.tmdbmovies.model.CastsList;
import com.sg.tmdbmovies.model.DiscoverDBResponse;
import com.sg.tmdbmovies.model.GenresListDBResponse;
import com.sg.tmdbmovies.model.ReviewsList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDataService {

    @GET("genre/movie/list")
    Observable<GenresListDBResponse> getGenresList(@Query("api_key") String apiKey);

    @GET("movie/{movieId}/credits")
    Observable<CastsList> getCasts(@Path("movieId") Integer movieId, @Query("api_key") String apiKey);

    @GET("discover/movie")
    Observable<DiscoverDBResponse> discover(@Query("api_key") String apiKey, @Query("primary_release_date.gte") String startDate, @Query("include_adult") Boolean adult, @Query("page") int pageIndex, @Query("sort_by") String sortBy);
}
