package tw.davy.minecraft.skinny.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import tw.davy.minecraft.skinny.SignedSkin;
import tw.davy.minecraft.skinny.Skinny;
import tw.davy.minecraft.skinny.utils.NbtModifier;

import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_EQUIPMENT;
import static com.comphenix.protocol.PacketType.Play.Server.SET_SLOT;
import static com.comphenix.protocol.PacketType.Play.Server.WINDOW_ITEMS;

/**
 * @author Davy
 */
public class SkullItemDataPacketAdapter extends PacketAdapter {
    private final Skinny mPlugin;

    public SkullItemDataPacketAdapter(final Skinny plugin) {
        super(params(plugin, WINDOW_ITEMS, ENTITY_EQUIPMENT, SET_SLOT));

        mPlugin = plugin;
    }

    @Override
    public void onPacketSending(final PacketEvent packetEvent) {
        if (packetEvent.isCancelled())
            return;

        if (packetEvent.getPacketType() == WINDOW_ITEMS) {
            final List<ItemStack> slotData = packetEvent.getPacket().getItemListModifier().read(0);
            for (final ItemStack itemStack : slotData)
                modifyItem(itemStack);

            packetEvent.getPacket().getItemListModifier().write(0, slotData);
        } else {
            final ItemStack itemStack = packetEvent.getPacket().getItemModifier().read(0);
            modifyItem(itemStack);
            packetEvent.getPacket().getItemModifier().write(0, itemStack);
        }
    }

    private void modifyItem(final ItemStack itemStack) {
        if (itemStack.getType() != Material.SKULL_ITEM ||
                itemStack.getDurability() != 3)
            return;
        if (!MinecraftReflection.isCraftItemStack(itemStack))
            return;

        try {
            final NbtCompound tag = (NbtCompound) NbtFactory.fromItemTag(itemStack);
            final NbtCompound ownerTag = tag.getCompound("SkullOwner");
            final String ownerName = ownerTag.getString("Name");
            final SignedSkin skin = mPlugin.getProviderManager().getSkin(ownerName);

            NbtModifier.patchOwnerSkin(ownerTag, skin);
        } catch (FieldAccessException | IllegalArgumentException ignored) {
        }
    }
}
