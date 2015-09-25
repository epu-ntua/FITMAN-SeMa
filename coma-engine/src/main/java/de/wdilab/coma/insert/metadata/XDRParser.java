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

package de.wdilab.coma.insert.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Source;

/**
 * This class extracts the metadata from a xdr file (xml based) as a model.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class XDRParser extends InsertParser {

	String targetNamespace = null;
	
	public XDRParser(boolean dbInsert){
		super(dbInsert, Source.TYPE_XDR);
	}

	@Override
	public int parseSingleSource(String filename, String schemaName, String author, String domain, String version, String comment) {
		targetNamespace=null;
		beforeParse(schemaName, filename);
		// parse information into temp tables (parse_<tablename>)
		parse();
	//	additional: author, domain, version
		setInformation(author, domain, version, comment);
		updateSourceURL(source_id, targetNamespace);
		// copy information into real tables (<tablename>), delete temp tables, close statements
		afterParse();	    
	    return source_id;
	}
	
	void parse() {
		parse(provider);
	}
		  
	void parse(String provider) {
		SAXParser saxParser = new SAXParser();
		XDRContentHandler contentHandler = new XDRContentHandler(provider, this);
		saxParser.setContentHandler(contentHandler);
		try {
			provider = provider.replaceAll(" ", "%20");
			saxParser.parse(provider);
		}
		catch (IOException e) { System.out.println("Error reading URI: " + e.getMessage()); }
		catch (SAXException e) {System.out.println("Error parsing: " + e.getMessage()); }
		
		String namespace = contentHandler.getTargetNamespace();
		if (namespace!=null) {
			if (targetNamespace==null)
				targetNamespace = namespace;
			else if (!targetNamespace.contains(namespace))
				targetNamespace += ", " + namespace;
		}
	}

}

class XDRContentHandler implements ContentHandler {
	  static String FILE_DELIM = "\t";
	  static String FILE_NULL = "\\N";   //for mysql only, otherwise set to null
	  static String FILE_NEWLINE = "\n"; //avoid println !!!

	  static String[] ignoreKeywords = {
	    "Schema", "description", "group",
	  };
	  static HashSet<String> ignoreKeywordList = new HashSet<String>(Arrays.asList(ignoreKeywords));

	  HashMap<List<Integer>, Integer> ids = new HashMap<List<Integer>, Integer>();
	  
	  class XDRElement{
	    int id = -1;
	    int elemType = -1;
	    String name = null;
	    String type = null;
	    String namespace = null;
	    String typespace = null;
	    String comment = null;
	  }

//	  int elemId = 0;
	  ArrayList<Integer> idPath = new ArrayList<Integer>();
	  ArrayList<String> namePath = new ArrayList<String>();
	  ArrayList<XDRElement> elementPath = new ArrayList<XDRElement>();

	  String sourceName = null;
	  InsertParser parser = null;
	  int entryCount = 0;
	  String elementText = null;

	  String targetNamespace = null;
	  ArrayList<String> prefixes = new ArrayList<String>();
	  ArrayList<String> namespaces = new ArrayList<String>();

	//  private Locator locator;

	  public XDRContentHandler(String fileName, InsertParser sourceParser) {
	    this.sourceName = fileName;
	    this.parser = sourceParser;
	  }

	  public String getTargetNamespace() {
	    return targetNamespace;
	  }

	  public void setDocumentLocator(Locator locator) {
	    //System.out.println("setDocumentLocator()");
//	    this.locator = locator;
	  }
	  public void startDocument()
	      throws SAXException {
	    //System.out.println("startDocument()");
	  }
	  public void endDocument()
	      throws SAXException {
	    //System.out.println("endDocument()");
	  }

	  public void processingInstruction(String target, String data)
	      throws SAXException {
//	    System.out.println("processingInstruction(): target: " + target + " and data: " + data);
	  }

	  public void startPrefixMapping(String prefix, String uri) {
	    //System.out.println("startPrefixMapping(): start [" + prefix + "] mapped to: [" + uri + "]");
	    if (! prefixes.contains(prefix)) {
	      prefixes.add(prefix);
	      namespaces.add(uri);
	    }
	  }
	  public void endPrefixMapping(String prefix) {
	    //System.out.println("endPrefixMapping(): end [" + prefix + "]");
	    int ind = prefixes.indexOf(prefix);
	    if (ind!=-1) {
	      prefixes.remove(ind);
	      namespaces.remove(ind);
	    }
	  }

	  public void startElement(String namespaceURI, String localName,
	                           String rawName, Attributes atts)
	      throws SAXException {
	    //System.out.println("startElement(): " + localName + "[" + namespaceURI + ", " + rawName +"]" );
	    //if (atts != null) {
	    //  for (int i = 0; i < atts.getLength(); i++) {
	    //    System.out.println("Attribute");
	    //    System.out.println(" - QName: " + atts.getQName(i));
	    //    System.out.println(" - Local Name: " + atts.getLocalName(i));
	    //    System.out.println(" - Namespace: " + atts.getURI(i));
	    //    System.out.println(" - Value: " + atts.getValue(i));
	    //  }
	    //}

	    if (localName.equals("Schema")) {
	      for (int i=0; i<atts.getLength(); i++) {
	        String attrName = atts.getLocalName(i);
	        String attrValue = atts.getValue(i);
	        if (attrName.equals("name")) targetNamespace = attrValue;
	      }
	    }
	    if (localName.equals("description")) elementText = new String();
	    if (ignoreKeywordList.contains(localName)) return;

	    XDRElement element = new XDRElement();
	    if (localName.equals("ElementType") || localName.equals("AttributeType")) {
	      for (int i=0; i<atts.getLength(); i++) {
	        String attrName = atts.getLocalName(i);
	        String attrValue = atts.getValue(i);
	        if (attrName.equals("name")) element.name = attrValue; //global definition
	        else if (attrName.equals("type")) element.type = attrValue;
	      }
	      element.elemType = Element.KIND_ELEMTYPE;
	    }
	    else if (localName.equals("element") || localName.equals("attribute")) {
	      for (int i=0; i<atts.getLength(); i++) {
	        String attrName = atts.getLocalName(i);
	        String attrValue = atts.getValue(i);
	        if (attrName.equals("type")) {
	          element.name = attrValue;
	          element.type = attrValue;
	        }
	      }
	      element.elemType = Element.KIND_ELEMENT;
	    }

	    if (element.name==null) element.name = localName;
	    
//	    element.id = ++elemId;
	    element.id =  parser.insertObject( parser.source_id, element.name, element.name, element.type, element.typespace,
	    		element.elemType, element.comment, null);   
	    	    
	    idPath.add(new Integer(element.id));
	    namePath.add(element.name);
	    elementPath.add(element);	    
	    
	    ids.put((ArrayList<Integer>) idPath.clone(),  element.id );
	  }

	  public void endElement(String namespaceURI, String localName, String rawName)
	      throws SAXException {
	    //System.out.println("endElement(): " + localName);
	    //System.out.println("ID Path   : " + idPath);
	    //System.out.println("Name Path : " + namePath);

	    XDRElement element = null;
	    //Setting values from inner elements
	    if (elementPath.size()>0) {
	      element = elementPath.get(elementPath.size()-1);
	      if (localName.equals("description"))
	        element.comment = elementText;
	    }

	    //Ignore elements with name from the ignoreList
	    if (ignoreKeywordList.contains(localName)) return;

	    //no element added by startElement
	    if (element==null) return;

	    String idStr = pathToString(idPath);
	    List<Integer>  parentIdPath = idPath.subList(0, idPath.size()-1);
//	    List parentNamePath = namePath.subList(0, namePath.size()-1);

	    //Check namespace prefix
	    int ind;
	    if (element.name!=null) {
	      ind = element.name.indexOf(':');
	      if (ind>0) {
	        //Only for element references, referenced element may be from other namespace
	        String prefix = element.name.substring(0, ind);
	        element.name = element.name.substring(ind+1);
	        ind = prefixes.indexOf(prefix);
	        if (ind!=-1) element.namespace = namespaces.get(ind);
	        else {
	          //System.out.println("endElement(): Ignore prefix: " + prefix + " of element " + element.name);
	          element.namespace = targetNamespace;
	        }
	      }
	      else element.namespace = targetNamespace;
	    }
	    if (element.type!=null) {
	      ind = element.type.indexOf(':');
	      if (ind>0) {
	        String prefix = element.type.substring(0, ind);
	        element.type = element.type.substring(ind+1);
	        ind = prefixes.indexOf(prefix);
	        if (ind!=-1) element.typespace = namespaces.get(ind);
	        else {
	          //System.out.println("endElement(): Ignore prefix: " + prefix + " of element " + element.name);
	          element.typespace = null;
	        }
	      }
	      else {
	        element.typespace = targetNamespace;
	        /*
	        ind = prefixes.indexOf(""); //default namespace
	        if (ind!=-1) element.typespace = (String)namespaces.get(ind);
	        else {
	          //System.out.println("endElement(): No default namespace for element " + element.name);
	          element.typespace = null;
	        }
	        */
	      }
	    }

	    //Set global element type
	    if (parentIdPath.isEmpty()) {
	      if (element.elemType==Element.KIND_ELEMENT)
	        element.elemType = Element.KIND_GLOBELEM;
	      else if (element.elemType==Element.KIND_ELEMTYPE)
	        element.elemType = Element.KIND_GLOBTYPE;
	    }

	    //load element
