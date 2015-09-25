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

/**
 * This class mainly calculates the statistic for a element of a graph
 * by analyzing e.g. the number of parent, child and successor elements.
 * The configuration has been change over the years due to evaluation results. 
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class Statistics {

	  //Structure statistics
	  //upwards statistics: context
	//  public static final int DIST       = 0;
	//  public static final int UP_CNT   = 0;
//	  public static final int PARENT_CNT = 0;
	  //downwards statistics
	//  public static final int DOWN_CNT = PARENT_CNT+1;
//	  public static final int DEPTH      = DOWN_CNT+1;
	  
	  public static final int DEPTH      = 0;
	  public static final int CHILD_CNT  = DEPTH+1;
	  public static final int SUB_CNT    = CHILD_CNT+1;
	  public static final int INNER_CNT  = SUB_CNT+1;
	  public static final int LEAF_CNT   = INNER_CNT+1;
//	  public static final int SIBL_CNT 	 = LEAF_CNT+1;
	//  public static final int SIBL_POS 	 = 9;
	//  public static final int SIBL_BEFORE 	 = 9;
	//  public static final int SIBL_AFTER 	 = 10;
	  
	     // 2005 version, PO matching: DEPTH, CHILD_CNT, SUB_CNT, INNER_CNT, LEAF_CNT
	     // 2008 version: CHILD_CNT, SUB_CNT, INNER_CNT, LEAF_CNT, SIBL_CNT
	  
//	  public static final int STAT_NUM   = SIBL_CNT+1;
	  public static final int STAT_NUM   = LEAF_CNT+1;
	
//	float[] values = null;
	  int[] values = null;
	
	  int code = 0;
	  
	public Statistics(int[] values){
		this.values = values;
//		hashCode();
		for (int i = 0; i < values.length; i++) {
			code+=values[i]*Math.pow(10,i);
		}
	}
	
	public int size(){ 
		if (values==null) return 0;
		return values.length;
	}
	
	public int[] getValues(){ return values; }
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Statistics)) return false;
		Statistics stat = (Statistics) obj;
		if (stat.size()!=this.size()) return false;
		int[] statValues = stat.getValues();
		for (int i = 0; i < values.length; i++) {
			if (statValues[i]!=values[i]) return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		// done only once, assumption values don't change
//		code=0;
//		for (int i = 0; i < values.length; i++) {
//			code+=values[i]*Math.pow(10,i);
//		}
		return code;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
//			if (i>0){
//				buffer.append(("\t" +  values[i]).replace(".", ","));
//			} else{
//				buffer.append(("" +  values[i]).replace(".", ","));
//			}
			if (i>0){
				buffer.append(", " +  values[i]);
			} else{
				buffer.append("[" +  values[i]);
			}
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	
	   //--------------------------------------------------------------------------//
	   // Compute structure statistics                                             //
	   //--------------------------------------------------------------------------//
	  public static Statistics computeStatistics(Graph graph, Element elem) {
	     if (elem==null) return null;

	     int[] values = new int[STAT_NUM];
	     /*     
	     //DIST && UP_CNT
	     ArrayList upPaths = getUpRootPaths(vertex);
	     if (upPaths !=null && upPaths.size()>0) {
//	       values[UP_CNT] = upPaths.size();
	       for (int i = 0; i < upPaths.size(); i++)
	         if (((ArrayList) upPaths.get(i)).size() > values[DIST])
	           values[DIST] = ((ArrayList) upPaths.get(i)).size();
	     }
	*/
//	     //PARENT_CNT
//	     ArrayList parents = graph.getParents(elem);
//	     if (parents == null)
//	       values[PARENT_CNT] = 0;
//	     else
//	       values[PARENT_CNT] = parents.size();

	     //DEPTH && DOWN_CNT
	     ArrayList<Path> downPaths = graph.getDownLeafPaths(elem);
	     if (downPaths != null && downPaths.size() > 0) {    	 
//	    	 values[DOWN_CNT] = downPaths.size();
	       for (int i = 0; i < downPaths.size(); i++)
	         if (downPaths.get(i).size() > values[DEPTH])
	           values[DEPTH] = downPaths.get(i).size();
//	     } else {
//	    	 values[DOWN_CNT] = 0;
	     }
	     
	     //CHILD_CNT
	     ArrayList children = graph.getChildren(elem);
	     if (children == null)
	       values[CHILD_CNT] = 0;
	     else
	       values[CHILD_CNT] = children.size();

	     //SUB_CNT
	     ArrayList subNodes = graph.getSubNodes(elem);
	     if (subNodes == null)
	       values[SUB_CNT] = 0;
	     else
	       values[SUB_CNT] = subNodes.size();

	     //INNER_CNT
	     ArrayList inners = graph.getInners(elem);
	     if (inners == null)
	       values[INNER_CNT] = 0;
	     else
	       values[INNER_CNT] = inners.size();

	     //LEAF_CNT
	     ArrayList leaves = graph.getLeaves(elem);
	     if (leaves==null)
	       values[LEAF_CNT] = 0;
	     else
	       values[LEAF_CNT] = leaves.size();

//	     //SIBL_CNT
//	     ArrayList siblings = graph.getSiblings(elem);
//	     if (siblings == null){
//	       values[SIBL_CNT] = 0;     
////	       values[SIBL_POS] = 0;
//	     } else{
//	         values[SIBL_CNT] = siblings.size();
////	         String label = vertex.getLabel();
////	         int firstLarger = 0;
////	         for (int i = 0; i < siblings.size(); i++) {
////				String current = ((VertexImpl)siblings.get(i)).getLabel();
////				if (label.compareTo(current)<0){
////					firstLarger = i;
////					break;
////				}
////			}
//////	         values[SIBL_POS] = (float) firstLarger;
////	         values[SIBL_POS] = (float) firstLarger/ siblings.size();
////	         values[SIBL_BEFORE]=firstLarger;
////	         values[SIBL_AFTER]=siblings.size()-firstLarger;         
//	     }

//	     return values;
	     Statistics stat = new Statistics(values);
	     return stat;
	   }

	
}
