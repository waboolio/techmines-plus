package gg.techmines.plus;
import java.nio.file.*;
import java.util.*;
public final class VaultPlayersCheck {
 static int checks;static void ok(boolean value,String why){checks++;if(!value)throw new AssertionError(why);}
 public static void main(String[] args)throws Exception{
  Path dir=Files.createTempDirectory("vault-players-check-");Path file=dir.resolve("vault-names.json");var names=new VaultNames(file);
  ok(names.label("account-A",1).equals("Vault 1"),"default labels");ok(!Files.exists(file),"opening names does not create or overwrite file");
  ok(names.rename("account-A",1," Mining Blocks "),"rename saves");ok(names.label("account-A",1).equals("1: Mining Blocks"),"name retains vault number");ok(names.label("account-A",2).equals("Vault 2"),"other vault untouched");
  ok(new VaultNames(file).custom("account-A",1).equals("Mining Blocks"),"restart persistence");ok(names.rename("account-B",1,"Armour"),"second account saves");var loaded=new VaultNames(file);ok(loaded.custom("account-A",1).equals("Mining Blocks")&&loaded.custom("account-B",1).equals("Armour"),"account labels independent");
  ok(loaded.rename("account-A",7,"Tools \u26cf"),"Unicode name saves");ok(new VaultNames(file).custom("account-A",7).equals("Tools \u26cf"),"Unicode round trip");
  String before=Files.readString(file);ok(!loaded.rename("account-A",0,"bad")&&!loaded.rename("account-A",8,"bad"),"invalid vault IDs rejected");ok(!loaded.rename("account-A",1,"a".repeat(25)),"length limit enforced");ok(Files.readString(file).equals(before),"invalid input preserves saved labels");
  ok(loaded.rename("account-A",1,""),"reset saves");ok(loaded.label("account-A",1).equals("Vault 1")&&new VaultNames(file).custom("account-B",1).equals("Armour"),"reset affects only selected vault/account");
  Files.writeString(file,"unreadable original");var bad=new VaultNames(file);ok(!bad.rename("account-A",1,"new"),"unreadable data blocks overwrite");ok(Files.readString(file).equals("unreadable original"),"unreadable original preserved");
  Path blocker=dir.resolve("blocker");Files.writeString(blocker,"not a folder");var fail=new VaultNames(blocker.resolve("vault-names.json"));ok(!fail.rename("account-A",1,"new"),"write failure reported");ok(fail.label("account-A",1).equals("Vault 1"),"failed save preserves in-memory label");
  List<PlayerRoster.Player> all=new ArrayList<>();for(int i=0;i<241;i++)all.add(new PlayerRoster.Player(new UUID(0,i),String.format(Locale.ROOT,"Player%03d",i),i));
  ok(PlayerRoster.search(all,"").size()==241,"all 241 players retained beyond vanilla 80 cap");ok(PlayerRoster.search(all,"player240").size()==1,"player beyond Tab cap searchable");ok(PlayerRoster.search(all," PLAYER240 ").get(0).name().equals("Player240"),"case-insensitive search");ok(PlayerRoster.search(all,"no-such-name").isEmpty(),"no-match search");
  Collections.reverse(all);ok(PlayerRoster.search(all,"").get(0).name().equals("Player000"),"alphabetical order");all.add(new PlayerRoster.Player(new UUID(0,240),"Player240",5));ok(PlayerRoster.search(all,"").size()==241,"UUID duplicates do not inflate count");ok(PlayerRoster.search(all,"player240").get(0).latency()==5,"latest latency used");ok(PlayerRoster.search(List.of(),"").isEmpty(),"disconnect clears roster");
  for(int height:new int[]{240,256,270,360,480}){int rows=Math.max(1,(height-142)/16);ok(73+(rows-1)*16+13<height-58,"list stays above footer "+height);int y=Math.max(6,(height-194)/2);ok(y+26+3*36+30<y+174&&y+194<=height,"vault grid fits "+height);}
  System.out.println(checks+" vault-name persistence and player-roster checks passed.");
 }
}
