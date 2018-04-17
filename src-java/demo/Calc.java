package demo;

import java.util.*;

public class Calc {
  public static double add( double x, double y ) {
    return (x + y); }

  public static HashMap<String, Long> incVals( Map<String,Long> mappy ) {
    HashMap<String, Long> result = new HashMap<>();
    for (String k : mappy.keySet()) {
      long ii = mappy.get(k);
      result.put( k, (ii+1) );
    }
    return result;
  }
}
