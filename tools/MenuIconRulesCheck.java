package gg.techmines.plus;
public final class MenuIconRulesCheck {
 static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 public static void main(String[] args){
  check(MenuIconRules.kind("ACCESSORY BAG")==MenuIconRules.Kind.BAG,"bag title");
  check(MenuIconRules.kind("\u00a75Accessory Bag (1/7)")==MenuIconRules.Kind.BAG,"formatted paged bag title");
  check(MenuIconRules.kind("| Overclocks")==MenuIconRules.Kind.OVERCLOCK,"provided overclock title");
  check(MenuIconRules.kind("\u00a7bI Overclocks")==MenuIconRules.Kind.OVERCLOCK,"formatted overclock title");
  for(String title:new String[]{"Inventory","Warps","Auction House","Overclock Helmet","Personal Vault 1","Accessory Bag Help"})check(MenuIconRules.kind(title)==null,"unrelated menu ignored: "+title);
  check(MenuIconRules.slot(MenuIconRules.Kind.BAG)==10,"bag icon uses pictured first item on second row");
  check(MenuIconRules.slot(MenuIconRules.Kind.OVERCLOCK)==13,"overclock icon uses pictured center item on second row");
  System.out.println("PASS: exact screenshot menu slots, title formatting and unrelated-menu rejection.");
 }
}
