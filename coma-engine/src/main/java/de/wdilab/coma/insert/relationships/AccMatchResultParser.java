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
import java.util.Date;

import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This class imports a alignment that contains for each correspondence 
 * between two ontologies only the accession (unique identifier) of the 
 * two elements.
 * The source and target models have to be given as input because the 
 * accession file doesn't contain that information.
 * 
 * @author Sabine Massmann
 */
public class AccMatchResultParser {
	DataImport importer=null; 
	
	int srcId = -1, trgId = -1, mappingId = -1;	
	MatchResult result = null;
	
	// if false return match result otherwise insert directly into database
	boolean dbInsert = false;
	
	public AccMatchResultParser(DataImport importer, boolean dbInsert){
		this.importer = importer;
		this.dbInsert = dbInsert;
	}
	public MatchResult loadMatchResultFile(String fileName, Graph srcGraph, Graph trgGraph, int graphState) {
		String resultName = getResultName(fileName);
		srcGraph = srcGraph.getGraph(graphState);
		trgGraph = trgGraph.getGraph(graphState);
		if (dbInsert){
			this.srcId = srcGraph.getSource().getId();
			this.trgId = trgGraph.getSource().getId();
			String date = new Date().toString();
			mappingId = importer.getSourceRelId(srcId, trgId,SourceRelationship.REL_MATCHRESULT,
					resultName);
			if (mappingId == -1) {
				mappingId = importer.insertSourceRel(srcId, trgId, SourceRelationship.REL_MATCHRESULT,
						 resultName, null, fileName, graphState, date);
			} else {
				System.out.println("saveMatchResult(): Error saving already existing match result "
								+ resultName + " with id " + mappingId);
				return null;
			}			
			importer.updateSourceRel(mappingId, Repository.STATUS_IMPORT_STARTED);
		}
		loadMatchResultFile(fileName, srcGraph, trgGraph);
		
		if (dbInsert){
			importer.updateSourceRel(mappingId, Repository.STATUS_IMPORT_DONE);
		}
		return result;		
	}	
	
	private String getResultName(String fileName){
		String resultName = fileName;
		if (resultName.contains("/")){
			resultName = resultName.substring(resultName.lastIndexOf("/")+1);
		}
		if (resultName.contains("\\")){
			resultName = resultName.substring(resultName.lastIndexOf("\\")+1);
		}
		resultName = resultName.replaceAll("[ .-]", "_");
		return resultName;
	}
	
	// Load match result from files as produced by matchResult.print()
	// A file may contains multiple matchResults
	public MatchResult loadMatchResultFile(String fileName,
			int srcId, int trgId, int graphState) {
			this.srcId = srcId;
			this.trgId = trgId;

			String resultName = getResultName(fileName);
			String date = new Date().toString();
			mappingId = importer.getSourceRelId(srcId, trgId,SourceRelationship.REL_MATCHRESULT,
					resultName);
			if (mappingId == -1) {
				mappingId = importer.insertSourceRel(srcId, trgId, SourceRelationship.REL_MATCHRESULT,
						 resultName, null, fileName, graphState, date);
			} else {
				System.out.println("saveMatchResult(): Error saving already existing match result "
								+ resultName + " with id " + mappingId);
				return null;
			}			
			importer.updateSourceRel(mappingId, Repository.STATUS_IMPORT_STARTED);
			
			MatchResult result = loadMatchResultFile(fileName, null, null);
			
			importer.updateSourceRel(mappingId, Repository.STATUS_IMPORT_DONE);
			return result;
		}
	
	public int getMappingId(){
		return mappingId;
	}
			
