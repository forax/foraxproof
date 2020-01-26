package com.github.forax.foraxproof.reflect;

import static com.github.forax.foraxproof.AsmVersion.ASM_API;
import static java.util.Collections.unmodifiableMap;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V9;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import com.github.forax.foraxproof.annotation.ForaxProof;

public final class ClassFile implements Type {
  static final String FORAX_PROOF_DESC = 'L' + ForaxProof.class.getName() + ';';
  
  final ClassFileLoader loader;
  final String name;
  volatile Info info;
  
  static class Info {
    static final Info EMPTY = new Info(null, V9, ACC_PUBLIC, null, null, Map.of(), Map.of());
    
    final ClassFileLoader loader;
    final int version;
    final int access;
    final String superName;
    final String[] interfaces;
    
    /*lazy*/ ClassFile superType;
    /*lazy*/ List<ClassFile> interfaceTypes;
    
    final Map<String, Field> fields;
    final Map<String, Method> methods;
    
    Info(ClassFileLoader loader,
        int version, int access, String superName, String[] interfaces,
        Map<String, Field> fields, Map<String, Method> methods) {
      this.loader = loader;
      
      this.version = version;
      this.access = access;
      this.superName = superName;
      this.interfaces = interfaces;
      
      this.fields = fields;
      this.methods = methods;
    }
    
    ClassFile superType() {
      if (superType != null) {
        return superType;
      }
      return superType = superName == null? null: loader.getClassFile(superName);
    }
    List<ClassFile> interfaceTypes() {
      if (interfaceTypes != null) {
        return interfaceTypes;
      }
      return interfaceTypes = Types.getClassFilesOrEmpty(loader, interfaces);
    }
  }
  
  ClassFile(ClassFileLoader loader, String name) {
    this.loader = loader;
    this.name = name;
  }
  
  private Info getInfo() {
    Info info = this.info;   // volatile read
    if (info != null) {
      return info;
    }
    return this.info = createInfo(loader, this);  // volatile write
  }
  
  private static Info createInfo(ClassFileLoader loader, ClassFile classFile) {
    return loader.finder.find(classFile.name).map(input -> createInfo(input, loader)).orElse(Info.EMPTY);
  }
  
  private static Info createInfo(InputStream resource, ClassFileLoader loader) {
    try(InputStream input = resource) {
      ClassReader reader = new ClassReader(input);
      return new ClassVisitor(ASM_API) {
        private Info info;
        final HashMap<String, Field> fields = new HashMap<>();
        final HashMap<String, Method> methods = new HashMap<>();
        
        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
          info = new Info(loader, version, access, superName, interfaces,
              unmodifiableMap(fields),
              unmodifiableMap(methods));
        }
        
        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
          return new FieldVisitor(ASM_API) {
            private boolean marked;
            
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
              if (FORAX_PROOF_DESC.equals(desc)) {
                marked = true;
              }
              return null;
            }
            @Override
            public void visitEnd() {
              Field field = new Field(loader, access, name, desc, marked);
              fields.put(name, field);
            }
          };
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
          return new MethodVisitor(ASM_API) {
            private boolean marked;
            
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
              if (FORAX_PROOF_DESC.equals(desc)) {
                marked = true;
              }
              return null;
            }
            @Override
            public void visitEnd() {
              Method method = new Method(loader, access, name, desc, signature, exceptions, marked);
              methods.put(name + desc, method);
            }
          };
        }
        
        Info parse() {
          reader.accept(this, ClassReader.SKIP_CODE);
          return info;
        }
      }.parse();
      
    } catch(IOException e) {
      throw new UncheckedIOException(e);
    }
  }
  
  @Override
  public Optional<ClassFile> asClassFile() {
    return Optional.of(this);
  }
  public int version() {
    return getInfo().version;
  }
  public int access() {
    return getInfo().access;
  }
  @Override
  public String name() {
    return name;
  }
  @Override
  public Optional<ClassFile> superType() {
    return Optional.ofNullable(getInfo().superType());
  }
  @Override
  public List<ClassFile> interfaceTypes() {
    return getInfo().interfaceTypes();
  }
  
  @Override
  public Collection<Field> fields() {
    return getInfo().fields.values();
  }
  @Override
  public Optional<Field> field(String name) {
    return Optional.ofNullable(getInfo().fields.get(name));
  }
  @Override
  public Collection<Method> methods() {
    return getInfo().methods.values();
  }
  @Override
  public Optional<Method> method(String name, String descriptor) {
    return Optional.ofNullable(getInfo().methods.get(name + descriptor));
  }
  
  @Override
  public String toString() {
    return name();
  }
}
