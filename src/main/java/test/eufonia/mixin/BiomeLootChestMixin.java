package test.eufonia.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import test.eufonia.EufoniaTest;
import test.eufonia.mod.commons.LootTableConfig;

import java.util.Optional;

@Mixin(ChestBlock.class)
public class BiomeLootChestMixin {

    @Inject(method = "onUse", at = @At("TAIL"))
    private void onChestOpened(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            RegistryEntry<Biome> biomeEntry = serverWorld.getBiome(pos);
            String b = biomeEntry.getKey().map(RegistryKey::getValue).map(Identifier::toString).orElse("default");
            String lt = LootTableConfig.getRandomLootTable(b);
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
                NbtCompound nbt = chestBlockEntity.createNbt();
                String customName = Optional.ofNullable(chestBlockEntity.getCustomName()).orElse(Text.of("")).getString();
                System.out.println();
                nbt.getKeys().forEach(s -> System.out.println("[NBT] " + s + ": " + nbt.getString(s)));
                System.out.println("[getCustomName() Method] Custom Name: " + customName);
                System.out.println();
                System.out.println("[BIOME] " + b);
                System.out.println();
                player.sendMessage(Text.literal("ID Bioma actual " + b), true);
                if (customName.contains("usado")) {
                    player.sendMessage(Text.literal("Este cofre ya ha sido utilizado y no generará más loot."));
                    return;
                }

                if (LootTableConfig.getLootTablesForBiome(b).isEmpty()) {
                    chestBlockEntity.setLootTable(null, 0);
                    player.sendMessage(Text.literal("El cofre está vacío debido a que el bioma no está registrado en el archivo JSON"));
                } else {
                    Identifier idLoot = new Identifier(lt);
                    //LootTable lootTable = world.getServer().getLootManager().getTable(idLoot);
                    chestBlockEntity.setLootTable(idLoot, world.getRandom().nextLong());
                    player.sendMessage(Text.literal(String.format("Como estás en el bioma %s al cofre se le asignó el loot table %s", b, lt)));
                }

                nbt.putBoolean("used", true);
                nbt.putString("usedStr", "true");
                chestBlockEntity.readNbt(nbt);
                chestBlockEntity.markDirty();
                chestBlockEntity.setCustomName(Text.literal(EufoniaTest.TEXT_USED_CHEST));
                world.updateListeners(pos, state, state, 3);
                serverWorld.getChunkManager().markForUpdate(pos);
            }
        }
    }
}
