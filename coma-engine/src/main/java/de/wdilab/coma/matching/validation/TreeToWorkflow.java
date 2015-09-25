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

package de.wdilab.coma.matching.validation;


import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.debug.ParseTreeBuilder;
import org.antlr.runtime.tree.Tree;

import de.wdilab.coma.matching.Combination;
import de.wdilab.coma.matching.ComplexMatcher;
import de.wdilab.coma.matching.Matcher;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Selection;
import de.wdilab.coma.matching.SimilarityMeasure;
import de.wdilab.coma.matching.Strategy;
import de.wdilab.coma.matching.Workflow;


/**
 * TreeToWorkflow realizes the transformation from a given string to a Grammar Tree
 * and from that to a Workflow structure that can be executed  * 
 * 
 * @author Patrick Arnold, Sabine Massmann
 */
public class TreeToWorkflow {   
    
    /**
     * The method to print the parsed tree. Remember: Adapting this method would make it possible
     * to create a tree data structure. The method is recursively executed.
     * @param tree The tree which the parsed generated.
     * @param indent The indent (node level). This should be 0 at the beginning.
     */
    public static void showTree( Tree tree, int indent) {
    	// Creating the tree using recursion:
    	int i = tree.getChildCount();
    	if( i > 0) {   // We have at least one child :-)
    		indent++;	// Thus, increase the indent (we are one level below now)
    		for( int j=0; j < i; j++) {   // Iterate the children (no indent increasing here!)
	    		Tree subtree = tree.getChild(j);   // The subtree of the j-th child
	    		String name = tree.getChild(j).toString();   // The child's name
	    		// Print the name, but only if it is not a comma, brace etc. (we don't need those).
	    		
	    		if( !name.equals( ";") && !name.equals( "(") && !name.equals( ")") && !name.equals( ",")) {
	    	    	for( int k=0; k < indent; k++) {   // Create indent
//	    	    		System.out.print( "\t");
	    	    		System.out.print( "  ");
	    	    	}
	    	    	System.out.print( "(" + indent + ")");   // Print the level (for debugging/test)
	    			System.out.println( name);    // Print the node name
	    		} 
	    		// After printing the node name call this method again to display the children.
	    		showTree( subtree, indent);   
    		}
    		indent--;   // Backtracking -> Decrease the indent (we get back to a higher level again)
    	}
    }

    public static Workflow buildWorkflow( String value) {
    	Tree tree = getTree(value);
    	return buildWorkflow(tree);
    }
    
    /**
     * build the parsed tree. Remember: Adapting this method would make it possible
     * to create a tree data structure. The method is recursively executed.
     * @param tree The tree which the parsed generated.
     */
    public static Workflow buildWorkflow( Tree tree) {
    	// Creating the tree using recursion:
    	int cnt = tree.getChildCount();
    	if( cnt > 0) {   // We have at least one child, otherwise atomic level
    		 // Iterate the children
    		for( int j=0; j < cnt; j++) {   
	    		Tree subtree = tree.getChild(j);   // The subtree of the j-th child
	    		String name = tree.getChild(j).toString();   // The child's name
	    		// Print the name, but only if it is not a comma, brace etc. (we don't need those).	    		
	    		if( !name.equals( ";") && !name.equals( "(") && !name.equals( ")") && !name.equals( ",")) {
	    			System.out.println( name);  
	    		} 
	    		// After printing the node name call this method again to display the children.
	    		if (name.equals("workflow")){
	    			Workflow workflow = getWorkflow(subtree);
	    			if (workflow!=null){
		    			System.out.println(workflow.toString());
		    			System.out.println(workflow.toString(false));
	    			}
	    			return workflow;
	    		} 
	    		
	    		Workflow workflow =  buildWorkflow( subtree);
	    		if (workflow!=null) return workflow;
	    		
    		}
    	}
    	return null;
    }
    
