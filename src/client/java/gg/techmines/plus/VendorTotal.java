package gg.techmines.plus;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/** Sums every sword/tool tier, four armour pieces per tier I-III, and both specials. */
final class VendorTotal {
 record Result(BigDecimal amount,boolean estimated,boolean complete) {
  String label() {
   if(amount==null)return "Total: unavailable";
   String text=VendorNumbers.format(amount);
   return (complete?"Total: ":"Recorded subtotal: ")+(estimated?"~":"")+text;
  }
 }
 static Result calculate(List<String> costs) { return calculate(costs,List.of()); }
 static Result calculate(List<String> costs,List<String> additionalCosts) {
  if(costs.size()!=15)throw new IllegalArgumentException("Expected five sword, five tool, three armour and two special prices");
  List<String> allCosts=new ArrayList<>(costs);allCosts.addAll(additionalCosts);
  BigDecimal sum=BigDecimal.ZERO;boolean estimated=false,complete=true,known=false;
  for(int i=0;i<allCosts.size();i++) {
   String text=allCosts.get(i).strip();boolean guess=text.startsWith("~");
   if(guess)text=text.substring(1);
   BigDecimal price=VendorNumbers.amount(text);
   if(price==null){complete=false;continue;}
   if(i>=10&&i<13)price=price.multiply(BigDecimal.valueOf(4));
   sum=sum.add(price);estimated|=guess;known=true;
  }
  return new Result(known?sum:null,estimated,complete);
 }
}
