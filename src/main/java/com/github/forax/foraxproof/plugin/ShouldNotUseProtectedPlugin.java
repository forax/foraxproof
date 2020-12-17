package com.github.forax.foraxproof.plugin;

import static com.github.forax.foraxproof.AsmVersion.ASM_API;
import static com.github.forax.foraxproof.plugin.Utils.is;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import com.github.forax.foraxproof.analysis.Analysis;
import com.github.forax.foraxproof.analysis.Plugin;
import com.github.forax.foraxproof.analysis.Plugin.PluginName;
import com.github.forax.foraxproof.reflect.ClassFileLoader;

@PluginName("protected")
public class ShouldNotUseProtectedPlugin implements Plugin {
  @Override
  public Analysis provide(ClassFileLoader loader) {
    return (cv, context) -> {
      return new ClassVisitor(ASM_API, cv) {
        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
          if (is(access, ACC_PROTECTED)) {
            context.error("should.not.use.protected", null);
          }
          cv.visit(version, access, name, signature, superName, interfaces);
        }
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
          if (is(access, ACC_PROTECTED)) {
            context.error("should.not.use.protected", null);
          }
          return cv.visitMethod(access, name, desc, signature, exceptions);
        }
        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
          if (is(access, ACC_PROTECTED)) {
            context.error("should.not.use.protected", null);
          }
          return cv.visitField(access, name, desc, signature, value);
        }
      };
    };
  }
}
