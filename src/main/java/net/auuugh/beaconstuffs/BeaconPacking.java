package net.auuugh.beaconstuffs;

import net.auuugh.component.PortABeaconComponents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BeaconPacking {
    public static void packBeacon(ServerPlayerEntity player, int layerCount) {
        // I don't think ive gotten this far in tutorials
        // Maybe use custom components to store pyramid level and block type?

        ItemStack portedBeacon = new ItemStack(Items.BEACON);
        portedBeacon.set(PortABeaconComponents.PYRAMID_LAYERS, layerCount);
        player.getInventory().offerOrDrop(portedBeacon);

        System.out.println("Gave " + player + " beacon with " + layerCount + " layers.");
    }
}
