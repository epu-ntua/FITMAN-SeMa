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

package de.wdilab.coma.matching.execution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.wdilab.coma.matching.SimilarityMeasure;
import de.wdilab.ml.impl.ObjectInstance;
import de.wdilab.ml.impl.mapping.mainmemory.MainMemoryMapping;
import de.wdilab.ml.impl.oi.SingleObjectObjectInstanceProvider;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * This class executes the direct application of a similarity measure
 * onto two values or value lists. 
 * 
 * @author Sabine Massmann
 */
public class Match2Values {

	static public float execute(int simmeasId, String value1, String value2){
		// put source objects into the source instance provider			
	    ObjectInstance oiA = new ObjectInstance("id", 1, "attr", value1);
	    IObjectInstanceProvider  oipA = new SingleObjectObjectInstanceProvider(oiA, null);
	    
		ObjectInstance oiB = new ObjectInstance("id", 2, "attr", value2);
	    IObjectInstanceProvider  oipB = new SingleObjectObjectInstanceProvider(oiB, null);

		// initiate mapping - here get the match results stored
	    final MainMemoryMapping mmm = new MainMemoryMapping(oipA, oipB);
	    
		IObjectMatcher matcher = SimilarityMeasure.getMatcher(simmeasId, null, null);
	    if (matcher==null) return -1;
	    try {
			// execute the matching over using the "filled" instance provider and write results into the mapping
			matcher.match( oipA, oipB, mmm);	
			
		    Iterator<IMappingEntry> it = mmm.iterator();
		    if (it.hasNext()){
		    	IMappingEntry me = it.next();
		    	return (float) me.getSimilarity().getSim();
		    }
		    // assumption that calculation did execute but did not find this pair as match because sim=0
		    return 0;
		} catch (MappingStoreException e) {
			System.err.println("Match2Values.execute() Error " + e.getMessage());
		}
		return -1;
	}
	
	static public float execute(int simmeasId, ArrayList<String> value1, ArrayList<String>  value2){
		if (value1==null || value2==null || value1.isEmpty() || value2.isEmpty()) return -1;
		String newValue1 = value1.toArray().toString();
		String newValue2 = value2.toArray().toString();
		return execute(simmeasId, newValue1, newValue2);
	}

	public static float execute(int simmeasId, HashMap<String, ArrayList<String>> value1,
			HashMap<String, ArrayList<String>> value2) {
		if (value1==null || value2==null || value1.isEmpty() || value2.isEmpty()) return -1;
		String newValue1 = "";
		for (String attr : value1.keySet()) {
			ArrayList<String> current = value1.get(attr);
			newValue1+= " " + current.toArray().toString();
		}
		String newValue2 = "";
		for (String attr : value2.keySet()) {
			ArrayList<String> current = value2.get(attr);
			newValue2+= " " + current.toArray().toString();
		}
		return execute(simmeasId, newValue1, newValue2);
	}
    
}
