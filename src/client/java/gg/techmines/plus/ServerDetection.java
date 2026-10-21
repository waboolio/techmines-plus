package gg.techmines.plus;
import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.PlayerTeam;
/** Uses only the currently visible sidebar; chat announcements cannot enable the mod. */
final class ServerDetection {
 static boolean active(Minecraft client){
  if(client.player==null||client.level==null||client.getConnection()==null||client.getCurrentServer()==null)return false;
  String address=client.getCurrentServer().ip;
  if(!ServerRules.minehut(address))return false;
  var board=client.level.getScoreboard();
  var objective=board.getDisplayObjective(DisplaySlot.SIDEBAR);
  var team=board.getPlayersTeam(client.player.getScoreboardName());
  if(team!=null&&team.getColor().isPresent()){
   var colored=board.getDisplayObjective(team.getColor().get().displaySlot());
   if(colored!=null)objective=colored;
  }
  if(objective==null)return ServerRules.active(address,false,false);
  boolean branded=ServerRules.title(objective.getDisplayName().getString());
  if(!branded)for(var score:board.listPlayerScores(objective)){
   if(score.owner().startsWith("#"))continue;
   String line=PlayerTeam.formatNameForTeam(board.getPlayersTeam(score.owner()),score.ownerName()).getString();
   if(ServerRules.website(line)){branded=true;break;}
  }
  return ServerRules.active(address,true,branded);
 }
}
