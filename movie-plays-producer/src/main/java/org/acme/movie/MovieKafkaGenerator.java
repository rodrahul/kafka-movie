package org.acme.movie;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MovieKafkaGenerator {

  @Inject
  Logger logger;

  @Channel("movies-play-time")
  Emitter<Record<String, PlayedMovie>> moviePlayTimeEmitter;

  private List<Movie> movies = List.of(
      new Movie(1, "The Hobbit", "Peter Jackson", "Fantasy"),
      new Movie(2, "Star Trek: First Contact", "Jonathan Frakes", "Space"),
      new Movie(3, "Encanto", "Jared Bush", "Animation"),
      new Movie(4, "Cruella", "Craig Gillespie", "Crime Comedy"),
      new Movie(5, "Sing 2", "Garth Jennings", "Jukebox Musical Comedy"));

  // Populates movies into Kafka topic
  @Outgoing("movies")
  public Multi<Record<Integer, Movie>> movies() {
    return Multi.createFrom().items(movies.stream()
        .map(m -> Record.of(m.id, m)));
  }

  private Random random = new Random();

  // @Outgoing("movies-play-time")
  @Scheduled(every = "1s")
  @RunOnVirtualThread
  public void generate() {
    Movie movie = movies.get(random.nextInt(movies.size()));
    int time = random.nextInt(300);
    logger.infof("movie %s played for %d minutes", movie.name, time);
    // Region as key
    var message = Message.of(Record.of("eu", new PlayedMovie(movie.id, time)));
    moviePlayTimeEmitter.send(message);

  }
  
  /**
   * This is the original code, i've tried to implement using VT above
   * @return
   */
  // @Outgoing("movies-play-time")
  public Multi<Record<String, PlayedMovie>> generateRx() {
  return Multi.createFrom().ticks().every(Duration.ofMillis(1000))
  .onOverflow().drop()
  .map(tick -> {
  Movie movie = movies.get(random.nextInt(movies.size()));
  int time = random.nextInt(300);
  logger.infof("movie %s played for %d minutes", movie.name, time);
  // Region as key
  return Record.of("eu", new PlayedMovie(movie.id, time));
  });
  }
}