package com.spartez.ezdelegator;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * User: kalamon
 * Date: 2009-04-29
 * Time: 19:49:02
 */
public class Delegator {

    private Map<String, Class> generatedClasses = new HashMap<String, Class>();

    private static Delegator instance;
    public static final String DELEGATOR_CLASS_SUFFIX = "_$$$DELEGATOR$$$";
    private ClassAugmenter classAugmenter;

    public static synchronized Delegator getInstance() {
        if (instance == null) {
            instance = new Delegator();
        }
        return instance;
    }

    private Delegator() {
        classAugmenter = new ClassAugmenter();
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T createObject(Class<T> clazz, Object... args) throws DelegatorException {
        if (clazz.getName().startsWith("java")) {
            return generateObject(clazz, args);
        }
        if (generatedClasses.containsKey(clazz.getName())) {
            return (T) generateObject(generatedClasses.get(clazz.getName()), args);
        }
        Class augmentedClass;
        try {
            augmentedClass = generateAugmentedClass(clazz);
        } catch (ClassNotFoundException e) {
            throw new DelegatorException(e);
        } catch (InvocationTargetException e) {
            throw new DelegatorException(e);
        } catch (NoSuchMethodException e) {
            throw new DelegatorException(e);
        } catch (IllegalAccessException e) {
            throw new DelegatorException(e);
        }
        generatedClasses.put(clazz.getName(), augmentedClass);
        return (T) generateObject(augmentedClass, args);
    }

    public synchronized int getGeneratedClassesCount() {
        return generatedClasses.size();
    }

    public synchronized void flushGeneratedClasses() {
        generatedClasses.clear();
    }

    private Class generateAugmentedClass(Class clazz)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        byte[] bytes = classAugmenter.getClassBytes(clazz, clazz.getName() + DELEGATOR_CLASS_SUFFIX);
        return loadClass(clazz.getName() + DELEGATOR_CLASS_SUFFIX, bytes);
    }

    /**
     * Loads class into the current class loader. Requires appropriate security perms
     * @param className
     * @param b
     * @return loaded class
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Class loadClass(String className, byte[] b)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //override classDefine (as it is protected) and define the class.
        Class clazz = null;
        ClassLoader loader = getClass().getClassLoader();
        Class cls = Class.forName("java.lang.ClassLoader");
        java.lang.reflect.Method method =
                cls.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);

        // protected method invocaton
        method.setAccessible(true);
        try {
            Object[] args = new Object[]{className, b, 0, b.length};
            clazz = (Class) method.invoke(loader, args);
        } finally {
            method.setAccessible(false);
        }
        return clazz;
    }

    @SuppressWarnings("unchecked")
    private static <T> T generateObject(Class<T> clazz, Object... args) throws DelegatorException {
        List<Class> paramClasses = new ArrayList<Class>();
        for (Object arg : args) {
            paramClasses.add(arg.getClass());
        }
        try {
            Constructor[] ctors = clazz.getConstructors();
            for (Constructor<T> ctor : ctors) {
                if (isMatching(ctor, paramClasses)) {
                    return ctor.newInstance(args);
                }
            }
            throw new DelegatorException("No matching constructor found");
        } catch (Exception e) {
            throw new DelegatorException(e);
        }
    }

    private static boolean isMatching(Constructor ctor, List<Class> argTypes) {
        Class[] ctorParamClasses = ctor.getParameterTypes();
        if (ctorParamClasses.length != argTypes.size()) {
            return false;
        }
        int i = 0;
        for (Class argType : argTypes) {
            if (ctorParamClasses[i].isPrimitive() && matchPrimitiveType(ctorParamClasses[i], argType)) {
                ++i;
                continue;
            }
            if (!ctorParamClasses[i].isAssignableFrom(argType)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private static Map<Class, Class> primitives = new HashMap<Class, Class>();

    static {
        primitives.put(byte.class, Byte.class);
        primitives.put(short.class, Short.class);
        primitives.put(int.class, Integer.class);
        primitives.put(long.class, Long.class);
        primitives.put(float.class, Float.class);
        primitives.put(double.class, Double.class);
        primitives.put(boolean.class, Boolean.class);
        primitives.put(char.class, Character.class);
    }

    private static boolean matchPrimitiveType(Class ctorArgType, Class type) {
        Class wrapper = primitives.get(ctorArgType);
        return wrapper != null && wrapper.equals(type);
    }

    public static class DelegatorException extends Exception {
        public DelegatorException(Throwable cause) {
            super(cause);
        }

        public DelegatorException(String message) {
            super(message);
        }
    }
}
