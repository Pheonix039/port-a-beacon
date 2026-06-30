package net.auuugh;

import net.auuugh.beaconstuffs.BeaconPacking;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.auuugh.component.PortABeaconComponents.BEACON_BLOCKTYPE;
import static net.auuugh.component.PortABeaconComponents.BEACON_LAYERS;

public class BeaconScanner {
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            if(hand != Hand.MAIN_HAND) return ActionResult.PASS;

            //Get block position, what type of block, and block entity data
            ItemStack mainHandItem = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            // BlockEntity entity = world.getBlockEntity(pos);

            //Player & Block check
            if(player.isSneaking() && state.isOf(Blocks.BEACON)) {
                if (mainHandItem.isOf(Items.BEACON) && mainHandItem.getComponents().contains(BEACON_LAYERS) && mainHandItem.getComponents().contains(BEACON_BLOCKTYPE)) return ActionResult.SUCCESS;
                if(!world.isClient()) {
                    System.out.println("New Beacon scan at " + pos);
                    pyramidScanner(world, pos, (ServerPlayerEntity) player);
                }
                return ActionResult.SUCCESS;
            }
            if(world.isClient()) return ActionResult.PASS;
            return ActionResult.PASS;
        });
    }

    static void pyramidScanner(World world, BlockPos beaconPos, ServerPlayerEntity player) {
        //vars
        int x = beaconPos.getX();
        int y = beaconPos.getY();
        int z = beaconPos.getZ();
        int radius = 0;
        int layerY;
        int xCoord;
        int zCoord;
        boolean firstLayerBroken = false;

        world.setBlockState(beaconPos, Blocks.AIR.getDefaultState());

        BlockState blockTypeD = world.getBlockState(beaconPos.down());
        Identifier blockType = Registries.BLOCK.getId(blockTypeD.getBlock());
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
                    if ((!state.isOf(blockTypeD.getBlock()) || !state.isIn(BlockTags.BEACON_BASE_BLOCKS))) {
                        //System.out.println("Non Beacon block found at " + pos + ": " + state.getBlock());
                        //System.out.println("Block expected: " + blockType);
                        radius--;

                        //Text errorMsg = Text.literal("Non Beacon block found at " + pos + ": " + state.getBlock() + ". \nExpected " + blockType);
                        //player.sendMessage(errorMsg);
                        if (layer == 1) {
                            firstLayerBroken = true;
                            player.getInventory().offerOrDrop(new ItemStack(Items.BEACON));
                        }
                        break layerLoop;
                    }
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
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
