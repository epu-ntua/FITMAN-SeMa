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

import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * ComplexMatcher realizes the sequence of Resolution2, one or more Matcher and
 * their combination (if only one matcher that combination is an id function) 
 * 
 * NOT part of the grammar, only variable
 * 
 * @author Sabine Massmann
 */
public class ComplexMatcher{
	
	// pre-defined complex matcher
	// start counting 3000 Manager.COMPLEXMATCH_CNT
	public static final int NAMESTAT = Constants.COMPLEXMATCH_CNT + 1;
	public static final int NAMETYPE = Constants.COMPLEXMATCH_CNT + 2;
	public static final int PATH = Constants.COMPLEXMATCH_CNT + 3;
	public static final int LEAVES = Constants.COMPLEXMATCH_CNT + 4;  // PROBLEM: also as STRATEGY!!
	public static final int NAMESYNPATH = Constants.COMPLEXMATCH_CNT + 5;
	public static final int PATHNODES = Constants.COMPLEXMATCH_CNT + 6;
	public static final int NAMESYN = Constants.COMPLEXMATCH_CNT + 7;
	public static final int NAME = Constants.COMPLEXMATCH_CNT + 8;	
		
	// previous Strategy
	public static final int PARENTS = Constants.COMPLEXMATCH_CNT + 101;
	public static final int CHILDREN = Constants.COMPLEXMATCH_CNT + 102;
	public static final int SIBLINGS = Constants.COMPLEXMATCH_CNT + 104;
	public static final int NAMEPATH = Constants.COMPLEXMATCH_CNT + 105;
//	public static final int PATHNODES2 = Constants.COMPLEXMATCH_CNT + 106;
	public static final int SUCCESSORS = Constants.COMPLEXMATCH_CNT + 107;
	
	public static final int INSTANCES = Constants.COMPLEXMATCH_CNT + 108;
	
	
	public static final Integer[] COMPLEXMATCHER = {
		PATH,  LEAVES, 
		NAMESTAT, NAMETYPE, NAMESYNPATH, PATHNODES, NAMESYN, NAME,
		
		// previous Strategy
		PARENTS, CHILDREN, SIBLINGS, NAMEPATH,  SUCCESSORS, // PATHNODES2,
		
		INSTANCES
	};
	public static final List<Integer> COMPLEXMATCH_LIST = Arrays.asList(COMPLEXMATCHER);
	  
	
	// resolution 2
	Resolution resolution = null;
	// matcher or complex matcher
	Object[] m_cm = null;
	// combination -> necessary if more than one matcher
	Combination simCombination = null;
	// set combination to combine result if resolution delivers not just one object
	Combination setCombination = null;
	
    String name= null;
	
	// Constructor of a complex matcher with only resolution and matcher and setCombination	
	public ComplexMatcher (int resolution, Matcher matcher, int setCombination){
		// check if resolution is resolution 2
		if (Resolution.getType(resolution)==Resolution.TYPE_RES2){
			this.resolution = new Resolution(resolution);
		} else {
			System.out.println("Error: resolution " + resolution + " not type 2");
		}
		this.m_cm = new Matcher[1];
		this.m_cm[0] = matcher;
		this.setCombination = new Combination(setCombination);
	}
	
	// Constructor of a complex matcher with only resolution and complex matcher and setCombination	
	public ComplexMatcher (int resolution, ComplexMatcher cmatcher, int setCombination){
		// check if resolution is resolution 2
		if (Resolution.getType(resolution)==Resolution.TYPE_RES2){
			this.resolution = new Resolution(resolution);
		} else {
			System.out.println("Error: resolution " + resolution + " not type 2");
		}
		this.m_cm = new ComplexMatcher[1];
		this.m_cm[0] = cmatcher;
		this.setCombination = new Combination(setCombination);
	}

	
//	// Constructor of a complex matcher with only resolution and matcher and setCombination	
//	public ComplexMatcher (int resolution, int matcher, int setCombination){
//		// check if resolution is resolution 2
//		if (Resolution.getType(resolution)==Resolution.TYPE_RES2){
//			this.resolution = new Resolution(resolution);
//		} else {
//			System.out.println("Error: resolution " + resolution + " not type 2");
//		}
//		this.m_cm = new Matcher[1];
//		this.m_cm[0] = new Matcher(matcher);
//		this.setCombination = new Combination(setCombination);
//	}
	
