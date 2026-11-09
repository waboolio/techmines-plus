package gg.techmines.plus;
import java.math.BigDecimal;
import java.util.*;
public final class VendorNumbersCheck {
 static int checks;static void check(boolean ok,String message){checks++;if(!ok)throw new AssertionError(message);}
 static void expect(String input,String expected){check(VendorNumbers.display(input).equals(expected),input+" -> "+VendorNumbers.display(input));}
 public static void main(String[] args){
  expect("2009999999.9999998 (unit unconfirmed)","2.01B");
  expect("16829999999.999998 (unit unconfirmed)","16.83B");
  expect("66489999999.99999 (unit unconfirmed)","66.49B");
  expect("~2009999999.9999998 (unit unconfirmed)","~2.01B");
  expect("536.44 (unit unconfirmed)","536.44 (unit unconfirmed)");
  expect("2000000000.5 (unit unconfirmed)","2000000000.5 (unit unconfirmed)");
  expect("1.2B","1.20B");expect("24.11B","24.11B");expect("~482.2B","~482.20B");expect("1,234,567.89123","1.23M");
  expect("999999999","1.00B");expect("999999","1.00M");expect("1.005M","1.01M");expect("161.26k","161.26k");expect("28","28.00");expect("Not recorded","Not recorded");expect("Not confirmed","Not confirmed");expect("2.01E9","2.01B");
  check(VendorNumbers.amount("~1.23456789M").compareTo(new BigDecimal("1234567.89"))==0,"formatting does not round calculation input");
  var costs=new ArrayList<>(Collections.nCopies(15,"0"));costs.set(5,"2009999999.9999998 (unit unconfirmed)");var total=VendorTotal.calculate(costs);check(total.complete()&&total.amount().compareTo(new BigDecimal("2010000000"))==0,"float residue included in exact total");check(total.label().equals("Total: 2.01B"),"total label uses two decimals");
  costs.set(13,"536.44 (unit unconfirmed)");check(!VendorTotal.calculate(costs).complete(),"genuinely unknown unit remains subtotal");
  System.out.println(checks+" vendor formatting checks passed.");
 }
}
