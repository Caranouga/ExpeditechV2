package fr.caranouga.expeditech.common.items;

import fr.caranouga.expeditech.common.tab.ModTabs;
import fr.caranouga.expeditech.common.tileentities.custom.multiblock.AbstractMultiBlockMasterTile;
import fr.caranouga.expeditech.common.tileentities.custom.multiblock.TestMultiBlockSlaveTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WrenchItem extends Item {
    public WrenchItem() {
        super(new Properties().tab(ModTabs.EXPEDITECH).durability(60));
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        TileEntity tileEntity = context.getLevel().getBlockEntity(pos);
        World world = context.getLevel();
        Direction clickedFace = context.getClickedFace();
        PlayerEntity player = context.getPlayer();

        if(world.isClientSide()) return ActionResultType.PASS;

        AbstractMultiBlockMasterTile mbTile = null;
        if(tileEntity instanceof AbstractMultiBlockMasterTile){
            mbTile = (AbstractMultiBlockMasterTile) tileEntity;
        } else if (tileEntity instanceof TestMultiBlockSlaveTile) {
            TestMultiBlockSlaveTile slaveTile = (TestMultiBlockSlaveTile) tileEntity;

            mbTile = slaveTile.getMasterTile();
        }

        if(mbTile != null){
            if(mbTile.isBuilt()) return destroy(mbTile, player, stack, world, pos);
            return build(mbTile, clickedFace, player, stack, world, pos);
        }

        return super.onItemUseFirst(stack, context);
    }

    private ActionResultType build(AbstractMultiBlockMasterTile mbTile, Direction clickedFace, PlayerEntity player, ItemStack stack, World world, BlockPos pos){
        boolean success = mbTile.tryBuild(clickedFace.getOpposite());

        if(success){
            onActionSuccess(player, stack, world, pos);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    private ActionResultType destroy(AbstractMultiBlockMasterTile mbTile, PlayerEntity player, ItemStack stack, World world, BlockPos pos){
        boolean success = mbTile.unform();

        if(success){
            onActionSuccess(player, stack, world, pos);

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    private void onActionSuccess(PlayerEntity player, ItemStack stack, World world, BlockPos pos){
        if(player != null){
            stack.hurtAndBreak(1, player, (playerEntity) -> {
                playerEntity.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
            });
        }
    }
}
