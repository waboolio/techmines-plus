package gg.techmines.plus;
import java.io.IOException;
import java.nio.file.*;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
/** Reuses only copied display stacks from the two observed menus. Never clicks or moves items. */
final class MenuIcons {
 private static Path file;private static Object connection;private static Screen screen;
 private static boolean captured;private static ItemStack bag=ItemStack.EMPTY,overclock=ItemStack.EMPTY;
 static void initialize(Path path){file=path;}
 static ItemStack bag(){return bag.isEmpty()?new ItemStack(Items.RESPAWN_ANCHOR):bag;}
 static ItemStack overclock(){return overclock.isEmpty()?new ItemStack(Items.LAPIS_LAZULI):overclock;}
 static void tick(Minecraft client){
  if(!TechminesPlus.onTechmines(client)||client.level==null){screen=null;return;}
  if(connection!=client.getConnection()){connection=client.getConnection();load(client);screen=null;}
  Screen current=client.gui.screen();if(current!=screen){screen=current;captured=false;}
  if(captured||!(current instanceof AbstractContainerScreen<?> container))return;
  MenuIconRules.Kind kind=MenuIconRules.kind(current.getTitle().getString());if(kind==null)return;
  int slot=MenuIconRules.slot(kind);if(container.getMenu().slots.size()<=slot)return;
  ItemStack stack=container.getMenu().slots.get(slot).getItem();if(stack.isEmpty())return;
  String id=BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
  if(id.endsWith("_stained_glass_pane")||id.equals("barrier")||id.equals("stone_button"))return;
  ItemStack copy=stack.copyWithCount(1);captured=true;
  ItemStack previous=kind==MenuIconRules.Kind.BAG?bag:overclock;
  if(ItemStack.matches(previous,copy))return;
  if(kind==MenuIconRules.Kind.BAG)bag=copy;else overclock=copy;
  save(client);
 }
 private static void load(Minecraft client){
  bag=ItemStack.EMPTY;overclock=ItemStack.EMPTY;
  if(file==null||!Files.isRegularFile(file))return;
  try{
   JsonObject data=JsonParser.parseString(Files.readString(file)).getAsJsonObject();
   if(!data.has("schemaVersion")||data.get("schemaVersion").getAsInt()!=1)return;
   var ops=RegistryOps.create(JsonOps.INSTANCE,client.level.registryAccess());
   if(data.has("bag"))bag=ItemStack.CODEC.parse(ops,data.get("bag")).result().orElse(ItemStack.EMPTY);
   if(data.has("overclock"))overclock=ItemStack.CODEC.parse(ops,data.get("overclock")).result().orElse(ItemStack.EMPTY);
  }catch(IOException|RuntimeException ex){warn(ex);}
 }
 private static void save(Minecraft client){
  if(file==null)return;
  try{
   JsonObject data=new JsonObject();data.addProperty("schemaVersion",1);var ops=RegistryOps.create(JsonOps.INSTANCE,client.level.registryAccess());
   if(!bag.isEmpty())ItemStack.CODEC.encodeStart(ops,bag).result().ifPresent(v->data.add("bag",v));
   if(!overclock.isEmpty())ItemStack.CODEC.encodeStart(ops,overclock).result().ifPresent(v->data.add("overclock",v));
   Files.createDirectories(file.getParent());Path temp=file.resolveSibling(file.getFileName()+".tmp");Files.writeString(temp,new GsonBuilder().setPrettyPrinting().create().toJson(data));
   try{Files.move(temp,file,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}catch(AtomicMoveNotSupportedException ex){Files.move(temp,file,StandardCopyOption.REPLACE_EXISTING);}
  }catch(IOException|RuntimeException ex){warn(ex);}
 }
 private static void warn(Exception ex){System.getLogger("techmines_plus_icons").log(System.Logger.Level.WARNING,"Could not read/save local menu icons",ex);}
}
