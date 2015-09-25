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

package de.wdilab.coma.structure;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graphs;

import de.wdilab.coma.structure.graph.GraphUtil;

/**
 * MatchResult contains the correspondences (with similarity values)
 * between the objects of a source graph and a target graph
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public abstract class MatchResult {
	// Constants
	/** undefined similarity value (reasons e.g. not yet computed, algorithm
	 * was not able to compute or object not in the lists)*/
	public static final float SIM_UNDEF = 0; // previously -1
	/** smallest similarity value that is valid, indicates that objects are dissimilar */
	public static final float SIM_MIN = 0;
	/** highest similarity value that is valid, indicates that objects are equal*/
	public static final float SIM_MAX = 1;
	/** match result operation undefined*/
	public static final int OP_UNDEF = -1;
	/** match result operations*/
	public static final int OP_INTERSECT = 0;
	public static final int OP_MERGE = 1;
	public static final int OP_DIFF = 2;
	public static final int OP_COMPOSE = 3;
	public static final int OP_TRANSPOSE = 4;
	public static final int OP_TRANSFORM = 5;
	/** all match result operations*/
	public static final int[] OP = {
		OP_INTERSECT, OP_MERGE, OP_DIFF, OP_COMPOSE, OP_TRANSPOSE, OP_TRANSFORM
	};
	/** all match result operations that needs matchresults to have the same graphs (src, trg)*/
	public static final int[] OP_SAMEGRAPHS = {
		OP_INTERSECT, 
		OP_DIFF,
		OP_MERGE, 
	
	};
	/** all match result operations that operate on one matchresult*/
	public static final int[] OP_SINGLEMR = {
		OP_TRANSPOSE, 
		OP_TRANSFORM
	};
	
    public static final int UNDEF = -1;
    public static final int ARRAY = 0;
    public static final int DB = 1;
    public static final int MIXED = 2;
	
    public static final String MAP_OB_UNKNOWN   = "Unknown";
	
 
    
    public static final int COMPOSITION_UNDEF = -1;
    public static final int COMPOSITION_MIN = 1;
    public static final int COMPOSITION_MAX = 2;
    public static final int COMPOSITION_AVERAGE = 3;
    public static final int COMPOSITION_SUM = 4; //used to calculate correct average for pathlenght>2
    public static final int[] COMPOSE_IDS      = {COMPOSITION_MIN, COMPOSITION_MAX, COMPOSITION_AVERAGE };
    
    
    /**
     * @param combination
     * @return the string representation for the combination
     */
    public static String compositionToString(int composition) {
    	switch (composition) {
    	// sim combination
    		case COMPOSITION_MIN:        return "Com_Min";
    		case COMPOSITION_MAX:    return "Com_Max";
    		case COMPOSITION_AVERAGE:   return "Com_Average";       
    		default: return "UNDEF";
    	}
    }
    
    /**
     * @param combination
     * @return the id for the given string representation of a combination
     */
    public static int stringToComposition(String composition) {
        if (composition==null) return COMPOSITION_UNDEF;
        composition = composition.toLowerCase();
        if (composition.equals("com_min")) return COMPOSITION_MIN;
        else if (composition.equals("com_max"))    return COMPOSITION_MAX;
        else if (composition.startsWith("com_average"))    return COMPOSITION_AVERAGE;
        return COMPOSITION_UNDEF;
      }
    
    
	// variables
	/** The source graph */
	Graph sourceGraph;
	/** The target graph */
	Graph targetGraph;
	/** The name for this result */
	String name;
	/** The match information for this result e.g. algorithm, configuration */
	String matchInfo;
	/** Location of match result file */
	String provider;
	/** The source objects (nodes or paths) */
	ArrayList<Object> srcObjects;
	/** The target objects (nodes or paths) */
	ArrayList<Object> trgObjects;
	/** If true use hashing via an index for the source and target objects*/
	boolean hashing = true;
	/** object (e.g. intermediate match results) for displaying three-splitpane **/
	public Object userObject = null;
	

	// constructors
	/**
	 * Constructor which only initiates internal settings
	 */
	public MatchResult() {
		srcObjects = new ArrayList<Object>();
		trgObjects = new ArrayList<Object>();
	}
	

	public MatchResult(ArrayList<Object> srcObjects, ArrayList<Object> trgObjects) {

	}

	// simple getters
	public Graph getSourceGraph() { return sourceGraph; }
	public Graph getTargetGraph() { return targetGraph; }
	public String getName() { return name; }
	public String getProvider() { return provider; }
	public ArrayList<Object> getSrcObjects() { return srcObjects; }
	public ArrayList<Object> getTrgObjects() { return trgObjects; }
	public String getMatchInfo() { return matchInfo; }
	public Object getUserObject() {	return userObject; }
	
	// simple setters
	public void setSourceGraph(Graph graph) { sourceGraph = graph; }
	public void setTargetGraph(Graph graph) { targetGraph = graph; }
	public void setGraphs(Graph source, Graph target) { 
		sourceGraph = source;
		targetGraph = target;
	}
	public void setName(String name) { this.name = name; }
	public void setMatchInfo(String info) { matchInfo = info; }
	public void setProvider(String provider) { this.provider = provider; }
	public void setUserObject(Object userObject) { this.userObject = userObject; }

	
	  
	public int getSrcMatchObjectsCount(){
		ArrayList<Object> objects = getSrcMatchObjects();
		if (objects==null) return 0;
		return objects.size();
	}
	
	public int getTrgMatchObjectsCount(){
		ArrayList<Object> objects = getTrgMatchObjects();
		if (objects==null) return 0;
		return objects.size();
	}
	
	// functions	
	/**
	 * @param id operation id
	 * @return name representing this operation
	 */
	public static String operationToString(int id) {
		switch (id) {
		case OP_INTERSECT:  return "INTERSECT";
		case OP_MERGE:      return "MERGE";
		case OP_DIFF:      	return "DIFF";
		case OP_COMPOSE:    return "COMPOSE";
		case OP_TRANSPOSE:    return "TRANSPOSE";
		case OP_TRANSFORM:    return "TRANSFORM";
		default: return "UNDEF";
		}
	}
	
    /**
     * @param operation operation name
     * @return operation id
     */
    public static int stringToOperation(String operation) {
        if (operation==null) return OP_UNDEF;
        else if (operation.startsWith("INTERSECT")) return OP_INTERSECT;
        else if (operation.startsWith("MERGE")) return OP_MERGE;
        else if (operation.startsWith("DIFF")) return OP_DIFF;
        else if (operation.startsWith("COMPOSE"))  return OP_COMPOSE;
        else if (operation.startsWith("TRANSPOSE"))  return OP_TRANSPOSE;
        else if (operation.startsWith("TRANSFORM"))  return OP_TRANSFORM;
        return OP_UNDEF;
      }
	

	
	

	
	/**
	 * compare this mapping (the intended mapping) with another mapping (the test mapping)
	 * @param testResult
	 * @return evaluation measures e.g. precision, recall, f-measure
	 */
	public EvaluationMeasure compare(MatchResult testResult) {
		if (testResult == null) return null;
		int I = 0; // # intended correspondences
		int T = 0; // # test correspondences
		int M = 0; // # true positives (correct matches in test)
		for (int i = 0; i < srcObjects.size(); i++) {
			Object srcObj = srcObjects.get(i);
			for (int j = 0; j < trgObjects.size(); j++) {
				Object trgObj = trgObjects.get(j);
				float sim = getSimilarity(srcObj, trgObj);
				if (sim > 0) {
					I++; // an intended match
					if (testResult.getSimilarity(srcObj, trgObj) > 0) {
						M++; // true positive, also found in test
					}
				}
			}
		}
		// match found in testResult
		T = testResult.getMatchCount();
		EvaluationMeasure measure = new EvaluationMeasure(I,T,M);
		return measure;
	}

	
	/**
	 * @param id operation id
	 * @param result1
	 * @param result2
	 * @return result matchresult that is generated by the operation
	 * this function calls operations that need two matchresults as intput e.g. intersect, compose
	 * - if the matchresults are of the same kind (db, array) than call their optimized methods
	 */
	public  static MatchResult applyOperation(int id, MatchResult result1, MatchResult result2){
		switch (id) {
		case OP_INTERSECT:  
			return intersect(result1, result2);
		case OP_MERGE:   
			return merge(result1, result2);
		case OP_DIFF:      	
			return diff(result1, result2);
		case OP_COMPOSE:    
			return compose(result1, result2, COMPOSITION_AVERAGE);		
		default: 
			System.err.println("MatchResult.applyOperation unkown operation");
			return null;
		}
	}
	
	/**
	 * @param id operation id
	 * @param result
	 * @return result matchresult that is generated by the operation
	 * this function calls operations that are based on one matchresult e.g. transpose
	 */
	public  static MatchResult applyOperation(int id, MatchResult result){
		switch (id) {
		case OP_TRANSPOSE:  
			return transpose(result);
		default: 
			System.err.println("MatchResult.applyOperation unkown operation");
			return null;
		}
	}
	
	/**
	 * @param result
	 * @return transposed match result -> source and target objects switched
	 * and similarity reversed as well
	 */
	public static MatchResult transpose(MatchResult result) {
			return MatchResultArray.transpose((MatchResultArray)result);
	}
	
	
	/**
	 * @param result1
	 * @param result2
	 * @return the correspondences that appear in both match results
	 * if one of the result is null return null
	 * if the result have different graphs return null
	 */
	public static MatchResult intersect(MatchResult result1, MatchResult result2) {
		if (result1 == null || result2 == null) return null;		
	    if (!sameGraphs(result1, result2)){
	    	return null;
	    }		
		if (result1 instanceof MatchResultArray && result2  instanceof MatchResultArray){
			return MatchResultArray.intersect((MatchResultArray)result1, (MatchResultArray)result2);
		}    
	    
		MatchResultArray interResult = new MatchResultArray();
		ArrayList<Object> srcObjectsBoth = new ArrayList<Object>(result1.getSrcObjects());
		srcObjectsBoth.retainAll(result2.getSrcObjects());
		ArrayList<Object> trgObjectsBoth = new ArrayList<Object>(result1.getTrgObjects());
		trgObjectsBoth.retainAll(result2.getTrgObjects());
		
		for (int i = 0; i < srcObjectsBoth.size(); i++) {
			Object srcObj = srcObjectsBoth.get(i);
			for (int j = 0; j < trgObjectsBoth.size(); j++) {
				Object trgObj = trgObjectsBoth.get(j);
				float sim = result1.getSimilarity(srcObj, trgObj);
				if (sim > 0 && result2.getSimilarity(srcObj, trgObj) > 0){
						interResult.append(srcObj, trgObj, sim);
				}
			}
		}
		if (interResult.getSrcObjects().isEmpty() || interResult.getTrgObjects().isEmpty())
			return null;
		String matchInfo = operationToString(OP_INTERSECT) + " " + result1.getName() + " - " + result2.getName();
		interResult.setMatchInfo(matchInfo);
		interResult.setGraphs(result1.getSourceGraph(), result1.getTargetGraph());
		return interResult;
	}
	
	/**
	 * @param result1
	 * @param result2
	 * @return the correspondences that appear in result1 but not in result2
	 * if one of the result is null return null
	 * if the result have different graphs return null
	 */
	  public static MatchResult diff(MatchResult result1, MatchResult result2) {
	    if (result1==null) return null;
	    else if (result2==null) return result1;
	    
	    if (!sameGraphs(result1, result2)){
	    	return null;
	    }
	    MatchResult diffResult = null;
		if (result1 instanceof MatchResultArray && result2  instanceof MatchResultArray){
			return MatchResultArray.diff((MatchResultArray)result1, (MatchResultArray)result2);
		}	    
	    
	    // two different kinds of matchresult -> one is database (because only two kinds exist) 
		diffResult = new MatchResultArray();
	   
	    ArrayList<Object> srcObjects = result1.getSrcObjects();
	    ArrayList<Object> trgObjects = result1.getTrgObjects();
	    for (int i=0; i<srcObjects.size(); i++) {
	      Object srcObj = srcObjects.get(i);
	      for (int j=0; j<trgObjects.size(); j++) {
	        Object trgObj = trgObjects.get(j);
	        float sim = result1.getSimilarity(srcObj, trgObj);
	        if (sim>0 && result2.getSimilarity(srcObj, trgObj)<=0){
	            diffResult.append(srcObj, trgObj, sim);
	        }
	      }
	    }
	    if (diffResult.getSrcObjects().isEmpty() ||
	        diffResult.getTrgObjects().isEmpty()) return null;
	    diffResult.setName(operationToString(OP_DIFF));
	    diffResult.setMatchInfo(result1.getName() + "|" + result2.getName());
	    diffResult.setGraphs(result1.getSourceGraph(), result1.getTargetGraph());
	    return diffResult;
	  }
	   
	
	  /**
	 * @param result1
	 * @param result2
	 * @return check if both results have the same graphs
	 * 
	 */
	public static boolean sameGraphs(MatchResult results1, MatchResult results2){
		return sameGraphs(new MatchResult[] {results1, results2});
	}
	  /**
	 * @param result1
	 * @param result2
	 * @return check if all results have the same graphs
	 * 
	 */
	public static boolean sameGraphs(MatchResult[] results){
		if (results==null || results.length<2){
			return true;
		}
		boolean sameGraphs = true;
		for (int i = 0; i < results.length-1; i++) {
			Graph srcGraph1 = results[i].getSourceGraph();
			Graph srcGraph2 = results[i+1].getSourceGraph();
			
			Graph trgGraph1 = results[i].getTargetGraph();
			Graph trgGraph2 = results[i+1].getTargetGraph();
			if (srcGraph1!=null && trgGraph1!=null && 
					(!srcGraph1.equals(srcGraph2) || !trgGraph1.equals(trgGraph2))){
				sameGraphs = false;
				break;
			} 
		}
		return sameGraphs;
	}
	
	  /**
	 * @param result1
	 * @param result2
	 * @return check if both results have the same source and target objects
	 * 
	 */
	public static boolean sameObjects(MatchResult results1, MatchResult results2){
//		return sameObjects(new MatchResult[] {results1, results2});
		return false; // for testing operations without optimization
	}
	
	  /**
	 * @param result1
	 * @param result2
	 * @return check if all results have the same source and target objects
	 * 
	 */
	public static boolean sameObjects(MatchResult[] results){
		if (results==null || results.length<2){
			return true;
		}
		boolean sameObjects = true;
		for (int i = 0; i < results.length-1; i++) {
			if (results[i].getSrcObjects()!=null && results[i].getTrgObjects()!=null && 
					(!results[i].getSrcObjects().equals(results[i+1].getSrcObjects()) || !results[i].getTrgObjects().equals(results[i+1].getTrgObjects()))){
				sameObjects = false;
				break;
			} 
		}
		return sameObjects;
	}
	  
	  /**
		 * add a complete match result to another match result
		 * @param result1
		 * @param result2
		 * @return merged match result
		 * returns null if the match result don't have the same graphs
		 */
	  public static MatchResult merge(MatchResult result1, MatchResult result2) {
		    if (result1==null) return result2;
		    else if (result2==null) return result1;
		    
		    if (!sameGraphs(result1, result2)){
		    	return null;
		    }

			if (result1 instanceof MatchResultArray && result2  instanceof MatchResultArray){
				return MatchResultArray.merge((MatchResultArray)result1, (MatchResultArray)result2);
			}
		    
		    MatchResult diff = diff(result2, result1);
		    if (diff==null) return result1;
		    MatchResult mergeResult = null;

		    // two different kinds of matchresult -> one is database (because only two kinds exist) 
		    mergeResult = new MatchResultArray();
		    
		    ArrayList<Object> resAObjects = result1.getSrcObjects();
		    ArrayList<Object> resBObjects = result1.getTrgObjects();
		    for (int i=0; i<resAObjects.size(); i++) {
			      Object srcObj = resAObjects.get(i);
		      for (int j=0; j<resBObjects.size(); j++) {
		    	  Object trgObj = resBObjects.get(j);
		    	  float sim = result1.getSimilarity(srcObj, trgObj);
		        if (sim>0)
		          mergeResult.append(resAObjects.get(i), resBObjects.get(j), sim);
		      }
		    }

		    ArrayList<Object> diffAObjects = diff.getSrcObjects();
		    ArrayList<Object> diffBObjects = diff.getTrgObjects();
		    for (int i=0; i<diffAObjects.size(); i++) {
		    	 Object srcObj = diffAObjects.get(i);
		      for (int j=0; j<diffBObjects.size(); j++) {
		    	  Object trgObj = diffBObjects.get(j);
		    	  float sim = diff.getSimilarity(srcObj, trgObj);
		        if (sim>0)
		          mergeResult.append(diffAObjects.get(i), diffBObjects.get(j), sim);
		      }
		    }

		    mergeResult.setName(operationToString(OP_MERGE));
		    mergeResult.setMatchInfo(result1.getName() + "|" + result2.getName());
		    mergeResult.setGraphs(result1.getSourceGraph(), result1.getTargetGraph());
		    return mergeResult;
	}
	  
	  /**
	 * compose two matchresults with an overlapping graph to create a new matchresult
	 * @param leftResult
	 * @param rightResult
	 * @return matchresult
	 * return null if either of the matchresults is null or don't have a same graph
	 */
	public static MatchResult compose(MatchResult leftResult, MatchResult rightResult, int composition) {
		    if (leftResult==null || rightResult==null) return null;
		    
			if (leftResult instanceof MatchResultArray && rightResult  instanceof MatchResultArray){
				return MatchResultArray.compose((MatchResultArray)leftResult, (MatchResultArray)rightResult, composition);
//			} else if (leftResult instanceof MatchResultDB && rightResult  instanceof MatchResultDB){
//				return MatchResultDB.compose((MatchResultDB)leftResult, (MatchResultDB)rightResult);
			}
		    
			if (leftResult.getTargetGraph()==rightResult.getSourceGraph()){
				// normal case
			} else if (leftResult.getSourceGraph()==rightResult.getSourceGraph()){
				// transpose left result
				leftResult = MatchResult.transpose(leftResult);
			} else if (leftResult.getTargetGraph()==rightResult.getTargetGraph()){
				// transpose right result
				rightResult = MatchResult.transpose(rightResult);
			} else if (leftResult.getSourceGraph()==rightResult.getTargetGraph()){
				// transpose left and right result
				leftResult = MatchResult.transpose(leftResult);
				rightResult = MatchResult.transpose(rightResult);
			} else {
				System.out.println("MatchResult.compose no graph overlapping ");
				return null;
			}
		    
		    boolean verbose = false;
		    if (verbose) {
		      System.out.println("Compose mappings: ");
		      System.out.println("LeftMapping: " + leftResult.getSourceGraph().getSource() + "<->" + leftResult.getTargetGraph().getSource());
		      System.out.println("RightMapping: " + rightResult.getSourceGraph().getSource() + "<->" + rightResult.getTargetGraph().getSource());
		    }
		    MatchResultArray compResult = new MatchResultArray();
		    compResult.setName(operationToString(OP_COMPOSE));
		    compResult.setMatchInfo(leftResult.getName() + "|" + rightResult.getName());
		    compResult.setGraphs(leftResult.getSourceGraph(), rightResult.getTargetGraph());

		    //Left correspondences to drive composition
		    ArrayList<Object> leftSrcObjects = leftResult.getSrcObjects();
		    ArrayList<Object> leftTrgObjects = leftResult.getTrgObjects();
//		    ArrayList rightSrcObjects = rightResult.getTrgObjects();
		    ArrayList<Object> rightTrgObjects = rightResult.getTrgObjects();
		    for (int i=0; i<leftSrcObjects.size(); i++) {
		      Object leftSrcObj = leftSrcObjects.get(i);
		      for (int j=0; j<leftTrgObjects.size(); j++) {
		        Object leftTrgObj = leftTrgObjects.get(j);
		        float leftSim = leftResult.getSimilarity(leftSrcObj, leftTrgObj);
		        if (leftSim>0) { //its a correspondence
		          if (verbose) {
		            System.out.print("Left correspondence: ");
		            if (leftSrcObj instanceof Element)
		              System.out.print(((Element) leftSrcObj).getName());
		            else if (leftSrcObj instanceof Path)
		              System.out.print(((Path)leftSrcObj).toNameString());
		            else
		              System.out.print(leftSrcObj);
		            System.out.print(" <-> ");
		            if (leftTrgObj instanceof Element)
		              System.out.println(((Element)leftTrgObj).getName());
		            else if (leftTrgObj instanceof Path)
		              System.out.println(((Path)leftTrgObj).toNameString());
		            else
		              System.out.println(leftTrgObj);
		          }
		          //int m = rightSrcObjects.indexOf(leftTrgObj);
		          boolean m = rightResult.getSrcObjects().contains(leftTrgObj);
		          if (m) {
		            for (int n=0; n<rightTrgObjects.size(); n++) {
		              Object rightBObj = rightTrgObjects.get(n);
		              float rightSim = rightResult.getSimilarity(leftTrgObj, rightBObj);
		              if (rightSim>0) {
		                if (verbose) {
		                  System.out.print(" - Right matching object: ");
		                  if (rightBObj instanceof Element)
		                    System.out.println(((Element)rightBObj).getName());
		                  else if (rightBObj instanceof Path)
		                    System.out.println(((Path)rightBObj).toNameString());
		                  else
		                    System.out.println(rightBObj);
		                }

		                //compose similarities
		                float sim = 0;
		                switch (composition) {
		                  case COMPOSITION_MAX:
		                     sim = (leftSim>rightSim)?leftSim:rightSim;
		                     break;
		                  case COMPOSITION_MIN:
		                     sim = (leftSim<rightSim)?leftSim:rightSim;
		                     break;
		                  case COMPOSITION_SUM: // used to later calculate correct average if more than two matchresults
			                 sim = leftSim+rightSim;
			                 break;
		                  default: //COM_COMP_SIMAVERAGE
		                     sim = (leftSim+rightSim)/2;
		                     break;
		                }
		                compResult.append(leftSrcObj, rightBObj, sim);
		              }
		            }
		          }
		        }
		      }
		    }
		    if (verbose) System.out.println("Compose Mappings: DONE!");
		    if (compResult.getSrcObjects().isEmpty() || compResult.getTrgObjects().isEmpty())
		      return null;
		    return compResult;
		  }

	
	  //---------------------------------------------------------------------------//
	  //Transform a match result to a targetState of schemaGraph
	  //---------------------------------------------------------------------------//
	  public static MatchResult transformMatchResult(MatchResult matchResult, int preprocessing) {
	    if (matchResult==null) return null;
	    Graph sourceGraph = matchResult.getSourceGraph();
	    Graph targetGraph = matchResult.getTargetGraph();
	    if (sourceGraph==null || targetGraph==null) return null;
	    if (preprocessing==sourceGraph.getPreprocessing() &&
	        preprocessing==targetGraph.getPreprocessing())
	    	// nothing to do, alread correct preprocessing
	      return matchResult;
	    if (sourceGraph.getGraph(preprocessing)==null || targetGraph.getGraph(preprocessing)==null){
	    	System.out.println("transformMatchResult(): one or both graphs do not support this targetState ("+Graph.preprocessingToString(preprocessing)+")!");
	    	return null;
	    }
	    
	    
//	    long start, end;
	    //trim matchResult
	    matchResult = matchResult.trim();

	    ArrayList sourceComps = matchResult.getSrcObjects();
	    ArrayList targetComps = matchResult.getTrgObjects();
//	    start = System.currentTimeMillis();
	    MatchResult sourceTransformMapping = GraphUtil.transformComponents(sourceGraph,sourceComps, preprocessing);
	    MatchResult targetTransformMapping = GraphUtil.transformComponents(targetGraph,targetComps, preprocessing);
//	    end = System.currentTimeMillis();
//	    System.out.println("         Transform components: " + (end-start));

	    //Compose
//	    start = System.currentTimeMillis();
	    MatchResult transformedResult = null;
	    if (sourceTransformMapping!=null && targetTransformMapping!=null) {
	      transformedResult = MatchResult.compose(
	                              MatchResult.compose(
	                                    MatchResult.transpose(sourceTransformMapping),
	                                    matchResult, COMPOSITION_AVERAGE
	                              ),
	                              targetTransformMapping, COMPOSITION_AVERAGE
	                          );
	    }
//	    end = System.currentTimeMillis();
//	    System.out.println("         Compose components: " + (end-start));

	    //Set corresponding info
	    if (transformedResult!=null) {
	      transformedResult.setMatchInfo(matchResult.getMatchInfo());
	      transformedResult.setName(matchResult.getName());
	      transformedResult.setMatchInfo(matchResult.getMatchInfo());
//	      transformedResult.setEvidence(matchResult.getEvidence());
	      transformedResult.setUserObject(matchResult.getUserObject());
	    }
	    return transformedResult;
	  }
	
	
		
		public static int getMatchResultKind(MatchResult result){
			if (result==null){
				return UNDEF;
			}
			if (result instanceof MatchResultArray){
				return ARRAY;
			}
			System.out.println("Combination.getMatchResultKind contains not valid ");
			return UNDEF; 	
		}
		
		/**
		 * create a new match result which contains the correspondences of the given match result
		 * but limited to the given objects
		 * @param result
		 * @param srcObjects source objects
		 * @param trgObjects target objects
		 */
		public static MatchResult restrict(MatchResult result, ArrayList<Object> srcObjects, ArrayList<Object> trgObjects){
			if (result instanceof MatchResultArray){
				return MatchResultArray.restrict((MatchResultArray)result, srcObjects, trgObjects);
			}
			return null;
		}
		
		public void print(){
			System.out.println(toString());
		}
		
		public String toString() {
			String info=name;
			info+=" ["+srcObjects.size()+","+trgObjects.size()+"] ";
			if (sourceGraph!=null)
				info+=sourceGraph.getSource().getName();
			if (targetGraph!=null)
				info+=targetGraph.getSource().getName();
			
			
			StringBuffer sb = new StringBuffer();
			sb.append("--------------------------------------------------------\n");
		    int matchCnt = 0;
		    for (int i=0; i<srcObjects.size(); i++) {
		      Object srcObject = srcObjects.get(i);
		      for (int j=0; j<trgObjects.size(); j++) {
		        Object trgObject = trgObjects.get(j);
		        float sim = getSimilarity(srcObject, trgObject);			        
		        if (sim>0) {
		          matchCnt ++;
		          sb.append(" - ");
		          if (srcObject instanceof Path && trgObject instanceof Path)
		            sb.append(((Path)srcObject).toNameString()).append(" <-> ").append(((Path)trgObject).toNameString());
		          else if (srcObject instanceof Element && trgObject instanceof Element)
		            sb.append(srcObject.toString()).append(" <-> ").append(trgObject.toString());
		          else
		            sb.append(srcObject).append(" <-> ").append(trgObject);
		          sb.append(": ").append(sim).append("\n");
		        }
		      }
		    }
		    sb.append(" + Total: ").append(matchCnt).append(" correspondences\n");
		    sb.append("--------------------------------------------------------\n");
		    info+= sb.toString();
			
			
			return info;
		}
		
		 public boolean containsOnlyNodes() {
			 for (Object current : srcObjects) {
				 if (!(current instanceof Element)){
					return false;
				 }
			 }	
			 for (Object current : trgObjects) {
				 if (!(current instanceof Element)){
					return false;
				 }
			 }				
			 return true;
		 }
		  
		  //---------------------------------------------------------------------------//
		  //To connectedGraph                                                           //
		  //---------------------------------------------------------------------------//
		  public Graph toConnectedGraph() {
			  Graph simGraph = new Graph();
		    for (int i=0; i<srcObjects.size(); i++) {
		      Object srcObject = srcObjects.get(i);
		      Element aVertex = null;
		      if (srcObject instanceof Element){
		    	  aVertex = (Element)srcObject;
		      } else {
		        System.err.println("MatchResult.toConnectedGraph Error: not expected");
		        continue;
		      }
		      for (int j=0; j<trgObjects.size(); j++) {
		        Object trgObject = trgObjects.get(j);
		        Element bVertex = null;
		        if (trgObject instanceof Element){
		        	bVertex = (Element)trgObject;
		        } else {
		        	System.err.println("MatchResult.toConnectedGraph Error: not expected");
		        	continue;
		        }
		        float sim = getSimilarity(srcObject, trgObject);
		        if (sim>0) {
		          try {
		              simGraph.addVertex(aVertex);
		              simGraph.addVertex(bVertex);
//		              java.util.List adjacents = simGraph.getAdjacentVertices(aVertex);
		              List<Element> adjacents = Graphs.neighborListOf(simGraph, aVertex);		              
		              
		              if (adjacents==null || !adjacents.contains(bVertex)){
		            	simGraph.addEdge(aVertex, bVertex);
		              }
		          }
		          catch (Exception e) { System.err.println("MatchResult.toConnectedGraph Error building weightedGraph"); }
		        }
		      }
		    }
		    return simGraph;
		  }
		  
	/**
	 * @return number of correspondences of all sim/ sim=1
	 */
	public int[] getMatchCountAll() {
		int matchCnt = 0;
		int matchCnt1 = 0;
		for (int i = 0; i < srcObjects.size(); i++) {
			Object srcObj = srcObjects.get(i);
			for (int j = 0; j < trgObjects.size(); j++) {
				Object trgObj = trgObjects.get(j);
				float sim = getSimilarity(srcObj, trgObj);
				if (sim > 0) {
					matchCnt++;
					if (sim == 1) {
						matchCnt1++;
					}
				}
			}
		}
		int[] matchC = new int[2];
		matchC[0] = matchCnt;
		matchC[1] = matchCnt1;
		return matchC;
	}
	
		  public String getStatInfo() {
//			   - SchemaMapping Statistics ( all src/trg Elements, # src/trg Elements with Korresp., # all Korresp,  # 1:1 Korrespondenzen (each element just has one correspondence))
			  StringBuffer buffer = new StringBuffer();
			  buffer.append("Mapping: " + this.getName());
			  buffer.append("\n\nSource: " + this.getSourceGraph().getSource().getName() 
					  +"\tnodes: " + this.getSourceGraph().getElementCount() + "\tpaths: " + this.getSourceGraph().getAllPaths().size());
			  buffer.append("\nTarget:\t" + this.getTargetGraph().getSource().getName()
					  +"\tnodes: " + this.getTargetGraph().getElementCount() + "\tpaths: " + this.getTargetGraph().getAllPaths().size());	 
			  int[] matchC = getMatchCountAll();
			  buffer.append("\n\nnumber of correspondences (count as 1:1): \t" + matchC[0]);
			  buffer.append("\n\t\twith similarity value 1.0: \t\t" + matchC[1]);
//			  buffer.append("\nnumber of correspondences (allowing n:m):\t" + getMultipleMatches().size());
			  buffer.append("\nsource objects having a correspondences:\t\t" + getSrcMatchObjectsCount());
			  buffer.append("\ntarget objects having a correspondences:\t\t" + getTrgMatchObjectsCount());

			  return buffer.toString();
		  }
		  
		  
	public static MatchResult[] trim(MatchResult[] results){
		int size = results.length;
		int sizeNull = 0;
		for (int i = 0; i < results.length; i++) {
			if (results[i]==null){
				sizeNull++;
			}
		}
		if (sizeNull>0){
			MatchResult[] newResults = new MatchResult[size-sizeNull];
			int current = 0;
			for (int i = 0; i < results.length; i++) {
				if (results[i]!=null){
					newResults[current] = results[i];
					current++;
				}
			}
			return newResults;
		}
		return results;
	}
	
	public MatchResult trim(){
		if (this instanceof MatchResultArray){
			return ((MatchResultArray)this).trim();			
		}
		return this;
	}
	

	public void divideBy(int divide){
		if (divide<=0){
			System.err.println("MatchResultDB.divideBy divide not allowed (should be > 0) is " + divide);
			return;
		}
		if (this instanceof MatchResultArray){
			// optimized for array
			((MatchResultArray)this).divideBy(divide);
		} else {
			ArrayList<Object> srcMatchObjects = getSrcMatchObjects();
			for (Object srcObject : srcMatchObjects) {
				ArrayList<Object> trgObjects = getTrgMatchObjects(srcObject);
				for (Object trgObject : trgObjects) {
					float sim = getSimilarity(srcObject, trgObject);
					sim = sim/divide;
					setSimilarity(srcObject, trgObject, sim);
				}
			}
		}
	}
	
	
	
    public static String renderMatchResultAsRDFAlignment(MatchResult result) {
        //String PART1 = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\r\n\r\n" //encoding='utf-8' ?>\r\n\r\n");
        String PART1 = "<?xml version=\"1.0\" encoding='utf-8' ?>\r\n\r\n"
                + "<rdf:RDF xmlns=\"http://knowledgeweb.semanticweb.org/heterogeneity/alignment\" "
                + "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\r\n\r\n"
                + "<Alignment>\r\n";
        String PART2 = "<xml>yes</xml>\r\n"
                + "<level>0</level>\r\n"
                + "<type>11</type>\r\n";

        String PART3 = "</map>\r\n"
                + "</Alignment>\r\n"
                + "</rdf:RDF>\r\n";


        if (result == null || result.getMatchCount()==0) {
            return null;
        }
        String nameStr = null, infoStr = null;
        String sourceStr = null, targetStr = null;
        String matcherStr = null, configStr = null;
//		String sourceProvider = null, targetProvider = null;
//		SchemaGraph sourceGraph = getSourceGraph();
//		SchemaGraph targetGraph = getTargetGraph();
        String resultName = result.getName();
        String matchInfo = result.getMatchInfo();
        ArrayList aObjects = result.getSrcMatchObjects();
        ArrayList bObjects = result.getTrgMatchObjects();
        //Name
        nameStr = "" + resultName;
        //Match info
        infoStr = "" + matchInfo;
        StringBuilder sb = new StringBuilder();
        //TODO: Zeichensatz aus Systemumgebung holen??
        sb.append(PART1);
        sb.append("\r\n\r\n<!--");
        sb.append("MatchResult of simMatrix [" + aObjects.size() + ","
                + bObjects.size() + "]");
        sb.append(" + Name: " + nameStr);
        sb.append(" + Info: " + infoStr);
        sb.append(" + Source: " + sourceStr);
        sb.append(" + Target: " + targetStr);
        sb.append(" + Matcher: " + matcherStr);
        sb.append(" + Config: " + configStr);
        sb.append("-->\r\n\r\n\r\n");
        sb.append(PART2); // ?
        String onto1 = result.getSourceGraph().getSource().getProvider();
        if (onto1 != null && onto1.endsWith("#")) {
            onto1 = onto1.substring(0, onto1.length() - 1);
        }
        String onto2 = result.getTargetGraph().getSource().getProvider();
        if (onto2 != null && onto2.endsWith("#")) {
            onto2 = onto2.substring(0, onto2.length() - 1);
        }
        sb.append("<onto1>" + onto1 + "</onto1>\r\n");
        sb.append("<onto2>" + onto2 + "</onto2>\r\n");
        String url1 = result.getSourceGraph().getSource().getUrl();
        if (url1 != null && url1.endsWith("#")) {
            url1 = url1.substring(0, url1.length() - 1);
        }
        String url2 = result.getTargetGraph().getSource().getUrl();
        if (url2 != null && url2.endsWith("#")) {
            url2 = url2.substring(0, url2.length() - 1);
        }
        sb.append("<uri1>" + url1 + "</uri1>\r\n");
        sb.append("<uri2>" + url2 + "</uri2>\r\n");

        sb.append("<map>\r\n");
        int matchCnt = 0;
        for (int i = 0; i < aObjects.size(); i++) {
            Object objA = aObjects.get(i);
            for (int j = 0; j < bObjects.size(); j++) {
                Object objB = bObjects.get(j);
                float sim = result.getSimilarity(objA, objB);
                if (sim > 0) {
                    matchCnt++;

                    String acc1=null, acc2=null;
                    if (objA instanceof de.wdilab.coma.structure.Path) {
                    	acc1 = ((de.wdilab.coma.structure.Path) objA).toNameString();
                    } else if (objA instanceof Element) {
                    	acc1 = ((Element) objA).getAccession();
                    }
                    if (objB instanceof de.wdilab.coma.structure.Path) {
                    	acc2 = ((de.wdilab.coma.structure.Path) objB).toNameString();
                    } else if (objA instanceof Element) {
                    	acc2 = ((Element) objB).getAccession();
                    }
                    if (acc1!=null && acc2!=null 
//                    		&& !acc1.contains("/")&& !acc2.contains("/")
                    		) {

//	                	if (OAEIConstants.DIRECTORY_FULL){ // only true for directory 10/full
//                        acc1 = acc1.replaceAll("&", "%26").replaceAll("\'", "%27").replaceAll(",", "%2C");
//                        acc1 = acc1.replaceAll(":", "%3A").replaceAll("#", "%23");
//                        while (acc1.indexOf('(') > 0 || acc1.indexOf(')') > 0) {
//                            acc1 = acc1.replace("(", "%28").replace(")", "%29");
//                        }
//                        acc2 = acc2.replaceAll("&", "%26").replaceAll("\'", "%27").replaceAll(",", "%2C").replaceAll(":", "%3A").replaceAll("#", "%23");
//                        while (acc2.indexOf('(') > 0 || acc2.indexOf(')') > 0) {
//                            acc2 = acc2.replace("(", "%28").replace(")", "%29");
//                        }
//	                	}
//		                String namespace1= aVertex.getNamespace();
//		                if (namespace1.equals(url1)){
//		                	namespace1=onto1;
//		                }
//		                String namespace2= bVertex.getNamespace();
//		                if (namespace2.equals(url2)){
//		                	namespace2=onto1;
//		                }
                        sb.append("<Cell>\r\n"
                                + "<entity1 rdf:resource=\""
                                //									+ sourceProvider
                                //									+ "#"
                                //									+ ((Element) aVertex.getObject())
                                //											.getTextRep()
                                + acc1
                                //.getComment()
                                //.getTypespace()
                                + "\"/>\r\n"
                                + "<entity2 rdf:resource=\""
                                //									+ targetProvider
                                //									+ "#"
                                //									+ ((Element) bVertex.getObject())
                                //											.getTextRep()

                                + acc2
                                //.getComment()
                                //.getTypespace()
                                + "\"/>\r\n"
                                + "<measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">"
                                + sim + "</measure>\r\n"
                                + "<relation>"
                                + "="
                                + "</relation>\r\n" + "</Cell>\r\n");
                    }
                }
            }
        }

        sb.append(PART3);
        return sb.toString();
    }    
	
		  
	// abstract functions
	/**
	 * @return number of source objects having a correspondence 
	 */
	public abstract ArrayList<Object> getSrcMatchObjects();
	
	
	/**
	 * @param trgObject  target object
	 * @return number of source objects corresponding with the target object
	 * returns null if no corresponding objects
	 */
	public abstract ArrayList<Object> getSrcMatchObjects(Object trgObject);
	
	/**
	 * @return number of target objects having a correspondence 
	 */
	public abstract ArrayList<Object> getTrgMatchObjects();
	
	/**
	 * @param srcObj  source object
	 * @return number of target objects corresponding with the source object
	 * returns null if no corresponding objects
	 */
	public abstract ArrayList<Object> getTrgMatchObjects(Object srcObject);
	
	/**
	 * @return number of correspondences (1:1)
	 * returns 0 if no correspondences exist
	 */
	public abstract int getMatchCount();

	/**
	 * @param srcObject source object
	 * @param trgObject target object
	 * @return similarity of the correspondence between both objects
	 * similarity value is between 0 and 1 (0 indicate not similar, 1 indicates equal)
	 * return value 0 (SIM_UNDEF) means maybe undefined similarity
	 */
	public abstract float getSimilarity(Object srcObject, Object trgObject);
	
	/**
	 * @param srcObject source object
	 * @param trgObject target object
	 * remove (if existing) similarity of the correspondence between both objects
	 */
	public abstract void remove(Object srcObject, Object trgObject);
	
	/**
	 * append the correspondence between two objects with its similarity to this match result
	 * @param srcObject source object
	 * @param trgObject target object
	 * @param sim similarity value
	 */
	public abstract void append(Object srcObject, Object trgObject, float sim);
	
	/**
	 * set the correspondence similarity between two objects to the given one
	 * @param srcObject source object
	 * @param trgObject target object
	 * @param sim similarity value
	 */
	public abstract void setSimilarity(Object srcObject, Object trgObject, float sim);
	

	/**
	 * clone the matchresult (object and similarity values)
	 * @see java.lang.Object#clone()
	 */
	public abstract MatchResult clone();

}