//	    parser.loadObject(sourceName, idStr,
//	                            element.name, element.type, element.namespace, element.typespace,
//	                            element.elemType, element.comment,
//	                            // TODO for XQuery 1
//	                            null, null, null, null, null, null);

	    int current_id = element.id;
	    if (current_id>-1){
	    	parser.updateObject( parser.source_id, element.id, idStr, element.name, element.type, element.typespace,
	    			element.elemType, element.comment, null);
	    } else {
	    	current_id =  parser.insertObject( parser.source_id, idStr, element.name, element.type, element.typespace,
		    		element.elemType, element.comment, null);
	    }
	    ids.put((ArrayList<Integer>) idPath.clone(), current_id);
	    
	    //load parent association
	    if (! parentIdPath.isEmpty()) {
//	      String parentIdStr = pathToString(parentIdPath);
//	      parser.loadLink(sourceName, idStr, sourceName, parentIdStr, 
//	    		  SourceRelationship.REL_IS_A, null);
	    	int parent_id = ids.get(parentIdPath);
	    	parser.insertLink(parser.sourcerel_id, parent_id, current_id);
	    }

	    //remove the last element from the path
	    idPath.remove(idPath.size()-1);
	    namePath.remove(namePath.size()-1);
	    elementPath.remove(elementPath.size()-1);
	  }

	  public void characters(char[] ch, int start, int end)
	      throws SAXException {
	    String str = new String(ch, start, end);
	    elementText += str;
	  }

	  public void ignorableWhitespace(char[] ch, int start, int end)
	      throws SAXException {
	    //String str = new String(ch, start, end);
	    //System.out.println("ignorableWhitespace(): " + s);
	  }
	  public void skippedEntity(String name)
	      throws SAXException {
	    //System.out.println("skippedEntity(): " + name);
	  }

	  String pathToString(List<Integer> path) {
	    if (path==null || path.isEmpty()) return null;
	    String idStr = "";
	    for (int i=0; i<path.size()-1; i++) {
	      idStr += path.get(i) + ".";
	    }
	    idStr += path.get(path.size()-1);
	    return idStr;
	  }
	}