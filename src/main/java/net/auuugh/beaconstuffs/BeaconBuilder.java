package net.auuugh.beaconstuffs;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class BeaconBuilder {
    public static void register() {
        //I may or may not have copied and pasted this from BeaconScanner LOL
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if(world.isClientSide()) return InteractionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            ItemStack mainHandItem = player.getItemInHand(hand);

            CustomData customData = mainHandItem.get(DataComponents.CUSTOM_DATA);
            if (customData == null) {
                return InteractionResult.PASS;
            }
            CompoundTag nbt = customData.copyTag();
            String layerKey = nbt.contains("portabeacon:beacon_layers") ? "portabeacon:beacon_layers" : "beacon_layers";
            String blocktypeKey = nbt.contains("portabeacon:beacon_blocktype") ? "portabeacon:beacon_blocktype" : "beacon_blocktype";
            //Items.BEACON.getComponents().contains(PYRAMID_LAYERS) && Items.BEACON.getComponents().contains(PYRAMID_BLOCKTYPE)

            //Player & Block check
            if(mainHandItem.is(Items.BEACON) && nbt.contains(layerKey) && nbt.contains(blocktypeKey)) {
                if(!world.isClientSide()) {
                    System.out.println("New Beacon build at " + pos);
                    int buildLayers = nbt.getInt(layerKey).orElse(0);
                    String buildBlockType = nbt.getString(blocktypeKey).orElse("minecraft:iron_block");
                    pyramidBuilder(world, pos, (ServerPlayer) player, buildLayers, buildBlockType);
                }

                if (!player.getAbilities().instabuild) {
                    mainHandItem.shrink(1);
                }
            }
            return InteractionResult.PASS;
        });
    }

    static void pyramidBuilder(Level world, BlockPos beaconPos, ServerPlayer player, int layerCount, String pyramidBlockType) {
        //System.out.println("temp msg beaconbuilder");
        //vars
        int x = beaconPos.getX();
        int y = beaconPos.getY() + layerCount + 1;
        int z = beaconPos.getZ();
        int radius = 0;
        int layerY;
        int xCoord;
        int zCoord;
        BlockPos newBeaconSpot = beaconPos.above(layerCount + 1);

        Identifier blockType = Identifier.tryParse(pyramidBlockType);
        Block blockType2 = BuiltInRegistries.BLOCK.get(blockType).map(Holder.Reference::value).orElse(Blocks.AIR);
        BlockState blockType3 = blockType2.defaultBlockState();
        //System.out.println("x: " +  x + " y: " + y + " z: " + z);
        //System.out.println("Block type to check for (when building!): " + blockType);


        for (int layer = 1; layer <= layerCount; layer++) {
            //temp
            layerY = y - layer;
            radius = layer;

            world.setBlockAndUpdate(newBeaconSpot, Blocks.BEACON.defaultBlockState());
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
                    world.setBlockAndUpdate(placeBlockHere, blockType3.getBlock().defaultBlockState());
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
        player.teleportTo(player.getX(), tpUp, player.getZ());
    }
}
