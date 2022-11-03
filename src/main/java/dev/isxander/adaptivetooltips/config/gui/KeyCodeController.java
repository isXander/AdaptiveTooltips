package dev.isxander.adaptivetooltips.config.gui;

import dev.isxander.yacl.api.Controller;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.utils.Dimension;
import dev.isxander.yacl.gui.AbstractWidget;
import dev.isxander.yacl.gui.YACLScreen;
import dev.isxander.yacl.gui.controllers.BooleanController;
import dev.isxander.yacl.gui.controllers.ControllerWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Function;

public class KeyCodeController implements Controller<Integer> {
    public static final Function<Integer, Text> DEFAULT_FORMATTER = code -> InputUtil.Type.KEYSYM.createFromCode(code).getLocalizedText();

    private final Option<Integer> option;
    private final Function<Integer, Text> valueFormatter;

    public KeyCodeController(Option<Integer> option) {
        this(option, DEFAULT_FORMATTER);
    }

    public KeyCodeController(Option<Integer> option, Function<Integer, Text> valueFormatter) {
        this.option = option;
        this.valueFormatter = valueFormatter;
    }

    @Override
    public Option<Integer> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        return valueFormatter.apply(option().pendingValue());
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
        protected void drawHoveredControl(MatrixStack matrices, int mouseX, int mouseY, float delta) {

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
        protected Text getValueText() {
            if (awaitingKeyPress)
                return Text.translatable("adaptivetooltips.gui.awaiting_key").formatted(Formatting.ITALIC);

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
