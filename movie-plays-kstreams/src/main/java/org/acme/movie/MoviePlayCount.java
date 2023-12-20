package org.acme.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoviePlayCount {
  private String name;
  private int count;

  public MoviePlayCount aggregate(String name) {
    this.name = name;
    this.count++;
    return this;
  }
}
