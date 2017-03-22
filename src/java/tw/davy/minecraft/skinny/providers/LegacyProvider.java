package tw.davy.minecraft.skinny.providers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import tw.davy.minecraft.skinny.SignedSkin;
import tw.davy.minecraft.skinny.Skinny;

/**
 * @author Davy
 */
public class LegacyProvider implements Provider {
    public LegacyProvider() {
        createSkinFolder();
    }

    @Override
    public SignedSkin getSkinData(final String name) {
        if (!getSkinDir(name).exists())
            return null;

        final String value = getSkinValue(name);
        final String signature = getSkinSignature(name);
        if (value != null && signature != null)
            return new SignedSkin(value, signature);

        return null;
    }

    protected String getSkinValue(final String name) {
        return readData(new File(getSkinDir(name), "value.dat"));
    }

    protected String getSkinSignature(final String name) {
        return readData(new File(getSkinDir(name), "signature.dat"));
    }

    protected File getSkinDir(final String name) {
        return new File(getSkinFolder(), name.toLowerCase());
    }

    protected void createSkinFolder() {
        if (!getSkinFolder().exists())
            getSkinFolder().mkdir();
    }

    protected File getSkinFolder() {
        return new File(Skinny.getInstance().getDataFolder(),"skins");
    }

    protected String readData(final File file) {
        if (file.exists()) {
            try {
                final BufferedReader buf = new BufferedReader(new FileReader(file));
                final String data = buf.readLine();

                buf.close();
                return data;
            } catch (IOException ignored) {
            }
        }

        return null;
    }
}
