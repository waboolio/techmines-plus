package gg.techmines.plus;
import java.util.Locale;
final class MenuIconRules {
 enum Kind { BAG, OVERCLOCK }
 static Kind kind(String title){
  String text=title.replaceAll("\u00a7[0-9A-FK-ORa-fk-or]","").replaceAll("[^A-Za-z]","").toLowerCase(Locale.ROOT);
  if(text.endsWith("accessorybag"))return Kind.BAG;
  if(text.endsWith("overclocks"))return Kind.OVERCLOCK;
  return null;
 }
 static int slot(Kind kind){return kind==Kind.BAG?10:13;}
}
