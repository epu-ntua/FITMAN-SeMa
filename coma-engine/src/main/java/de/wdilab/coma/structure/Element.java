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

package de.wdilab.coma.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Element contains most of the metadata of a schema e.g. concept names, 
 * datatypes, comments and (if existing and loaded) instances.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class Element extends AbstractNode{
	// constants
	 public static final int UNDEF = -1;
	
	 // Predefined values for field kind
	 public static final int KIND_ELEMENT = 1;
	 public static final int KIND_ELEMTYPE = 2;
	 public static final int KIND_GLOBELEM = 3;
	 public static final int KIND_GLOBTYPE = 4;
	 public static final int KIND_ELEMPATH = 5; // Will be
	
	 public static String kindToString(int kind) {
		 switch (kind) {
		 case KIND_ELEMENT : return "Element";
		 case KIND_ELEMTYPE : return "ElemType";
		 case KIND_GLOBELEM : return "GlobElem";
		 case KIND_GLOBTYPE : return "GlobType";
		 case KIND_ELEMPATH : return "ElemPath";
		 default : return "LOADED";
		 }
	 }
	 
	 public static int stringToKind(String kindStr) {
		 if (kindStr == null) return KIND_ELEMENT;
		 if (kindStr.equals("Element")) return KIND_ELEMENT;
		 if (kindStr.equals("ElemType")) return KIND_ELEMTYPE;
		 if (kindStr.equals("GlobElem")) return KIND_GLOBELEM;
		 if (kindStr.equals("GlobType")) return KIND_GLOBTYPE;
		 if (kindStr.equals("ElemPath")) return KIND_ELEMPATH;
		 return KIND_ELEMENT;
	 }

	/** label - needed for implementing Vertex */
	String name = null;
	String accession = null;
	String type = null;
	String typespace = null;
	int kind = Element.UNDEF;
	String comment = null;
	String synonyms = null;
	int sourceid = Source.UNDEF;
	// maybe calculate statics depending on preprocessing (at the moment only one element in all graphs)
	Statistics statistics;
	
    /** identification number */
    int id;
    
    // for instance-based matching
    /** direct instances of this element */
    ArrayList<String> instancesSimple = new ArrayList<String>();
    HashMap<String, ArrayList<String>> instancesComplex = new HashMap<String, ArrayList<String>>();
    /** indirect instances are all the direct instances of the element children */
    ArrayList<String> indirectInstancesSimple = new ArrayList<String>();
    HashMap<String, ArrayList<String>> indirectInstancesComplex = new HashMap<String, ArrayList<String>>();
    
    // Constructors
    /**
     * @param id
     * Constructor setting only id
     */
    public Element(int id) {
        this.id = id;
    }
    
    /**
     * @param name
     * Constructor setting only name (id is UNDEF)
     */
    public Element(String name) {
        id = UNDEF;
        this.name = name;
    }
    
    /**
     * @param id
     * @param source_id
     * @param name
     * @param accession
     * @param type
     * @param typespace
     * @param kind
     * @param comment
     * @param synonyms
     * Constructor setting all given input
     */
    public Element(int id, int source_id, String name, String accession,
    		String type, String typespace, int kind, String comment, String synonyms) {
        this.id = id;
        sourceid = source_id;
        this.name = name;
        this.accession = accession;
        this.type = type;
        this.typespace = typespace;
        this.kind = kind;
        this.comment = comment;
        this.synonyms = synonyms;
    }    
    
    // simple getter
	public String getName() { return name; }	
    public int getId() { return id; }
    public String getComment() { return comment; }
    public String getType() { return type; }
    public int getKind() { return kind; }
    public int getSourceId() { return sourceid; }
    public String getAccession() { return accession; }
    public String getTypespace() { return typespace; }
    public Statistics getStatistics() { return statistics; }
	public String getSynonym(){return synonyms;}
	public String getNameSyn() {
		if (synonyms==null) return name;
		else return name + " " + synonyms.replace("|", " ");
	}
	// simple setter
	public void setLabel(String label) { this.name = label; }
    public void setId(int id) {	this.id = id; }
    public void setKind(int kind) {	this.kind = kind; }
    public void setAccession(String accession) { this.accession = accession; }
    public void setComment(String comment) { this.comment = comment; }
    public void setType(String type) { this.type = type; }
    public void setTypespace(String typespace) { this.typespace = typespace; }
    public void setSynonyms(String synonyms) { this.synonyms = synonyms; }
    public void setStatistics(Statistics statistics) { this.statistics = statistics; }
    public void setIndirectInstancesSimple(ArrayList<String> instances) {
        this.indirectInstancesSimple = instances;
    }
    public void setIndirectInstancesComplex(HashMap<String, ArrayList<String>> instances) {
        this.indirectInstancesComplex = instances;
    }    
    
    // functions
	public boolean hasLabel() {
		if (name!=null){
			return true;
		}
		return false;
	}
    
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
        if (type != null)
            return id + "[" + name + ":" + type + "]";
        else
            return id + "[" + name + "]";
		
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj==null || !(obj instanceof Element)){
			// if given object null or not an element
			return false;
		}
		// use only id as indicator, maybe later compare all information?
		if (((Element)obj).getId()!=getId()){
			return false;
		}		
		return true;
	}
	
	
	/*
	 * OVERWRITTEN METHODS FROM CLASS AbstractNode.java (COMA MAPPING). The class GraphPathImpl
	 * is now an AbstractNode, overwriting the methods used for the coma mapping process. 
	 * Patrick, Mar 14
	 */
	
	
	@Override
	public String getUniqueRepresentation() {   // e.g. Student.Name.FirstName
		return getAccession();
	}

	
