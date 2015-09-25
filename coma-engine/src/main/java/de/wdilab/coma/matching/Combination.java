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

package de.wdilab.coma.matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;

/**
 * Combination realizes the combination of at least two match results
 * combination can be e.g. max, average, weighted, min
 * but also more complex functions e.g. nonlinear, sigmoid
 * 
 * part of the grammar
 * 
 * (in previous prototype called aggregation)
 * 
 * @author Hong Hai Do, Sabine Massmann, Eric Peukert
 */
public class Combination{
	
	 // start counting 4000 Manager.COM_CNT
    //Strategies for combination of matcher-specific similarities
    public static final int COM_MAX       = Constants.COM_CNT + 11;
    public static final int COM_AVERAGE   = Constants.COM_CNT + 12;
    public static final int COM_WEIGHTED  = Constants.COM_CNT + 13;
    public static final int COM_MIN       = Constants.COM_CNT + 14;
    public static final int COM_NONLINEAR = Constants.COM_CNT+15;
    public static final  int COM_HARMONY  = Constants.COM_CNT+16;
    public static final int COM_SIGMOID   = Constants.COM_CNT+17;
    public static final int COM_OPENII    = Constants.COM_CNT+18;
    public static final int COM_OWA_ALH   = Constants.COM_CNT+19;
    public static final int COM_OWA_MOST   = Constants.COM_CNT+20;
    public static final int COM_WEIGHTED2  = Constants.COM_CNT + 21;
    public static final int[] COM_IDS      = {COM_MAX, COM_AVERAGE, COM_WEIGHTED, COM_MIN,COM_NONLINEAR, COM_HARMONY,
                                               COM_SIGMOID, COM_OPENII,COM_OWA_ALH,COM_OWA_MOST, COM_WEIGHTED2 };
    
    //Strategies for computation of set similarity
    public static final int SET_AVERAGE       = Constants.COM_CNT + 51;
    public static final int SET_DICE          = Constants.COM_CNT + 52;
    public static final int SET_MIN           = Constants.COM_CNT + 53;
    public static final int SET_MAX           = Constants.COM_CNT + 54;
    public static final int SET_HIGHEST           = Constants.COM_CNT + 55;
    public static final int[] SET_IDS      = {SET_MIN, SET_AVERAGE, SET_DICE, SET_MAX, SET_HIGHEST };
    
    //Strategies for computation of set similarity
    public static final int RESULT_INTERSECT  = Constants.COM_CNT + 61;
    public static final int RESULT_DIFF       = Constants.COM_CNT + 62;
    public static final int RESULT_MERGE      = Constants.COM_CNT + 63;
    public static final int[] RESULT_IDS      = {RESULT_INTERSECT, RESULT_DIFF, RESULT_MERGE };
    
    
    int combination = Constants.UNDEF;
    float[] weights = null;
	String name;

	
    /**
     * Constructor for threshold or delta selection
     * 
     * @param combination
     */
    public Combination (int combination){
		this.combination = combination;
		this.name=combinationToString(combination);
    }
    
    public Combination (int combination, float[] weights){
		this.combination = combination;
		this.weights = weights;
		name=toString();		
    }
    
    public int getId() { return combination;}
    public float[] getWeights() { return weights;}
    
