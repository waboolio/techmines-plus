package gg.techmines.plus;

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
 private static VaultNames vaultNames;
 private static final int[] COLORS = {0xffe34b4b,0xffffa332,0xffffdf4d,0xff61bd58,0xff4fbce0,0xff626de0,0xffbd63d5};
 private static final String[] COLOR_NAMES = {"Red","Orange","Yellow","Green","Cyan","Blue","Purple"};
 public static boolean onTechmines(Minecraft client) {
  return ServerDetection.active(client);
 }
 private static void command(String command) {
  Minecraft client = Minecraft.getInstance();
  if (onTechmines(client)) { client.gui.setScreen(null); client.getConnection().sendCommand(command); }
 }
 @Override public void onInitializeClient() {
  vaultNames=new VaultNames(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("techmines-plus/vault-names.json"));
  ChatMentions.initialize();
  MenuIcons.initialize(net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir().resolve("techmines-plus/menu-icons.json"));
  KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("techmines_plus", "shortcuts"));
  KeyMapping menu = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.techmines_plus.menu", InputConstants.Type.KEYBOARD, InputConstants.KEY_K, category));
  ClientTickEvents.END_CLIENT_TICK.register(client -> {
   MenuIcons.tick(client);
   while (menu.consumeClick()) if (client.gui.screen() == null && onTechmines(client)) client.gui.setScreen(new PlusScreen(null, false));
  });
  ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
   if (screen instanceof InventoryScreen && onTechmines(client)) {
    InventoryOrigin origin = (InventoryOrigin)screen;
    int left=origin.techminesPlusLeft(),top=origin.techminesPlusTop();
    // Shorten only the vendor row at the minimum GUI height so both rows stay visible.
    int vendorHeight=Math.min(20,Math.max(13,top-24));
    Screens.getWidgets(screen).add(Button.builder(Component.literal("Vendor Info"),b -> VendorInfoScreen.open(screen)).bounds(left,Math.max(0,top-24-vendorHeight),176,vendorHeight).build());
    Screens.getWidgets(screen).add(new ItemButton(left+40,top-22,"Overclock - /oc",MenuIcons::overclock,0,b -> command("oc")));
    Screens.getWidgets(screen).add(new ItemButton(left+64,top-22,"Shop - /shop",new ItemStack(Items.TOTEM_OF_UNDYING),0,b -> command("shop")));
    Screens.getWidgets(screen).add(new ItemButton(left+88,top-22,"Shards - /shards",new ItemStack(Items.AMETHYST_SHARD),0,b -> command("shards")));
    Screens.getWidgets(screen).add(new ItemButton(left+112,top-22,"Skill Tree - /skilltree",new ItemStack(Items.OAK_SAPLING),0,b -> command("skilltree")));
    Screens.getWidgets(screen).add(new ItemButton(left-24,top+7,"Toggle PvP - /pvp",new ItemStack(Items.IRON_SWORD),0,b -> command("pvp")).rail());
    // Keep the compact inventory row clear; auxiliary shortcuts sit outside the slots.
    Screens.getWidgets(screen).removeIf(widget -> widget instanceof ImageButton && widget.getX()==origin.techminesPlusLeft()+104 && widget.getY()==height/2-22);
    int x = origin.techminesPlusLeft() + 98, y = origin.techminesPlusTop() + 61;
    Screens.getWidgets(screen).add(new ItemButton(x,y,"Wardrobe — /wardrobe",new ItemStack(Items.ARMOR_STAND),0,b -> command("wardrobe")).compact());
    Screens.getWidgets(screen).add(new ItemButton(x+15,y,"Ender Chest — /ec",new ItemStack(Items.ENDER_CHEST),0,b -> command("ec")).compact());
    Screens.getWidgets(screen).add(new ItemButton(x+30,y,"Personal Vaults",new ItemStack(Items.CHEST),0,b -> client.gui.setScreen(new PlusScreen(screen,true))).compact());
    Screens.getWidgets(screen).add(new ItemButton(x+45,y,"Accessory Bag - /bag",MenuIcons::bag,0,b -> command("bag")).compact());
    Screens.getWidgets(screen).add(new ItemButton(origin.techminesPlusLeft()-24,origin.techminesPlusTop()+25,"Fly - /fly",new ItemStack(Items.ELYTRA),0,b -> command("fly")).rail());
    Screens.getWidgets(screen).add(new ItemButton(origin.techminesPlusLeft()-24,origin.techminesPlusTop()+43,"Heal - /heal",new ItemStack(Items.GLISTERING_MELON_SLICE),0,b -> command("heal")).rail());
    Screens.getWidgets(screen).add(new ItemButton(left-24,top+61,"Players",new ItemStack(Items.PLAYER_HEAD),0,b -> client.gui.setScreen(new PlayersScreen(screen))).rail());
    Screens.getWidgets(screen).add(new ItemButton(origin.techminesPlusLeft()+180,origin.techminesPlusTop()+140,"Trash - /trash",new ItemStack(Items.HOPPER),0,b -> command("trash")));
    Screens.getWidgets(screen).add(new ItemButton(x+60,y,"Auction House — /ah",new ItemStack(Items.EMERALD),0,b -> command("ah")).compact());
   }
  });
 }
 private static final class ItemButton extends Button {
  private final java.util.function.Supplier<ItemStack> icon;
  private final int color;
  ItemButton(int x,int y,String label,ItemStack icon,int color,OnPress action) {
   this(x,y,label,()->icon,color,action);
  }
  ItemButton(int x,int y,String label,java.util.function.Supplier<ItemStack> icon,int color,OnPress action) {
   super(x,y,22,20,Component.literal(label),action,DEFAULT_NARRATION);
   this.icon=icon; this.color=color;
   setTooltip(Tooltip.create(Component.literal(label)));
  }
  ItemButton rail() { setWidth(20); setHeight(18); return this; }
  ItemButton compact() { setWidth(15); setHeight(18); return this; }
  @Override protected void extractContents(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {
   int x=getX(),y=getY();
   g.fill(x,y,x+getWidth(),y+getHeight(),0xff373737);
   g.fill(x+1,y+1,x+getWidth()-1,y+getHeight()-1,isHoveredOrFocused()?0xffeeeeee:0xffa4a4a4);
   g.fill(x+2,y+2,x+getWidth()-2,y+getHeight()-2,0xff6b6b6b);
   if(color==0) {
    if(getWidth()==15) {
     g.pose().pushMatrix();g.pose().translate((float)x,(float)(y+2)).scale(0.875f,0.875f);g.item(icon.get(),0,0);g.pose().popMatrix();
    } else g.item(icon.get(),x+(getWidth()-16)/2,y+(getHeight()-16)/2);
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
   int x=width/2-100,y=Math.max(6,(height-(vaults?194:146))/2);
   Button heading=Button.builder(title,b -> {}).bounds(x,y,200,20).build(); heading.active=false; addRenderableWidget(heading);
   if(vaults) {
    String account=Minecraft.getInstance().player.getUUID().toString();
    for(int i=0;i<7;i++) {
     final int vault=i+1;int bx=x+(i%2)*104,by=y+26+(i/2)*36;
     addRenderableWidget(new VaultShortcutButton(bx,by,vaultNames.label(account,vault),vault,COLORS[i],b -> command("pv "+vault),()->Minecraft.getInstance().gui.setScreen(new VaultRenameScreen(this,vaultNames,account,vault))));
    }
   } else {
    addRenderableWidget(new ItemButton(x,y+26,"Wardrobe - /wardrobe",new ItemStack(Items.ARMOR_STAND),0,b -> command("wardrobe")));
    addRenderableWidget(new ItemButton(x+44,y+26,"Ender Chest - /ec",new ItemStack(Items.ENDER_CHEST),0,b -> command("ec")));
    addRenderableWidget(new ItemButton(x+88,y+26,"Personal Vaults",new ItemStack(Items.CHEST),0,b -> Minecraft.getInstance().gui.setScreen(new PlusScreen(this,true))));
    addRenderableWidget(new ItemButton(x+132,y+26,"Accessory Bag - /bag",MenuIcons::bag,0,b -> command("bag")));
    addRenderableWidget(new ItemButton(x+176,y+26,"Auction House - /ah",new ItemStack(Items.EMERALD),0,b -> command("ah")));
    addRenderableWidget(new ItemButton(x,y+50,"Fly - /fly",new ItemStack(Items.ELYTRA),0,b -> command("fly")));
    addRenderableWidget(new ItemButton(x+44,y+50,"Heal - /heal",new ItemStack(Items.GLISTERING_MELON_SLICE),0,b -> command("heal")));
    addRenderableWidget(new ItemButton(x+88,y+50,"Overclock - /oc",MenuIcons::overclock,0,b -> command("oc")));
    addRenderableWidget(new ItemButton(x+132,y+50,"Trash - /trash",new ItemStack(Items.HOPPER),0,b -> command("trash")));
    addRenderableWidget(new ItemButton(x+176,y+50,"Toggle PvP - /pvp",new ItemStack(Items.IRON_SWORD),0,b -> command("pvp")));
   }
   if(!vaults) {
    addRenderableWidget(new ItemButton(x+88,y+74,"Shards - /shards",new ItemStack(Items.AMETHYST_SHARD),0,b -> command("shards")));
    addRenderableWidget(new ItemButton(x+44,y+74,"Shop - /shop",new ItemStack(Items.TOTEM_OF_UNDYING),0,b -> command("shop")));
    addRenderableWidget(new ItemButton(x+132,y+74,"Skill Tree - /skilltree",new ItemStack(Items.OAK_SAPLING),0,b -> command("skilltree")));
    addRenderableWidget(new ItemButton(x+176,y+74,"Players",new ItemStack(Items.PLAYER_HEAD),0,b -> Minecraft.getInstance().gui.setScreen(new PlayersScreen(this))));
    addRenderableWidget(Button.builder(Component.literal("Vendor Info"),b -> VendorInfoScreen.open(this)).bounds(x,y+100,200,20).build());
   }
   addRenderableWidget(Button.builder(Component.literal("Back"),b -> onClose()).bounds(x,y+(vaults?174:126),200,20).build());
  }
  @Override public void onClose() { Minecraft.getInstance().gui.setScreen(parent); }
  @Override public boolean isPauseScreen() { return false; }
 }
}








