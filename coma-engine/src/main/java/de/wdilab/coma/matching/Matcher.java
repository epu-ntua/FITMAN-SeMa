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
 * Matcher realizes the sequence of Resolution3, one or more Similarity Measures
 * and their combination (if only one measure that combination is a id function) 
 * 
 * NOT part of the grammar, only variable
 * 
 * @author Sabine Massmann
 */
public class Matcher{
	
	// pre-defined matcher
	// start counting 2000 Manager.MATCH_CNT
//	public static final int NAMETOKEN = Constants.MATCH_CNT + 1;
	public static final int DATATYPE = Constants.MATCH_CNT + 2;
	public static final int STATISTICS = Constants.MATCH_CNT + 3;
	public static final int COMMENT = Constants.MATCH_CNT + 4;
	public static final int PATHTOKEN = Constants.MATCH_CNT + 5;
	public static final int NAME = Constants.MATCH_CNT + 6;
	public static final int SYNONYM = Constants.MATCH_CNT + 7;
	public static final int NAMESYNONYM = Constants.MATCH_CNT + 8;
	public static final int PATH = Constants.MATCH_CNT + 9;
	public static final int INSTANCES_DIRECT = Constants.MATCH_CNT + 10;
	public static final int INSTANCES_ALL = Constants.MATCH_CNT + 11;
	
	public static final Integer[] MATCHER = {
//		NAMETOKEN, 
		DATATYPE, STATISTICS, COMMENT, PATHTOKEN, NAME, SYNONYM, NAMESYNONYM, PATH,
		INSTANCES_DIRECT, INSTANCES_ALL
	};
	public static final List<Integer> MATCHER_LIST = Arrays.asList(MATCHER);
	
	// resolution 3
	Resolution resolution = null;
	// similarity function(s)
	SimilarityMeasure[] simMeasures = null;
	// combination -> necessary if more than one similarity measure
	Combination simCombination = null;
	// set combination to combine result if resolution delivers not just one object
	Combination setCombination = null;
	
	
    String name= null;
	
	// Constructor of a matcher with only resolution and similarity measure
	public Matcher (int resolution, int simmeasure, int setCombination){
		// check if resolution is resolution 3
		if (Resolution.getType(resolution)==Resolution.TYPE_RES3){
			this.resolution = new Resolution(resolution);
		} else {
			System.out.println("Error: resolution " + resolution + " not type 3");
		}
		simMeasures = new SimilarityMeasure[1];
		simMeasures[0] = new SimilarityMeasure(simmeasure);
		this.setCombination = new Combination(setCombination);
	}
	
