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

package de.wdilab.coma.insert.instance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;

/**
 * Import instances of a xml file. As input we need the file name
 * that contains the xml instance data.
 * 
 * @author Sabine Massmann
 */
public class InstanceXMLParser {
	DataImport importer = null;

	public InstanceXMLParser(DataImport _importer) {
		importer = _importer;
	}

	public Graph parseInstances(Graph schemaGraph, String file) {
		int sourceid = schemaGraph.getSource().getId();
		importer.prepareInstancesStatement(sourceid);

		SAXParser saxParser = new SAXParser();
		XMLContentHandler contentHandler = new XMLContentHandler(importer,
				this, file, schemaGraph);
		saxParser.setContentHandler(contentHandler);
		
		try {
			file = file.replaceAll(" ", "%20");
			saxParser.parse(file);
		}
		catch (IOException e) {
			System.out.println("Error reading URI: " + e.getMessage());
		}
		catch (SAXException e) {
			System.out.println("Error parsing: " + e.getMessage());
		}
		importer.closeInstancesStatement();
//		schemaGraph.print();
		return schemaGraph;
	}

	public static void main(String[] args) {
	// System.setProperty("comaUrl",
	//                "jdbc:mysql://localhost/GenMatcher?autoReconnect=true");
	//        System.setProperty("comaUser", "");
	//        System.setProperty("comaPwd", "");
	//        int graphstate = SchemaGraph.GRAPH_STATE_REDUCED;
	//        SchemaManager manager = new SchemaManager();
	//        manager.loadRepository(true);
	//        
	////        int source = 3;
	////        int target = 4;
	////        SchemaGraph schemaGraph1 = manager.loadSchemaGraph(manager.getSource(source), true).getGraph(graphstate);
	////        SchemaGraph schemaGraph2 =  manager.loadSchemaGraph(manager.getSource(target), true).getGraph(graphstate);
	////        String sourceInstances = "E:\\workspace\\COMA++\\Sources\\Clio_BIBDEMO\\S1.xml";
	////        manager.loadInstances(schemaGraph1, sourceInstances);
	////        String targetInstances = "E:\\workspace\\COMA++\\Sources\\Clio_BIBDEMO\\S3.xml";
	////        manager.loadInstances(schemaGraph2, targetInstances);
	//        String directory =  "E:\\workspace\\COMA++\\Sources\\amalgam_XML";
	//        SchemaGraph s1 = manager.loadSchemaGraph(manager.getFirstSource("S1"), true).getGraph(graphstate);
	//        String s1Instances = directory + "\\S1.xml";
	//        manager.loadInstances(s1, s1Instances);
	//        SchemaGraph s2 =  manager.loadSchemaGraph(manager.getFirstSource("S2"), true).getGraph(graphstate);
	//        String s2Instances = directory + "\\S2.xml";
	//        manager.loadInstances(s2, s2Instances);
	//        SchemaGraph s3 = manager.loadSchemaGraph(manager.getFirstSource("S3"), true).getGraph(graphstate);
	//        String s3Instances = directory + "\\S3.xml";
	//        manager.loadInstances(s3, s3Instances);
	//        SchemaGraph s4 =  manager.loadSchemaGraph(manager.getFirstSource("S4"), true).getGraph(graphstate);
	//        String s4Instances = directory + "\\S4.xml";
	//        manager.loadInstances(s4, s4Instances);
	}

}

class XMLContentHandler implements ContentHandler {
	String sourceName = null;
	InstanceXMLParser sourceParser = null;
	String elementText = "";
	Graph graph = null;
	DataImport importer = null;

	String targetNamespace = null;
	ArrayList<String> prefixes = new ArrayList<String>();
	ArrayList<String> namespaces = new ArrayList<String>();
	final static int GRAPHSTATE = Graph.PREP_REDUCED;
	HashMap<Element, Integer> countInstances = new HashMap<Element, Integer>();

