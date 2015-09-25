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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import de.wdilab.coma.matching.Combination;
import de.wdilab.coma.matching.ComplexMatcher;
import de.wdilab.coma.matching.Matcher;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Selection;
import de.wdilab.coma.matching.SimilarityMeasure;
import de.wdilab.coma.matching.Strategy;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;
import de.wdilab.coma.structure.Path;
import de.wdilab.coma.structure.Statistics;
import de.wdilab.ml.impl.ObjectInstance;
import de.wdilab.ml.impl.mapping.mainmemory.ArrayListMainMemoryMapping;
import de.wdilab.ml.impl.oi.preprocessing.OipWordReplacer;
import de.wdilab.ml.impl.oi.set.StoreableMainMemoryObjectInstanceProvider;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;
import de.wdilab.ml.interfaces.oi.ObjectInstanceProviderException;

/**
 * This class organizes the execution of the given workflow for the
 * given source and target graphs. It goes "down" to each Strategy 
 * and then to each ComplexMatcher and Matcher and determines the 
 * current information to be used. The match result is iterated upwards
 * and there the needed combination and / or selection applied.
 * 
 * @author Sabine Massmann
 */
public class ExecWorkflow {
	static boolean verbose = false;
	static boolean printSteps = false;
	
	static String WORD_ATTRNAME = "wordAttrName";
	static String REPL_ATTRNAME = "replAttrName";
	
	java.sql.Connection connection = null;
	
	HashMap<String, SimilarityCache> resultCaches = new HashMap<String, SimilarityCache>();
	
	HashMap<String, MatchResult> resultCS = new HashMap<String, MatchResult>();
	
	boolean useSynAbb = false;
	
	ArrayList<String> abbrevList = null, fullFormList = null;
	ArrayList<String> wordList = null, synonymList = null;
	
	
	public ExecWorkflow(){
	}
	
	
	public ExecWorkflow(DataAccess accessor ){
		if (accessor!=null){
			this.connection = accessor.getConnection();
			abbrevList = new ArrayList<String>();
			fullFormList = new ArrayList<String>();
			wordList = new ArrayList<String>();
			synonymList = new ArrayList<String>();
	
		    // load abbreviation (one list the abbreviation and the other the "long" version)
		    accessor.loadList(Repository.SRC_ABBREV, abbrevList, fullFormList);	
		    accessor.loadList(Repository.SRC_SYNONYM, wordList, synonymList);
		    if (abbrevList.isEmpty()){
		    	abbrevList=null;
		    	fullFormList=null;
		    }
		    if (wordList.isEmpty()){
		    	wordList=null;
		    	synonymList=null;
		    }
		}
	}

	public ExecWorkflow(ArrayList<String> abbrevList, ArrayList<String> fullFormList, 
			ArrayList<String> wordList, ArrayList<String> synonymList){
		this.abbrevList = abbrevList;
		this.fullFormList = fullFormList;
		this.wordList = wordList;
		this.synonymList = synonymList;
	}
	
	public MatchResult[] execute(Workflow workflow){
		if (workflow==null){
			return null;
		}
		MatchResult selected = workflow.getSelected();
		if (selected!=null){
			workflow.setSelected(null);
			MatchResult combined = null; 
			ArrayList<Object> srcObjects = selected.getSrcMatchObjects();
			for (Object srcObject : srcObjects) {
				ArrayList<Object> srcObjectsSingle = new ArrayList<Object>();
				srcObjectsSingle.add(srcObject);
				workflow.setSourceSelected(srcObjectsSingle);
				ArrayList<Object> trgObjects = selected.getTrgMatchObjects(srcObject);
				workflow.setTargetSelected(trgObjects);
				MatchResult[] resultTmp = executeWorkflow(workflow);
				if (resultTmp==null) continue;
				if (resultTmp[0]==null) continue;
				if (combined==null){
					combined = resultTmp[0];
					if (resultTmp.length>1){
						for (int i = 1; i < resultTmp.length; i++) {
							combined = MatchResult.merge(combined, resultTmp[i]);
						}
					}
				} else {
					for (int i = 0; i < resultTmp.length; i++) {
						combined = MatchResult.merge(combined, resultTmp[i]);
					}
				}
			}
			MatchResult[] results = { combined };
			return results;			
		}
		useSynAbb = workflow.useSynAbb();
		return executeWorkflow(workflow);
	}
		
	public void loadSynAbb(DataAccess accessor){
		if (accessor!=null){
			this.connection = accessor.getConnection();
			abbrevList = new ArrayList<String>();
			fullFormList = new ArrayList<String>();
			wordList = new ArrayList<String>();
			synonymList = new ArrayList<String>();
	
		    // load abbreviation (one list the abbreviation and the other the "long" version)
		    accessor.loadList(Repository.SRC_ABBREV, abbrevList, fullFormList);	
		    accessor.loadList(Repository.SRC_SYNONYM, wordList, synonymList);
		    if (abbrevList.isEmpty()){
		    	abbrevList=null;
		    	fullFormList=null;
		    }
		    if (wordList.isEmpty()){
		    	wordList=null;
		    	synonymList=null;
		    }
		}
	}
	
	public void removeSynAbb(){
    	abbrevList=null;
    	fullFormList=null;
    	wordList=null;
    	synonymList=null;
	}
		
