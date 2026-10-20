package gg.techmines.plus;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

final class VendorInfoScreen extends Screen {
 private final Screen parent;
 private final VendorCatalog catalog;
 private final VendorCatalog.World world;
 private final int mineIndex;
 private int page;
 private int perPage;
 private int left,boxWidth;
 private VendorInfoScreen(Screen parent,VendorCatalog catalog,VendorCatalog.World world,int mineIndex) {
  super(Component.literal(mineIndex>=0?world.mines().get(mineIndex).name():world==null?"Vendor Info":world.name()));
  this.parent=parent;this.catalog=catalog;this.world=world;this.mineIndex=mineIndex;
 }
 static void open(Screen parent) { Minecraft.getInstance().gui.setScreen(new VendorInfoScreen(parent,VendorCatalog.load(),null,-1)); }
 private void show(Screen screen) { Minecraft.getInstance().gui.setScreen(screen); }
 private Button button(String label,int x,int y,int w,Button.OnPress action) {
  Button b=Button.builder(Component.literal(label),action).bounds(x,y,w,20).build();addRenderableWidget(b);return b;
 }
 @Override protected void init() {
  boxWidth=Math.min(440,width-16);left=(width-boxWidth)/2;
  if(mineIndex<0) {
   int count=world==null?catalog.worlds.size():world.mines().size();
   int stride=world==null?32:24;
   perPage=Math.max(2,((height-104)/stride)*2);
   int pages=Math.max(1,(count+perPage-1)/perPage);page=Math.min(page,pages-1);
   for(int i=page*perPage;i<Math.min(count,(page+1)*perPage);i++) {
    final int index=i; int pos=i-page*perPage;String name;
    if(world==null) { VendorCatalog.World w=catalog.worlds.get(i);name=w.name()+(w.mines().isEmpty()?" [no data]":""); }
    else name=world.mines().get(i).name();
    int bx=left+(pos%2)*(boxWidth/2+2),by=48+(pos/2)*stride,bw=boxWidth/2-2;
    if(world==null){
     VendorCatalog.World target=catalog.worlds.get(index);
     addRenderableWidget(new WorldButton(target.name(),bx,by,bw,b -> show(new VendorInfoScreen(this,catalog,target,-1))));
    }else button(name,bx,by,bw,b -> show(new VendorInfoScreen(this,catalog,world,index)));

   }
   if(pages>1) {
    Button prev=button("<",left,height-50,40,b -> {page--;rebuildWidgets();});prev.active=page>0;
    Button next=button(">",left+boxWidth-40,height-50,40,b -> {page++;rebuildWidgets();});next.active=page<pages-1;
   }
  } else {
   Button prev=button("< Mine",left,height-26,68,b -> show(new VendorInfoScreen(parent,catalog,world,mineIndex-1)));prev.active=mineIndex>0;
   Button next=button("Mine >",left+boxWidth-68,height-26,68,b -> show(new VendorInfoScreen(parent,catalog,world,mineIndex+1)));next.active=mineIndex+1<world.mines().size();
  }
  button("Back",width/2-42,height-26,84,b -> onClose());
 }
 private void line(GuiGraphicsExtractor g,String text,int x,int y,int color,int maxWidth) {
  String clipped=font.plainSubstrByWidth(text,maxWidth);
  if(!clipped.equals(text)) clipped=font.plainSubstrByWidth(text,Math.max(1,maxWidth-font.width("...")))+"...";
  g.text(font,clipped,x,y,color,false);
 }
 @Override public void extractRenderState(GuiGraphicsExtractor g,int mouseX,int mouseY,float delta) {
  super.extractRenderState(g,mouseX,mouseY,delta);
  g.fill(left-4,8,left+boxWidth+4,40,0xdd202630);
  g.centeredText(font,title,width/2,13,0xffffffff);
  if(mineIndex<0) {
   String subtitle=world==null?catalog.status:world.mines().isEmpty()?"Information not recorded yet":"Choose a mine to view resource requirements";
   line(g,subtitle,left,28,0xffcccccc,boxWidth);
   int count=world==null?catalog.worlds.size():world.mines().size();
   int pages=Math.max(1,(count+perPage-1)/perPage);
   if(pages>1) g.centeredText(font,"Page "+(page+1)+" / "+pages,width/2,height-44,0xffdddddd);
   if(world!=null&&world.mines().isEmpty()) {
    g.centeredText(font,"No vendor requirements recorded for this world.",width/2,66,0xffffcc77);
    g.centeredText(font,"Add data from the spreadsheet or vendor menus.",width/2,82,0xffcccccc);
   }
   return;
  }
  VendorCatalog.Mine mine=world.mines().get(mineIndex);
  line(g,world.name()+" | Resource: "+mine.resource(),left,28,0xff8fdeb1,boxWidth);
  int top=43,rowH=12,col=(boxWidth-30)/3;
  g.fill(left,top-3,left+boxWidth,top+6*rowH+1,0xdd202630);
  int[] xs={left+3,left+32,left+32+col,left+32+col*2};
  String[] headers={"Tier","Sword",mine.toolName(),"Armor (each)"};
  for(int i=0;i<4;i++) line(g,headers[i],xs[i],top,0xffffdc7a,i==0?28:col-3);
  for(int i=0;i<5;i++) {
   VendorCatalog.Cost cost=mine.rows().get(i);int y=top+(i+1)*rowH;
   if(i%2==0)g.fill(left,y-2,left+boxWidth,y+11,0x443d5366);
   String[] values={cost.tier(),cost.sword(),cost.tool(),cost.armor()};
   for(int j=0;j<4;j++)line(g,values[j].equals("Not recorded")?"--":values[j],xs[j],y,values[j].equals("Not recorded")?0xff999999:values[j].startsWith("~")?0xff60cfff:0xffffffff,j==0?28:col-3);
  }
  line(g,"Talisman: "+mine.talisman(),left,120,mine.talisman().startsWith("~")?0xff60cfff:0xffffffff,boxWidth);
  line(g,"Elytra: "+mine.elytra(),left,133,mine.elytra().startsWith("~")?0xff60cfff:0xffffffff,boxWidth);
  line(g,mine.total().label(),left,146,mine.total().estimated()?0xff60cfff:mine.total().complete()?0xff8fdeb1:0xffffbf69,boxWidth);
  line(g,"All tiers + 4 armor pieces + talisman + elytra",left,159,0xffbbbbbb,boxWidth);
  line(g,"k=thousand M=million B=billion ~=estimate",left,172,0xffbbbbbb,boxWidth);
  if(height>=224&&!mine.notes().isEmpty())line(g,mine.notes().get(0),left,185,0xffffbf69,boxWidth);
  if(height>=248)line(g,"Source: "+mine.source(),left,height-44,0xffaaaaaa,boxWidth);
 }
 @Override public void onClose() { show(parent); }
 @Override public boolean isPauseScreen() { return false; }
}

