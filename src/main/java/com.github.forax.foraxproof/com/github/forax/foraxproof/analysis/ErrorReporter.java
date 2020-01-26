package com.github.forax.foraxproof.analysis;

import java.io.PrintStream;
import java.util.Map;
import java.util.Objects;

public interface ErrorReporter {
  default void enterClass(@SuppressWarnings("unused") Context context) { /* empty */ }
  default void enterMethod(@SuppressWarnings("unused") Context context)  { /* empty */ }
  default void enterField(@SuppressWarnings("unused") Context context)  { /* empty */ }
  
  void error(Context context, String type, String info);

  default void exitField()  { /* empty */ }
  default void exitMethod()  { /* empty */ }
  default void exitClass()  { /* empty */ }
  
  public static ErrorReporter xml(PrintStream printer) {
    Objects.requireNonNull(printer);
    ThreadLocal<StringBuilder> builders = ThreadLocal.withInitial(StringBuilder::new);
    return new ErrorReporter() {
      @Override
      public void enterClass(Context context) {
        builders.get().append("<class name=\"" + context.className() + "\"" + ((context.sourceFile() != null)?" source=\"" + context.sourceFile() + "\"":"") + ">\n");
      }
      @Override
      public void enterMethod(Context context) {
        builders.get().append("  <method name=\"" + context.memberName() + "\" desc=\"" + context.memberDescriptor() + "\">\n");
      }
      @Override
      public void enterField(Context context) {
        builders.get().append("  <field name=\"" + context.memberName() + "\" desc=\"" + context.memberDescriptor() + "\">\n");
      }
      
      @Override
      public void error(Context context, String type, String info) {
        builders.get().append("    <error type=\"" + type +"\" info=\"" + info + "\" loc=\"" + context.line() + "\"\n");
      }

      @Override
      public void exitField() { builders.get().append("  </field>\n"); }
      @Override
      public void exitMethod() { builders.get().append("  </method>\n"); }
      @Override
      public void exitClass() {
        StringBuilder builder = builders.get().append("</class>\n");
        printer.print(builder.toString());
        builder.setLength(0);
      }
    };
  }
  
  public static ErrorReporter log(PrintStream printer) {
    Objects.requireNonNull(printer);
    return (context, type, info) -> {
        printer.println(context.className() + ' ' + context.sourceFile() + ' ' + context.member() + ' ' + context.memberName() + context.memberDescriptor() + ' ' + type + ' ' + info + ' ' + context.line());
    };
  }
  
  public static ErrorReporter stats(Map<String, Long> stats) {
    Objects.requireNonNull(stats);
    return (context, type, info) -> stats.merge(type, 1L, Long::sum);
  }
}
