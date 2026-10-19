package gg.techmines.plus;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
public final class VendorTotalCheck {
 static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 public static void main(String[] args)throws Exception {
  var prices=new ArrayList<>(List.of("1","2","3","4","5","1","2","4","8","12","1","2","3","20","10"));
  var result=VendorTotal.calculate(prices);check(result.amount().compareTo(new BigDecimal("96"))==0,"all tiers and four armour pieces");check(result.complete()&&!result.estimated(),"confirmed complete total");
  prices.set(0,"~1");check(VendorTotal.calculate(prices).estimated(),"estimate propagation");
  prices.set(14,"Not recorded");result=VendorTotal.calculate(prices);check(!result.complete()&&result.amount().compareTo(new BigDecimal("86"))==0,"missing special produces subtotal");check(result.label().startsWith("Recorded subtotal: ~"),"subtotal label");
  result=VendorTotal.calculate(Collections.nCopies(15,"Not confirmed"));check(result.amount()==null&&result.label().equals("Total: unavailable"),"unknown prices never become zero");
  prices=new ArrayList<>(Collections.nCopies(15,"0"));prices.set(0,"1.25B");prices.set(5,"2.50M");prices.set(10,"3.5k");prices.set(13,"0.25");result=VendorTotal.calculate(prices);check(result.amount().compareTo(new BigDecimal("1252514000.25"))==0,"suffixes, decimals and armour multiplier");
  prices=new ArrayList<>(Collections.nCopies(15,"0"));result=VendorTotal.calculate(prices,List.of("512"));check(result.amount().compareTo(new BigDecimal("512"))==0,"additional starter tool counted once");
  int count=0;
  for(String line:Files.readAllLines(Path.of(args[0]))){String[] parts=line.split("\\t",-1);result=VendorTotal.calculate(Arrays.asList(Arrays.copyOfRange(parts,4,19)),Arrays.asList(Arrays.copyOfRange(parts,19,parts.length)));check(result.amount()==null?parts[1].equals("unknown"):result.amount().compareTo(new BigDecimal(parts[1]))==0,"spreadsheet sum: "+parts[0]);check(result.complete()==Boolean.parseBoolean(parts[2]),"completeness: "+parts[0]);check(result.estimated()==Boolean.parseBoolean(parts[3]),"estimate flag: "+parts[0]);count++;}
  check(count==99,"all pages checked");System.out.println("Vendor total checks passed: 99 spreadsheet totals, full armour sets, specials, estimates, missing values and units.");
 }
}
