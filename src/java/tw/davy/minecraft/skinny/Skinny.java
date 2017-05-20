package tw.davy.minecraft.skinny;

import com.comphenix.protocol.AsynchronousManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import tw.davy.minecraft.skinny.packet.PlayerInfoPacketAdapter;
import tw.davy.minecraft.skinny.packet.SkullItemDataPacketAdapter;
import tw.davy.minecraft.skinny.packet.TileEntityDataPacketAdapter;
import tw.davy.minecraft.skinny.providers.ProviderManager;

/**
 * @author Davy
 */
public class Skinny extends JavaPlugin implements Listener {
    private static Skinny sInstance;

    private ProviderManager mProviderManager;
    private ProtocolManager mProtocolManager;

    @Override
    public void onEnable() {
        sInstance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();
        final List<String> enableProviders = getConfig().getStringList("providers");
        mProviderManager = new ProviderManager(enableProviders);

        mProtocolManager = ProtocolLibrary.getProtocolManager();
        final AsynchronousManager asynchronousManager = mProtocolManager.getAsynchronousManager();
        asynchronousManager.registerAsyncHandler(new PlayerInfoPacketAdapter(this)).start();
        // These might not work
        asynchronousManager.registerAsyncHandler(new TileEntityDataPacketAdapter(this)).start();
        asynchronousManager.registerAsyncHandler(new SkullItemDataPacketAdapter(this)).start();
    }

    @Override
    public void onDisable() {
        mProtocolManager.removePacketListeners(this);
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
