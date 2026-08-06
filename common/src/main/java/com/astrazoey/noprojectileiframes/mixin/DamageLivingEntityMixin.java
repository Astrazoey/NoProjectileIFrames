package com.astrazoey.noprojectileiframes.mixin;

import com.astrazoey.noprojectileiframes.IFramesHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class DamageLivingEntityMixin {

    /**
     * Removes damage invulnerability (i-frames) against projectiles.
     * Only applies on Fabric/vanilla: NeoForge rewrites hurtServer so the
     * constant 20 no longer exists there; the NeoForge side is handled by
     * a LivingIncomingDamageEvent listener in NoProjectileIFramesNeoForge instead.
     */
    @ModifyConstant(method = "hurtServer", constant = @Constant(intValue = 20, ordinal = 0), require = 0)
    public int changeIFrames(int constant, ServerLevel level, DamageSource source, float amount) {
        return IFramesHelper.isProjectile(source)
                ? IFramesHelper.getProjectileIFrames()
                : constant;
    }

}

