package com.github.frorax.foraxproof.samples;

import java.util.Iterator;

public class Anonymous2 implements Iterator<Object> {
  @Override
  public boolean equals(Object arg0) {
    return super.equals(arg0);
  }
  
  @Override
  public int hashCode() {
    return super.hashCode();
  }
  
  @Override
  public boolean hasNext() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object next() {
    throw new UnsupportedOperationException();
  }
}
