package gg.techmines.plus;
import java.util.*;
import net.minecraft.network.chat.*;
import net.minecraft.ChatFormatting;
public final class ReforgeTooltipCheck {
 static int checks;static void ok(boolean value,String why){checks++;if(!value)throw new AssertionError(why);}
 public static void main(String[] args){
  var original=Component.literal("Fortunate Charmander Pickaxe [V]").withStyle(ChatFormatting.GOLD);var stat=Component.literal("Fortune: +57 (+25%)").withStyle(ChatFormatting.YELLOW);List<Component> lines=new ArrayList<>(List.of(original,stat));var match=new ReforgeRules.Match("Fortunate",new ReforgeRules.Tier("Secret","S",0xFF5555));
  ReforgeTooltips.annotate(lines,match);ok(lines.get(0).getString().equals("Fortunate Charmander Pickaxe [V] [S]"),"compact marker appended to title");ok(original.getString().equals("Fortunate Charmander Pickaxe [V]"),"original title component not mutated");ok(lines.get(1)==stat,"stats/lore components preserved");ok(lines.get(0).getStyle().equals(original.getStyle()),"original title style preserved");
  boolean[] red={false};lines.get(0).visit((style,text)->{if(text.equals(" [S]"))red[0]=style.getColor()!=null&&style.getColor().getValue()==0xFF5555;return Optional.empty();},Style.EMPTY);ok(red[0],"secret marker is requested red");ReforgeTooltips.annotate(lines,match);ok(lines.get(0).getString().equals("Fortunate Charmander Pickaxe [V] [S]"),"repeated callback does not duplicate marker");
  List<Component> empty=new ArrayList<>();ReforgeTooltips.annotate(empty,match);ok(empty.isEmpty(),"empty tooltip safe");System.out.println(checks+" tooltip presentation checks passed.");
 }
}
