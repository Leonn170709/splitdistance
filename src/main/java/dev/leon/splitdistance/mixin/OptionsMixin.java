package dev.leon.splitdistance.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.leon.splitdistance.Cap;
import dev.leon.splitdistance.Config;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * getEffectiveRenderDistance() is min(your setting, server view-distance) and feeds the section
 * renderer, fog and culling. It is NOT what gets sent to the server -- Options#buildPlayerInformation
 * uses the raw renderDistance option -- so capping here shrinks what is drawn without shrinking what
 * is requested. The chunks still arrive, ClientChunkCache still holds them (its radius comes from the
 * server's SetChunkCacheRadius packet, not from this method), and map mods still see them.
 */
@Mixin(Options.class)
public class OptionsMixin {

    @Inject(method = "getEffectiveRenderDistance", at = @At("RETURN"), cancellable = true)
    private void splitdistance$capRenderDistance(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Cap.cap(
                cir.getReturnValueI(),
                Config.renderChunks(),
                RenderSystem.isOnRenderThread(),
                Config.threadGuard()));
    }
}
