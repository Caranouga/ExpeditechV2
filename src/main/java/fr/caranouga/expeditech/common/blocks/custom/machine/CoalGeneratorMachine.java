package fr.caranouga.expeditech.common.blocks.custom.machine;

import fr.caranouga.expeditech.Expeditech;
import fr.caranouga.expeditech.common.blocks.custom.MachineBlock;
import fr.caranouga.expeditech.common.containers.CoalGeneratorMachineContainer;
import fr.caranouga.expeditech.common.tileentities.ModTileEntities;
import fr.caranouga.expeditech.common.tileentities.custom.machine.CoalGeneratorMachineTE;
import fr.caranouga.expeditech.common.tileentities.custom.machine.MachineTE;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CoalGeneratorMachine extends MachineBlock {
    public CoalGeneratorMachine() {
        super(Properties.of(Material.METAL));
    }

    @Override
    protected TileEntityType<? extends MachineTE> getTileEntityType() {
        return ModTileEntities.COAL_GENERATOR.get();
    }


    // TODO: Faire en sorte que quand la machine est allumé, elle émette de la lumière





    @Override
    public ActionResultType use(BlockState pState, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockRayTraceResult pHit) {
        if(!pLevel.isClientSide()) {
            TileEntity tileEntity = pLevel.getBlockEntity(pPos);

            if(tileEntity instanceof CoalGeneratorMachineTE){
                INamedContainerProvider containerProvider = createContainerProvider(pLevel, pPos);

                NetworkHooks.openGui((ServerPlayerEntity) pPlayer, containerProvider, tileEntity.getBlockPos());
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World pLevel, BlockPos pPos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen." + Expeditech.MODID + "." + "coal_generator");
            }

            @Nullable
            @Override
            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return getContainer(i, pLevel, pPos, playerInventory, playerEntity);
            }
        };
    }

    protected Container getContainer(int i, World pLevel, BlockPos pPos, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new CoalGeneratorMachineContainer(i, pLevel, pPos, playerInventory, playerEntity);
    }
}
