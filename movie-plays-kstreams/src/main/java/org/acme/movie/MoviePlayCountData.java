package org.acme.movie;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * MoviePlayCountData for storing the result of the query. We do it in this way
 * to decouple classes used in Kafka Streams from classes used in the rest of
 * the application.
 */
@Data
@AllArgsConstructor
public class MoviePlayCountData {
  private String name;
  private int count;

}
