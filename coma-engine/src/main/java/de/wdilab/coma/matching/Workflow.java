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
import java.util.List;
import java.util.Random;

import de.wdilab.coma.matching.validation.TreeToWorkflow;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;

/**
 * Workflow realizes the application of one Strategy 
 * or sequence of several Strategies and their combination 
 * or the sequence of a Strategy and a Strategy
 * To be executed a source and target model are needed.
 * 
 * @author Sabine Massmann
 */
public class Workflow{
	  // start counting 8000 Manager.WORK_CNT
	  public static final int ALLCONTEXT = Constants.WORK_CNT + 1;
	  public static final int FILTEREDCONTEXT = Constants.WORK_CNT + 2;
	  public static final int FRAGMENTBASED = Constants.WORK_CNT + 3;
	  public static final int OPTIMISTIC = Constants.WORK_CNT + 4;
	  public static final int NODES = Constants.WORK_CNT + 5;
	  
	  public static final int NODES_NAME = Constants.WORK_CNT + 6;
	  public static final int NODES_PATH = Constants.WORK_CNT + 7;
	  
	  public static final int ALLCONTEXT_INST = Constants.WORK_CNT + 8;
	 
		public static final Integer[] WORKFLOW = {
			ALLCONTEXT, ALLCONTEXT_INST, FILTEREDCONTEXT, FRAGMENTBASED, // OPTIMISTIC
			NODES, NODES_NAME, NODES_PATH
		};
		public static final List<Integer> WORKFLOW_LIST = Arrays.asList(WORKFLOW);		  
	  
		public static final String DEF_STRATEGY = "default_strategy";
		public static final String DEF_COMPLEXMATCHER = "default_complexmatcher";
		public static final String DEF_MATCHER = "default_matcher";
		
		
		
	// start elements
	Graph source;
	Graph target;
	String name;
	// type 1: complex strategy
	// type 2: several complex strategies with optional combination (independently)
	// type 3: complex strategy, complex strategy (second works with output of first as input)
	Strategy[] strategies;
	Strategy secondStrategy=null;
	Combination combination;
	
	ArrayList srcSelected = null;
	ArrayList trgSelected = null;
	MatchResult selected = null;
	
	boolean useSynAbb = false;
	
	// Constructor
	public Workflow(){
	}
		
	// Constructor of a pre-defined workflow
	public Workflow (int workflow){
		// create one of the default workflow
		switch (workflow) {
		case ALLCONTEXT :	// $AllContextWorkflow=($ComaOptStrategy)
			// COMA_OPT includes already resolution1 and selection
			strategies = new Strategy[1];
			strategies[0] = new Strategy(Strategy.COMA_OPT);
//			cstrategies[0] = new Strategy(Strategy.NODES);
//			cstrategies[0] = new Strategy(Strategy.CONTEXT);			
			setName("AllContextW");
			break;
		case ALLCONTEXT_INST :	// $AllContextWorkflow=($ComaOptInstanceStrategy)
			// COMA_OPT includes already resolution1 and selection
			strategies = new Strategy[1];
			strategies[0] = new Strategy(Strategy.COMA_OPT_INST);
//			cstrategies[0] = new Strategy(Strategy.NODES);
//			cstrategies[0] = new Strategy(Strategy.CONTEXT);			
			setName("AllContextInstW");
			break;
		case FILTEREDCONTEXT :	// $FilteredContextWorkflow=($NodeSelectionStrategy,$UpPathSelectionStrategy)			
			// TODO: automatically transfer the selected node pairs
			// Question: how to differentiate between limitation and extension from given result???
			strategies = new Strategy[1];
			strategies[0] = new Strategy(Strategy.NODE_SELECTION);			
			secondStrategy = new Strategy(Strategy.UPPATH_SELECTION);
			setName("FilteredContextW");
			break;
		case FRAGMENTBASED :
			strategies = new Strategy[1];
			strategies[0] = new Strategy(Strategy.FRAG_SELECTION);			
			secondStrategy = new Strategy(Strategy.DOWNPATH_SELECTION);			
			// TODO: automatically transfer result back to path from root
			setName("FragmentBasedW");
			break;
		case OPTIMISTIC:
			strategies = new Strategy[3];
			strategies[0] = new Strategy(Strategy.COMA);
			strategies[1] = new Strategy(Strategy.COMA_OPT);
			strategies[2] = new Strategy(Strategy.SIMPLE_NAMEPATH);
			combination = new Combination(Combination.RESULT_MERGE);
			setName("OptimisticW");
			break;
		case NODES:
			strategies = new Strategy[1];
			strategies[0] = new Strategy(Strategy.NODES);
//			combination = new Combination(Combination.RESULT_MERGE);
			setName("OnlyNodesW");
			break;
		case NODES_NAME:
			strategies = new Strategy[1];			
			strategies[0] = new Strategy(Strategy.NODES_NAME);
			setName("NodesNameW");
			break;
		case NODES_PATH:
			strategies = new Strategy[1];			
			strategies[0] = new Strategy(Strategy.NODES_PATH);
			setName("NodesPathW");
			break;
		default :
			System.out.println("Workflow id not known " + workflow);
			break;
		}
	}
	
