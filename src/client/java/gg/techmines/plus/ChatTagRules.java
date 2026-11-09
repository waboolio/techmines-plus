package gg.techmines.plus;
import java.util.*;
import java.util.regex.Pattern;
/** Recognizes the sender before Techmines' separator, never names in message bodies. */
public final class ChatTagRules {
 public static final String MARKER="techmines_plus:tag:";
 private static final Pattern NAME=Pattern.compile("(?<![A-Za-z0-9_])([A-Za-z0-9_]{1,16})(?:[+*])?\\s*$");
 public record Sender(int start,int end,String name){}
 static Sender sender(String message,Collection<String> online){
  int separator=message.indexOf('\u00bb');if(separator<0||message.indexOf('\n')>=0)return null;
  String header=message.substring(0,separator);var match=NAME.matcher(header);if(!match.find())return null;
  String visible=match.group(1);String actual=online.stream().filter(n->n.equalsIgnoreCase(visible)).findFirst().orElse(null);
  if(actual==null){if(!header.contains("[")||!header.contains("]"))return null;actual=visible;}
  return new Sender(match.start(1),match.end(1),actual);
 }
 public static String markedName(String marker){
  if(marker==null||!marker.startsWith(MARKER))return null;String name=marker.substring(MARKER.length());
  return name.matches("[A-Za-z0-9_]{1,16}")?name:null;
 }
 public static String insertion(String text,int cursor,String name){
  int at=Math.max(0,Math.min(text.length(),cursor));
  return (at>0&&!Character.isWhitespace(text.charAt(at-1))?" ":"")+"@"+name+(at==text.length()||!Character.isWhitespace(text.charAt(at))?" ":"");
 }
}
