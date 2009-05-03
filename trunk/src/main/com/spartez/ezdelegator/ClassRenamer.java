package com.spartez.ezdelegator;

import org.objectweb.asm.*;

import java.util.Set;
import java.util.HashSet;

/**
 * User: kalamon
 * Date: 2009-05-01
 * Time: 21:32:44
 */

public class ClassRenamer extends ClassAdapter implements Opcodes {

    private Set<String> oldNames = new HashSet<String>();
    private final String newName;

    public ClassRenamer(ClassVisitor cv, String newName) {
        super(cv);
        this.newName = newName.replace('.', '/');
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        oldNames.add(name);
        cv.visit(version, ACC_PUBLIC, newName, signature, superName, interfaces);
        // todo: super class should now be the original class. We need to call appropriate super() constructors
//        cv.visit(version, ACC_PUBLIC, newName, signature, name, interfaces);
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, fix(desc), fix(signature), exceptions);
        if (mv != null && (access & ACC_ABSTRACT) == 0) {
            mv = new MethodRenamer(mv);
        }
        if (mv != null && name.equals("<init>")) {
            mv = new SuperCaller(mv);
        }
        return mv;
    }

    class SuperCaller extends MethodAdapter {

        public SuperCaller(MethodVisitor mv) {
            super(mv);
        }

        // todo
    }

    class MethodRenamer extends MethodAdapter {

        public MethodRenamer(final MethodVisitor mv) {
            super(mv);
        }

        public void visitTypeInsn(int i, String s) {
            if (oldNames.contains(s)) {
                s = newName;
            }
            mv.visitTypeInsn(i, s);
        }

        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            if (oldNames.contains(owner)) {
                mv.visitFieldInsn(opcode, newName, name, fix(desc));
            } else {
                mv.visitFieldInsn(opcode, owner, name, fix(desc));
            }
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (oldNames.contains(owner)) {
                mv.visitMethodInsn(opcode, newName, name, fix(desc));
            } else {
                mv.visitMethodInsn(opcode, owner, name, fix(desc));
            }
        }
    }

    private String fix(String s) {
        if (s != null) {
            for (String oldName : oldNames) {
                if (s.indexOf(oldName) != -1) {
                    s = s.replaceAll(oldName, newName);
                }
            }
        }
        return s;
    }
}
