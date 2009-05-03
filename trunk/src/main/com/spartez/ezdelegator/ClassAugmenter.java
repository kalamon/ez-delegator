package com.spartez.ezdelegator;

import com.spartez.ezdelegator.annotation.Delegate;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;

/**
 * User: kalamon
 * Date: 2009-05-01
 * Time: 12:43:39
 */
public class ClassAugmenter {

    public byte[] getClassBytes(Class clazz, String newName) {

        processAnnotations(clazz);

        try {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassAdapter ca = new ClassRenamer(cw, newName);
            ClassReader cr = new ClassReader(clazz.getName());
            cr.accept(ca, 0);

            File f = new File(newName);
            new FileOutputStream(f).write(cw.toByteArray());
            return cw.toByteArray();

        } catch (IOException e) {
            return null;
        }
    }

    private void processAnnotations(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Delegate d = field.getAnnotation(Delegate.class);
            if (d != null) {
                Class ifc = d.ifc();
                System.out.println("field: " + field.getName() + " is a delegate for: " + ifc.getName());
            }
        }
    }
}
