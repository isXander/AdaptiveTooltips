package dev.isxander.adaptivetooltips.mixins;

import dev.isxander.adaptivetooltips.helpers.ScrollTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void checkTooltipRendered(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (!ScrollTracker.renderedThisFrame) {
            ScrollTracker.reset();
        }
        ScrollTracker.renderedThisFrame = false;
    }
}
