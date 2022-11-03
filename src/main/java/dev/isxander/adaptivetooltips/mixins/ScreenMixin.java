package dev.isxander.adaptivetooltips.mixins;

import dev.isxander.adaptivetooltips.helpers.ScrollTracker;
import dev.isxander.adaptivetooltips.helpers.TooltipWrapper;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(Screen.class)
public class ScreenMixin {
    @Shadow public int width;
    @Shadow public int height;

    @Shadow protected TextRenderer textRenderer;

    @Unique
    private int debugify$modifiedX;

    @Unique
    private int debugify$modifiedY;

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

        if (AdaptiveTooltipConfig.getInstance().bedrockCentering)
            bedrockCenter(mouseX, mouseY, width, height, debugify$modifiedX, debugify$modifiedY);

        if (AdaptiveTooltipConfig.getInstance().bestCorner)
            bestCornerTooltip(mouseX, mouseY, width, height);

        scrollTooltip(components);

        if (AdaptiveTooltipConfig.getInstance().clampTooltip)
            clampTooltip(width, height);
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
        if (debugify$modifiedX < 5 || debugify$modifiedX + width > this.width - 5 || debugify$modifiedY < 5 || debugify$modifiedY + height > this.height - 5) {
            // find the least overlapping (over the mouse) corner to render the tooltip in
            TreeMap<Integer, Pair<Integer, Integer>> corners = new TreeMap<>(); // obstruction amt - x, y
            corners.put(Math.max((mouseX - (this.width - 5 - width)) * (5 + height - mouseY), -10), new Pair<>(this.width - 5 - width, 5)); // top right
            corners.put(Math.max((5 + width - mouseX) * (mouseY - (this.height - 5 - height)), -10), new Pair<>(5, this.height - 5 - height)); // bottom left
            corners.put(Math.max((mouseX - (this.width - 5 - width)) * (mouseY - (this.height - 5 - height)), -10), new Pair<>(this.width - 5 - width, this.height - 5 - height)); // bottom right
            corners.put(Math.max((5 + width - mouseX) * (5 + height - mouseY), -10), new Pair<>(5, 5)); // top left
            // top left must be put last to rely on that maps override identical keys so if each overlapping is less than -10 it uses top left

            Pair<Integer, Integer> bestCorner = corners.firstEntry().getValue();
            debugify$modifiedX = bestCorner.getLeft();
            debugify$modifiedY = bestCorner.getRight();
        }
    }

    private void clampTooltip(int width, int height) {
        debugify$modifiedX = MathHelper.clamp(debugify$modifiedX, 5, this.width - width - 5);
        debugify$modifiedY = MathHelper.clamp(debugify$modifiedY, 5, this.height - height - 5);
    }

    private void scrollTooltip(List<TooltipComponent> components) {
        ScrollTracker.resetIfNeeded(components);

        debugify$modifiedX += ScrollTracker.horizontalScroll * AdaptiveTooltipConfig.getInstance().horizontalScrollSensitivity;
        debugify$modifiedY += ScrollTracker.verticalScroll * AdaptiveTooltipConfig.getInstance().verticalScrollSensitivity;
    }

    /**
     * cursed modifyvariable because you can't modify localcaptures
     */
    @ModifyVariable(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal = 0), ordinal = 4)
    private int modifyX(int x) {
        return debugify$modifiedX;
    }

    @ModifyVariable(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal = 0), ordinal = 5)
    private int modifyY(int y) {
        return debugify$modifiedY;
    }
}
