package com.github.frorax.foraxproof.samples;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class APIOrImpl3 {
  static class MyList implements Collection<Object> {
    public void m2() {
      
    }
    
    @Override
    public boolean add(Object arg0) {
      return false;
    }

    @Override
    public boolean addAll(Collection<? extends Object> arg0) {
      return false;
    }

    @Override
    public void clear() {
      
    }

    @Override
    public boolean contains(Object arg0) {
      return false;
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
      return false;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public Iterator<Object> iterator() {
      return null;
    }

    @Override
    public boolean remove(Object arg0) {
      return false;
    }

    @Override
    public boolean removeAll(Collection<?> arg0) {
      return false;
    }

    @Override
    public boolean retainAll(Collection<?> arg0) {
      return false;
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public Object[] toArray() {
      return null;
    }

    @Override
    public <T> T[] toArray(T[] arg0) {
      return null;
    }
  }
  
  static class MyMap implements Map<Object, Object> {
    public void m() {
      
    }
    
    @Override
    public void clear() {
      
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
  
  public static void test(MyList list) {
    throw new UnsupportedOperationException();
  }
}
