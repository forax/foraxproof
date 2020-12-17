package com.github.forax.foraxproof.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, METHOD})
@Retention(CLASS)
public @interface ForaxProof {
  /* marker interface */
}