	/**
	 * @param workflow
	 * execute the workflow (has to be valid) and produce one or more 
	 * match results
	 */
	MatchResult[] executeWorkflow(Workflow workflow){
		if (workflow==null){
			return null;
		}
		if (printSteps){
			System.out.println("Execute workflow " + workflow.getName());
		}
//		if (!workflow.isExecutable()){
//			System.out.println("ExecWorkflow.execute Error: workflow not valid");
//			System.out.println(workflow.toString());
//			return;
//		}		
		Graph srcGraph = workflow.getSource();
		Graph trgGraph = workflow.getTarget();
		Strategy[] begins = workflow.getBegins();
        if( begins == null) {
            System.out.println( "CAUTION! NO STRATEGY FOUND.");
        }
		Strategy secondStrategy = workflow.getSecondStrategy();
		MatchResult result = null;
		if (secondStrategy==null){
			// if source/target selected
			ArrayList srcSelected = workflow.getSourceSelected();
			ArrayList trgSelected = workflow.getTargetSelected();
			
			if (begins.length==1){
				// workflow kind 1: Strategy
				MatchResult selMR = createMRToUseSelected(begins[0], srcGraph, trgGraph,srcSelected, trgSelected);
				if (selMR==null){
					result = executePath(srcGraph, trgGraph, begins[0], null, null, null);
				} else {
					result = executeStrategy(srcGraph, trgGraph, begins[0], selMR);
				}
			}else {
				// workflow kind 3: (Strategy)*; Combination
				MatchResult[] resultTmp = new MatchResult[begins.length];
				// each "begin" stands for one path incl. branches in the workflow
				// processing can be separated and thus e.g. parallelized 
				for (int i = 0; i < begins.length; i++) {					
					MatchResult selMR = createMRToUseSelected(begins[i], srcGraph, trgGraph,srcSelected, trgSelected);
					if (selMR==null){
						resultTmp[i] = executePath(srcGraph, trgGraph, begins[i], null, null, null);
					} else {
						result = executeStrategy(srcGraph, trgGraph, begins[i], selMR);
					}
				}				
				Combination combination = workflow.getCombination();
				if (combination!=null){
					result = combination.combine(resultTmp);
				} else {
					// if there is no combination there are several results returned
					MatchResult[] results = resultTmp;
					for (int i = 0; i < results.length; i++) {
						results[i].setSourceGraph(srcGraph);
						results[i].setTargetGraph(trgGraph);
						results[i].setName("MatchResult");
					}
					return results;
				}
			}
		} else {
			ArrayList srcSelected = workflow.getSourceSelected();
			ArrayList trgSelected = workflow.getTargetSelected();
			// workflow kind 2: Strategy; Strategy
			result = executePath(srcGraph, trgGraph,  begins[0], secondStrategy, srcSelected, trgSelected);
		}
//	       System.out.println("Total Memory"+Runtime.getRuntime().totalMemory());    
//	       System.out.println("Free Memory"+Runtime.getRuntime().freeMemory());
		System.gc();
//	       System.out.println("Total Memory"+Runtime.getRuntime().totalMemory());    
//	       System.out.println("Free Memory"+Runtime.getRuntime().freeMemory());
		if (result==null) return null;
		result.setSourceGraph(srcGraph);
		result.setTargetGraph(trgGraph);
		result.setName("MatchResult");
		MatchResult[] results = { result };
		return results;
	}
	
