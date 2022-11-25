package dev.isxander.adaptivetooltips.helpers.positioner;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Optional;

public class BedrockCenteringPositionModule implements TooltipPositionModule {
    @Override
    public Optional<Vector2ic> repositionTooltip(int x, int y, int width, int height, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (!AdaptiveTooltipConfig.INSTANCE.getConfig().bedrockCentering)
            return Optional.empty();

        int modX = x;
        int modY = y;

        if (x < 4) {
            modX = MathHelper.clamp(mouseX - width / 2, 6, screenWidth - width - 6);
            modY = mouseY - height - 12;

            if (modY < 6) {
                // find amount of obstruction to decide if it
                // is best to be above or below cursor
                var below = mouseY + 12;
                var belowObstruction = below + height - screenHeight;
                var aboveObstruction = -modY;

                if (belowObstruction < aboveObstruction) {
                    modY = below;
                }
            }
        } else if (y + height > screenHeight + 2) {
            modY = Math.max(screenHeight - height - 4, 4);
        } else {
            return Optional.empty();
        }

        return Optional.of(new Vector2i(modX, modY));
    }
}
