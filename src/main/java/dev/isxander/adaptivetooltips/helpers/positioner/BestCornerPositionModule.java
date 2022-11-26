package dev.isxander.adaptivetooltips.helpers.positioner;

import dev.isxander.adaptivetooltips.config.AdaptiveTooltipConfig;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Optional;
import java.util.TreeMap;

public class BestCornerPositionModule implements TooltipPositionModule {
    @Override
    public Optional<Vector2ic> repositionTooltip(int x, int y, int width, int height, int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (!AdaptiveTooltipConfig.INSTANCE.getConfig().bestCorner)
            return Optional.empty();

        if ((x < 4 || y < 4) || AdaptiveTooltipConfig.INSTANCE.getConfig().alwaysBestCorner) {
            int topObstruction = Math.max(5 + height - mouseY, 0);
            int bottomObstruction = Math.max(mouseY - (screenHeight - 5 - height), 0);
            int leftObstruction = Math.max((5 + width - mouseX), 0);
            int rightObstruction = Math.max(mouseX - (screenWidth - 5 - width), 0);

            // find the least overlapping (over the mouse) corner to render the tooltip in
            TreeMap<Integer, Vector2ic> corners = new TreeMap<>(); // obstruction amt - x, y
            corners.put(rightObstruction * topObstruction, new Vector2i(screenWidth - 5 - width, 5)); // top right
            corners.put(leftObstruction * bottomObstruction, new Vector2i(5, screenHeight - 5 - height)); // bottom left
            corners.put(rightObstruction * bottomObstruction, new Vector2i(screenWidth - 5 - width, screenHeight - 5 - height)); // bottom right
            corners.put(topObstruction * leftObstruction, new Vector2i(5, 5)); // top left
            // top left must be put last to rely on that maps override identical keys so if each overlapping is less than -10 it uses top left

            return Optional.of(corners.firstEntry().getValue()); // treemap is sorted by key, which is obstruction amount, getting the first entry gets the least obstructive
        } else {
            return Optional.empty();
        }
    }
}