	public MatchResult loadMatchResultFile(String fileName, Graph srcGraph, Graph trgGraph){
		try {
			result = new MatchResultArray(srcGraph.getAllNodes(), trgGraph.getAllNodes());
			result.setName(getResultName(fileName));
			result.setGraphs(srcGraph, trgGraph);
			int count=0;
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line;
			float defaultEvidence = 1;
			if (!dbInsert){
				if (srcGraph==null || trgGraph==null){
					System.err.println("AccMatchResultParser.loadMatchResultFile srcGraph or trgGraph are null - creation of match result not possible ");
					return null;
				}		
			}

			while ((line = in.readLine()) != null) {
				if (line.isEmpty()){
					// ignore empty line
					continue;
				}
				String[] parts = null;
				if (line.contains("=")){
					parts = line.split("=");
				} else if (line.contains("<")){				
					parts = line.split("<");
				} else if (line.contains(">")){				
					parts = line.split(">");
				} else if (line.contains("\t")){				
					parts = line.split("\t");
				}
				if (parts==null){
					continue;
				}
//				if (parts.length!=2){
//					// less or more than 2 element don't make sense
//					continue;
//				}
				String srcAcc = parts[0];
				if (srcAcc.contains("#")){
					srcAcc = srcAcc.substring(srcAcc.indexOf("#")+1);
				}
				while (srcAcc.startsWith(" ")){
					srcAcc = srcAcc.substring(1);
				}
				while (srcAcc.endsWith(" ")){
					srcAcc = srcAcc.substring(0, srcAcc.length()-1);
				}
				String trgAcc = parts[1];
				if (trgAcc.contains("#")){
					trgAcc = trgAcc.substring(trgAcc.indexOf("#")+1);
				}
				while (trgAcc.startsWith(" ")){
					trgAcc = trgAcc.substring(1);
				}
				while (trgAcc.endsWith(" ")){
					trgAcc = trgAcc.substring(0, trgAcc.length()-1);
				}
				String sim= null;
				if (trgAcc.contains(" ")){
					String[] parts2 = trgAcc.split(" ");
					trgAcc=parts2[0];
					sim = parts2[1];
				}
				float simValue = defaultEvidence;
				if (sim!=null){
					simValue = Float.valueOf(sim);
				}
				if (!dbInsert && result==null){
					System.err.println("AccMatchResultParser.loadMatchResultFile result null - appending not possible ");
					return null;
				}
				ArrayList<Element> srcObjects = srcGraph.getElementsWithAccession(srcAcc);
				// expecting exactly one element				
				if (srcObjects==null){
					// not valid accession
					System.err.println("AccMatchResultParser.loadMatchResultFile Error not found accessioon in source: " + srcAcc);
					continue;
				}	
				ArrayList<Element> trgObjects = trgGraph.getElementsWithAccession(trgAcc);
				if (trgObjects==null){
					// not valid accession
					System.err.println("AccMatchResultParser.loadMatchResultFile Error not found accessioon in target: " + trgAcc);
					continue;
				}
				if (srcObjects.size()>1){
					System.err.println("AccMatchResultParser.loadMatchResultFile Error found several elements with the accessioon in source: " + srcAcc);
				}
				if (trgObjects.size()>1){
					System.out.println("AccMatchResultParser.loadMatchResultFile Error found several elements with the accessioon in target: " + trgAcc);
				}
				for (Element srcObject : srcObjects) {
					for (Element trgObject : trgObjects) {
						// append to match result
						if (dbInsert){
							importer.insertObjectRel(mappingId, srcObject.getId(),	trgObject.getId(), simValue,null);
						} else {
							result.append(srcObject, trgObject, simValue);
						}
						count++;
					}
				}
			
				if (count%100==0){
					System.out.println("count: " + count);
					if (result!=null){
						System.out.println(count+"\t" + result.getMatchCount());
					}
				}
			}
			

			if (result!=null){
				System.out.println(count+"\t" +result.getMatchCount());
			}
			in.close();
		} catch (IOException e) {
			System.out.println("loadMatchResultFile(): Error opening file "
				+ fileName + ": " + e.getMessage());
		}
		return result;
	}
	
	
}
