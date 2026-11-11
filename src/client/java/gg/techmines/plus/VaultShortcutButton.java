package gg.techmines.plus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
final class VaultShortcutButton extends Button {
 private final int color;private final Runnable rename;
 VaultShortcutButton(int x,int y,String label,int vault,int color,OnPress open,Runnable rename){
  super(x,y,96,30,Component.literal(label),open,DEFAULT_NARRATION);this.color=color;this.rename=rename;
  setTooltip(Tooltip.create(Component.literal(label+" | /pv "+vault+"\nLeft-click: open | Right-click: rename")));
 }
 @Override public boolean mouseClicked(MouseButtonEvent event,boolean twice){
  if(event.button()==1&&active&&visible&&isMouseOver(event.x(),event.y())){rename.run();return true;}
  return super.mouseClicked(event,twice);
 }
 @Override protected void extractContents(GuiGraphicsExtractor g,int mx,int my,float dt){
  int x=getX(),y=getY();g.fill(x,y,x+getWidth(),y+getHeight(),0xff373737);g.fill(x+1,y+1,x+getWidth()-1,y+getHeight()-1,isHoveredOrFocused()?0xff8b9ba9:0xff626b75);
  int shade=0xff000000|((color&0x00fefefe)>>>1);g.fill(x+5,y+6,x+23,y+24,0xff292420);g.fill(x+6,y+7,x+22,y+13,color);g.fill(x+6,y+14,x+22,y+23,shade);g.fill(x+7,y+15,x+21,y+22,color);g.fill(x+12,y+11,x+15,y+17,0xffffe39b);
  var font=Minecraft.getInstance().font;String name=getMessage().getString();int available=getWidth()-32;String shown=font.plainSubstrByWidth(name,available);if(!shown.equals(name))shown=font.plainSubstrByWidth(name,available-font.width("..."))+"...";
  g.text(font,shown,x+28,y+11,0xffffffff,false);
 }
}
