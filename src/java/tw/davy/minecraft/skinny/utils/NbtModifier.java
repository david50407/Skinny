package tw.davy.minecraft.skinny.utils;

import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;

import java.util.Collections;

import tw.davy.minecraft.skinny.SignedSkin;

/**
 * @author Davy
 */
public class NbtModifier {
    public static void patchOwnerSkin(final NbtCompound owner, final SignedSkin skin) {
        if (skin == null)
            return;

        try {
            final NbtCompound propertiesTag = owner.getCompound("Properties");
            if (propertiesTag.containsKey("textures"))
                propertiesTag.remove("textures");
            final NbtCompound skinTexture = NbtFactory.ofCompound(NbtList.EMPTY_NAME);
            skinTexture.put("Value", skin.getValue());
            skinTexture.put("Signature", skin.getSignature());
            propertiesTag.put("textures", Collections.singletonList(skinTexture));
        } catch (FieldAccessException | IllegalArgumentException ignored) {
        }
    }
}
