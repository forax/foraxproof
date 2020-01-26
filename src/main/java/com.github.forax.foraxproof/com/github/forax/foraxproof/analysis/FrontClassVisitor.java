package com.github.forax.foraxproof.analysis;

import static com.github.forax.foraxproof.AsmVersion.ASM_API;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.TypePath;

import com.github.forax.foraxproof.reflect.ClassFileLoader;

class FrontClassVisitor extends ClassVisitor {
  private final ClassFileLoader loader;
  private boolean published;
  private int version;
  private int access;
  private String className;
  private String signature;
  private String superName;
  private String[] interfaces;
  private String sourceFile;
  
  final ErrorReporter reporter;
  final ContextImpl context;
  
  static class AnnotationInfo  {
    final String desc;
    final boolean visible;
    final AnnotationInfo next;
    
    AnnotationInfo(String desc, boolean visible, AnnotationInfo next) {
      this.desc = desc;
      this.visible = visible;
      this.next = next;
    }
  }
  
  static class ContextImpl implements Context { 
    private final ErrorReporter reporter;
    private String className;
    private String sourceFile;
    private Member member;
    private String memberName;
    private String memberDescriptor;
    int line = -1;
    
    public ContextImpl(ErrorReporter reporter) {
      this.reporter = reporter;
    }

    void initClass(String className, String sourceFile) {
      this.className = className;
      this.sourceFile = sourceFile;
    }
    void initMember(Member member, String memberName, String memberDescriptor) {
      this.member = member;
      this.memberName = memberName;
      this.memberDescriptor = memberDescriptor;
    }
    
    @Override
    public String className() {
      return className;
    }
    @Override
    public String sourceFile() {
      return sourceFile;
    }

    @Override
    public Member member() {
      return member;
    }
    @Override
    public String memberName() {
      return memberName;
    }
    @Override
    public String memberDescriptor() {
      return memberDescriptor;
    }

    @Override
    public int line() {
      return line;
    }
    
    @Override
    public void error(String type, String info) {
      reporter.error(this, type, info);
    }
  }
  
  private FrontClassVisitor(ClassVisitor cv, ClassFileLoader loader, ErrorReporter reporter, ContextImpl context) {
    super(ASM_API, cv);
    this.loader = loader;
    this.reporter = reporter;
    this.context = context;
  }
  
  static FrontClassVisitor create(Analysis analysis, ClassFileLoader loader, ErrorReporter reporter) {
    ContextImpl context = new ContextImpl(reporter);
    ClassVisitor chain = analysis.analyze(new ClassVisitor(ASM_API) { /*empty*/ }, context);
    return new FrontClassVisitor(chain, loader, reporter, context);
  }

  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    this.published = false;
    this.version = version;
    this.access = access;
    this.className = name;
    this.signature = signature;
    this.superName = superName;
    this.interfaces = interfaces;
  }
  
  @Override
  public void visitSource(String source, String debug) {
    this.sourceFile = source;
    publishIfNecessary();
    cv.visitSource(source, debug);
  }

  private void publishIfNecessary() {
    if (published) {
      return;
    }
    
    context.initClass(className, sourceFile);
    context.initMember(Context.Member.CLASS, className, "");
    reporter.enterClass(context);
    cv.visit(version, access, className, signature, superName, interfaces);
    published = true;
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    publishIfNecessary();
    return cv.visitAnnotation(desc, visible);
  }
  @Override
  public void visitAttribute(Attribute attr) {
    publishIfNecessary();
    cv.visitAttribute(attr);
  }
  @Override
  public ModuleVisitor visitModule(String name, int access, String version) {
    publishIfNecessary();
    return cv.visitModule(name, access, version);
  }
  
  @Override
  public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
    publishIfNecessary();
    return cv.visitTypeAnnotation(typeRef, typePath, desc, visible);
  }
  @Override
  public void visitInnerClass(String name, String outerName, String innerName, int access) {
    publishIfNecessary();
    cv.visitInnerClass(name, outerName, innerName, access);
  }
  @Override
  public void visitOuterClass(String owner, String name, String desc) {
    publishIfNecessary();
    cv.visitOuterClass(owner, name, desc);
  }
  
  @Override
  public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
    publishIfNecessary();
    if (loader.getClassFile(this.className).field(name).get().marked()) { // do not analyze marked fields
      return null;
    }
    context.initMember(Context.Member.FIELD, name, desc);
    reporter.enterField(context);
    return new FieldVisitor(ASM_API, cv.visitField(access, name, desc, signature, value)) {
      @Override
      public void visitEnd() {
        if (fv != null) {
          fv.visitEnd();
        }
        reporter.exitField();
      }
    };
  }
  @Override
  public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    publishIfNecessary();
    if (loader.getClassFile(this.className).method(name, desc).get().marked()) { // do not analyze marked methods
      return null;
    }
    context.initMember(Context.Member.METHOD, name, desc);
    reporter.enterMethod(context);
    return new MethodVisitor(ASM_API, cv.visitMethod(access, name, desc, signature, exceptions)) {
      @Override
      public void visitLineNumber(int line, Label start) {
        context.line = line;
      }
      @Override
      public void visitEnd() {
        if (mv != null) {
          mv.visitEnd();
        }
        reporter.exitMethod();
        context.line = -1;
      }
    };
  }
  @Override
  public void visitEnd() {
    publishIfNecessary();
    cv.visitEnd();
    reporter.exitClass();
    context.initClass(null, null);
    context.initMember(null, null, null);
  }
}