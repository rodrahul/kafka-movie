package org.acme.movie;

import java.util.List;
import java.util.Optional;

import io.debezium.outbox.quarkus.ExportedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MovieService {

  @Inject
  MovieRepository movieRepo;

  // CDI event interface triggering Outbox entities
  @Inject
  Event<ExportedEvent<?, ?>> event;

  public List<Movie> listMovies() {
    return movieRepo.listAll();
  }

  public Optional<Movie> findMovieById(Long id) {
    return movieRepo.findByIdOptional(id);
  }

  @Transactional
  public Movie persistMovie(Movie movie) {
    movieRepo.persist(movie);

    // Persist outbox event
    event.fire(new MovieEvent(movie));
    return movie;
  }

}
