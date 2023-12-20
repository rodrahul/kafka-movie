package org.acme.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PlayedMovie {
  private int id;
  private long duration;
}
