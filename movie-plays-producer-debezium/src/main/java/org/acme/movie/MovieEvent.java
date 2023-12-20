package org.acme.movie;

import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.debezium.outbox.quarkus.ExportedEvent;

public class MovieEvent implements ExportedEvent<String, JsonNode> {

  // Set the type enclosed inside the event
  private static final String TYPE = "Movie";
  // Set the event type
  private static final String EVENT_TYPE = "MovieCreated";

  private static ObjectMapper mapper = new ObjectMapper();

  private final long gameId;
  private final JsonNode jsonNode;
  private final Instant timestamp;

  public MovieEvent(Movie movie) {
    this.gameId = movie.getId();
    this.timestamp = Instant.now();
    this.jsonNode = convertToJson(movie);
  }

  private JsonNode convertToJson(Movie movie) {
    JsonNode movieJson = mapper.createObjectNode()
        .put("id", movie.getId())
        .put("name", movie.getName())
        .put("director", movie.getDirector())
        .put("genre", movie.getGenre());

    return movieJson;
  }

  @Override
  public String getAggregateId() {
    return String.valueOf(this.gameId);
  }

  @Override
  public String getAggregateType() {
    return TYPE;
  }

  @Override
  public JsonNode getPayload() {
    return jsonNode;
  }

  @Override
  public Instant getTimestamp() {
    return timestamp;
  }

  @Override
  public String getType() {
    return EVENT_TYPE;
  }

}
