package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.isxander.adaptivetooltips.AdaptiveTooltips;
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
        if (InputUtil.isKeyPressed(client.getWindow().getHandle(), ((KeyBindingAccessor) AdaptiveTooltips.SCROLL_BIND).getBoundKey().getCode())) {
            if (InputUtil.isKeyPressed(client.getWindow().getHandle(), ((KeyBindingAccessor) AdaptiveTooltips.HORIZONTAL_SCROLL_BIND).getBoundKey().getCode())) {
                ScrollTracker.horizontalScroll += (int) Math.signum(vertical);
            } else {
                ScrollTracker.verticalScroll += (int) Math.signum(vertical);
            }
            return false;
        }

        return true;
    }
}
