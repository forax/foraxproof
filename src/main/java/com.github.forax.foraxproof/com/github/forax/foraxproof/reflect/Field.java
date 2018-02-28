package com.github.forax.foraxproof.reflect;

public final class Field {
  private final ClassFileLoader loader;
  private final int access;
  private final String name;
  private final String desc;
  private final boolean marked;
  
  private /*lazy*/ Type type;
  
  Field(ClassFileLoader loader, int access, String name, String desc, boolean marked) {
    this.loader = loader;
    this.access = access;
    this.name = name;
    this.desc = desc;
    this.marked = marked;
  }

  public int access() {
    return access;
  }
  public String name() {
    return name;
  }
  public String descriptor() {
    return desc;
  }
  public boolean marked() {
    return marked;
  }
  
  public Type type() {
    if (type != null) {
      return type;
    }
    return type = Types.getTypeFromDescriptor(loader, desc);
  }
  
  @Override
  public String toString() {
    return name + desc;
  }
}