//	already exists in Element
//	@Override
//	public String getComment() {   // Temporarily not needed in the coma.mapping package.
//		return null;	
//	}

	
	
	@Override
	public String getDataType() {   // Temporarily not needed in the coma.mapping package.
		return getType();	
	}

	
	
	@Override
	public String getElement() {    // e.g. FirstName
		return getName();	
	}

	
	
	@Override
	public int getDepth() {   // e.g. 2 for Student.Name.FirstName
		return -1;
	}
	
    public void addInstance(String instance) {
        if (instance != null && instance.length() > 0) {
        	if (instancesSimple==null){
        	   instancesSimple = new ArrayList<String>();
        	}        
            instancesSimple.add(instance);
        }
    }   
	
    public void addInstance(String attribute, String instance) {
        if (instance == null) {
        	return;        	
        }
        if (attribute==null){
        	if (instancesSimple==null){
          	   instancesSimple = new ArrayList<String>();
          	}
        	instancesSimple.add(instance);
        	return;
        }
    	if (instancesComplex==null){
    		instancesComplex = new HashMap<String, ArrayList<String>>();
      	}
        ArrayList values = instancesComplex.get(attribute);
        if (values==null){
        	values = new  ArrayList();
        }
        values.add(instance);
        instancesComplex.put(attribute, values);
    }
	
    public void clearInstances() { 
    	if (instancesSimple!=null){
    		instancesSimple.clear();
    	}
    	if (indirectInstancesSimple!=null) {
    		indirectInstancesSimple.clear();
    	}
    	if (instancesComplex!=null) {
   	 		instancesComplex.clear();
    	}
    	if (indirectInstancesComplex!=null) {
   	 		indirectInstancesComplex.clear();
    	}
   }
    
    // use - where / when to differentiate if complex or simple
    public ArrayList getDirectInstancesSimple() { return instancesSimple; }
    
    public HashMap<String, ArrayList<String>> getDirectInstancesComplex() { return instancesComplex;  }
    
    public int getDirectInstancesComplexMaxSize(){
    	int max=0;
    	for (Iterator iterator = instancesComplex.keySet().iterator(); iterator.hasNext();) {
			String attribute = (String) iterator.next();
			int size = ((ArrayList)instancesComplex.get(attribute)).size();
			if (max<size){
				max=size;
			}
		}
    	return max;
    }
    
    public ArrayList getIndirectInstancesSimple() { return indirectInstancesSimple; }
    
    public HashMap<String, ArrayList<String>> getIndirectInstancesComplex() { return indirectInstancesComplex; }
    
    public int getIndirectInstancesComplexMaxSize(){
    	int max=0;
    	for (Iterator iterator = indirectInstancesComplex.keySet().iterator(); iterator.hasNext();) {
			String attribute = (String) iterator.next();
			int size = ((ArrayList)indirectInstancesComplex.get(attribute)).size();
			if (max<size){
				max=size;
			}
		}
    	return max;
    }
    
    public ArrayList getAllInstancesSimple() {
      ArrayList allInstances = new ArrayList(getDirectInstancesSimple());
      allInstances.addAll(getIndirectInstancesSimple());
      return allInstances;
    }
    
    public boolean hasAllInstancesSimple(){    	return (hasDirectInstancesSimple() || hasIndirectInstancesSimple());    }
    
    public boolean hasDirectInstancesSimple(){
    	if (instancesSimple.size()>0){
    		return true;
    	}
    	return false;
    }
    
    public boolean hasIndirectInstancesSimple(){
    	if (indirectInstancesSimple.size()>0){
    		return true;
    	}
    	return false;
    }
    
    public boolean hasAllInstancesComplex(){    	return (hasDirectInstancesComplex() || hasIndirectInstancesComplex());    }
    
    public boolean hasDirectInstancesComplex(){
    	if (instancesComplex.size()>0){
    		return true;
    	}
    	return false;
    }
    
    public boolean hasIndirectInstancesComplex(){
    	if (indirectInstancesComplex.size()>0){
    		return true;
    	}
    	return false;
    }
    
    public HashMap<String, ArrayList<String>> getAllInstancesComplex() {
    	HashMap<String, ArrayList<String>> allInstances = new HashMap<String, ArrayList<String>>();
    	if (instancesComplex.keySet().size()>0){
	    	for (Iterator iterator = instancesComplex.keySet().iterator(); iterator.hasNext();) {
				String attribute = (String) iterator.next();
				ArrayList<String> values = new ArrayList<String>(instancesComplex.get(attribute));
				ArrayList<String> instances = indirectInstancesComplex.get(attribute);
				if (instances!=null) {
					values.addAll(instances);
				}
				allInstances.put(attribute, values);
			}
    	} else if (indirectInstancesComplex.keySet().size()>0){
	    	for (Iterator iterator = indirectInstancesComplex.keySet().iterator(); iterator.hasNext();) {
				String attribute = (String) iterator.next();
				ArrayList<String> values = new ArrayList<String>(indirectInstancesComplex.get(attribute));
				allInstances.put(attribute, values);
			}
    	}
        return allInstances;
      }    
    
}
