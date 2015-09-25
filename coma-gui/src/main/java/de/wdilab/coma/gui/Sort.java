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

package de.wdilab.coma.gui;

import java.util.ArrayList;

import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This class sorts sources and schemas, source relationships and match results.
 * 
 * @author Sabine Massmann
 */
public class Sort {

	static ArrayList<Source> sortSources(ArrayList<Source> _sources){
		if (_sources==null || _sources.isEmpty()){
			return _sources;
		}
		ArrayList<Source> sortedSources = new ArrayList<Source>();
		for (int i=0; i<_sources.size();i++){
			addSortedSource(sortedSources,  _sources.get(i));
		}
		return sortedSources;
	}
	
	static ArrayList<Graph> sortSchemas(ArrayList<Graph> _sources){
		if (_sources==null || _sources.isEmpty()){
			return _sources;
		}
		ArrayList<Graph> sortedSources = new ArrayList<Graph>();
		for (int i=0; i<_sources.size();i++){
			addSortedSchema(sortedSources,  _sources.get(i));
		}
		return sortedSources;
	}
	
	static ArrayList<MatchResult> sortMatchresults(ArrayList<MatchResult> _matchresults){
		if (_matchresults==null || _matchresults.isEmpty()){
			return _matchresults;
		}
		ArrayList<MatchResult> sortedMatchresults = new ArrayList<MatchResult>();
		for (int i=0; i<_matchresults.size();i++){
			addSortedMatchresult(sortedMatchresults, _matchresults.get(i));
		}
		return sortedMatchresults;
	}
	
	static ArrayList<SourceRelationship> sortSourceRelationsships(ArrayList<SourceRelationship> _matchresults){
		if (_matchresults==null || _matchresults.isEmpty()){
			return _matchresults;
		}
		ArrayList<SourceRelationship> sortedMatchresults = new ArrayList<SourceRelationship>();
		for (int i=0; i<_matchresults.size();i++){
			addSortedSourceRelationship(sortedMatchresults, _matchresults.get(i));
		}
		return sortedMatchresults;
	}
		
	
//	public static ArrayList<MatcherConfig> sortMatcherConfigs(ArrayList<MatcherConfig> _matcher){
//		if (_matcher==null || _matcher.isEmpty()){
//			return _matcher;
//		}
//		ArrayList<MatcherConfig> sortedMatcher = new ArrayList<MatcherConfig>();
//		for (int i=0; i<_matcher.size();i++){
//			addSortedMatcher(sortedMatcher, (MatcherConfig) _matcher.get(i));
//		}
//		return sortedMatcher;
//	}
	
	
	static void addSortedSchema(ArrayList<Graph> _selectedSchemas, Graph _s) {
		if (_selectedSchemas.isEmpty()){
			_selectedSchemas.add(_s);
			return;
		}
		String sName = _s.getSource().getName();
		for (int i=0; i<_selectedSchemas.size();i++){
			Graph current = _selectedSchemas.get(i);
			String currentName = current.getSource().getName();
			if (sName.compareToIgnoreCase(currentName)<0){
				_selectedSchemas.add(i,_s);
				break;
			}
		}
		if (!_selectedSchemas.contains(_s)){
			_selectedSchemas.add(_s);
		}
	}
	
	static void addSortedSource(ArrayList<Source> _selectedSchemas, Source _s) {
		if (_selectedSchemas.isEmpty()){
			_selectedSchemas.add(_s);
			return;
		}
		String sName = _s.getName();
		for (int i=0; i<_selectedSchemas.size();i++){
			Source current =  _selectedSchemas.get(i);
			String currentName = current.getName();
			if (sName.compareToIgnoreCase(currentName)<0){
				_selectedSchemas.add(i,_s);
				break;
			}
		}
		if (!_selectedSchemas.contains(_s)){
			_selectedSchemas.add(_s);
		}
	}
	
	
	static void addSortedSourceRelationship(ArrayList<SourceRelationship> _selectedSR, SourceRelationship _sr) {
		if (_selectedSR.isEmpty()){
			_selectedSR.add(_sr);
			return;
		}
//		String srName = SourceRelationship.typeToString(_sr.getType());
		String srName = _sr.getName();
		for (int i=0; i<_selectedSR.size();i++){
			SourceRelationship current = _selectedSR.get(i);
//			String currentName = SourceRelationship.typeToString(current.getType());
			String currentName = current.getName();
			if (srName.compareToIgnoreCase(currentName)<0){
				_selectedSR.add(i,_sr);
				break;
			}
		}
		if (!_selectedSR.contains(_sr)){
			_selectedSR.add(_sr);
		}
	}
	
	static void addSortedMatchresult(ArrayList<MatchResult> _selectedSR, MatchResult _sr) {
		if (_selectedSR.isEmpty()){
			_selectedSR.add(_sr);
			return;
		}
		String srName = _sr.getName();
		for (int i=0; i<_selectedSR.size();i++){
			MatchResult current = _selectedSR.get(i);
			String currentName = current.getName();
			if (srName.compareToIgnoreCase(currentName)<0){
				_selectedSR.add(i,_sr);
				break;
			}
		}
		if (!_selectedSR.contains(_sr)){
			_selectedSR.add(_sr);
		}
	}
	
//	static void addSortedMatcher(ArrayList<MatcherConfig> _selectedMatcher, MatcherConfig _m) {
//		if (_selectedMatcher.isEmpty()){
//			_selectedMatcher.add(_m);
//			return;
//		}
//		String mName = _m.getName();
//		for (int i=0; i<_selectedMatcher.size();i++){
//			MatcherConfig current = (MatcherConfig) _selectedMatcher.get(i);
//			String currentName = current.getName();
//			if (mName!=null && mName.compareToIgnoreCase(currentName)<0){
//				_selectedMatcher.add(i,_m);
//				break;
//			}
//		}
//		if (!_selectedMatcher.contains(_m)){
//			_selectedMatcher.add(_m);
//		}
//	}
//	
	
}
