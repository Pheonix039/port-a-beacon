package net.auuugh;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.text.Text;

public class BeaconScanner {
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if(world.isClient()) return ActionResult.PASS;

            //Get block position, what type of block, and block entity data
            BlockPos pos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(pos);
            BlockEntity entity = world.getBlockEntity(pos);

            //Player & Block check
            if(player.isSneaking() && state.isOf(Blocks.BEACON)) {
                pyramidScanner(world, pos, (ServerPlayerEntity) player);
                System.out.println("Beacon has been right clicked at " + pos + "!");
            }
            return ActionResult.PASS;
        });
    }

    static void pyramidScanner(World world, BlockPos beaconPos, ServerPlayerEntity player) {
        System.out.println("New Beacon scan at " + beaconPos);
        //vars
        int x = beaconPos.getX();
        int y = beaconPos.getY();
        int z = beaconPos.getZ();
        int radius = 0;
        int layerY;
        int xCoord;
        int zCoord;

        BlockState blockType = world.getBlockState(beaconPos.down());
        //System.out.println(blockType);


        layerLoop:
        for(int layer = 1; layer <= 4; layer++) {
            //temp
            layerY = y - layer;

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

                    //ends if any block found doesn't match blockType
                    if (state != blockType) {
                        System.out.println("Non Beacon block found at " + pos + ": " + state.getBlock());
                        System.out.println("Block expected: " + blockType);

                        Text errorMsg = Text.literal("Non Beacon block found at " + pos + ": " + state.getBlock() + ". \nExpected " + blockType);
                        player.sendMessage(errorMsg);
                        break layerLoop;
                    }
                    radius = layer;
                }
            }
        }
        System.out.println("Total layers in beacon: " + radius);
        System.out.println("Beacon block type: " + blockType);


    }
}
