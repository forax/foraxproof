package com.github.forax.foraxproof.plugin;

import static com.github.forax.foraxproof.plugin.Utils.is;
import static com.github.forax.foraxproof.plugin.Utils.isNot;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ASM6;

import org.objectweb.asm.ClassVisitor;

import com.github.forax.foraxproof.analysis.Analysis;
import com.github.forax.foraxproof.analysis.Plugin;
import com.github.forax.foraxproof.analysis.Plugin.PluginName;
import com.github.forax.foraxproof.reflect.ClassFileLoader;

@PluginName("abstract-class")
public class AbstractClassShouldNoBePublicPlugin implements Plugin {
  @Override
  public Analysis provide(ClassFileLoader loader) {
    return (cv, context) -> {
      return new ClassVisitor(ASM6, cv) {
        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
          if (is(access, ACC_ABSTRACT) && is(access, ACC_PUBLIC) && isNot(access, ACC_INTERFACE)) {
            context.error("abstract.class.should.not.be.public", null);
          }
          cv.visit(version, access, name, signature, superName, interfaces);
        }
      };
    };
  }
}
