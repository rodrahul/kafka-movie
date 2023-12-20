package org.acme.movie;

import java.util.Optional;

import org.acme.movie.kstream.InteractiveQueries;
import org.jboss.resteasy.reactive.RestPath;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("movie")
public class MovieCountResource {

  @Inject
  InteractiveQueries interactiveQueries;

  @GET
  @Path("/data/{id}")
  public Response movieCountData(@RestPath int id) {
    Optional<MoviePlayCountData> moviePlayCountData = interactiveQueries.getMoviePlayCountData(id);

    // Depending on the result returns the value or a 404
    if (moviePlayCountData.isPresent()) {
      return Response.ok(moviePlayCountData.get()).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND.getStatusCode(),
          "No data found for movie " + id).build();
    }
  }

}
