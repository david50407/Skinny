package tw.davy.minecraft.skinny;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import tw.davy.minecraft.skinny.packet.PacketListener;
import tw.davy.minecraft.skinny.providers.ProviderManager;
import tw.davy.minecraft.skinny.utils.ReflectionUtil;

/**
 * @author Davy
 */
public class Skinny extends JavaPlugin implements Listener {
    private static Skinny sInstance;

    private ProviderManager mProviderManager;

    @Override
    public void onEnable() {
        sInstance = this;
        getLogger().info("Detected Minecraft Server Version: " + ReflectionUtil.getVersionSignature());

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();
        final List<String> enableProviders = getConfig().getStringList("providers");
        mProviderManager = new ProviderManager(enableProviders);

        Bukkit.getOnlinePlayers().forEach(PacketListener::inject);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(PacketListener::uninject);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final PlayerJoinEvent evt) {
        PacketListener.inject(evt.getPlayer());
    }

    @NotNull
    public static Skinny getInstance() {
        return sInstance;
    }

    @NotNull
    public ProviderManager getProviderManager() {
        return mProviderManager;
    }
}
