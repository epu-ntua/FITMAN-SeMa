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

package de.wdilab.coma.integration;

import org.semanticweb.owl.align.AlignmentException;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.insert.metadata.*;
import de.wdilab.coma.matching.*;
import de.wdilab.coma.matching.execution.ExecWorkflow;
import de.wdilab.coma.structure.EvaluationMeasure;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides a small API for the usage of the
 * COMA Community Edition. It can be as well seen as
 * an example how to execute certain functionalities.
 * 
 * @author Sabine Massmann
 */
public class COMA_API {
	
	Manager manager = null;

	
	public COMA_API(){
		this.manager = new Manager();
	}
	
	// function: given two uri and match workflow, load and match schemas/ontologies, return match result 
	
	/**
         * @param source source file/url
         * @param target target file/url
         * @param resolution expected Resolution.RES2_XXX (currently SELFNODE, PARENTS, CHILDREN, LEAVES, SIBLINGS, SELFPATH)
         * @param similaritymeasure SimilarityMeasure (should start with SIM_STR_ or SIM_DOC_ )
         * @return match result
         * function: given two uri, load and match schemas/ontologies (default/automatic configuration), return match result
         */
        public MatchResult matchModels(String source, String target, int resolution, int similaritymeasure) {
    		Graph graphSrc = loadGraph(source, null);
    		Graph graphTrg = loadGraph(target, null);
    		ExecWorkflow exec = new ExecWorkflow();
    		
    		if (Resolution.getType(resolution)!=Resolution.TYPE_RES2){
    			System.err.println("COMA_API.matchModels wrong resolution type (not type 2) " + resolution);
    			return null;
    		}
    			
    		Matcher matcher = null;
      		ComplexMatcher cm = null;
    		switch (resolution) {
			case Resolution.RES2_SELFNODE:
			case Resolution.RES2_PARENTS:
			case Resolution.RES2_CHILDREN:
			case Resolution.RES2_LEAVES:
			case Resolution.RES2_SIBLINGS:
				matcher = new Matcher(Resolution.RES3_NAME, similaritymeasure, Combination.SET_AVERAGE);
				break;
			case Resolution.RES2_SELFPATH:
				matcher = new Matcher(Resolution.RES3_PATH, similaritymeasure, Combination.SET_AVERAGE);				
				break;
			default:
				System.err.println("COMA_API.matchModels unexpected resolution " + resolution);
				return null;
			}
    		
    		cm = new ComplexMatcher(resolution,matcher,Combination.SET_AVERAGE);
    		
    		int res1 = -1;
    		if (graphSrc.getSource().getType()== Source.TYPE_ONTOLOGY
    				|| graphTrg.getSource().getType()== Source.TYPE_ONTOLOGY){
    			res1 = Resolution.RES1_NODES;
    		} else {
    			res1 = Resolution.RES1_PATHS;
    		}
  
    		Selection selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, (float)0.01, (float)0.1);    		
    		Strategy strategy = new Strategy(res1, cm, selection);
    		if (graphSrc.getSource().getType()!= Source.TYPE_ONTOLOGY
    				&& graphTrg.getSource().getType()!= Source.TYPE_ONTOLOGY){
    			graphSrc = graphSrc.getGraph(Graph.PREP_SIMPLIFIED);
    			graphTrg = graphTrg.getGraph(Graph.PREP_SIMPLIFIED);
    		}
    		Workflow workflow = new Workflow();		
    		
