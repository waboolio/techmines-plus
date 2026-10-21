package gg.techmines.plus;
public final class ServerRulesCheck {
 static void check(boolean b){if(!b)throw new AssertionError();}
 public static void main(String[] args){
  check(ServerRules.active("mc.minehut.com",true,ServerRules.title("TECHMINES")));
  check(ServerRules.active("mc.minehut.com:25565",true,ServerRules.website("techmines.net")));
  check(ServerRules.active(" TECHMINES.MINEHUT.GG:25565 ",false,false));
  check(!ServerRules.active("mc.minehut.com",false,false));
  check(!ServerRules.active("mc.minehut.com",true,false));
  check(!ServerRules.active("techmines.minehut.gg",true,false));
  check(!ServerRules.active("other.example.com",true,true));
  check(!ServerRules.active("mc.minehut.com.evil.example",true,true));
  check(!ServerRules.title("Other server Techmines player"));
  check(ServerRules.title("\u00a7bTECHMINES"));
  check(ServerRules.title("\u1d1b\u1d07\u1d04\u029c\u1d0d\u026a\u0274\u1d07\ua731"));
  check(!ServerRules.website("visit techmines.net sometime"));
  check(ServerRules.active("mc.minehut.com",true,true));
  check(!ServerRules.active("mc.minehut.com",true,false));
  check(ServerRules.active("mc.minehut.com",true,true));
  System.out.println("Server detection checks passed: direct join, lobby transfer, return to lobby, rejoin, unrelated hosts and formatting.");
 }
}
