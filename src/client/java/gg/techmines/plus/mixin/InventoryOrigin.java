package gg.techmines.plus.mixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
@Mixin(AbstractContainerScreen.class)
public interface InventoryOrigin {
 @Accessor("leftPos") int techminesPlusLeft();
 @Accessor("topPos") int techminesPlusTop();
}