	public ComplexMatcher (int resolution, Object[] m_cm, int combination, float[] weights, int setCombination){		
		// check if resolution is resolution 2
		if (Resolution.getType(resolution)==Resolution.TYPE_RES2){
			this.resolution = new Resolution(resolution);
		} else {
			System.out.println("Error: resolution " + resolution + " not type 2");
		}
		this.simCombination = new Combination(combination, weights);
		this.m_cm = m_cm;
		this.setCombination = new Combination(setCombination);
	}
	
	
	
	
	
//	// Constructor of a strategy with only resolution and matcher and set combination
//	public Strategy (int resolution, ComplexMatcher cmatcher, int setCombination){
//		// check if resolution is resolution 2
//		if (Resolution.getType(resolution)==Resolution.TYPE_RES2){
//			this.resolution = new Resolution(resolution);
//		} else {
//			System.out.println("Error: resolution " + resolution + " not type 2");
//		}
//		comatch = new ComplexMatcher[1];
//		comatch[0] = cmatcher;
//		this.setCombination = new Combination(setCombination);
//	}
//	
//	// Constructor of a strategy with only resolution and matcher and set combination
//	public Strategy (int resolution, int cmatcher, int setCombination){
//		// check if resolution is resolution 2
//		if (Resolution.getType(resolution)==Resolution.TYPE_RES2){
//			this.resolution = new Resolution(resolution);
//		} else {
//			System.out.println("Error: resolution " + resolution + " not type 2");
//		}
//		comatch = new ComplexMatcher[1];
//		comatch[0] = new ComplexMatcher(cmatcher);
//		this.setCombination = new Combination(setCombination);
//	}
//	
//
//	
//	// Constructor of a strategy with a resolution, similarity measures, combination plus weights and set combination
//	public Strategy (int resolution, ComplexMatcher[] cmatcher, int combination, float[] weights, int setCombination){		
//		// check if resolution is resolution  2
//		if (Resolution.getType(resolution)==Resolution.TYPE_RES2){
//			this.resolution = new Resolution(resolution);
//		} else {
//			System.out.println("Error: resolution " + resolution + " not type 2");
//		}
//		this.simCombination = new Combination(combination, weights);
//		this.comatch = cmatcher;
//		this.setCombination = new Combination(setCombination);
//	}
	
	
	
