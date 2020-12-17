package com.github.frorax.foraxproof.samples;

public class LineNumberFalsePositive {
  public static void main(String[] args) {
    System.out.println("line1");
    ((Runnable)() -> {
      System.out.println("line1");
      System.out.println("line2");
      System.out.println("line3");
      System.out.println("line4");
      System.out.println("line5");
      System.out.println("line6");
    }).run();
    System.out.println("line4");
  }
}
