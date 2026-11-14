package gg.techmines.plus;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemLore;
/** Client presentation only: does not change item names, lore, components or server data. */
final class ReforgeTooltips {
 static void initialize(){
  final ReforgeRules rules;
  try(InputStream in=ReforgeTooltips.class.getResourceAsStream("/assets/techmines_plus/data/reforges.json")){
   if(in==null)throw new IOException("Missing reforge catalogue");rules=new ReforgeRules(JsonParser.parseReader(new InputStreamReader(in,StandardCharsets.UTF_8)).getAsJsonObject());
  }catch(IOException|RuntimeException ex){System.getLogger("techmines_reforges").log(System.Logger.Level.WARNING,"Could not load reforge tiers",ex);return;}
  ItemTooltipCallback.EVENT.register((stack,context,flag,lines)->{
   if(lines.isEmpty()||!TechminesPlus.onTechmines(Minecraft.getInstance()))return;
   JsonObject metadata=new JsonObject();var custom=stack.get(DataComponents.CUSTOM_DATA);
   if(custom!=null){var json=NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE,custom.copyTag());if(json.isJsonObject())metadata=json.getAsJsonObject();}
   var lore=stack.getOrDefault(DataComponents.LORE,ItemLore.EMPTY).lines().stream().map(Component::getString).toList();
   var match=rules.find(stack.getHoverName().getString(),metadata,lore);if(match!=null)annotate(lines,match);
  });
 }
 static void annotate(List<Component> lines,ReforgeRules.Match match){
  if(lines.isEmpty())return;String marker="["+match.tier().letter()+"]";if(lines.get(0).getString().endsWith(" "+marker))return;
  lines.set(0,lines.get(0).copy().append(Component.literal(" "+marker).withStyle(style->style.withColor(match.tier().colour()).withBold(true).withItalic(false))));
 }
}
