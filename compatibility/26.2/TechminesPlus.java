package gg.techmines.plus;

import java.util.Locale;
import gg.techmines.plus.mixin.InventoryOrigin;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

public final class TechminesPlus implements ClientModInitializer {
 private static final int[] COLORS = {0xffe34b4b,0xffffa332,0xffffdf4d,0xff61bd58,0xff4fbce0,0xff626de0,0xffbd63d5};
 private static final String[] COLOR_NAMES = {"Red","Orange","Yellow","Green","Cyan","Blue","Purple"};
 public static boolean onTechmines(Minecraft client) {
  if (client.player == null || client.getConnection() == null || client.getCurrentServer() == null) return false;
  String address = client.getCurrentServer().ip.toLowerCase(Locale.ROOT);
  return address.equals("techmines.minehut.gg") || address.equals("techmines.minehut.gg:25565");
 }
 private static void command(String command) {
  Minecraft client = Minecraft.getInstance();
  if (onTechmines(client)) { client.gui.setScreen(null); client.getConnection().sendCommand(command); }
 }
 @Override public void onInitializeClient() {
  KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("techmines_plus", "shortcuts"));
  KeyMapping menu = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.techmines_plus.menu", InputConstants.Type.KEYSYM, InputConstants.KEY_K, category));
  ClientTickEvents.END_CLIENT_TICK.register(client -> {
   while (menu.consumeClick()) if (client.gui.screen() == null && onTechmines(client)) client.gui.setScreen(new PlusScreen(null, false));
  });
  ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
   if (screen instanceof InventoryScreen && onTechmines(client)) {
    InventoryOrigin origin = (InventoryOrigin)screen;
    Screens.getWidgets(screen).add(Button.builder(Component.literal("Vendor Info"),b -> VendorInfoScreen.open(screen)).bounds(origin.techminesPlusLeft()+98,origin.techminesPlusTop()-24,76,20).build());
    Screens.getWidgets(screen).add(new ItemButton(origin.techminesPlusLeft()+48,origin.techminesPlusTop()-24,"Trash - /trash",new ItemStack(Items.HOPPER),0,b -> command("trash")));
    Screens.getWidgets(screen).add(new ItemButton(origin.techminesPlusLeft()+74,origin.techminesPlusTop()-24,"Toggle PvP - /pvp",new ItemStack(Items.IRON_SWORD),0,b -> command("pvp")));
    // Replace the unused recipe button with five compact command shortcuts and two upper buttons.
    Screens.getWidgets(screen).removeIf(widget -> widget instanceof ImageButton && widget.getX()==origin.techminesPlusLeft()+104 && widget.getY()==height/2-22);
    int x = origin.techminesPlusLeft() + 98, y = origin.techminesPlusTop() + 61;
    Screens.getWidgets(screen).add(new ItemButton(x,y,"Wardrobe — /wardrobe",new ItemStack(Items.ARMOR_STAND),0,b -> command("wardrobe")).compact());
    Screens.getWidgets(screen).add(new ItemButton(x+15,y,"Ender Chest — /ec",new ItemStack(Items.ENDER_CHEST),0,b -> command("ec")).compact());
    Screens.getWidgets(screen).add(new ItemButton(x+30,y,"Personal Vaults",new ItemStack(Items.CHEST),0,b -> client.gui.setScreen(new PlusScreen(screen,true))).compact());
    Screens.getWidgets(screen).add(new ItemButton(x+45,y,"Fly — /fly",new ItemStack(Items.ELYTRA),0,b -> command("fly")).compact());
    Screens.getWidgets(screen).add(new ItemButton(x+60,y,"Auction House — /ah",new ItemStack(Items.EMERALD),0,b -> command("ah")).compact());
   }
  });
 }
 private static final class ItemButton extends Button {
  private final ItemStack icon;
  private final int color;
  ItemButton(int x,int y,String label,ItemStack icon,int color,OnPress action) {
   super(x,y,22,20,Component.literal(label),action,DEFAULT_NARRATION);
   this.icon=icon; this.color=color;
   setTooltip(Tooltip.create(Component.literal(label)));
  }
  ItemButton compact() { setWidth(15); setHeight(18); return this; }
  @Override protected void extractContents(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {
   int x=getX(),y=getY();
   g.fill(x,y,x+getWidth(),y+getHeight(),0xff373737);
   g.fill(x+1,y+1,x+getWidth()-1,y+getHeight()-1,isHoveredOrFocused()?0xffeeeeee:0xffa4a4a4);
   g.fill(x+2,y+2,x+getWidth()-2,y+getHeight()-2,0xff6b6b6b);
   if(color==0) {
    if(getWidth()==15) {
     g.pose().pushMatrix();g.pose().translate((float)x,(float)(y+2)).scale(0.875f,0.875f);g.item(icon,0,0);g.pose().popMatrix();
    } else g.item(icon,x+(getWidth()-16)/2,y+(getHeight()-16)/2);
   }
   else {
    // Client-only coloured chest artwork; no inventory stacks or server slots change.
    int shade=0xff000000 | ((color & 0x00fefefe) >>> 1);
    g.fill(x+4,y+4,x+18,y+17,0xff292420);
    g.fill(x+5,y+5,x+17,y+9,color);
    g.fill(x+5,y+10,x+17,y+16,shade);
    g.fill(x+6,y+11,x+16,y+15,color);
    g.fill(x+10,y+8,x+12,y+12,0xffffe39b);
   }
  }
 }
 private static final class PlusScreen extends Screen {
  private final Screen parent;
  private final boolean vaults;
  PlusScreen(Screen parent,boolean vaults) { super(Component.literal(vaults?"Personal Vaults":"Techmines Plus")); this.parent=parent; this.vaults=vaults; }
  @Override protected void init() {
   int x=width/2-100,y=Math.max(6,height/2-65);
   Button heading=Button.builder(title,b -> {}).bounds(x,y,200,20).build(); heading.active=false; addRenderableWidget(heading);
   if(vaults) {
    for(int i=0;i<7;i++) {
     final int vault=i+1;
     int bx=x+(i%4)*50,by=y+28+(i/4)*42;
     addRenderableWidget(new ItemButton(bx+14,by,COLOR_NAMES[i]+" chest — Vault "+vault+" — /pv "+vault,new ItemStack(Items.CHEST),COLORS[i],b -> command("pv "+vault)));
     Button label=Button.builder(Component.literal(""+vault),b -> command("pv "+vault)).bounds(bx+8,by+21,34,18).build();
     addRenderableWidget(label);
    }
   } else {
    addRenderableWidget(new ItemButton(x,y+32,"Wardrobe — /wardrobe",new ItemStack(Items.ARMOR_STAND),0,b -> command("wardrobe")));
    addRenderableWidget(new ItemButton(x+29,y+32,"Ender Chest — /ec",new ItemStack(Items.ENDER_CHEST),0,b -> command("ec")));
    addRenderableWidget(new ItemButton(x+58,y+32,"Personal Vaults",new ItemStack(Items.CHEST),0,b -> Minecraft.getInstance().gui.setScreen(new PlusScreen(this,true))));
    addRenderableWidget(new ItemButton(x+87,y+32,"Fly — /fly",new ItemStack(Items.ELYTRA),0,b -> command("fly")));
    addRenderableWidget(new ItemButton(x+116,y+32,"Auction House — /ah",new ItemStack(Items.EMERALD),0,b -> command("ah")));
    addRenderableWidget(new ItemButton(x+145,y+32,"Trash - /trash",new ItemStack(Items.HOPPER),0,b -> command("trash")));
    addRenderableWidget(new ItemButton(x+174,y+32,"Toggle PvP - /pvp",new ItemStack(Items.IRON_SWORD),0,b -> command("pvp")));
   }
   if(!vaults) {
    addRenderableWidget(Button.builder(Component.literal("Vendor Info"),b -> VendorInfoScreen.open(this)).bounds(x,y+62,200,20).build());
   }
   addRenderableWidget(Button.builder(Component.literal("Back"),b -> onClose()).bounds(x,y+(vaults?116:90),200,20).build());
  }
  @Override public void onClose() { Minecraft.getInstance().gui.setScreen(parent); }
  @Override public boolean isPauseScreen() { return false; }
 }
}









