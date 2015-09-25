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
import java.util.List;
import java.util.Random;

/**
 * Strategy realizes the sequence of Resolution1, one or more ComplexMatchers/Strategies 
 * and their combination (if only one complexmatcher/strategy that combination is a id function) 
 * 
 * NOT part of the grammar, only variable
 * 
 * @author Sabine Massmann
 */
public class Strategy{ 	// previous ComplexStrategy
	
	// pre-defined strategies
	// start counting 6000 Manager.COMPLEXSTRAT_CNT


	public static final int CONTEXT = Constants.STRAT_CNT + 1;
	public static final int NODES = Constants.STRAT_CNT + 2;
	public static final int COMA_OPT = Constants.STRAT_CNT + 3;
	public static final int COMA = Constants.STRAT_CNT + 4;
	public static final int SIMPLE_NAMEPATH = Constants.STRAT_CNT + 5;
	public static final int NODES_NAME = Constants.STRAT_CNT + 6;
	public static final int NODES_PATH = Constants.STRAT_CNT + 7;
	public static final int COMA_OPT_INST = Constants.STRAT_CNT + 8;
	
	
	public static final int NODE_SELECTION = Constants.STRAT_CNT + 11;
	public static final int UPPATH_SELECTION = Constants.STRAT_CNT + 12;
	public static final int FRAG_SELECTION = Constants.STRAT_CNT + 13;
	public static final int DOWNPATH_SELECTION = Constants.STRAT_CNT + 14;
	
	public static final Integer[] STRATEGY = {
		CONTEXT, NODES, COMA_OPT, COMA_OPT_INST, COMA, SIMPLE_NAMEPATH,
		NODE_SELECTION, UPPATH_SELECTION,
		FRAG_SELECTION, DOWNPATH_SELECTION,
		NODES_NAME, NODES_PATH
	};
	public static final List<Integer> STRATEGY_LIST = Arrays.asList(STRATEGY);
	  
	// resolution 1
	Resolution resolution = null;
	// complex matcher or strategies
	ComplexMatcher[] cm = null;
	// combination -> necessary if more than one function
	Combination simCombination = null;
	// selection -> often needed (maybe not for reuse, etc.)
	Selection selection = null;
	
    String name= null;
	
	
    public Strategy(String name){
    	this.name= name;
    }
    
	// Constructor of a comple strategy with only resolution and complex matcher/strategy
	public Strategy (int resolution, ComplexMatcher cm){
		// check if resolution is resolution 1
		if (Resolution.getType(resolution)==Resolution.TYPE_RES1){
			this.resolution = new Resolution(resolution);
		} else {
			System.out.println("Error: resolution " + resolution + " not type 1");
		}
		this.cm = new ComplexMatcher[1];
		this.cm[0] = cm;
	}
	
	// Constructor of a comple strategy with only resolution and complex matcher/strategy
	public Strategy (int resolution, ComplexMatcher cm, Selection selection){
		// check if resolution is resolution 1
		if (Resolution.getType(resolution)==Resolution.TYPE_RES1){
			this.resolution = new Resolution(resolution);
		} else {
			System.out.println("Error: resolution " + resolution + " not type 1");
		}
		this.cm = new ComplexMatcher[1];
		this.cm[0] = cm;
		this.selection = selection;
	}
	
	// Constructor of a strategy with a resolution, similarity measures and combination
	public Strategy (int resolution, ComplexMatcher[] cm, int combination, float[] weights){		
		// check if resolution is resolution 1
		if (Resolution.getType(resolution)==Resolution.TYPE_RES1){
			this.resolution = new Resolution(resolution);
		} else {
			System.out.println("Error: resolution " + resolution + " not type 1");
		}
		this.simCombination = new Combination(combination, weights);
		this.cm = cm;
	}
	
	// Constructor of a strategy with a resolution, similarity measures and combination
	public Strategy (int resolution, ComplexMatcher[] cm, int combination, float[] weights, Selection selection){		
		// check if resolution is resolution 1
		if (Resolution.getType(resolution)==Resolution.TYPE_RES1){
			this.resolution = new Resolution(resolution);
		} else {
			System.out.println("Error: resolution " + resolution + " not type 1");
		}
		this.simCombination = new Combination(combination, weights);
		this.cm = cm;
		this.selection = selection;
	}
	
	// Constructor of a strategy with a resolution, similarity measures and combination
	public Strategy (int resolution, ComplexMatcher[] cm, int combination, Selection selection){		
		// check if resolution is resolution 1
		if (Resolution.getType(resolution)==Resolution.TYPE_RES1){
			this.resolution = new Resolution(resolution);
		} else {
			System.out.println("Error: resolution " + resolution + " not type 1");
		}
		this.simCombination = new Combination(combination);
		this.cm = cm;
		this.selection = selection;
	}
	