	// Constructor of a pre-defined complex matcher
	public ComplexMatcher (int id){
		// create one of the default complex matcher
		switch (id) {
		case NAMESTAT :		// $NameStatCMatcher=(Selfnode;$NametokenMatcher,$StatisticsMatcher;Weighted(0.7,03);SetAverage)
			resolution = new Resolution(Resolution.RES2_SELFNODE);
			m_cm = new Matcher[2]; // 2
			m_cm[0] = new Matcher(Matcher.NAME);
			m_cm[1] = new Matcher(Matcher.STATISTICS);
//			simCombination = new Combination(Combination.COM_WEIGHTED, new float[]{(float)0.7, (float)0.3});
			simCombination = new Combination(Combination.COM_AVERAGE);
			// setCombination - not needed but in grammar it needs to be defined
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("NameStatCM");
			break;
		case NAMETYPE :		// $NameTypeCMatcher=(Selfnode;$NametokenMatcher,$DatatypeMatcher;Weighted(0.7,03);SetAverage)
			resolution = new Resolution(Resolution.RES2_SELFNODE);
			m_cm = new Matcher[2];
//			matcher[0] = new Matcher(Matcher.NAMETOKEN);
			m_cm[0] = new Matcher(Matcher.NAME);
			m_cm[1] = new Matcher(Matcher.DATATYPE);
			simCombination = new Combination(Combination.COM_WEIGHTED, new float[]{(float)0.7, (float)0.3});
			// setCombination - not needed but in grammar it needs to be defined
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("NameTypeCM");
			break;
		case PATH :			// $PathCMatcher=(Selfpath;$Nametoken;SetAverage)
			resolution = new Resolution(Resolution.RES2_SELFPATH);
			m_cm = new Matcher[1];
//			m_cm[0] = new Matcher(Matcher.NAMESYNONYM);
			m_cm[0] = new Matcher(Matcher.PATH);
			setCombination = new Combination(Combination.SET_AVERAGE);
//			setCombination = new Combination(Combination.SET_HIGHEST);
			setName("PathCM");
			break;		
		case NAMESYNPATH :			// $PathCMatcher=(Selfpath;$Nametoken;SetAverage)
			resolution = new Resolution(Resolution.RES2_SELFNODE);
			m_cm = new Matcher[2];
			m_cm[0] = new Matcher(Matcher.NAMESYNONYM);
			m_cm[1] = new Matcher(Matcher.PATH);
			simCombination = new Combination(Combination.COM_AVERAGE);
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("NameSynPathCM");
			break;		
		case LEAVES :		// $LeavesCMatcher=(Leaves;$NametokenMatcher,$DatatypeMatcher;Weighted(0.7,03);SetAverage)
			resolution = new Resolution(Resolution.RES2_LEAVES);
//			matcher = new Matcher[2];
//			matcher[0] = new Matcher(Matcher.NAMETOKEN);
//			matcher[1] = new Matcher(Matcher.DATATYPE);
//			simCombination = new Combination(Combination.COM_WEIGHTED, new float[]{(float)0.7, (float)0.3});
			
			m_cm = new Matcher[1];
			m_cm[0] = new Matcher(Matcher.NAME); // NAME NAMESYNONYM
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("LeavesCM");
			break;	
		case PATHNODES :			// $PathCMatcher=(Selfpath;$Nametoken;SetAverage)
			resolution = new Resolution(Resolution.RES2_ALLNODES);
			m_cm = new Matcher[1];
			m_cm[0] = new Matcher(Matcher.NAME);
//			matcher[0] = new Matcher(Matcher.NAMESYNONYM);
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("PathNodesCM");
			break;	
			
//		case PATHNODES2 :		
//			this.resolution = new Resolution(Resolution.RES2_SELFPATH);
//			m_cm = new ComplexMatcher[1];
//			m_cm[0] = new ComplexMatcher(ComplexMatcher.PATHNODES);
//			setCombination = new Combination(Combination.SET_AVERAGE);
//			setName("PathNodesS");
//			break;
						
			
		case NAMESYN :	
			resolution = new Resolution(Resolution.RES2_SELFNODE);
			m_cm = new Matcher[1]; // 2
			m_cm[0] = new Matcher(Matcher.NAMESYNONYM); // NAME NAMESYNONYM
			// setCombination - not needed but in grammar it needs to be defined
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("NameSynCM");
			break;
		case NAME :	
			resolution = new Resolution(Resolution.RES2_SELFNODE);
			m_cm = new Matcher[1]; // 2
			m_cm[0] = new Matcher(Matcher.NAME); 
			// setCombination - not needed but in grammar it needs to be defined
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("NameCM");
			break;
			
			
		// previous Strategy	
		case CHILDREN :		// $ChildrenStrategy=(Children;$NameTypeCMatcher;SetAverage)
			this.resolution = new Resolution(Resolution.RES2_CHILDREN);
			m_cm = new ComplexMatcher[1];	
			m_cm[0] = new ComplexMatcher(ComplexMatcher.NAME); // NAMETYPE NAME
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("ChildrenCM");
			break;
		case NAMEPATH :		// $NamePathStrategy=(Selfpath;$PathCMatcher,$NametokenSynCMatcher;Average;SetAverage)
			// do not set in order to be easy combined with other strategies that have only resolution2
			//--> maybe prefer putting resolution1 in the workflow 
			this.resolution = new Resolution(Resolution.RES2_SELFPATH);
			m_cm = new ComplexMatcher[2];
			m_cm[0] = new ComplexMatcher(ComplexMatcher.PATH);
			m_cm[1] = new ComplexMatcher(ComplexMatcher.NAMESYN); //NAMETOKENSYN NAMETYPE NAME
			simCombination = new Combination(Combination.COM_AVERAGE);
			// setCombination - not needed but in grammar it needs to be defined
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("NamePathCM");
			break;
		case PARENTS :		// $ParentsStrategy =(Parents;$LeavesCMatcher;SetAverage)
			this.resolution = new Resolution(Resolution.RES2_PARENTS);
			m_cm = new ComplexMatcher[1];
			m_cm[0] = new ComplexMatcher(ComplexMatcher.LEAVES);
//			m_cm[0] = new ComplexMatcher(ComplexMatcher.NAME);	
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("ParentsCM");
			break;
		case SIBLINGS :		// $SiblingsStrategy =(Siblings;$LeavesCMatcher;SetAverage)
			this.resolution = new Resolution(Resolution.RES2_SIBLINGS);
			m_cm = new ComplexMatcher[1];
			m_cm[0] = new ComplexMatcher(ComplexMatcher.LEAVES);
			setCombination = new Combination(Combination.SET_AVERAGE);
			setName("SiblingsCM");
			break;		
		case SUCCESSORS :
			this.resolution = new Resolution(Resolution.RES2_SUCCESSORS);
			m_cm = new ComplexMatcher[1];	
			m_cm[0] = new ComplexMatcher(ComplexMatcher.NAME); // NAMETYPE NAME NAMESYN
			setCombination = new Combination(Combination.SET_AVERAGE); // SET_AVERAGE SET_DICE
			setName("SuccessorsCM");
			break;
			
		case INSTANCES :
			this.resolution = new Resolution(Resolution.RES2_SELFNODE);
			m_cm = new Matcher[2];
			m_cm[0] = new Matcher(Matcher.INSTANCES_DIRECT);
			m_cm[1] = new Matcher(Matcher.INSTANCES_ALL);
			simCombination = new Combination(Combination.COM_MAX);
			setCombination = new Combination(Combination.SET_AVERAGE); // SET_AVERAGE SET_DICE
			setName("InstancesCM");
			break;
			
		default :
			System.out.println("Strategy id not known " + id);
			break;
		}
	}
	

