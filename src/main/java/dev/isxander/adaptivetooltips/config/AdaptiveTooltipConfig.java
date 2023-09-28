package dev.isxander.adaptivetooltips.config;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.adaptivetooltips.config.gui.KeyCode;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.ValueFormatters;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AdaptiveTooltipConfig {
    public static final ConfigClassHandler<AdaptiveTooltipConfig> HANDLER = ConfigClassHandler.createBuilder(AdaptiveTooltipConfig.class)
            .id(new ResourceLocation("adaptivetooltips", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("adaptive-tooltips.json"))
                    .setJson5(true)
                    .build())
            .build();

    private static final String CATEGORY = "adaptivetooltips";
    private static final String CONTENT_MANIPULATION = "content_manipulation";
    private static final String POSITIONING = "positioning";
    private static final String SCROLLING = "scrolling";
    private static final String STYLE = "style";

    private static final String PIXELS_FORMAT = "adaptivetooltips.format.pixels";

    @AutoGen(category = CATEGORY, group = CONTENT_MANIPULATION)
    @EnumCycler
    @SerialEntry
    public WrapTextBehaviour wrapText = WrapTextBehaviour.SCREEN_WIDTH;

    @AutoGen(category = CATEGORY, group = CONTENT_MANIPULATION)
    @TickBox
    @SerialEntry
    public boolean overwriteVanillaWrapping = false;

    @AutoGen(category = CATEGORY, group = POSITIONING)
    @TickBox
    @SerialEntry
    public boolean prioritizeTooltipTop = true;

    @AutoGen(category = CATEGORY, group = POSITIONING)
    @TickBox
    @SerialEntry
    public boolean bedrockCentering = true;

    @AutoGen(category = CATEGORY, group = POSITIONING)
    @MasterTickBox("alwaysBestCorner")
    @SerialEntry
    public boolean bestCorner = false;

    @AutoGen(category = CATEGORY, group = POSITIONING)
    @MasterTickBox(value = {"prioritizeTooltipTop", "bedrockCentering"}, invert = true)
    @SerialEntry
    public boolean alwaysBestCorner = false;

    @AutoGen(category = CATEGORY, group = POSITIONING)
    @MasterTickBox("bedrockCentering")
    @SerialEntry
    public boolean preventVanillaClamping = true;

    @AutoGen(category = CATEGORY, group = POSITIONING)
    @TickBox
    @SerialEntry
    public boolean onlyRepositionHoverTooltips = true;

    @AutoGen(category = CATEGORY, group = POSITIONING)
    @TickBox
    @SerialEntry
    public boolean useYACLTooltipPositioner = false;

    @AutoGen(category = CATEGORY, group = SCROLLING)
    @Label
    private final Component scrollingGuide = Component.translatable("adaptivetooltips.label.scrolling_instructions");

    @AutoGen(category = CATEGORY, group = SCROLLING)
    @KeyCode
    @SerialEntry
    public int scrollKeyCode = InputConstants.KEY_LALT;

    @AutoGen(category = CATEGORY, group = SCROLLING)
    @KeyCode
    @SerialEntry
    public int horizontalScrollKeyCode = InputConstants.KEY_LCONTROL;

    @AutoGen(category = CATEGORY, group = SCROLLING)
    @TickBox
    @SerialEntry
    public boolean smoothScrolling = true;

    @AutoGen(category = CATEGORY, group = SCROLLING)
    @EnumCycler
    @SerialEntry
    public ScrollDirection scrollDirection = Util.getPlatform() == Util.OS.OSX ? ScrollDirection.NATURAL : ScrollDirection.REVERSE;

    @AutoGen(category = CATEGORY, group = SCROLLING)
    @IntSlider(min = 5, max = 20, step = 1)
    @FormatTranslation(PIXELS_FORMAT)
    @SerialEntry
    public int verticalScrollSensitivity = 10;

    @AutoGen(category = CATEGORY, group = SCROLLING)
    @IntSlider(min = 5, max = 20, step = 1)
    @FormatTranslation(PIXELS_FORMAT)
    @SerialEntry
    public int horizontalScrollSensitivity = 10;

    @AutoGen(category = CATEGORY, group = STYLE)
    @FloatSlider(min = 0f, max = 1f, step = 0.1f)
    @CustomFormat(ValueFormatters.PercentFormatter.class)
    @SerialEntry
    public float tooltipTransparency = 1f;

    @AutoGen(category = CATEGORY, group = STYLE)
    @TickBox
    @SerialEntry
    public boolean removeFirstLinePadding = true;
}
