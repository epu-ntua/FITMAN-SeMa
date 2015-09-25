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

package de.wdilab.coma.insert.instance;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Vector;

import de.wdilab.coma.insert.metadata.CSVParser;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;

/**
 * Import instances of a csv file. Assumption is that the csv file
 * used to import the metadata also contains the instance data. 
 *  
 * @author Thomas Efert, Sabine Massmann
 */
public class InstanceCSVParser {
	DataImport importer = null;

  public InstanceCSVParser(DataImport importer) {  
	  this.importer = importer;
  }

  
  public Graph parseInstances(Graph schemaGraph){
	  String file = schemaGraph.getSource().getProvider();
	  int id = schemaGraph.getSource().getId();
//	  importer.createInstancesTable(id);
	  importer.prepareInstancesStatement(id);
	  ArrayList<Element> inners = schemaGraph.getInners();
	  for (int i = 0; i < inners.size(); i++) {
		  Element inner = inners.get(i); // table name
		ArrayList<Element> children = schemaGraph.getChildren(inner); // attributes
		if (children.isEmpty()){
			continue;
		}
		// each inner node is a root representing one file
		// get file and parse instances
		parseInstancesForFile(file, inner, children);
	  }
	  importer.closeInstancesStatement();
//		schemaGraph.print();
	  return schemaGraph;
  }
  
  private void parseInstancesForFile(String fileName, Element root, ArrayList<Element> elements){
//	  int connect = 1; // counts the current instance data set
	  
//	  String fileName = root.getProvider();
//	  int instanceid=1;
	  int linecount=1;
	  
	  Reader reader = null; 
	  try 
	  { 
		Vector<String> line = new Vector<String>();
	    
		char separator = CSVParser.approxSepChar(fileName);
		
		reader = new FileReader(fileName);
	    String lineaccession;
	        
	    line = CSVParser.parseCSVline(reader,separator); // Skip line 1 as header
    	
	    int lastId=0;
    	// count number of instances until max (not linenumber because lines can be empty or several line present one instance)
	    while ( reader.ready() && lastId < Repository.INSTANCES_MAX_PER_ELEMENT ) {
		    line = CSVParser.parseCSVline(reader,separator);
	    	int id=-1;
	    	lineaccession = "line" + ("000000"+linecount).substring((linecount+"").length());
	    	linecount++;
		    for (int i=0;i<elements.size();i++) {
		    	Element elemi = elements.get(i);
		    	// new version
		    	if (line.size()>i) {
		    		if (line.get(i).length()==0){
		    			continue; // one way to signal a null value -> ignore
		    		}
		    		if (line.get(i).equals("NULL")){
		    			continue; // another way to signal a null value -> ignore
		    		}		    				    		
			    	if (id==-1){
			    		// first value of an instance
			    		id = lastId+1;
			    		importer.insertInstance(lineaccession,elemi.getId(),id,null,line.get(i));
			    		lastId = id;
			    	} else {
			    		importer.insertInstance(lineaccession,elemi.getId(),id,null,line.get(i));
			    	}
		    	}
//		    	// old version
//		    	if (line.size()<=i) {
//		    		//elemi.addInstance(null);
//		    		id = importer.insertInstance(lineaccession,elemi.getId(),instanceid++,null,null);
//		    	}
//		    	else {
//		    		//elemi.addInstance(line.get(i));
//		    		id = importer.insertInstance(lineaccession,elemi.getId(),instanceid++,null,line.get(i));
//		    	}
//		    	//importer.updateInstance(id, id);
		    }
	    }

	  } 
	  catch ( IOException e ) { 
	    System.err.println( "Error reading file \""+fileName+"\":"  + e.getMessage() ); 
	  } 
	  
	  if (reader==null){
			System.out.println("InstanceCSVParser.parseInstancesForFile() Error reade is null");
			return;
	  }

	  try { reader.close(); } catch (IOException e) {/* file gone? no closing necessary!  */}
	  
	  
	}
	
//	public static void main(String[] args) {
//		int graphstate = Graph.PREP_RESOLVED;
//		Manager manager = new Manager();
//		manager.loadRepository();
//		int id = 3;
//		Graph schemaGraph = manager.loadSchemaGraph(manager.getSource(id), true).getGraph(graphstate);
//		manager.parseInstances(schemaGraph, true);
//		schemaGraph.printGraphInfo();
//		System.out.println("DONE.");
//	}	


}
