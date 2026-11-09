package gg.techmines.plus;
import java.util.*;
import net.minecraft.network.chat.*;
import net.minecraft.ChatFormatting;
public final class ChatTagCheck {
 static int checks;static void ok(boolean value,String why){checks++;if(!value)throw new AssertionError(why);}
 static String sender(String text){var s=ChatTagRules.sender(text,List.of("Gibb","Ben","Betterrng"));return s==null?null:s.name();}
 public static void main(String[] args){
  ok(sender("[\u2605347] MVP+ gibb \u00bb i GG i").equals("Gibb"),"canonical account case");
  ok(sender("[324] MVP Ben \u00bb hello @Gibb").equals("Ben"),"body mention is not sender");
  ok(sender("[383] MVP+ Betterrng+ \u00bb hello").equals("Betterrng"),"cosmetic plus outside username");
  ok(sender("Ben \u00bb hello").equals("Ben"),"known unranked sender");
  ok(sender("[421] LEGEND someone_new \u00bb hello").equals("someone_new"),"ranked sender absent from tab list");
  for(String text:List.of("PURCHASE! Ben has made a large purchase!","Type gg in chat","CHAT GAME COMPLETE","Server \u00bb restart","[rank] abcdefghijklmnopq \u00bb hi","[rank] Ben \u00bb hi\n[rank] Gibb \u00bb another"))ok(sender(text)==null,"non-player/ambiguous message: "+text);
  ok(ChatTagRules.insertion("",0,"Ben").equals("@Ben "),"empty draft");ok(ChatTagRules.insertion("hello",5,"Ben").equals(" @Ben "),"space before tag");ok(ChatTagRules.insertion("hello ",6,"Ben").equals("@Ben "),"no doubled space");ok(ChatTagRules.insertion("hello world",6,"Ben").equals("@Ben "),"cursor insertion between words");ok(ChatTagRules.insertion("hello world",5,"Ben").equals(" @Ben"),"existing following whitespace retained");
  ok(ChatTagRules.markedName(ChatTagRules.MARKER+"Ben").equals("Ben"),"our click marker");for(String marker:List.of("Ben","https://example.com",ChatTagRules.MARKER+"Ben /op",ChatTagRules.MARKER+""))ok(ChatTagRules.markedName(marker)==null,"invalid marker rejected");
  Component body=Component.literal("visit example.com").withStyle(s->s.withClickEvent(new ClickEvent.SuggestCommand("original body action")).withColor(ChatFormatting.GREEN));
  Component message=Component.literal("[347] MVP+ ").withStyle(ChatFormatting.GOLD).append(Component.literal("Gi").withStyle(ChatFormatting.AQUA)).append(Component.literal("bb").withStyle(ChatFormatting.BOLD)).append(" \u00bb ").append(body);
  Component tagged=ChatMentions.decorate(message,List.of("Gibb"));ok(tagged.getString().equals(message.getString()),"visible incoming text unchanged");
  List<String> taggedText=new ArrayList<>();boolean[] bodyPreserved={false},rankPreserved={false},boldPreserved={false};
  tagged.visit((style,text)->{if(ChatTagRules.markedName(style.getInsertion())!=null){taggedText.add(text);ok(style.getClickEvent() instanceof ClickEvent.SuggestCommand event&&event.command().equals("@Gibb "),"only draft suggestion attached");if(text.equals("bb"))boldPreserved[0]=style.isBold();}if(text.equals("visit example.com"))bodyPreserved[0]=style.getClickEvent().equals(body.getStyle().getClickEvent())&&style.getColor().equals(body.getStyle().getColor());if(text.startsWith("[347]"))rankPreserved[0]=style.getColor()!=null;return Optional.empty();},Style.EMPTY);
  ok(String.join("",taggedText).equals("Gibb"),"only sender span clickable across styled fragments");ok(bodyPreserved[0],"body links and colour preserved");ok(rankPreserved[0]&&boldPreserved[0],"rank and name styles preserved");
  Component server=Component.literal("PURCHASE! Thank you!");ok(ChatMentions.decorate(server,List.of("Gibb"))==server,"server message untouched");
  System.out.println(checks+" chat tagging checks passed.");
 }
}
