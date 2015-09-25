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

import java.util.ArrayList;
import java.util.Enumeration;

import java.net.URI;
import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;

import de.wdilab.coma.center.Manager;
import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.repository.DataAccess;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.MatchResultArray;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

/**
 * This class imports a rdf alignment that contains the location of the
 * source and target model and for  * for each correspondence 
 * between them unique identifier of the two elements.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class RDFAlignmentParser {
	
//	public static int onlySourceInstances = 0;
//	public static int onlyTargetInstances = 0;
//	public static int bothInstances = 0, bothInstancesIntended=0;
//	public static int noInstances = 0;
	
	Manager manager = null;
	DataImport importer=null;	

	// if false return match result otherwise insert directly into database
	boolean dbInsert = false;
	int mappingId = -1;
	
	public RDFAlignmentParser(Manager manager, boolean dbInsert){
		this.manager=manager;
		if (manager!=null){
			importer = manager.getImporter();
		}
		this.dbInsert = dbInsert;
	}
	
	public MatchResult loadOWLAlignmentFile(String alignFile) {
		return loadOWLAlignmentFile(alignFile,(String) null, (String)null, (String)null);
	}
	
	
	public MatchResult loadOWLAlignmentFile(String alignFile, String resultName, String srcURI, String trgURI){		
//		ArrayList<ArrayList<String>> pairs1 = new ArrayList<ArrayList<String>>();
		Alignment alignment =null;
		try {
			AlignmentParser alignParser = new AlignmentParser(0);
			if (!alignFile.startsWith("file:") && alignFile.indexOf(":")==1){
				alignFile = "file:/"+alignFile;
			}
			alignFile = alignFile.replace("\\", "/");
//			alignFile = alignFile.replaceFirst("file:/", "").replace(" ", "%20");
			alignment = alignParser.parse(alignFile.replace(" ", "%20"));
			System.gc();
		} catch (Exception e) {
			System.out.println("loadOWLAlignmentFile(): Error parsing file "
					+ e.getMessage());
		}
		if (alignment == null) {
			System.out.println("loadOWLAlignmentFile(): Error parsing file " + alignFile);
			return null;
		} 
		int srcId = Source.UNDEF, trgId = Source.UNDEF;
		if (srcURI!=null){
			srcId = getSourceWithProvider(srcURI);
		}
		if (trgURI!=null){
			trgId = getSourceWithProvider(trgURI);
		}
		try {
			if (srcURI==null){
				URI uri = alignment.getOntology1URI();
				if (uri!=null){
					srcURI = uri.toString();
				} else {
					System.out.println("loadOWLAlignmentFile(): Error no source uri " );
					return null;
				}
				srcId = getSourceWithURI(srcURI);
			}
			if (trgURI==null){
				URI uri = alignment.getOntology2URI();
				if (uri!=null){
					trgURI = uri.toString();
				} else {
					System.out.println("loadOWLAlignmentFile(): Error no target uri " );
					return null;
				}
				trgId = getSourceWithURI(trgURI);
			}
		} catch (AlignmentException e1) {
//			e1.printStackTrace();
			System.out.println("loadOWLAlignmentFile(): Error getting URI for ontology " + e1.getCause());
		}
		if (srcId==Source.UNDEF || trgId==Source.UNDEF){
			return null;
		}
		
		if (dbInsert){
			return loadOWLAlignmentDBInsert(resultName, alignFile, srcId, trgId, alignment);
		} 
		return loadOWLAlignmentMemory(resultName, alignFile, srcId, trgId, alignment);

	}
	
	int getSourceWithURI(String uri){
		if (uri == null) {
			return Source.UNDEF;
		}
		DataAccess accessor = manager.getAccessor();
		ArrayList<Integer> sources = accessor.getSourceIdsWithUrl(uri.toString());
		if (sources==null){
			System.out.println("loadOWLAlignmentFile(): Error getting source with the uri " + uri);
			return Source.UNDEF;
		} else if (sources.size()==1){
			return sources.get(0);
		}	else {
			System.out.println("loadOWLAlignmentFile(): Error getting source with the uri " + uri);
		}
		return Source.UNDEF;
	}
	
	MatchResult loadOWLAlignmentDBInsert(String resultName, String alignFile, int srcId, int trgId, Alignment alignment){	
		 if (resultName==null){
			 resultName = InsertParser.createSourceName(alignFile);
		 }
		 String date = new java.util.Date().toString();
		 mappingId = importer.insertSourceRel(srcId,trgId, 
				 SourceRelationship.REL_MATCHRESULT, resultName, null, 
				 alignFile, Graph.PREP_DEFAULT_ONTOLOGY, date);
		 importer.updateSourceRel(mappingId, Repository.STATUS_IMPORT_STARTED);
		Enumeration<Cell> cells = alignment.getElements();
		int count =0;
		try {
			while (cells.hasMoreElements()) {
				Cell cell = cells.nextElement();
				String srcAcc = cell.getObject1AsURI().toString();
				String trgAcc = cell.getObject2AsURI().toString();
				float simValue = (float) cell.getStrength();
				String relation = cell.getRelation().getRelation();
				// dbInsert - srcAcc and trgAcc are the accessions
								
				int oSrcId = importer.getObjectIdNotKind(srcId, srcAcc, Element.KIND_ELEMPATH);
				if (oSrcId<0){
					oSrcId = importer.getObjectIdEndingNotKind(srcId, srcAcc, Element.KIND_ELEMPATH);
				}				
				if (oSrcId<0){
					// not valid id
					continue;
				}	

				int oTrgId = importer.getObjectIdNotKind(trgId, trgAcc, Element.KIND_ELEMPATH);
				if (oTrgId<0){
					oTrgId = importer.getObjectIdEndingNotKind(trgId, trgAcc, Element.KIND_ELEMPATH);
				}
				if (oTrgId<0){
					// not valid id
					continue;
				}
				if (simValue==0){
					// bugfix: zero doesn't make sense - treated like no correspondence
					simValue=(float) 1;
				}
				importer.insertObjectRel(mappingId, oSrcId,	oTrgId, simValue, relation);
				count++;
			}
		} catch (AlignmentException e) {
			System.err.println("RDFAlignmentParser.loadOWLAlignmentDBInsert AlignmentException " 
					+ e.getLocalizedMessage());
		}
		System.out.println("cells\t" + count);
		importer.updateSourceRel(mappingId, Repository.STATUS_IMPORT_DONE);
		// return null because direct import into database
		return null;
	}
	
	public int getMappingId(){
		return mappingId;
	}
	

	int getSourceWithProvider(String uri){
		if (uri == null) {
			return Source.UNDEF;
		}
		DataAccess accessor = manager.getAccessor();
		ArrayList<Integer> sources = accessor.getSourceIdsWithProvider(uri.toString());
		if (sources==null){
			System.out.println("loadOWLAlignmentFile(): Error getting source with the uri " + uri);
			return Source.UNDEF;
		} else if (sources.size()==1){
			return sources.get(0);
		}	else {
			System.out.println("loadOWLAlignmentFile(): Error getting source with the uri " + uri);
		}
		return Source.UNDEF;
	}
	
	MatchResult loadOWLAlignmentMemory(String resultName, String alignFile, int srcId, int trgId, Alignment alignment){	
		Graph srcGraph=manager.loadGraph(srcId);
		Graph trgGraph=manager.loadGraph(trgId);
		return loadOWLAlignmentMemory(resultName, alignFile, srcGraph, trgGraph, alignment);
	}
	
	
	MatchResult loadOWLAlignmentFile(String resultName, String alignFile, Graph srcGraph, Graph trgGraph){
		Alignment alignment =null;
		try {
			AlignmentParser alignParser = null;
			alignParser = new AlignmentParser(0);
			if (!alignFile.startsWith("file:") && alignFile.indexOf(":")==1){
				alignFile = "file:/"+alignFile;
			}
			alignFile = alignFile.replace("\\", "/");
	//		alignFile = alignFile.replaceFirst("file:/", "").replace(" ", "%20");
			alignment = alignParser.parse(alignFile.replace(" ", "%20"));
			System.gc();
		} catch (Exception e) {
			System.out.println("loadOWLAlignmentFile(): Error parsing file "
					+ e.getMessage());
		}
		if (alignment == null) {
			System.out.println("loadOWLAlignmentFile(): Error parsing file " + alignFile);
			return null;
		}
		if (dbInsert){
			return loadOWLAlignmentDBInsert(resultName, alignFile, srcGraph.getSource().getId(), trgGraph.getSource().getId(), alignment);	
		}		
		return loadOWLAlignmentMemory(resultName, alignFile, srcGraph, trgGraph, alignment);
	}
	
	MatchResult loadOWLAlignmentMemory(String resultName, String alignFile, Graph srcGraph, Graph trgGraph, Alignment alignment){	
		 if (resultName==null){
			 resultName = InsertParser.createSourceName(alignFile);
		 }		 
//		 srcGraph = srcGraph.getGraph(graphState);
//		 trgGraph = trgGraph.getGraph(graphState);
		 MatchResultArray result = new MatchResultArray(srcGraph.getAllNodes(), trgGraph.getAllNodes());
		 result.setName(resultName);


		Enumeration<Cell> cells = alignment.getElements();
		int count =0;
		try {
			while (cells.hasMoreElements()) {
				Cell cell = cells.nextElement();
				String srcAcc = cell.getObject1AsURI().toString();
				String trgAcc = cell.getObject2AsURI().toString();
				float sim = (float) cell.getStrength();
//				String relation = cell.getRelation().getRelation(); // not used
				// srcAcc and trgAcc are the accessions
				
				ArrayList srcObjects = srcGraph.getElementsWithAccession(srcAcc);
				// expecting exactly one element				
				if (srcObjects==null){
					// not valid accession
					System.out.println("loadOWLAlignmentMemory() Error not found accessioon in source: " + srcAcc);
					continue;
				}	
				ArrayList trgObjects = trgGraph.getElementsWithAccession(trgAcc);
				if (trgObjects==null){
					// not valid accession
					System.out.println("loadOWLAlignmentMemory() Error not found accessioon in target: " + trgAcc);
					continue;
				}
				if (srcObjects.size()>1){
					System.out.println("loadOWLAlignmentMemory() Error found several elements with the accessioon in source: " + srcAcc);
				}
				if (trgObjects.size()>1){
					System.out.println("loadOWLAlignmentMemory() Error found several elements with the accessioon in target: " + trgAcc);
				}
				if (sim==0){
					// bugfix: zero doesn't make sense - treated like no correspondence
					sim=(float) 1;
				}
				for (Object srcObject : srcObjects) {
					for (Object trgObject : trgObjects) {
						if (result.getSimilarity(srcObject, trgObject)>0){
							System.out.println("Duplicate information");
						} else {
							result.append(srcObject, trgObject, sim);
							count++;
						}
					}
				}
				if (count%100==0){
					System.out.println("count: " + count);
					if (result!=null){
						System.out.println(count+"\t" + result.getMatchCount());
					}
				}
			}
			result.getMatchCount();
			System.out.println(count);
		} catch (AlignmentException e) {
			System.err.println("RDFAlignmentParser.loadOWLAlignmentMemory AlignmentException " 
					+ e.getLocalizedMessage());
		}
		System.out.println("cells\t" + count);
		// return result because no direct import into database
		return result;
	}
	

//	public MatchResult loadOWLAlignmentFile(String alignFile,String resultName,
//			String endString1, String endString2) {
//		return loadOWLAlignmentFile(alignFile, resultName, endString1, endString2, 
////				SchemaGraph.GRAPH_STATE_LOADED);
//				Graph.PREP_DEFAULT_ONTOLOGY);
////				SchemaGraph.GRAPH_STATE_SIMPLIFIED);
//	}
	
//	public MatchResult loadOWLAlignmentFile(String alignFile, String resultName,
//			String endString1, String endString2, int graphState) {
		// TODO directly insert result into database if true
//		Alignment alignment =null;
//		try {
//			AlignmentParser alignParser = null;
//			alignParser = new AlignmentParser(0);
//			Hashtable hash = new Hashtable();
//			System.gc();
////			alignFile = alignFile.replaceFirst("file:/", "").replace(" ", "%20");
////			alignment = alignParser.parse(alignFile.replace(" ", "%20"), hash);
//			alignment = alignParser.parse(alignFile.replace(" ", "%20"), hash);
//		} catch (Exception e) {
//			System.out.println("loadOWLAlignmentFile(): Error parsing file "
//					+ e.getMessage());
//		}
//		if (alignment == null) {
//			System.out.println("loadOWLAlignmentFile(): Error parsing file "
//					+ alignFile);
//			return null;
//		}
//
//		DataAccess accessor = manager.getAccessor();
//		
//		OWLOntology ont1 = (OWLOntology) alignment.getOntology1();
//		OWLOntology ont2 = (OWLOntology) alignment.getOntology2();
//		String oUri1 = null, oUri2 = null;
//		try {
//			oUri1 = ont1.getPhysicalURI().toString();
//			oUri2 = ont2.getPhysicalURI().toString();
//		} catch (Exception e) {
//			System.out.println("loadOWLAlignmentFile(): Error getting URI "
//					+ e.getMessage());
//		}
//		if (oUri1 == null) {
//			System.out
//					.println("loadOWLAlignmentFile(): Error getting URI from source ontology "
//							+ ont1);
//			return null;
//		} else if (oUri2 == null) {
//			System.out
//					.println("loadOWLAlignmentFile(): Error getting URI from target ontology"
//							+ ont2);
//			return null;
//		}
//
//		// System.out.println("URI1: " + oUri1);
//		// System.out.println("URI2: " + oUri2);
//		Source source1 = null, source2 = null;
//		ArrayList<Source> sources = null;
//		if (endString1 != null && endString1.length() > 0)
//			sources = accessor.getSourcesWithUrl(endString1);
//		else
//			sources = accessor.getSourcesWithUrl(oUri1);
//		if (!(sources == null || sources.isEmpty()))
//			source1 = sources.get(0);
//		if (source1==null){
//			try {
//				OWLClassImpl classI = (OWLClassImpl) ont1.getClasses().iterator().next();
//				oUri1 = classI.getURI().toString();
//				if (oUri1.contains("#")){
//					oUri1 = oUri1.substring(0, oUri1.indexOf("#"));
//				}
//			} catch (Exception e) {
//				System.out.println("loadOWLAlignmentFile(): Error getting URI "
//						+ e.getMessage());
//			}
//			if (endString1 != null && endString1.length() > 0)
//				sources = accessor.getSourcesWithUrl(endString1);
//			else
//				sources = accessor.getSourcesWithUrl(oUri1);
//			if (sources != null && !sources.isEmpty())
//				source1 = sources.get(0);
//		}	
//		if (endString2 != null && endString2.length() > 0)
//			sources = accessor.getSourcesWithUrl(endString2);
//		else
//			sources = accessor.getSourcesWithUrl(oUri2);
//		if (sources != null && !sources.isEmpty())
//			source2 = sources.get(0);
//		if (source2==null){
//			try {
//				OWLClassImpl classI = (OWLClassImpl) ont2.getClasses().iterator().next();
//				oUri2 = classI.getURI().toString();
//				if (oUri2.contains("#")){
//					oUri2 = oUri2.substring(0, oUri2.indexOf("#"));
//				}
//			} catch (Exception e) {
//				System.out.println("loadOWLAlignmentFile(): Error getting URI "
//						+ e.getMessage());
//			}
//			if (endString2 != null && endString2.length() > 0)
//				sources = accessor.getSourcesWithUrl(endString2);
//			else
//				sources = accessor.getSourcesWithUrl(oUri2);
//			if (!(sources == null || sources.isEmpty()))
//				source2 = sources.get(0);
//		}
//
//		if (source1 == null) {
//			System.out
//					.println("loadOWLAlignmentFile(): No source with such URI "
//							+ oUri1);
//			return null;
//		} else if (source2 == null) {
//			System.out
//					.println("loadOWLAlignmentFile(): No target with such URI "
//							+ oUri2);
//			return null;
//		}
//		Graph ontGraph1 = manager.loadGraph(source1, true, true,  true);
//		Graph ontGraph2 =  manager.loadGraph(source2, true, true, true);
//		// because at the moment - only loaded and resolved are supported
//		 ontGraph1 = ontGraph1.getGraph(graphState);
//		 ontGraph2 = ontGraph2.getGraph(graphState);
//		 DataImport importer = manager.getImporter();
//			
//		 if (resultName==null){
//			 resultName = InsertParser.createSourceName(alignFile);
//		 }
//		 String date = new java.util.Date().toString();
//		 int mappingId = importer.insertSourceRel(source1.getId(), source2.getId(), 
//				 SourceRelationship.REL_MATCHRESULT, resultName, null, 
//				 alignFile, Graph.PREP_DEFAULT_ONTOLOGY, date);
//		 
////		if (false) {
////			// for statistics
////			// count overlapping names and datatypes
////			ArrayList simpleTypes1 = ontGraph1.getAllSimpleTypes();
////			ArrayList complexTypes1 = ontGraph1.getAllComplexTypes();
////			ArrayList simpleTypes2 = ontGraph2.getAllSimpleTypes();
////			ArrayList complexTypes2 = ontGraph1.getAllComplexTypes();
////
////			ArrayList<String> names1 = new ArrayList<String>();
////			ArrayList<String> names2 = new ArrayList<String>();
////
////			for (Iterator iter = simpleTypes1.iterator(); iter.hasNext();) {
////				Element element = (Element)  iter.next();
////				if (element != null && element.getLabel() != null) {
////					names1.add(element.getLabel());
////				}
////			}
////			for (Iterator iter = complexTypes1.iterator(); iter.hasNext();) {
////				Element element = (Element) iter.next();
////				if (element != null && element.getLabel() != null) {
////					names1.add(element.getLabel());
////				}
////			}
////
////			for (Iterator iter = simpleTypes2.iterator(); iter.hasNext();) {
////				Element element = (Element) iter.next();
////				if (element != null && element.getLabel() != null) {
////					names2.add(element.getLabel());
////				}
////			}
////			for (Iterator iter = complexTypes2.iterator(); iter.hasNext();) {
////				Element element = (Element) iter.next();
////				if (element != null && element.getLabel() != null) {
////					names2.add(element.getLabel());
////				}
////			}
////			ArrayList<String> help = (ArrayList<String>) names1.clone();
////			help.removeAll(names2);
////			int diff = names1.size() - help.size();
////			System.out.println("count overlapping names types2 in types1: "
////					+ diff);
////
////			help = (ArrayList<String>) names2.clone();
////			help.removeAll(names1);
////			diff = names2.size() - help.size();
////			System.out.println("count overlapping names types1 in types2: "
////					+ diff);
////
////			Iterator iterator = ontGraph1.getElementIterator();
////			names1 = new ArrayList<String>();
////			while (iterator.hasNext()) {
////				Element element = (Element) iterator.next();
////				names1.add(element.getLabel());
////			}
////			iterator = ontGraph2.getElementIterator();
////			names2 = new ArrayList<String>();
////			while (iterator.hasNext()) {
////				Element element = (Element) iterator.next();
////				names2.add(element.getLabel());
////			}
////			help = (ArrayList<String>) names1.clone();
////			help.removeAll(names2);
////			diff = names1.size() - help.size();
////			System.out.println("count overlapping names names2 in names1: "
////					+ diff);
////
////			help = (ArrayList<String>) names2.clone();
////			help.removeAll(names1);
////			diff = names2.size() - help.size();
////			System.out.println("count overlapping names names1 in names2: "
////					+ diff);
////		}
////		ontGraph1 = ontGraph1.getGraph(SchemaGraph.GRAPH_STATE_SIMPLIFIED);
////		ontGraph2 = ontGraph2.getGraph(SchemaGraph.GRAPH_STATE_SIMPLIFIED);
////		if (ontGraph1==null || ontGraph2==null){	
////			System.out.println("loadOWLAlignmentFile(): Transformation to SIMPLIFIED failed, therefore use REDUCED");
////			ontGraph1 = ontGraph1.getGraph(SchemaGraph.GRAPH_STATE_REDUCED);
////			ontGraph2 = ontGraph2.getGraph(SchemaGraph.GRAPH_STATE_REDUCED);
////		}	
//
////		System.out.println(ontGraph2.getSource().getName());
////		ontGraph2.printGraphInfo();
//		// ontGraph2.printSchemaInfo();
//		if (ontGraph1 == null) {
//			System.out
//					.println("loadOWLAlignmentFile(): No graph loaded for source "
//							+ source1);
//			return null;
//		} else if (ontGraph2 == null) {
//			System.out
//					.println("loadOWLAlignmentFile(): No graph loaded for target "
//							+ source2);
//			return null;
//		}
////		countExpectedElementsWithInstances(alignment, ontGraph1, ontGraph2);
//		Enumeration<Cell> cells = alignment.getElements();
//		MatchResult matchResult = null;
//		if (!dbInsert) {
//			matchResult = new MatchResultArray();
//		}
//		int count =0;
//		while (cells.hasMoreElements()) {
//			Cell cell = cells.nextElement();
//			count++;
//			OWLNamedObject o1 = (OWLNamedObject) cell.getObject1();
//			OWLNamedObject o2 = (OWLNamedObject) cell.getObject2();
//			// String sem = cell.getSemantics();
//			double sim = cell.getStrength();
//
//			URI uri1 = null, uri2 = null;
//			try {
//				uri1 = o1.getURI();
//				uri2 = o2.getURI();
//			} catch (Exception e) {
//				System.out
//						.println("loadOWLAlignmentFile(): Error getting object URI "
//								+ e.getMessage());
//			}
//			if (uri1 == null || uri2 == null) {
//				System.out
//						.println("loadOWLAlignmentFile(): Error getting URI from objects");
//				continue;
//			}
//
////			String namespace1 = OWLParser.getNamespace(uri1.toString());
////			String name1 = OWLParser.getName(uri1);
////			String namespace2 = OWLParser.getNamespace(uri2.toString());
////			String name2 = OWLParser.getName(uri2);
//
////			ArrayList vertices1 = ontGraph1.getVerticesWithQualifiedName(name1, namespace1);
////			ArrayList vertices1 = ontGraph1.getElementsWithAccession(namespace1+"#"+name1);
//			ArrayList vertices1 = ontGraph1.getElementsWithAccession(uri1.toString());
////			ArrayList vertices2 = ontGraph2.getVerticesWithQualifiedName(name2, namespace2);
////			ArrayList vertices2 = ontGraph2.getElementsWithAccession(namespace2+"#"+name2);
//			ArrayList vertices2 = ontGraph2.getElementsWithAccession(uri2.toString());
//			if (vertices1 == null) {
//				System.out
//						.println("loadOWLAlignmentFile(): No source nodes with URI "
//								+ uri1);
//				continue;
//			} else if (vertices2 == null) {
//				System.out
//						.println("loadOWLAlignmentFile(): No target nodes with URI "
//								+ uri2);
//				continue;
//			}
//
//			for (int i = 0; i < vertices1.size(); i++) {
//				Element vertex1 = (Element) vertices1.get(i);
//				for (int j = 0; j < vertices2.size(); j++) {
//					Element vertex2 = (Element) vertices2.get(j);
//					// int before = matchResult.getMatchCount();
////					
//					if (dbInsert) {
//						importer.insertObjectRel(mappingId, vertex1.getId(),
//							vertex2.getId(), (float) sim,null);
//					} else {
//						matchResult.append(vertex1, vertex2, (float) sim);
//					}
//					// int after = matchResult.getMatchCount();
//					// if (before==after){
//					// System.out.print("\t MatchCount " +
//					// matchResult.getMatchCount());
//					// System.out.println(vertex1 + " - " + vertex2 + " : " +
//					// sim);
//					// matchResult.append(vertex1, vertex2, (float)1.0);
//					// }
//				}
//			}
//		}
//		System.out.println("count: " + count);
//		System.out.println("LOADED " + alignFile + ": " + alignment.nbCells()
////				+ " and " + matchResult.getMatchCount()
//				);
//		if (dbInsert) {
//			importer.updateSourceRel(mappingId, Repository.STATUS_IMPORT_DONE);
//			return null;
//		}
//		matchResult.setGraphs(ontGraph1, ontGraph2);
//		return matchResult;
//	}
//	
//	static public void loadOWLAlignmentFile(String alignFile) {
//		Alignment alignment =null;
//		try {
//			AlignmentParser alignParser = null;
//			alignParser = new AlignmentParser(0);
//			Hashtable hash = new Hashtable();
//			System.gc();
////			alignFile = alignFile.replaceFirst("file:/", "").replace(" ", "%20");
////			alignment = alignParser.parse(alignFile.replace(" ", "%20"), hash);
//			alignment = alignParser.parse(alignFile.replace(" ", "%20"), hash);
//			System.gc();
//		} catch (Exception e) {
//			System.out.println("loadOWLAlignmentFile(): Error parsing file "
//					+ e.getMessage());
//		}
//		if (alignment == null) {
//			System.out.println("loadOWLAlignmentFile(): Error parsing file "
//					+ alignFile);
//		} else {
//			Enumeration<Cell> cells = alignment.getElements();
//			int count =0;
//			while (cells.hasMoreElements()) {
//				Cell cell = cells.nextElement();
//				count++;
//			}
//			System.out.println("cells\t" + count);
//		}
//		
//	}
//	
//	
////	private void countExpectedElementsWithInstances(Alignment alignment,
////			Graph sourceGraph, Graph targetGraph) {
////		Enumeration<Cell> cells = (Enumeration<Cell>) alignment.getElements();
////		Set<Element> sourceVertices = sourceGraph.vertexSet();
////		Set<Element> targetVertices = targetGraph.vertexSet();
////		bothInstancesIntended = 0;
////		cellLoop: while (cells.hasMoreElements()) {
////			Cell cell = cells.nextElement();
////			OWLNamedObjectImpl namedObject1 = (OWLNamedObjectImpl) cell
////					.getObject1();
////			OWLNamedObjectImpl namedObject2 = (OWLNamedObjectImpl) cell
////					.getObject2();
////
////			for (Element sourceElement : sourceVertices) {
////				if (sourceElement.getNamespace() == null)
////					continue;
////				String sourceUri = sourceElement.getNamespace();
////				if (!(sourceElement.getNamespace().equals(sourceElement
////						.getAccession())))
////					sourceUri += "#" + sourceElement.getAccession();
////
////				if (sourceUri.equals(namedObject1.getURI().toString())) {
////					for (VertexImpl targetVetex : targetVertices) {
////						Element targetElement = (Element) targetVetex
////								.getObject();
////						if (targetElement.getNamespace() == null)
////							continue;
////						String targetUri = targetElement.getNamespace();
////						if (!(targetElement.getNamespace().equals(targetElement
////								.getAccession())))
////							targetUri += "#" + targetElement.getAccession();
////
////						if (targetUri.equals(namedObject2.getURI().toString())) {
////							// Element in refalign
////							if (sourceElement.hasDirectInstancesSimple() || sourceElement.hasDirectInstancesComplex()
////									|| sourceElement.getIdentifiers().size() > 0){
////								if (targetElement.hasDirectInstancesSimple() || targetElement.hasDirectInstancesComplex()
////										|| targetElement.getIdentifiers()
////												.size() > 0){
////									bothInstances++;
////									bothInstancesIntended++;
////								} else{
////									onlySourceInstances++;
////								}
////							}
////							else if (targetElement.hasDirectInstancesSimple() || targetElement.hasDirectInstancesComplex()
////									|| targetElement.getIdentifiers().size() > 0)
////								onlyTargetInstances++;
////							else
////								noInstances++;
////							continue cellLoop;
////						}
////					}
////				}
////			}
////		}
////	}

}
