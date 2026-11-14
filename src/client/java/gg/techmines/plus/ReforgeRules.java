package gg.techmines.plus;
import java.util.*;
import java.util.regex.Pattern;
import java.math.BigDecimal;
import com.google.gson.*;
/** Reforge rarity, never the item's base rarity. Explicit server data has priority. */
final class ReforgeRules {
 record Tier(String name,String letter,int colour){}
 record Match(String reforge,Tier tier){}
 private record Definition(String name,Map<String,List<BigDecimal>> bonuses){}
 private final List<Tier> tiers=new ArrayList<>();private final List<Definition> definitions=new ArrayList<>();
 private static final Pattern PERCENT=Pattern.compile("([+-]?[0-9]+(?:\\.[0-9]+)?)\\s*%");
 ReforgeRules(JsonObject data){
  if(data.get("schemaVersion").getAsInt()!=1)throw new IllegalArgumentException("Unknown reforge data");
  for(var t:data.getAsJsonArray("tiers")){var o=t.getAsJsonObject();tiers.add(new Tier(o.get("name").getAsString(),o.get("letter").getAsString(),Integer.parseInt(o.get("hex").getAsString().substring(1),16)));}
  if(tiers.size()!=8)throw new IllegalArgumentException("Eight tiers required");
  for(var value:data.getAsJsonArray("reforges")){var o=value.getAsJsonObject();Map<String,List<BigDecimal>> bonuses=new LinkedHashMap<>();for(var stat:o.getAsJsonObject("bonuses").entrySet()){List<BigDecimal> rates=new ArrayList<>();for(var rate:stat.getValue().getAsJsonArray())rates.add(rate.getAsBigDecimal());if(rates.size()!=8)throw new IllegalArgumentException("Eight rates required");bonuses.put(stat(stat.getKey()),rates);}definitions.add(new Definition(o.get("name").getAsString(),bonuses));}
 }
 static String clean(String text){return text.replaceAll("\u00a7[0-9A-FK-ORa-fk-or]","").replace('\u00a0',' ').strip();}
 private static String stat(String text){String s=clean(text).toLowerCase(Locale.ROOT).replaceAll("[^a-z]","");return switch(s){case "fortune","miningfortune"->"miningfortune";case "efficiency","miningspeed"->"miningspeed";default->s;};}
 private static JsonObject object(JsonObject parent,String key){var value=parent.get(key);return value!=null&&value.isJsonObject()?value.getAsJsonObject():new JsonObject();}
 private static String text(JsonObject object,String key){var value=object.get(key);return value!=null&&value.isJsonPrimitive()&&value.getAsJsonPrimitive().isString()?value.getAsString():"";}
 private Definition definition(String title){String name=clean(title).toLowerCase(Locale.ROOT);return definitions.stream().filter(d->name.startsWith(d.name().toLowerCase(Locale.ROOT)+" ")).max(Comparator.comparingInt(d->d.name().length())).orElse(null);}
 private Match explicit(String title,String name,String rarity){
  if(name.isBlank()){var known=definition(title);if(known!=null)name=known.name();}
  final String chosen=name;if(chosen.isBlank()||!clean(title).toLowerCase(Locale.ROOT).startsWith(clean(chosen).toLowerCase(Locale.ROOT)+" "))return null;
  return tiers.stream().filter(t->t.name().equalsIgnoreCase(rarity)).findFirst().map(t->new Match(chosen,t)).orElse(null);
 }
 Match find(String title,JsonObject metadata,List<String> lore){
  JsonObject pdc=object(metadata,"PublicBukkitValues");String rarity=text(pdc,"miningplugin:reforge_reforgerarity");
  if(!rarity.isBlank())return explicit(title,text(pdc,"miningplugin:reforge_reforgename"),rarity);
  JsonObject legacy=object(metadata,"reforge");rarity=text(legacy,"reforgeRarity");if(!rarity.isBlank())return explicit(title,text(legacy,"reforgeName"),rarity);
  Definition known=definition(title);if(known==null)return null;Map<String,BigDecimal> observed=new HashMap<>();
  for(String raw:lore){String line=clean(raw);int colon=line.indexOf(':'),open=line.indexOf('(',colon+1),close=line.indexOf(')',open+1);if(colon<0||open<0||close<0)continue;
   String stat=stat(line.substring(0,colon));var percent=PERCENT.matcher(line.substring(open+1,close).strip());if(!percent.matches())continue;
   BigDecimal value=new BigDecimal(percent.group(1));var prior=observed.put(stat,value);if(prior!=null&&prior.compareTo(value)!=0)return null;
  }
  Match match=null;for(int i=0;i<8;i++){boolean equal=true;for(var expected:known.bonuses().entrySet()){var actual=observed.get(expected.getKey());if(actual==null||actual.compareTo(expected.getValue().get(i))!=0){equal=false;break;}}
   if(equal){if(match!=null)return null;match=new Match(known.name(),tiers.get(i));}
  }return match;
 }
}
