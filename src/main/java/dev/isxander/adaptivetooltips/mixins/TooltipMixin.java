package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Tooltip.class)
public class TooltipMixin {
    @WrapOperation(method = "wrapLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;wrapLines(Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;"))
    private static List<OrderedText> preventWrapping(TextRenderer instance, StringVisitable text, int width, Operation<List<OrderedText>> operation) {
        if (AdaptiveTooltipConfig.INSTANCE.getConfig().preventVanillaWrapping)
            return List.of(Language.getInstance().reorder(text));
        return operation.call(instance, text, width);
    }
}