	// Constructor of a matcher with a resolution, similarity measures and combination
	public Matcher (int resolution, int[] simmeasure, int simCombination, float[] weights, int setCombination){
		// check if resolution is resolution 3
		if (Resolution.getType(resolution)==Resolution.TYPE_RES3){
			this.resolution = new Resolution(resolution);
		} else {
			System.out.println("Error: resolution " + resolution + " not type 3");
		}
		this.simCombination = new Combination(simCombination, weights);
		simMeasures = new SimilarityMeasure[ simmeasure.length];
		for (int i = 0; i < simmeasure.length; i++) {
			simMeasures[i] = new SimilarityMeasure(simmeasure[i]);
		}
		this.setCombination = new Combination(setCombination);
	}
	
	
	// Constructor of a pre-defined matcher
	public Matcher (int matcher){
		// create one of the default matcher
		switch (matcher) {
			case NAME:			// $NameMatcher=(Name;SimTrigram;SetAverage)
				// comparing the whole name at once -> sequence important
				this.resolution = new Resolution(Resolution.RES3_NAME);
				simMeasures = new SimilarityMeasure[1];
				// SIM_STR_TRIGRAM SIM_STR_TRIGRAM_COMA SIM_STR_TRIGRAM_IFUICE SIM_STR_TRIGRAM_LOWMEM SIM_STR_TRIGRAM_OPT
				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_STR_TRIGRAM);
				// setCombination - not needed but in grammar it needs to be defined
				setCombination = new Combination(Combination.SET_AVERAGE);
				setName("NameM");
				break;
			case NAMESYNONYM:
				// comparing the whole name at once -> sequence important
				this.resolution = new Resolution(Resolution.RES3_NAMESYN); // RES3_NAMESYN RES3_NAME
				simMeasures = new SimilarityMeasure[1];
				// SIM_STR_TRIGRAM SIM_STR_TRIGRAM_COMA SIM_STR_TRIGRAM_IFUICE SIM_STR_TRIGRAM_LOWMEM SIM_STR_TRIGRAM_OPT
				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_STR_TRIGRAM);
				// setCombination - not needed but in grammar it needs to be defined
//				setCombination = new Combination(Combination.SET_MAX);
//				setCombination = new Combination(Combination.SET_AVERAGE);
				setCombination = new Combination(Combination.SET_HIGHEST);				
				setName("NameAndSynonymM");
				break;
//			case NAMETOKEN:	// $NametokenMatcher=(Nametoken;Trigram;SetAverage)
//				// comparing tokens -> sequence unimportant
//				this.resolution = new Resolution(Resolution.RES3_NAMETOKEN);
//				simMeasures = new SimilarityMeasure[1];
//				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_STR_TRIGRAM);
//				// TODO Using Synonyms
//				setCombination = new Combination(Combination.SET_AVERAGE);
//				setName("NameTokenM");
//				break;
			case DATATYPE:		// $DatatypeMatcher=(Datatype;SimDatatype;SetAverage) 
				this.resolution = new Resolution(Resolution.RES3_DATATYPE);
				simMeasures = new SimilarityMeasure[1];
				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_DATATYPE);
				// setCombination - not needed but in grammar it needs to be defined
				setCombination = new Combination(Combination.SET_AVERAGE);
				setName("DatatypeM");
				break;
			case STATISTICS:	// $StatisticsMatcher=(Statistics;SimVector;SetAverage)
				this.resolution = new Resolution(Resolution.RES3_STATISTICS);
				simMeasures = new SimilarityMeasure[1];
				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_VECT_FEATURES);
				// setCombination - not needed but in grammar it needs to be defined
				setCombination = new Combination(Combination.SET_AVERAGE);
				setName("StatisticsM");
				break;
			case COMMENT :		// $CommentMatcher=(Comment;SimTFIDF;SetAverage)
				// take the comment at once and use TFIDF to compare them as documents 
				// so word sequence doesn't matter and important (often in one doc, rare overall) words count more 
				this.resolution = new Resolution(Resolution.RES3_COMMENT);
				simMeasures = new SimilarityMeasure[1];
				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_DOC_TFIDF);
				// setCombination - not needed but in grammar it needs to be defined
				setCombination = new Combination(Combination.SET_AVERAGE);
				setName("CommentM");
				break;
			case PATHTOKEN :	// $PathtokenMatcher=(Pathtoken;SimTrigram;SetAverage)
				// comparing tokens -> sequence unimportant
				this.resolution = new Resolution(Resolution.RES3_PATHTOKEN);
				simMeasures = new SimilarityMeasure[1];
				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_STR_TRIGRAM);
				setCombination = new Combination(Combination.SET_AVERAGE);
				setName("PathtokenM");
				break;
			case SYNONYM:			// $SynonymMatcher=(Synonyms;SimTrigram;SetMax)
				// comparing the whole name at once -> sequence important
				this.resolution = new Resolution(Resolution.RES3_SYNONYMS);
				simMeasures = new SimilarityMeasure[1];
				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_STR_TRIGRAM);
				// setCombination - not needed but in grammar it needs to be defined
				setCombination = new Combination(Combination.SET_MAX);
				setName("SynonymM");
				break;
			case PATH :
				this.resolution = new Resolution(Resolution.RES3_PATH);
				simMeasures = new SimilarityMeasure[1];
				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_STR_TRIGRAM);
				setCombination = new Combination(Combination.SET_AVERAGE);
				setName("PathM");
				break;
			case INSTANCES_DIRECT:
				this.resolution = new Resolution(Resolution.RES3_INST_CONTENT_DIRECT);
				simMeasures = new SimilarityMeasure[1];
				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_DOC_TFIDF);
				setCombination = new Combination(Combination.SET_AVERAGE);
				setName("InstancesDirectM");
				break;
			case INSTANCES_ALL:
				this.resolution = new Resolution(Resolution.RES3_INST_CONTENT_ALL);
				simMeasures = new SimilarityMeasure[1];
				simMeasures[0] = new SimilarityMeasure(SimilarityMeasure.SIM_DOC_TFIDF);
				setCombination = new Combination(Combination.SET_AVERAGE);
				setName("InstancesAllM");
				break;
			default :
				System.out.println("Matcher id not known " + matcher);
				break;
		}
	}
	
	//simple getter
	public Resolution getResolution() { return resolution; }
	public Combination getSimCombination() { return simCombination; }
	public SimilarityMeasure[] getSimMeasures() { return simMeasures; }
	public Combination getSetCombination() { return setCombination; }

	// simple setter
	public void setName(String name) { this.name=name; }

	public String getName() {
		if (name==null){
			// generate Name
			Random r = new Random();
			name=Constants.MATCHER+Math.abs(r.nextInt());
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
		for (int i = 0; i < simMeasures.length; i++) {
			SimilarityMeasure current = simMeasures[i];
			text+=current.getName()+",";

		}
		// remove last ,
		text = text.substring(0, text.length()-1);
		// combination -> necessary if more than one function
		if (simCombination!=null){
			text+=";"+simCombination.getName();
		}
		text+=";"+setCombination.getName()+")";	
		return text;
	}
	
}
