package gg.techmines.plus;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
public final class VaultClickCheck {
 public static void main(String[] args){
  int[] actions={0,0};var button=new VaultShortcutButton(10,10,"1: Blocks",1,0xff336699,b->actions[0]++,()->actions[1]++);
  if(!button.mouseClicked(new MouseButtonEvent(20,20,new MouseButtonInfo(1,0)),false)||actions[1]!=1||actions[0]!=0)throw new AssertionError("right click must rename without opening vault");
  button.onPress(new MouseButtonInfo(0,0));if(actions[0]!=1||actions[1]!=1)throw new AssertionError("normal activation opens vault");
  button.active=false;button.mouseClicked(new MouseButtonEvent(20,20,new MouseButtonInfo(1,0)),false);if(actions[1]!=1)throw new AssertionError("disabled button cannot rename");
  button.active=true;button.mouseClicked(new MouseButtonEvent(200,200,new MouseButtonInfo(1,0)),false);if(actions[1]!=1)throw new AssertionError("outside click cannot rename");
  System.out.println("4 actual vault-button click checks passed.");
 }
}
