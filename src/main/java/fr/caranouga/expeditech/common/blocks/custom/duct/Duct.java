package fr.caranouga.expeditech.common.blocks.custom.duct;

import fr.caranouga.expeditech.common.items.custom.DuctItem;
import fr.caranouga.expeditech.common.te.custom.duct.DuctTE;
import fr.caranouga.expeditech.common.utils.VoxelUtils;
import fr.caranouga.expeditech.common.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class Duct<D extends DuctTE<?, D>> extends Block implements IWaterLoggable {
    private final Class<D> ductClass;
    private final String type;
    private final DuctTier[] tiers;

    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<DuctTier> TIER = EnumProperty.create("tier", DuctTier.class);

    public static final VoxelShape SHAPE_NORTH = Block.box(6.5, 6.5, 0, 9.5, 9.5, 6.5);
    public static final VoxelShape SHAPE_SOUTH = Block.box(6.5, 6.5, 9.5, 9.5, 9.5, 16);
    public static final VoxelShape SHAPE_EAST = Block.box(9.5, 6.5, 6.5, 16, 9.5, 9.5);
    public static final VoxelShape SHAPE_WEST = Block.box(0, 6.5, 6.5, 6.5, 9.5, 9.5);
    public static final VoxelShape SHAPE_UP = Block.box(6.5, 9.5, 6.5, 9.5, 16, 9.5);
    public static final VoxelShape SHAPE_DOWN = Block.box(6.5, 0, 6.5, 9.5, 6.5, 9.5);
    public static final VoxelShape SHAPE_CORE = Block.box(6.5, 6.5, 6.5, 9.5, 9.5, 9.5);

    // TODO: Make the shape
    protected Duct(Class<D> ductClass, String type, DuctTier... tiers) {
        // TODO: Complete the properties
        super(Properties.of(Material.METAL));

        this.ductClass = ductClass;
        this.type = type;
        this.tiers = tiers;

        DuctTier defaultTier = Arrays.stream(tiers).findFirst().orElse(null);
        if(defaultTier == null) throw new RuntimeException("Tried to register a duct, but couldn't find a default tier");

        registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(WATERLOGGED, false)
                .setValue(TIER, defaultTier));
    }

    // region BlockState
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext pContext) {
        return getState(pContext.getLevel(), pContext.getClickedPos());
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED, TIER);
    }
    // endregion

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return getTileEntityType().create();
    }

    @Override
    public void onPlace(@Nonnull BlockState pState, @Nonnull World pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);

        if(pState.getBlock().equals(pOldState.getBlock())) return;

        if(!pLevel.isClientSide()){
            BlockState newState = getState(pLevel, pPos);
            if(newState != pState){
                pLevel.setBlock(pPos, newState, Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE);
            }
        }

        DuctTE<?, D> te = getTileEntity(pLevel, pPos);
        if(te == null) return;

        te.onPlaced();
    }

    @Override
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, world, pos, neighbor);
        // TODO: Check pk a chaque machine placé ya une nouvelle grid de créé
        DuctTE<?, D> te = getTileEntity(world, pos);
        if(te == null) return;

        TileEntity neighborTe = world.getBlockEntity(neighbor);
        Direction neighborSide = WorldUtils.getDirectionFrom(pos, neighbor);

        te.neighborChanged(neighborTe, neighbor, neighborSide);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos pos1, boolean b) {
        super.neighborChanged(state, world, pos, block, pos1, b);
        BlockState newState = getState(world, pos);
        if (!state.getProperties().stream().allMatch(property -> state.getValue(property).equals(newState.getValue(property)))) {
            world.setBlockAndUpdate(pos, getState(world, pos));
        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        stack.getOrCreateTag().putString(DuctItem.TIER_TAG, state.getValue(TIER).getName());

        return stack;
    }

    private BlockState getState(World world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        BlockState current = world.getBlockState(pos);
        
        if(!current.is(this)) return defaultBlockState();

        return defaultBlockState()
                .setValue(TIER, current.getValue(TIER))
                .setValue(UP, isAbleToConnect(world, pos, Direction.UP))
                .setValue(DOWN, isAbleToConnect(world, pos, Direction.DOWN))
                .setValue(NORTH, isAbleToConnect(world, pos, Direction.NORTH))
                .setValue(SOUTH, isAbleToConnect(world, pos, Direction.SOUTH))
                .setValue(EAST, isAbleToConnect(world, pos, Direction.EAST))
                .setValue(WEST, isAbleToConnect(world, pos, Direction.WEST))
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
    }

    public boolean isAbleToConnect(IWorldReader world, BlockPos pos, Direction facing) {
        BlockPos newPos = pos.relative(facing);

        // TODO: Check que c'est le même type de duct
        TileEntity te = world.getBlockEntity(newPos);
        if(te == null) return false;
        if(te.getCapability(getCapability(), facing).isPresent()) return true;
        if(!(te instanceof DuctTE)) return false;
        ResourceLocation registryName = te.getBlockState().getBlock().getRegistryName();
        if(registryName == null) return false;

        return registryName.equals(getRegistryName());
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.BLOCK;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return getShape(state);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return getShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return getShape(state);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, IBlockReader reader, BlockPos pos) {
        return getShape(state);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return getShape(state);
    }

    public VoxelShape getShape(BlockState state) {
        VoxelShape shape = SHAPE_CORE;
        if (state.getValue(UP)) shape = VoxelUtils.combine(shape, SHAPE_UP);
        if (state.getValue(DOWN)) shape = VoxelUtils.combine(shape, SHAPE_DOWN);
        if (state.getValue(SOUTH)) shape = VoxelUtils.combine(shape, SHAPE_SOUTH);
        if (state.getValue(NORTH)) shape = VoxelUtils.combine(shape, SHAPE_NORTH);
        if (state.getValue(EAST)) shape = VoxelUtils.combine(shape, SHAPE_EAST);
        if (state.getValue(WEST)) shape = VoxelUtils.combine(shape, SHAPE_WEST);

        return shape;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(WATERLOGGED)) {
            worldIn.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return getShape(state);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private DuctTE<?, D> getTileEntity(IWorldReader world, BlockPos pos){
        TileEntity te = world.getBlockEntity(pos);
        if(ductClass.isInstance(te)) return (DuctTE<?, D>) te;
        return null;
    }

    protected abstract TileEntityType<? extends DuctTE<?, D>> getTileEntityType();
    protected abstract Capability<?> getCapability();

    public String getType() {
        return type;
    }

    public DuctTier[] getTiers() {
        return tiers;
    }
}