	static MatchResult createMRToUseSelected(Strategy strategy,Graph srcGraph, Graph trgGraph, ArrayList srcSelected, ArrayList trgSelected){
		if (strategy==null || srcGraph==null || trgGraph==null || srcSelected==null || trgSelected==null) return null;
		Resolution resolution1 = strategy.getResolution();
		boolean path = true;
		for (Object object : srcSelected) {
			if (object instanceof Element){
				path=false;
				break;
			}
		}
		ArrayList<Object> srcObjects = resolution1.getResolution1(srcGraph);
		ArrayList<Object> trgObjects = resolution1.getResolution1(trgGraph);	
		if (Resolution.RES1_NODETYPE_LIST.contains(resolution1.getId())){
			// restrict to nodes
			if (path){
				System.err.println("ExecWorkflow.createMRToUseSelected() this selection is currently not supported");
			} else {
				// nodes selected in view and nodes to be matched -> just allow the selected nodes
				srcObjects.retainAll(srcSelected);
				trgObjects.retainAll(trgSelected);
			}
		} else if (Resolution.RES1_PATHTYPE_LIST.contains(resolution1.getId())){
			// restrict to paths
			if (path){
				ArrayList<Object> tmpSrcObjects = new ArrayList<Object>();
				ArrayList<Object> tmpTrgObjects = new ArrayList<Object>(); 
				// all allowed paths must start with one of the selected paths
				for (Object srcObject : srcObjects) {
					if (srcObject instanceof Path){
						for (Object sel : srcSelected) {
							if (srcObject instanceof Path){
								if (((Path)srcObject).containsAll((Path)sel)){
									tmpSrcObjects.add(srcObject);
									break;
								}
							}
						}
					}
				}
				for (Object trgObject : trgObjects) {
					if (trgObject instanceof Path){
						for (Object sel : trgSelected) {
							if (trgObject instanceof Path){
								if (((Path)trgObject).containsAll((Path)sel)){
									tmpTrgObjects.add(trgObject);
									break;
								}
							}
						}
					}
				}
				srcObjects = tmpSrcObjects;
				trgObjects = tmpTrgObjects;
			} else {
				System.err.println("ExecWorkflow.createMRToUseSelected() this selection is currently not supported");
			}
		}	
		if (srcObjects.isEmpty() || trgObjects.isEmpty()){
			System.err.println("ExecWorkflow.createMRToUseSelected() this selection didn't work -> so not used");
			return null;
		}
		// to ensure that later all pairs are possible
		float[][] fakeSim = new float[srcObjects.size()][trgObjects.size()];
		for (int i = 0; i < fakeSim.length; i++) {
			for (int j = 0; j < fakeSim[0].length; j++) {
				fakeSim[i][j]=1;
			}
		}
		MatchResult result = new MatchResultArray(srcObjects, trgObjects, fakeSim);
		
		return result;
	}
	
	
	/**
	 * @param srcGraph
	 * @param trgGraph
	 * @param workflow
	 * @param begin
	 * execute workflow of kind 1 (Strategy) or kind 2 (Strategy; Strategy)
	 */
	MatchResult executePath(Graph srcGraph, Graph trgGraph, Strategy begin, Strategy begin2, ArrayList srcSelected, ArrayList trgSelected){
		if (printSteps){
			System.out.println("Execute path ");
			if (begin!=null) System.out.println(begin.getName() + " ");
			if (begin2!=null) System.out.println(begin2.getName());
		}
		MatchResult selMR = null;
		if (srcSelected!=null && trgSelected!=null){
			selMR = createMRToUseSelected(begin, srcGraph, trgGraph, srcSelected, trgSelected);
		}
		MatchResult result = executeStrategy(srcGraph, trgGraph, begin, selMR);
		if (result==null){
			return null;
		}
		boolean print = false;
		
		if (begin2!=null){
		if (print) result.print();
			
			MatchResult combined = null; 
			ArrayList<Object> srcObjects = result.getSrcMatchObjects();
			for (Object srcObject : srcObjects) {
				ArrayList<Object> srcObjectsSingle = new ArrayList<Object>();
				srcObjectsSingle.add(srcObject);
				ArrayList<Object> trgObjects = result.getTrgMatchObjects(srcObject);
				MatchResult selTmp= new MatchResultArray(srcObjectsSingle, trgObjects, 1);
				if (print) selTmp.print();
				// workflow kind 2: Strategy; Strategy
				MatchResult resultTmp = executeStrategy(srcGraph, trgGraph, begin2, selTmp);
				if (resultTmp==null) continue;
				if (begin2.getResolution().getId()==Resolution.RES1_DOWNPATHS){
					Resolution r = new Resolution(Resolution.RES1_UPPATHS);
					ArrayList<Object> srcMatchObjects = resultTmp.getSrcMatchObjects();					
					ArrayList<Object> trgMatchObjects = resultTmp.getTrgMatchObjects();
					MatchResult resultTmpPaths = new MatchResultArray();
					//change local paths to global paths (thus include uppaths)
					for (Object srcMatch : srcMatchObjects) {					
						ArrayList<Object> srcUpPaths = r.getResolution2(srcGraph, srcMatch);
						for (Object srcUpPath : srcUpPaths) {
							if (!((Path)srcUpPath).containsAll(srcObject)) continue;
							for (Object trgMatch : trgMatchObjects) {
								float sim=resultTmp.getSimilarity(srcMatch, trgMatch);
								if (sim>0){
									ArrayList<Object> trgUpPaths = r.getResolution2(trgGraph, trgMatch);
									for (Object trgUpPath : trgUpPaths) {
										for (Object trgMatchObject : trgMatchObjects) {
											if (((Path)trgUpPath).containsAll(trgMatchObject)){
												// new Match
//												boolean src= srcGraph.getAllPaths().contains(srcUpPath);
//												boolean trg= trgGraph.getAllPaths().contains(trgUpPath);												
												resultTmpPaths.append(srcUpPath, trgUpPath, sim);
											}
										}
									}
								}
							}
						}
					}
					resultTmp = resultTmpPaths;					
				}
				if (combined==null){
					combined = resultTmp;
				} else {
					combined = MatchResult.merge(combined, resultTmp);
				}
			}
			if (begin2.getSelection()!=null){
				combined = begin2.getSelection().select(combined);
			}
			return combined;
			
//			// workflow kind 2: Strategy; Strategy
//			result = executeStrategy(srcGraph, trgGraph, begin2, result);
		}
		return result;
	}
	
	
	/**
	 * @param srcGraph
	 * @param trgGraph
	 * @param strategy
	 * @return MatchResult
	 * execute the given complex strategy on the given input graphs
	 */
	MatchResult executeStrategy(Graph srcGraph, Graph trgGraph, 
			Strategy strategy, MatchResult previousResult){
		if (printSteps){
			System.out.println("Execute complex strategy " + strategy.getName());
		}
		// maybe replace resolution1 with the actual information
		Resolution resolution1 = strategy.getResolution();
		if (srcGraph==null || trgGraph==null){
			return null;
		}
		ArrayList<Object> srcObjects=null;
		ArrayList<Object> trgObjects=null;
		if (previousResult==null){
			// depending on resolution 1 a list of paths or nodes
			srcObjects = resolution1.getResolution1(srcGraph);
			trgObjects = resolution1.getResolution1(trgGraph);		
		} else {
			// execute second Strategy of the workflow kind 2 (Strategy; Strategy)
			srcObjects = previousResult.getSrcMatchObjects();
			trgObjects = previousResult.getTrgMatchObjects();
			if (srcObjects==null || trgObjects==null){
				System.err.println("ExecWorkflow.executeComplexStrateg() not matches in previous match result");
				return null;
			}
			if (resolution1.getId()==Resolution.RES1_UPPATHS || resolution1.getId()==Resolution.RES1_DOWNPATHS){
				// treated like resolution2 -> only res1 used in getResolution2(Graph, Element/Path)
				ArrayList<ArrayList<Object>> srcObjectsTmp = resolution1.getResolution2(srcGraph, srcObjects);
				ArrayList<ArrayList<Object>> trgObjectsTmp = resolution1.getResolution2(trgGraph, trgObjects);	
				srcObjects = Resolution.getSingleObjects(srcObjectsTmp);
				trgObjects = Resolution.getSingleObjects(trgObjectsTmp);
			} else {
				ArrayList<Object> srcObjectsTmp = resolution1.getResolution1(srcGraph);
				ArrayList<Object> trgObjectsTmp = resolution1.getResolution1(trgGraph);
				srcObjectsTmp.retainAll(srcObjects);
				trgObjectsTmp.retainAll(trgObjects);
				srcObjects = srcObjectsTmp;
				trgObjects = trgObjectsTmp;
			}
		}
		ComplexMatcher[] match_strat = strategy.getComplexMatcher();
		if (match_strat==null || match_strat.length==0){
			return null;
		}
		MatchResult[] results = new MatchResult[match_strat.length];
		for (int i = 0; i < match_strat.length; i++) {
				ComplexMatcher cm = match_strat[i];
				String idString =cm.toString(false);
				results[i] = resultCS.get(idString);
				if (results[i]==null || !results[i].getSourceGraph().equals(srcGraph)|| !results[i].getTargetGraph().equals(trgGraph)){
					results[i] = executeComplexMatcher( srcGraph, trgGraph, cm, srcObjects, trgObjects);
					resultCS.put(idString, results[i]);
				}
		}
		MatchResult result = null;
		Combination combination = strategy.getSimCombination();
		if (verbose) System.out.println(combination);
		if (results.length>1){
			// combine results if more than one results
			result = combination.combine(results);
		} else if (results.length==1) {
			result = results[0];
		}
		if (result!=null && previousResult!=null){
			// selection if previous result given
			// TODO optimization: don't compare them in the first place
			for (Iterator srcIterator = srcObjects.iterator(); srcIterator.hasNext();) {
				Object srcObject = srcIterator.next();
				for (Iterator trgIterator = trgObjects.iterator(); trgIterator.hasNext();) {
					Object trgObject = trgIterator.next();
					if (result.getSimilarity(srcObject, trgObject)<=0){
						result.remove(srcObject, trgObject);
					}
				}
			}
		}
		if (result==null){
			return null;
		}
		boolean print = false;
		if (print) result.print();
		Selection selection = strategy.getSelection();
		if (selection!=null){
			long start = System.currentTimeMillis();
			System.out.println("MatchCount: " + result.getMatchCount() );
			// select correspondences
			result = selection.select(result);
			if (verbose){
				long end = System.currentTimeMillis();
				System.out.println("MatchCount (after selection) : " + result.getMatchCount()
						+"\t" + (end-start)/1000 + " s" );
			}
		}
		if (print) result.print();
		if (verbose && result!=null) System.out.println("MatchCount : " + result.getMatchCount());
		return result;
	}
	
//	/**
//	 * @param srcGraph
//	 * @param trgGraph
//	 * @param resolution1
//	 * @param strategy
//	 * @return MatchResult
//	 * execute the given strategy with the resolution 1 on the given input graphs
//	 */
//	MatchResult executeStrategy(Resolution resolution1, Graph srcGraph, Graph trgGraph, Strategy strategy,
//			ArrayList<Object> srcObjects, ArrayList<Object> trgObjects){
//		if (printSteps){
//			System.out.println("Execute strategy " + strategy.getName());
//		}
//		Resolution resolution2 = strategy.getResolution();
//		
//		// depending on resolution 2 a single or list of paths or odes for each resolution 1
//	      ArrayList<ArrayList<Object>> srcCombinedObjects = resolution2.getResolution2(srcGraph, srcObjects);
//	      ArrayList<ArrayList<Object>> trgCombinedObjects = resolution2.getResolution2(trgGraph, trgObjects);		
//	      ArrayList<Object> srcSingleObjects = Resolution.getSingleObjects(srcCombinedObjects);
//	      ArrayList<Object> trgSingleObjects = Resolution.getSingleObjects(trgCombinedObjects);
//	      
//	      System.out.println(strategy.getName());
//	      System.out.println("Strategy\tsrc\t"+ srcSingleObjects.size() + "\ttrg\t"+ trgSingleObjects.size());
//	      srcSingleObjects = new ArrayList(new HashSet<Object>(srcSingleObjects));
//	      trgSingleObjects =  new ArrayList(new HashSet<Object>(trgSingleObjects));
//	      System.out.println("Strategy\tsrc\t"+ srcSingleObjects.size() + "\ttrg\t"+ trgSingleObjects.size());
//	      
////	      // only if coming from Paths to Nodes
////	      ArrayList<Object> aSubAtomObjects = Resolution.determineAtomicConstituents(aSubObjects);
////	      ArrayList<Object> bSubAtomObjects = Resolution.determineAtomicConstituents(bSubObjects);	      
//	      
//		ComplexMatcher[] cm = strategy.getComplexMatcher();
//		if (cm==null || cm.length==0){
//			return null;
//		}
//		
//		// For additional matching - part 1*start* 
//		// elements that don't have resolution 2 e.g. parents, children, siblings
//		 ArrayList<Object> srcObjects2 = new ArrayList<Object>();
//		 ArrayList<Object> trgObjects2 =  new ArrayList<Object>();
//		 ArrayList<Object> srcSingleObjects2 = new ArrayList<Object>();
//		 ArrayList<Object> trgSingleObjects2 =  new ArrayList<Object>();
//		boolean pathRes2 = Path.class.equals(Resolution.getResolutionOutputType(resolution2.getId()));
//		Resolution resolution2Tmp = null;
//		if (pathRes2){
//			// path out
//			resolution2Tmp = new Resolution(Resolution.RES2_SELFPATH);
//		} else {
//			// node out
//			resolution2Tmp = new Resolution(Resolution.RES2_SELFNODE);
//		}			
//		 
//		for (int i = 0; i < srcCombinedObjects.size(); i++) {
//			if (srcCombinedObjects.get(i)==null){				
//				Object srcObject = srcObjects.get(i);
//				srcObjects2.add(srcObject);
//				srcSingleObjects2.addAll(resolution2Tmp.getResolution2(srcGraph, srcObject));				
//			}
//		}
//		for (int i = 0; i < trgCombinedObjects.size(); i++) {
//			if (trgCombinedObjects.get(i)==null){
//				Object trgObject = trgObjects.get(i);	
//				trgObjects2.add(trgObject);
//				trgSingleObjects2.addAll(resolution2Tmp.getResolution2(trgGraph, trgObject));				
//			}
//		}
//		// For additional matching - part 1 *end*
//		
//		MatchResult[] results = new MatchResult[cm.length];
//		for (int i = 0; i < cm.length; i++) {
//			ComplexMatcher current = cm[i];
//			results[i] = executeComplexMatcher(resolution1, resolution2, srcGraph, trgGraph, current, srcSingleObjects, trgSingleObjects);
//		}
//		MatchResult result = null;
//		Combination combination = strategy.getSimCombination();
//		if (results.length>1){
//			// combine results if more than one results
//			result = combination.combine(results);
//		} else if (results.length==1) {
//			result = results[0];
//		}
//		boolean print = false;
//		if (print) result.print();
//		float start = System.currentTimeMillis();
//		if (print) result.print();
//		
//		// For additional matching - part 2 *start* 
//		if (!srcSingleObjects2.isEmpty() && !trgSingleObjects2.isEmpty()){
//			for (int i = 0; i < cm.length; i++) {	
//				ComplexMatcher current = cm[i];
//				results[i] = executeComplexMatcher(resolution1, resolution2, srcGraph, trgGraph, current, srcSingleObjects2, trgSingleObjects2);
//			}
//			MatchResult resultTmp = null;
//			if (results.length>1){
//				// combine results if more than one results
//				resultTmp = combination.combine(results);
//			} else if (results.length==1) {
//				resultTmp = results[0];
//			}
//			resultTmp = setCombination.setCombination(srcObjects2, trgObjects2, resolution2Tmp, resultTmp);
//			result = MatchResult.merge(result, resultTmp);
//			if (print) result.print();
//		}
//		// For additional matching - part 2 *end*
//		
//		
//		if (verbose){
//			long end = System.currentTimeMillis();
//			System.out.println(combination +  " " + setCombination +"\t" + (end-start)/1000 + " s" );
//			System.out.println("MatchCount : " + result.getMatchCount());
//		}
//		start = System.currentTimeMillis();
//		Selection selection = strategy.getSelection();
//		if (selection!=null){
//			// select correspondences
//			result = selection.select(result);
//			if (verbose){
//				long end = System.currentTimeMillis();
//				System.out.println("MatchCount (after selection) : " + result.getMatchCount()
//						+"\t" + (end-start)/1000 + " s" );
//			}
//		}
//		return result;
//	}
	
