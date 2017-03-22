package tw.davy.minecraft.skinny.providers;

import tw.davy.minecraft.skinny.SignedSkin;

/**
 * @author Davy
 */
public interface Provider {
    SignedSkin getSkinData(String name);
}
