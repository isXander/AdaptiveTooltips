package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.helpers.ScrollTracker;
import dev.isxander.adaptivetooltips.helpers.TooltipWrapper;
import dev.isxander.adaptivetooltips.helpers.positioner.BedrockCenteringPositionModule;
import dev.isxander.adaptivetooltips.helpers.positioner.BestCornerPositionModule;
import dev.isxander.adaptivetooltips.helpers.positioner.PrioritizeTooltipTopPositionModule;
import dev.isxander.adaptivetooltips.helpers.positioner.TooltipPositionModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {
    @Shadow
    public abstract PoseStack pose();

    @Shadow public abstract int guiWidth();

    @Shadow public abstract int guiHeight();

    // wrapping
    @ModifyVariable(method = "renderTooltipInternal", at = @At("HEAD"), argsOnly = true)
    private List<ClientTooltipComponent> modifyTooltip(List<ClientTooltipComponent> tooltip, @Local(argsOnly = true) Font font, @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true) ClientTooltipPositioner positioner) {
        return TooltipWrapper.wrapComponents(tooltip, font, this.guiWidth(), this.guiHeight(), x, positioner);
    }

    @WrapOperation(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"))
    private Vector2ic moveTooltip(ClientTooltipPositioner positioner, int screenWidth, int screenHeight, int x, int y, int width, int height, Operation<Vector2ic> operation, @Local(argsOnly = true) List<ClientTooltipComponent> tooltip, @Local(argsOnly = true, ordinal = 0) int mouseX, @Local(argsOnly = true, ordinal = 1) int mouseY) {
        Vector2ic currentPosition = operation.call(positioner, screenWidth, screenHeight, x, y, width, height);

        pose().pushPose(); // injection is before matrices.push()

        if (!(positioner instanceof DefaultTooltipPositioner) && AdaptiveTooltipConfig.HANDLER.instance().onlyRepositionHoverTooltips)
            return currentPosition;

        for (TooltipPositionModule tooltipPositionModule : List.of(
                new PrioritizeTooltipTopPositionModule(),
                new BedrockCenteringPositionModule(),
                new BestCornerPositionModule()
        )) {
            Optional<Vector2ic> position = tooltipPositionModule.repositionTooltip(currentPosition.x(), currentPosition.y(), width, height, mouseX, mouseY, this.guiWidth(), this.guiHeight());
            if (position.isPresent())
                currentPosition = position.get();
        }

        ScrollTracker.scroll((GuiGraphics) (Object) this, tooltip, currentPosition.x(), currentPosition.y(), width, height, screenWidth, screenHeight);

        return currentPosition;
    }

    @Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V", ordinal = 0))
    private void closeCustomMatrices(CallbackInfo ci) {
        pose().popPose();
    }

    @ModifyExpressionValue(method = "renderTooltipInternal", at = @At(value = "CONSTANT", args = "intValue=2"))
    private int removeFirstLinePadding(int padding) {
        return AdaptiveTooltipConfig.HANDLER.instance().removeFirstLinePadding ? 0 : padding;
    }
}
