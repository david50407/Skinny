package tw.davy.minecraft.skinny.proxies;

import tw.davy.minecraft.skinny.utils.ReflectionUtil;

/**
 * @author Davy
 */
public class PropertyProxy {
    private static Class<?> NativeProperty;
    private final static Class<?>[] InitializerSignature = { String.class, String.class, String.class };

    static {
        try {
            NativeProperty = Class.forName("com.mojang.authlib.properties.Property");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // com.mojang.authlib.properties.Property
    private final Object mNativeObj;

    public PropertyProxy(final Object nativeObj) {
        mNativeObj = nativeObj;
    }

    public PropertyProxy(final String name, final String value) {
        this(name, value, null);
    }

    public PropertyProxy(final String name, final String value, final String signature) {
        try {
            mNativeObj = ReflectionUtil.invokeConstructor(NativeProperty, InitializerSignature, name, value, signature);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return @{com.mojang.authlib.properties.Property}
     */
    public Object getNativeObject() {
        return mNativeObj;
    }
}
