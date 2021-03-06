package com.blockscore.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The quality of a match in a watchlist search or verification.
 */
public enum MatchRating {
  MATCH("match"),
  NO_MATCH("no_match"),
  PARTIAL_MATCH("partial_match"),
  MISMATCH("mismatch"),
  NO_DATA("insufficient_data");

  private final String value;

  MatchRating(@NotNull final String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  /**
   * Converts a string to a enum.
   *
   * @param value the value to convert.
   * @return the matched enum value
   */
  public static MatchRating toEnum(@Nullable final String value) {
    if (MATCH.toString().equalsIgnoreCase(value)) {
      return MATCH;
    } else if (PARTIAL_MATCH.toString().equalsIgnoreCase(value)) {
      return PARTIAL_MATCH;
    } else if (NO_MATCH.toString().equalsIgnoreCase(value)) {
      return NO_MATCH;
    } else if (MISMATCH.toString().equalsIgnoreCase(value)) {
      return MISMATCH;
    } else {
      return NO_DATA;
    }
  }
}
