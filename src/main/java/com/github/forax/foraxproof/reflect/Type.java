package com.github.forax.foraxproof.reflect;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface Type {
  public String name();
  
  public default Optional<ClassFile> asClassFile() {
    return Optional.empty();
  }
  
  public default Optional<? extends Type> superType() {
    return Optional.empty();
  }
  public default List<ClassFile> interfaceTypes() {
    return List.of();
  }
  
  public default Collection<Field> fields() {
    return List.of();
  }
  public default Optional<Field> field(@SuppressWarnings("unused") String name) {
    return Optional.empty();
  }
  public default Collection<Method> methods() {
    return List.of();
  }
  public default Optional<Method> method(@SuppressWarnings("unused") String name, @SuppressWarnings("unused") String descriptor) {
    return Optional.empty();
  }
}
