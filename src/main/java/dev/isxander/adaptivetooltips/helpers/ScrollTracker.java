package dev.isxander.adaptivetooltips.helpers;

import dev.isxander.adaptivetooltips.mixins.BundleTooltipComponentAccessor;
import dev.isxander.adaptivetooltips.mixins.OrderedTextTooltipComponentAccessor;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.Iterator;
import java.util.List;

public class ScrollTracker {
    public static int verticalScroll = 0;
    public static int horizontalScroll = 0;

    private static List<TooltipComponent> trackedComponents = null;

    public static void reset() {
        verticalScroll = 0;
        horizontalScroll = 0;
    }

    public static void resetIfNeeded(List<TooltipComponent> components) {
        if (!isEqual(components, trackedComponents)) {
            reset();
        }

        trackedComponents = components;
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