	/**
	 * @param srcGraph
	 * @param trgGraph
	 * @param resolution1
	 * @param resolution2
	 * @param comatcher
	 * @return MatchResult
	 * execute the given complex matcher with the resolution 1 and 2 on the given input graphs
	 */
	MatchResult executeComplexMatcher(Graph srcGraph, Graph trgGraph, ComplexMatcher comatcher, 
			ArrayList<Object> srcObjects, ArrayList<Object> trgObjects){
		if (printSteps){
			System.out.println("Execute complex matcher " + comatcher.getName());
		}
		// resolution 2 for the given resolution 1 or 2 input objects
		Resolution resolution2 = comatcher.getResolution();
		
		// depending on resolution 2 a single or list of paths or odes for each resolution 1
		ArrayList<ArrayList<Object>> srcCombinedObjects = resolution2.getResolution2(srcGraph, srcObjects);
		ArrayList<ArrayList<Object>> trgCombinedObjects = resolution2.getResolution2(trgGraph, trgObjects);		
		ArrayList<Object> srcSingleObjects = Resolution.getSingleObjects(srcCombinedObjects);
		ArrayList<Object> trgSingleObjects = Resolution.getSingleObjects(trgCombinedObjects);
				
		// ToDo: why false for PathMatching? 
		// answer: obj are Elements, combinedObj are Lists of Paths, singleObj are Paths
		boolean combinedOneValue = haveCombinedOneValue(srcCombinedObjects) &&  haveCombinedOneValue(trgCombinedObjects);
		
		if (!combinedOneValue){
			System.out.println(comatcher.getName());
		      System.out.println("CMatcher\tsrc\t"+ srcSingleObjects.size() + "\ttrg\t"+ trgSingleObjects.size());
		      srcSingleObjects = new ArrayList<Object>(new HashSet<Object>(srcSingleObjects));
		      trgSingleObjects =  new ArrayList<Object>(new HashSet<Object>(trgSingleObjects));
		      System.out.println("CMatcher\tsrc\t"+ srcSingleObjects.size() + "\ttrg\t"+ trgSingleObjects.size());
		}
		
		
		// For additional matching - part 1*start* 
		// elements that don't have resolution 2 e.g. parents, children, siblings
		 ArrayList<Object> srcObjects2 = new ArrayList<Object>();
		 ArrayList<Object> trgObjects2 =  new ArrayList<Object>();
		 ArrayList<Object> srcSingleObjects2 = new ArrayList<Object>();
		 ArrayList<Object> trgSingleObjects2 =  new ArrayList<Object>();
		boolean pathRes2 = Path.class.equals(Resolution.getResolutionOutputType(resolution2.getId()));
		Resolution resolution2Tmp = null;
		if (pathRes2){
			// path out
			resolution2Tmp = new Resolution(Resolution.RES2_SELFPATH);
		} else {
			// node out
			resolution2Tmp = new Resolution(Resolution.RES2_SELFNODE);
		}			
		 
		for (int i = 0; i < srcCombinedObjects.size(); i++) {
			if (srcCombinedObjects.get(i)==null){				
				Object srcObject = srcObjects.get(i);
				srcObjects2.add(srcObject);
				ArrayList<Object> res2 = resolution2Tmp.getResolution2(srcGraph, srcObject);
				if (res2!=null){
					srcSingleObjects2.addAll(res2);
				}
			}
		}
		for (int i = 0; i < trgCombinedObjects.size(); i++) {
			if (trgCombinedObjects.get(i)==null){
				Object trgObject = trgObjects.get(i);	
				trgObjects2.add(trgObject);
				ArrayList<Object> res2 = resolution2Tmp.getResolution2(trgGraph, trgObject);
				if (res2!=null){
					trgSingleObjects2.addAll(res2);				
				}
			}
		}
		// For additional matching - part 1 *end*
		System.out.println("element with constituents");
		Object[] c_cm = comatcher.getMatcherAndComplexMatcher();
		MatchResult[] results = new MatchResult[c_cm.length];
		for (int i = 0; i < c_cm.length; i++) {
			long start = System.currentTimeMillis();
			Object current = c_cm[i];
			// execute matcher		
			System.out.println("Size: src " + srcSingleObjects.size() + ", trg " +
					trgSingleObjects.size());
			
			if (current instanceof ComplexMatcher){
				results[i] = executeComplexMatcher(srcGraph, trgGraph, (ComplexMatcher)current, srcSingleObjects, trgSingleObjects);
			}else if (current instanceof Matcher){		
					MatchResult resultsTmp2 = executeMatcherMemory(srcGraph, trgGraph, (Matcher) current, srcSingleObjects, trgSingleObjects);
					results[i] = resultsTmp2;		

			} else {
				System.out.println("ExecWorkflow.executeComplexMatcher() not supported - no a complex matcher nor a matcher:\t" + current);
			}
			long end = System.currentTimeMillis();
			if (current instanceof ComplexMatcher){
				System.out.println("execute matcher "+((ComplexMatcher)current).getName()+"\t" + (float)(end-start)/1000 + " s");
			} else if (current instanceof Matcher){ 
				System.out.println("execute matcher "+((Matcher)current).getName()+"\t" + (float)(end-start)/1000 + " s");
			}
//			results[i].print();
		}
		Combination combination = comatcher.getSimCombination();
		MatchResult result = null;
		if (results.length>1){
			// combine results if more than one results
			result = combination.combine(results);
		} else if (results.length==1) {
			result = results[0];
		}
//		System.out.println("combinedOneValue: " + combinedOneValue);
		Combination setCombination = comatcher.getSetCombination();
		long start = System.currentTimeMillis();
				// MatchResultArray
		result = setCombination.setCombination(srcObjects, trgObjects, resolution2, result);
		
		// For additional matching - part 2 *start* 
		System.out.println("element w/o constituents");
		if (!srcSingleObjects2.isEmpty() && !trgSingleObjects2.isEmpty()){
			for (int i = 0; i < c_cm.length; i++) {
				Object current = c_cm[i];	
				if (current instanceof Matcher){
					results[i] = executeMatcherMemory(srcGraph, trgGraph, (Matcher) current, srcSingleObjects2, trgSingleObjects2);		
				} else if (current instanceof ComplexMatcher){
					results[i] = executeComplexMatcher(srcGraph, trgGraph, (ComplexMatcher) current, srcSingleObjects2, trgSingleObjects2);	
				}
			}
			MatchResult resultTmp = null;
			if (results.length>1){
				// combine results if more than one results
				resultTmp = combination.combine(results);
			} else if (results.length==1) {
				resultTmp = results[0];
			}
			resultTmp = setCombination.setCombination(srcObjects2, trgObjects2, resolution2Tmp, resultTmp);
			result = MatchResult.merge(result, resultTmp);
//			result.print();
		}
		// For additional matching - part 2 *end*
		
//		System.out.println("end to combining");

		if (verbose){
			System.out.println(resolution2+ " " + comatcher + " " + combination +  " " + setCombination);
			long end = System.currentTimeMillis();
			System.out.println("combination\t" + (end-start)/1000 + " s");
		}
//		if (verbose)
//		System.out.println(result.toString());
		if (verbose) System.out.println("MatchCount : " + result.getMatchCount());
//		System.out.println("Done (execute matcher).");
//		result.setSourceGraph(srcGraph);
//		result.setTargetGraph(trgGraph);
		return result;
	}
	
