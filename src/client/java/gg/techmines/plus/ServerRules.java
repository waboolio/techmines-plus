package gg.techmines.plus;
import java.util.Locale;
/** Stateless checks: never carry Techmines detection across a proxy transfer. */
final class ServerRules {
 static String host(String address){
  if(address==null)return "";
  return address.trim().toLowerCase(Locale.ROOT).replaceFirst(":\\d+$", "").replaceFirst("\\.$", "");
 }
 static boolean direct(String address){return host(address).equals("techmines.minehut.gg");}
 static boolean minehut(String address){
  String h=host(address);
  return h.equals("minehut.com")||h.endsWith(".minehut.com")||h.equals("minehut.gg")||h.endsWith(".minehut.gg");
 }
 static String plain(String value){
  String s=value.replaceAll("\u00a7[0-9A-FK-ORa-fk-or]", "").toLowerCase(Locale.ROOT);
  String small="\u1d1b\u1d07\u1d04\u029c\u1d0d\u026a\u0274\ua731",ascii="techmins";
  for(int i=0;i<small.length();i++)s=s.replace(small.charAt(i),ascii.charAt(i));
  return s.trim();
 }
 static boolean title(String value){return plain(value).replaceAll("[^a-z]", "").equals("techmines");}
 static boolean website(String value){
  String s=plain(value).replaceAll("\\s+", "");
  return s.equals("techmines.net")||s.equals("www.techmines.net")||s.equals("techmines.minehut.gg");
 }
 static boolean active(String address,boolean sidebarPresent,boolean branded){
  return minehut(address)&&(branded||(!sidebarPresent&&direct(address)));
 }
}
