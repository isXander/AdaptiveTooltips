package dev.isxander.adaptivetooltips.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(Tooltip.class)
public class TooltipMixin {
    @WrapOperation(method = "splitTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;split(Lnet/minecraft/network/chat/FormattedText;I)Ljava/util/List;"))
    private static List<FormattedCharSequence> preventWrapping(Font instance, FormattedText text, int width, Operation<List<FormattedCharSequence>> operation) {
        if (AdaptiveTooltipConfig.HANDLER.instance().overwriteVanillaWrapping)
            return List.of(Language.getInstance().getVisualOrder(text));
        return operation.call(instance, text, width);
    }
}
