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

import java.util.Arrays;
import java.util.List;

/**
 * This class represents the meta information of a source relationship,
 * thus match result. This includes the source and target model and a name.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class SourceRelationship {
	
	public static final int UNDEF = -1;

	public static final int REL_IS_A       = 1;
	public static final int REL_CONTAINS   = 2;
	public static final int REL_MATCHRESULT   = 3;
	public static final int REL_MAPPING   = 4;
	
	public static final int REL_INTERNAL = 5;
	
	  public static final Integer[] ALL_RELS = {
	      REL_CONTAINS, REL_IS_A
	  };
	  public static final List<Integer> ALL_RELLIST = Arrays.asList(ALL_RELS);
	
	int id = -1;
	int type = -1;
	int srcId = -1;
	int trgId = -1;
	String provider = null;
	String name = null;
	String comment = null;
	int preprocessing = -1;
	String date = null;
	String status = null;
	int objRelCount= -1, obj1Count= -1, obj2Count= -1;
	
	public SourceRelationship(int sourcerel_id, int source1Id, int source2Id, 
			int type, String name, String comment, String provider, int preprocessing, 
			String date, String status) {
		this.id = sourcerel_id;
		this.srcId = source1Id;
		this.trgId = source2Id;
		this.type =type;
		this.name = name;
		this.comment = comment;
		this.provider = provider;		
		this.preprocessing = preprocessing;
		this.date = date;
		this.status = status;
	}
	
	// simple getter
	public int getId() { return id; }
	public int getType() { return type; }
	public int getSourceId() { return srcId; }
	public int getTargetId() { 	return trgId; }
	public String getProvider() { return provider; }
	public String getName() { return name; }
	public String getComment() { return comment; }
	public int getPreprocessing() { return preprocessing; }
	public int getObjectRelCount() { return objRelCount; }
	
	// simple setter
	public void setId(int id) { this.id = id; }
	public void setType(int type) { this.type = type; }
	public void setSourceId(int srcId) { this.srcId = srcId; }
	public void setTargetId(int trgId) { this.trgId = trgId; }
	public void setProvider(String provider) { this.provider = provider; }
	public void setComment(String comment) { this.comment = comment; }
	public void setObjectRelCount(int objRelCount) { this.objRelCount = objRelCount; }
	public void setObject1Count(int obj1Count) { this.obj1Count = obj1Count; }
	public void setObject2Count(int obj2Count) { this.obj2Count = obj2Count; }

	public String toString() {
		return id + ": " + name + "("+srcId+" "+typeToString(type)+" " + trgId+")";
	}
	
	
    /**
     * @param type
     * @return the string representation for the type
     */
    public static String typeToString(int type) {
        switch (type) {
            case REL_IS_A:            	return "IS_A";
            case REL_CONTAINS:        	return "CONTAINS";
            case REL_MATCHRESULT:        	return "MATCHRESULT";
            case REL_MAPPING:        	return "MAPPING";
            case REL_INTERNAL:        	return "INTERNAL";
            default: return "UNDEF";
          }
    }
    
    /**
     * @param type
     * @return the id for the given string representation of a type
     */
    public static int stringToType(String type) {
        if (type==null) return UNDEF;
        else if (type.startsWith("IS_A"))      return REL_IS_A;
        else if (type.startsWith("CONTAINS"))  return REL_CONTAINS;
        else if (type.startsWith("MATCHRESULT"))      return REL_MATCHRESULT;
        else if (type.startsWith("MAPPING"))      return REL_MAPPING;
        else if (type.startsWith("INTERNAL"))      return REL_INTERNAL;
        return UNDEF;
      }
	  
}
