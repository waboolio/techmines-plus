package gg.techmines.plus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
final class VaultRenameScreen extends Screen {
 private final Screen parent;private final VaultNames names;private final String account;private final int vault;private EditBox input;private String draft,error="";
 VaultRenameScreen(Screen parent,VaultNames names,String account,int vault){super(Component.literal("Rename Vault "+vault));this.parent=parent;this.names=names;this.account=account;this.vault=vault;draft=names.custom(account,vault);}
 private int left(){return width/2-120;}private int top(){return Math.max(12,(height-125)/2);}
 @Override protected void init(){int x=left(),y=top();input=new EditBox(font,x,y+27,240,20,Component.literal("Vault name"));input.setMaxLength(24);input.setHint(Component.literal("Vault "+vault));input.setValue(draft);input.setResponder(value->draft=value);addRenderableWidget(input);setInitialFocus(input);
  addRenderableWidget(Button.builder(Component.literal("Save"),b->save(draft)).bounds(x,y+55,76,20).build());addRenderableWidget(Button.builder(Component.literal("Reset"),b->save("")).bounds(x+82,y+55,76,20).build());addRenderableWidget(Button.builder(Component.literal("Cancel"),b->onClose()).bounds(x+164,y+55,76,20).build());
 }
 private void save(String label){if(names.rename(account,vault,label))onClose();else error=names.error;}
 @Override public boolean keyPressed(KeyEvent event){if(event.key()==257||event.key()==335){save(draft);return true;}return super.keyPressed(event);}
 @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float dt){int x=left(),y=top();g.fill(x-8,y-6,x+248,y+108,0xee202630);super.extractRenderState(g,mx,my,dt);g.centeredText(font,title,width/2,y+5,0xffffffff);g.centeredText(font,"Local name for this account | /pv "+vault,width/2,y+82,0xffbbbbbb);if(!error.isEmpty())g.centeredText(font,Component.literal(error),width/2,y+96,0xffff9988);}
 @Override public void onClose(){Minecraft.getInstance().gui.setScreen(parent);}
 @Override public boolean isPauseScreen(){return false;}
}
