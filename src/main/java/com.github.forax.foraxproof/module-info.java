module com.github.forax.foraxproof {
  requires org.objectweb.asm;
  
  uses com.github.forax.foraxproof.analysis.Plugin;
  provides com.github.forax.foraxproof.analysis.Plugin with
    com.github.forax.foraxproof.plugin.ShouldNotUseProtectedPlugin,
    com.github.forax.foraxproof.plugin.ShouldNotUseInstanceofPlugin,
    com.github.forax.foraxproof.plugin.AbstractClassShouldNoBePublicPlugin,
    com.github.forax.foraxproof.plugin.PublicMethodSignatureShouldNotUseCollectionImplementationPlugin;
}