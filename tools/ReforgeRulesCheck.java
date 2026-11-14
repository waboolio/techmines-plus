package gg.techmines.plus;
import java.nio.file.*;
import java.util.*;
import com.google.gson.*;
public final class ReforgeRulesCheck {
 static int checks;static void ok(boolean value,String why){checks++;if(!value)throw new AssertionError(why);}
 static JsonObject meta(String name,String tier,boolean pdc){JsonObject root=new JsonObject(),tag=new JsonObject();if(pdc){tag.addProperty("miningplugin:reforge_reforgename",name);tag.addProperty("miningplugin:reforge_reforgerarity",tier);root.add("PublicBukkitValues",tag);}else{tag.addProperty("reforgeName",name);tag.addProperty("reforgeRarity",tier);root.add("reforge",tag);}return root;}
 public static void main(String[] args)throws Exception{
  var data=JsonParser.parseString(Files.readString(Path.of(args[0]))).getAsJsonObject();var rules=new ReforgeRules(data);int definitions=0;
  for(var reforge:data.getAsJsonArray("reforges")){var r=reforge.getAsJsonObject();String name=r.get("name").getAsString();for(int i=0;i<8;i++){var tier=data.getAsJsonArray("tiers").get(i).getAsJsonObject();String rarity=tier.get("name").getAsString();List<String> lore=new ArrayList<>();for(var stat:r.getAsJsonObject("bonuses").entrySet()){var rate=stat.getValue().getAsJsonArray().get(i).getAsBigDecimal();lore.add(stat.getKey()+": +123 ("+(rate.signum()>=0?"+":"")+rate.toPlainString()+"%)");}
   var match=rules.find(name+" Test Item",new JsonObject(),lore);ok(match!=null&&match.tier().name().equals(rarity),"full bonuses: "+name+" "+rarity);ok(match.tier().letter().equals(tier.get("letter").getAsString())&&match.tier().colour()==Integer.parseInt(tier.get("hex").getAsString().substring(1),16),"user marker colour: "+rarity);
   for(boolean pdc:new boolean[]{false,true}){match=rules.find(name+" Test Item",meta(name,rarity.toLowerCase(Locale.ROOT),pdc),List.of());ok(match!=null&&match.tier().name().equals(rarity),"exact metadata format: "+name+" "+rarity+" "+pdc);}definitions++;
  }}
  ok(definitions==168,"all screenshot tier entries covered");
  ok(rules.find("Fortunate Charmander Pickaxe [V]",new JsonObject(),List.of("Fortune: +57 (+25%)")).tier().name().equals("Secret"),"provided Fortunate screenshot");
  ok(rules.find("Godly Galaxy",new JsonObject(),List.of("Fortune: +1% (+1%) (+1%)")).tier().name().equals("Epic"),"first bracket is reforge; amplifier ignored");
  ok(rules.find("Itchy Galaxy",new JsonObject(),List.of("Damage: +10 (+6.25%)","Health: +20 (-2%)")).tier().name().equals("Secret"),"negative Itchy penalty");
  ok(rules.find("Swift Pickaxe",new JsonObject(),List.of("Efficiency: +200 (+25%)","Fortune: +30 (-8%)")).tier().name().equals("Secret"),"Swift penalty and efficiency alias");
  for(var lore:List.of(List.of("Mining Speed: +20 (+25%)"),List.of("Mining Speed: +20 (+25%)","Fortune: +30 (+8%)")))ok(rules.find("Swift Pickaxe",new JsonObject(),lore)==null,"partial/wrong penalty never guessed");
  ok(rules.find("Godly Galaxy",new JsonObject(),List.of("Fortune: +1% (1.5x) (+1%)"))==null,"unrelated later brackets ignored");
  ok(rules.find("Godly Galaxy",meta("Godly","future-rarity",true),List.of("Fortune: +1% (+1%)"))==null,"unknown explicit rarity not guessed");
  ok(rules.find("Galaxy",meta("Godly","secret",true),List.of())==null,"reforge metadata must match item name");
  ok(rules.find("Fortunate Charmander Pickaxe",meta("Fortunate","rare",true),List.of("Fortune: +57 (+25%)")).tier().name().equals("Rare"),"exact server metadata takes priority over rendered modifiers");
  JsonObject base=new JsonObject();base.addProperty("rarity","secret");ok(rules.find("Galaxy",base,List.of("Fortune: +57"))==null,"base item rarity is not reforge rarity");ok(rules.find("Godly Galaxy",new JsonObject(),List.of())==null,"name alone is insufficient");
  if(args.length>1){int matched=0;var offers=JsonParser.parseString(Files.readString(Path.of(args[1]))).getAsJsonArray();for(var value:offers){var e=value.getAsJsonObject();if(!e.has("icon")||e.get("icon").isJsonNull())continue;var components=e.getAsJsonObject("icon").getAsJsonObject("components");if(components==null||!components.has("minecraft:custom_data"))continue;var metadata=components.getAsJsonObject("minecraft:custom_data");JsonObject pdc=metadata.has("PublicBukkitValues")?metadata.getAsJsonObject("PublicBukkitValues"):new JsonObject();JsonObject old=metadata.has("reforge")?metadata.getAsJsonObject("reforge"):new JsonObject();String rarity=pdc.has("miningplugin:reforge_reforgerarity")?pdc.get("miningplugin:reforge_reforgerarity").getAsString():old.has("reforgeRarity")?old.get("reforgeRarity").getAsString():"";String name=pdc.has("miningplugin:reforge_reforgename")?pdc.get("miningplugin:reforge_reforgename").getAsString():old.has("reforgeName")?old.get("reforgeName").getAsString():"";String title=e.get("name").getAsString();if(name.isBlank()||rarity.isBlank()||!title.toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT)+" "))continue;List<String> lore=new ArrayList<>();for(var line:e.getAsJsonArray("lore"))lore.add(line.getAsString());var match=rules.find(title,metadata,lore);ok(match!=null&&match.tier().name().equalsIgnoreCase(rarity),"actual saved item: "+title);matched++;}System.out.println("Matched exact reforge rarity on "+matched+" locally recorded real items.");}
  System.out.println(checks+" reforge detection/colour checks passed.");
 }
}
