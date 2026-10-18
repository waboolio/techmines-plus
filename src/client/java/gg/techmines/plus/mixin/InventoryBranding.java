package gg.techmines.plus.mixin;
import gg.techmines.plus.TechminesPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(InventoryScreen.class)
public abstract class InventoryBranding {
 @Inject(method="extractLabels", at=@At("HEAD"), cancellable=true)
 private void techminesPlusBrand(GuiGraphicsExtractor graphics,int mouseX,int mouseY,CallbackInfo ci) {
  Minecraft client=Minecraft.getInstance();
  if(!TechminesPlus.onTechmines(client)) return;
  // Use the built-in font so the public build needs no third-party font assets.
  Component brand=Component.literal("Techmines Plus");
  graphics.text(client.font,brand,97,6,0xff404040,false);
  ci.cancel();
 }
}


