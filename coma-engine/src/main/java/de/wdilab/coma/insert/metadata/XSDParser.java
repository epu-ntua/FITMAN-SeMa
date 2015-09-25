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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Source;

/**
 * This class extracts the metadata from a xsd file (xml based) as a model.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class XSDParser extends InsertParser {	
	
	String targetNamespace = null;
	
	public XSDParser(boolean dbInsert){
		super(dbInsert, Source.TYPE_XSD);
	}
	
	public void parseCompositeSources(String directory, String schemaName) {
	    parseCompositeSources(directory, schemaName, null, null, null, null);
	}
	
	public void parseCompositeSources(String directory, String schemaName, String author, String domain, String version, String comment) {
	    File currentDir = new File(directory);
	    if (! currentDir.exists()) {
	      System.out.println("parseCompositeXSD(): Directory " + directory + " does not exist!");
	      return;
	    }

	    FilenameFilter filter = new FilenameFilter() {
	      public boolean accept(File dir, String name) {
	        return (name.endsWith(".xsd"));
	      }
	    };
	    String[] infiles = currentDir.list(filter);
	    parseCompositeSources(directory, infiles, schemaName, author, domain, version, comment);
	}
	
	void parseCompositeSources(String directory, String[] filename, String schemaName, String author, String domain, String version, String comment) {		
		targetNamespace=null;
		beforeParse(schemaName, directory);
		for (int i = 0; i < filename.length; i++) {			    
			parse(directory+"/"+filename[i]);	    
	    }
		setInformation(author, domain, version, comment);	
		updateSourceURL(source_id, targetNamespace);
		afterParse();	    
	}

	@Override
	public int parseSingleSource(String filename, String schemaName, String author, String domain, String version, String comment) {
		targetNamespace=null;
		beforeParse(schemaName, filename);
		parse();
	//	 additional: author, domain, version
		setInformation(author, domain, version, comment);
		updateSourceURL(source_id, targetNamespace);
		afterParse();
	    return source_id;
	}

	  void parse() {
		  parse(provider);
	  }
	  
	  void parse(String provider) {
		  SAXParser saxParser = new SAXParser();
		  XSDContentHandler contentHandler = new XSDContentHandler(provider, this);
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

class XSDContentHandler implements ContentHandler {
	  static String FILE_DELIM = "\t";
	  static String FILE_NULL = "\\N";   //for mysql only, otherwise set to null
	  static String FILE_NEWLINE = "\n"; //avoid println !!!
	  
	  static String[] ignoreKeywords = {
	      "include", "import",
	      "annotation", "documentation", "appInfo",
	      "sequence", "all", "choice",
	      "pattern", "minLength", "maxLength", "enumeration",
	      "restriction", "extension",
	      "complexContent", "simpleContent",
//	      "key", "keyref", "unique", "selector", "field",
	  };
	  static HashSet<String> ignoreKeywordList = new HashSet<String>(Arrays.asList(ignoreKeywords));

	  HashMap<List<Integer>, Integer> ids = new HashMap<List<Integer>, Integer>();
	  
	  class XSDElement{
	    int id = -1;
	    int elemType = -1;
	    String name = null;
	    String type = null;
	    String namespace = null;
	    String typespace = null;
	    String comment = null;
	    // for XQuery 1
//	    String localName = null, minOccurs = null, maxOccurs=null, fixed=null, defaultValue=null, useValue=null;
	  }

//	  int elemId = 0;
	  ArrayList<Integer> idPath = new ArrayList<Integer>();
	  ArrayList<String> namePath = new ArrayList<String>();
	  ArrayList<XSDElement> elementPath = new ArrayList<XSDElement>();

	  String sourceName = null;
	  InsertParser parser = null;
	  String elementText = null;
	  String baseType = null;

	  String targetNamespace = null;
	  ArrayList<String> prefixes = new ArrayList<String>();
	  ArrayList<String> namespaces = new ArrayList<String>();

	  boolean inlinedMode = false;
	  Stack<XSDElement> inlinedStack = new Stack<XSDElement>();

//	  IdentityConstraint currentIC=null;
	  
	//  private Locator locator;

	  public XSDContentHandler(String sourceName, InsertParser insertParser) {
	    this.sourceName = sourceName;
	    this.parser = insertParser;
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
	    //System.out.println("processingInstruction(): target: " + target + " and data: " + data);
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

          String isElement=null;
	    if (localName.equals("schema")) {
	      for (int i=0; i<atts.getLength(); i++) {
	        String attrName = atts.getLocalName(i);
	        String attrValue = atts.getValue(i);
	        if (attrName.equals("targetNamespace"))
	          targetNamespace = attrValue;
	      }
	    }
	    else if (localName.equals("documentation")) elementText = new String();
	    else if (localName.equals("restriction") || localName.equals("extension")) {
	      for (int i=0; i<atts.getLength(); i++) {
	        String attrName = atts.getLocalName(i);
	        String attrValue = atts.getValue(i);
	        if (attrName.equals("base")) baseType = attrValue;
	      }
	    }

	    if (ignoreKeywordList.contains(localName)) return;

	    XSDElement element = new XSDElement();
	    if (localName.equals("complexType") || localName.equals("simpleType")) {
	      for (int i=0; i<atts.getLength(); i++) {
	        String attrName = atts.getLocalName(i);
	        String attrValue = atts.getValue(i);
	        if (attrName.equals("name")) {  //global definition
	          element.name = attrValue;
	        }
	      }
	      element.elemType = Element.KIND_ELEMTYPE;
	    }
	    else if (localName.equals("element") || localName.equals("attribute") ||
	             localName.equals("group") || localName.equals("attributeGroup")) {
	      String name=null, type=null, ref=null;

            //morfoula  tha valw sti stili comment an einai attr(isElement=no)

            if (localName.equals("attribute"))
                isElement="no";


	      // for XQuery 1
//	      element.localName = localName;
	      for (int i=0; i<atts.getLength(); i++) {
	        String attrName = atts.getLocalName(i);
	        String attrValue = atts.getValue(i);
	        if (attrName.equals("name")) name = attrValue;
	        else if (attrName.equals("type")) type = attrValue;
	        else if (attrName.equals("ref")) ref = attrValue;
//	        // for XQuery 1
//	        else if (attrName.equals("minOccurs")) element.minOccurs = attrValue;
              //morfoula wanna know about maxOccurs
            else if (attrName.equals("maxOccurs")) {
//                if (isElement!=null)
//                    System.out.println("nooooo ----- noooooo ----- nooooo ----------- noooo ------");
                if (attrValue.contains("unbound"))
                    isElement="more";
                else if (Integer.parseInt(attrValue)>1)
                    isElement="more";
            }
//	        else if (attrName.equals("maxOccurs")) element.maxOccurs = attrValue;
//	        else if (attrName.equals("fixed")) element.fixed = attrValue;
//	        else if (attrName.equals("default")) element.defaultValue = attrValue;
//	        else if (attrName.equals("use")) element.useValue = attrValue;
	      }
	      if (name!=null) {
	        element.name = name;
	        element.type = type;
	      }
	      else if (ref!=null) {
	        element.name = ref;
	        element.type = ref;
	      }
	      element.elemType = Element.KIND_ELEMENT;
//	    } else if (localName.equals("unique") || localName.equals("key") ||
//	            localName.equals("keyref")) {
//	    	// 
//	    	currentIC = new IdentityConstraint(localName);
//		    String idStr = pathToString(idPath);
//		    currentIC.setElement(idStr);
//	        for (int i=0; i<atts.getLength(); i++) {
//	            String attrName = atts.getLocalName(i);
//	            String attrValue = atts.getValue(i);
//	            if (attrName.equals("id")) currentIC.setId(attrValue);
//	            else if (attrName.equals("name"))currentIC.setName(attrValue);
//	            else if (attrName.equals("refer")) currentIC.setRefer(attrValue);
//	        }
//	    } else if (localName.equals("field")){
//	    	if (currentIC!=null){
//	    		String xpath = atts.getValue("xpath");
//	    		currentIC.addField(xpath);
//	    	}
//	    } else if (localName.equals("selector")){
//	    	if (currentIC!=null){
//	    		String xpath = atts.getValue("xpath");
//	    		currentIC.setSelector(xpath);
//	    	}
	    }

	    if (element.name==null) {  //inlined mode
	      inlinedMode = true;
	      element.id = -1;
	      inlinedStack.push(element);
	      return;
	    }

//	    element.id = ++elemId;	    
	    element.id =  parser.insertObject( parser.source_id, element.name, element.name, element.type, element.typespace,
	    		element.elemType, isElement, null);
	    
	    idPath.add(new Integer(element.id));
	    namePath.add(element.name);
	    elementPath.add(element);
	    
	    ids.put((ArrayList<Integer>) idPath.clone(),  element.id );

	    if (inlinedMode) inlinedStack.push(element);
	  }

	  public void endElement(String namespaceURI, String localName, String rawName)
	      throws SAXException {
	    //System.out.println("endElement(): " + localName);
	    //System.out.println("ID Path     : " + idPath);
	    //System.out.println("Name Path   : " + namePath);

	    XSDElement element = null;
	    //Set values parsed from child elements
	    if (elementPath.size()>0) {
	      element = elementPath.get(elementPath.size()-1);
	      if (localName.equals("documentation"))
	        element.comment = elementText;
	      else if (localName.equals("restriction") || localName.equals("extension"))
	        element.type = baseType;
	    }    

//	    if (localName.equals("unique") || localName.equals("key") ||
//	            localName.equals("keyref")) {
//	    	if (currentIC!=null 
//	    			&& localName.equals(currentIC.getType())){
////	    		System.out.println(currentIC.toString());
//	    		sourceParser.loadIdConstraint(sourceName, currentIC);
//	    		currentIC=null;
//	    	}
//	    }
	    //Ignore elements from the ignore list
	    if (ignoreKeywordList.contains(localName)) return;

	    //Check for inlined definition
	    if (! inlinedStack.isEmpty()) {
	      XSDElement inlinedElement = inlinedStack.pop();
	      if (inlinedStack.isEmpty()) inlinedMode = false;
	      if (inlinedElement.id==-1) return;  //inlined element
	    }

	    //No element added by startElement
	    if (element==null) return;

	    String idStr = pathToString(idPath);
	    List<Integer> parentIdPath = idPath.subList(0, idPath.size()-1);
//	    List parentNamePath = namePath.subList(0, namePath.size()-1);

	    //Check namespace prefix
	    int ind;
	    if (element.name!=null) {
	      ind = element.name.indexOf(':');
	      if (ind>0) {
	        //Only for element references, referenced element may be from other namespace
//	        String prefix = element.type.substring(0, ind); 
	        String prefix = element.name.substring(0, ind); // BUGFIX (?!?)
	        element.name = element.name.substring(ind+1);
	        ind = prefixes.indexOf(prefix);
	        if (ind!=-1) element.namespace = namespaces.get(ind);
	        else {
	          //System.out.println("endElement(): Ignore prefix: " + prefix + " of element " + element.name);
	          element.namespace = null;
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

//	    if ( element.localName!=null ||  element.minOccurs!=null ||  element.maxOccurs!=null ||  element.fixed!=null ||  element.defaultValue!=null ||  element.useValue!=null )
//	    System.out.print("");
	    
	    //load element
//	    insertParser.loadObject(sourceName, idStr, element.name, element.type, element.namespace, element.typespace, element.elemType, element.comment,
//	                            // for XQuery 1
//	                            element.localName,  element.minOccurs,  element.maxOccurs,  element.fixed,  element.defaultValue,  element.useValue);
	    
//	    int current_id =  insertParser.insertObject( insertParser.source_id, idStr, element.name, element.type, element.typespace,
//	    		element.elemType, element.comment, null);
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
//	      insertParser.loadLink(sourceName, idStr, sourceName, parentIdStr, 
//	    		  SourceRelationship.REL_IS_A, null);
	    	int parent_id = ids.get(parentIdPath);
	    	parser.insertLink(parser.sourcerel_id, parent_id, current_id);
	    }

	    //remove current element from processing queue
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