    static Workflow getWorkflow(Tree tree){
    	Workflow workflow = new Workflow();
    	int cnt = tree.getChildCount();
    	if( cnt > 0) {   // We have at least one child, otherwise atomic level
    		boolean parallel = true;  // default parallel execution
    		Strategy firstStrategy = null;
    		 // Iterate the children
    		for( int j=0; j < cnt; j++) {   
    			Tree subtree = tree.getChild(j);
	    		String name = subtree.toString();   // The child's name
	    		if (name.equals( ";")){
	    			parallel = false;
	    		} else if (name.equals( ",")){
	    			parallel = true;
	    		} else if (name.equals( "strategy")){	
	    			Strategy strategy = getStrategy(subtree);
	    			if (strategy!=null){
	    				
	    				if (!parallel && firstStrategy!=null){
	    					workflow.setSecondStrategy(strategy);
		    			} else {
		    				
		    				workflow.addBegin(strategy);
		    			}
	    			}
	    			if (firstStrategy==null){
	    				firstStrategy = strategy;
	    			}
	    			
	    		} else if (name.equals( "result_combination")){
	    			// assumption all nodes as input
	    			Combination combination = getCombination(subtree);
	    			// add combination for all strategies to this combination
	    			if (combination!=null){
	    				workflow.setCombination(combination);
	    			}
//	    		} else if( !name.equals( "(") && !name.equals( ")")) {
	    		} else if( name.equals( "reuse")) {
	    			return null;
    			}
    		}
    		return workflow;
    	}
    	return null;
    }

    
    static Strategy getStrategy(Tree tree){
    	removeNotNeededChildren(tree); // remove (;,)
    	int cnt = tree.getChildCount();
		String resolution = tree.getChild(0).toString();
		int resolutionId = Resolution.stringToResolution(resolution);
		if( cnt == 2) {
    		// resolution, strategy or cmatcher
    		Tree subtree = tree.getChild(1);
    		String name = subtree.toString();   // The child's name
    		if (name.equals( "complexMatcher")){
    			ComplexMatcher cmatcher = getComplexMatcher(subtree);
        		return new Strategy(resolutionId, cmatcher);
    		}
    	} else if( cnt == 3) {
    		// resolution, strategy or cmatcher, selection
    		Tree subtree = tree.getChild(1);
    		String name = subtree.toString();   // The child's name
    		if (name.equals( "complexMatcher")){
    			ComplexMatcher cmatcher = getComplexMatcher(subtree);
    			subtree = tree.getChild(2);
        		name = subtree.toString();   // The child's name
        		if (name.equals( "selection")){
        			Selection selection = getSelection(subtree);
            		return new Strategy(resolutionId, cmatcher, selection);
        		}
    		}
    	} else {
    		// resolution, several strategy or cmatcher, combination, maybe selection	
    		int size = cnt-1; // first is resolution
    		Tree subtree = tree.getChild(cnt-1);
    		Combination simCombination = getCombination(subtree); 
    		Selection selection = null;
    		if (simCombination==null){
    			// last selection
    			selection = getSelection(subtree);
    			subtree = tree.getChild(cnt-2);
        		simCombination = getCombination(subtree); 
        		size--;
    		}
    		ComplexMatcher[] cmatch = new ComplexMatcher[size-1];
    		for (int j = 1; j < size; j++) {
        		subtree = tree.getChild(j);
        		String name = subtree.toString();   // The child's name
        		if (name.equals( "complexMatcher")){
        			cmatch[j-1] = getComplexMatcher(subtree);
        		}
			}    		
    		return new Strategy(resolutionId, cmatch, simCombination.getId(), simCombination.getWeights(), selection);
    	}
		return null;
    }
 
