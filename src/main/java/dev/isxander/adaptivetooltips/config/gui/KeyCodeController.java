package dev.isxander.adaptivetooltips.config.gui;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class KeyCodeController implements Controller<Integer> {
    public static final ValueFormatter<Integer> DEFAULT_FORMATTER = code -> InputConstants.Type.KEYSYM.getOrCreate(code).getDisplayName();

    private final Option<Integer> option;
    private final ValueFormatter<Integer> valueFormatter;

    public KeyCodeController(Option<Integer> option) {
        this(option, DEFAULT_FORMATTER);
    }

    public KeyCodeController(Option<Integer> option, ValueFormatter<Integer> valueFormatter) {
        this.option = option;
        this.valueFormatter = valueFormatter;
    }

    @Override
    public Option<Integer> option() {
        return option;
    }

    @Override
    public Component formatValue() {
        return valueFormatter.format(option().pendingValue());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen yaclScreen, Dimension<Integer> dimension) {
        return new KeyCodeControllerElement(this, yaclScreen, dimension);
    }

    public static class KeyCodeControllerElement extends ControllerWidget<KeyCodeController> {
        private boolean awaitingKeyPress = false;

        private KeyCodeControllerElement(KeyCodeController control, YACLScreen screen, Dimension<Integer> dim) {
            super(control, screen, dim);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY) || !isAvailable()) {
                return false;
            }

            awaitingKeyPress = !awaitingKeyPress;
            return true;
        }

        @Override
        protected Component getValueText() {
            if (awaitingKeyPress)
                return Component.translatable("adaptivetooltips.gui.awaiting_key").withStyle(ChatFormatting.ITALIC);

            return super.getValueText();
        }

        @Override
        protected int getHoveredControlWidth() {
            return getUnhoveredControlWidth();
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (awaitingKeyPress) {
                control.option().requestSet(keyCode);
                awaitingKeyPress = false;
                return true;
            }

            return false;
        }
    }
}
