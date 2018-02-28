package com.github.frorax.foraxproof.samples;

import java.util.Iterator;

public class AnonymousFalsePositive {
  public static Iterable<String> m() {
    return new Iterable<>() {
      @Override
      public Iterator<String> iterator() {
        throw new UnsupportedOperationException();
      }
    };
  }
}
