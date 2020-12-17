package com.github.frorax.foraxproof.samples;

public class InstanceofFalsePositive {
  @Override
  public boolean equals(Object o) {
    return o instanceof String;
  }
}
