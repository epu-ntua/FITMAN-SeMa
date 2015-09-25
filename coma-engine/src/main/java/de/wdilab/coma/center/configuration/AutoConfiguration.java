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

package de.wdilab.coma.center.configuration;

import java.util.ArrayList;

import de.wdilab.coma.center.analysis.Characteristics;
import de.wdilab.coma.matching.Combination;
import de.wdilab.coma.matching.ComplexMatcher;
import de.wdilab.coma.matching.Matcher;
import de.wdilab.coma.matching.Resolution;
import de.wdilab.coma.matching.Selection;
import de.wdilab.coma.matching.Strategy;
import de.wdilab.coma.matching.Workflow;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Source;

/**
 * This class determines an automatic matching workflow based on the characteristics
 * of the given source and target graphs.
 * 
 * @author Sabine Massmann
 */
public class AutoConfiguration {

	// 0: start with default match configuration
	// matcher Name, Path, Parents and Leaves, direction Both, selection maxDelta+Threshold
	
	
	static Workflow getConfiguration(Graph graphSrc, Graph graphTrg) {
		if (graphSrc==null || graphTrg==null){
			System.out.println("AutoConfiguration.getConfiguration one or both graphs are empty, no configration created");
			return null;
		}
		Workflow w = new Workflow();
		// analyze schemas separately (e.g. availability of instances, comments,	and diverse data types)
		Characteristics cSrc = new Characteristics(graphSrc);
		Characteristics cTrg = new Characteristics(graphTrg);
		
		// TODO analyze match task (e.g. degree of linguistic and structural similarity, input size)
		
		// base complex matcher
		ComplexMatcher baseCM = getBaseCM(cSrc, cTrg);
		
		ArrayList<ComplexMatcher> cms = new ArrayList<ComplexMatcher>();
		cms.add(baseCM);
		
		// add (maybe) matcher if useful e.g. instance matcher, comment matcher
		// comments exist, use comment matcher
		if (cSrc.comments && cTrg.comments){
			ComplexMatcher current = new ComplexMatcher(Resolution.RES2_SELFNODE, new Matcher(Matcher.COMMENT), Combination.SET_AVERAGE);
			cms.add(current);
		}
		// TODO instances exist, use instance matcher
//		if (cSrc.instances && cTrg.instances){
//			ComplexMatcher current = new ComplexMatcher(Resolution.RES2_SELFNODE, new Matcher(Matcher.COMMENT), Combination.SET_AVERAGE);
//			cms.add(current);
//		}
		// structure thus hierarchy 
		if (cSrc.structure && cTrg.structure){
			// similarity of hierarchy
			// similarity of language
			// Path, Parents and Leaves
			// Leaves
			ComplexMatcher leaves = new ComplexMatcher(Resolution.RES2_LEAVES, baseCM, Combination.SET_AVERAGE);
			cms.add(leaves);
			ComplexMatcher parents = new ComplexMatcher(Resolution.RES2_PARENTS, leaves, Combination.SET_AVERAGE);
			cms.add(parents);
			if (cSrc.similarLanguage(cTrg)){
				// only if similar language path matcher is useful
				ComplexMatcher path = new ComplexMatcher(ComplexMatcher.PATH);
				cms.add(path);
			}
		} else {
			// drop matcher if not applicable (e.g. no hierarchy -> no parents, no path)
		}
		
		// TODO continue automatic configuration
		// 3: reuse possibe? 
		// 4: if very large use fragment or cluster based matching
		// 5: if very similar structure and language -> restrictive selection
		
		Selection selection = new Selection(Selection.DIR_BOTH, Selection.SEL_MULTIPLE, 0, 0.02f, 0.3f);
		Strategy strategy = null;
		// ontologies use resolution 1 nodes, schemas paths
		if (graphSrc.getSource().getType()==Source.TYPE_ONTOLOGY){
			strategy = new Strategy(Resolution.RES1_NODES,(ComplexMatcher[]) cms.toArray(), Combination.COM_AVERAGE, selection);
		} else {
			strategy = new Strategy(Resolution.RES1_PATHS,(ComplexMatcher[]) cms.toArray(), Combination.COM_AVERAGE, selection);
		}
		w.addBegin(strategy);
		w.setSource(graphSrc);
		w.setTarget(graphTrg);
		
		return w;
	}

	
	static ComplexMatcher getBaseCM(Characteristics cSrc, Characteristics cTrg) {
		ComplexMatcher cm = null;
		
		// adapt configuration e.g. if no datatypes don't use NameType but Name or NameStat,  
		// if synonyms are available use NameSyn, if no similar language use dictionary (future)
		boolean datatypesSrc = cSrc.datatypes;
		boolean datatypesTrg = cTrg.datatypes;
		boolean synonymsSrc = cSrc.synonyms;
		boolean synonymsTrg = cTrg.synonyms;
		boolean statisticsSrc = cSrc.structure;
		boolean statisticsTrg = cTrg.structure;

		// name matcher for selfnodes, children, parents, leaves
		Matcher name = null;
		if (cSrc.similarLanguage(cTrg)){
			if (synonymsSrc && synonymsTrg){
				// use synonyms in addition to name
				name = new Matcher(Matcher.NAMESYNONYM);
			} else {
				name = new Matcher(Matcher.NAME);
			}
		} else {
			// TODO if  no similar language don't use name
			// TODO maybe more detailed statistics/structure support 
			
		     // 2005 version, PO matching: DEPTH, CHILD_CNT, SUB_CNT, INNER_CNT, LEAF_CNT
		     // 2008 version: CHILD_CNT, SUB_CNT, INNER_CNT, LEAF_CNT, SIBL_CNT
			// in addition possible: PARENT_CNT, DIST, UP_CNT, SIBL_CNT, SIBL_POS, SIBL_BEFORE, SIBL_AFTER
			
			
		}
		
		if (datatypesSrc && datatypesTrg){
			if (statisticsSrc && statisticsTrg){
				// use both datatype and statistics
				float[] weights = {0.6f, 0.2f, 0.2f};
				Matcher[] matchers = new Matcher[weights.length];
				matchers[0]=name;
				matchers[1]=new Matcher(Matcher.DATATYPE);
				matchers[2]=new Matcher(Matcher.STATISTICS);
				cm = new ComplexMatcher(Resolution.RES2_SELFNODE, matchers, Combination.COM_WEIGHTED, weights, Combination.SET_AVERAGE);
			} else {
				// use datatype
				float[] weights = {0.7f, 0.3f};
				Matcher[] matchers = new Matcher[weights.length];
				matchers[0]=name;
				matchers[1]=new Matcher(Matcher.DATATYPE);
				cm = new ComplexMatcher(Resolution.RES2_SELFNODE, matchers, Combination.COM_WEIGHTED, weights, Combination.SET_AVERAGE);
			}
		} else {
			if (statisticsSrc && statisticsTrg){
				// use statistics
				float[] weights = {0.7f, 0.3f};
				Matcher[] matchers = new Matcher[weights.length];
				matchers[0]=name;
				matchers[1]=new Matcher(Matcher.STATISTICS);
				cm = new ComplexMatcher(Resolution.RES2_SELFNODE, matchers, Combination.COM_WEIGHTED, weights, Combination.SET_AVERAGE);
			} else {
				// use neither statistics nor datatype, only name
				cm = new ComplexMatcher(Resolution.RES2_SELFNODE, name, Combination.SET_AVERAGE);
			}
		}
		return cm;
	}
	
	
}