   public static ComplexMatcher getComplexMatcher(Tree tree){
    	removeNotNeededChildren(tree); // remove (;,)
    	int cnt = tree.getChildCount();
    	if( cnt == 3) {
    		// resolution, matcher, setCombination
    		String resolution = tree.getChild(0).toString();
    		int resolutionId = Resolution.stringToResolution(resolution);
    		Tree subtree = tree.getChild(1);
    		String text = subtree.getText();
    		if (text.equals("matcher")){
    			Matcher matcher = getMatcher(subtree);
        		String setCombination = tree.getChild(2).toString();
        		int setCombinationId = Combination.stringToCombination(setCombination);
        		return new ComplexMatcher(resolutionId, matcher, setCombinationId);
    		} else if (text.equals("complexMatcher")){
    			ComplexMatcher cmatcher = getComplexMatcher(subtree);
        		String setCombination = tree.getChild(2).toString();
        		int setCombinationId = Combination.stringToCombination(setCombination);
        		return new ComplexMatcher(resolutionId, cmatcher, setCombinationId);
    		} else {
    			return null;
    		}
    	} else {
    		// resolution, matchers, simCombination, setCombination
    		String resolution = tree.getChild(0).toString();
    		int resolutionId = Resolution.stringToResolution(resolution);
    		
    		int size = cnt-3; // first is resolution, last two are combinations
    		Object[] m_cm = new Object[size];
    		for (int j = 1; j <= size; j++) {
        		Tree subtree = tree.getChild(j);
        		String text = subtree.getText();
        		// matcher or complex matcher
        		if (text.equals("matcher")){
        			m_cm[j-1] = getMatcher(subtree);
        		} else if (text.equals("complexMatcher")){
        			m_cm[j-1] = getComplexMatcher(subtree);
        		}        		
			}    		
    		Tree subtree = tree.getChild(cnt-2);
    		Combination simCombination = getCombination(subtree);    		
    		String setCombination = tree.getChild(cnt-1).toString();
    		int setCombinationId = Combination.stringToCombination(setCombination);
    		return new ComplexMatcher(resolutionId, m_cm, simCombination.getId(), simCombination.getWeights(), setCombinationId);
    	}
    }
    
    static Matcher getMatcher(Tree tree){
    	removeNotNeededChildren(tree); // remove (;,)
    	int cnt = tree.getChildCount();
    	if( cnt == 3) {
    		// resolution, simmeasure, setCombination
    		String resolution = tree.getChild(0).toString();
    		int resolutionId = Resolution.stringToResolution(resolution);
    		String simmeasure = tree.getChild(1).toString();
    		int simmeasureId = SimilarityMeasure.stringToMeasure(simmeasure);
    		String setCombination = tree.getChild(2).toString();
    		int setCombinationId = Combination.stringToCombination(setCombination);
    		return new Matcher(resolutionId, simmeasureId, setCombinationId);
    	}
    	
		// resolution, simmeasures, simCombination, setCombination
		String resolution = tree.getChild(0).toString();
		int resolutionId = Resolution.stringToResolution(resolution);
		int size = cnt - 3; // first is resolution, last two are combinations
		int[] simmeasureIds = new int[size];
		for (int j = 1; j <= size; j++) {
			String simmeasure = tree.getChild(j).toString();
			simmeasureIds[j - 1] = SimilarityMeasure
					.stringToMeasure(simmeasure);
		}
		Tree subtree = tree.getChild(cnt - 2);
		Combination simCombination = getCombination(subtree);
		String setCombination = tree.getChild(cnt - 1).toString();
		int setCombinationId = Combination.stringToCombination(setCombination);
		return new Matcher(resolutionId, simmeasureIds, simCombination.getId(), simCombination.getWeights(), setCombinationId);
    	
    }
    
    
    
    static Combination getCombination(Tree tree){
    	removeNotNeededChildren(tree); // remove (;,)
    	int cnt = tree.getChildCount();
    	if( cnt == 1) {   // One child expected
    		String combination =  tree.getChild(0).toString();
    		int combinationId = Combination.stringToCombination(combination);
    		return new Combination(combinationId);
    	} else if (cnt>2){
    		String combination =  tree.getChild(0).toString();
    		int combinationId = Combination.stringToCombination(combination);
    		int size = cnt-1;
    		float[] weights = new float[size];
    		for (int j = 0; j < size; j++) {
    			weights[j] = Float.valueOf(tree.getChild(j+1).toString());
			}
    		return new Combination(combinationId, weights);
    	}
    	return null;
    }
    
