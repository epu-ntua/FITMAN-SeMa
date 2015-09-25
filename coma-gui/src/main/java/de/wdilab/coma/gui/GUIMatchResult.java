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

import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.MatchResult;
import de.wdilab.coma.structure.Source;

/**
 * This class contains a source schema, a target schema and match result between them.
 * It is used to display the current matchresult and its models.
 * 
 * @author Sabine Massmann
 */
public class GUIMatchResult{
	private Graph sourceGraph = null;
	private Graph targetGraph = null;
	private MatchResult matchResult = null;

	/* Create empty GUIMatchResult. */
	public GUIMatchResult() {}

	public String toString(){
		String text = "containsSource=" + (sourceGraph!=null) + ", containsTarget="+(targetGraph!=null) +", containsMatchResult="+(matchResult!=null);
		return text;
	}
	
	/* set matchResult */
	public void setMatchResult(MatchResult _result) {
		matchResult = _result;
	}

	/* set orgSourceSchema */
	public void setSourceGraph(Graph _Graph) {
		sourceGraph = _Graph;
	}

	/* set orgTargetSchema */
	public void setTargetGraph(Graph _Graph) {
		targetGraph = _Graph;
	}

	/* return orgSourceSchema */
	public Graph getSourceGraph() {
		return sourceGraph;
	}

	/* return orgTargetSchema */
	public Graph getTargetGraph() {
		return targetGraph;
	}

	/* return targetSource */
	public MatchResult getMatchResult() {
		return matchResult;
	}

	/* return targetSource */
	public Source getSourceSource() {
		if (sourceGraph==null)
			return null;
		return sourceGraph.getSource();
	}

	/* return targetSource */
	public Source getTargetSource() {
		if (targetGraph==null)
			return null;
		return targetGraph.getSource();
	}

	public void closeSource() {
		sourceGraph = null;
	}

	public void closeTarget() {
		targetGraph = null;
	}

	public boolean containsSource() {
		return (sourceGraph!=null);
	}

	public boolean containsTarget() {
		return (targetGraph!=null);
	}

	public boolean containsMatchResult() {
		return (matchResult!=null);
	}

}