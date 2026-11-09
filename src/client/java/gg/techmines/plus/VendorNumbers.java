package gg.techmines.plus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Pattern;
/** One display format; arithmetic retains full precision until the final label. */
final class VendorNumbers {
 private static final String UNKNOWN=" (unit unconfirmed)";
 private static final Pattern PRICE=Pattern.compile("([0-9]+(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?)([kmb]?)",Pattern.CASE_INSENSITIVE);
 static BigDecimal amount(String input){
  String text=input.strip();if(text.startsWith("~"))text=text.substring(1);
  boolean uncertain=text.endsWith(UNKNOWN);if(uncertain)text=text.substring(0,text.length()-UNKNOWN.length());
  var match=PRICE.matcher(text.replace(",","").replace(" ",""));if(!match.matches())return null;
  BigDecimal value=new BigDecimal(match.group(1)).scaleByPowerOfTen(switch(match.group(2).toLowerCase(Locale.ROOT)){case "k"->3;case "m"->6;case "b"->9;default->0;});
  if(uncertain){
   if(value.compareTo(BigDecimal.valueOf(1000))<0)return null;
   BigDecimal rounded=value.setScale(0,RoundingMode.HALF_UP);
   double tolerance=Math.max(0.000001,Math.ulp(value.doubleValue())*4);
   if(!Double.isFinite(tolerance)||value.subtract(rounded).abs().compareTo(BigDecimal.valueOf(tolerance))>0)return null;
   value=rounded;
  }
  return value;
 }
 static String display(String text){BigDecimal value=amount(text);return value==null?text:(text.strip().startsWith("~")?"~":"")+format(value);}
 static String format(BigDecimal value){
  int exponent=value.compareTo(new BigDecimal("1000000000"))>=0?9:value.compareTo(new BigDecimal("1000000"))>=0?6:value.compareTo(new BigDecimal("1000"))>=0?3:0;
  BigDecimal scaled=value.scaleByPowerOfTen(-exponent).setScale(2,RoundingMode.HALF_UP);
  if(exponent<9&&scaled.compareTo(BigDecimal.valueOf(1000))>=0){exponent+=3;scaled=value.scaleByPowerOfTen(-exponent).setScale(2,RoundingMode.HALF_UP);}
  return scaled.toPlainString()+switch(exponent){case 9->"B";case 6->"M";case 3->"k";default->"";};
 }
}