    static Selection getSelection(Tree tree){
    	removeNotNeededChildren(tree); // remove (;,)
    	int cnt = tree.getChildCount();
    	if( cnt == 2) {   // two children expected
    		// direction
    		String direction =  tree.getChild(0).toString();    		
    		int directionId = Selection.stringToDirection(direction);
    		Tree selectionParameter =  tree.getChild(1);    
    		removeNotNeededChildren(selectionParameter); // remove (;,)
    		String selection = selectionParameter.getChild(0).toString();
    		int selectionId= Selection.stringToSelection(selection);
    		if (selectionId==Selection.SEL_THRESHOLD || selectionId==Selection.SEL_DELTA){
    			float value = Float.valueOf(selectionParameter.getChild(1).toString());
    			return new Selection(directionId, selectionId,value);	
    		} else if (selectionId==Selection.SEL_MAXN){
    			int value = Integer.valueOf(selectionParameter.getChild(1).toString());
    			return new Selection(directionId, selectionId ,value);	
    		} else if (selectionId==Selection.SEL_MULTIPLE){
    			int maxn = Integer.valueOf(selectionParameter.getChild(1).toString());
    			float delta = Float.valueOf(selectionParameter.getChild(2).toString());
    			float threshold = Float.valueOf(selectionParameter.getChild(3).toString());
    			return new Selection(directionId, selectionId,maxn, delta, threshold);	
    		} 
    	}
    	return null;
    }
    
	// deal with (;,)
    public static void removeNotNeededChildren(Tree tree){
    	int cnt = tree.getChildCount();
    	for (int i = 0; i < cnt; i++) {
			String name =  tree.getChild(i).toString();
			if (name.equals("(") || name.equals(";") || name.equals(",") || name.equals(")")){
				cnt--;
				tree.deleteChild(i);
				i--;
			}
		}
    }    
    
    public static boolean isValidWorkflowVariable(String value){
    	String treeString = null;
    	Tree tree = TreeToWorkflow.getTree(value);
    	if (tree!=null && tree.getChildCount()>0){
    		// remove first level "<grammar ComaWorkFlow>" 
    		tree = tree.getChild(0);
    		if (tree!=null && tree.getChildCount()>0)
    			// remove second level "coma"
    			treeString = tree.getChild(0).toStringTree();
    	}
	
		if (treeString==null){
			String message = "Error not specified";
			System.err.println("TreeToWorkflow.valid " + message);
			return false;
		} else if (treeString.contains("<") && treeString.contains(">")){
			// e.g. missing or mismatched
			String message = treeString.substring(treeString.indexOf("<")+1, treeString.lastIndexOf(">"));
			System.err.println("TreeToWorkflow.valid " + message);
			return false;
		} else if (treeString.contains("Exception")){
			// e.g. missing or mismatched
			int index = treeString.indexOf("Exception");
			String message = treeString.substring(0, index);
			message = message.substring(message.lastIndexOf(" "));
			System.err.println("TreeToWorkflow.valid " + message);
			return false;
		} else {
			return true;
		}
    }
    
    public static Tree getTree(String value){	
    	ANTLRStringStream in = new ANTLRStringStream( value.toLowerCase());        
    	ComaWorkFlowLexer lexer = new ComaWorkFlowLexer(in);
    	CommonTokenStream tokens = new CommonTokenStream(lexer);   
    	ParseTreeBuilder builder = new ParseTreeBuilder("ComaWorkFlow");
    
    	ComaWorkFlowParser parser = new ComaWorkFlowParser( tokens, builder);
    	 try {
			parser.coma();
//			parser.workflow();
//    		 parser.reuse();
    		return builder.getTree();
		} catch (RecognitionException e) {
			System.out.println("TreeToWorkflow.getTree() Error  " + e.getMessage());
		} 
		return null;
    }
    
    public static boolean getAllowedToken(String value){
    	ANTLRStringStream in = new ANTLRStringStream( value.toLowerCase());        
    	ComaWorkFlowLexer lexer = new ComaWorkFlowLexer(in);
    	CommonTokenStream tokens = new CommonTokenStream(lexer);   
    	ParseTreeBuilder builder = new ParseTreeBuilder("ComaWorkFlow");
    
    	ComaWorkFlowParser parser = new ComaWorkFlowParser( tokens, builder);
    	 try {
			parser.allowedToken();
    		Tree tree = builder.getTree();
    		if (tree==null){
    			return false;
    		}
    		Tree child = tree.getChild(0).getChild(0);
    		if (child.toString().equalsIgnoreCase(value)) {
    			return true;
    		}
    		
		} catch (RecognitionException e) {
			System.out.println("TreeToWorkflow.getTree() Error  " + e.getMessage());
		} 
		return false;
    }
    
}