package com.github.forax.foraxproof.analysis;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.forax.foraxproof.reflect.ClassFileLoader;

public interface Plugin {
  @Documented
  @Target({TYPE})
  @Retention(RUNTIME)
  public @interface PluginName {
    String value();
  }
  
  Analysis provide(ClassFileLoader loader);
  
  public static Map<String, Supplier<Plugin>> findPluginMap() {
    return ServiceLoader.load(Plugin.class).stream()
        .collect(Collectors.toMap(Plugin::pluginName, provider -> (Supplier<Plugin>)provider::get));
  }

  private static String pluginName(Provider<Plugin> provider) {
    return Optional.ofNullable(provider.type().getAnnotation(PluginName.class))
        .map(PluginName::value)
        .orElseThrow(() -> new IllegalStateException("plugin " + provider.type() + " doesn't define the annotation @PluginName"));
  }
}
