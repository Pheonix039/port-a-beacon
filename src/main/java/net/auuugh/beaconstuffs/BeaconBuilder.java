package net.auuugh.beaconstuffs;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class BeaconBuilder {
    public static void register() {
        //I may or may not have copy pasted this from BeaconScanner LOL
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if(world.isClient()) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            ItemStack mainHandItem = player.getStackInHand(hand);

            NbtComponent customData = mainHandItem.get(DataComponentTypes.CUSTOM_DATA);
            if (customData == null) {
                return ActionResult.PASS;
            }
            NbtCompound nbt = customData.copyNbt();
            String layerKey = nbt.contains("portabeacon:beacon_layers") ? "portabeacon:beacon_layers" : "beacon_layers";
            String blocktypeKey = nbt.contains("portabeacon:beacon_blocktype") ? "portabeacon:beacon_blocktype" : "beacon_blocktype";
            //Items.BEACON.getComponents().contains(PYRAMID_LAYERS) && Items.BEACON.getComponents().contains(PYRAMID_BLOCKTYPE)

            //Player & Block check
            if(mainHandItem.isOf(Items.BEACON) && nbt.contains(layerKey) && nbt.contains(blocktypeKey)) {
                if(!world.isClient()) {
                    System.out.println("New Beacon build at " + pos);
                    int buildLayers = nbt.getInt(layerKey).orElse(0);
                    String buildBlockType = nbt.getString(blocktypeKey).orElse("minecraft:iron_block");
                    pyramidBuilder(world, pos, (ServerPlayerEntity) player, buildLayers, buildBlockType);
                }

                if (!player.getAbilities().creativeMode) {
                    mainHandItem.decrement(1);
                }
            }
            return ActionResult.PASS;
        });
    }

    static void pyramidBuilder(World world, BlockPos beaconPos, ServerPlayerEntity player, int layerCount, String pyramidBlockType) {
        //System.out.println("temp msg beaconbuilder");
        //vars
        int x = beaconPos.getX();
        int y = beaconPos.getY() + layerCount + 1;
        int z = beaconPos.getZ();
        int radius = 0;
        int layerY;
        int xCoord;
        int zCoord;
        BlockPos newBeaconSpot = beaconPos.up(layerCount + 1);

        Identifier blockType = Identifier.tryParse(pyramidBlockType);
        Block blockType2 = Registries.BLOCK.get(blockType);
        BlockState blockType3 = blockType2.getDefaultState();
        //System.out.println("x: " +  x + " y: " + y + " z: " + z);
        //System.out.println("Block type to check for (when building!): " + blockType);


        for (int layer = 1; layer <= layerCount; layer++) {
            //temp
            layerY = y - layer;
            radius = layer;

            world.setBlockState(newBeaconSpot, Blocks.BEACON.getDefaultState());
            //System.out.println("newBeaconSpot " + newBeaconSpot);
            //main loop for checking pyramid grid
            checkBlock:
            for (int checkx = -radius; checkx <= radius; checkx++) {
                xCoord = x + checkx;
                for (int checkz = -radius; checkz <= radius; checkz++) {
                    //update z coord, position, and block
                    zCoord = z + checkz;
                    BlockPos pos = new BlockPos(xCoord, layerY, zCoord);
                    BlockState state = world.getBlockState(pos);
                    BlockPos placeBlockHere = new BlockPos(xCoord, layerY, zCoord);

                    //System.out.println("place blockHere " + blockType3);
                    world.setBlockState(placeBlockHere, blockType3.getBlock().getDefaultState());
                    //System.out.println(state.getBlock());
                    //System.out.println("Radius = " + radius);
                    //System.out.println("radius " + radius);
                    //System.out.println("checkX: " + checkx + " || checkz: " + checkz);
                    //System.out.println("xCoord " + xCoord + " || zCoord " + zCoord + "\n");
                    //System.out.println("layerY " + layerY);
                }
            }
        }

        double tpUp = player.getY() + layerCount;
        player.teleport(player.getX(), tpUp, player.getZ(), false);
    }
}
