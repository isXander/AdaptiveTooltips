package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.isxander.adaptivetooltips.helpers.ScrollTracker;
import dev.isxander.adaptivetooltips.helpers.TooltipWrapper;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
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

    @Unique private int debugify$modifiedX;
    @Unique private int debugify$modifiedY;
    @Unique private boolean debugify$preventVanillaRePos = false;

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

    @Inject(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void moveTooltip(MatrixStack matrices, List<TooltipComponent> components, int mouseX, int mouseY, CallbackInfo ci, int width, int height, int x, int y) {
        debugify$modifiedX = x;
        debugify$modifiedY = y;
        debugify$preventVanillaRePos = false;

        if (AdaptiveTooltipConfig.getInstance().prioritizeTooltipTop)
            prioritizeTooltipTop(height);

        if (AdaptiveTooltipConfig.getInstance().bedrockCentering)
            bedrockCenter(mouseX, mouseY, width, height, debugify$modifiedX, debugify$modifiedY);

        if (AdaptiveTooltipConfig.getInstance().bestCorner)
            bestCornerTooltip(mouseX, mouseY, width, height);

        if (AdaptiveTooltipConfig.getInstance().clampTooltip)
            clampTooltip(width, height);

        scrollTooltip(matrices, components);
    }

    private void bedrockCenter(int mouseX, int mouseY, int width, int height, int x, int y) {
        if (x < 4) {
            debugify$modifiedX = MathHelper.clamp(mouseX - width / 2, 6, this.width - width - 6);
            debugify$modifiedY = mouseY - height - 12;

            if (debugify$modifiedY < 6) {
                // find amount of obstruction to decide if it
                // is best to be above or below cursor
                var below = mouseY + 12;
                var belowObstruction = below + height - this.height;
                var aboveObstruction = -debugify$modifiedY;

                if (belowObstruction < aboveObstruction) {
                    debugify$modifiedY = below;
                }
            }
        } else if (y + height > this.height + 2) {
            debugify$modifiedY = Math.max(this.height - height - 4, 4);
        }
    }

    private void bestCornerTooltip(int mouseX, int mouseY, int width, int height) {
        if ((debugify$modifiedX < 4 || debugify$modifiedY < 4) || AdaptiveTooltipConfig.getInstance().alwaysBestCorner) {
            debugify$preventVanillaRePos = true;

            // find the least overlapping (over the mouse) corner to render the tooltip in
            TreeMap<Integer, Pair<Integer, Integer>> corners = new TreeMap<>(); // obstruction amt - x, y
            corners.put((int) Math.max(Math.max(mouseX - (this.width - 5 - width), 0) * Math.max(5 + height - mouseY, 0), -10), new Pair<>(this.width - 5 - width, 5)); // top right
            corners.put((int) Math.max(Math.max((5 + width - mouseX), 0) * Math.max(mouseY - (this.height - 5 - height), 0), -10), new Pair<>(5, this.height - 5 - height)); // bottom left
            corners.put((int) Math.max(Math.max(mouseX - (this.width - 5 - width), 0) * Math.max(mouseY - (this.height - 5 - height), 0), -10), new Pair<>(this.width - 5 - width, this.height - 5 - height)); // bottom right
            corners.put((int) Math.max(Math.max(5 + width - mouseX, 0) * Math.max(5 + height - mouseY, 0), -10), new Pair<>(5, 5)); // top left
            // top left must be put last to rely on that maps override identical keys so if each overlapping is less than -10 it uses top left

            corners.forEach((k, v) -> {
                System.out.println(k + " - " + v.getLeft() + ", " + v.getRight());
            });

            Pair<Integer, Integer> bestCorner = corners.firstEntry().getValue();
            debugify$modifiedX = bestCorner.getLeft();
            debugify$modifiedY = bestCorner.getRight();
        }
    }

    private void prioritizeTooltipTop(int height) {
        if (height > this.height) {
            debugify$modifiedY += height - this.height + 1;
        }
    }

    private void clampTooltip(int width, int height) {
        debugify$modifiedX = MathHelper.clamp(debugify$modifiedX, 5, this.width - width - 5);
        debugify$modifiedY = MathHelper.clamp(debugify$modifiedY, 5, this.height - height - 5);
    }

    private void scrollTooltip(MatrixStack matrices, List<TooltipComponent> components) {
        ScrollTracker.resetIfNeeded(components);
        ScrollTracker.tickAnimation(MinecraftClient.getInstance().getLastFrameDuration());

        matrices.translate(ScrollTracker.currentHorizontalScroll, ScrollTracker.currentVerticalScroll, 0);
    }

    /**
     * cursed modifyvariable because you can't modify localcaptures
     */
    @ModifyVariable(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal = 0, shift = At.Shift.AFTER), ordinal = 4)
    private int modifyX(int x) {
        return debugify$modifiedX;
    }

    @ModifyVariable(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal = 0, shift = At.Shift.AFTER), ordinal = 5)
    private int modifyY(int y) {
        return debugify$modifiedY;
    }

    @ModifyExpressionValue(method = "renderTooltipFromComponents", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/Screen;width:I", opcode = Opcodes.GETFIELD, ordinal = 0))
    private int shouldDoXRePos(int width) {
        if (debugify$preventVanillaRePos)
            return Integer.MAX_VALUE;
        return width;
    }

    @ModifyExpressionValue(method = "renderTooltipFromComponents", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/Screen;height:I", opcode = Opcodes.GETFIELD, ordinal = 0))
    private int shouldDoYRePos(int height) {
        if (debugify$preventVanillaRePos)
            return Integer.MAX_VALUE;
        return height;
    }

    @ModifyArgs(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(Lnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/BufferBuilder;IIIIIII)V"))
    private void changeTooltipColorAlpha(Args args) {
        Color colorStart = new Color(args.<Integer>get(7), true);
        args.set(7, new Color(colorStart.getRed(), colorStart.getGreen(), colorStart.getBlue(), (int) MathHelper.clamp(colorStart.getAlpha() * AdaptiveTooltipConfig.getInstance().tooltipTransparency, 0, 255)).getRGB());

        Color colorEnd = new Color(args.<Integer>get(8), true);
        args.set(8, new Color(colorEnd.getRed(), colorEnd.getGreen(), colorEnd.getBlue(), (int) MathHelper.clamp(colorEnd.getAlpha() * AdaptiveTooltipConfig.getInstance().tooltipTransparency, 0, 255)).getRGB());
    }
}