	// Constructor of a pre-defined strategy
	public Strategy (int strategy){
		// create one of the default strategy
		switch (strategy) {

		case CONTEXT :	// $ContextStrategy=(Paths;$PathCMatcher;Both,Multiple(0,0.01,0.5))
			this.resolution = new Resolution(Resolution.RES1_PATHS);
			cm = new ComplexMatcher[1];
			cm[0] = new ComplexMatcher(ComplexMatcher.PATH);
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, (float)0.01, (float)0.5);
			setName("ContextS");
			break;	
		case NODES :	// $NodeStrategy=(Nodes;$NametokenSynCMatcher,$LeavesStrategy,$ParentsStrategy,$SiblingsStrategy;Average;Both,Multiple(0,0.01,0.5))
			this.resolution = new Resolution(Resolution.RES1_NODES);
//			cm = new ComplexMatcher[4];
//			cm[0] = new ComplexMatcher(ComplexMatcher.NAMETYPE); //NAMETOKENSYN
//			cm[1] = new ComplexMatcher(ComplexMatcher.LEAVES);
//			cm[2] = new ComplexMatcher(ComplexMatcher.PARENTS);
////			cm[3] = new ComplexMatcher(ComplexMatcher.SIBLINGS);
//			cm[3] = new ComplexMatcher(ComplexMatcher.PATH);
//			simCombination = new Combination(Combination.COM_AVERAGE);
			
//			cm = new Object[1];
//			cm[0] = new Strategy(Strategy.NAMEPATH);
			
//			cm = new Object[2];
//			cm[0] = new ComplexMatcher(ComplexMatcher.NAME); // NAMESTAT NAMETYPE NAME
//			cm[1] = new ComplexMatcher(ComplexMatcher.PATH);
			
			cm = new ComplexMatcher[2];
			cm[0] = new ComplexMatcher(ComplexMatcher.NAME); // NAMESTAT NAMETYPE NAME NAMESYN
			cm[1] = new ComplexMatcher(ComplexMatcher.PARENTS); // PARENTS  LEAVES
			
//			simCombination = new Combination(Combination.COM_AVERAGE);
			simCombination = new Combination(Combination.COM_WEIGHTED, new float[]{(float)0.7, (float)0.3});
//			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, (float)0.01, (float)0.5);
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MAXN, 1, 0, 0);
			setName("NodesS");
			break;	
			
