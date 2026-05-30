/*
 * Copyright (C) 2026 isXander
 *
 * This file is part of Adaptive Tooltips.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.helpers.ScrollTracker;
import dev.isxander.adaptivetooltips.helpers.TooltipWrapper;
import dev.isxander.adaptivetooltips.helpers.positioner.BedrockCenteringPositionModule;
import dev.isxander.adaptivetooltips.helpers.positioner.BestCornerPositionModule;
import dev.isxander.adaptivetooltips.helpers.positioner.PrioritizeTooltipTopPositionModule;
import dev.isxander.adaptivetooltips.helpers.positioner.TooltipPositionModule;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;

import org.joml.Matrix3x2fStack;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {
    @Shadow
    public abstract Matrix3x2fStack pose();

    @Shadow public abstract int guiWidth();

    @Shadow public abstract int guiHeight();

    // WARNING: Using `tooltip*` instead of `tooltip` because NeoForge patches the
    // canonical method will an extra argument at the ent.

    // wrapping
    @ModifyVariable(method = "tooltip*", at = @At("HEAD"), argsOnly = true, name = "lines")
    private List<ClientTooltipComponent> modifyTooltip(
            List<ClientTooltipComponent> lines,
            @Local(argsOnly = true, name = "font") Font font,
            @Local(argsOnly = true, name = "xo") int xo,
            @Local(argsOnly = true, name = "yo") int yo,
            @Local(argsOnly = true, name = "positioner") ClientTooltipPositioner positioner
    ) {
        return TooltipWrapper.wrapComponents(lines, font, this.guiWidth(), this.guiHeight(), xo, positioner);
    }

    @WrapOperation(
            method = "tooltip*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"
            )
    )
    private Vector2ic moveTooltip(
            ClientTooltipPositioner positioner,
            int screenWidth, int screenHeight,
            int x, int y,
            int width, int height,
            Operation<Vector2ic> operation,
            @Local(argsOnly = true, name = "lines") List<ClientTooltipComponent> lines,
            @Local(argsOnly = true, name = "xo") int xo,
            @Local(argsOnly = true, name = "yo") int yo
    ) {
        Vector2ic currentPosition = operation.call(positioner, screenWidth, screenHeight, x, y, width, height);

        pose().pushMatrix(); // injection is before matrices.push()

        if (!(positioner instanceof DefaultTooltipPositioner) && AdaptiveTooltipConfig.HANDLER.instance().onlyRepositionHoverTooltips)
            return currentPosition;

        for (TooltipPositionModule tooltipPositionModule : List.of(
                new PrioritizeTooltipTopPositionModule(),
                new BedrockCenteringPositionModule(),
                new BestCornerPositionModule()
        )) {
            Optional<Vector2ic> position = tooltipPositionModule.repositionTooltip(currentPosition.x(), currentPosition.y(), width, height, xo, yo, this.guiWidth(), this.guiHeight());
            if (position.isPresent())
                currentPosition = position.get();
        }

        ScrollTracker.scroll((GuiGraphicsExtractor) (Object) this, lines, currentPosition.x(), currentPosition.y(), width, height, screenWidth, screenHeight);

        return currentPosition;
    }

    @Inject(
            method = "tooltip*",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Matrix3x2fStack;popMatrix()Lorg/joml/Matrix3x2fStack;",
                    ordinal = 0
            )
    )
    private void closeCustomMatrices(CallbackInfo ci) {
        pose().popMatrix();
    }

    @ModifyExpressionValue(method = "tooltip*", at = @At(value = "CONSTANT", args = "intValue=2"))
    private int removeFirstLinePadding(int padding) {
        return AdaptiveTooltipConfig.HANDLER.instance().removeFirstLinePadding ? 0 : padding;
    }
}
