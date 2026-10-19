package gg.techmines.plus;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

final class VendorCatalog {
 record Cost(String tier,String sword,String tool,String armor) {}
 record Mine(String name,String resource,String toolName,List<Cost> rows,String talisman,String elytra,List<String> notes,String source,VendorTotal.Result total) {}
 record World(String name,List<Mine> mines) {}
 final List<World> worlds;
 final String status;
 private VendorCatalog(List<World> worlds,String status) { this.worlds=worlds;this.status=status; }
 static VendorCatalog load() {
  Path override=FabricLoader.getInstance().getConfigDir().resolve("techmines-plus/vendors.json");
  if(Files.isRegularFile(override)) {
   try(Reader reader=Files.newBufferedReader(override)) { return parse(reader,"Local vendor data"); }
   catch(Exception ex) { LoggerFactory.getLogger("techmines_plus").warn("Invalid local vendor catalogue; using bundled data",ex); return bundled("Local data invalid; using bundled data"); }
  }
  return bundled("Workbook data; some costs not recorded");
 }
 private static VendorCatalog bundled(String status) {
  try(InputStream in=VendorCatalog.class.getResourceAsStream("/assets/techmines_plus/data/vendors.json")) {
   if(in==null) throw new IOException("Missing vendor data");
   return parse(new InputStreamReader(in,StandardCharsets.UTF_8),status);
  } catch(Exception ex) {
   LoggerFactory.getLogger("techmines_plus").error("Could not load vendor catalogue",ex);
   return new VendorCatalog(List.of(),"Vendor data unavailable");
  }
 }
 private static String text(JsonObject o,String key) { return o.has(key)&&!o.get(key).isJsonNull()?o.get(key).getAsString():"Not recorded"; }
 private static VendorCatalog parse(Reader reader,String status) {
  JsonObject root=JsonParser.parseReader(reader).getAsJsonObject();
  if(root.get("schemaVersion").getAsInt()!=1) throw new IllegalArgumentException("Unsupported vendor schema");
  List<World> worlds=new ArrayList<>();
  for(JsonElement entry:root.getAsJsonArray("worlds")) {
   JsonObject w=entry.getAsJsonObject();List<Mine> mines=new ArrayList<>();
   for(JsonElement element:w.getAsJsonArray("mines")) {
    JsonObject m=element.getAsJsonObject();List<Cost> costs=new ArrayList<>();
    for(JsonElement value:m.getAsJsonArray("rows")) {
     JsonObject c=value.getAsJsonObject();costs.add(new Cost(text(c,"tier"),text(c,"sword"),text(c,"tool"),text(c,"armor")));
    }
    if(costs.size()!=5) throw new IllegalArgumentException("Each mine needs five tier rows");
    List<String> notes=new ArrayList<>();
    if(m.has("notes")) for(JsonElement n:m.getAsJsonArray("notes")) notes.add(n.getAsString());
    List<String> totalCosts=new ArrayList<>();
    if(m.has("totalCosts")) for(JsonElement price:m.getAsJsonArray("totalCosts")) totalCosts.add(price.getAsString());
    else {
     for(Cost cost:costs)totalCosts.add(cost.sword());
     for(Cost cost:costs)totalCosts.add(cost.tool());
     for(int i=0;i<3;i++)totalCosts.add(costs.get(i).armor());
     totalCosts.add(text(m,"talisman"));totalCosts.add(text(m,"elytra"));
    }
    List<String> additionalCosts=new ArrayList<>();
    if(m.has("additionalTotalCosts")) for(JsonElement price:m.getAsJsonArray("additionalTotalCosts")) additionalCosts.add(price.getAsString());
    mines.add(new Mine(text(m,"name"),text(m,"resource"),text(m,"toolName"),List.copyOf(costs),text(m,"talisman"),text(m,"elytra"),List.copyOf(notes),text(m,"source"),VendorTotal.calculate(totalCosts,additionalCosts)));
   }
   worlds.add(new World(text(w,"name"),List.copyOf(mines)));
  }
  return new VendorCatalog(List.copyOf(worlds),status);
 }
}
