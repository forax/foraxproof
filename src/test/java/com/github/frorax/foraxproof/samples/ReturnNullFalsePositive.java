package com.github.frorax.foraxproof.samples;

public class ReturnNullFalsePositive {
  public String m(int v) {
    String s = null;
    if (v == 0) {
      s = "foo";
    } else {
      s = "bar";
    }
    return s;
  }
}
