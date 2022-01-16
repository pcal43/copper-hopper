package net.pcal.nospawnzone.init.fabric.mixins;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mixin(SpawnHelper.class)
public abstract class MobStopMixin {

    private static final Logger LOGGER = LogManager.getLogger(MobStopMixin.class);

    @Inject(method = "canSpawn(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/SpawnGroup;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/biome/SpawnSettings$SpawnEntry;Lnet/minecraft/util/math/BlockPos$Mutable;D)Z", at = @At("RETURN"), cancellable = true)
    private static void onCanSpawn_nBXjeY(ServerWorld world, SpawnGroup group, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, SpawnSettings.SpawnEntry spawnEntry, BlockPos.Mutable pos, double squaredDistance, CallbackInfoReturnable<Boolean> returnable) {
        //if (returnable.getReturnValue() && PeaceAPI.filterEntity(FabricPeacefulSurface.wrapEntity(spawnEntry.type), FabricPeacefulSurface.wrapWorld(world), pos.getX(), pos.getY(), pos.getZ()))
        LOGGER.info("[MobStop] preventing spawn of "+spawnEntry.type);
        returnable.setReturnValue(false);
    }

}
