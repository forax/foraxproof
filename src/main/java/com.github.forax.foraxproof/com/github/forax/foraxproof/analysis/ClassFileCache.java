package com.github.forax.foraxproof.analysis;

import java.util.HashMap;
import java.util.function.Function;

import com.github.forax.foraxproof.reflect.ClassFile;

public interface ClassFileCache<T> {
  public T computeIfAbsent(ClassFile classFile, Function<? super ClassFile, ? extends T> computation);
  
  public static <T> ClassFileCache<T> create() {
    return new HashMap<ClassFile, T>()::computeIfAbsent;
  }
}
