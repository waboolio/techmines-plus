package gg.techmines.plus.mixin;
import gg.techmines.plus.ChatTagRules;
import gg.techmines.plus.TechminesPlus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(ChatScreen.class)
public abstract class ChatNameClick {
 @Shadow protected EditBox input;
 @Inject(method="handleComponentClicked",at=@At("HEAD"),cancellable=true)
 private void techminesTag(Style style,boolean insertionMode,CallbackInfoReturnable<Boolean> ci){
  if(style==null||!TechminesPlus.onTechmines(Minecraft.getInstance()))return;
  String name=ChatTagRules.markedName(style.getInsertion());
  if(name==null||!(style.getClickEvent() instanceof ClickEvent.SuggestCommand event)||!event.command().equals("@"+name+" "))return;
  String draft=input.getValue();String insert=ChatTagRules.insertion(draft,input.getCursorPosition(),name);
  // Do not insert a truncated username when the existing chat draft is full.
  if(draft.length()-input.getHighlighted().length()+insert.length()<=256)input.insertText(insert);
  input.setFocused(true);ci.setReturnValue(true);
 }
}
