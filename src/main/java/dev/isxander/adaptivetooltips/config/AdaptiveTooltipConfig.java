package dev.isxander.adaptivetooltips.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import dev.isxander.adaptivetooltips.AdaptiveTooltips;
import dev.isxander.adaptivetooltips.config.gui.KeyCodeController;
import dev.isxander.yacl.api.*;
import dev.isxander.yacl.gui.controllers.LabelController;
import dev.isxander.yacl.gui.controllers.TickBoxController;
import dev.isxander.yacl.gui.controllers.cycling.EnumController;
import dev.isxander.yacl.gui.controllers.slider.IntegerSliderController;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AdaptiveTooltipConfig {
    private static final Logger logger = LoggerFactory.getLogger("AdaptiveTooltips");
    private static final Path path = FabricLoader.getInstance().getConfigDir().resolve("adaptive-tooltips.json");
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private static AdaptiveTooltipConfig instance = null;
    public static final AdaptiveTooltipConfig DEFAULTS = new AdaptiveTooltipConfig();

    @Expose public WrapTextBehaviour wrapText = WrapTextBehaviour.SCREEN_WIDTH;
    @Expose public boolean bedrockCentering = true;
    @Expose public boolean bestCorner = true;
    @Expose public boolean clampTooltip = false;
    @Expose public int scrollKeyCode = InputUtil.GLFW_KEY_LEFT_ALT;
    @Expose public int horizontalScrollKeyCode = InputUtil.GLFW_KEY_LEFT_CONTROL;
    @Expose public int verticalScrollSensitivity = 3;
    @Expose public int horizontalScrollSensitivity = 3;

    public void save() {
        try {
            logger.info("Saving AdaptiveTooltip config...");
            Files.writeString(path, gson.toJson(this), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            if (Files.notExists(path)) {
                getInstance().save();
                return;
            }

            logger.info("Loading AdaptiveTooltip config...");
            instance = gson.fromJson(Files.readString(path), AdaptiveTooltipConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AdaptiveTooltipConfig getInstance() {
        if (instance == null)
            instance = new AdaptiveTooltipConfig();

        return instance;
    }

    public Screen makeScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("adaptivetooltips.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("adaptivetooltips.title"))
                        .group(OptionGroup.createBuilder()
                                .option(Option.createBuilder(WrapTextBehaviour.class)
                                        .name(Text.translatable("adaptivetooltips.opt.text_wrapping.title"))
                                        .tooltip(Text.translatable("adaptivetooltips.opt.text_wrapping.desc"))
                                        .binding(
                                                DEFAULTS.wrapText,
                                                () -> wrapText,
                                                val -> wrapText = val
                                        )
                                        .controller(EnumController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .option(Option.createBuilder(boolean.class)
                                        .name(Text.translatable("adaptivetooltips.opt.bedrock_centering.title"))
                                        .tooltip(Text.translatable("adaptivetooltips.opt.bedrock_centering.desc"))
                                        .binding(
                                                DEFAULTS.bedrockCentering,
                                                () -> bedrockCentering,
                                                val -> bedrockCentering = val
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(Text.translatable("adaptivetooltips.opt.align_to_corner.title"))
                                        .tooltip(Text.translatable("adaptivetooltips.opt.align_to_corner.desc"))
                                        .binding(
                                                DEFAULTS.bestCorner,
                                                () -> bestCorner,
                                                val -> bestCorner = val
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .option(Option.createBuilder(boolean.class)
                                        .name(Text.translatable("adaptivetooltips.opt.clamp_tooltip_pos.title"))
                                        .tooltip(Text.translatable("adaptivetooltips.opt.clamp_tooltip_pos.desc"))
                                        .binding(
                                                DEFAULTS.clampTooltip,
                                                () -> clampTooltip,
                                                val -> clampTooltip = val
                                        )
                                        .controller(TickBoxController::new)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .option(Option.createBuilder(Text.class)
                                        .binding(Binding.immutable(Text.translatable("adaptivetooltips.label.scrolling_instructions")))
                                        .controller(LabelController::new)
                                        .build())
                                .option(Option.createBuilder(int.class)
                                        .name(Text.translatable("adaptivetooltips.bind.scroll"))
                                        .binding(
                                                DEFAULTS.scrollKeyCode,
                                                () -> scrollKeyCode,
                                                val -> scrollKeyCode = val
                                        )
                                        .controller(KeyCodeController::new)
                                        .build())
                                .option(Option.createBuilder(int.class)
                                        .name(Text.translatable("adaptivetooltips.bind.horizontal_scroll"))
                                        .binding(
                                                DEFAULTS.horizontalScrollKeyCode,
                                                () -> horizontalScrollKeyCode,
                                                val -> horizontalScrollKeyCode = val
                                        )
                                        .controller(KeyCodeController::new)
                                        .build())
                                .option(Option.createBuilder(int.class)
                                        .name(Text.translatable("adaptivetooltips.opt.vertical_scroll_sensitivity.title"))
                                        .tooltip(Text.translatable("adaptivetooltips.opt.vertical_scroll_sensitivity.desc"))
                                        .binding(
                                                DEFAULTS.verticalScrollSensitivity,
                                                () -> verticalScrollSensitivity,
                                                val -> verticalScrollSensitivity = val
                                        )
                                        .controller(opt -> new IntegerSliderController(opt, 1, 10, 1, val -> Text.translatable("adaptivetooltips.format.pixels", val)))
                                        .build())
                                .option(Option.createBuilder(int.class)
                                        .name(Text.translatable("adaptivetooltips.opt.horizontal_scroll_sensitivity.title"))
                                        .tooltip(Text.translatable("adaptivetooltips.opt.horizontal_scroll_sensitivity.desc"))
                                        .binding(
                                                DEFAULTS.horizontalScrollSensitivity,
                                                () -> horizontalScrollSensitivity,
                                                val -> horizontalScrollSensitivity = val
                                        )
                                        .controller(opt -> new IntegerSliderController(opt, 1, 10, 1, val -> Text.translatable("adaptivetooltips.format.pixels", val)))
                                        .build())
                                .build())
                        .build())
                .save(this::save)
                .build()
                .generateScreen(parent);
    }
}
