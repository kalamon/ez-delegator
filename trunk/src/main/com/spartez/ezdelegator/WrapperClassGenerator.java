package com.spartez.ezdelegator;

import org.objectweb.asm.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

import java.lang.reflect.*;
import java.util.Map;

/**
 * User: kalamon
 * Date: 2009-05-01
 * Time: 21:32:44
 */

public class WrapperClassGenerator extends ClassAdapter implements Opcodes {

    private String oldName;
    private final String newName;
    private Map<Field, Class> annotatedFieldsMap;

    public WrapperClassGenerator(ClassVisitor cv, String newName, Map<Field, Class> annotatedFieldsMap) {
        super(cv);
        this.annotatedFieldsMap = annotatedFieldsMap;
        this.newName = newName.replace('.', '/');
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        oldName = name;
        cv.visit(version, ACC_PUBLIC, newName, signature, name, interfaces);
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals("<init>")) {
            MethodVisitor mv = cv.visitMethod(access, name, fix(desc), fix(signature), exceptions);
            generateConstructor(mv, oldName, desc);
            return mv;
        }
        return null;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        for (Field field : annotatedFieldsMap.keySet()) {
            if (name.equals(field.getName())) {
                generateDelegatingMethods(field);
            }
        }
        return null;
    }

    private void generateDelegatingMethods(Field field) {
        Class ifc = annotatedFieldsMap.get(field);
        java.lang.reflect.Method[] methods = ifc.getMethods();
        for (java.lang.reflect.Method method : methods) {
            generateDelegatingMethod(field, method);
        }
    }

    private void generateDelegatingMethod(Field field, java.lang.reflect.Method method) {
//        System.out.println("creating delegate in field " + field.getName() + " for method " + method.getDeclaringClass() + "." + method.getName());
        Method m = new Method(method.getName(), Type.getMethodDescriptor(method));
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, m.getName(), m.getDescriptor(), null, null);
        Type[] argTypes = m.getArgumentTypes();
        mv.visitCode();
        int i = 0;
        mv.visitVarInsn(ALOAD, i++);

        String clsName = field.getDeclaringClass().getName().replace('.', '/');
        String nm = field.getName();
        String desc = Type.getDescriptor(field.getType());
        mv.visitFieldInsn(GETFIELD, clsName, nm, desc);

        for (Type argType : argTypes) {
            mv.visitVarInsn(argType.getOpcode(ILOAD), i++);
        }

        String methodName = m.getName();
        String fieldClsName = field.getType().getName().replace('.', '/');
        String methodDescr = m.getDescriptor();
        mv.visitMethodInsn(INVOKEVIRTUAL, fieldClsName, methodName, methodDescr);

        mv.visitInsn(m.getReturnType().getOpcode(IRETURN));
        mv.visitMaxs(i, 1);
        mv.visitEnd();
    }

    private void generateConstructor(MethodVisitor mv, String owner, String desc) {
        Method m = new Method("<init>", desc);
        Type[] argTypes = m.getArgumentTypes();
        mv.visitCode();
        int i = 0;
        mv.visitVarInsn(ALOAD, i++);
        for (Type argType : argTypes) {
            mv.visitVarInsn(argType.getOpcode(ILOAD), i++);
        }
        mv.visitMethodInsn(INVOKESPECIAL, owner, "<init>", desc);
        mv.visitInsn(RETURN);
        mv.visitMaxs(i, 1);
        mv.visitEnd();
    }

    private String fix(String s) {
        if (s != null) {
            if (s.indexOf(oldName) != -1) {
                s = s.replaceAll(oldName, newName);
            }
        }
        return s;
    }
}
