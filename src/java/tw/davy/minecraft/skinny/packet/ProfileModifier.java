package tw.davy.minecraft.skinny.packet;

import java.lang.reflect.InvocationTargetException;

import tw.davy.minecraft.skinny.SignedSkin;
import tw.davy.minecraft.skinny.proxies.PropertyProxy;
import tw.davy.minecraft.skinny.utils.ReflectionUtil;

/**
 * @author Davy
 */
public class ProfileModifier {
    private static final Class[] PROPERTY_MAP_PUT_SIGNATURE = new Class[]{ Object.class, Object.class };

    public static void applySkinTextures(final Object profile, final SignedSkin skin) {
        try {
            // com.google.common.collect.ForwardingMultimap<String, com.mojang.authlib.properties.Property>
            final Object props = ReflectionUtil.getObject(profile, "properties");
            ReflectionUtil.invokeMethod(props, "clear");
            ReflectionUtil.invokeMethod(props, "put",
                    PROPERTY_MAP_PUT_SIGNATURE,
                    "textures", createProperty("textures", skin.getValue(), skin.getSignature()));
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    // com.mojang.authlib.properties.Property
    private static Object createProperty(final String name, final String value, final String signature) {
        return new PropertyProxy(name, value, signature).getNativeObject();
    }
}
