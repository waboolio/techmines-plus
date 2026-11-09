package gg.techmines.plus;
import java.util.*;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
/** Changes clickable presentation only; incoming text and outgoing sends stay untouched. */
final class ChatMentions {
 static void initialize(){
  ClientReceiveMessageEvents.MODIFY_GAME.register((message,overlay)->{
   var client=Minecraft.getInstance();if(overlay||!TechminesPlus.onTechmines(client))return message;
   var names=client.getConnection().getOnlinePlayers().stream().map(p->p.getProfile().name()).toList();
   return decorate(message,names);
  });
 }
 static Component decorate(Component message,Collection<String> online){
  var sender=ChatTagRules.sender(message.getString(),online);if(sender==null)return message;
  MutableComponent result=Component.empty();int[] cursor={0};
  message.visit((style,part)->{
   int start=cursor[0],end=start+part.length();cursor[0]=end;
   int from=Math.max(start,sender.start()),to=Math.min(end,sender.end());
   if(from>=to){result.append(Component.literal(part).setStyle(style));return Optional.empty();}
   if(from>start)result.append(Component.literal(part.substring(0,from-start)).setStyle(style));
   Style tag=style.withClickEvent(new ClickEvent.SuggestCommand("@"+sender.name()+" ")).withInsertion(ChatTagRules.MARKER+sender.name());
   if(tag.getHoverEvent()==null)tag=tag.withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to tag @"+sender.name())));
   result.append(Component.literal(part.substring(from-start,to-start)).setStyle(tag));
   if(to<end)result.append(Component.literal(part.substring(to-start)).setStyle(style));
   return Optional.empty();
  },Style.EMPTY);
  return result;
 }
}
