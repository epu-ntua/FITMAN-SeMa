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

package de.wdilab.coma.structure.graph;

import java.util.Comparator;

import de.wdilab.coma.structure.Element;

/**
 * Compares two <code>Element</code>s, to sort them.
 * 
 * @author Sabine Massmann
 */
public class ElementComparator implements Comparator<Element> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Element arg1, Element arg2) {

        Element vertex1 = (Element) arg1;
        Element vertex2 = (Element) arg2;
        int id1 = vertex1.getId();
        int id2 = vertex2.getId();
        if (id1 < id2)
        	return -1;
        else if (id1 > id2)
        	return 1;
        return 0;
    }
}