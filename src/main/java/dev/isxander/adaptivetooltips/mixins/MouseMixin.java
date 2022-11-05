package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.helpers.ScrollTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @WrapWithCondition(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"))
    private boolean trackMouseWheel(Screen screen, double mouseX, double mouseY, double amount, long window, double horizontal, double vertical) {
        if (InputUtil.isKeyPressed(client.getWindow().getHandle(), AdaptiveTooltipConfig.getInstance().scrollKeyCode)) {
            if (InputUtil.isKeyPressed(client.getWindow().getHandle(), AdaptiveTooltipConfig.getInstance().horizontalScrollKeyCode)) {
                ScrollTracker.addHorizontalScroll((int) Math.signum(vertical));
            } else {
                ScrollTracker.addVerticalScroll((int) Math.signum(vertical));
                ScrollTracker.addHorizontalScroll((int) Math.signum(horizontal));
            }
            return false;
        }

        return true;
    }
}
