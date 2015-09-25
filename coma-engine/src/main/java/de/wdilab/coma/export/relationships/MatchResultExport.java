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

package de.wdilab.coma.export.relationships;

import java.util.ArrayList;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.export.ExportUtil;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Path;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This class saves a matchresult to a text file by writing the main information
 * (source and target model) and the correspondences (paths for path-based results, 
 * name for node-based results).
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class MatchResultExport {
	
	Manager manager=null;

	public MatchResultExport(Manager manager){
		this.manager=manager;
	}
	
	public void saveMatchResultFile(int source1Id, int source2Id,
			String resultName, String fileName) {
		if (source1Id == Source.UNDEF || source2Id == Source.UNDEF 
				|| resultName == null || fileName == null)
			return;
		MatchResult result = manager.loadMatchResult(source1Id, source2Id, resultName);
		String resultStr = result.toString();
		// append result to file if existing other create new file
		ExportUtil.writeToFile(fileName, resultStr, true);
	}
	
	public void saveMatchResultFile(SourceRelationship sr, String fileName) {
		if (sr == null) return;
		MatchResult result = manager.loadMatchResult(sr.getSourceId(), sr.getTargetId(), sr.getId());
		String resultStr = result.toString();
		// append result to file if existing other create new file
		ExportUtil.writeToFile(fileName, resultStr, true);
	}
	
	static public void saveMatchResultFile(MatchResult result, String fileName) {
		if (result == null) return;
		String resultStr = result.toString();
		// append result to file if existing other create new file
		ExportUtil.writeToFile(fileName, resultStr, true);
	}
	
	 static public String resultToString(MatchResult result) {
		  String name = result.getName();
		  String matchInfo = result.getMatchInfo();
		  Graph sourceGraph = result.getSourceGraph();
		  Graph targetGraph = result.getTargetGraph();
		  ArrayList<Object> srcObjects = result.getSrcObjects();
		  ArrayList<Object> trgObjects = result.getTrgObjects();
		  
		    String nameStr=null, infoStr=null;
		    String sourceStr=null, targetStr=null;

		    //Name
		    if (name!=null) nameStr = name;
		    else nameStr = MatchResult.MAP_OB_UNKNOWN;
		    //Match info
		    if (matchInfo!=null) infoStr = matchInfo;
		    else infoStr = MatchResult.MAP_OB_UNKNOWN;
		    //Source
		    if (sourceGraph!=null) {
		      sourceStr = sourceGraph.getSource().getName() + "|" + Graph.preprocessingToString(sourceGraph.getPreprocessing());
		      sourceStr = sourceStr+ "|" + sourceGraph.getSource().getProvider();
		    }
		    else sourceStr = MatchResult.MAP_OB_UNKNOWN + "|" + MatchResult.MAP_OB_UNKNOWN;
		    //Target
		    if (targetGraph!=null) {
		      targetStr = targetGraph.getSource().getName() + "|" + Graph.preprocessingToString(targetGraph.getPreprocessing());
		      targetStr = targetStr+ "|" + targetGraph.getSource().getProvider();
		    }
		    else targetStr = MatchResult.MAP_OB_UNKNOWN + "|" + MatchResult.MAP_OB_UNKNOWN;

		    StringBuffer sb = new StringBuffer();
		    sb.append("--------------------------------------------------------\n");
		    sb.append("MatchResult of simMatrix [").append(srcObjects.size()).append(",").append(trgObjects.size()).append("]\n");
		    sb.append(" + Name: ").append(nameStr).append("\n");
		    sb.append(" + Info: ").append(infoStr).append("\n");
		    sb.append(" + Source: ").append(sourceStr).append("\n");
		    sb.append(" + Target: ").append(targetStr).append("\n");

		    int matchCnt = 0;
		    for (int i=0; i<srcObjects.size(); i++) {
		      Object srcObject = srcObjects.get(i);
		      for (int j=0; j<trgObjects.size(); j++) {
		        Object trgObject = trgObjects.get(j);
		        float sim = result.getSimilarity(srcObject, trgObject);			        
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
		    return sb.toString();
	  }
	
}
