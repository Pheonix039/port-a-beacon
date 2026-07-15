package net.auuugh.beaconstuffs;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class BeaconScanner {
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            if(hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

            //Get block position, what type of block, and block entity data
            ItemStack mainHandItem = player.getItemInHand(hand);
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            // BlockEntity entity = world.getBlockEntity(pos);

            //Player & Block check
            if(player.isShiftKeyDown() && state.is(Blocks.BEACON)) {
                if (mainHandItem.is(Items.BEACON)) return InteractionResult.SUCCESS;
                if(!world.isClientSide()) {
                    System.out.println("New Beacon scan at " + pos);
                    pyramidScanner(world, pos, (ServerPlayer) player);
                }
                return InteractionResult.SUCCESS;
            }
            if(world.isClientSide()) return InteractionResult.PASS;
            return InteractionResult.PASS;
        });
    }

    static void pyramidScanner(Level world, BlockPos beaconPos, ServerPlayer player) {
        //vars
        int x = beaconPos.getX();
        int y = beaconPos.getY();
        int z = beaconPos.getZ();
        int radius = 0;
        int layerY;
        int xCoord;
        int zCoord;
        boolean firstLayerBroken = false;

        world.setBlockAndUpdate(beaconPos, Blocks.AIR.defaultBlockState());

        BlockState blockTypeD = world.getBlockState(beaconPos.below());
        Identifier blockType = BuiltInRegistries.BLOCK.getKey(blockTypeD.getBlock());
        //System.out.println("Block type to check for: " + blockType);

        layerLoop:
        for(int layer = 1; layer <= 4; layer++) {
            //temp
            layerY = y - layer;
            radius = layer;

            //main loop for checking pyramid grid
            checkBlock:
            for (int checkx = -radius; checkx <= radius; checkx++) {
                xCoord = x + checkx;
                for (int checkz = -radius; checkz <= radius; checkz++) {
                    //update z coord, position, and block
                    zCoord = z + checkz;
                    BlockPos pos = new BlockPos(xCoord, layerY, zCoord);
                    BlockState state = world.getBlockState(pos);
                    //System.out.println(state.getBlock());
                    //System.out.println("Radius = " + radius);


                    //ends if any block found doesn't match blockType or isn't a beacon block tag
                    if ((!state.is(blockTypeD.getBlock()) || !state.is(BlockTags.BEACON_BASE_BLOCKS))) {
                        //System.out.println("Non Beacon block found at " + pos + ": " + state.getBlock());
                        //System.out.println("Block expected: " + blockType);
                        radius--;

                        //Text errorMsg = Text.literal("Non Beacon block found at " + pos + ": " + state.getBlock() + ". \nExpected " + blockType);
                        //player.sendMessage(errorMsg);
                        if (layer == 1) {
                            firstLayerBroken = true;
                            player.getInventory().placeItemBackInInventory(new ItemStack(Items.BEACON));
                        }
                        break layerLoop;
                    }
                    world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    radius = layer;
                }
            }
        }
        //System.out.println("Total layers in beacon: " + radius);
        //System.out.println("Beacon block type: " + blockType);
        //System.out.println("Sending to BeaconPacking.java for processing...");

        if (firstLayerBroken) {
            //System.out.println("First layer broke, not making beacon lol");
        } else BeaconPacking.packBeacon(player, radius, blockType);
    }
}
