package tw.davy.minecraft.skinny.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import tw.davy.minecraft.skinny.SignedSkin;
import tw.davy.minecraft.skinny.Skinny;

/**
 * @author Davy
 */
public class AsyncPlayerPreLoginListener implements Listener {
    private final Skinny mPlugin;

    public AsyncPlayerPreLoginListener(final Skinny plugin) {
        mPlugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerPreLogin(final AsyncPlayerPreLoginEvent preLoginEvent) {
        if (preLoginEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED)
            return;

        final String playerName = preLoginEvent.getName();
        final SignedSkin skin = mPlugin.getProviderManager().getSkin(playerName);
        if (skin != null)
            mPlugin.getCache().put(playerName, skin);
    }
}
