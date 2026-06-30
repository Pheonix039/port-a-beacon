package net.auuugh.beaconstuffs;

import net.auuugh.component.PortABeaconComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class BeaconPacking {
    public static void packBeacon(ServerPlayerEntity player, int layerCount, Identifier blockID) {

        //Beacon layers (tiers)
        ItemStack packedBeacon = new ItemStack(Items.BEACON);
        packedBeacon.set(PortABeaconComponents.BEACON_LAYERS, layerCount);

        Text packedBeaconName = Text.literal("Packed Beacon").formatted(Formatting.AQUA).formatted(Formatting.BOLD);
        packedBeacon.set(DataComponentTypes.CUSTOM_NAME, packedBeaconName);

        //Beacon Blocktype
        packedBeacon.set(PortABeaconComponents.BEACON_BLOCKTYPE, blockID.toString());

        Text packedBeaconLayers = Text.literal("Tiers: ").formatted(Formatting.DARK_AQUA).append(Text.of(String.valueOf(layerCount).formatted(Formatting.AQUA)));

        Text packedBeaconBlock = Text.literal(blockID.toString()).formatted(Formatting.AQUA);
        LoreComponent packedBeaconLore = new LoreComponent(List.of(packedBeaconLayers, packedBeaconBlock));
        packedBeacon.set(DataComponentTypes.LORE, packedBeaconLore);

        player.getInventory().offerOrDrop(packedBeacon);

        System.out.println("Gave player beacon with " + layerCount + " layers.");
    }
}
