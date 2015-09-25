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

package de.wdilab.coma.center.analysis;

import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This class determines the characteristics of a model. This includes the kind of
 * existing information as well as analyzes of the starting character and all characters
 * of the element names.
 * 
 * @author Sabine Massmann
 */
public class Characteristics {
	
    //	boolean name = false;
	public boolean structure = false;
	public boolean comments = false;
	public boolean datatypes = false;
	boolean instancesSimple = false;
	boolean instancesComplex = false;
	public boolean instances = false;
	public boolean synonyms = false;
	private ArrayList<Character> characters = null;
	ArrayList<Character> startingCharacters = null;
	// String domain =
	// int vertexCount =
	String countTypes = null;

	public Characteristics(Graph graph){

		setCharacters(graph.frequentCharacters());
		countTypes = graph.analyseDatatypes();
		startingCharacters=graph.frequentStartingCharacters();
		for (Iterator iterator = graph.getElementIterator(); iterator.hasNext();) {
			Element element = (Element) iterator.next();
			if (!comments && element.getComment()!=null){
				comments=true;
			}
			if (!datatypes && element.getType()!=null){
				datatypes=true;
			}
			if (!instancesSimple && element.hasAllInstancesSimple()){
				instancesSimple=true;
			}
			if (!instancesComplex &&  element.hasAllInstancesComplex()){
				instancesComplex=true;
			}
			
			if (instancesSimple || instancesComplex){
				instances=true;
			}
			if (!structure && graph.getParents(element)!=null){
				structure=true;
			}
			if (comments && instancesSimple  && instancesComplex && structure){
				break;
			}
		}
	}

	boolean equal(Characteristics property){
		if (structure != property.structure){
			return false;
		}
		if (comments != property.comments){
			return false;
		}
		if (datatypes != property.datatypes){
			return false;
		}
		if (instancesSimple != property.instancesSimple){
			return false;
		}
		if (instancesComplex != property.instancesComplex){
			return false;
		}
		HashSet<Character>  overlap = new HashSet<Character>();
		overlap.addAll(getCharacters());
		overlap.retainAll(property.getCharacters());
		if (overlap.size()<getCharacters().size()){
			return false;
		}
		overlap = new HashSet<Character>();
		overlap.addAll(startingCharacters);
		overlap.retainAll(property.startingCharacters);
		if (overlap.size()<startingCharacters.size()){
			return false;
		}
		return true;
	}

	public boolean sameLanguage(Characteristics property){
		HashSet<Character>  overlap = new HashSet<Character>();
		overlap.addAll(getCharacters());
		overlap.retainAll(property.getCharacters());
		if (overlap.size()<getCharacters().size()){ // no different character allowed
			return false;
		}
		overlap = new HashSet<Character>();
		overlap.addAll(startingCharacters);
		overlap.retainAll(property.startingCharacters);
		if (overlap.size()<startingCharacters.size()){ // no different character allowed
			return false;
		}
		return true;
	}

	public boolean similarLanguage(Characteristics property){
		HashSet<Character>  overlap = new HashSet<Character>();
		overlap.addAll(getCharacters());
		overlap.retainAll(property.getCharacters());
		if (overlap.size()<(getCharacters().size()-3)){ // x different character allowed
			return false;
		}
		overlap = new HashSet<Character>();
		overlap.addAll(startingCharacters);
		overlap.retainAll(property.startingCharacters);
		if (overlap.size()<(startingCharacters.size()-3)){ // x different character allowed
			return false;
		}
		return true;
	}

	public int getTrues(){
		int count = 0;
		if (structure) count++;
		if (comments) count++;
		if (datatypes) count++;
		if (instancesSimple) count++;
		if (instancesComplex) count++;
		return count;
	}

	public String toString(){
		return getCharacters() + "  " + startingCharacters +  " - Comm:" + comments+  " - Struct:" + structure +  " - DataType:" + datatypes +
		" - InstSimple:" + instancesSimple + " - InstComplex:" + instancesComplex ;
	}

	public void setCharacters(ArrayList<Character> characters) {
		this.characters = characters;
	}

	public ArrayList<Character> getCharacters() {
		return characters;
	}

	public String getCountTypes() {
		return countTypes;
	}
}
