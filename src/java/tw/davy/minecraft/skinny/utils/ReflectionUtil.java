package tw.davy.minecraft.skinny.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Davy
 */
public class ReflectionUtil {
    private static String versionSignature = null;

    static {
        try {
            Class.forName("org.bukkit.Bukkit");
            final String bukkitPackageName = Bukkit.getServer().getClass().getPackage().getName();
            versionSignature = bukkitPackageName.substring(bukkitPackageName.lastIndexOf('.') + 1);
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static Field getField(final Class<?> klass, final String name)
            throws NoSuchFieldException {
        Field field;
        try {
            field = klass.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            field = klass.getField(name);
        }
        setFieldAccessible(field);
        return field;
    }

    private static void setFieldAccessible(final Field field) {
        field.setAccessible(true);
        try {
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }

    public static Object getObject(final Object obj, final String name)
            throws NoSuchFieldException, IllegalAccessException {
        return getObject(obj.getClass(), obj, name);
    }

    private static Object getObject(final Class<?> klass, final Object obj, final String name)
            throws NoSuchFieldException, IllegalAccessException {
        return getField(klass, name).get(obj);
    }

    public static Object getObjectOrNull(final Object obj, final String name) {
        try {
            return getObject(obj, name);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return null;
        }
    }

    public static Object getFirstMatchObject(final Object obj, final Class<?> targetClass)
            throws IllegalAccessException {
        final Field field = getFirstMatchedFieldInternal(obj.getClass(), targetClass);
        if (field == null)
            return null;

        setFieldAccessible(field);
        return field.get(obj);
    }

    private static Field getFirstMatchedFieldInternal(final Class<?> klass, final Class<?> targetClass) {
        for (final Field f : klass.getDeclaredFields()) {
            if (f.getType().equals(targetClass))
                return f;
        }

        for (final Field f : klass.getFields()) {
            if (f.getType().equals(targetClass))
                return f;
        }

        return null;
    }

    public static Method getMethod(final Class<?> klazz, final String name)
            throws NoSuchMethodException {
        Method method;
        try {
            method = klazz.getDeclaredMethod(name);
        } catch (NoSuchMethodException e) {
            method = klazz.getMethod(name);
        }
        method.setAccessible(true);

        return method;
    }

    public static Method getMethod(final Class<?> klazz, final String name, final Class<?>... args)
            throws NoSuchMethodException {
        Method method;
        try {
            method = klazz.getDeclaredMethod(name, args);
        } catch (NoSuchMethodException e) {
            method = klazz.getMethod(name, args);
        }
        method.setAccessible(true);

        return method;
    }

    public static Object invokeMethod(final Class<?> klass, final Object obj, final String methodName)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return getMethod(klass, methodName).invoke(obj);
    }
    public static Object invokeMethod(final Object obj, final String methodName)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(obj.getClass(), obj, methodName);
    }

    public static Object invokeMethod(final Class<?> klass, final Object obj, final String methodName,
                                      final Class<?>[] argumentSignatures, final Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return getMethod(klass, methodName, argumentSignatures).invoke(obj, args);
    }
    public static Object invokeMethod(final Object obj, final String methodName,
                                      final Class<?>[] argumentSignatures, final Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(obj.getClass(), obj, methodName, argumentSignatures, args);
    }

    public static Enum<?> getEnum(final Class<?> klass, final String constant)
            throws ClassNotFoundException, EnumConstantNotPresentException {
        return getEnum(klass.getName(), null, constant);
    }

    public static Enum<?> getEnum(final Class<?> klass, final String enumName, final String constant)
            throws ClassNotFoundException, EnumConstantNotPresentException {
        return getEnum(klass.getName(), enumName, constant);
    }

    private static Enum<?> getEnum(final String klassSignature, final String enumName, final String constant)
            throws ClassNotFoundException, EnumConstantNotPresentException {
        final String enumClassSignature = klassSignature + (enumName == null ? "" : "$" + enumName);
        Class<? extends Enum> klass = (Class<? extends Enum>) Class.forName(enumClassSignature);
        Enum<?>[] enumConstants = (Enum<?>[]) klass.getEnumConstants();
        for (Enum<?> anEnum : enumConstants) {
            if (anEnum.name().equalsIgnoreCase(constant))
                return anEnum;
        }
        throw new EnumConstantNotPresentException(klass, constant);
    }

    public static Class<?> getNMSClass(final String klass) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + versionSignature + "." + klass);
    }

    public static Class<?> getCBClass(final String klass) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + versionSignature + "." + klass);
    }

    private static Constructor<?> getConstructor(final Class<?> klass, final Class<?>... signature)
            throws NoSuchMethodException {
        final Constructor<?> constructor = klass.getConstructor(signature);
        constructor.setAccessible(true);
        return constructor;
    }

    public static Object invokeConstructor(final Class<?> klass,
                                           final Class<?>[] argumentSignatures, final Object... args)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return getConstructor(klass, argumentSignatures).newInstance(args);
    }

    public static String getVersionSignature() {
        return versionSignature;
    }
}
