package net.auuugh.beaconstuffs;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;

import java.util.List;

public class BeaconPacking {
    public static void packBeacon(ServerPlayer player, int layerCount, Identifier blockID) {

        //Beacon layers (tiers)
        ItemStack packedBeacon = new ItemStack(Items.BEACON);
        //packedBeacon.set(PortABeaconComponents.BEACON_LAYERS, layerCount);

        //New nbt system to hold data
        CustomData beaconLayersNBT = packedBeacon.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbt = beaconLayersNBT.copyTag();
        nbt.putInt("portabeacon:beacon_layers", layerCount);
        nbt.putString("portabeacon:beacon_blocktype", blockID.toString());

        packedBeacon.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        Component packedBeaconName = Component.literal("Packed Beacon").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD);
        packedBeacon.set(DataComponents.CUSTOM_NAME, packedBeaconName);

        //Beacon Blocktype
        //packedBeacon.set(PortABeaconComponents.BEACON_BLOCKTYPE, blockID.toString());

        Component packedBeaconLayers = Component.literal("Tiers: ").withStyle(ChatFormatting.DARK_AQUA).append(Component.literal(String.valueOf(layerCount)).withStyle(ChatFormatting.AQUA));

        Component packedBeaconBlock = Component.literal(blockID.toString()).withStyle(ChatFormatting.AQUA);
        ItemLore packedBeaconLore = new ItemLore(List.of(packedBeaconLayers, packedBeaconBlock));
        packedBeacon.set(DataComponents.LORE, packedBeaconLore);

        player.getInventory().placeItemBackInInventory(packedBeacon);

        System.out.println("Gave player beacon with " + layerCount + " layers.");
    }
}
