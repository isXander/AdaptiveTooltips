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
        int maxWidth = getMaxWidth(textRenderer, lines);

        int allowedMaxWidth = 0;
        switch (AdaptiveTooltipConfig.INSTANCE.getConfig().wrapText) {
            case OFF ->
                    allowedMaxWidth = Integer.MAX_VALUE; // max_value essentially bypasses wrapping using the check below
            case SCREEN_WIDTH ->
                    allowedMaxWidth = screen.width - 15; // minus 15 to add a bit of padding to each side
            case REMAINING_WIDTH -> {
                // can't rely on tooltip x as wrapping is calculated *before* tooltip positioners
                // any we can't use tooltip positioners early without height, which you get by wrapping! infinite loop!
                if (tooltipPositioner instanceof HoveredTooltipPositioner) {
                    allowedMaxWidth = screen.width - x - 15;

                    if (x + 12 + maxWidth > screen.width)
                        allowedMaxWidth = Math.max(allowedMaxWidth, x - 20);
                } else {
                    allowedMaxWidth = Integer.MAX_VALUE; // max_value essentially bypasses wrapping using the check below
                }
            }
            case HALF_SCREEN_WIDTH ->
                allowedMaxWidth = screen.width / 2;
        }

        // if max width is less than allowed max width, there is no need to do any wrapping
        if (maxWidth <= allowedMaxWidth)
            return lines.stream().map(Text::asOrderedText).collect(Collectors.toList());

        List<OrderedText> wrapped = new ArrayList<>();
        for (Text line : lines) {
            wrapped.addAll(textRenderer.wrapLines(line, allowedMaxWidth));
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
