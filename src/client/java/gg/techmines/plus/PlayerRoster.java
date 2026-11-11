package gg.techmines.plus;
import java.util.*;
final class PlayerRoster {
 record Player(UUID id,String name,int latency){}
 static List<Player> search(Collection<Player> source,String query){
  String filter=query.strip().toLowerCase(Locale.ROOT);Map<UUID,Player> unique=new HashMap<>();for(Player player:source)unique.put(player.id(),player);
  return unique.values().stream().filter(p->p.name().toLowerCase(Locale.ROOT).contains(filter)).sorted(Comparator.comparing(Player::name,String.CASE_INSENSITIVE_ORDER).thenComparing(Player::name)).toList();
 }
}
