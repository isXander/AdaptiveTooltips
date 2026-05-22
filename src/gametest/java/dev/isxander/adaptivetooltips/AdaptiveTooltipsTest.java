package dev.isxander.adaptivetooltips;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import dev.isxander.adaptivetooltips.config.WrapTextBehaviour;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;

import java.util.function.BiConsumer;

@SuppressWarnings("UnstableApiUsage")
public class AdaptiveTooltipsTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getClientWorld().waitForChunksRender();

            context.getInput().pressKey(options -> options.keyInventory);
            context.waitForScreen(InventoryScreen.class);
            context.waitTick();

            IntIntPair pos = context.computeOnClient(minecraft -> {
                AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) minecraft.screen;
                if (screen == null) {
                    throw new IllegalStateException("Screen is null");
                }

                Slot slot = screen.getMenu().getSlot(36);
                int guiScale = minecraft.getWindow().getGuiScale();
                int leftPos = (screen.width - 176) / 2;
                int topPos = (screen.height - 166) / 2;
                return IntIntPair.of((leftPos + slot.x + 8) * guiScale, (topPos + slot.y + 8) * guiScale);
            });
            context.getInput().setCursorPos(pos.firstInt(), pos.secondInt());

            testWrapText(context, singleplayer);
            testPrioritizeTooltipTop(context, singleplayer);
            testBedrockCentering(context, singleplayer);
            testBestCorner(context, singleplayer);
            testTransparency(context, singleplayer);
        }
    }

    private static void testWrapText(ClientGameTestContext context, TestSingleplayerContext singleplayer) {
        testWithConfig(
                context, singleplayer,
                "wrap_text", (config, value) -> config.wrapText = value,
                "'This is a very long tooltip line that should definitely be wrapped because it contains so many words that it exceeds the screen width and will need to be broken into multiple lines by the adaptive tooltips mod.'",
                WrapTextBehaviour.SCREEN_WIDTH, WrapTextBehaviour.REMAINING_WIDTH, WrapTextBehaviour.OFF
        );
    }

    private static void testPrioritizeTooltipTop(ClientGameTestContext context, TestSingleplayerContext singleplayer) {
        testWithConfig(
                context, singleplayer,
                "prioritize_tooltip_top", (config, value) -> config.prioritizeTooltipTop = value,
                "'Line 1','Line 2','Line 3','Line 4','Line 5','Line 6','Line 7','Line 8','Line 9','Line 10','Line 11','Line 12','Line 13','Line 14','Line 15','Line 16','Line 17','Line 18','Line 19','Line 20','Line 21','Line 22','Line 23','Line 24','Line 25'",
                true, false
        );
    }

    private static void testBedrockCentering(ClientGameTestContext context, TestSingleplayerContext singleplayer) {
        testWithConfig(
                context, singleplayer,
                "bedrock_centering", (config, value) -> config.bedrockCentering = value,
                "'This is a very long tooltip line that should definitely be wrapped because it contains so many words that it exceeds the screen width and will need to be broken into multiple lines by the adaptive tooltips mod.'",
                true, false
        );
    }

    private static void testBestCorner(ClientGameTestContext context, TestSingleplayerContext singleplayer) {
        String lore = "There is a lot of text to type here so I am just going to repeat it a bunch of times because I couldn\\'t write a paragraph the size of this whole screen.";
        lore = String.join(" ", lore, lore, lore, lore, lore, lore);

        AdaptiveTooltipConfig.HANDLER.instance().bedrockCentering = false;
        testWithConfig(
                context, singleplayer,
                "best_corner", (config, value) -> config.bestCorner = value,
                "'" + lore + "'",
                false, true
        );
        AdaptiveTooltipConfig.HANDLER.instance().bedrockCentering = true;
    }

    private static void testTransparency(ClientGameTestContext context, TestSingleplayerContext singleplayer) {
        testWithConfig(
                context, singleplayer,
                "transparency", (config, value) -> config.tooltipTransparency = value,
                "'This is for an example of tooltip transparency.'",
                1f, 0.5f
        );
    }

    @SafeVarargs
    private static <T> void testWithConfig(ClientGameTestContext context, TestSingleplayerContext singleplayer, String name, BiConsumer<AdaptiveTooltipConfig, T> config, String lore, T... values) {
        singleplayer.getServer().runCommand("clear @a");
        singleplayer.getServer().runCommand("give @a dirt[lore=[" + lore + "]]");
        context.waitTick();

        for (T value : values) {
            config.accept(AdaptiveTooltipConfig.HANDLER.instance(), value);
            context.waitTick();
            context.assertScreenshotEquals(name + '_' + value.toString().toLowerCase());
        }

        config.accept(AdaptiveTooltipConfig.HANDLER.instance(), values[0]);
    }
}
