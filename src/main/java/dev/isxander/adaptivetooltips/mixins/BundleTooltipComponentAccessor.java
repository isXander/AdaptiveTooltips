package dev.isxander.adaptivetooltips.mixins;

import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BundleTooltipComponent.class)
public interface BundleTooltipComponentAccessor {
    @Accessor
    DefaultedList<ItemStack> getInventory();
}
