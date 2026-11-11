package gg.techmines.plus;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
final class PlayersScreen extends Screen {
 private final Screen parent;private List<PlayerRoster.Player> all=List.of(),rows=List.of();private String query="";private int page;private long refreshed;private Button previous,next;private int w,x,perPage;
 PlayersScreen(Screen parent){super(Component.literal("Players"));this.parent=parent;}
 private void refresh(){
  var client=Minecraft.getInstance();if(TechminesPlus.onTechmines(client))all=client.getConnection().getListedOnlinePlayers().stream().map(p->new PlayerRoster.Player(p.getProfile().id(),p.getProfile().name(),p.getLatency())).toList();else all=List.of();
  rows=PlayerRoster.search(all,query);page=Math.min(page,Math.max(0,(rows.size()-1)/perPage));refreshed=System.nanoTime();
 }
 @Override protected void init(){w=Math.min(420,width-16);x=(width-w)/2;perPage=Math.max(2,((height-142)/16)*2);refresh();
  EditBox search=new EditBox(font,x,30,w,20,Component.literal("Search players"));search.setMaxLength(64);search.setHint(Component.literal("Search player names..."));search.setValue(query);search.setResponder(value->{query=value;page=0;rows=PlayerRoster.search(all,query);});addRenderableWidget(search);
  previous=addRenderableWidget(Button.builder(Component.literal("Previous"),b->page=Math.max(0,page-1)).bounds(x,height-28,72,20).build());next=addRenderableWidget(Button.builder(Component.literal("Next"),b->page=Math.min(Math.max(0,(rows.size()-1)/perPage),page+1)).bounds(x+w-72,height-28,72,20).build());addRenderableWidget(Button.builder(Component.literal("Back"),b->onClose()).bounds(width/2-34,height-28,68,20).build());
 }
 @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float dt){
  if(System.nanoTime()-refreshed>1_000_000_000L)refresh();previous.active=page>0;next.active=(page+1)*perPage<rows.size();g.fill(x-5,5,x+w+5,height-5,0xee202630);super.extractRenderState(g,mx,my,dt);g.centeredText(font,title,width/2,13,0xffffffff);
  g.text(font,rows.size()+" matches | "+all.size()+" server-listed players",x,56,0xff8fdeb1,false);int col=w/2;
  for(int i=0;i<perPage&&page*perPage+i<rows.size();i++){var player=rows.get(page*perPage+i);int bx=x+(i%2)*col,by=73+(i/2)*16;if((i/2)%2==0)g.fill(bx,by-2,bx+col-2,by+13,0x443d5366);String ping=player.latency()+"ms";g.text(font,font.plainSubstrByWidth(player.name(),Math.max(10,col-font.width(ping)-12)),bx+3,by,0xffffffff,false);g.text(font,ping,bx+col-font.width(ping)-5,by,0xffaab8c7,false);}
  g.centeredText(font,"Page "+(page+1)+" / "+Math.max(1,(rows.size()+perPage-1)/perPage),width/2,height-58,0xffbbbbbb);g.centeredText(font,"Shows the roster supplied by the server",width/2,height-44,0xffbbbbbb);
 }
 @Override public void onClose(){Minecraft.getInstance().gui.setScreen(parent);}
 @Override public boolean isPauseScreen(){return false;}
}
