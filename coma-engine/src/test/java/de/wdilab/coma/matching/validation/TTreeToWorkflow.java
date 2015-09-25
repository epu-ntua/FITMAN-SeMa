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

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.debug.ParseTreeBuilder;
import org.antlr.runtime.tree.ParseTree;
import org.antlr.runtime.tree.Tree;

import de.wdilab.coma.matching.Combination;
import de.wdilab.coma.matching.ComplexMatcher;
import de.wdilab.coma.matching.Matcher;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Selection;
import de.wdilab.coma.matching.SimilarityMeasure;
import de.wdilab.coma.matching.Strategy;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.structure.MatchResult;

/**
 * Testing if default matcher/complex matcher/strategy/workflow
 * are as directly created and via string + grammar the same;
 * test also for allowed token of selection/direction/combination/simmeasures 
 * 
 * @author Sabine Massmann
 *
 */
public class TTreeToWorkflow extends TestCase {
	
	public void testStringWithoutVariable() throws Exception {
		System.out.println("Matcher");
		for (int i = 0; i < Matcher.MATCHER.length; i++) {
			int current = Matcher.MATCHER[i];
			Matcher matcher = new Matcher(current);
			String expected = matcher.toString(false);
			System.out.println(expected.toLowerCase());
			Tree tree = TreeToWorkflow.getTree(expected).getChild(0).getChild(0);
			Matcher actualMatcher = TreeToWorkflow.getMatcher(tree);
			String actual = actualMatcher.toString(false);
			assertEquals(expected, actual);
		}
		System.out.println("\nComplexMatcher");
		for (int i = 0; i < ComplexMatcher.COMPLEXMATCHER.length; i++) {
			int current = ComplexMatcher.COMPLEXMATCHER[i];
			ComplexMatcher cmatcher = new ComplexMatcher(current);
			String expected = cmatcher.toString(false);
			System.out.println(expected.toLowerCase());
			Tree tree = TreeToWorkflow.getTree(expected).getChild(0).getChild(0);
			ComplexMatcher actualComplexMatcher = TreeToWorkflow.getComplexMatcher(tree);
			String actual = actualComplexMatcher.toString(false);
			assertEquals(expected, actual);
		}
		System.out.println("\nStrategy");
		for (int i = 0; i < Strategy.STRATEGY.length; i++) {
			int current = Strategy.STRATEGY[i];
			Strategy strategy = new Strategy(current);
			String expected = strategy.toString(false);
			System.out.println(expected.toLowerCase());
			Tree tree = TreeToWorkflow.getTree(expected).getChild(0).getChild(0);
			Strategy actualStrategy = TreeToWorkflow.getStrategy(tree);
			String actual = actualStrategy.toString(false);
			assertEquals(expected, actual);
		}

		System.out.println("\n Workflow");
		for (int i = 0; i < Workflow.WORKFLOW.length; i++) {
			int current = Workflow.WORKFLOW[i];
			Workflow workflow = new Workflow(current);
			String expected = workflow.toString(false);
			Tree tree = TreeToWorkflow.getTree(expected).getChild(0).getChild(0);
			Workflow actualWorkflow = TreeToWorkflow.getWorkflow(tree);
			String actual = actualWorkflow.toString(false);
			System.out.println(expected);
			System.out.println(actual);
			assertEquals(expected, actual);
		}
	}
	
	
	public void testToken(){
		for (int i = 0; i < Selection.SELECTION_IDS.length; i++) {
			int current = Selection.SELECTION_IDS[i];
			String expected = Selection.selectionToString(current);
			System.out.println(expected);
			assertTrue(TreeToWorkflow.getAllowedToken(expected));
		}
		for (int i = 0; i < Selection.DIRECTION_IDS.length; i++) {
			int current = Selection.DIRECTION_IDS[i];
			String expected = Selection.directionToString(current);
			System.out.println(expected);
			assertTrue(TreeToWorkflow.getAllowedToken(expected));
		}		
		for (int i = 0; i < Combination.COM_IDS.length; i++) {
			int current = Combination.COM_IDS[i];
			String expected = Combination.combinationToString(current);
			System.out.println(expected);
			assertTrue(TreeToWorkflow.getAllowedToken(expected));
		}
		for (int i = 0; i < SimilarityMeasure.SIMMEASURE.length; i++) {
			int current = SimilarityMeasure.SIMMEASURE[i];
			String expected = SimilarityMeasure.measureToString(current);
			System.out.println(expected);
			assertTrue(TreeToWorkflow.getAllowedToken(expected));
		}
		for (int i = 0; i < SimilarityMeasure.SIMMEASURE_STRING.length; i++) {
			int current = SimilarityMeasure.SIMMEASURE_STRING[i];
			String expected = SimilarityMeasure.measureToString(current);
			System.out.println(expected);
			assertTrue(TreeToWorkflow.getAllowedToken(expected));
		}
		for (int i = 0; i < Resolution.RES1.length; i++) {
			int current = Resolution.RES1[i];
			String expected = Resolution.resolutionToString(current);
			System.out.println(expected);
			assertTrue(TreeToWorkflow.getAllowedToken(expected));
		}
		for (int i = 0; i < Resolution.RES2.length; i++) {
			int current = Resolution.RES2[i];
			String expected = Resolution.resolutionToString(current);
			System.out.println(expected);
			assertTrue(TreeToWorkflow.getAllowedToken(expected));
		}
		for (int i = 0; i < Resolution.RES3.length; i++) {
			int current = Resolution.RES3[i];
			String expected = Resolution.resolutionToString(current);
			System.out.println(expected);
			assertTrue(TreeToWorkflow.getAllowedToken(expected));
		}
		for (int i = 0; i < MatchResult.COMPOSE_IDS.length; i++) {
			int current = MatchResult.COMPOSE_IDS[i];
			String expected = MatchResult.compositionToString(current);
			System.out.println(expected);
			assertTrue(TreeToWorkflow.getAllowedToken(expected));
		}
	}
	
