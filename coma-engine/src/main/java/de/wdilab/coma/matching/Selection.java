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

import java.util.Arrays;

import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;

/**
 * Selection realizes the selection of a set or correspondences from
 * a given match result following different possibilities:
 * - threshold (take every correspondence that has a least this similarity value)
 * - maxn (take the maxn best correspondences for an element)
 * - delta (take the best correspondence and all with delta percentage difference)
 * - combinations of threshold, maxn and delta
 * 
 *  part of the grammar
 *  
 * @author Hong Hai Do, Sabine Massmann
 */
public class Selection{

	  // start counting 7000  Manager.SEL_CNT
	
    //Match direction
    public static final int DIR_FORWARD   = Constants.SEL_CNT + 1;
    public static final int DIR_BACKWARD  = Constants.SEL_CNT + 2;
    public static final int DIR_BOTH      = Constants.SEL_CNT + 3;	// result of forward and backward restricted to values occuring in both directions
    public static final int DIR_SIMPLE      = Constants.SEL_CNT + 4; // result of forward and backward together (not restricted) -> needed for chopped selection
    public static final int DIR_DEFAULT   = DIR_BOTH;
    public static final int[] DIRECTION_IDS  = {DIR_BOTH, DIR_FORWARD, DIR_BACKWARD, DIR_SIMPLE};

    //Top down selection, applied to similarity values
    public static final int SEL_MAXN      = Constants.SEL_CNT + 11;
    public static final int SEL_DELTA  = Constants.SEL_CNT + 12;
    public static final int SEL_THRESHOLD = Constants.SEL_CNT + 13;
    public static final int SEL_MULTIPLE  = Constants.SEL_CNT + 14;
	
    public static final int[] SELECTION_IDS  = {SEL_MAXN, SEL_DELTA, SEL_THRESHOLD, SEL_MULTIPLE};

    int selection;
    int direction;
    int maxN = 0;
    float delta = 0;
    float threshold  = 0;    
	String name;
    
    // Constructors   
    /**
     * Constructor for especially multiple selection
     * @param direction
     * @param selection
     * @param selN
     * @param selDelta
     * @param selThres
     */
    public Selection(int direction, int selection, int selN, float selDelta, float selThres) {
        this.direction = direction;
        this.selection = selection;
        this.maxN = selN;
        this.delta = selDelta;
        this.threshold = selThres;
        name = toString();
      }
    
    /**
     * Constructor for maxn selection
     * @param direction
     * @param selection
     * @param selN
     */
    public Selection(int direction, int selection, int selN) {
        this.direction = direction;
        if (selection==SEL_MAXN){
        	this.selection = selection;
        	this.maxN = selN;
        } else if (selection==SEL_THRESHOLD && (selN==0 || selN==1)){
        	this.selection = selection;
        	this.threshold = selN;
        } else {
        	System.err.println("not valid");
        }
        name = toString();
      }
    
    /**
     * Constructor for threshold or delta selection
     * @param direction
     * @param selection
     * @param sel
     */
    public Selection(int direction, int selection, float sel) {
        this.direction = direction;
        this.selection = selection;
        if (selection==SEL_THRESHOLD){
        	this.threshold = sel;
        } else if (selection==SEL_DELTA){
        	 this.delta = sel;
        } else {
        	System.err.println("not valid");
        }
        name = toString();
      }
    
    /**
     * @param selection
     * @return the string representation for the selection
     */
    public static String selectionToString(int selection) {
        switch (selection) {
            case SEL_MAXN:            	return "MaxN";
            case SEL_DELTA:        		return "MaxDelta";
            case SEL_THRESHOLD:			return "Threshold";
            case SEL_MULTIPLE:        	return "Multiple";
            default: return "UNDEF";
          }
    }
    
    /**
     * @param selection
     * @param maxN
     * @param delta
     * @param threshold
     * @return the string representation for the selection and its parameters
     */
    public static String selectionToString(int selection, int maxN, float delta, float threshold) {
        switch (selection) {
            case SEL_MAXN:            	return "MaxN(" + maxN + ")";
            case SEL_DELTA:        		return "MaxDelta(" + delta + ")";
            case SEL_THRESHOLD:       	return "Threshold(" + threshold + ")";
            case SEL_MULTIPLE:        	return "Multiple(" + maxN + "," + delta + "," + threshold + ")";
            default: return "UNDEF";
          }
    }
    
    /**
     * @param selection
     * @return the id for the given string representation of a selection
     */
    public static int stringToSelection(String selection) {
        if (selection==null) return Constants.UNDEF;
        selection = selection.toLowerCase();
        if (selection.startsWith("maxn"))      return SEL_MAXN;
        else if (selection.startsWith("maxdelta"))  return SEL_DELTA;
        else if (selection.startsWith("threshold")) return SEL_THRESHOLD;
        else if (selection.startsWith("multiple"))  return SEL_MULTIPLE;
        return Constants.UNDEF;
    }
   
