package org.acme.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Movie {
  int id;
  String name;
  String director;
  String genre;

}
