package dev.isxander.adaptivetooltips.mixins;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientBundleTooltip.class)
public interface BundleTooltipComponentAccessor {
    @Accessor
    NonNullList<ItemStack> getItems();
}
