package com.github.forax.foraxproof.plugin;

import static com.github.forax.foraxproof.AsmVersion.ASM_API;
import static com.github.forax.foraxproof.plugin.Utils.is;
import static com.github.forax.foraxproof.plugin.Utils.isNot;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.github.forax.foraxproof.analysis.Analysis;
import com.github.forax.foraxproof.analysis.ClassFileCache;
import com.github.forax.foraxproof.analysis.Plugin;
import com.github.forax.foraxproof.analysis.Plugin.PluginName;
import com.github.forax.foraxproof.reflect.ClassFile;
import com.github.forax.foraxproof.reflect.ClassFileLoader;
import com.github.forax.foraxproof.reflect.Method;
import com.github.forax.foraxproof.reflect.Type;

@PluginName("method-collection-impl")
public class PublicMethodSignatureShouldNotUseCollectionImplementationPlugin implements Plugin {
  @Override
  public Analysis provide(ClassFileLoader loader) {
    return (cv, context) -> {
      ClassFileCache<Boolean> isACollection = ClassFileCache.create();  
      return new ClassVisitor(ASM_API, cv) {
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
          if (is(access, ACC_PUBLIC)) {
            ClassFile classFile = loader.getClassFile(context.className());
            Method method = classFile.method(name, desc).get();
            check(method.returnType());
            method.parameterTypes().forEach(type -> check(type));
          }
          return cv.visitMethod(access, name, desc, signature, exceptions);
        }
        
        private void check(Type type) {
          type.asClassFile()
              .filter(classFile -> isNot(classFile.access(), ACC_INTERFACE))
              .filter(classFile -> isCollectionSubType(isACollection, classFile))
              .ifPresent(classFile -> context.error("public.method.signature.should.not.use.collection.implementation",
                                                    classFile.name()));
        }
      };
    };
  }
  
  static boolean isCollectionSubType(ClassFileCache<Boolean> isACollection, ClassFile classFile) {
    return isACollection.computeIfAbsent(classFile, type -> {
      switch(type.name()) {
      case "java/util/Collection":
      case "java/util/Map":
        return true;
      case "java/lang/Object":
        return false;
      default:
      }
      if (type.interfaceTypes().stream().anyMatch(t -> isCollectionSubType(isACollection, t))) {
        return true;
      }
      return type.superType().map(t -> isCollectionSubType(isACollection, t)).orElse(false);
    });
  }
}
