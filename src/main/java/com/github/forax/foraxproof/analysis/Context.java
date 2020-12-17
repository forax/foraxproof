package com.github.forax.foraxproof.analysis;

public interface Context {
  public enum Member { CLASS, FIELD, METHOD }
  
  public String className();
  public String sourceFile();
  public Member member();
  public String memberName();
  public String memberDescriptor();
  public int line();
  
  void error(String type, String message);
  
  //public void addPostProcessing(Runnable postProcessing);
}
