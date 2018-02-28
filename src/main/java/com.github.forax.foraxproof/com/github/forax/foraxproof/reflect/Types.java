package com.github.forax.foraxproof.reflect;

import static java.util.Arrays.stream;

import java.util.List;

class Types {
  static List<ClassFile> getClassFilesOrEmpty(ClassFileLoader loader, String[] internalNames) {
    return internalNames == null? List.of(): List.of(stream(internalNames).map(loader::getClassFile).toArray(ClassFile[]::new));
  }
  
  static Type getTypeFromDescriptor(ClassFileLoader loader, String desc) {
    return asType(loader, org.objectweb.asm.Type.getType(desc));
  }
  
  static List<Type> getParameterTypesFromDescriptor(ClassFileLoader loader, String desc) {
    return List.of(stream(org.objectweb.asm.Type.getArgumentTypes(desc))
              .map(t -> asType(loader, t))
              .toArray(Type[]::new));
  }

  static Type getReturnTypeFromDescriptor(ClassFileLoader loader, String desc) {
    return asType(loader, org.objectweb.asm.Type.getReturnType(desc));
  }
  
  static Type array(ClassFileLoader loader, Type type, int dimension) {
    Type arrayType = type; 
    for(int i = 0; i < dimension; i++) {
      arrayType = new ArrayType(loader, arrayType);
    }
    return arrayType;
  }
  
  private static Type asType(ClassFileLoader loader, org.objectweb.asm.Type type) {
    switch(type.getSort()) {
    case org.objectweb.asm.Type.BOOLEAN:
      return PrimitiveType.BOOLEAN;
    case org.objectweb.asm.Type.BYTE:
      return PrimitiveType.BYTE;
    case org.objectweb.asm.Type.CHAR:
      return PrimitiveType.CHAR;
    case org.objectweb.asm.Type.SHORT:
      return PrimitiveType.SHORT;
    case org.objectweb.asm.Type.INT:
      return PrimitiveType.INT;
    case org.objectweb.asm.Type.LONG:
      return PrimitiveType.LONG;
    case org.objectweb.asm.Type.FLOAT:
      return PrimitiveType.FLOAT;
    case org.objectweb.asm.Type.DOUBLE:
      return PrimitiveType.DOUBLE;
    case org.objectweb.asm.Type.VOID:
      return PrimitiveType.VOID;
    case org.objectweb.asm.Type.OBJECT:
      return loader.getClassFile(type.getClassName());
    case org.objectweb.asm.Type.ARRAY:
      return array(loader, asType(loader, type.getElementType()), type.getDimensions());
    default:
      throw new AssertionError(type);
    }
  }
}
