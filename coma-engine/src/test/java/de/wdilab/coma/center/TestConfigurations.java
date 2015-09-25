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

package de.wdilab.coma.center;

import java.io.File;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.insert.metadata.OWLParser_V3;
import de.wdilab.coma.insert.metadata.XDRParser;
import de.wdilab.coma.insert.relationships.MatchResultParser;
import de.wdilab.coma.matching.Combination;
import de.wdilab.coma.matching.ComplexMatcher;
import de.wdilab.coma.matching.Constants;
import de.wdilab.coma.matching.Matcher;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Selection;
import de.wdilab.coma.matching.Strategy;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.matching.execution.ExecWorkflow;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.TRepository;
import de.wdilab.coma.structure.EvaluationMeasure;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;

/**
 * @author Sabine Massmann
 */
public class TestConfigurations {

			// source	// target	// mapping
	static String[][] DATA_SCHEMA = {
			// PO small
			{"CIDXPOSCHEMA", "Apertum", "CIDXPOSCHEMA_Apertum"},
			{"CIDXPOSCHEMA", "Excel", "CIDXPOSCHEMA_Excel"},
			{"CIDXPOSCHEMA", "Noris", "CIDXPOSCHEMA_Noris"},
			{"CIDXPOSCHEMA", "Paragon", "CIDXPOSCHEMA_Paragon"},
			{"Excel", "Apertum", "Excel_Apertum"},
			{"Excel", "Noris", "Excel_Noris"},
			{"Excel", "Paragon", "Excel_Paragon"},
			{"Noris", "Apertum", "Noris_Apertum"},
			{"Noris", "Paragon", "Noris_Paragon"},
			{"Paragon", "Apertum", "Paragon_Apertum"},
	};
	
	static String[][] DATA_ONTOLOGY_PATH = {
			// WebDirectory small
			{"dmoz_Freizeit", "Google_Freizeit", "Freizeit" },
			{"Google_Lebensmittel", "web_Lebensmittel", "Lebensmittel" },		
	};

	
	static int[] CONFIGURATION = {
		Matcher.STATISTICS,
		Matcher.DATATYPE,
		Matcher.NAME,			
		ComplexMatcher.NAMETYPE,
		ComplexMatcher.NAMESTAT,
		ComplexMatcher.PATH, // uses Matcher.PATH
		ComplexMatcher.LEAVES,
		ComplexMatcher.PARENTS,
		ComplexMatcher.SIBLINGS,
		ComplexMatcher.CHILDREN,
		ComplexMatcher.SUCCESSORS,
		Strategy.COMA_OPT,
		Strategy.COMA,
	};
	
	
	 static boolean matchAllContext = true; 
//	 static boolean matchAllContext = false;

//	 static boolean matchNode = true;
	 static boolean matchNode = true;

//	 static Selection SELECTION = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, 0.01f, 0.4f);
	 static Selection SELECTION = new Selection(Selection.DIR_BOTH, Selection.SEL_MAXN, 1);
//	 static Selection SELECTION = new Selection(Selection.DIR_BOTH, Selection.SEL_THRESHOLD, 0);
	 
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TRepository.setDatabaseProperties();
		
		Manager manager = new Manager();
		DataAccess accessor = manager.getAccessor();	
		accessor.dropRepositorySchema();
		accessor.createRepositorySchema();
		
		String directory = (new File ("")).getAbsolutePath() + "\\resources\\Sources\\";
		
		boolean dbInsert = true;

		// Import small PO
		XDRParser xdrPar = new XDRParser(dbInsert);	
		xdrPar.parseMultipleSources(directory+"PO_xdr");	
		
		OWLParser_V3 owlPar = new OWLParser_V3(dbInsert);
		owlPar.parseMultipleSources(directory+"webdirectories");
		
		manager.loadRepository();
		MatchResultParser mrParser = new MatchResultParser(manager, dbInsert);
		mrParser.loadMatchResultFile(directory+"Mappings/mappings-PO.txt");
		mrParser.loadMatchResultFile(directory+"Mappings/mappings-WebDir.txt");
		
		manager.loadRepository();
		System.out.println("*************************************************************");
		
		matchSchemas(manager,accessor);
		
