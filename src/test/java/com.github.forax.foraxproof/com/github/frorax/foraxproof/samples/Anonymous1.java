package com.github.frorax.foraxproof.samples;

import java.util.Iterator;

public class Anonymous1 implements Iterator<Object> {
  @Override
  public boolean hasNext() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object next() {
    throw new UnsupportedOperationException();
  }
}
