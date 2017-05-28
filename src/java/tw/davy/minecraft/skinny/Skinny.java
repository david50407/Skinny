package tw.davy.minecraft.skinny;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import tw.davy.minecraft.skinny.listener.AsyncPlayerPreLoginListener;
import tw.davy.minecraft.skinny.listener.PlayerLoginListener;
import tw.davy.minecraft.skinny.providers.ProviderManager;

/**
 * @author Davy
 */
public class Skinny extends JavaPlugin implements Listener {
    private static Skinny sInstance;

    private ProviderManager mProviderManager;
    private final ConcurrentMap<String, SignedSkin> mCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<String, SignedSkin>() {
                @Override
                public SignedSkin load(final String playerName) throws Exception {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            })
            .asMap();

    @Override
    public void onEnable() {
        sInstance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();
        final List<String> enableProviders = getConfig().getStringList("providers");
        mProviderManager = new ProviderManager(enableProviders);

        getServer().getPluginManager().registerEvents(new AsyncPlayerPreLoginListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLoginListener(this), this);
    }

    @NotNull
    public static Skinny getInstance() {
        return sInstance;
    }

    @NotNull
    public ProviderManager getProviderManager() {
        return mProviderManager;
    }

    @NotNull
    public ConcurrentMap<String, SignedSkin> getCache() {
        return mCache;
    }
}
