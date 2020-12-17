package com.github.forax.foraxproof.plugin;

class Utils {
  static boolean is(int access, int flags) {
    return (access & flags) != 0;
  }
  static boolean isNot(int access, int flags) {
    return (access & flags) == 0;
  }
}
