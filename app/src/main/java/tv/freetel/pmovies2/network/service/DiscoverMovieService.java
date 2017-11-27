package tv.freetel.pmovies2.network.service;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tv.freetel.pmovies2.network.model.MovieInfo;
import tv.freetel.pmovies2.network.model.ReviewInfo;
import tv.freetel.pmovies2.network.model.TrailerInfo;

/**
 * Defines the REST API for Retrofit to access the movie DB API.
 *
 */
public interface DiscoverMovieService {
    /**adds the sharedpreference value from fetchmovies method to the endpoint.
     * Whereas getMovies is declared in the fetchmovies method in the MoviesFragment Grid.
     */

    @GET("/3/movie/{Sort_By}")
    Call<MovieInfo> getMovies(@Path ("Sort_By") String sortBy, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/reviews")
    public Call<ReviewInfo> getReviews(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("/3/movie/{id}/videos")
    public Call<TrailerInfo> getTrailers(@Path("id") int movieId, @Query("api_key") String apiKey);
}