	public XMLContentHandler(DataImport importer,
			InstanceXMLParser sourceParser, String sourceName, Graph graph) {
		this.importer = importer;
		this.sourceName = sourceName;
		this.graph = graph.getGraph(GRAPHSTATE);
		this.sourceParser = sourceParser;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setDocumentLocator(Locator locator) {
	//System.out.println("setDocumentLocator()");
	//        this.locator = locator;
	}

	public void startDocument() throws SAXException {
	//System.out.println("startDocument()");
	}

	public void endDocument() throws SAXException {
	//System.out.println("endDocument()");
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
	//System.out.println("processingInstruction(): target: " + target + " and data: " + data);
	}

	public void startPrefixMapping(String prefix, String uri) {
		//System.out.println("startPrefixMapping(): start [" + prefix + "] mapped to: [" + uri + "]");
		if (!prefixes.contains(prefix)) {
			prefixes.add(prefix);
			namespaces.add(uri);
		}
	}

	public void endPrefixMapping(String prefix) {
		//System.out.println("endPrefixMapping(): end [" + prefix + "]");
		int ind = prefixes.indexOf(prefix);
		if (ind != -1) {
			prefixes.remove(ind);
			namespaces.remove(ind);
		}
	}

	Element current = null;
	ArrayList<Element> path = new ArrayList<Element>();

	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes atts) throws SAXException {
		//System.out.println("startElement(): " + localName + "[" + namespaceURI + ", " + rawName +"]" );
		ArrayList<Element> nodes = null;
		if (current == null) {
			nodes = graph.getRoots();
		} else {
			nodes = graph.getChildren(current);
		}
		for (int i = 0; i < nodes.size(); i++) {
			Element node = nodes.get(i);
			String name = node.getName();
			if (localName.equals(name)) {
				current = node;
				break;
			}
		}

		if (current == null) {
			System.out.println("no node.");
			return;
		}
		Integer count = countInstances.get(current);
		if (count == null) {
			count = new Integer(1);
		} else {
			count = new Integer(count.intValue() + 1);
		}
		countInstances.put(current, count);
		path.add(current);
		ArrayList<Element> children = graph.getChildren(current);
		if (children!=null){
			for (int i = 0; i < atts.getLength(); i++) {
				String attrName = atts.getLocalName(i);
				String attrValue = atts.getValue(i);
	
				for (int j = 0; j < children.size(); j++) {
					Element node = children.get(j);
					String name = node.getName();
					if (attrName.equals(name)) {
						count = countInstances.get(node);
						if (count == null) {
							count = new Integer(1);
						} else {
							count = new Integer(count.intValue() + 1);
						}
						countInstances.put(node, count);
						ArrayList pathAll = (ArrayList) path.clone();
						pathAll.add(node);
						String connect = getInstanceConnect(pathAll);
						saveInstanceToRepository( node,
								attrValue, connect, importer);
					}
				}
				if (attrName.equals("targetNamespace"))
					targetNamespace = attrValue;
			}
		}
	}
	
	private String getInstanceConnect(ArrayList path){
		if (path==null || path.isEmpty()){
			return null;
		}
		String connect="";
		Element node = null;
		Integer count=null;
		for (int i = 0; i < (path.size()-1); i++) {
			node = (Element) path.get(i);
			count = countInstances.get(node);
			if (connect.length()>0){
				connect+="-";
			}
			connect+=node.getName()+"_"+count;
		}
		return connect;
	}

	public void endElement(String namespaceURI, String localName, String rawName)
			throws SAXException {
		//System.out.println("endElement(): " + localName);
		//System.out.println("ID Path     : " + idPath);
		//System.out.println("Name Path   : " + namePath);


		if (current!=null &&
				graph.isLeaf(current) // assumption: only leaves have instances (in xml)
				&& elementText != null && elementText.length() > 0) {
			if (current.getDirectInstancesSimple().size() < Repository.INSTANCES_MAX_PER_ELEMENT) {
				elementText = elementText.replaceAll("[\t\n\r\f]", "");
				while (elementText.startsWith(" ")) {
					elementText = elementText.replaceFirst(" ", "");
				}
				while (elementText.endsWith(" ")) {
					elementText = elementText.substring(0,
							elementText.length() - 1);
				}
				String connect = getInstanceConnect(path);
				saveInstanceToRepository(current, elementText, connect, importer);
			}
		}
		path.remove(current);
		if (path.size() == 0) {
			current = null;
		} else {
			current = path.get(path.size() - 1);
		}
		elementText = "";
	}

	private static void saveInstanceToRepository(Element element,
			String elementText, String connect, DataImport importer) {
		if (elementText.length() > 0) {
			element.addInstance(elementText);
			int id = -1;
			String attributeName = null;
			id = importer.insertInstance(connect, element.getId(), id,
					attributeName, elementText);
			importer.updateInstance(id, id);
		}
	}

	public void characters(char[] ch, int start, int end) throws SAXException {
		String str = new String(ch, start, end);
		elementText += str;
	}

	public void ignorableWhitespace(char[] ch, int start, int end)
			throws SAXException {
	//System.out.println("ignorableWhitespace(): " + s);
	}

	public void skippedEntity(String name) throws SAXException {
	//System.out.println("skippedEntity(): " + name);
	}

	String pathToString(List path) {
		if (path == null || path.isEmpty()) return null;
		String idStr = "";
		for (int i = 0; i < path.size() - 1; i++) {
			idStr += path.get(i) + ".";
		}
		idStr += path.get(path.size() - 1);
		return idStr;
	}

}