	private IObjectInstanceProvider createAndFillInstanceProvider(ArrayList<Object> objects){
		// put source objects into the source instance provider		
	    StoreableMainMemoryObjectInstanceProvider provider = new StoreableMainMemoryObjectInstanceProvider();
	    provider.open();
	    IObjectInstanceProvider  oip = provider;
	    int length = 0;
		for (int j=0; j<objects.size(); j++) {	
			Object constituent =  objects.get(j);
			if (constituent!=null) length+=constituent.toString().length();
//			ObjectInstance oi = getObjectInstance(resolutionLib, resolution, constituent, sourceAttribute, j);
			ObjectInstance oi = getObjectInstance(constituent, j);
			if (oi!=null){
				provider.add( oi);
			}
//			if (j==1000) break;
		}
		length=length/objects.size();
		System.out.println("***\tlength:\t" + length );
		return oip;
	}
	
	
	/**
	 * @param srcGraph
	 * @param trgGraph
	 * @param resolution1
	 * @param resolution2a
	 * @param resolution2b
	 * @param matcher
	 * @return MatchResult
	 * execute the given matcher with the resolutions on the given input graphs
	 */
	MatchResult executeMatcherMemory(Graph srcGraph, Graph trgGraph, 
			Matcher matcher, ArrayList<Object> srcObjects, ArrayList<Object> trgObjects){
		if (printSteps){
			System.out.println("Execute matcher (in memory) " + matcher.getName());
		}
		Resolution resolution3 = matcher.getResolution();
		// depending on resolution 2 a single or list of paths or nodes for each resolution 1
		ArrayList<ArrayList<Object>> srcCombinedObjects = resolution3.getResolution3(srcGraph, srcObjects);
		ArrayList<ArrayList<Object>> trgCombinedObjects = resolution3.getResolution3(trgGraph, trgObjects);		
		ArrayList<Object> srcSingleObjects = Resolution.getSingleObjects(srcCombinedObjects);
		ArrayList<Object> trgSingleObjects = Resolution.getSingleObjects(trgCombinedObjects);
		
		ArrayList<Object>  srcSingleSet = new ArrayList<Object>(new HashSet<Object>(srcSingleObjects));
		ArrayList<Object>  trgSingleSet =  new ArrayList<Object>(new HashSet<Object>(trgSingleObjects));
		
		boolean duplicates = ( srcSingleObjects.size()!=srcSingleSet.size() || trgSingleObjects.size()!=trgSingleSet.size());
		if (!duplicates){
			srcSingleSet = srcSingleObjects;
			trgSingleSet = trgSingleObjects;
		}
		
	      System.out.println("Matcher\tsrc\t"+ srcSingleObjects.size() + "\ttrg\t"+ trgSingleObjects.size());
	      System.out.println("Matcher\tsrc\t"+ srcSingleSet.size() + "\ttrg\t"+ trgSingleSet.size());

		
		boolean combinedOneValue = haveCombinedOneValue(srcCombinedObjects) &&  haveCombinedOneValue(trgCombinedObjects);
		
		// put source objects into the source instance provider		
		 IObjectInstanceProvider  oipA = createAndFillInstanceProvider(srcSingleSet);
		
		// put target objects into the target instance provider		
		 IObjectInstanceProvider  oipB = createAndFillInstanceProvider(trgSingleSet);
		
		 Class output = Resolution.getResolutionOutputType(resolution3.getId());
		 if (String.class.equals(output) 
				 && useSynAbb){

//			// ********************************************************************************************
//			// CSVWordReplacer
//			oipA = new CSVWordReplacer(oipA,
//	                "doc/input/LookupManufacturerCSV.csv",
//	                CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE, true,
//	                "manufacturer", "manufacturer_clean");
			
			
			// ********************************************************************************************
			// OipWordReplacer

		    try
		    {
		    	if (abbrevList!=null && !abbrevList.isEmpty()){
			    	IObjectInstanceProvider dict = createAndFillWordReplacer(abbrevList, fullFormList);
			    	oipA = new OipWordReplacer( oipA, "attr", "attr", dict, WORD_ATTRNAME, REPL_ATTRNAME).load();
			   		oipB = new OipWordReplacer( oipB, "attr", "attr", dict, WORD_ATTRNAME, REPL_ATTRNAME).load();
		    	}
		    } catch( final ObjectInstanceProviderException e)
		    { // nothing
		    }
			    

//		    try
//		    {
//			   	if (wordList!=null && !wordList.isEmpty()){
//			    	IObjectInstanceProvider dict = createAndFillWordReplacer(wordList, synonymList);
////			    	oipA = new OipWordReplacer( oipA, "attr", "attr", dict, WORD_ATTRNAME, REPL_ATTRNAME).load();
////			   		oipB = new OipWordReplacer( oipB, "attr", "attr", dict, WORD_ATTRNAME, REPL_ATTRNAME).load();
//			    	oipA = new OipPhraseReplacer( oipA, "attr", "attr", dict, WORD_ATTRNAME, REPL_ATTRNAME).load();
//			   		oipB = new OipPhraseReplacer( oipB, "attr", "attr", dict, WORD_ATTRNAME, REPL_ATTRNAME).load();
//		    	}
//		    } catch( final ObjectInstanceProviderException e)
//		    { // nothing
//		    }
			 
		 }		 
		 
		SimilarityMeasure[] simmeas = matcher.getSimMeasures();
		MatchResult[] results = new MatchResult[simmeas.length];
		for (int i = 0; i < simmeas.length; i++) {
			
			// without using cache
			// execute similarity measure
			results[i] = executeMemory(srcGraph, trgGraph, simmeas[i], srcSingleSet, trgSingleSet, oipA, oipB);
//			results[i].print();
			
			
				// using cache
//				String measName = simmeas[i].getName();
//				MatchResultArray resultTmp = cacheGetFull(measName, srcSingleObjects, trgSingleObjects);
//				if (resultTmp!=null){
//					resultTmp.setSourceGraph(srcGraph);
//					resultTmp.setTargetGraph(trgGraph);
//					ArrayList<Object> srcCovered = resultTmp.getSrcObjects();
//					ArrayList<Object> trgCovered = resultTmp.getTrgObjects();
//					if (srcCovered.equals(srcSingleObjects)){
//						if (trgCovered.equals(trgSingleObjects)){
//							// all values were in cache
//							results[i] = resultTmp;
//						} else {
//							// source dimension was covered but not for all target objects
//							ArrayList<Object> trgNotCovered = new ArrayList<Object>(trgSingleObjects);
//							trgNotCovered.removeAll(trgCovered);
//							IObjectInstanceProvider  oipBNotCovered = createAndFillInstanceProvider(trgNotCovered);
//							MatchResult resultsNotCovered = executeMemory(srcGraph, trgGraph, simmeas[i], srcSingleObjects, trgNotCovered, oipA, oipBNotCovered);
//							results[i] = new MatchResultArray(srcSingleObjects, trgSingleObjects);
//							((MatchResultArray)results[i]).append(resultTmp);
//							((MatchResultArray)results[i]).append((MatchResultArray)resultsNotCovered);
//							cachePut(simmeas[i].getName(), resultsNotCovered);						
//						}	
////						MatchResult resultsExpected = executeMemory(srcGraph, trgGraph, simmeas[i], srcSingleObjects, trgSingleObjects, oipA, oipB);
////						EvaluationMeasure measure = resultsExpected.compare(results[i]);
////						System.out.println("equal: " + measure.getFmeasure());
////						if (measure.getFmeasure()!=1.0 && resultsExpected.getMatchCount()!=0 && results[i].getMatchCount()!=0){
////							System.out.println("NOT ENOUGH");
////						}
//					} else {
//						if (trgCovered.equals(trgSingleObjects)){
//							// target dimension was covered but not for all source objects
//							ArrayList<Object> srcNotCovered = new ArrayList<Object>(srcSingleObjects);
//							srcNotCovered.removeAll(srcCovered);
//							IObjectInstanceProvider  oipANotCovered = createAndFillInstanceProvider(srcNotCovered);
//							MatchResult resultsNotCovered = executeMemory(srcGraph, trgGraph, simmeas[i], srcNotCovered, trgSingleObjects, oipANotCovered, oipB);
//							results[i] = new MatchResultArray(srcSingleObjects, trgSingleObjects);
//							((MatchResultArray)results[i]).append(resultTmp);
//							((MatchResultArray)results[i]).append((MatchResultArray)resultsNotCovered);
//							cachePut(simmeas[i].getName(), resultsNotCovered);
////							MatchResult resultsExpected = executeMemory(srcGraph, trgGraph, simmeas[i], srcSingleObjects, trgSingleObjects, oipA, oipB);
////							System.out.println("equal: " + resultsExpected.compare(results[i]).getFmeasure());
//						} else {
//						// only a fraction was covered - execute normal match
//							results[i] = executeMemory(srcGraph, trgGraph, simmeas[i], srcSingleObjects, trgSingleObjects, oipA, oipB);
//							cachePut(simmeas[i].getName(), results[i]);
//						}
//					}
//				} else {
//					results[i] = executeMemory(srcGraph, trgGraph, simmeas[i], srcSingleObjects, trgSingleObjects, oipA, oipB);
//					cachePut(simmeas[i].getName(), results[i]);
//				}
		}
		Combination combination = matcher.getSimCombination();
		// combine if severals similarity measure 
		MatchResult result = null;
		if (results.length>1){
			// combine results if more than one results
			result = combination.combine(results);
		} else if (results.length==1) {
			result = results[0];
		}
		if (result==null){
			return null;
		}
		Combination setCombination = matcher.getSetCombination();
		if (combinedOneValue && !duplicates){			
			result = new MatchResultArray(srcObjects, trgObjects, ((MatchResultArray)result).getSimMatrix());
		    result.setSourceGraph(srcGraph);
		    result.setTargetGraph(trgGraph);
		} else {
			// combine for "several resolved objects" for one higher resolution object
			result = setCombination.setCombination(srcObjects, trgObjects, resolution3, result);
		}
		
		if (verbose) System.out.println(resolution3+ " " + matcher + " " + combination +  " " + setCombination);
//		if (verbose) System.out.println(result.toString());
		if (verbose) System.out.println("MatchCount : " + result.getMatchCount());
		return result;
	}
	