	//simple getter
	public Resolution getResolution() { return resolution; }
	public Combination getSimCombination() { return simCombination; }
	public Object[] getMatcherAndComplexMatcher() { return m_cm; }
	public Combination getSetCombination() { return setCombination; }
	
	// simple setter
	public void setName(String name) { this.name=name; }

	public String getName() {
		if (name==null){
			// generate Name
			Random r = new Random();
			name=Constants.COMPLEXMATCHER+Math.abs(r.nextInt());
		}
		return name; 
	}
	
	public String toString(){
		// default string with variables
		return toString(false);
	}
	
	public String toString(boolean withVariables){
		String text = null;
//		if (withVariables){
//			text = "$" + getName()+Constants.SEP+"(";
//		} else {
			text = "(";
//		}
		// resolution 1
		text+=resolution.getName()+";";
		// complex matcher or strategies
		for (int i = 0; i < m_cm.length; i++) {
			Object current = m_cm[i];
			if (current instanceof Matcher){
				if (withVariables){
					text+="$" + ((Matcher)current).getName()+",";
				} else {
					text+=((Matcher)current).toString(withVariables)+",";
				}
			} else if (current instanceof ComplexMatcher){
				if (withVariables){
					text+="$" + ((ComplexMatcher)current).getName()+",";
				} else {
					text+=((ComplexMatcher)current).toString(withVariables)+",";
				}
			}
		}
		// remove last ,
		text = text.substring(0, text.length()-1);
		// combination -> necessary if more than one function
		if (simCombination!=null){
			text+=";"+simCombination.getName();
		}
		text+=";"+setCombination.getName() + ")";	
		return text;
	}
	
}
