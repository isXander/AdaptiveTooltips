package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.adaptivetooltips.helpers.*;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.helpers.positioner.BedrockCenteringPositioner;
import dev.isxander.adaptivetooltips.helpers.positioner.BestCornerPositioner;
import dev.isxander.adaptivetooltips.helpers.positioner.PrioritizeTooltipTopPositioner;
import dev.isxander.adaptivetooltips.helpers.positioner.TooltipPositioner;
import net.minecraft.class_8000;
import net.minecraft.class_8001;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow public int width;
    @Shadow public int height;

    @Shadow protected TextRenderer textRenderer;

    @Redirect(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderOrderedTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"))
    private void wrapText(Screen instance, MatrixStack matrices, List<? extends OrderedText> lines, int x, int y, MatrixStack dontuse, Text text) {
        instance.renderOrderedTooltip(matrices, TooltipWrapper.wrapTooltipLines(instance, textRenderer, Collections.singletonList(text), x), x, y);
    }

    @Redirect(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderOrderedTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;II)V"))
    private void wrapTextList(Screen instance, MatrixStack matrices, List<? extends OrderedText> iHateOrderedText, int x, int y, MatrixStack dontuse, List<Text> lines) {
        instance.renderOrderedTooltip(matrices, TooltipWrapper.wrapTooltipLines(instance, textRenderer, lines, x), x, y);
    }

    @Redirect(method = "renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;map(Ljava/util/function/Function;)Ljava/util/stream/Stream;", ordinal = 0))
    private Stream<OrderedText> wrapTextListWidthData(Stream<Text> instance, Function<Text, OrderedText> function, MatrixStack matrices, List<Text> lines, Optional<TooltipData> data, int x, int y) {
        return TooltipWrapper.wrapTooltipLines((Screen) (Object) this, textRenderer, instance.toList(), x).stream();
    }

    @WrapOperation(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_8000;method_47944(Lnet/minecraft/client/gui/screen/Screen;IIII)Lorg/joml/Vector2ic;"))
    private Vector2ic moveTooltip(class_8000 clientTooltipPositioner, Screen screen, int x, int y, int width, int height, Operation<Vector2ic> operation, MatrixStack matrices, List<TooltipComponent> components, int mouseX, int mouseY) {
        Vector2ic currentPosition = operation.call(clientTooltipPositioner, screen, x, y, width, height);

        if (!(clientTooltipPositioner instanceof class_8001) && !AdaptiveTooltipConfig.INSTANCE.getConfig().applyTweaksToAllPositioners) // class_8001 = DefaultTooltipPositioner
            return currentPosition;

        for (TooltipPositioner tooltipPositioner : List.of(
                new PrioritizeTooltipTopPositioner(),
                new BedrockCenteringPositioner(),
                new BestCornerPositioner()
        )) {
            Optional<Vector2ic> position = tooltipPositioner.repositionTooltip(currentPosition.x(), currentPosition.y(), width, height, mouseX, mouseY, this.width, this.height);
            if (position.isPresent())
                currentPosition = position.get();
        }

        ScrollTracker.scroll(matrices, components, currentPosition.x(), currentPosition.y(), width, height, this.width, this.height);

        return currentPosition;
    }

    @ModifyArgs(method = "method_47943", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;fillGradient(Lorg/joml/Matrix4f;Lnet/minecraft/client/render/BufferBuilder;IIIIIII)V"))
    private static void changeTooltipColorAlpha(Args args) {
        Color colorStart = new Color(args.<Integer>get(7), true);
        args.set(7, new Color(
                colorStart.getRed(),
                colorStart.getGreen(),
                colorStart.getBlue(),
                (int) MathHelper.clamp(
                        colorStart.getAlpha() * AdaptiveTooltipConfig.INSTANCE.getConfig().tooltipTransparency,
                        0, 255)
        ).getRGB());

        Color colorEnd = new Color(args.<Integer>get(8), true);
        args.set(8, new Color(
                colorEnd.getRed(),
                colorEnd.getGreen(),
                colorEnd.getBlue(),
                (int) MathHelper.clamp(
                        colorEnd.getAlpha() * AdaptiveTooltipConfig.INSTANCE.getConfig().tooltipTransparency,
                        0, 255)
        ).getRGB());
    }
}
