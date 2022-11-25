package dev.isxander.adaptivetooltips.helpers;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.text.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TooltipWrapper {
    public static List<OrderedText> wrapTooltipLines(Screen screen, TextRenderer textRenderer, List<? extends Text> lines, int x, TooltipPositioner tooltipPositioner) {
        if (lines.stream().allMatch(text -> text.getString().isBlank()))
            return List.of();
        int width = getMaxWidth(textRenderer, lines);

        int maxWidth = 0;
        switch (AdaptiveTooltipConfig.INSTANCE.getConfig().wrapText) {
            case OFF ->
                    maxWidth = Integer.MAX_VALUE;
            case SCREEN_WIDTH ->
                    maxWidth = screen.width - 15;
            case REMAINING_WIDTH -> {
                if (tooltipPositioner instanceof HoveredTooltipPositioner) {
                    maxWidth = screen.width - x - 15;

                    if (x + 12 + width > screen.width)
                        maxWidth = Math.max(maxWidth, x - 20);
                } else {
                    maxWidth = Integer.MAX_VALUE;
                }
            }
            case HALF_SCREEN_WIDTH ->
                maxWidth = screen.width / 2;
        }

        if (width <= maxWidth)
            return lines.stream().map(Text::asOrderedText).collect(Collectors.toList());

        List<OrderedText> wrapped = new ArrayList<>();
        for (Text line : lines) {
            wrapped.addAll(textRenderer.wrapLines(line, maxWidth));
        }

        return wrapped;
    }

    private static int getMaxWidth(TextRenderer textRenderer, List<? extends Text> lines) {
        int maxWidth = 0;

        for (Text line : lines) {
            int width = textRenderer.getWidth(line);
            if (width > maxWidth)
                maxWidth = width;
        }

        return maxWidth;
    }
}