	public void testtransform() throws Exception {
    	String expr1a = "((InnerPaths;(SelfPath;(SelfPath;(Nametoken;Trigram;Set_Average);Set_Average),(SelfNode;(Nametoken;Trigram,UserSyn;Max;Set_Max),(Name;UserSyn;Set_Average);Max;Set_Average);Average;Set_Average);(Both,Threshold(0.3))),(DownPaths;(SelfNode;(Nametoken;Trigram;Set_Average),(Datatype;DatatypeSimilarity;Set_Average);Weighted(0.7,0.3);Set_Average);(Both,Threshold(0.3))))";
    	String expr1b = "((DownPaths;(SelfNode;(Nametoken;Trigram;Set_Average),(Datatype;DatatypeSimilarity;Set_Average);Weighted(0.7,0.3);Set_Average);(Both,Threshold(0.3))),(InnerPaths;(SelfPath;(SelfPath;(Nametoken;Trigram;Set_Average);Set_Average),(SelfNode;(Nametoken;Trigram,UserSyn;Max;Set_Max),(Name;UserSyn;Set_Average);Max;Set_Average);Average;Set_Average);(Both,Threshold(0.3))))";
	
    	// Initializing and running the parser
    	// lowercase because token in grammar are all lowercase
    	ANTLRStringStream in = new ANTLRStringStream( expr1a.toLowerCase());        
    	ComaWorkFlowLexer lexer = new ComaWorkFlowLexer(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
       
        ParseTreeBuilder builder = new ParseTreeBuilder("ComaWorkFlow");
        ComaWorkFlowParser parser = new ComaWorkFlowParser( tokens, builder);
        parser.coma(); 
        
        ParseTree tree = builder.getTree();
        TreeToWorkflow.showTree(  tree, 0);   // That's the tree printed as tree
        
        Workflow w = TreeToWorkflow.buildWorkflow( tree);

        String expr1_2 =  w.toString(false);
        System.out.println("***");
        System.out.println(expr1a);
        System.out.println(expr1b);
        System.out.println(expr1_2);
        if (expr1a.equals(expr1_2)){
        	assertEquals(expr1a, w.toString(false));
        } else {
        	assertEquals(expr1b, w.toString(false));
        }
	}

	
}
