package gg.techmines.plus;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;
import com.google.gson.*;
/** Local, per-account vault labels shared by the public and personal editions. */
final class VaultNames {
 private final Path file;private final Map<String,Map<Integer,String>> names=new HashMap<>();private boolean blocked;String error="";
 VaultNames(Path file){this.file=file;load();}
 static String clean(String name){return name.codePoints().filter(c->!Character.isISOControl(c)&&c!=0x00a7).collect(StringBuilder::new,StringBuilder::appendCodePoint,StringBuilder::append).toString().strip();}
 String custom(String account,int vault){return names.getOrDefault(account,Map.of()).getOrDefault(vault,"");}
 String label(String account,int vault){String name=custom(account,vault);return name.isEmpty()?"Vault "+vault:vault+": "+name;}
 boolean rename(String account,int vault,String input){
  error="";String name=clean(input);
  if(vault<1||vault>7||account.isBlank()||name.codePointCount(0,name.length())>24){error="Use up to 24 characters.";return false;}
  if(blocked){error="Saved names could not load; original file preserved.";return false;}
  var updated=new HashMap<>(names.getOrDefault(account,Map.of()));if(name.isEmpty())updated.remove(vault);else updated.put(vault,name);
  JsonObject players=new JsonObject();var next=new HashMap<>(names);if(updated.isEmpty())next.remove(account);else next.put(account,updated);
  for(var player:next.entrySet()){JsonObject vaults=new JsonObject();player.getValue().forEach((id,label)->vaults.addProperty(id.toString(),label));players.add(player.getKey(),vaults);}
  JsonObject root=new JsonObject();root.addProperty("schemaVersion",1);root.add("players",players);
  try{Files.createDirectories(file.getParent());Path temp=file.resolveSibling(file.getFileName()+".tmp");Files.writeString(temp,new GsonBuilder().setPrettyPrinting().create().toJson(root));
   try{Files.move(temp,file,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException ex){Files.move(temp,file,StandardCopyOption.REPLACE_EXISTING);}
   names.clear();names.putAll(next);return true;
  }catch(IOException|RuntimeException ex){error="Could not save names. Try again.";return false;}
 }
 private void load(){
  if(!Files.exists(file))return;
  try{JsonObject root=JsonParser.parseString(Files.readString(file)).getAsJsonObject();if(root.get("schemaVersion").getAsInt()!=1)throw new IOException("Unknown version");
   for(var player:root.getAsJsonObject("players").entrySet()){
    Map<Integer,String> labels=new HashMap<>();for(var vault:player.getValue().getAsJsonObject().entrySet()){
     int id=Integer.parseInt(vault.getKey());String label=vault.getValue().getAsString();if(id<1||id>7||!label.equals(clean(label))||label.codePointCount(0,label.length())>24)throw new IOException("Invalid saved name");labels.put(id,label);
    }names.put(player.getKey(),labels);
   }
  }catch(IOException|RuntimeException ex){names.clear();blocked=true;error="Saved names could not load; original file preserved.";}
 }
}
