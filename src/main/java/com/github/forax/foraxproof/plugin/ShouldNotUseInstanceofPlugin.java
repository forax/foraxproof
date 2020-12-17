package com.github.forax.foraxproof.plugin;

import static com.github.forax.foraxproof.AsmVersion.ASM_API;
import static com.github.forax.foraxproof.plugin.Utils.is;
import static com.github.forax.foraxproof.plugin.Utils.isNot;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.INSTANCEOF;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.github.forax.foraxproof.analysis.Analysis;
import com.github.forax.foraxproof.analysis.Plugin;
import com.github.forax.foraxproof.analysis.Plugin.PluginName;
import com.github.forax.foraxproof.reflect.ClassFileLoader;

@PluginName("instanceof")
public class ShouldNotUseInstanceofPlugin implements Plugin {
  @Override
  public Analysis provide(ClassFileLoader loader) {
    return (cv, context) -> {
      return new ClassVisitor(ASM_API, cv) {
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
          MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
          if (is(access, ACC_PUBLIC) && isNot(access, ACC_STATIC) && "equals".equals(name) && "(Ljava/lang/Object;)Z".equals(desc)) {
            return mv;
          }
          return new MethodVisitor(ASM_API, mv) {
            @Override
            public void visitTypeInsn(int opcode, String type) {
              if (opcode == INSTANCEOF) {
                context.error("instanceof.ouside.equals", null);
              }
              if (mv != null) {
                mv.visitTypeInsn(opcode, type);
              }
            }
          };
        }
      };
    };
  }
}
