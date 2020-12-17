package com.github.forax.foraxproof.main;

import java.io.IOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.forax.foraxproof.analysis.Analyzer;
import com.github.forax.foraxproof.analysis.ErrorReporter;
import com.github.forax.foraxproof.analysis.Plugin;

public class Main {
  public static void main(String[] args) throws IOException {
    Map<String, Supplier<Plugin>> knownPluginMap = Plugin.findPluginMap();
    ArrayList<Plugin> plugins = new ArrayList<>();
    HashSet<ModuleReference> modules = new HashSet<>();
    for(String arg: args) {
      if (arg.startsWith("-")) {
        String pluginName = arg.substring(1);
        Plugin plugin = Optional.ofNullable(knownPluginMap.get(pluginName))
            .map(Supplier::get)
            .orElseThrow(() -> new IllegalArgumentException("unknown plugin '" + pluginName + "', known plugins " + knownPluginMap.keySet()));
        plugins.add(plugin);
      } else {
        ModuleFinder finder = ModuleFinder.of(Paths.get(arg));
        modules.add(finder.findAll().stream().findFirst().orElseThrow(() -> new IllegalArgumentException("invalid module " + arg)));
      }
    }
    if (modules.isEmpty()) { // no module specified, use jdk ones
      modules.addAll(ModuleFinder.ofSystem().findAll());
    }
    if (plugins.isEmpty()) { // no plugins specified, use all of them
      knownPluginMap.values().stream().map(Supplier::get).forEach(plugins::add);
    }
    
    HashMap<String, Long> stats = new HashMap<>();
    ErrorReporter reporter = ErrorReporter.stats(stats);
    Analyzer.run(modules, plugins, reporter);
    System.out.println("stats " + stats);
  }
}
