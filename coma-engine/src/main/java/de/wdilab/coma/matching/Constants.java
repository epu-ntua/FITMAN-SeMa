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

package de.wdilab.coma.matching;

/**
 * This class contains the string representation for the main workflow variables
 * as well as the id differentiations for the different worflow grammar elements.
 * 
 * @author Sabine Massmann
 */
public class Constants {
	
	public static String SEP = "->";
		
	public static String WORKFLOW = "Workflow";
	public static String STRATEGY = "Strategy";
	public static String COMPLEXMATCHER = "ComplexMatcher";
	public static String MATCHER = "Matcher";
	
	
	public static final int UNDEF = -1;
	
	// start count at
	public static final int RES_CNT =0;
	public static final int MEAS_CNT =1000;
	public static final int MATCH_CNT =2000;
	public static final int COMPLEXMATCH_CNT =3000;
	public static final int COM_CNT =4000;
	public static final int STRAT_CNT =5000;
	public static final int SEL_CNT =6000;
	public static final int WORK_CNT =7000;
	
	public static Class<?> getClass(int value){
		if (value<RES_CNT){
			return null;
		} else if (RES_CNT<=value && value<MEAS_CNT){
			return Resolution.class;
		} else if (MEAS_CNT<=value && value<MATCH_CNT){
			return SimilarityMeasure.class;
		} else if (MATCH_CNT<=value && value<COMPLEXMATCH_CNT){
			return Matcher.class;
		} else if (COMPLEXMATCH_CNT<=value && value<COM_CNT){
			return ComplexMatcher.class;
		} else if (COM_CNT<=value && value<STRAT_CNT){
			return Combination.class;
		} else if (STRAT_CNT<=value && value<SEL_CNT){
			return Strategy.class;
		} else if (SEL_CNT<=value && value<WORK_CNT){
			return Selection.class;
		} else if (WORK_CNT<=value){
			return Workflow.class;
		}		
		return null;
	}
}
