package com.github.forax.foraxproof.analysis;

import java.util.Objects;

import org.objectweb.asm.ClassVisitor;

public interface Analysis {
  ClassVisitor analyze(ClassVisitor cv, Context context);
  
  public default Analysis combine(Analysis analysis) {
    Objects.requireNonNull(analysis);
    return (cv, context) -> analyze(analysis.analyze(cv, context), context);
  }
  
  public static Analysis empty() {
    return (cv, __) -> cv;
  }
}