		case NODES_NAME :	
			this.resolution = new Resolution(Resolution.RES1_NODES);
			cm = new ComplexMatcher[1];
			cm[0] = new ComplexMatcher(ComplexMatcher.NAME); // NAMETYPE NAMESTAT
//			cm[0] = new ComplexMatcher(Resolution.RES2_SELFNODE,	new Matcher(Matcher.NAME),Combination.SET_AVERAGE );
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, (float)0.01, (float)0.5);
			setName("NodesNameS");
			break;	
		case NODES_PATH :	
			this.resolution = new Resolution(Resolution.RES1_NODES);
			cm = new ComplexMatcher[1];
			cm[0] = new ComplexMatcher(ComplexMatcher.PATH);
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, (float)0.01, (float)0.5);
			setName("NodesPathS");
			break;	
		case COMA_OPT :	// $ComaOptStrategy=(Paths;$NametokenSynCMatcher,$PathCMatcher,$LeavesStrategy,$ParentsStrategy;Average;Both,Multiple(0,0.01,0.4))
			this.resolution = new Resolution(Resolution.RES1_PATHS);
			cm = new ComplexMatcher[4]; //4 2
			cm[0] = new ComplexMatcher(ComplexMatcher.NAME); // NAMETOKENSYN NAMETYPE NAMESTAT
			cm[1] = new ComplexMatcher(ComplexMatcher.PATH);
			cm[2] = new ComplexMatcher(ComplexMatcher.LEAVES);
			cm[3] = new ComplexMatcher(ComplexMatcher.PARENTS);
			simCombination = new Combination(Combination.COM_AVERAGE);
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, (float)0.01, (float)0.4);
			setName("ComaOptS");
			break;	
		case COMA_OPT_INST :	// $ComaOptStrategy=(Paths;$NametokenSynCMatcher,$PathCMatcher,$InstanceCMatcher,$LeavesStrategy,$ParentsStrategy;Average;Both,Multiple(0,0.01,0.4))
			this.resolution = new Resolution(Resolution.RES1_PATHS);
			cm = new ComplexMatcher[5]; //4 2
			cm[0] = new ComplexMatcher(ComplexMatcher.NAME); // NAMETOKENSYN NAMETYPE NAMESTAT
			cm[1] = new ComplexMatcher(ComplexMatcher.PATH);
			cm[2] = new ComplexMatcher(ComplexMatcher.INSTANCES);
			cm[3] = new ComplexMatcher(ComplexMatcher.LEAVES);
			cm[4] = new ComplexMatcher(ComplexMatcher.PARENTS);
			simCombination = new Combination(Combination.COM_AVERAGE);
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, (float)0.01, (float)0.4);
			setName("ComaOptInstS");
			break;	
		case COMA :		// $ComaStrategy=(Paths;$NametokenSynCMatcher,$PathCMatcher,$LeavesStrategy,$ParentsStrategy,$SiblingsStrategy;Average;Both,Multiple(0,0.008,0.5))
			this.resolution = new Resolution(Resolution.RES1_PATHS);
			cm = new ComplexMatcher[5];
			cm[0] = new ComplexMatcher(ComplexMatcher.NAMETYPE); // NAMETOKENSYN
			cm[1] = new ComplexMatcher(ComplexMatcher.PATH);
			cm[2] = new ComplexMatcher(ComplexMatcher.LEAVES);
			cm[3] = new ComplexMatcher(ComplexMatcher.PARENTS);
			cm[4] = new ComplexMatcher(ComplexMatcher.SIBLINGS);
			simCombination = new Combination(Combination.COM_AVERAGE);
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, (float)0.008, (float)0.5);
			setName("ComaS");
			break;	
		case SIMPLE_NAMEPATH :	
			this.resolution = new Resolution(Resolution.RES1_PATHS);
			cm = new ComplexMatcher[2];
			cm[0] = new ComplexMatcher(ComplexMatcher.NAMETYPE); // NAMETOKENSYN NAMESTAT
			cm[1] = new ComplexMatcher(ComplexMatcher.PATH);
			simCombination = new Combination(Combination.COM_AVERAGE);
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, (float)0.01, (float)0.2);
			setName("SimpleNamePathS");
			break;
		case NODE_SELECTION:	// $NodeSelectionStrategy=(Nodes;$NametokenSynCMatcher;Both,Threshold(0.3));
			this.resolution = new Resolution(Resolution.RES1_NODES);
			cm = new ComplexMatcher[1];
			cm[0] = new ComplexMatcher(ComplexMatcher.NAMETYPE); // NAMETOKENSYN
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_THRESHOLD, 0, 0, (float)0.3);
			setName("NodeSelectionS");
			break;
		case UPPATH_SELECTION:	// $UpPathSelectionStrategy=(UpPaths;$PathCMatcher;Both,Threshold(0.5));
			// treat each path for a node separately and not as a set (because it is Resolution1)
			this.resolution = new Resolution(Resolution.RES1_UPPATHS);
			cm = new ComplexMatcher[1];
			cm[0] = new ComplexMatcher(ComplexMatcher.PATH);
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_THRESHOLD, 0, 0, (float)0.5);
			setName("UpPathSelectionS");
			break;
		case FRAG_SELECTION:	// $FragSelectionStrategy=(Innerpaths;$NamePathStrategy; Both,Threshold(0.3))
			this.resolution = new Resolution(Resolution.RES1_INNERPATHS);
			cm = new ComplexMatcher[1];
			cm[0] = new ComplexMatcher(ComplexMatcher.NAMEPATH);
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MAXN, 3);
			setName("FragSelectionS");
			break;				
		case DOWNPATH_SELECTION:	// $DownPathSelectionStrategy=(DownPaths;$NameTypeCMatcher;Both,Threshold(0.3))
			// treat each path for a node separately and not as a set (because it is Resolution1)
			this.resolution = new Resolution(Resolution.RES1_DOWNPATHS);
			cm = new ComplexMatcher[1];
			cm[0] = new ComplexMatcher(ComplexMatcher.NAMETYPE);
			selection = new Selection(Selection.DIR_BOTH, Selection.SEL_DELTA, (float)0.1);
			setName("DownPathSelectionS");
			break;			
		default :
			System.out.println("Strategy id not known " + strategy);
			break;
		}
	}
	

	//simple getter
	public Resolution getResolution() { return resolution; }
	public Combination getSimCombination() { return simCombination; }
	public ComplexMatcher[] getComplexMatcher() { return cm; }
	public Selection getSelection() { return selection; }

	// simple setter
	public void setName(String name) { this.name=name; }
	public void setResolution(Resolution resolution) { this.resolution=resolution; }
	
	public String getName() {
		if (name==null){
			// generate Name
			Random r = new Random();
			name=Constants.STRATEGY+Math.abs(r.nextInt());
		}
		return name; 
	}
		
	public String toString(){
		// default string with variables
		return toString(false);
	}
	
	
	public String toString(boolean withVariables){
		String text = null;
//		if (withVariables){
//			text ="$" +  getName()+Constants.SEP+"(";
//		} else {
			text = "(";
//		}
		// resolution 1
		text+=resolution.getName()+";";
		// complex matcher or strategies
		for (int i = 0; i < cm.length; i++) {
			ComplexMatcher current = cm[i];
			if (withVariables){
				text+="$" + current.getName()+",";
			} else {
				text+=current.toString(withVariables)+",";
			}
		}
		// remove last ,
		text = text.substring(0, text.length()-1);
		// combination -> necessary if more than one function
		if (simCombination!=null){
			text+=";"+simCombination.getName();	
		}
		if (selection!=null){
			// selection -> often needed (maybe not for reuse, etc.)
			text+=";"+selection.getName();
		}
		text+=")";	
		return text;
	}
	
	
}
