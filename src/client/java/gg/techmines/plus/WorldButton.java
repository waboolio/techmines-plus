package gg.techmines.plus;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
final class WorldButton extends Button {
 private final ItemStack icon;private final int accent;
 WorldButton(String name,int x,int y,int width,OnPress action){
  super(x,y,width,28,Component.literal(name),action,DEFAULT_NARRATION);
  String world=name.toLowerCase(Locale.ROOT);
  icon=new ItemStack(switch(world){
   case "spawn"->Items.GRASS_BLOCK;case "nether"->Items.NETHERRACK;case "end"->Items.END_STONE;
   case "heaven"->Items.QUARTZ_BLOCK;case "ocean"->Items.BLUE_ICE;case "acidic lakes"->Items.MELON;
   case "steampunk"->Items.COPPER_BLOCK.weathering().unaffected();case "sakura forest"->Items.CHERRY_LEAVES;
   case "wild west"->Items.HAY_BLOCK;case "pixel grove"->Items.OAK_LEAVES;
   case "planet x"->Items.CRYING_OBSIDIAN;default->Items.COMPASS;
  });
  accent=switch(world){case "spawn","pixel grove"->0xff86b85b;case "nether"->0xffa85048;case "end"->0xffddd69b;case "heaven"->0xffeeeeec;case "ocean"->0xff6ecae1;case "acidic lakes"->0xffbad745;case "steampunk"->0xffcf946c;case "sakura forest"->0xffeea7cc;case "wild west"->0xffdbbe58;case "planet x"->0xffa574de;default->0xffa7bcc8;};
  setTooltip(Tooltip.create(Component.literal(name+" vendor prices")));
 }
 @Override protected void extractContents(GuiGraphicsExtractor g,int mx,int my,float delta){
  int x=getX(),y=getY(),w=getWidth(),h=getHeight();boolean hover=isHoveredOrFocused();
  g.fill(x,y,x+w,y+h,0xff202329);g.fill(x+1,y+1,x+w-1,y+h-1,hover?0xff929da7:0xff737b84);
  g.fill(x+2,y+2,x+w-2,y+h-2,hover?0xff465365:0xff343d4b);
  g.fill(x+2,y+2,x+4,y+h-2,accent);g.fill(x+5,y+4,x+25,y+24,0xff252b34);
  g.item(icon,x+7,y+6);
  var font=Minecraft.getInstance().font;String text=getMessage().getString();int available=Math.max(1,w-36);
  String clipped=font.plainSubstrByWidth(text,available);
  if(!clipped.equals(text))clipped=font.plainSubstrByWidth(text,Math.max(1,available-font.width("...")))+"...";
  g.text(font,clipped,x+31,y+(h-9)/2,hover?0xffffe6ac:0xffffffff,false);
 }
}
