package com.github.forax.foraxproof.reflect;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ClassFileLoader {
  @FunctionalInterface
  public interface ClassFileFinder {
    public Optional<InputStream> find(String internalTypeName);
  }

  final ClassFileFinder finder;
  private final ConcurrentHashMap<String, ClassFile> classFileMap = new ConcurrentHashMap<>();

  private ClassFileLoader(ClassFileFinder finder) {
    this.finder = Objects.requireNonNull(finder);
  }

  public static ClassFileLoader create(ClassFileFinder finder) {
    Objects.requireNonNull(finder);
    return new ClassFileLoader(finder);
  }

  public ClassFile getClassFile(String internalTypeName) {
    Objects.requireNonNull(internalTypeName);
    ClassFile classFile = classFileMap.get(internalTypeName);
    if (classFile != null) {
      return classFile;
    }
    classFile = new ClassFile(this, internalTypeName);
    ClassFile otherClassFile = classFileMap.putIfAbsent(internalTypeName, classFile);
    return (otherClassFile == null)? classFile: otherClassFile;
  }
}