		System.out.println("DONE.");
	}

	
	public static MatchResult loadMatchResult(Manager manager, DataAccess accessor,
			String sourceName, String targetName, int graphPreprocessing, String resultName){
		int sourceId = (Integer) accessor.getSourceIdsWithName(sourceName).get(0);
		int targetId = (Integer) accessor.getSourceIdsWithName(targetName).get(0);
		int sourceRelId = accessor.getSourceRelIdWithName(sourceId, targetId, resultName);
		MatchResult result = manager.loadMatchResult(sourceId, targetId, sourceRelId);
		result = MatchResult.transformMatchResult(result, graphPreprocessing);
		return result;
	}
	

	public static Strategy getStrategy(int id, boolean path, Selection selection){
		Strategy cs = null;
		if (Constants.getClass(id).equals(Strategy.class)){
			 cs = new Strategy(id);
		} else{
			ComplexMatcher config = null;		
			if (Constants.getClass(id).equals(ComplexMatcher.class)){
				// use directly in Strategy
				config = new ComplexMatcher(id); 
			} else if (Constants.getClass(id).equals(Matcher.class)){
				// build ComplexMatcher
				config = new ComplexMatcher(Resolution.RES2_SELFNODE, new Matcher(id), Combination.SET_AVERAGE); 
			}
			if (path){
				cs = new Strategy(Resolution.RES1_PATHS, config, selection);
			} else {
				cs = new Strategy(Resolution.RES1_NODES, config, selection);
			}
			
		}
		return cs;
	}

	static void matchSchemas(Manager manager, DataAccess accessor){
			int graphState = Graph.PREP_SIMPLIFIED;
			MatchResult[] intendedResults = new MatchResult[DATA_SCHEMA.length];
			for (int i = 0; i < DATA_SCHEMA.length; i++) {
				String sourceName = DATA_SCHEMA[i][0];
				String targetName = DATA_SCHEMA[i][1];
				String resultName = DATA_SCHEMA[i][2];
				MatchResult intendedResult = loadMatchResult(manager, accessor, sourceName, targetName, graphState, resultName);
				System.out.println("***\t" + sourceName + "\t" +  targetName + "\t" + resultName + "\t" + intendedResult.getMatchCount());	
				intendedResults[i]=intendedResult;
			}
			
			int graphState2 = Graph.PREP_RESOLVED;
			MatchResult[] intendedResults2 = new MatchResult[ DATA_ONTOLOGY_PATH.length];
			for (int i = 0; i < DATA_ONTOLOGY_PATH.length; i++) {
				String sourceName = DATA_ONTOLOGY_PATH[i][0];
				String targetName = DATA_ONTOLOGY_PATH[i][1];
				String resultName = DATA_ONTOLOGY_PATH[i][2];
				MatchResult intendedResult = loadMatchResult(manager, accessor, sourceName, targetName, graphState2, resultName);
				System.out.println("***\t" + sourceName + "\t" +  targetName + "\t" + resultName + "\t" + intendedResult.getMatchCount());	
				intendedResults2[i]=intendedResult;
			}
			
			System.out.println("\n******************************************************************************\n");

			 Workflow w = new Workflow();
//			 ExecWorkflow exec = new ExecWorkflow();
			 ExecWorkflow exec = manager.getExecWorkflow();
				StringBuffer allContextBuffer = new StringBuffer();		
				allContextBuffer.append("Name\n\tIntended\tPrecision\tRecall\tFmeasure\n");
				
			for (int j = 0; j < CONFIGURATION.length; j++) {
						
				MatchResult[] allContext = new MatchResult[intendedResults.length];
				MatchResult[] allContext2 = new MatchResult[intendedResults2.length];
				// create Strategy, ComplexMatcher or ComplexMatcher with Matcher "inside"
				int current = CONFIGURATION[j];
				Strategy strategy = getStrategy(current,true , SELECTION);
				 w.setBegin(strategy);
				System.out.println(strategy.toString(false));
				allContextBuffer.append("\n"+strategy.toString(false)+"\n");

				for (int i = 0; i < intendedResults.length; i++) {
					MatchResult intendedResult = intendedResults[i];

					 w.setSource(intendedResult.getSourceGraph());
					 w.setTarget( intendedResult.getTargetGraph());
						// AllContext
						allContext[i] = exec.execute(w)[0]; 			
						EvaluationMeasure measures = intendedResult.compare(allContext[i]);
						if (measures!=null){
							allContextBuffer.append(intendedResult.getName() + "\t" + intendedResult.getMatchCount()+ "\t"+ allContext[i].getMatchCount()+ "\t"+
									measures.getPrecision()+"\t"+measures.getRecall()+"\t"+measures.getFmeasure()+ "\n");
						} else {
							allContextBuffer.append(intendedResult.getName() + "no results\n");
						}		
				}
				
				for (int i = 0; i < intendedResults2.length; i++) {
					MatchResult intendedResult = intendedResults2[i];
					 w.setSource(intendedResult.getSourceGraph());
					 w.setTarget( intendedResult.getTargetGraph());
					 
						// AllContext
						allContext2[i] = exec.execute(w)[0]; 
						EvaluationMeasure measures = intendedResult.compare(allContext2[i]);
						if (measures!=null){
							allContextBuffer.append(intendedResult.getName() + "\t" + intendedResult.getMatchCount()+ "\t"+ allContext2[i].getMatchCount()+ "\t"+
									measures.getPrecision()+"\t"+measures.getRecall()+"\t"+measures.getFmeasure()+ "\n");
						} else {
							allContextBuffer.append(intendedResult.getName() + "no results\n");
						}				
				}				
			}
		
				System.out.println(allContextBuffer.toString().replace(".", ","));
		}

}
