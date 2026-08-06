package com.astrazoey.noprojectileiframes;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;

/**
 * Shared helper for adjusting projectile damage invulnerability ticks.
 * Used by both the Fabric/vanilla mixin and the NeoForge event listener.
 */
public class IFramesHelper {

    /**
     * @return true if the given damage source is a projectile
     */
    public static boolean isProjectile(DamageSource source) {
        return source.is(DamageTypeTags.IS_PROJECTILE);
    }

    /**
     * @return the configured projectile i-frame count, clamped to a non-negative value
     */
    public static int getProjectileIFrames() {
        NoProjectileIFramesConfig config = CommonClass.getConfig();
        int ticks = config != null ? config.getProjectileIframes() : 0;
        return Math.max(0, ticks);
    }
}
