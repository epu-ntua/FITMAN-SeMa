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

import de.wdilab.coma.export.ExportUtil;
import de.wdilab.coma.center.Manager;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Path;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This class saves a matchresult to a rdf file by writing the main information
 * (source and target model) and the correspondences (using the unique identifier).
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class RDFExport {
	
	  static final String PART1 = "<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>\r\n\r\n" //encoding='utf-8' ?>\r\n\r\n");
		  	+"<rdf:RDF xmlns=\"http://knowledgeweb.semanticweb.org/heterogeneity/alignment\" "
		  	+ "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
		  	+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\r\n\r\n"
		  	+ "<Alignment>\r\n";
	  static final String PART2 = "<xml>yes</xml>\r\n"
		  	+ "<level>0</level>\r\n"
		  	+ "<type>11</type>\r\n";
	  
	  static final String PART3 = "</map>\r\n"
		+"</Alignment>\r\n"
		+"</rdf:RDF>\r\n";
	
	Manager manager=null;

	public RDFExport(Manager manager){
		this.manager=manager;
	}
	
	public void saveMatchResultFile(int source1Id, int source2Id,
			String resultName, String fileName) {
		if (source1Id == Source.UNDEF || source2Id == Source.UNDEF 
				|| resultName == null || fileName == null)
			return;
		MatchResult result = manager.loadMatchResult(source1Id, source2Id, resultName);
		String resultStr = toRDFAlignment(result);
		// append result to file if existing other create new file
		ExportUtil.writeToFile(fileName, resultStr, true);
	}
	
	public void saveMatchResultFile(SourceRelationship sr, String fileName) {
		if (sr == null) return;
		MatchResult result = manager.loadMatchResult(sr.getSourceId(), sr.getTargetId(), sr.getId());
		String resultStr = toRDFAlignment(result);
		// append result to file if existing other create new file
		ExportUtil.writeToFile(fileName, resultStr, true);
	}
	
	static public void saveMatchResultFile(MatchResult result, String fileName) {
		if (result == null) return;
		String resultStr = toRDFAlignment(result);
		// append result to file if existing other create new file
		ExportUtil.writeToFile(fileName, resultStr, true);
	}
	
	/**
	 * format alignment as RDF alignment
	 * @return MatchResult as RDFAlignment
	 * @author david
	 */
	public static String toRDFAlignment(MatchResult result) {
		if (result == null) return null;
		String nameStr = null, infoStr = null;
		String sourceStr = null, targetStr = null;
		String matcherStr = null, configStr = null;
//		String sourceProvider = null, targetProvider = null;
//		SchemaGraph sourceGraph = getSourceGraph();
//		SchemaGraph targetGraph = getTargetGraph();
		String resultName = result.getName();
		String matchInfo = result.getMatchInfo();
		ArrayList aObjects = result.getSrcMatchObjects();
		ArrayList bObjects = result.getTrgMatchObjects();
		//Name
		nameStr = "" + resultName;
		//Match info
		infoStr = "" + matchInfo;
		StringBuffer sb = new StringBuffer();
	    //TODO: Zeichensatz aus Systemumgebung holen??
		sb.append(PART1);
		sb.append("\r\n\r\n<!--");
		sb.append("MatchResult of simMatrix [" + aObjects.size() + ","
				+ bObjects.size() + "]");
		sb.append(" + Name: " + nameStr);
		sb.append(" + Info: " + infoStr);
		sb.append(" + Source: " + sourceStr);
		sb.append(" + Target: " + targetStr);
		sb.append(" + Matcher: " + matcherStr);
		sb.append(" + Config: " + configStr);
		sb.append("-->\r\n\r\n\r\n");
		sb.append(PART2); // ?
	    String onto1 = result.getSourceGraph().getSource().getProvider();
	    if(onto1!=null && onto1.endsWith("#"))
	        onto1 = onto1.substring(0,onto1.length()-1);
	    String onto2 = result.getTargetGraph().getSource().getProvider();
	    if(onto2!=null && onto2.endsWith("#"))
	        onto2 = onto2.substring(0,onto2.length()-1);
		sb.append("<onto1>" + onto1 + "</onto1>\r\n");
		sb.append("<onto2>" + onto2 + "</onto2>\r\n");
	    String url1 = result.getSourceGraph().getSource().getUrl();
	    if(url1!=null && url1.endsWith("#"))
	        url1 = url1.substring(0,url1.length()-1);
	    String url2 = result.getTargetGraph().getSource().getUrl();
	    if(url2!=null && url2.endsWith("#"))
	        url2 = url2.substring(0,url2.length()-1);
	    sb.append("<uri1>" + url1 + "</uri1>\r\n");
	    sb.append("<uri2>" + url2 + "</uri2>\r\n");
		//sb.append("<uri1>file://localhost/Volumes/Phata/Web/html/co4/align/Contest/101/onto.rdf</uri1>\r\n");
		//sb.append("<uri2>file://localhost/Volumes/Phata/Web/html/co4/align/Contest/101/onto.rdf</uri2>\r\n");
		sb.append("<map>\r\n");
		int matchCnt = 0;
		for (int i = 0; i < aObjects.size(); i++) {
			Object objA = aObjects.get(i);
			for (int j = 0; j < bObjects.size(); j++) {
				Object objB = bObjects.get(j);
				float sim = result.getSimilarity(objA, objB);
				if (sim > 0) {
					matchCnt++;
	                // Wenn aus einem Test aufgerufen, dann sind objA bzw. objB VertexImpl
	                // und keine ArrayList
	                Element aVertex = null;
	                Element bVertex = null;
	                if(objA instanceof Path)
	                    aVertex =  ((Path) objA).getLastElement();
	                else
	                    aVertex = (Element)objA;
	                if(objB instanceof ArrayList)
	                    bVertex = ((Path) objB).getLastElement();
	                else
	                    bVertex = (Element)objB;
	                if(!aVertex.getAccession().contains("/") &&
	                        ! bVertex.getAccession().contains("/")){
	                	String acc1 = aVertex.getAccession();
	                	String acc2 = bVertex.getAccession();

//	                	if (OAEIConstants.DIRECTORY_FULL){ // only true for directory 10/full
		                	acc1 = acc1.replaceAll("&","%26").replaceAll("\'","%27").replaceAll(",","%2C").replaceAll(":","%3A").replaceAll("#","%23");
		                	while (acc1.indexOf('(')>0 || acc1.indexOf(')')>0){
		                		acc1 = acc1.replace("(", "%28").replace(")", "%29");
		                	}
		                	acc2 = acc2.replaceAll("&","%26").replaceAll("\'","%27").replaceAll(",","%2C").replaceAll(":","%3A").replaceAll("#","%23");
		                	while (acc2.indexOf('(')>0 || acc2.indexOf(')')>0){
		                		acc2 = acc2.replace("(", "%28").replace(")", "%29");
		                	}
//	                	}
//		                String namespace1= aVertex.getNamespace();
//		                if (namespace1.equals(url1)){
//		                	namespace1=onto1;
//		                }
//		                String namespace2= bVertex.getNamespace();
//		                if (namespace2.equals(url2)){
//		                	namespace2=onto1;
//		                }
	                    //TODO: jeden Teilstring appenden, die "+" raus.
	                    sb
							.append("<Cell>\r\n"
									+ "<entity1 rdf:resource=\""
									//									+ sourceProvider
									//									+ "#"
									//									+ ((Element) aVertex.getObject())
									//											.getTextRep()
	                                //TODO: alle Sonderzeichen codieren
	                                + acc1
											//.getComment()
											//.getTypespace()
									+ "\"/>\r\n"
									+ "<entity2 rdf:resource=\""
									//									+ targetProvider
									//									+ "#"
									//									+ ((Element) bVertex.getObject())
									//											.getTextRep()
	                                // TODO: alle Sonderzeichen codieren
	                                + acc2
											//.getComment()
											//.getTypespace()
									+ "\"/>\r\n"
									+ "<measure rdf:datatype=\"http://www.w3.org/2001/XMLSchema#float\">"
									+ sim + "</measure>\r\n"
									+ "<relation>"
									+ "="
	                                +"</relation>\r\n" + "</Cell>\r\n");
	                }
				}
			}
		}
		//sb.append(" + Total: " + matchCnt + " Correspondences");
		//sb.append("--------------------------------------------------------");
		sb.append(PART3);
		return sb.toString();
	}

}
