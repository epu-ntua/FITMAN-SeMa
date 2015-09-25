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

package de.wdilab.coma.matching.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;

/**
 * This class organizes the reuse of already calculates match results
 * or single similarity values.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class SimilarityCache {
	// 10000 100000 1000000	
	  static final int MAX_VALUES = 1000000;
	    HashMap<ArrayList<Object>, Float> hash;
	    int putCnt = 0;
	    long putTime = 0;
	    int getCnt = 0;
	    long getTime = 0;

	    public SimilarityCache() {
	      hash = new HashMap<ArrayList<Object>, Float>();
	    }

	    public void put(Object aObj, Object bObj, float sim) {
	    	if (hash.size()>MAX_VALUES){
	    		return;
	    	}
	      long start = System.currentTimeMillis();
	      ArrayList<Object> pair = new ArrayList<Object>();
	      pair.add(aObj);
	      pair.add(bObj);
	      hash.put(pair, new Float(sim));
	      long end = System.currentTimeMillis();
	      putCnt++;
	      putTime += (end-start);
	    }
	    public float get(Object aObj, Object bObj) {
	      long start = System.currentTimeMillis();
	      ArrayList<Object> pair = new ArrayList<Object>();
	      pair.add(aObj);
	      pair.add(bObj);
	      Float sim = hash.get(pair);
	      long end = System.currentTimeMillis();
	      getCnt++;
	      getTime += (end-start);
	      if (sim==null) return MatchResult.SIM_UNDEF;
	      return sim.floatValue();
	    }

	    public void put(MatchResultArray result) {
	    	ArrayList aObjs= result.getSrcObjects();
	    	ArrayList bObjs= result.getTrgObjects();
	    	float[][] simMatrix = result.getSimMatrix();
	    	
	 	    if (aObjs==null || bObjs==null) return;
	    	for (int i=0; i<aObjs.size(); i++) {
		    	  Object aObj = aObjs.get(i);
	    	  for (int j=0; j<bObjs.size(); j++) {
	    		  put(aObj, bObjs.get(j), simMatrix[i][j]);
	    	  }
	    	}
	    }
	    
	    public float[][] get(ArrayList aObjs, ArrayList bObjs) {
	      if (aObjs==null || bObjs==null) return null;
	      float[][] simMatrix = new float[aObjs.size()][bObjs.size()];
	      for (int i=0; i<aObjs.size(); i++) {
	        for (int j=0; j<bObjs.size(); j++) {
	          simMatrix[i][j] = get(aObjs.get(i), bObjs.get(j));
	        }
	      }
	      return simMatrix;
	    }
	    
	    public MatchResultArray getFull(ArrayList<Object> aObjs, ArrayList<Object> bObjs) {
		      if (aObjs==null || bObjs==null) return null;
		      HashSet<Object> aObjNot = new HashSet<Object>();
		      HashSet<Object> bObjNot = new HashSet<Object>();
		      MatchResultArray result = new MatchResultArray(aObjs, bObjs);
		      float[][] simMatrix = result.getSimMatrix();
		      for (int i=0; i<aObjs.size(); i++) {
		    	  Object aObj = aObjs.get(i);
		        for (int j=0; j<bObjs.size(); j++) {
		        	Object bObj = bObjs.get(j);
		          float sim = get(aObj, bObj);
		          if (sim==MatchResult.SIM_UNDEF){
		        	  aObjNot.add(aObj);
		        	  bObjNot.add(bObj);
		          } else {
		        	  simMatrix[i][j]=sim;
		          }
		        }
		      }
		      ArrayList<Object> aObjsFull = new ArrayList<Object>(aObjs);
		      ArrayList<Object> bObjsFull = new ArrayList<Object>(bObjs);
		      aObjsFull.removeAll(aObjNot);
		      bObjsFull.removeAll(bObjNot);
		      // one dimension empty mean no result matrix
		      if (aObjsFull.isEmpty() || bObjsFull.isEmpty()) return null;
		      result = MatchResultArray.restrict(result, aObjsFull, bObjsFull);
		      return result;
		    }
	    
	    
	    public void put(MatchResult result) {
	    	if (result instanceof MatchResultArray){
	    		put((MatchResultArray) result);
	    	}
	    }
	    

	    public String getStatistics() {
	      return "Hash[Get: " + getCnt + "/" + getTime + " ms; Put: " + putCnt + "/" + putTime + " ms]";
	    }
	    
	    public int getSize(){
	    	return hash.size();
	    }
	    
}
