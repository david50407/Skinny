package tw.davy.minecraft.skinny.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Multimap;

import java.util.List;

import tw.davy.minecraft.skinny.SignedSkin;
import tw.davy.minecraft.skinny.Skinny;
import tw.davy.minecraft.skinny.providers.ProviderManager;

/**
 * @author Davy
 */
public class PlayerInfoPacketAdapter extends PacketAdapter {
    private Skinny mPlugin;

    public PlayerInfoPacketAdapter(final Skinny plugin) {
        super(params(plugin, PacketType.Play.Server.PLAYER_INFO).optionAsync());

        mPlugin = plugin;
    }

    @Override
    public void onPacketSending(final PacketEvent packetEvent) {
        if (packetEvent.isCancelled())
            return;

        try {
            final PacketContainer packet = packetEvent.getPacket();
            if (packet.getPlayerInfoAction().read(0) != PlayerInfoAction.ADD_PLAYER)
                return;

            final List<PlayerInfoData> datas = packet.getPlayerInfoDataLists().read(0);
            if (datas.size() < 0)
                return;

            final PlayerInfoData data = datas.get(0);
            final WrappedGameProfile profile = data.getProfile();
            final String name = profile.getName();
            final SignedSkin skin = getStorageManager().getSkin(name);

            if (skin == null)
                return;

            final Multimap<String, WrappedSignedProperty> properties = profile.getProperties();
            properties.clear();
            properties.put("textures",
                    WrappedSignedProperty.fromValues("textures", skin.getValue(), skin.getSignature()));
            packet.getPlayerInfoDataLists().write(0, datas);
        } catch (FieldAccessException | IllegalStateException ignored) {
        }
    }

    private ProviderManager getStorageManager() {
        return mPlugin.getProviderManager();
    }
}