	// simple getter
	public Graph getSource() { return source; }
	public Graph getTarget() { return target; }
	public Strategy[] getBegins() { return strategies; }
	public Strategy getSecondStrategy() { return secondStrategy; }
	public Combination getCombination() { return combination; }
	public ArrayList getSourceSelected() { return srcSelected; }
	public ArrayList getTargetSelected() { return trgSelected; }
	public MatchResult getSelected() { return selected; }
	
	// simple setter
	public void setSource(Graph source) { this.source = source; }
	public void setTarget(Graph target) { this.target = target; }
	public void setName(String name) { this.name=name; }
	public void setBegins(Strategy[] cstrategies) { this.strategies= cstrategies; }
	public void setSecondStrategy(Strategy secondStrategy) { this.secondStrategy = secondStrategy; }
	public void setCombination(Combination combination) { this.combination = combination; }
	public void setSourceSelected(ArrayList srcSelected) { this.srcSelected = srcSelected; }
	public void setTargetSelected(ArrayList trgSelected) { this.trgSelected = trgSelected; }
	public void setSelected(MatchResult selected) { this.selected= selected; }
	
	public boolean useSynAbb() { return useSynAbb; }
	public void setUseSynAbb(boolean useSynAbb) { this.useSynAbb = useSynAbb; }
	
	public void clear(){
		strategies=null;
		secondStrategy=null;
	}
	
	public void addBegin(Strategy strategy) { 
		if (strategies==null){
			strategies = new Strategy[1];
			strategies[0] = strategy;
		} else{
			Strategy[] tmp = new Strategy[strategies.length+1];
			for (int i = 0; i < strategies.length; i++) {
				tmp[i] = strategies[i];
			}
			tmp[strategies.length]=strategy;
			strategies=tmp;
		}
	}
	
	public void setBegin(Strategy strategy) { 
		strategies = new Strategy[1];
		strategies[0] = strategy;
	}
	
	public String getName() {
		if (name==null){
			// generate Name
			Random r = new Random();
			name=Constants.WORKFLOW+Math.abs(r.nextInt());
		}
		return name; 
	}
	
	
	public boolean isExecutable(){
		if (source==null || target==null){
			// no source or target means the workflow can't be executed
			return false;
		}
		// workflow has to be valid
		String value = this.toString(false);
		return TreeToWorkflow.isValidWorkflowVariable(value);
	}
		
	public String toString(){
		// default string with variables
		return toString(true);
	}
	
	public String toString(boolean withVariables){
		if (strategies==null){
			return null;
		}
		String text = null;
//		if (withVariables){
//			text = "$" + getName()+Constants.SEP+"(";
//		} else {
			text = "(";
//		}
		if (strategies.length==1){
			// 1. case: only one Strategy
			Strategy strategy = strategies[0];
			if (withVariables){
				text+="$" + strategy.getName();
			} else {
				text+=strategy.toString(withVariables);
			}
			if (secondStrategy!=null){
				// 3. case: Strategy; Strategy
				if (withVariables){
					text+=";$" + secondStrategy.getName();
				} else {
					text+=";" + secondStrategy.toString(withVariables);
				}
			}
		} else if (secondStrategy==null){
				// 2. case: independently Strategy
			for (int i = 0; i < strategies.length; i++) {
				Strategy strategy = strategies[i];
				if (withVariables){
					text+="$" + strategy.getName()+",";
				} else {
					text+=strategy.toString(withVariables)+",";
				}
			}
			// remove last ;
			text = text.substring(0, text.length()-1);
			if (combination!=null){
				// combination exists
				text+= ";" + combination.getName();
			}
		}
		text+=")";	
		return text;
	}
	
