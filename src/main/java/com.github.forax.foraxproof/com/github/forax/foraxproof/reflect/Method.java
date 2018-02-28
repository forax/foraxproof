package com.github.forax.foraxproof.reflect;

import java.util.List;

public class Method {
  private final ClassFileLoader loader;
  private final int access;
  private final String name;
  private final String desc;
  private final String signature;
  private final String[] exceptions;
  private final boolean marked;
  
  private /*lazy*/ Type returnType;
  private /*lazy*/ List<Type> parameterTypes;
  private /*lazy*/ List<ClassFile> exceptionTypes;
  
  Method(ClassFileLoader loader, int access, String name, String desc, String signature, String[] exceptions, boolean marked) {
    this.loader = loader;
    this.access = access;
    this.name = name;
    this.desc = desc;
    this.signature = signature;
    this.exceptions = exceptions;
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
  public String signature() {
    return signature;
  }
  public boolean marked() {
    return marked;
  }
  
  public Type returnType() {
    if (returnType != null) {
      return returnType;
    }
    return returnType = Types.getReturnTypeFromDescriptor(loader, desc);
  }
  public List<Type> parameterTypes() {
    if (parameterTypes != null) {
      return parameterTypes;
    }
    return parameterTypes = Types.getParameterTypesFromDescriptor(loader, desc);
  }
  public List<ClassFile> exceptionTypes() {
    if (exceptionTypes != null) {
      return exceptionTypes;
    }
    return exceptionTypes = Types.getClassFilesOrEmpty(loader, exceptions);
  }
  
  @Override
  public String toString() {
    return name + desc;
  }
}