	public String getName() {
		if (name==null){
			// generate Name
			name=toString();
		}
		return name; 
	}
	
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
    	String s = combinationToString(combination);
    	if (weights!=null){
    		s+="(";
			for (int i = 0; i < weights.length-1; i++) {
				s+=weights[i]+",";
			}
			s+=weights[weights.length-1]+")";
    	}
    	return s;
    }
	
    
    
    /**
     * @param combination
     * @return the string representation for the combination
     */
    public static String combinationToString(int combination) {
    	switch (combination) {
    	// sim combination
    		case COM_MAX:        return "Max";
    		case COM_AVERAGE:    return "Average";
    		case COM_WEIGHTED:   return "Weighted";
    		case COM_MIN:        return "Min";
            case COM_NONLINEAR:  return "NonLinear";
            case COM_HARMONY:    return "Harmony";
            case COM_SIGMOID:    return "Sigmoid";
            case COM_OPENII:     return "OpenII";
            case COM_OWA_ALH:    return "OWA";
            case COM_OWA_MOST:    return "OWA_MOST";
    		case COM_WEIGHTED2:   return "Weighted2";
    	// set combination    		
            case SET_AVERAGE: return "Set_Average";
            case SET_DICE:    return "Set_Dice";
            case SET_MIN:     return "Set_Min";
            case SET_MAX:     return "Set_Max";
            case SET_HIGHEST:     return "Set_Highest";  
        // result combination
            case RESULT_INTERSECT: return "intersect";
            case RESULT_DIFF:      return "diff";
            case RESULT_MERGE:     return "merge";               
    		default: return "UNDEF";
    	}
    }
    
    /**
     * @param combination
     * @return the id for the given string representation of a combination
     */
    public static int stringToCombination(String combination) {
        if (combination==null) return Constants.UNDEF;
        combination = combination.toLowerCase();
        if (combination.equals("max")) return COM_MAX;
        else if (combination.equals("average"))    return COM_AVERAGE;
        else if (combination.startsWith("weighted"))    return COM_WEIGHTED;
        else if (combination.startsWith("weighted2"))    return COM_WEIGHTED2;
        else if (combination.equals("min"))       return COM_MIN;
        else if(combination.equals("nonlinear"))  return COM_NONLINEAR;
        else if(combination.equals("harmony"))    return COM_HARMONY;
        else if(combination.equals("sigmoid"))    return COM_SIGMOID;
        else if(combination.equals("openii"))     return COM_OPENII;
        else if(combination.equals("owa"))        return COM_OWA_ALH;
        else if (combination.equals("owa_most"))  return COM_OWA_MOST;
        else if (combination.equals("set_average")) return SET_AVERAGE;
        else if (combination.equals("set_dice"))    return SET_DICE;
        else if (combination.equals("set_min"))    return SET_MIN;
        else if (combination.equals("set_max"))    return SET_MAX;
        else if (combination.equals("set_highest"))    return SET_HIGHEST;  
        return Constants.UNDEF;
      }


	/**
	 * executes the specified combination for the given results
	 * @param results
	 * @return new match result containing the combined correspondences
	 * - returns null if results do not have the same graphs
	 */
	public MatchResult combine(MatchResult[] results){
		if (results==null) return null;
		
		results = MatchResult.trim(results);
		
		// if only one result nothing two combine
		if (results.length==1) return results[0];
		
		boolean sameGraphs = MatchResult.sameGraphs(results);
		if (!sameGraphs){
			return null;
		}
		boolean sameObjects = MatchResult.sameObjects(results);
        if (weights==null && combination==COM_WEIGHTED){
        	combination = COM_AVERAGE;
        }
        
        if (sameObjects)
			return combineArrays(results); 
		return combineNotSameObjects(results);
        
        
//        MatchResult result = null;
//		switch (kind) {
//		case MatchResult.ARRAY:
//			if (sameObjects)
//				result =  combineArrays(results); 
//			break;
//		case MatchResult.DB:
//			if (sameObjects){
//				result =  combineDB(results);
//			} 
//			break;
//		case MatchResult.MIXED:
//			result = combineMixed(results);
//			break;
//		default:
//			result = combineMixed(results);
//			break;
//		}
//		if (result!=null){
//			result.setSourceGraph(results[0].getSourceGraph());
//			result.setTargetGraph(results[0].getTargetGraph());
//		}
//		return result;
	}
	
	/**
	 * executes the specified combination for the given results
	 * - optimized for results that are using arrays
	 * @param results
	 * @return new match result containing the combined correspondences
	 */
	MatchResult combineArrays(MatchResult[] results) {
		// assumption all results have same graphs and same objects
		MatchResultArray result = new MatchResultArray(results[0].getSrcObjects(), results[0].getTrgObjects());
		
		float[][][] simCube = new float[results.length][][];
		for (int i = 0; i < results.length; i++) {
			simCube[i] = ((MatchResultArray)results[i]).getSimMatrix();
		}
		float[][] simMatrix = combineCube(simCube);
		if (simMatrix==null){
			return null;
		}
		result.setSimMatrix(simMatrix);
		result.setGraphs(results[0].getSourceGraph(), results[0].getTargetGraph());
		return result;
	}
	
	
	float[][] combineCube(float[][][] simCube) {
		if (simCube == null) return null;
	    int k, m = 0, n = 0;
		k = simCube.length;           //1. Dimension: matchers
	    if(k==0){
	        m = 0;
	        n = 0;
	    }else{
	    	for (int i = 0; i < k; i++) {
	        	if (simCube[i]!=null){
	      		  m = simCube[i].length;        //2. Dimension: elements of the first schema
	      	      if(m==0)
	      	        n = 0;
	      	      else
	      		    n = simCube[i][0].length;     //3. Dimension: elements of the second schema
	      	      break;
	          	}
			}
	    }

    switch(combination)
    {
       case COM_WEIGHTED2: // using weights for combination
		/**
		 * If no weight is given, an average is assumed.
		 */
    	   return combine(transformCube(simCube),	COM_WEIGHTED2);

      case COM_HARMONY:  // weighted combination using the computed harmony of results
    	/**
    	 * Harmony from Ming Mao. In GI-Workshop Paper called Hadapt because in Hadapt-System used 
    	 */
    	  return combine(transformCube(simCube), COM_HARMONY);

       case COM_SIGMOID:  // SigmoidFunction
    	/**
    	 * Sigmoid function. The problem is to define the two additional
    	 * parameters that is a steepness factor and a shifting value.
    	 */
    	   return combine(transformCube(simCube), COM_SIGMOID, weights, 8, 0.5f);

      case COM_OWA_ALH:  // OWA-Linguistic Method, orders and defines weights for the first half of matchers
		/**
		 * OWA
		 */
    	// OWA-Linguistic Method, orders and defines weights for the first half of matchers
    	  return combine(transformCube(simCube), COM_OWA_ALH);
    case COM_OWA_MOST:  // OWA-Linguistic Method, orders and defines weights most matchers except for highest and lowest values
		/**
		 * OWA
		 */
    	// OWA-Linguistic Method, orders and defines weights most matchers except for highest and lowest values");
    	return combine(transformCube(simCube), COM_OWA_MOST);
    }

		float[][] simMatrix = new float[m][n];
		for (int i=0; i<m; i++) {
		    for (int j=0; j<n; j++) {
			float maxSim = 0;
			float sumSim = 0;
			float weightedSim = 0;
            float nonSim=0;
            float sumHarmonyNumerator = 0, sumHarmonyDivisor=0 ;
			float minSim = Float.MAX_VALUE;
			
			for (int l=0; l<k; l++) {
			    float sim = simCube[l][i][j];
			    switch(combination) {
			    case COM_MAX: 
				if (sim>maxSim) maxSim = sim;
	                        break;
			    case COM_AVERAGE:
	                        sumSim+=sim;
	                        break;
			    case COM_WEIGHTED:  
				weightedSim += (sim*weights[l]);
	                        break;
			    case COM_MIN:
				if (sim<minSim) minSim = sim;
	                        break;
                case COM_NONLINEAR:
                {
                    if(weights==null)
                     weightedSim += sim;// till tuning the weighting scheme
		          else
                     weightedSim+=sim*weights[l];
			      for(int ii=l+1;ii<k;ii++)
			       {
			        nonSim+=(sim*simCube[ii][i][j]);
			       }
                }
	                        break;
                case COM_OPENII:  
				double simTmp = sim-0.5;
				sumHarmonyNumerator+=Math.abs(simTmp)*simTmp;
				sumHarmonyDivisor+=Math.abs(simTmp);
	                        break;
			    }
			}
			switch(combination) {
			case COM_MAX: 
			    simMatrix[i][j] = maxSim;
	                    break;
			case COM_AVERAGE: 
			    simMatrix[i][j] = sumSim/k;
	                    break;
			case COM_WEIGHTED: 
			    simMatrix[i][j] = weightedSim;
	                    break;
			case COM_MIN: 
			    simMatrix[i][j] = minSim;
	                    break;
            case COM_NONLINEAR:
			    if(weights==null)
	    		weightedSim=weightedSim/k; //till tuning the weighting scheme
	     	    int kk=(k*k-k)/2;
	    	    nonSim=nonSim/kk;
	    	    if(weightedSim>0.3)
				  nonSim=(float) (0.5*weightedSim+0.5*nonSim);
			   else
				 nonSim=(float) (0.5*weightedSim-0.5*nonSim);
	    	   simMatrix[i][j] = nonSim;
	                    break;
            case COM_OPENII:
			    simMatrix[i][j] = (float)0.5+(sumHarmonyNumerator/sumHarmonyDivisor);
	                    break;

			}
		    }
		}
		return simMatrix;
	}
	
	
	/**
	 * executes the specified combination for the given results
	 * - for results where some are using arrays and others database 
	 * @param results
	 * @return new match result containing the combined correspondences
	 */
	MatchResult combineNotSameObjects(MatchResult[] results) {
		// assumption all results have same graphs and same objects

		ArrayList<Object> srcObjectsAll = new ArrayList<Object>();
		ArrayList<Object> trgObjectsAll = new ArrayList<Object>();
		for (int i = 0; i < results.length; i++) {
				// must exist otherwise we would not be in mixed
				srcObjectsAll.addAll(results[i].getSrcObjects());
				trgObjectsAll.addAll(results[i].getTrgObjects());
		}
		if (srcObjectsAll==null || trgObjectsAll==null){
			System.out.println("Combination.combineMixed srcObjects or trgObjects null");
			return null;
		}
		if (srcObjectsAll.isEmpty() ||  trgObjectsAll.isEmpty()){
			System.out.println("Combination.combineMixed srcObjects or trgObjects null");
			return null;
		}
		MatchResult result = new MatchResultArray(srcObjectsAll, trgObjectsAll) ;
		
		//1. Dimension: matchers
		int k = results.length;
		//2. Dimension: elements of the first schema
		int m = srcObjectsAll.size();
		//3. Dimension: elements of the second schema
		int n = trgObjectsAll.size();
		
		for (int i=0; i<m; i++) {
			Object srcObj = srcObjectsAll.get(i);			
		    for (int j=0; j<n; j++) {
				Object trgObj = trgObjectsAll.get(j);				
				float maxSim = 0;
				float sumSim = 0;
				float weightedSim = 0;
				float minSim = Float.MAX_VALUE;			
				for (int l=0; l<k; l++) {
				    float sim = results[l].getSimilarity(srcObj, trgObj);
				    switch(combination) {
					    case COM_MAX: 
						if (sim>maxSim) maxSim = sim;
			                        break;
					    case COM_AVERAGE:
			                        sumSim+=sim;
			                        break;
					    case COM_WEIGHTED:  
						weightedSim += (sim*weights[l]);
			                        break;
					    case COM_MIN:  
						if (sim<minSim) minSim = sim;
			                        break;
				    }
				}
				switch(combination) {
				case COM_MAX: 
					if (maxSim>0)
						result.append(srcObj, trgObj, maxSim);
		                    break;
				case COM_AVERAGE: 
					sumSim = sumSim/k;
					if (sumSim>0)
						result.append(srcObj, trgObj, sumSim/k);
		                    break;
				case COM_WEIGHTED:
					if (weightedSim>0)
						result.append(srcObj, trgObj, weightedSim);
		                    break;
				case COM_MIN:
					if (minSim>0)
						result.append(srcObj, trgObj, minSim);
		                    break;
				}
		    }
		}
		result.setGraphs(results[0].getSourceGraph(), results[0].getTargetGraph());
		return result;
	}
	
	public MatchResult setCombination(ArrayList<Object> srcObjects, ArrayList<Object> trgObjects, 
			Resolution resolution, MatchResult singleResult){
		if (singleResult==null) return null;
		int type = resolution.returnType();
		Graph srcGraph = singleResult.getSourceGraph();
		Graph trgGraph = singleResult.getTargetGraph();
		
//		int countSrcObjects = 0, countSim = 0, count11 = 0, countnm = 0;
		MatchResult setResult = new MatchResultArray(srcObjects, trgObjects);
		for (Iterator<Object> iterator = srcObjects.iterator(); iterator.hasNext();) {
			Object srcObject = iterator.next();

//			if (countSrcObjects % 10 == 0) System.out.print(".");
//			if (countSrcObjects>1 && countSrcObjects % 1000 == 0) System.out.println();
//			countSrcObjects++;			
//			if (countSrcObjects % 20 == 0) break;
			
			ArrayList<Object> singleSrcObjects = null;
			if (type==Resolution.TYPE_RES2){
				singleSrcObjects = resolution.getResolution2(srcGraph, srcObject);
			} else if (type==Resolution.TYPE_RES3){
				singleSrcObjects = resolution.getResolution3(srcGraph, srcObject);
			}
			if (singleSrcObjects==null){
				continue;
			}
			for (Iterator<Object> iterator2 = trgObjects.iterator(); iterator2.hasNext();) {
				Object trgObject = iterator2.next();
				ArrayList<Object> singleTrgObjects = null;
				if (type==Resolution.TYPE_RES2){
					singleTrgObjects = resolution.getResolution2(trgGraph, trgObject);
				} else if (type==Resolution.TYPE_RES3){
					singleTrgObjects = resolution.getResolution3(trgGraph, trgObject);
				}
				if (singleTrgObjects==null){
					continue;
				}
//				if (singleSrcObjects.size()==1 && singleTrgObjects.size()==1 ){
//					count11++;
//				} else {
//					countnm++;
//				}
				float sim = computeSetSimilarity(singleSrcObjects, singleTrgObjects, singleResult);
//				float sim = 0;
				if (sim>0){
//					countSim++;
					setResult.append(srcObject, trgObject, sim);
				}
			}	
		}
//		System.out.println();
//		System.out.println("count11: " + count11 + "\tcountnm: " + countnm);
		setResult.setSourceGraph(srcGraph);
		setResult.setTargetGraph(trgGraph);
		return setResult;
	}
	
	
    //-------------------------------------------------------------------------//
    // Combined sim strategies (set similarity)                                //
    //-------------------------------------------------------------------------//
	
	float computeSetSimilarity(ArrayList<Object> singleSrcObjects,
			ArrayList<Object> singleTrgObjects, MatchResult singleResult) {
		// catch special case only one object in source and target
		if (singleSrcObjects.size()==1 && singleTrgObjects.size()==1 ){
			Object singleSrcObject = singleSrcObjects.get(0);
			Object singleTrgObject = singleTrgObjects.get(0);
			return singleResult.getSimilarity(singleSrcObject, singleTrgObject);
		}

		// singleSrcObjects and singleTrgObjects both have values
		float[][] simMatrix = new float[singleSrcObjects.size()][singleTrgObjects.size()];
		for (int i = 0; i < singleSrcObjects.size(); i++) {
			Object singleSrcObject = singleSrcObjects.get(i);
			for (int j = 0; j < singleTrgObjects.size(); j++) {
				Object singleTrgObject = singleTrgObjects.get(j);
				simMatrix[i][j] = singleResult.getSimilarity(singleSrcObject, singleTrgObject);
			}
		}
		return computeSetSimilarity(simMatrix);
	}
	
	
	float computeSetSimilarity(float[][] simMatrix) {                                  
		float sim;
		switch (combination){
		case SET_AVERAGE:
		case SET_HIGHEST:
			sim = computeSetSimilarity(simMatrix, combination, 0);
			break;
		case SET_DICE:
		case SET_MIN:
		case SET_MAX:
			sim = computeSetSimilarity(simMatrix, combination, (float)0.5);
			break;
		default: 
			sim = computeSetSimilarity(simMatrix, combination, 0);
//			sim = computeSetSimilarity(simMatrix, SET_AVERAGE, 0);
			break;
		}
		return sim;
	}
	
	float computeSetSimilarity(Integer[] trgIds, float[][] simmatrix) {
		// catch special case only one object in source and target
		if (trgIds.length==1 ){
			return simmatrix[0][trgIds[0]];
		}
		
		// singleSrcObjects and singleTrgObjects both have values
		float[][] simMatrix = new float[1][trgIds.length] ;
		// fill
		for (int i = 0; i < trgIds.length; i++) {
			float sim = simmatrix[0][trgIds[i]];
			simMatrix[0][i] = sim;
		}
		return computeSetSimilarity(simMatrix);
	}
	
	float computeSetSimilarity(Integer[] srcIds, Integer[] trgIds, float[][] simmatrix) {
		// catch special case only one object in source and target
		if (srcIds.length==1 && trgIds.length==1 ){
			return simmatrix[0][trgIds[0]];
		}
		List<Integer> srcList = Arrays.asList(srcIds);
		// singleSrcObjects and singleTrgObjects both have values
		float[][] simMatrix = new float[srcIds.length][trgIds.length] ;
		// fill
		for (int i = 0; i < srcIds.length; i++) {
			int src = srcList.indexOf(srcIds[i]);
			for (int j = 0; j < trgIds.length; j++) {
				simMatrix[src][j] = simmatrix[src][trgIds[j]];
			}
		}
		return computeSetSimilarity(simMatrix);
	}
	

	static float[][] combine(float[][][] similarityCube, int strategy, float[] weights,
				float sigmoid_factor, float sigmoid_shift)
	    {
	
			if (similarityCube != null && similarityCube.length != 0
					&& similarityCube[0].length != 0
					&& similarityCube[0][0].length != 0) {
				float[][] similarityMatrix = new float[similarityCube.length][similarityCube[0].length];
				if (weights == null) {
					weights = new float[similarityCube[0][0].length];
					for (int i = 0; i < similarityCube[0][0].length; i++) {
						weights[i] = 1f / weights.length;
					}
				}
	
				if(strategy==COM_OWA_ALH){
					for (int i = 0; i < similarityCube.length; i++) {
						for (int j = 0; j < similarityCube[i].length; j++) {
							float[] values= similarityCube[i][j];
							Float[] sortedValues= new Float[values.length];
							for (int k = 0; k < values.length; k++) {
								float f = values[k];
								sortedValues[k]=f;
							}
							Arrays.sort(sortedValues, Collections.reverseOrder());
							weights=  getOWAWeightALH(sortedValues);
							float[] sortedValues2= new float[values.length];
							for (int k = 0; k < sortedValues.length; k++) {
								float f = sortedValues[k];
								sortedValues2[k]=f;
							}
	
							similarityMatrix[i][j] = combineRow(sortedValues2,
									weights, COM_WEIGHTED2);
						}
					}
					return similarityMatrix;
				}
	
				if(strategy==COM_OWA_MOST){
					for (int i = 0; i < similarityCube.length; i++) {
						for (int j = 0; j < similarityCube[i].length; j++) {
							float[] values= similarityCube[i][j];
							Float[] sortedValues= new Float[values.length];
							for (int k = 0; k < values.length; k++) {
								float f = values[k];
								sortedValues[k]=f;
							}
							Arrays.sort(sortedValues, Collections.reverseOrder());
							weights=  getOWAWeightMost(sortedValues);
							float[] sortedValues2= new float[values.length];
							for (int k = 0; k < sortedValues.length; k++) {
								float f = sortedValues[k];
								sortedValues2[k]=f;
							}
	
							similarityMatrix[i][j] = combineRow(sortedValues2,
									weights, COM_WEIGHTED2);
						}
					}
					return similarityMatrix;
				}
	
				if (strategy==COM_HARMONY) {// compute
																				// weight
																				// using
																				// the
																				// harmony
					// Calculate weights using Harmony
					float simMatrix[][] = new float[similarityCube.length][similarityCube[0].length];
					weights = new float[similarityCube[0][0].length];
					for (int m = 0; m < weights.length; m++) {
						for (int k = 0; k < similarityCube.length; k++)
							for (int j = 0; j < similarityCube[0].length; j++)
								simMatrix[k][j] = similarityCube[k][j][m];
						weights[m] = ((float) 1 / weights.length)
								* getOriginalHarmony(simMatrix);
	
					}
					float sum = 0;
					for (int n = 0; n < weights.length; n++) {
						sum += weights[n];
					}
					if (sum < 1.0f) {
						float diff = ( 1 / sum);
						for (int t = 0; t < weights.length; t++) {
							weights[t] = weights[t] * diff;
						}
					}
					System.out.println("Computed Weights from Harmony");
					for (int i = 0; i < weights.length; i++) {
						System.out.print(" " + weights[i]);
					}
					System.out.println("");
					// end of harmony calculation
				}
	
				if (strategy==COM_SIGMOID) {
					//Recomputing similiarity values using a sigmoid function
					float newSimCube[][][] = new float[similarityCube.length][similarityCube[0].length][similarityCube[0][0].length];
					for (int i = 0; i < similarityCube.length; i++) {
						float[][] fs = similarityCube[i];
						for (int j = 0; j < fs.length; j++) {
							float[] fs2 = fs[j];
							for (int k = 0; k < fs2.length; k++) {
								float f = fs2[k];
								if (f > 0)
									newSimCube[i][j][k] = (float) sigmoid(f,
											sigmoid_factor, sigmoid_shift);
								else {
									newSimCube[i][j][k] = f;
								}
							}
						}
					}
	
					similarityCube = newSimCube;
	
				}
	// combination row wise
				for (int i = 0; i < similarityCube.length; i++) {
					for (int j = 0; j < similarityCube[i].length; j++) {
						similarityMatrix[i][j] = combineRow(similarityCube[i][j],
								weights, strategy);
					}
				}
	
				return similarityMatrix;
			} else
				return null;
		}

	private static float[] getOWAWeightALH(Float[] sortedValues)
	{
		float[] result= new float[sortedValues.length];
		int n= sortedValues.length;
		for (int i = 0; i < result.length; i++) {
			float wi=owaQALH((float)(i+1)/n) - owaQALH((float)i/n); // Q(i/n) - Q(i-1/n)  -- since i begins with 0, all with +1
			if(wi>0)
			result[i]=wi;
		}
		return result;
	}

	private static float[] getOWAWeightMost(Float[] sortedValues)
	{
		float[] result= new float[sortedValues.length];
		int n= sortedValues.length;
		for (int i = 0; i < result.length; i++) {
			float wi=owaQMost((float)(i+1)/n) - owaQMost((float)i/n); // Q(i/n) - Q(i-1/n)  -- since i begins with 0, all with +1
			if(wi>0)
			result[i]=wi;
		}
		return result;
	}

	private static float owaQALH(float input){
		float output=0;
		if(0<=input && input<=0.5f){
			output=2*input;
		}
		return output;
	}

	private static float owaQMost(float input){
		float output=0;
		if(0.3<=input && input<=0.8f){
			output=2*(input-0.3f);
		}
		if(0.8<input && input<=1f){
			output=1;
		}
		return output;
	}

	/**
	 *
	 * @param f
	 *            value to be changed by sigmoid
	 * @param factor
	 *            - factor that decided about the steepnes of the sigmoid curve
	 * @return
	 */
	private static double sigmoid(float f, float factor, float shift) {
		double result = 1 / (1 + Math.exp(-factor * (f - shift)));
		return result;
	}

	/**
	 * float cube[n][m][k]: n * m * k
	 * k sim matrices, n * m elements
	 *
	 *
	 */
	static float[][] combine(float[][][] similarityCube,
			int strategy) {
	
		return combine(similarityCube, strategy, null, 8f, 0.5f);
	}

	static float combineRow(float[] row, float[] weights,
			int strategy) {
		if (weights==null){
			System.out.println("Combination.combine Error weigths are null");
			return 0;
		}
		
		float combination = 0;
		float sumWeights = 0;
		boolean unknown = false;
		int cntUnknown = 0;
	
		for (int i = 0; i < row.length; i++)
			if (row[i] >= 0) {
				if (weights != null)
					sumWeights = sumWeights + weights[i];
			} else {
				unknown = true;
				cntUnknown++;
			}
	
		for (int i = 0; i < row.length; i++) {
			switch (strategy) {
			case COM_WEIGHTED2:
				if (unknown) {
					if (row[i] >= 0)
						combination += row[i] * weights[i] * 1 / sumWeights;
				} else
					combination += row[i] * weights[i];
				break;
			case COM_HARMONY:
				if (unknown) {
					if (row[i] >= 0)
						combination += row[i] * weights[i] * 1 / sumWeights;
				} else
					combination += row[i] * weights[i];
				break;
			case COM_SIGMOID:
				if (unknown) {
					if (row[i] >= 0)
						combination += row[i] * weights[i] * 1 / sumWeights;
				} else
					combination += row[i] * weights[i];
				break;
			}
		}
		return combination;
	}

	private static float getOriginalHarmony(float simMatrix[][]) {
		int matchedElements = 0;
		int countHarmony = 0;
		for (int k = 0; k < simMatrix.length; k++) {
			float max = 0;
			int maxIndex = -1;
			for (int l = 0; l < simMatrix[0].length; l++) {
				if (max < simMatrix[k][l]) {
					maxIndex = l;
					max = simMatrix[k][l];
				}
			}
			// not including values into harmony where the sim value is lower that MIN_VALUE/ this is a slight addition to the harmony
			if (maxIndex != -1 && max > 0) {
				matchedElements++;
				boolean found = false;
				for (int m = 0; m < simMatrix.length; m++) {
					if (simMatrix[m][maxIndex] > max) {
						found = true;
						break;
					}					
				}
				if (!found)
					countHarmony++;
			}
		}
		if (simMatrix.length > 0) {
			return (float) countHarmony
					/ Math.max(simMatrix.length, simMatrix[0].length);
		}
		return 0.0f;
	}

	public static float[][][] transformCube(float[][][] testCube) {
		float[][][] testCube2 = new float[testCube[0].length][testCube[0][0].length][testCube.length];
		// change tube organisation so that the last index enumerates the
		// sim-matrices
		for (int k = 0; k < testCube.length; k++) {
			for (int l = 0; l < testCube[0].length; l++) {
				for (int m = 0; m < testCube[0][0].length; m++) {
					testCube2[l][m][k] = testCube[k][l][m];
				}
			}
		}
		return testCube2;
	}
	
	public static float[][][] transformCubeBack(float[][][] testCube) {
		float[][][] testCube2 = new float[testCube[0][0].length][testCube.length][testCube[0].length];
		// change tube organisation so that the last index enumerates the
		// sim-matrices
		for (int m = 0; m < testCube.length; m++) {
			for (int n = 0; n < testCube[0].length; n++) {
				for (int k = 0; k < testCube[0][0].length; k++) {
					 testCube2[k][m][n]=testCube[m][n][k];
				}
			}
		}
		return testCube2;
	}

	static float computeSetSimilarity(float[][] simMatrix, int setStrategy, float threshold) {
        if (simMatrix==null) return 0;
	int m = simMatrix.length;
    if(m==0){
        return 0;
    }
	int n = simMatrix[0].length;
    if(n==0){
        return 0;
    }
	float sim = 0;
	if (setStrategy == SET_HIGHEST) {
		float simHighest = 0;
	    for (int i=0; i<m; i++) {
	    	for (int j=0; j<n; j++){
	    		if (simHighest < simMatrix[i][j]) simHighest = simMatrix[i][j];
	    	}
	    }
    	return simHighest;
	} else if (setStrategy == SET_AVERAGE) {
	    float maxSim_i = 0, maxSim_j = 0;
	    float sumSim_i = 0, sumSim_j = 0;
	    for (int i=0; i<m; i++) {
	    	maxSim_i = 0;
	    	// calculate for the row the maximum value
	    	for (int j=0; j<n; j++){
	    		if (maxSim_i < simMatrix[i][j]) maxSim_i = simMatrix[i][j];
	    	}
	    	sumSim_i += maxSim_i; // add to row sum
	    }
	    for (int j=0; j<n; j++) {
	    	maxSim_j = 0;
	    	// calculate for the column the maximum value
	    	for (int i=0; i<m; i++){
	    		if (maxSim_j < simMatrix[i][j]) maxSim_j = simMatrix[i][j];
	    	}
	    	sumSim_j += maxSim_j; // add to column sum
	    }
	    // divide by the number of values summed (measures of the matrix)
	    sim = (sumSim_i + sumSim_j) / (m + n); 
	}
	else if (setStrategy == SET_DICE || setStrategy == SET_MIN ||setStrategy == SET_MAX ) {
	    int matchCount_i = 0, matchCount_j = 0;
	    for (int i=0; i<m; i++){
			for (int j=0; j<n; j++)
			    if (simMatrix[i][j]>threshold) {
			    	// row contains a value > threshold, increase counter
			    	matchCount_i++;
			    	break;
			    }
	    }
	    for (int j=0; j<n; j++){
			for (int i=0; i<m; i++)
			    if (simMatrix[i][j]>threshold) {
			    	// column contains a value > threshold, increase counter
			    	matchCount_j++;
			    	break;
			    }
	    }
	    // summarize the count (#rows and #columns having a value > threshold)
	    sim = matchCount_i + matchCount_j ;
	    if (setStrategy == SET_DICE){
	    	// divide through the sum of #rows and #columns
	    	sim = (sim) / (m+n);
	    } else if (setStrategy == SET_MIN){
	    	// divide through twice of the smallest number of #rows or #columns
	    	sim = (sim) / (2 * ((m<n) ? m : n));
	    } else if (setStrategy == SET_MAX){
	    	// divide through twice of the largest number of #rows or #columns
	    	sim = (sim) / (2 * ((m<n) ? n : m));
	    }
	}
	return sim;
    }
	
}
