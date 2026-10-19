package gg.techmines.plus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

/** Sums every sword/tool tier, four armour pieces per tier I-III, and both specials. */
final class VendorTotal {
 private static final Pattern PRICE=Pattern.compile("([0-9]+(?:\\.[0-9]+)?)([kmb]?)",Pattern.CASE_INSENSITIVE);
 record Result(BigDecimal amount,boolean estimated,boolean complete) {
  String label() {
   if(amount==null)return "Total: unavailable";
   BigDecimal scale=BigDecimal.ONE;String suffix="";
   if(amount.compareTo(new BigDecimal("1000000000"))>=0){scale=new BigDecimal("1000000000");suffix="B";}
   else if(amount.compareTo(new BigDecimal("1000000"))>=0){scale=new BigDecimal("1000000");suffix="M";}
   else if(amount.compareTo(new BigDecimal("1000"))>=0){scale=new BigDecimal("1000");suffix="k";}
   String text=amount.divide(scale,2,RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()+suffix;
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
   var match=PRICE.matcher(text.replace(",","").replace(" ",""));
   if(!match.matches()){complete=false;continue;}
   BigDecimal price=new BigDecimal(match.group(1));
   int exponent=switch(match.group(2).toLowerCase(Locale.ROOT)){case "k"->3;case "m"->6;case "b"->9;default->0;};
   price=price.scaleByPowerOfTen(exponent);
   if(i>=10&&i<13)price=price.multiply(BigDecimal.valueOf(4));
   sum=sum.add(price);estimated|=guess;known=true;
  }
  return new Result(known?sum:null,estimated,complete);
 }
}
