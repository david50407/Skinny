package tw.davy.minecraft.skinny.listener;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Multimap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import tw.davy.minecraft.skinny.SignedSkin;
import tw.davy.minecraft.skinny.Skinny;

/**
 * @author Davy
 */
public class PlayerLoginListener implements Listener {
    private final Skinny mPlugin;

    public PlayerLoginListener(final Skinny plugin) {
        mPlugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent loginEvent) {
        if (loginEvent.getResult() != PlayerLoginEvent.Result.ALLOWED)
            return;

        final Player player = loginEvent.getPlayer();
        final SignedSkin skin = mPlugin.getCache().getOrDefault(player.getName(), null);
        if (skin == null)
            return;

        final WrappedGameProfile profile = WrappedGameProfile.fromPlayer(player);
        final Multimap<String, WrappedSignedProperty> properties = profile.getProperties();
        properties.clear();
        properties.put("textures",
                WrappedSignedProperty.fromValues("textures", skin.getValue(), skin.getSignature()));
    }
}
