package org.acme.movie.kstream;

import static org.apache.kafka.streams.StoreQueryParameters.fromNameAndType;

import java.util.Optional;

import org.acme.movie.MoviePlayCount;
import org.acme.movie.MoviePlayCountData;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class InteractiveQueries {

  @Inject
  KafkaStreams streams;

  public Optional<MoviePlayCountData> getMoviePlayCountData(int id) {
    // gets the state store and get the movie count by movie id
    MoviePlayCount moviePlayCount = getMoviesPlayCount().get(id);
    // If there is a result
    if (moviePlayCount != null) {
      // Wrap the result into MoviePlayCountData
      return Optional.of(new MoviePlayCountData(moviePlayCount.getName(), moviePlayCount.getCount()));
    } else {
      return Optional.empty();
    }
  }

  // Gets the state store
  private ReadOnlyKeyValueStore<Integer, MoviePlayCount> getMoviesPlayCount() {
    while (true) {
      try {
        return streams.store(fromNameAndType(TopologyProducer.COUNT_MOVIE_STORE, QueryableStoreTypes.keyValueStore()));
      } catch (InvalidStateStoreException e) {
        // ignore, store not ready yet
      }
    }
  }

}