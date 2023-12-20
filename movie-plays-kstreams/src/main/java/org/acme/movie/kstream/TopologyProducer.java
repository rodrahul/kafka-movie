package org.acme.movie.kstream;

import org.acme.movie.Movie;
import org.acme.movie.MoviePlayCount;
import org.acme.movie.PlayedMovie;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class TopologyProducer {

  private static final String MOVIES_TOPIC = "movies";
  private static final String PLAY_MOVIES_TOPIC = "moviesplaytime";
  public static final String COUNT_MOVIE_STORE = "countMovieStore";
  KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(COUNT_MOVIE_STORE);

  @Produces
  public Topology getTopCharts() {

    final var builder = new StreamsBuilder();

    // SerDes for Movie and PlayedMovie
    final ObjectMapperSerde<Movie> movieSerde = new ObjectMapperSerde<>(Movie.class);
    final ObjectMapperSerde<PlayedMovie> playedMovieSerde = new ObjectMapperSerde<>(PlayedMovie.class);

    // Create a global Kafka table for Movies topic
    final GlobalKTable<Integer, Movie> moviesTable = builder.globalTable(MOVIES_TOPIC,
        Consumed.with(Serdes.Integer(), movieSerde));

    // Stream connected to moviesplaytime topic, every event produced is
    // consumed by this stream
    final KStream<String, PlayedMovie> playEvents = builder.stream(PLAY_MOVIES_TOPIC,
        Consumed.with(Serdes.String(), playedMovieSerde));

    /**
     * PlayedMovies has the region as key ("eu"), and the PlayedMovie as value.
     * Let’s map the content so the key is the movie id (to do the join) and leave
     * the object as value
     * 
     * Moreover, we do the join using the keys of the movies table (movieId) and the
     * keys of the stream (we changed it to be the movieId too in the map method).
     */

    /**
     * So far, the processing of events is stateless as the event was received,
     * processed, and sent to a sink processor (either to a topic or as a console
     * output), but to count the number of times a movie has been played, we need
     * some memory to remember how many times a movie has been played and increment
     * by one when it’s watched again by any user for more than 10 minutes. The
     * processing of the events needs to be done in a stateful way.
     */

    final ObjectMapperSerde<MoviePlayCount> moviePlayCountSerder = new ObjectMapperSerde<>(MoviePlayCount.class);

    System.out.println("TOPOLOGY");
    System.out.println(builder.build().describe());

    playEvents
        .filter((region, event) -> event.getDuration() >= 10)
        .map((key, value) -> KeyValue.pair(value.getId(), value))
        .join(moviesTable, (movieId, playedMovie) -> movieId, (playedMovie, movie) -> movie)
        .groupByKey(Grouped.with(Serdes.Integer(), movieSerde))
        .aggregate(MoviePlayCount::new,
            (movieId, movie, moviePlayCounter) -> moviePlayCounter.aggregate(movie.getName()),
            Materialized.<Integer, MoviePlayCount>as(storeSupplier)
                .withKeySerde(Serdes.Integer())
                .withValueSerde(moviePlayCountSerder))
        .toStream()
        .print(Printed.toSysOut());

    return builder.build();
  }

}
