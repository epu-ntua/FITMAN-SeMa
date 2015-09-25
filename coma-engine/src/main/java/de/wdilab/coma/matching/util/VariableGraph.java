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
package de.wdilab.coma.matching.util;

import java.util.HashMap;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import de.wdilab.coma.repository.DataAccess;

/**
 * This class arranges the workflow variables with their dependencies into a graph.
 * This helps to determine which variables can be deleted without influencing 
 * other variables.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class VariableGraph {
	
	static public SimpleDirectedGraph<String, DefaultEdge> getVariableGraph(DataAccess accessor)	{
		if (accessor==null) return null;
		HashMap<String, String> variables = accessor.getWorkflowVariables();
		HashMap<String, String> variablesValue = new HashMap<String, String>();
		if (variables.isEmpty()) return null;
		SimpleDirectedGraph<String, DefaultEdge> graph = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		Set<String> variablesNames = variables.keySet();
		for (String name : variablesNames) {
			// concat name and value
			String value = name + "=" + variables.get(name);
			graph.addVertex(value);
			variablesValue.put(name, value);
		}
		for (String name : variablesNames) {
			String value = variables.get(name);
			while (value.contains("$")){
				value = value.substring(value.indexOf("$"));
				String nameTmp = new String (value);
				if (nameTmp.contains(";")){
					nameTmp=nameTmp.substring(0, nameTmp.indexOf(";"));
				}
				if (nameTmp.contains(",")){
					nameTmp=nameTmp.substring(0, nameTmp.indexOf(","));
				}
				if (nameTmp.contains("(")){
					nameTmp=nameTmp.substring(0, nameTmp.indexOf("("));
				}
				if (nameTmp.contains(")")){
					nameTmp=nameTmp.substring(0, nameTmp.indexOf(")"));
				}
				// there should only be the name left
				String valueTmp = variablesValue.get(nameTmp);
				if (valueTmp==null){
					System.out.println("VariableGraph.getVariableGraph() Error getting value for " + nameTmp);
				} else {
					String valueOrg = variablesValue.get(name);
					graph.addEdge(valueOrg, valueTmp);
				}
				value = value.replace(nameTmp, "");
			}
		}
		return graph;		
	}
	
}