	private IObjectInstanceProvider createAndFillWordReplacer(ArrayList<String> abbrevList, ArrayList<String> fullFormList){
			// put source objects into the source instance provider		
		    StoreableMainMemoryObjectInstanceProvider provider = new StoreableMainMemoryObjectInstanceProvider();
		    provider.open();
		    IObjectInstanceProvider  oip = provider;
		    int length = 0;
			for (int j=0; j<abbrevList.size(); j++) {	
				String wordAttrName =  abbrevList.get(j);
				String replAttrName =  fullFormList.get(j);
				HashMap<String, Object> map = new HashMap<String, Object>();
				
				map.put("id", j);
				map.put(WORD_ATTRNAME, wordAttrName);
				map.put(REPL_ATTRNAME, replAttrName);
				
				ObjectInstance oi =  new ObjectInstance(map, "id");
//				new ObjectInstance( "id", j , "attr", constituent);
				
				if (oi!=null){
					provider.add( oi);
				}
	//			if (j==1000) break;
			}
			length=length/abbrevList.size();
			System.out.println("***\tlength:\t" + length );
			return oip;
		}
	
	
	
	/**
	 * @param source
	 * @param target
	 * @param resolution1
	 * @param resolution2a
	 * @param resolution2b
	 * @param resolution3
	 * @param comatcher
	 * @return MatchResult
	 * execute the given similarity measure with the resolutions on the given input graphs
	 */
	MatchResult executeMemory(Graph srcGraph, Graph trgGraph, SimilarityMeasure simmeas, ArrayList<Object> srcObjects, 
			ArrayList<Object> trgObjects, IObjectInstanceProvider oipA, IObjectInstanceProvider oipB){
		if (printSteps){
			System.out.println("Execute similarity measure (in memory) " + simmeas.getName());
		}
		if (srcObjects.isEmpty() || trgObjects.isEmpty()) return null;
		
		
		// ** Version 1 *** use all 
		// initiate mapping - here get the match results stored
//	    MainMemoryMapping mapping = new MainMemoryMapping();
		// with more than 1 million values to use
	    ArrayListMainMemoryMapping mapping = null; 
	    
	    boolean forward = true;
	    
		if (forward){
			 mapping = new ArrayListMainMemoryMapping(oipA, oipB); 
		} else {
			 mapping = new ArrayListMainMemoryMapping(oipB, oipA); 
		}
	    
		
		
//		// *** Version 2 *** use topN selection
//		// save for each source object only parts of the possible match similarities
//		AbstractMappingStore mapping = null;
//		boolean forward = false;
//		int n = 10;  // 100 50 20 10 trgObjects.size()/10;
//		System.out.println("n:\t" + n);
//		if (forward){
//			 mapping = new TopNMainMemoryMapping(n, oipA);
//		} else {
//			 mapping = new TopNMainMemoryMapping(n, oipB);
//		}
		
		
//		// *** Version 3 *** use topDelta selection
//		// save for each source object only parts of the possible match similarities
//		AbstractMappingStore mapping = null;
//		boolean forward = false;
//		double d =0.1; // 0.1 0.2 0.3 0.4 1
//		System.out.println("d:\t" + d);
//		if (forward){
//			 mapping = new TopDeltaMainMemoryMapping(d, oipA);
//		} else {
//			 mapping = new TopDeltaMainMemoryMapping(d, oipB);
//		}		
		
		
	    IObjectMatcher matcher = SimilarityMeasure.getMatcher(simmeas.getId(),wordList, synonymList);
//	    if (matcher.getClass().equals(FeatVectorMatcher.class)){
//	    	((FeatVectorMatcher)matcher).setCache(resultCaches.get(simmeas.getName()));
//	    }
	    if (matcher==null) return null;
	    try {
	    	long start = System.currentTimeMillis();
//	    	System.out.println("start matcher.match");
			// execute the matching over using the "filled" instance provider and write results into the mapping
			if (forward){
				matcher.match( oipA, oipB, mapping);
			} else {
				matcher.match( oipB, oipA, mapping);
			}
			long end = System.currentTimeMillis();
//			System.out.println(srcGraph.getSource().getName() + " <-> " + trgGraph.getSource().getName());
			System.out.println("***\t" + simmeas.getName() + ": " + (end-start) + "ms");
			float[][] simMatrix = new float[srcObjects.size()][trgObjects.size()];
//			System.out.println(srcObjects.size() + " " + trgObjects.size());
		    for( final IMappingEntry me : mapping)
		    {
		    	if (me==null) continue;
		    	IObjectInstance source = me.getLeft();
		    	int src = Integer.parseInt(source.getId());
		    	IObjectInstance target = me.getRight();
		    	int trg = Integer.parseInt(target.getId());
		    	ISimilarity similarity = me.getSimilarity();
		    	float sim = (float) similarity.getSim();
		    	if (sim>0){
//		    		System.out.println(src + " / " + srcObjects.size()+ "  " + trg + " / " + trgObjects.size());
		    		if (forward){
		    			simMatrix[src][trg]=sim;
		    		} else {
		    			simMatrix[trg][src]=sim;
		    		}
		    	}
		    }
//		    mmm = new MainMemoryMapping();
//		    System.out.println("Matching done: " + matcher.getClass());
		    mapping = null;
		    MatchResult result = new MatchResultArray(srcObjects, trgObjects, simMatrix);
//		    if (verbose) System.out.println(result.toString());
		    if (verbose) System.out.println("MatchCount : " + result.getMatchCount() + "\t" +  matcher.getClass());
		    matcher = null;
		    result.setSourceGraph(srcGraph);
		    result.setTargetGraph(trgGraph);
		    return result;
		} catch (MappingStoreException e) {
			System.err.println("ExecWorkflow.execute() Error " + e.getMessage());
		}
		return null;
	}
	
