package dev.isxander.adaptivetooltips.helpers;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.config.ScrollDirection;
import dev.isxander.adaptivetooltips.mixins.BundleTooltipComponentAccessor;
import dev.isxander.adaptivetooltips.mixins.OrderedTextTooltipComponentAccessor;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;
import java.util.List;

public class ScrollTracker {
    private static int targetVerticalScroll = 0;
    private static int targetHorizontalScroll = 0;

    private static float currentVerticalScroll = 0f;
    private static float currentHorizontalScroll = 0f;

    private static List<TooltipComponent> trackedComponents = null;
    public static boolean renderedThisFrame = false;

    public static void addVerticalScroll(int amt) {
        if (AdaptiveTooltipConfig.getInstance().scrollDirection == ScrollDirection.NATURAL)
            amt = -amt;
        targetVerticalScroll += amt * AdaptiveTooltipConfig.getInstance().verticalScrollSensitivity;
    }

    public static void addHorizontalScroll(int amt) {
        if (AdaptiveTooltipConfig.getInstance().scrollDirection == ScrollDirection.NATURAL)
            amt = -amt;
        targetHorizontalScroll += amt * AdaptiveTooltipConfig.getInstance().horizontalScrollSensitivity;
    }

    public static float getVerticalScroll() {
        return currentVerticalScroll;
    }

    public static float getHorizontalScroll() {
        return currentHorizontalScroll;
    }

    public static void tick(List<TooltipComponent> components, int x, int y, int width, int height, int screenWidth, int screenHeight, float tickDelta) {
        renderedThisFrame = true;

        resetIfNeeded(components);

        if (height < screenHeight)
            targetVerticalScroll = 0;
        if (width < screenWidth)
            targetHorizontalScroll = 0;

        targetVerticalScroll = MathHelper.clamp(targetVerticalScroll, Math.min(screenHeight - (y + height) - 4, 0), Math.max(-y, 0));
        targetHorizontalScroll = MathHelper.clamp(targetHorizontalScroll, Math.min(screenWidth - (x + width) - 4, 0), Math.max(-x, 0));

        tickAnimation(tickDelta);
    }

    public static void tickAnimation(float tickDelta) {
        if (AdaptiveTooltipConfig.getInstance().smoothScrolling) {
            currentVerticalScroll = MathHelper.lerp(tickDelta * 0.5f, currentVerticalScroll, targetVerticalScroll);
            currentHorizontalScroll = MathHelper.lerp(tickDelta * 0.5f, currentHorizontalScroll, targetHorizontalScroll);
        } else {
            currentVerticalScroll = targetVerticalScroll;
            currentHorizontalScroll = targetHorizontalScroll;
        }
    }

    public static void resetIfNeeded(List<TooltipComponent> components) {
        if (!isEqual(components, trackedComponents)) {
            reset();
        }

        trackedComponents = components;
    }

    public static void reset() {
        targetVerticalScroll = targetHorizontalScroll = 0;
        currentVerticalScroll = currentHorizontalScroll = 0;
    }

    private static boolean isEqual(List<TooltipComponent> l1, List<TooltipComponent> l2) {
        if (l1 == null || l2 == null)
            return false;

        Iterator<TooltipComponent> iter1 = l1.iterator();
        Iterator<TooltipComponent> iter2 = l2.iterator();

        while (iter1.hasNext() && iter2.hasNext()) {
            TooltipComponent c1 = iter1.next();
            TooltipComponent c2 = iter2.next();

            if (c1 == c2) continue;

            if (c1 instanceof OrderedTextTooltipComponent ot1 && c2 instanceof OrderedTextTooltipComponent ot2) {
                if (!toText(((OrderedTextTooltipComponentAccessor) ot1).getText()).equals(toText(((OrderedTextTooltipComponentAccessor) ot2).getText())))
                    return false;
            } else if (c1 instanceof BundleTooltipComponent bt1 && c2 instanceof BundleTooltipComponent bt2) {
                Iterator<ItemStack> i1 = ((BundleTooltipComponentAccessor) bt1).getInventory().iterator();
                Iterator<ItemStack> i2 = ((BundleTooltipComponentAccessor) bt2).getInventory().iterator();

                while (i1.hasNext() && i2.hasNext()) {
                    ItemStack stack1 = i1.next();
                    ItemStack stack2 = i2.next();

                    if (!ItemStack.areEqual(stack1, stack2))
                        return false;
                }

                if (i1.hasNext() || i2.hasNext())
                    return false;
            } else {
                return false;
            }
        }

        return !(iter1.hasNext() || iter2.hasNext());
    }

    private static MutableText toText(OrderedText orderedText) {
        MutableText text = Text.empty();

        orderedText.accept((idx, style, codePoint) -> {
            text.append(Text.literal(Character.toString(codePoint)).setStyle(style));
            return true;
        });

        return text;
    }
}
