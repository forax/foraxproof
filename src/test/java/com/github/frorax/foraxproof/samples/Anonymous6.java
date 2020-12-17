package com.github.frorax.foraxproof.samples;

import java.util.Iterator;

public class Anonymous6 implements Iterator<String> {
  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public String next() {
    return null;
  }
  
  @Override
  public void remove() {
    Iterator.super.remove();
  }
}