	static ObjectInstance getObjectInstance(Object constituent, int id){
//		oi = new ObjectInstance( "id", 1, "attr", "a attr value");
		if (constituent==null) return null;
		if (constituent instanceof String){
			return new ObjectInstance( "id", id , "attr", constituent);
//		} else if (constituent instanceof float[]){
//			return new ObjectInstance( "id", id , "attr", constituent);
		} else if (constituent instanceof Statistics){
			return new ObjectInstance( "id", id , "attr", ((Statistics)constituent).getValues());
		} else{
			System.out.println("Unkown ObjectInstance");
		}
		return null;
	}
	
	  public void createTable(Statement statement, String mapping) throws SQLException
	  {
//		  String query = " CREATE TABLE "+mapping+" (oi_id_left VARCHAR(250) NOT NULL, oi_id_right VARCHAR(250) NOT NULL,"+
//		  String query = " CREATE TABLE "+mapping+" (oi_id_left VARCHAR(250) NOT NULL, oi_id_right VARCHAR(250) NOT NULL,"+
		  String query = " CREATE TABLE "+mapping+" (oi_id_left INTEGER NOT NULL, oi_id_right INTEGER NOT NULL,"+
		  " sim  FLOAT NOT NULL) ENGINE = MYISAM";
		  statement.execute(query);
	  }
	  
