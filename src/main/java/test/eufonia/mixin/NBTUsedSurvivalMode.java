package test.eufonia.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import test.eufonia.EufoniaTest;

@Mixin(ChestBlock.class)
public class NBTUsedSurvivalMode {

    @Inject(method = "onPlaced", at = @At("TAIL"))
    private void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if (!world.isClient && placer instanceof PlayerEntity player) {
            if (!player.getAbilities().creativeMode) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
                    NbtCompound nbt = chestBlockEntity.createNbt();
                    nbt.putBoolean("used", true);
                    nbt.putString("usedStr", "true");
                    itemStack.setNbt(nbt);
                    chestBlockEntity.readNbt(nbt);
                    chestBlockEntity.markDirty();
                    chestBlockEntity.setCustomName(Text.literal(EufoniaTest.TEXT_USED_CHEST));
                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.getChunkManager().markForUpdate(pos);
                    }
                }
            }
        }
    }

}
