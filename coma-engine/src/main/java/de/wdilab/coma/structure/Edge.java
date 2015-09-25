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


/**
 * Edge where we can get the source and target element
 * 
 * @author Sabine Massmann
 */
public class Edge{

	static final public int IS_A = 1;
	static final public int PART_OF = 2;
	
	// source object
	Element source = null;
	// target object
	Element target = null;
	// type, e.g. is-a, part-of (normally not needed)
	String type = null;
	
	/**
	 * @param source
	 * @param target
	 * setting source and target to given elements
	 */
	public Edge(Element source, Element target){
		this.source = source;
		this.target = target;
	}

	/**
	 * @param source
	 * @param target
	 * setting source and target to given elements
	 */
	public Edge(Element source, Element target, String type){
		this.source = source;
		this.target = target;
		this.type = type;
	}
	
	// simple getter
	public Element getSource() { return source; }
	public Element getTarget(){	return target;}
	public String getType() { return type; }
	
	public void setType(String type) { this.type=type; }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s = "(";
		if (source!=null){
			s+=source.toString();
		} else {
			s+="null";
		}
		s+=" : ";
		if (target!=null){
			s+=target.toString()+")";
		} else {
			s+="null)";
		}
		return s;
	}
}
