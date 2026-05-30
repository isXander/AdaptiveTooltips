package dev.isxander.adaptivetooltips.config;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.adaptivetooltips.AdaptiveTooltips;
import dev.isxander.adaptivetooltips.config.gui.KeyCodeController;
import dev.isxander.adaptivetooltips.platform.AdaptiveTooltipsPlatform;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.ValueFormatters;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class AdaptiveTooltipConfig {
    public static final ConfigClassHandler<AdaptiveTooltipConfig> HANDLER = ConfigClassHandler.createBuilder(AdaptiveTooltipConfig.class)
            .id(Identifier.fromNamespaceAndPath("adaptivetooltips", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(AdaptiveTooltipsPlatform.get().getConfigDir().resolve("adaptive-tooltips.json"))
                    .setJson5(true)
                    .build())
            .build();

    private static final String PIXELS_FORMAT = "adaptivetooltips.format.pixels";

    @SerialEntry
    public WrapTextBehaviour wrapText = WrapTextBehaviour.SCREEN_WIDTH;

    @SerialEntry
    public boolean overwriteVanillaWrapping = false;

    @SerialEntry
    public boolean prioritizeTooltipTop = true;

    @SerialEntry
    public boolean bedrockCentering = true;

    @SerialEntry
    public boolean bestCorner = false;

    @SerialEntry
    public boolean alwaysBestCorner = false;

    @SerialEntry
    public boolean preventVanillaClamping = true;

    @SerialEntry
    public boolean onlyRepositionHoverTooltips = true;

    @SerialEntry
    public boolean useYACLTooltipPositioner = false;

    @SerialEntry
    public int scrollKeyCode = InputConstants.KEY_LALT;

    @SerialEntry
    public int horizontalScrollKeyCode = InputConstants.KEY_LCONTROL;

    @SerialEntry
    public boolean smoothScrolling = true;

    @SerialEntry
    public ScrollDirection scrollDirection = Util.getPlatform() == Util.OS.OSX ? ScrollDirection.NATURAL : ScrollDirection.REVERSE;

    @SerialEntry
    public int verticalScrollSensitivity = 10;

    @SerialEntry
    public int horizontalScrollSensitivity = 10;

    @SerialEntry
    public float tooltipTransparency = 1f;

    @SerialEntry
    public boolean removeFirstLinePadding = true;

    public static YetAnotherConfigLib createGui() {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> builder
                .title(Component.translatable("yacl3.config.adaptivetooltips:config.category.adaptivetooltips"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.category.adaptivetooltips"))
                        .group(createContentManipulationGroup(defaults, config))
                        .group(createPositioningGroup(defaults, config))
                        .group(createScrollingGroup(defaults, config))
                        .group(createStyleGroup(defaults, config))
                        .build()));
    }

    private static OptionGroup createContentManipulationGroup(AdaptiveTooltipConfig defaults, AdaptiveTooltipConfig config) {
        return OptionGroup.createBuilder()
                .name(Component.translatable("yacl3.config.adaptivetooltips:config.category.adaptivetooltips.group.content_manipulation"))
                .description(OptionDescription.of(Component.translatable("yacl3.config.adaptivetooltips:config.category.adaptivetooltips.group.content_manipulation.desc")))
                .option(Option.<WrapTextBehaviour>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.wrapText"))
                        .description(OptionDescription.createBuilder()
                                .text(Component.translatable("yacl3.config.adaptivetooltips:config.wrapText.desc"))
                                .webpImage(AdaptiveTooltips.id("screenshots/remaining-width-wrapping.webp"))
                                .build())
                        .binding(defaults.wrapText, () -> config.wrapText, value -> config.wrapText = value)
                        .controller(option -> CyclingListControllerBuilder.create(option)
                                .values(WrapTextBehaviour.values())
                                .formatValue(WrapTextBehaviour::getDisplayName))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.overwriteVanillaWrapping"))
                        .description(OptionDescription.of(Component.translatable("yacl3.config.adaptivetooltips:config.overwriteVanillaWrapping.desc")))
                        .binding(defaults.overwriteVanillaWrapping, () -> config.overwriteVanillaWrapping, value -> config.overwriteVanillaWrapping = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .build();
    }

    private static OptionGroup createPositioningGroup(AdaptiveTooltipConfig defaults, AdaptiveTooltipConfig config) {
        Option<Boolean> prioritizeTooltipTop = Option.<Boolean>createBuilder()
                .name(Component.translatable("yacl3.config.adaptivetooltips:config.prioritizeTooltipTop"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("yacl3.config.adaptivetooltips:config.prioritizeTooltipTop.desc"))
                        .webpImage(AdaptiveTooltips.id("screenshots/prioritize-tooltip-top.webp"))
                        .build())
                .binding(defaults.prioritizeTooltipTop, () -> config.prioritizeTooltipTop, value -> config.prioritizeTooltipTop = value)
                .controller(TickBoxControllerBuilder::create)
                .build();
        Option<Boolean> bedrockCentering = Option.<Boolean>createBuilder()
                .name(Component.translatable("yacl3.config.adaptivetooltips:config.bedrockCentering"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("yacl3.config.adaptivetooltips:config.bedrockCentering.desc"))
                        .webpImage(AdaptiveTooltips.id("screenshots/bedrock-centering.webp"))
                        .build())
                .binding(defaults.bedrockCentering, () -> config.bedrockCentering, value -> config.bedrockCentering = value)
                .controller(TickBoxControllerBuilder::create)
                .build();
        Option<Boolean> bestCorner = Option.<Boolean>createBuilder()
                .name(Component.translatable("yacl3.config.adaptivetooltips:config.bestCorner"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("yacl3.config.adaptivetooltips:config.bestCorner.desc"))
                        .webpImage(AdaptiveTooltips.id("screenshots/align-corner.webp"))
                        .build())
                .binding(defaults.bestCorner, () -> config.bestCorner, value -> config.bestCorner = value)
                .controller(TickBoxControllerBuilder::create)
                .build();
        Option<Boolean> alwaysBestCorner = Option.<Boolean>createBuilder()
                .name(Component.translatable("yacl3.config.adaptivetooltips:config.alwaysBestCorner"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("yacl3.config.adaptivetooltips:config.alwaysBestCorner.desc"))
                        .webpImage(AdaptiveTooltips.id("screenshots/align-corner.webp"))
                        .build())
                .binding(defaults.alwaysBestCorner, () -> config.alwaysBestCorner, value -> config.alwaysBestCorner = value)
                .controller(TickBoxControllerBuilder::create)
                .build();
        Option<Boolean> preventVanillaClamping = Option.<Boolean>createBuilder()
                .name(Component.translatable("yacl3.config.adaptivetooltips:config.preventVanillaClamping"))
                .description(OptionDescription.of(Component.translatable("yacl3.config.adaptivetooltips:config.preventVanillaClamping.desc")))
                .binding(defaults.preventVanillaClamping, () -> config.preventVanillaClamping, value -> config.preventVanillaClamping = value)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Runnable updateAvailability = () -> {
            alwaysBestCorner.setAvailable(bestCorner.pendingValue());
            prioritizeTooltipTop.setAvailable(!alwaysBestCorner.pendingValue());
            bedrockCentering.setAvailable(preventVanillaClamping.pendingValue() && !alwaysBestCorner.pendingValue());
        };
        bestCorner.addEventListener((_, _) -> updateAvailability.run());
        alwaysBestCorner.addEventListener((_, _) -> updateAvailability.run());
        preventVanillaClamping.addEventListener((_, _) -> updateAvailability.run());
        updateAvailability.run();

        return OptionGroup.createBuilder()
                .name(Component.translatable("yacl3.config.adaptivetooltips:config.category.adaptivetooltips.group.positioning"))
                .description(OptionDescription.of(Component.translatable("yacl3.config.adaptivetooltips:config.category.adaptivetooltips.group.positioning.desc")))
                .option(prioritizeTooltipTop)
                .option(bedrockCentering)
                .option(bestCorner)
                .option(alwaysBestCorner)
                .option(preventVanillaClamping)
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.onlyRepositionHoverTooltips"))
                        .description(OptionDescription.of(Component.translatable("yacl3.config.adaptivetooltips:config.onlyRepositionHoverTooltips.desc")))
                        .binding(defaults.onlyRepositionHoverTooltips, () -> config.onlyRepositionHoverTooltips, value -> config.onlyRepositionHoverTooltips = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.useYACLTooltipPositioner"))
                        .description(OptionDescription.createBuilder()
                                .text(Component.translatable("yacl3.config.adaptivetooltips:config.useYACLTooltipPositioner.desc"))
                                .webpImage(AdaptiveTooltips.id("screenshots/yacl-style.webp"))
                                .build())
                        .binding(defaults.useYACLTooltipPositioner, () -> config.useYACLTooltipPositioner, value -> config.useYACLTooltipPositioner = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .build();
    }

    private static OptionGroup createScrollingGroup(AdaptiveTooltipConfig defaults, AdaptiveTooltipConfig config) {
        var image = AdaptiveTooltips.id("screenshots/scrolling.webp");

        return OptionGroup.createBuilder()
                .name(Component.translatable("yacl3.config.adaptivetooltips:config.category.adaptivetooltips.group.scrolling"))
                .description(OptionDescription.createBuilder()
                        .text(Component.translatable("adaptivetooltips.group.scrolling.desc"))
                        .webpImage(image)
                        .build())
                .option(LabelOption.create(Component.translatable("adaptivetooltips.label.scrolling_instructions")))
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.scrollKeyCode"))
                        .description(OptionDescription.createBuilder()
                                .webpImage(image)
                                .build())
                        .binding(defaults.scrollKeyCode, () -> config.scrollKeyCode, value -> config.scrollKeyCode = value)
                        .customController(KeyCodeController::new)
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.horizontalScrollKeyCode"))
                        .description(OptionDescription.createBuilder()
                                .webpImage(image)
                                .build())
                        .binding(defaults.horizontalScrollKeyCode, () -> config.horizontalScrollKeyCode, value -> config.horizontalScrollKeyCode = value)
                        .customController(KeyCodeController::new)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.smoothScrolling"))
                        .description(OptionDescription.createBuilder()
                                .text(Component.translatable("yacl3.config.adaptivetooltips:config.smoothScrolling.desc"))
                                .webpImage(image)
                                .build())
                        .binding(defaults.smoothScrolling, () -> config.smoothScrolling, value -> config.smoothScrolling = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<ScrollDirection>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.scrollDirection"))
                        .description(OptionDescription.createBuilder()
                                .text(Component.translatable("yacl3.config.adaptivetooltips:config.scrollDirection.desc"))
                                .webpImage(image)
                                .build())
                        .binding(defaults.scrollDirection, () -> config.scrollDirection, value -> config.scrollDirection = value)
                        .controller(option -> CyclingListControllerBuilder.create(option)
                                .values(ScrollDirection.values())
                                .formatValue(ScrollDirection::getDisplayName))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.verticalScrollSensitivity"))
                        .description(OptionDescription.createBuilder()
                                .text(Component.translatable("yacl3.config.adaptivetooltips:config.verticalScrollSensitivity.desc"))
                                .webpImage(image)
                                .build())
                        .binding(defaults.verticalScrollSensitivity, () -> config.verticalScrollSensitivity, value -> config.verticalScrollSensitivity = value)
                        .controller(option -> IntegerSliderControllerBuilder.create(option)
                                .range(5, 20)
                                .step(1)
                                .formatValue(value -> Component.translatable(PIXELS_FORMAT, value)))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.horizontalScrollSensitivity"))
                        .description(OptionDescription.createBuilder()
                                .text(Component.translatable("yacl3.config.adaptivetooltips:config.horizontalScrollSensitivity.desc"))
                                .webpImage(image)
                                .build())
                        .binding(defaults.horizontalScrollSensitivity, () -> config.horizontalScrollSensitivity, value -> config.horizontalScrollSensitivity = value)
                        .controller(option -> IntegerSliderControllerBuilder.create(option)
                                .range(5, 20)
                                .step(1)
                                .formatValue(value -> Component.translatable(PIXELS_FORMAT, value)))
                        .build())
                .build();
    }

    private static OptionGroup createStyleGroup(AdaptiveTooltipConfig defaults, AdaptiveTooltipConfig config) {
        return OptionGroup.createBuilder()
                .name(Component.translatable("yacl3.config.adaptivetooltips:config.category.adaptivetooltips.group.style"))
                .description(OptionDescription.of(Component.translatable("yacl3.config.adaptivetooltips:config.category.adaptivetooltips.group.style.desc")))
                .option(Option.<Float>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.tooltipTransparency"))
                        .description(OptionDescription.createBuilder()
                                .text(Component.translatable("yacl3.config.adaptivetooltips:config.tooltipTransparency.desc"))
                                .webpImage(AdaptiveTooltips.id("screenshots/transparency-modification.webp"))
                                .build())
                        .binding(defaults.tooltipTransparency, () -> config.tooltipTransparency, value -> config.tooltipTransparency = value)
                        .controller(option -> FloatSliderControllerBuilder.create(option)
                                .range(0f, 1f)
                                .step(0.1f)
                                .formatValue(ValueFormatters.percent(1)))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable("yacl3.config.adaptivetooltips:config.removeFirstLinePadding"))
                        .description(OptionDescription.of(Component.translatable("yacl3.config.adaptivetooltips:config.removeFirstLinePadding.desc")))
                        .binding(defaults.removeFirstLinePadding, () -> config.removeFirstLinePadding, value -> config.removeFirstLinePadding = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .build();
    }
}
