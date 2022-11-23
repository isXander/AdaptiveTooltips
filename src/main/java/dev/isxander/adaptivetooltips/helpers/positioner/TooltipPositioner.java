package dev.isxander.adaptivetooltips.helpers.positioner;

import org.joml.Vector2ic;

import java.util.Optional;

public interface TooltipPositioner {
    Optional<Vector2ic> repositionTooltip(int x, int y, int width, int height, int mouseX, int mouseY, int screenWidth, int screenHeight);
}
