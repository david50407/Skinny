package tw.davy.minecraft.skinny.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import tw.davy.minecraft.skinny.SignedSkin;
import tw.davy.minecraft.skinny.Skinny;

/**
 * @author Davy
 */
public class ProviderManager {
    private final ArrayList<Provider> mProviders = new ArrayList<>();

    public ProviderManager(List<String> enableStorages) {
        enableStorages.forEach(storageName -> {
            try {
                final Class<? extends Provider> klass = (Class<? extends Provider>) Class.forName(storageName);
                mProviders.add(klass.newInstance());
            } catch (ClassNotFoundException | ClassCastException ignored) {
                getLogger().warning("Failed to load provider: " + storageName);
            } catch (IllegalAccessException | InstantiationException ignored) {
                getLogger().warning("Failed to initialize provider: " + storageName);
            }
        });

        if (mProviders.isEmpty())
            getLogger().warning("No skin providers loaded, this plugin may not works.");
        else {
            getLogger().info("Loaded providers:");
            mProviders.forEach(provider -> {
                getLogger().info("* " + provider.getClass().getName());
            });
        }
    }

    public SignedSkin getSkin(final String name) {
        for (final Provider provider : mProviders) {
            final SignedSkin skin = provider.getSkinData(name);
            if (skin != null)
                return skin;
        }

        return null;
    }

    private static Logger getLogger() {
        return Skinny.getInstance().getLogger();
    }
}