    		workflow.setSource(graphSrc);
    		workflow.setTarget(graphTrg);
    		workflow.setBegin(strategy);
    		MatchResult[] results =  exec.execute(workflow);
    		if (results==null){
    			System.err.println("COMA_API.matchModelsDefault results unexpected null");
    			return null;
    		}
    		if (results.length>1){
    			System.err.println("COMA_API.matchModelsDefault results unexpected more than one, only first one returned");
    		}
    		return results[0];
        }
        
	/**
	 * @param fileSrc source file/url
	 * @param fileTrg target file/url
	 * @return match result
	 * function: given two uri, load and match schemas/ontologies (default/automatic configuration), return match result
	 */
	public MatchResult matchModelsDefault(String fileSrc, String fileTrg){
		Graph graphSrc = loadGraph(fileSrc, null);
		Graph graphTrg = loadGraph(fileTrg, null);
		ExecWorkflow exec = new ExecWorkflow();
		Strategy strategy = new Strategy(Strategy.COMA_OPT);
		if (graphSrc.getSource().getType()== Source.TYPE_ONTOLOGY
				|| graphTrg.getSource().getType()== Source.TYPE_ONTOLOGY){
			strategy.setResolution( new Resolution(Resolution.RES1_NODES));
		} else {
			graphSrc = graphSrc.getGraph(Graph.PREP_SIMPLIFIED);
			graphTrg = graphTrg.getGraph(Graph.PREP_SIMPLIFIED);
		}
		Workflow workflow = new Workflow();		
		
		workflow.setSource(graphSrc);
		workflow.setTarget(graphTrg);
		workflow.setBegin(strategy);
		MatchResult[] results =  exec.execute(workflow);
		if (results==null){
			System.err.println("COMA_API.matchModelsDefault results unexpected null");
			return null;
		}
		if (results.length>1){
			System.err.println("COMA_API.matchModelsDefault results unexpected more than one, only first one returned");
		}
		return results[0];
	}
	
	/**
	 * @param fileSrc source file/url
	 * @param fileTrg target file/url
	 * @param fileAbbreviations abbreviation file/url
	 * @param fileSynonyms synonyms file/url
	 * @return match result
	 * function: given two uri, load and match schemas/ontologies (default/automatic configuration)
	 * use also the given abbreviations and synonyms, return match result
	 */
	public MatchResult matchModelsDefault(String fileSrc, String fileTrg, String fileAbbreviations, String fileSynonyms){
		Graph graphSrc = loadGraph(fileSrc, null);
		Graph graphTrg = loadGraph(fileTrg, null);
		
		// load abbreviations to lists
		ListParser parser = new ListParser(false);
		parser.parseSingleSource(fileAbbreviations);
		ArrayList<String> abbrevList = parser.getList1();
		ArrayList<String> fullFormList =  parser.getList2();
		// load synonyms to lists		
		parser.parseSingleSource(fileSynonyms);
		ArrayList<String> wordList = parser.getList1();
		ArrayList<String> synonymList = parser.getList2();
		
		// init ExecWorkflow with abbreviatons and synonyms
		ExecWorkflow exec = new ExecWorkflow(abbrevList, fullFormList, wordList, synonymList);
		Strategy strategy = new Strategy(Strategy.COMA_OPT);
		if (graphSrc.getSource().getType()== Source.TYPE_ONTOLOGY
				|| graphTrg.getSource().getType()== Source.TYPE_ONTOLOGY){
			strategy.setResolution( new Resolution(Resolution.RES1_NODES));
		} else {
			graphSrc = graphSrc.getGraph(Graph.PREP_SIMPLIFIED);
			graphTrg = graphTrg.getGraph(Graph.PREP_SIMPLIFIED);
		}
		Workflow workflow = new Workflow();		
		
		workflow.setSource(graphSrc);
		workflow.setTarget(graphTrg);
		workflow.setBegin(strategy);
		workflow.setUseSynAbb(true);
		MatchResult[] results =  exec.execute(workflow);
		if (results==null){
			System.err.println("COMA_API.matchModelsDefault results unexpected null");
			return null;
		}
		if (results.length>1){
			System.err.println("COMA_API.matchModelsDefault results unexpected more than one, only first one returned");
		}
		return results[0];
	}
	
	/**
	 * @param file
	 * @param name short name of the Graph
	 * @return graph tree
	 * function: given a uri, transform schema/ontology to internal structure, return tree
	 */
	public Graph loadGraph(String file, String name){
		if (file==null){
			System.out.println("COMA_API.loadGraph Error file is null");
			return null;
		}
		boolean insertDB = false;
		String filetype = file.toLowerCase();
		filetype = filetype.substring(filetype.lastIndexOf("."));
		InsertParser par = null;
		if (filetype.equals(InsertParser.XSD)){
			par = new XSDParser(insertDB);
		} else if (filetype.equals(InsertParser.XDR)){
			par = new XDRParser(insertDB);
		} else if (filetype.equals(InsertParser.CSV)){
			par = new CSVParser(insertDB);
		} else if (filetype.equals(InsertParser.SQL)){
			par = new SQLParser(insertDB);
		} else if (filetype.equals(InsertParser.OWL) || filetype.equals(InsertParser.RDF)){
			par = new OWLParser_V3(insertDB);
		}
		
		if (par==null){
			System.out.println("COMA_API.loadGraph Error filetype not recognized");
			return null;
		}
		
		par.parseSingleSource(file);
		Graph graph = par.getGraph();
		return graph;
	}
	
	public MatchResult loadMatchResult(String file){
		if (file==null){
			System.out.println("COMA_API.loadMatchResult Error file is null");
			return null;
		}		
		MatchResult result = null;
		
		// TODO
		
		return result;
	}
	
	public EvaluationMeasure compare(MatchResult intendedResult, MatchResult testResult){
		return intendedResult.compare(testResult);
	}
	
	public EvaluationMeasure compare(String referenceAlignment, String testAlignment){
		org.semanticweb.owl.align.Alignment align1 = null;
		org.semanticweb.owl.align.Alignment align2 = null;				
		System.out.println(referenceAlignment);
		System.out.println(testAlignment);
		
		AlignmentParser alignParser = new AlignmentParser(0);
		try {
			align1 = alignParser.parse(referenceAlignment.replace(" ", "%20"));
			align2 = alignParser.parse(testAlignment.replace(" ", "%20"));

			return compare(align1, align2);
		} catch (AlignmentException e) {
			System.out.println("COMA_API.compare Error AlignmentException " + e.getMessage());
		}
		return null;
	}
	
	public EvaluationMeasure compare(org.semanticweb.owl.align.Alignment referenceAlignment, org.semanticweb.owl.align.Alignment testAlignment){
		PRecEvaluator pe = null;
			
		try {
			pe = new PRecEvaluator(referenceAlignment, testAlignment);
			pe.eval(null);
			
		} catch (AlignmentException e) {
			System.out.println("COMA_API.compare Error AlignmentException " + e.getMessage());
			return null;
		}
		EvaluationMeasure measure = new EvaluationMeasure(pe.getExpected(), pe.getFound(), pe.getCorrect());
		return measure;
	}
	
    
        /**
         * 
         * @param configStr
         * @return int[] of res1, res3
         */
        public int[] configStringToResolutionConstants(String configStr) {
            if (!configStr.contains(":")) {
                System.out.println("COMA_API.configStringToResolutionConstants Error configStr invalid"); 
                return null;
            }
            String[] pars = configStr.split(":");
            //System.out.println(pars[0] + " " + pars[1]);
            int rs = (this.getConstantsAsMap(Resolution.class)).get(pars[0]);
            int sm = (this.getConstantsAsMap(SimilarityMeasure.class)).get(pars[1]);
            System.out.println(configStr + " --> " + rs + " " + sm);
            return new int[]{rs, sm};
        }
        
        public HashMap<String,Integer> getConstantsAsMap(Class c) {
            HashMap<String,Integer> consts = new HashMap<String, Integer>();
            //Class<SimilarityMeasure> c = SimilarityMeasure.class;
            //for (Field f : c.getDeclaredFields()) {
            for (Field f : c.getFields()) { // of type constant?
                try {
                    //consts.put(f.getName(), f.getInt(f));
                    if (f.get(f) instanceof Integer) {
                        consts.put(f.getName(), f.getInt(f));
                    }
                } catch (IllegalArgumentException ex) {
                    //Logger.getLogger(COMA_API.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("COMA_API.getConstantsAsMap Error IllegalArgumentException " + ex.getMessage());
                    return null;
                } catch (IllegalAccessException ex) {
                    //Logger.getLogger(COMA_API.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("COMA_API.getConstantsAsMap Error IllegalAccessException " + ex.getMessage());
                    return null;
                }
            }
            //System.out.println(consts);
            return consts;
               
        }    
        
}
