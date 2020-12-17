package com.github.forax.foraxproof.reflect;

import java.util.List;
import java.util.Optional;

public final class ArrayType implements Type {
  private final ClassFileLoader loader;
  private final Type componentType;
  
  ArrayType(ClassFileLoader loader, Type componentType) {
    this.loader = loader;
    this.componentType = componentType;
  }
  
  @Override
  public String name() {
    return "[]" + componentType.name();
  }
  
  public Type getComponentType() {
    return componentType;
  }
  
  @Override
  public Optional<Type> superType() {
    if (componentType instanceof PrimitiveType) {
      return Optional.of(loader.getClassFile("java/lang/Object"));
    }
    return Optional.of(Types.array(loader, loader.getClassFile("java/lang/Object"), 1));
  }
  @Override
  public List<ClassFile> interfaceTypes() {
    return List.of(loader.getClassFile("java/io/Serializable"), loader.getClassFile("java/lang/Clonable"));
  }
  
  @Override
  public String toString() {
    return name();
  }
}
