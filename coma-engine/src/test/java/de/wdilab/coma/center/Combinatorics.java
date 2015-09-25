/*
 *  COMA 3.0 Community Edition
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package de.wdilab.coma.center;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * @author Sabine Massmann
 */
public class Combinatorics {

  public static String arrayToString(int[] array) {
    String str = "[";
    for (int i=0; i<array.length; i++) {
      if (i<array.length-1) str += array[i] + ",";
      else str += array[i];
    }
    str += "]";
    return str;
  }
  public static void printArray(int[] array) {
    System.out.println(arrayToString(array));
  }

  public static ArrayList getPermutations(int n) {
    PermutationGenerator permGen = new PermutationGenerator(n);
    ArrayList<int[]> perms = new ArrayList<int[]>();
    while (permGen.hasMore()) {
      int[] perm = permGen.getNext();
      int[] copy = new int[perm.length];
      System.arraycopy(perm, 0, copy, 0, perm.length);
      perms.add(copy);
    }
    if (perms.isEmpty()) return null;
    return perms;
  }

  public static ArrayList<int[]> getCombinations(int n, int r) {
    CombinationGenerator combGen = new CombinationGenerator(n, r);
    ArrayList<int[]> combs = new ArrayList<int[]>();
    while (combGen.hasMore()) {
      int[] comb = combGen.getNext();
      int[] copy = new int[comb.length];
      System.arraycopy(comb, 0, copy, 0, comb.length);
      combs.add(copy);
    }
    if (combs.isEmpty()) return null;
    return combs;
  }

  public static void main(String[] args) {
    int n = 8;
    ArrayList perms = getPermutations(n);
    System.out.println("Permutations of " + n + "=" + perms.size());
    //for (int i=0; i<perms.size(); i++) printArray((int[])perms.get(i));
    int cnt = 0;
    for (int r=1; r<=n; r++) {
      ArrayList combs = getCombinations(n, r);
      System.out.println("Combinations of " + n + " and " + r + "=" + combs.size());
      cnt += combs.size();
      //for (int i=0; i<combs.size(); i++) printArray((int[])combs.get(i));
    }
    System.out.println("Total combs: " + cnt);
  }
}

class PermutationGenerator {

  private int[] a;
  private BigInteger numLeft;
  private BigInteger total;

  //-----------------------------------------------------------
  // Constructor. WARNING: Don't make n too large.
  // Recall that the number of permutations is n!
  // which can be very large, even when n is as small as 20 --
  // 20! = 2,432,902,008,176,640,000 and
  // 21! is too big to fit into a Java long, which is
  // why we use BigInteger instead.
  //----------------------------------------------------------
  public PermutationGenerator(int n) {
    if (n < 1) {
      throw new IllegalArgumentException("Min 1");
    }
    a = new int[n];
    total = getFactorial(n);
    reset();
  }

  //------
  // Reset
  //------
  public void reset() {
    for (int i = 0; i < a.length; i++) {
      a[i] = i;
    }
    numLeft = new BigInteger(total.toString());
  }

  //------------------------------------------------
  // Return number of permutations not yet generated
  //------------------------------------------------
  public BigInteger getNumLeft() {
    return numLeft;
  }

  //------------------------------------
  // Return total number of permutations
  //------------------------------------
  public BigInteger getTotal() {
    return total;
  }

  //-----------------------------
  // Are there more permutations?
  //-----------------------------
  public boolean hasMore() {
    return numLeft.compareTo(BigInteger.ZERO) == 1;
  }

  //------------------
  // Compute factorial
  //------------------
  private static BigInteger getFactorial(int n) {
    BigInteger fact = BigInteger.ONE;
    for (int i = n; i > 1; i--) {
      fact = fact.multiply(new BigInteger(Integer.toString(i)));
    }
    return fact;
  }

  //--------------------------------------------------------
  // Generate next permutation (algorithm from Rosen p. 284)
  //--------------------------------------------------------
  public int[] getNext() {

    if (numLeft.equals(total)) {
      numLeft = numLeft.subtract(BigInteger.ONE);
      return a;
    }

    int temp;

    // Find largest index j with a[j] < a[j+1]
    int j = a.length - 2;
    while (a[j] > a[j + 1]) {
      j--;
    }

    // Find index k such that a[k] is smallest integer
    // greater than a[j] to the right of a[j]
    int k = a.length - 1;
    while (a[j] > a[k]) {
      k--;
    }

    // Interchange a[j] and a[k]
    temp = a[k];
    a[k] = a[j];
    a[j] = temp;

    // Put tail end of permutation after jth position in increasing order
    int r = a.length - 1;
    int s = j + 1;

    while (r > s) {
      temp = a[s];
      a[s] = a[r];
      a[r] = temp;
      r--;
      s++;
    }

    numLeft = numLeft.subtract(BigInteger.ONE);
    return a;
  }
}

class CombinationGenerator {

  private int[] a;
  private int n;
  private int r;
  private BigInteger numLeft;
  private BigInteger total;

  //------------
  // Constructor
  //------------
  public CombinationGenerator(int _n, int _r) {
    n = _n;
    r = _r;
    if (r > n) {
      throw new IllegalArgumentException();
    }
    if (n < 1) {
      throw new IllegalArgumentException();
    }
    a = new int[r];
    BigInteger nFact = getFactorial(n);
    BigInteger rFact = getFactorial(r);
    BigInteger nminusrFact = getFactorial(n - r);
    total = nFact.divide(rFact.multiply(nminusrFact));
    reset();
  }

  //------
  // Reset
  //------
  public void reset() {
    for (int i = 0; i < a.length; i++) {
      a[i] = i;
    }
    numLeft = new BigInteger(total.toString());
  }

  //------------------------------------------------
  // Return number of combinations not yet generated
  //------------------------------------------------
  public BigInteger getNumLeft() {
    return numLeft;
  }

  //-----------------------------
  // Are there more combinations?
  //-----------------------------
  public boolean hasMore() {
    return numLeft.compareTo(BigInteger.ZERO) == 1;
  }

  //------------------------------------
  // Return total number of combinations
  //------------------------------------

  public BigInteger getTotal() {
    return total;
  }

  //------------------
  // Compute factorial
  //------------------
  private static BigInteger getFactorial(int n) {
    BigInteger fact = BigInteger.ONE;
    for (int i = n; i > 1; i--) {
      fact = fact.multiply(new BigInteger(Integer.toString(i)));
    }
    return fact;
  }

  //--------------------------------------------------------
  // Generate next combination (algorithm from Rosen p. 286)
  //--------------------------------------------------------
  public int[] getNext() {

    if (numLeft.equals(total)) {
      numLeft = numLeft.subtract(BigInteger.ONE);
      return a;
    }

    int i = r - 1;
    while (a[i] == n - r + i) {
      i--;
    }
    a[i] = a[i] + 1;
    for (int j = i + 1; j < r; j++) {
      a[j] = a[i] + j - i;
    }

    numLeft = numLeft.subtract(BigInteger.ONE);
    return a;
  }
}