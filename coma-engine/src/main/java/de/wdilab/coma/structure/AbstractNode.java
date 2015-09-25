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
 * The Node class is an abstract class for nodes which can be single elements (leaves) or the
 * entire path. A node is the atomic part of a schema.
 * 
 * @author Patrick Arnold
 */
public abstract class AbstractNode {

	/** The unique path of the node (e.g. "person.name.firstname"). */
	protected String path;
	
	/** The comment of the node (some nodes may have a comment or annotation). */
	protected String comment;
	
	/** The data type of the node (e.g. string, integer, long, date etc.). */
	protected String dataType;

	
	
	/**
	 * Returns a unique id for the node, so the entire path of the node 
	 * (e.g. student.name.firstName).
	 * @return The unique representation of the node (the path of the node).
	 */
	public abstract String getUniqueRepresentation();
	
	
	/**
	 * Returns the comment of the node.
	 * @return The comment of the node.
	 */
	public abstract String getComment();
	
	
	/**
	 * Returns the data type of the node. 
	 * @return The data type of the node.
	 */
	public abstract String getDataType();
	
	
	/**
	 * Returns the last element of the node (the actual node name).
	 * @return The last element of the node.
	 */
	public abstract String getElement();
	
	
	/**
	 * Returns the depth of the node, so the node level of the name, starting at 0.
	 * @return The node level of the node (e.g. student.name.firstName has node level 2). Does
	 * only work if the complete path is known.
	 */
	public abstract int getDepth();
	
	
	
}