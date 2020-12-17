package com.github.forax.foraxproof.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Consumer;

import org.objectweb.asm.ClassReader;

import com.github.forax.foraxproof.reflect.ClassFileLoader;

public class Analyzer {
  private static void parseModule(ModuleReader moduleReader, Consumer<String> consumer) throws IOException {
      moduleReader.list()
        .filter(name -> name.endsWith(".class"))
        .forEach(consumer);
  }

  private static void parseClass(FrontClassVisitor frontClassVisitor, ModuleReader moduleReader, String name) {
    try {
      moduleReader.open(name).ifPresent(input -> {
        ClassReader reader;
        try(input) {
          reader = new ClassReader(input);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
        reader.accept(frontClassVisitor, 0);
      });
    } catch(IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  private static void serialRun(Set<ModuleReference> modules, ClassFileLoader loader, Analysis analysis, ErrorReporter reporter) throws IOException {
    FrontClassVisitor frontClassVisitor = FrontClassVisitor.create(analysis, loader, reporter);
    for(ModuleReference module: modules) {
      try(ModuleReader moduleReader = module.open()) {
        parseModule(moduleReader, name -> parseClass(frontClassVisitor, moduleReader, name));
      } catch(UncheckedIOException e) {
        throw e.getCause();
      }
    }
  }
  
  private static final Consumer<FrontClassVisitor> POISON = __ -> { /*empty*/ };
  
  private static void parallelRun(Set<ModuleReference> modules, ClassFileLoader loader, Analysis analysis, ErrorReporter reporter, int tasks) throws IOException {
    ArrayBlockingQueue<Consumer<FrontClassVisitor>> queue = new ArrayBlockingQueue<>(8192);
    
    Runnable runnable = () -> {
      FrontClassVisitor frontClassVisitor = FrontClassVisitor.create(analysis, loader, reporter);
      Consumer<FrontClassVisitor> consumer;
      try {
        while((consumer = queue.take()) != POISON) {
          consumer.accept(frontClassVisitor);
        }
        queue.put(POISON);  // re-inject the poison for the other tasks
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    };
    Thread[] threads = new Thread[tasks];
    for(int i = 0; i < tasks; i++) {
      Thread thread = new Thread(runnable);
      threads[i] = thread;
      thread.start();
    }
    
    ArrayList<ModuleReader> moduleReaders = new ArrayList<>();
    try {
      for(ModuleReference module: modules) {
        ModuleReader moduleReader = module.open();
        parseModule(moduleReader, name -> {
          try {
            queue.put(cv -> parseClass(cv, moduleReader, name));
          } catch (InterruptedException e) {
            throw new AssertionError(e);
          }
        });
        moduleReaders.add(moduleReader);
      }
      queue.put(POISON); // signal the end
      runnable.run();    // help the tasks to empty the queue

      for(Thread thread: threads) {
        thread.join();
      }
      
      for(ModuleReader reader: moduleReaders) {
        reader.close();
      }
    } catch (InterruptedException e) {
      throw new AssertionError(e);
    }
  }
  
  private static Optional<InputStream> load(ModuleReference module, String internalName) {
    try {
      return module.open().open(internalName + ".class");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  public static void run(Set<ModuleReference> modules, List<Plugin> plugins, ErrorReporter reporter) throws IOException {
    ClassFileLoader loader = ClassFileLoader.create(
        internalName -> modules.stream()
                                  .flatMap(module -> load(module, internalName).stream())
                                  .findFirst());
    
    Analysis analysis = plugins.stream()
        .map(plugin -> plugin.provide(loader))
        .reduce(Analysis.empty(), Analysis::combine);
    
    int availableProcessors = Runtime.getRuntime().availableProcessors();
    if (availableProcessors == 1) {
      serialRun(modules, loader, analysis, reporter);
    } else {
      parallelRun(modules, loader, analysis, reporter, availableProcessors - 1);
    }
  }
}
