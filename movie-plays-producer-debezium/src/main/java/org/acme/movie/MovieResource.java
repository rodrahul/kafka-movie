package org.acme.movie;

import java.util.List;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestResponse;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/movie")
public class MovieResource {

  @Inject
  MovieService movieService;

  @Inject
  Logger logger;

  @GET
  public RestResponse<List<Movie>> listMovie() {
    var movies = movieService.listMovies();
    return RestResponse.ok(movies);
  }

  @GET
  @Path("/{id}")
  public RestResponse<Movie> findMovie(@RestPath Long id) {
    var movie = movieService.findMovieById(id)
        .orElse(null);

    if (movie != null)
      return RestResponse.ok(movie);
    else
      return RestResponse.notFound();
  }

  // Http Post method to insert a movie
  @POST
  public Movie insert(Movie movie) {
    logger.info("New Movie inserted " + movie.getName());
    System.out.println(":)");

    return movieService.persistMovie(movie);
  }

}