	  public void deleteTable(Statement statement, String mapping) throws SQLException
	  {
		  String query = " DROP TABLE "+mapping;
		  statement.execute(query);
	  }


	  private boolean haveCombinedOneValue(ArrayList<ArrayList<Object>> combinedObjects){
			for (ArrayList<Object> list : combinedObjects) {
				if (list!=null && list.size()>1){
					return false;
				}
			}
			return true;
		}
		

	public void cachePut(String matcher, MatchResult result) {
			    if (matcher==null || result==null) return;
			    SimilarityCache cache = resultCaches.get(matcher);
			    if (cache==null){
			    	cache = new SimilarityCache();
			    	resultCaches.put(matcher, cache);
			    }
			    cache.put(result);
		  }
	
//		  public float cacheGet(String matcher, Object aObj, Object bObj) {
//			    if (aObj==null || bObj==null || matcher==null) return MatchResult.SIM_UNDEF;
//			    SimilarityCache cache = resultCaches.get(matcher);
//			    if (cache==null) return MatchResult.SIM_UNDEF;
//			    return cache.get(aObj, bObj);
//		  }
//		  
//		  public float[][] cacheGet(String matcher, ArrayList aObjs, ArrayList bObjs) {
//			    if (aObjs==null || bObjs==null) return null;
//			    Integer cacheId = new Integer(matcher);
//			    SimilarityCache cache = resultCaches.get(cacheId);
//			    return cache.get(aObjs, bObjs);
//		  }
		  
		  public MatchResultArray cacheGetFull(String matcher, ArrayList aObjs, ArrayList bObjs) {
			    if (aObjs==null || bObjs==null) return null;
			    SimilarityCache cache = resultCaches.get(matcher);
			    if (cache==null) return null;
			    return cache.getFull(aObjs, bObjs);
		  }



		
}
