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

package de.wdilab.coma.insert.relationships;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;
import de.wdilab.coma.structure.Source;

/**
 * This class import a match result of the internal format thus
 * contains meta information like source and target names and providers
 * as well as the correspondences (either path- or node-based).
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class MatchResultParser {

	// to use already loaded sources and graphs
	Manager manager = null;
	
	boolean dbInsert = false;
	
	public MatchResultParser(Manager manager){
		this.manager = manager; 
	}
	
	public MatchResultParser(Manager manager, boolean dbInsert){
		this.manager = manager; 
		this.dbInsert = dbInsert;
	}
	
	// Load match result from files as produced by matchResult.print()
	// A file may contains multiple matchResults
	public ArrayList<MatchResult> loadMatchResultFile(String fileName ) {
		ArrayList<MatchResult> matchResults = new ArrayList<MatchResult>();
		String resultName, matchInfo /*, evidence*/;
		String sourceName, targetName/*, matcherName, matcherConfig*/;
		int sourceState=-1, targetState=-1;
		Source sourceSchema = null, targetSchema = null;
		Graph sourceGraph = null, targetGraph = null;
		MatchResult matchResult = null;
		try {
			int count=0;
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line;
			boolean alreadyWarned = false;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("MatchResult")) {
					// System.out.println("Mapping start");
					if (matchResult != null && sourceGraph!=null && targetGraph!=null)
						matchResults.add(matchResult);
					matchResult = new MatchResultArray();
					sourceGraph = null;
					targetGraph = null;
					alreadyWarned = false;
				} else if (matchResult!=null){
					if (line.startsWith(" + Name:")) {
						// System.out.println("Name line");
						int i = line.indexOf(':');
						resultName = line.substring(i + 1).trim();
						if (resultName.equals(MatchResult.MAP_OB_UNKNOWN))
							resultName = null;
						matchResult.setName(resultName);
					} else if (line.startsWith(" + Info:")) {
						// System.out.println("Info line");
						int i = line.indexOf(':');
						matchInfo = line.substring(i + 1).trim();
						if (matchInfo.equals(MatchResult.MAP_OB_UNKNOWN))
							matchInfo = null;
						matchResult.setMatchInfo(matchInfo);
					} else if (line.startsWith(" + Source:")) {
						
						// System.out.println("Source line");
						int i = line.indexOf(':');
						int j = line.indexOf('|');
						int k = line.lastIndexOf('|');
						sourceName = line.substring(i + 1, j).trim();
						sourceSchema = null;
						if (!sourceName.equals(MatchResult.MAP_OB_UNKNOWN)) {
							if (j < k) {
								sourceState = Graph.stringToPreprocessing(line.substring(
										j + 1,k).trim());
								String sourceProvider = line.substring(k + 1)
										.trim();
	
								sourceSchema =manager.getSource( manager.getAccessor().getSourceId(sourceProvider));
								
								if (sourceSchema==null){
									 ArrayList<Integer> sources = manager.getAccessor().getSourceIdsWithUrl(sourceProvider);
									 if (sources!=null){
										 sourceSchema =manager.getSource(sources.iterator().next());
									 }
								}
								if (sourceSchema == null) {
									ArrayList<Source> sources = manager.getAccessor().getSourcesWithName(sourceName);
									if (sources==null) continue;
									sourceSchema =sources.iterator().next();
								}
							} else {
								sourceState = Graph.stringToPreprocessing(line.substring(
										j + 1).trim());
								sourceSchema =manager.getSource( manager.getAccessor().getSourceId(sourceName, null));
							}

	//						sourceGraph = loadSchemaGraph(sourceSchema, true, true, false);
	//						// load instances, propagate instances to parents
							// if already loaded before take that version - therefore use manager
							if (sourceGraph==null) {
								sourceGraph = manager.loadGraph(sourceSchema, true,true, true);
							}
	//						sourceGraph.printGraphInfo();
							if (sourceGraph == null) {
								System.out
										.println("loadMatchResultFile(): Error loading source schema "
												+ sourceName);
							} else {
								Graph sourceGraphTmp = sourceGraph.getGraph(sourceState);
								if (sourceGraphTmp==null){
									sourceState = Graph.stringToPreprocessing(Graph.preprocessingToString(sourceState-1));
									sourceGraph = sourceGraph.getGraph(sourceState);
								} else {
									sourceGraph = sourceGraphTmp;
								}
								matchResult.setSourceGraph(sourceGraph);
							}
						}
					} else if (line.startsWith(" + Target:") && sourceGraph!=null) {
	
						// System.out.println("Target line");
						int i = line.indexOf(':');
						int j = line.indexOf('|');
						int k = line.lastIndexOf('|');
						targetName = line.substring(i + 1, j).trim();
						targetSchema = null;
						if (!targetName.equals(MatchResult.MAP_OB_UNKNOWN)) {
							if (j < k) {
								targetState = Graph.stringToPreprocessing(line.substring(
										j + 1, k).trim());
								String targetProvider = line.substring(k + 1)
										.trim();
								
								targetSchema =manager.getSource( manager.getAccessor().getSourceId(targetProvider));
								if (targetSchema==null){
									ArrayList<Integer> targets = manager.getAccessor().getSourceIdsWithUrl(targetProvider);
									if (targets!=null) {
										targetSchema =manager.getSource(targets.iterator().next());
									}
								}
								if (targetSchema == null) {
									ArrayList<Source> sources = manager.getAccessor().getSourcesWithName(targetName);
									if (sources==null) continue;
									targetSchema =sources.iterator().next();
								}
							} else {
								targetState = Graph.stringToPreprocessing(line.substring(
										j + 1).trim());
								targetSchema =manager.getSource( manager.getAccessor().getSourceId(targetName, null));
							}

	//						targetGraph = loadSchemaGraph(targetSchema, true,true, false);
							// load instances, propagate instances to parents
							// if already loaded before take that version - therefore use manager
							if (targetGraph==null) {
								targetGraph = manager.loadGraph(targetSchema, true,true, true);
							}
	//						targetGraph.printGraphInfo();
							if (targetGraph == null) {
								System.out
										.println("loadMatchResultFile(): Error loading target schema "
												+ targetName);
								continue;
							} 
							Graph targetGraphTmp = targetGraph
									.getGraph(targetState);
							if (targetGraphTmp == null) {
								targetState = Graph
										.stringToPreprocessing(Graph
												.preprocessingToString(targetState - 1));
								targetGraph = targetGraph.getGraph(targetState);
								sourceGraph = sourceGraph.getGraph(targetState);
								matchResult.setSourceGraph(sourceGraph);
							} else {
								targetGraph = targetGraphTmp;
							}
							matchResult.setTargetGraph(targetGraph);
							
							// just for the unlikely case of having to load two different states...
							if (targetState<sourceState){
								sourceState=targetState;
								sourceGraph = sourceGraph.getGraph(targetState);
							} else if (targetState>sourceState){
								targetState=sourceState;
								targetGraph = targetGraph.getGraph(sourceState);
							}
							
						}
	//				} else if (line.startsWith(" + Matcher:")) {
	//					// System.out.println("Matcher line");
	//					int i = line.indexOf(':');
	//					matcherName = line.substring(i + 1).trim();
	//					if (matcherName.equals(MatchResult.MAP_OB_UNKNOWN))
	//						matcherName = null;
	//					matchResult.setMatcherName(matcherName);
	//				} else if (line.startsWith(" + Evidence:")) {
	//					// System.out.println("Evidence");
	//					int i = line.indexOf(':');
	//					evidence = line.substring(i + 1).trim();
	//					if (evidence.equals(MatchResult.MAP_OB_UNKNOWN))
	//						evidence = null;
	//					matchResult.setEvidence(evidence);
	//				} else if (line.startsWith(" + Config:")) {
	//					// System.out.println("Config line");
	//					int i = line.indexOf(':');
	//					matcherConfig = line.substring(i + 1).trim();
	//					if (matcherConfig.equals(MatchResult.MAP_OB_UNKNOWN))
	//						matcherConfig = null;
	//					matchResult.setMatcherConfig(MatcherConfig
	//							.parseConfig(matcherConfig));
					} else if (line.startsWith(" - ")) {
						if (!alreadyWarned && (sourceGraph==null || targetGraph==null)){
							System.out.println("source or target graph couldn't be loaded!");
							alreadyWarned = true;						
							continue;
						}
						if (sourceGraph==null || targetGraph==null){
							continue;
						}
	//					if (matchResult.getAMatchObjects()==null||
	//							matchResult.getAMatchObjects().isEmpty()){
	//						ArrayList aObjects = sourceGraph.getAllPaths();
	//						ArrayList bObjects = targetGraph.getAllPaths();
	//						matchResult.setAObjects(aObjects);
	//						matchResult.setBObjects(targetGraph.getAllPaths());
	//						matchResult.setSimMatrix(
	//								new float[aObjects.size()][bObjects.size()]);
	//					}
						count++;
						if (count%100==0){
							System.out.println("count: " + count);
						}
						
						// System.out.println("Match line");
						Pattern p = Pattern.compile(" - (.*) <-> (.*):(.*)");
						Matcher m = p.matcher(line);
						if (!m.matches()) {
							// in case there are no spaces before and after "<->" in
							// the mapping file
							p = Pattern.compile(" - (.*)<->(.*):(.*)");
							m = p.matcher(line);
						}
						if (m.matches()) {
							String sourceNamePath = m.group(1).trim();
							Object sourcePath = sourceGraph
									.nameStringToPath(sourceNamePath);						
							if (sourcePath == null && sourceNamePath.indexOf("[")<sourceNamePath.indexOf("]")){
								int id = new Integer(sourceNamePath.substring(0,sourceNamePath.indexOf("["))).intValue(); 
								sourcePath = sourceGraph.getElementWithId(id); // in fact it is a vertex							
							}
							if (sourcePath == null) {
								System.out
										.println("loadMatchResultFile(): Ignore source path "
												+ sourceNamePath
												+ " in "
												+ sourceSchema);
								continue;							
							}
	
							String targetNamePath = m.group(2).trim();
							Object targetPath = targetGraph
									.nameStringToPath(targetNamePath);
							if (targetPath == null && targetNamePath.indexOf("[")<targetNamePath.indexOf("]")){
								int id = new Integer(targetNamePath.substring(0,targetNamePath.indexOf("["))).intValue(); 
								targetPath = targetGraph.getElementWithId(id); // in fact it is a vertex							
							}
							if (targetPath == null) {
								System.out
										.println("loadMatchResultFile(): Ignore target path "
												+ targetNamePath
												+ " in "
												+ targetSchema);
								continue;
							}
							float sim = Float.parseFloat(m.group(3));
							matchResult.append(sourcePath, targetPath, sim);
						}
					}
				}
			}
			in.close();
		} catch (IOException e) {
			System.out.println("loadMatchResultFile(): Error opening file "
					+ fileName + ": " + e.getMessage());
		}
		// the last mapping
		if (matchResult != null)
			matchResults.add(matchResult);

		if (matchResults.isEmpty())
			return null;
		System.out.println(" " + matchResults.size() + " Match Results loaded from file.");
		
		if (dbInsert){
			for (int i = 0; i < matchResults.size(); i++) {
				MatchResult current = matchResults.get(i);
				manager.getImporter().saveMatchResult(current);
			}
			System.out.println(" " + matchResults.size() + " Match Results saved to repository.");
		}
		return matchResults;
	}
}