    /**
     * @param direction
     * @return the string representation for the direction
     */
    public static String directionToString(int direction) {
    	switch (direction) {
    	case DIR_FORWARD:   return "Forward";
    	case DIR_BACKWARD:  return "Backward";
    	case DIR_BOTH:      return "Both";
    	case DIR_SIMPLE:    return "Simple";
    	default: return "Undef";
    	}
    }
    
    /**
     * @param direction
     * @return the id for the given string representation of a direction
     */
    public static int stringToDirection(String direction) {
    	if (direction==null) return Constants.UNDEF;
    	direction = direction.toLowerCase();
    	if (direction.equals("forward"))  return DIR_FORWARD;
    	else if (direction.equals("backward")) return DIR_BACKWARD;
    	else if (direction.equals("both"))     return DIR_BOTH;
    	else if (direction.equals("simple"))     return DIR_SIMPLE;
    	return Constants.UNDEF;
    }
    
    
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
    	String s = "(" + directionToString(direction)  + "," + selectionToString(selection, maxN, delta, threshold) + ")";
    	return s;
    }
	
	/**
	 * executes the specified selection for the given result
	 * @param result MatchResult (either array or db based)
	 * @return MatchResult with the selected correspondences
	 */
	public MatchResult select(MatchResult result){
		// nothing to select
		if (result==null){
			return null;
		}
		// nothing to select therefore return copy of result
		if (result.getMatchCount()<=0 || result.getSrcObjects().isEmpty() || result.getTrgObjects().isEmpty())
			return result.clone();		
		if ( (selection == SEL_MAXN && maxN <= 0) 
				|| (selection == SEL_DELTA 	&& delta <= 0)
				|| (selection == SEL_THRESHOLD 	&& threshold <= 0))
			return result.clone();
		
		if (selection == SEL_MULTIPLE){
			if (maxN <= 0 && delta <= 0 && threshold <= 0){
				// nothing to select therefore return copy of result
				return result.clone();
			} else if (maxN > 0 && delta <= 0 && threshold <= 0){
				// only maxn selection, for optimization change from multiple
				selection = SEL_MAXN;
			}else if (maxN <= 0 && delta > 0 && threshold <= 0){
				// only delta selection, for optimization change from multiple
				selection = SEL_DELTA;				
			} else if (maxN <= 0 && delta <= 0 && threshold > 0){
				// only threshold selection, for optimization change from multiple
				selection = SEL_THRESHOLD;
			}
		}

		int kind = MatchResult.getMatchResultKind(result);
		if (kind==MatchResult.UNDEF){
			return null;
		}

		switch (kind) {
		case MatchResult.ARRAY:
				return selectArray((MatchResultArray)result); 

		}
		return null;
	}
	
	// General select method for arrays
	/**
	 * executes the specified selection for the match result that uses array 
	 * @param result
	 * @return new match result with only the selected correspondences
	 */
	MatchResult selectArray(MatchResultArray result) {
		// assumption all results have same graphs and same objects
		MatchResultArray newResult = new MatchResultArray(
				result.getSrcObjects(), result.getTrgObjects());
		float[][] simMatrix = null;
		if (selection == SEL_MULTIPLE) {
			simMatrix = selectArrayMultiple( result.getSimMatrix());
		} else {
			simMatrix = selectArraySingle(result.getSimMatrix());
		}
		if (simMatrix == null) {
			return null;
		}
		newResult.setSimMatrix(simMatrix);
		newResult.setSourceGraph(result.getSourceGraph());
		newResult.setTargetGraph(result.getTargetGraph());
		return newResult;
	}
	
	/**
	 * executes the specified multiple selection for the given similarity matrix
	 * @param simMatrix
	 * @return similarity matrix with the selected correspondences
	 */
	float[][] selectArrayMultiple(float[][] simMatrix) {
		// Dimension
		int m = simMatrix.length;
		int n = simMatrix[0].length;
	
		float[] forwardSelSim = new float[m];
		float[] backwardSelSim = new float[n];
		float diffdelta = 1-delta;
		
		if (direction!=DIR_BACKWARD){
			for (int i = 0; i < m; i++) {
				float[] sortedSims = new float[n];
				for (int j = 0; j < n; j++)
					sortedSims[j] = simMatrix[i][j];
				Arrays.sort(sortedSims); // ascending sort
				if (maxN > 0) {
					if (maxN > n)
						forwardSelSim[i] = sortedSims[0];
					else
						forwardSelSim[i] = sortedSims[n - maxN];
				}
				if (delta > 0) {
					// sortedSims[n-1] -> maxSim
					float sim = sortedSims[n - 1] * diffdelta;
					if (forwardSelSim[i] < sim)
						forwardSelSim[i] = sim;
				}
				if (threshold > 0) {
					if (forwardSelSim[i] < threshold)
						forwardSelSim[i] = threshold;
				}	
			}
		}
		if (direction!=DIR_FORWARD){
			for (int j = 0; j < n; j++) {
				float[] sortedSims = new float[m];
				for (int i = 0; i < m; i++)
					sortedSims[i] = simMatrix[i][j];
				Arrays.sort(sortedSims); // ascending sort
				float maxSim = sortedSims[m - 1];
				if (maxN > 0) {
					if (maxN > m)
						backwardSelSim[j] = sortedSims[0];
					else
						backwardSelSim[j] = sortedSims[m - maxN];
				}
				if (delta > 0) {
					float sim = maxSim * diffdelta;
					if (backwardSelSim[j] < sim)
						backwardSelSim[j] = sim;
				}
				if (threshold > 0) {
					if (backwardSelSim[j] < threshold)
						backwardSelSim[j] = threshold;
				}
			}
		}
		return selectArrayDirection(simMatrix, m, n, forwardSelSim, backwardSelSim);
	}
	
	/**
	 * executes the specified single selection for the given similarity matrix
	 * @param simMatrix
	 * @return similarity matrix with the selected correspondences
	 */
	float[][] selectArraySingle(float[][] simMatrix) {
		// Dimensions
		int m = simMatrix.length;
		if (m == 0 || simMatrix[0] == null) {
			// empty matrix
			return null; 
		}
		int n = simMatrix[0].length;
		
		float[] forwardSelSim = new float[m];
		float[] backwardSelSim = new float[n];
		float diffdelta = 1-delta;
	
		if (direction!=DIR_BACKWARD){
			if (selection == SEL_THRESHOLD) {
				// independent of the actual similarity values
				for (int i = 0; i < m; i++) {
					forwardSelSim[i] = threshold;
				}
			} else {
				// dependent of the actual similarity values in row/column
				for (int i = 0; i < m; i++) {
					float[] sortedSims = new float[n];
					for (int j = 0; j < n; j++)
						sortedSims[j] = simMatrix[i][j];
					Arrays.sort(sortedSims); // ascending sort
					switch (selection) {
					case SEL_MAXN:
						if (maxN > n)
							forwardSelSim[i] = sortedSims[0];
						else
							forwardSelSim[i] = sortedSims[n - maxN];
						break;
					case SEL_DELTA:
						// sortedSims[n-1] -> maxSim
						forwardSelSim[i] = sortedSims[n - 1] * diffdelta;
						break;
					}
				}
			}
		}
		
		if (direction!=DIR_FORWARD){
			if (selection == SEL_THRESHOLD) {
				// independent of the actual similarity values
				for (int j = 0; j < n; j++) {
					backwardSelSim[j] = threshold;
				}
			} else {
				// dependent of the actual similarity values in row/column
				for (int j = 0; j < n; j++) {
					float[] sortedSims = new float[m];
					for (int i = 0; i < m; i++)
						sortedSims[i] = simMatrix[i][j];
					Arrays.sort(sortedSims); // ascending sort
					switch (selection) {
					case SEL_MAXN:
						if (maxN > m)
							backwardSelSim[j] = sortedSims[0];
						else
							backwardSelSim[j] = sortedSims[m - maxN];
						break;
					case SEL_DELTA:
						// sortedSims[m-1] -> maxSim
						backwardSelSim[j] = sortedSims[m - 1] * diffdelta;
						break;
					}
				}
			}
		}
		return selectArrayDirection(simMatrix, m, n, forwardSelSim, backwardSelSim);
	}
	
	/**
	 * executes the specified selection for the given direction and minimum similarity values 
	 * @param simMatrix
	 * @param m	source dimension of matrix 
	 * @param n target dimension of matrix 
	 * @param forwardSelSim	minimum similarity values for the forward selection (also used for both and simple)
	 * @param backwardSelSim minimum similarity values for the backward selection (also used for both and simple)
	 * @return a new matrix only containing the selected similarity values
	 */
	float[][] selectArrayDirection(float[][] simMatrix, int m, int n, float[] forwardSelSim, float[] backwardSelSim){
		// Make a new copy of input simMatrix for selection
		float[][] selMatrix = new float[m][n];
		for (int i = 0; i < m; i++)
			System.arraycopy(simMatrix[i], 0, selMatrix[i], 0, n);		
		
		if (direction == DIR_FORWARD || direction == DIR_BOTH) {
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
						if (selMatrix[i][j] < forwardSelSim[i])
							selMatrix[i][j] = 0;
				}
			}
		}
		if (direction == DIR_BACKWARD || direction == DIR_BOTH) {
			for (int j = 0; j < n; j++) {
				for (int i = 0; i < m; i++) {
						if (selMatrix[i][j] < backwardSelSim[j])
							selMatrix[i][j] = 0;
				}
			}
		}
		if (direction == DIR_SIMPLE) {
			for (int j = 0; j < n; j++) {
				for (int i = 0; i < m; i++) {
						if (selMatrix[i][j] < backwardSelSim[j]
						         && selMatrix[i][j] < forwardSelSim[i])
							selMatrix[i][j] = 0;
				}
			}
		}
		return selMatrix;
	}
	

}
