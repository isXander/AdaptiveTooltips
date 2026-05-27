package dev.isxander.adaptivetooltips.helpers;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.config.ScrollDirection;
import dev.isxander.adaptivetooltips.mixins.BundleTooltipComponentAccessor;
import dev.isxander.adaptivetooltips.mixins.ClientTextTooltipAccessor;
import dev.isxander.adaptivetooltips.utils.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.List;

public class ScrollTracker {
    private static int targetVerticalScroll = 0;
    private static int targetHorizontalScroll = 0;

    private static float currentVerticalScroll = 0f;
    private static float currentHorizontalScroll = 0f;

    private static List<ClientTooltipComponent> trackedComponents = null;
    public static boolean renderedThisFrame = false;

    public static void addVerticalScroll(int amt) {
        if (AdaptiveTooltipConfig.HANDLER.instance().scrollDirection == ScrollDirection.NATURAL)
            amt = -amt;
        targetVerticalScroll += amt * AdaptiveTooltipConfig.HANDLER.instance().verticalScrollSensitivity;
    }

    public static void addHorizontalScroll(int amt) {
        if (AdaptiveTooltipConfig.HANDLER.instance().scrollDirection == ScrollDirection.NATURAL)
            amt = -amt;
        targetHorizontalScroll += amt * AdaptiveTooltipConfig.HANDLER.instance().horizontalScrollSensitivity;
    }

    public static float getVerticalScroll() {
        return currentVerticalScroll;
    }

    public static float getHorizontalScroll() {
        return currentHorizontalScroll;
    }

    public static void scroll(GuiGraphics graphics, List<ClientTooltipComponent> components, int x, int y, int width, int height, int screenWidth, int screenHeight) {
        tick(components, x, y, width, height, screenWidth, screenHeight, Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaTicks());

        // have to use a translate rather than moving the tooltip's x and y because int precision is too jittery
        graphics.pose().translate(ScrollTracker.getHorizontalScroll(), ScrollTracker.getVerticalScroll(), 0);
    }

    private static void tick(List<ClientTooltipComponent> components, int x, int y, int width, int height, int screenWidth, int screenHeight, float tickDelta) {
        renderedThisFrame = true;

        resetIfNeeded(components);

        // prevent scrolling if not needed, required for clamping to work without breaking every tooltip
        if (height < screenHeight)
            targetVerticalScroll = 0;
        if (width < screenWidth)
            targetHorizontalScroll = 0;

        // prevents scrolling too far up/down
        targetVerticalScroll = Mth.clamp(targetVerticalScroll, Math.min(screenHeight - (y + height) - 4, 0), Math.max(-y + 4, 0));
        targetHorizontalScroll = Mth.clamp(targetHorizontalScroll, Math.min(screenWidth - (x + width) - 4, 0), Math.max(-x + 4, 0));

        tickAnimation(tickDelta);
    }

    private static void tickAnimation(float tickDelta) {
        if (AdaptiveTooltipConfig.HANDLER.instance().smoothScrolling) {
            currentVerticalScroll = Mth.lerp(tickDelta * 0.5f, currentVerticalScroll, targetVerticalScroll);
            currentHorizontalScroll = Mth.lerp(tickDelta * 0.5f, currentHorizontalScroll, targetHorizontalScroll);
        } else {
            currentVerticalScroll = targetVerticalScroll;
            currentHorizontalScroll = targetHorizontalScroll;
        }
    }

    private static void resetIfNeeded(List<ClientTooltipComponent> components) {
        // if not the same component as last frame, reset the scrolling.
        if (!isEqual(components, trackedComponents)) {
            reset();
        }

        trackedComponents = components;
    }

    public static void reset() {
        targetVerticalScroll = targetHorizontalScroll = 0;
        // need to also reset the animation as it is funky upon next render
        currentVerticalScroll = currentHorizontalScroll = 0;
    }

    private static boolean isEqual(List<ClientTooltipComponent> l1, List<ClientTooltipComponent> l2) {
        if (l1 == null || l2 == null)
            return false;

        Iterator<ClientTooltipComponent> iter1 = l1.iterator();
        Iterator<ClientTooltipComponent> iter2 = l2.iterator();

        // loop through both lists until either ends
        while (iter1.hasNext() && iter2.hasNext()) {
            ClientTooltipComponent c1 = iter1.next();
            ClientTooltipComponent c2 = iter2.next();

            // if the components are same instance, they are the same, go to next element
            if (c1 == c2) continue;

            // no abstract way of comparing tooltip components so we have to check what implementation they are
            if (c1 instanceof ClientTextTooltip ot1 && c2 instanceof ClientTextTooltip ot2) {
                // OrderedText cannot be compared, MutableText can
                if (!TextUtil.toText(((ClientTextTooltipAccessor) ot1).getText()).equals(TextUtil.toText(((ClientTextTooltipAccessor) ot2).getText())))
                    return false;
            } else if (c1 instanceof ClientBundleTooltip bt1 && c2 instanceof ClientBundleTooltip bt2) {
                // gets the inventory of each bundle and loops through each stack
                
                Iterator<ItemStack> i1 = ((BundleTooltipComponentAccessor) bt1).getContents().items().iterator();
                Iterator<ItemStack> i2 = ((BundleTooltipComponentAccessor) bt2).getContents().items().iterator();

                // iterate through both bundle inventories until either runs out
                while (i1.hasNext() && i2.hasNext()) {
                    ItemStack stack1 = i1.next();
                    ItemStack stack2 = i2.next();

                    if (!ItemStack.matches(stack1, stack2))
                        return false;
                }
                
                // if either inventory has more items, we know they are not the same inventory
                if (i1.hasNext() || i2.hasNext())
                    return false;
            } else {
                // no other vanilla implementations of TooltipComponent or the two components are different to eachother
                return false;
            }
        }

        return !(iter1.hasNext() || iter2.hasNext());
    }
}