	public String toStringWithDefault(){
		if (strategies==null){
			return null;
		}
		String text = "(";
		if (strategies.length==1){
			// 1. case: only one Strategy
			text+=DEF_STRATEGY;
			if (secondStrategy!=null){
				// 3. case: Strategy; Strategy
					text+=";" + DEF_STRATEGY;
			}
		} else if (secondStrategy==null){
				// 2. case: independently Strategy
			for (int i = 0; i < strategies.length; i++) {
					text+="$" + DEF_STRATEGY+",";
			}
			// remove last ;
			text = text.substring(0, text.length()-1);
			if (combination!=null){
				// combination exists
				text+= ";" + combination.getName();
			}
		}
		text+=")";	
		return text;
	}
	
//	public void load(String file){
//		try{
//			FileReader fr=new FileReader(file);
//			BufferedReader br = new BufferedReader(fr); 
//			String line="",text="";
//			while((line=br.readLine())!=null){
//				if(text.length()>0 && text.endsWith(";")){
//					text+=line;
//				}
//				else if(text.length()>0){
//					text+=";"+line;
//				}
//				else
//					text=line;
//			}
////			String[] workflowParts=
//				seperateParts(text);
//			//Start mit letztem Part -> neuen Matcher o.ae. erstellen und in den anderen Parts ersetzen
//			//Workflow erstellen, nachdem alle kleineren Parts aufgeloest sind
//		}catch(IOException e){
//			e.printStackTrace();
//		}
//	}
	
//	public static String[] seperateParts(String text){
//		int count=-1,Openparenthesis=0,Closeparenthesis=0;
//		for(int i=0;i<text.length();i++){
//			if(text.charAt(i)=='(')
//				Openparenthesis++;
//			if(text.charAt(i)==')')
//				Closeparenthesis++;
//		}
//		if(Openparenthesis==Closeparenthesis){
//			int[] starts=new int[Openparenthesis];
//			int[] ends=new int[Openparenthesis];
//			String[] parts=new String[Openparenthesis];
//			for(int i=0;i<text.length();i++){
//				if(text.charAt(i)=='('){
//					starts[count]=i;
//					count++;
//				}	
//				if(text.charAt(i)==')'){
//					ends[count]=i;
//					count--;
//				}	
//			}
//			if(starts[0]==0){
//				System.out.println("Invalid workflow: Unknown Workflowpart Symbol at the beginning! Should be @ for starting workflow!");
//				return null;
//			}
//			for(int i=0;i<starts.length;i++){
//				if("!$%&?*+~#".contains(text.substring(starts[i]-1,starts[i])))
//						parts[i]=text.substring(starts[i]-1,ends[i]+1);
//				else{
//					System.out.println("Invalid workflow: Unknown Workflowpart Symbol "+text.substring(starts[i]-1,starts[i])+"! Should be one of \"+#*@\"");
//					return null;
//				}
//			}
//			return parts;
//		}
//		else{
//			System.out.println("Invalid workflow: incorrect number of parenthesis!");
//			return null;
//		}
//	}
	
	public static void insertMatcher(Matcher matcher, DataImport importer){
		if (importer.existWorkflowVariable("$" + matcher.getName())) return;
		importer.insertWorkflowVariable("$" + matcher.getName(), matcher.toString(true));
	}
	
	public static void insertComplexMatcher(ComplexMatcher cmatcher, DataImport importer){
		if (importer.existWorkflowVariable("$" + cmatcher.getName())) return;
		importer.insertWorkflowVariable("$" + cmatcher.getName(), cmatcher.toString(true));
		Object[] c_cm = cmatcher.getMatcherAndComplexMatcher();
		for (int j = 0; j < c_cm.length; j++) {
			if (c_cm[j] instanceof Matcher){
				insertMatcher((Matcher)c_cm[j], importer);
			} else if (c_cm[j] instanceof ComplexMatcher){
				insertComplexMatcher((ComplexMatcher)c_cm[j], importer);
			}
		}		
	}
	
	
	public static void insertStrategy(Strategy strategy, DataImport importer){
		if (importer.existWorkflowVariable("$" + strategy.getName())) return;
		importer.insertWorkflowVariable("$" + strategy.getName(), strategy.toString(true));
		ComplexMatcher[] cmatcher = strategy.getComplexMatcher();
		for (int j = 0; j < cmatcher.length; j++) {
			insertComplexMatcher(cmatcher[j], importer);
		}
	}
	
	
	public static void insertDefaults(DataImport importer){
		for (int i = 0; i < Matcher.MATCHER.length; i++) {
			Matcher matcher = new Matcher(Matcher.MATCHER[i]);
			insertMatcher(matcher , importer);
		}
		for (int i = 0; i < ComplexMatcher.COMPLEXMATCHER.length; i++) {
			ComplexMatcher cmatcher = new ComplexMatcher(ComplexMatcher.COMPLEXMATCHER[i]);
			insertComplexMatcher(cmatcher, importer);
		}
		for (int i = 0; i < Strategy.STRATEGY.length; i++) {
			Strategy strategy = new Strategy(Strategy.STRATEGY[i]);
			insertStrategy(strategy, importer);
		}	
		for (int i = 0; i < WORKFLOW.length; i++) {
			Workflow workflow = new Workflow(WORKFLOW[i]);
			importer.insertWorkflowVariable("$" + workflow.getName(), workflow.toString(true));
		}
	}
	
	public static void main(String[] args) {
//		Workflow w = new Workflow();
//		Resolution node = w.addResolution(Resolution.RES_I_PATHS);
//		Strategy strat1 = w.addStrategy(node, -1);
//		Strategy strat2 = w.addStrategy(node, -2);
//		Combination comb = w.addCombination(strat1, Combination.COM_AVERAGE);
//		w.attach(strat2, comb);
////		Vertex sel = 
//			w.addSelection(comb, -1);
//		
	}
	
}
