package com.github.frorax.foraxproof.samples;

import java.util.AbstractCollection;
import java.util.Iterator;

public class APIOrImpl5 {
  static class MyCollection extends AbstractCollection<String> {
    public void m() {
      // empty
    }

    @Override
    public Iterator<String> iterator() {
      return null;
    }

    @Override
    public int size() {
      return 0;
    }
  }
  
  public void foo(MyCollection c) {
    throw new UnsupportedOperationException();
  }
}
