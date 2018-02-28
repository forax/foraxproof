package com.github.frorax.foraxproof.samples;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class APIOrImpl4 {
  static class MyMap implements Map<Object, Object> {
    public void m() {
      // empty
    }
    
    @Override
    public void clear() {
      // empty
    }

    @Override
    public boolean containsKey(Object arg0) {
      return false;
    }

    @Override
    public boolean containsValue(Object arg0) {
      return false;
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
      return null;
    }

    @Override
    public Object get(Object arg0) {
      return null;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public Set<Object> keySet() {
      return null;
    }

    @Override
    public Object put(Object arg0, Object arg1) {
      return null;
    }

    @Override
    public void putAll(Map<? extends Object, ? extends Object> arg0) {
      // empty
    }

    @Override
    public Object remove(Object arg0) {
      return null;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public Collection<Object> values() {
      return null;
    }
  }
  
  public static MyMap test() {
    throw new UnsupportedOperationException();
  }
}
