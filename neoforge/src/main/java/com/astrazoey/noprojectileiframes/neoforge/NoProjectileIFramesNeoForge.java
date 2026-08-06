package com.astrazoey.noprojectileiframes.neoforge;

import com.astrazoey.noprojectileiframes.CommonClass;
import com.astrazoey.noprojectileiframes.Constants;
import com.astrazoey.noprojectileiframes.IFramesHelper;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class NoProjectileIFramesNeoForge {

    public NoProjectileIFramesNeoForge(IEventBus modEventBus) {
        // Statically initialize in the register event, as per the project convention.
        // This mirrors how Fabric's ModInitializer#onInitialize triggers CommonClass.init().
        modEventBus.addListener(this::onRegister);
        NeoForge.EVENT_BUS.addListener(this::onDamagePost);
    }

    /**
     * Removes damage invulnerability (i-frames) against projectiles.
     * NeoForge rewrites LivingEntity.hurtServer, so the common
     * DamageLivingEntityMixin constant no longer exists there.
     * Instead, wait until the damage sequence has finished and overwrite
     * the entity's invulnerability fields with the configured value.
     */
    private void onDamagePost(LivingDamageEvent.Post event) {
        if (IFramesHelper.isProjectile(event.getSource())) {
            LivingEntity entity = event.getEntity();
            entity.invulnerableTime = IFramesHelper.getProjectileIFrames();
        }
    }

    private void onRegister(RegisterEvent event) {
        CommonClass.init();
    }
}
