package tw.davy.minecraft.skinny.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;

import java.util.Collections;

import tw.davy.minecraft.skinny.SignedSkin;
import tw.davy.minecraft.skinny.Skinny;
import tw.davy.minecraft.skinny.utils.NbtModifier;

/**
 * @author Davy
 */
public class TileEntityDataPacketAdapter extends PacketAdapter {
    private Skinny mPlugin;

    public TileEntityDataPacketAdapter(final Skinny plugin) {
        super(params(plugin, PacketType.Play.Server.TILE_ENTITY_DATA).optionAsync());

        mPlugin = plugin;
    }

    @Override
    public void onPacketSending(final PacketEvent packetEvent) {
        if (packetEvent.isCancelled())
            return;

        // TileEntity (e.g. skull)
        try {
            final PacketContainer packet = packetEvent.getPacket();
            final NbtCompound tag = (NbtCompound) packet.getNbtModifier().read(0);
            if (!tag.getString("id").equals("minecraft:skull") ||
                    tag.getByte("SkullType") != 3)
                return;

            final NbtCompound ownerTag = tag.getCompound("Owner");
            final String ownerName = ownerTag.getString("Name");
            final SignedSkin skin = mPlugin.getProviderManager().getSkin(ownerName);

            NbtModifier.patchOwnerSkin(ownerTag, skin);
        } catch (FieldAccessException | IllegalArgumentException ignored) {
        }
    }
}
