import static com.github.forax.pro.Pro.*;
import static com.github.forax.pro.builder.Builders.*;

resolver.
    dependencies(list(
        "org.objectweb.asm=org.ow2.asm:asm:9.0"
    ))

packager.
    moduleMetadata(list(
        "com.github.forax.foraxproof@1.0/com.github.forax.foraxproof.main.Main"
    ))

run(resolver, compiler, docer, packager, runner)

/exit