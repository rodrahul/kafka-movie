package org.acme.movie;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/movies")
public class PlayedMovieResource {
  @Inject
  Logger logger;

  @Channel("movies-played")
  Multi<PlayedMovie> playedMovies;

  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public Multi<PlayedMovie> stream() {
    return playedMovies;
  }

  @Incoming("movies")
  @RunOnVirtualThread
  public void newMovie(Movie movie) {
    logger.infov("New movie: {0}", movie);
  }

}
