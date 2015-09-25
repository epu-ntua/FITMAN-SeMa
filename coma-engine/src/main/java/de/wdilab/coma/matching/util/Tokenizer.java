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

package de.wdilab.coma.matching.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class is used to tokenize a given String into smaller tokens.
 * It is used for Resolution3 to tokenize names, commments or paths.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class Tokenizer {
	
//	// expanding of abbreviation moved to exec workflow as preprocessing
//	static public ArrayList<String> expandTokenize(ArrayList<String> abbrevList,
//			ArrayList<String> fullFormList, String str) {
//		// add expanding abbreviations (if given)
//		    return expand(abbrevList, fullFormList, tokenize(str)); 
////		    return tokenize(str);
//	  }
//	
//	  //Look up in the abbreviation list and expand tokens when necessary
//	  //Note that the expanded tokens are tokenized once more to get more tokens
//	  public static ArrayList<String> expand(ArrayList<String> abbrevList,
//				ArrayList<String> fullFormList, ArrayList<String> tokens) {
//	    if (tokens==null || tokens.isEmpty()) return null;
//	    if (abbrevList==null || abbrevList.isEmpty()) return tokens;
//
//	    ArrayList<String> expandedList = new ArrayList<String>();
//	    for (int i=0; i<tokens.size(); i++) {
//	      String token = tokens.get(i);
//	      int index = abbrevList.indexOf(token.toUpperCase());
//	      if (index != -1) {
//	        String fullForm = fullFormList.get(index);
//	        ArrayList newTokens = tokenize(fullForm);
//	        for (int j=0; j<newTokens.size(); j++) {
//	          String newToken = (String)newTokens.get(j);
//	          if (! expandedList.contains(newToken)) expandedList.add(newToken);
//	        }
//	      }
//	      else if (! expandedList.contains(token)) expandedList.add(token);
//	    }
//	    return expandedList;
//	  }


	
	  //Tokenize with uppercase split: VATValue -> {VAT, Value}
	  static public ArrayList<String> tokenize(String str) {
	    if (str == null || str.length() == 0) return null;
	    String delimiters = " ._-";

//	    // Remove all digits from string, optional otherwise will be used as delimiters
//	    String txtStr = "";
//	    for (int i=0; i<str.length(); i++) {
//	      char c = str.charAt(i);
//	      if (Character.isDigit(c))
//	    	  continue;
//	      txtStr += c;
//	    }
//	    str = txtStr;

	    //Cut string into pieces separated by common delimiters
	    StringTokenizer st = new StringTokenizer(str, delimiters);
	    ArrayList<String> tokenList = new ArrayList<String>();

	    //in each piece search for further tokens separated by uppercase letters
	    while (st.hasMoreTokens()) {
	      String elem = st.nextToken();
	      int elemLen = elem.length();

	      // parse for Uppercase letters
	      int upperCount = 0;
	      int[] charType = new int[elemLen];
	      for (int i = 0; i < elemLen; i++) {
	        char c = elem.charAt(i);
	        if (Character.isUpperCase(c)) {
	          upperCount ++;
	          charType[i] = 1;
	        }
	        else if (Character.isDigit(c)) {
	          charType[i] = 2;
	        }
	        else {
	          charType[i] = 0;
	        }
	      }

	      //ignore if to many uppercase letters
	      if (upperCount > (elemLen/2)) {
	        tokenList.add(elem.toLowerCase());
	        continue;
	      }

	      // group subsequent uppercase letters to a single token
	      int begin = 0;
	      int lastState = charType[0];
	      for (int i = 0; i < charType.length; i++) {
	        //System.out.println("i = " + i +"; current char = " + elem.charAt(i) +"; begin = " + begin);
	        if (lastState == charType[i])
	          continue;
	        else { //change in charType occured
	          //System.out.println("CharType change at i = " + i);
	          if (lastState == 1) {
	            if (charType[i] == 2) {
	              //last char was Upper Case, current char is digit
	              String token = elem.substring(begin, i).toLowerCase();
	              if (!tokenList.contains(token))
	                tokenList.add(token);
	              begin = i;
	              //System.out.println("-->(1.a) new token: " + token);
	            }
	            else if (i - begin > 1) {
	              //last char was Upper Case, current char is lower case
	              //there are more then 1 upper case letter
	              String token = elem.substring(begin, i - 1).toLowerCase();
	              if (!tokenList.contains(token))
	                tokenList.add(token);
	              begin = i - 1;
	              i--;
	              //System.out.println("-->(1.b) new token: " + token);
	            }
	          }
	          else if (lastState == 0) {
	            //last char was Lower Case, current char is Upper case or digit
	            //create new token from begin till last char
	            String token = elem.substring(begin, i).toLowerCase();
	            if (!tokenList.contains(token))
	              tokenList.add(token);
	            begin = i;
	            //System.out.println("-->(2) new token: " + token);
	          }
	          else {
	            //last char was digit, new char is lowercase or uppercase
	            String token = elem.substring(begin, i).toLowerCase();
	            if (!tokenList.contains(token))
	              tokenList.add(token);
	            begin = i;
	            //System.out.println("-->(3) new token: " + token);
	          }
	          lastState = charType[i];
	        }
	      }
	      String lastToken = elem.substring(begin, elem.length()).toLowerCase();
	      if (!tokenList.contains(lastToken))
	        tokenList.add(lastToken);
	    }
	    //at this point, tokenList must contain at least one token
	    return tokenList;
	  }
	
}
