package tw.davy.minecraft.skinny.packet;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import tw.davy.minecraft.skinny.SignedSkin;
import tw.davy.minecraft.skinny.Skinny;
import tw.davy.minecraft.skinny.providers.ProviderManager;
import tw.davy.minecraft.skinny.utils.ReflectionUtil;

/**
 * @author Davy
 */
@ChannelHandler.Sharable
public class PacketListener extends ChannelDuplexHandler {
    private static final String HANDLER_NAME = "Skinny-Listener";
    private static Class<?> PacketPlayOutTileEntityData, PacketPlayOutPlayerInfo;
    private static Enum<?> EnumAddPlayer;
    private static PacketListener sInstance = new PacketListener();

    static {
        try {
            PacketPlayOutTileEntityData = ReflectionUtil.getNMSClass("PacketPlayOutTileEntityData");
            PacketPlayOutPlayerInfo = ReflectionUtil.getNMSClass("PacketPlayOutPlayerInfo");
            EnumAddPlayer = ReflectionUtil.getEnum(PacketPlayOutPlayerInfo, "EnumPlayerInfoAction", "ADD_PLAYER");
        } catch (ClassNotFoundException | EnumConstantNotPresentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(final ChannelHandlerContext context, final Object msg, ChannelPromise promise) throws Exception {
        // TileEntity (e.g. skull)
        if (PacketPlayOutTileEntityData.isInstance(msg)) {
            final Object tag = ReflectionUtil.getObject(msg, "c");
            final Object ownerTag = ReflectionUtil.invokeMethod(tag, "getCompound",
                    new Class<?>[] { String.class }, "Owner");

            if (ownerTag != null) {
                final String owner = ownerTag.toString();
                if (owner.contains("\"\"") || (owner.contains("textures") && owner.contains("Signature:\"\"")))
                    return;
            }
        } else if (PacketPlayOutPlayerInfo.isInstance(msg)) {
            final Object action = ReflectionUtil.getObject(msg, "a");

            if (EnumAddPlayer.equals(action)) {
                final List<?> datas = (List<?>) ReflectionUtil.getObject(msg, "b");

                for (final Object data : datas) {
                    final Object profile = ReflectionUtil.getObject(data, "d");
                    final String name = (String) ReflectionUtil.getObject(profile, "name");
                    final SignedSkin skin = getStorageManager().getSkin(name);

                    if (skin != null)
                        ProfileModifier.applySkinTextures(profile, skin);
                }
            }
        }

        super.write(context, msg, promise);
    }

    public static void inject(final Player player) {
        final Channel channel = getChannel(player);
        if (channel == null)
            return;

        if (channel.pipeline().context(HANDLER_NAME) != null)
            channel.pipeline().remove(HANDLER_NAME);

        channel.pipeline().addBefore("packet_handler", HANDLER_NAME, getInstance());
    }

    public static void uninject(final Player player) {
        final Channel channel = getChannel(player);
        if (channel == null)
            return;

        if (channel.pipeline().context(HANDLER_NAME) != null)
            channel.pipeline().remove(HANDLER_NAME);
    }

    private static ProviderManager getStorageManager() {
        return Skinny.getInstance().getProviderManager();
    }

    public static PacketListener getInstance() {
        return sInstance;
    }

    @Nullable
    private static Channel getChannel(final Player player) {
        try {
            final Object handle = ReflectionUtil.invokeMethod(player, "getHandle");
            final Object playerConnection = ReflectionUtil.getObject(handle, "playerConnection");
            final Object networkManager = ReflectionUtil.getObject(playerConnection, "networkManager");
            return (Channel) ReflectionUtil.getFirstMatchObject(networkManager, Channel.class);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return null;
    }
}
