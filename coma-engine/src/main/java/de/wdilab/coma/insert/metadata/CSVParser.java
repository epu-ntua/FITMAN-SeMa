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

package de.wdilab.coma.insert.metadata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Source;

/**
 * This class executes the metadata import of csv file as a model.
 * Part of the process is the automatic determination of the 
 * separation character.
 * 
 * @author Thomas Efert, Sabine Massmann
 */
public class CSVParser extends InsertParser {

	public CSVParser(boolean dbInsert){
		super(dbInsert, Source.TYPE_CSV);
	}

	@Override
	public int parseSingleSource(String filename, String schemaName, String author, String domain, String version, String comment) {
//	    System.out.println("Process schema " + sourceName + " from file " + filename + " ... " );
	    // start logging, initiate insert statements	   

		beforeParse(schemaName, filename);
		    // parse information into temp tables (parse_<tablename>)
		parse();
	//		 additional: author, domain, version
		setInformation(author, domain, version, comment);
		    // copy information into real tables (<tablename>), delete temp tables, close statements
		afterParse();
	    return source_id;
	}
	  
	  void parse() {
		  
		    boolean useUTF8=false;
				try {
					BufferedReader in = null;
					if (provider.startsWith("http")){
				        URLConnection connection = new URL(provider).openConnection();
				        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
				        in = new BufferedReader(reader);
					} else {
						in = new BufferedReader(new FileReader(provider));
					}
					String line= in.readLine();
					if (line!= null && line.startsWith("﻿")) {
						useUTF8=true;
						System.out.println("wrong coding, use UTF-8");
					}
				} catch (IOException e) {
					System.out.println("CSVParser.parse() Error reading file " + e.getMessage());
					return;
				}   
		  // todo
		  // - identify separation character, e.g. comma, tab 
		  // - remove double quotes (marking single entry) and additional space characters (leading/trailing entries)
		  // - parse column names (assumption they exist)�
		  
		  int colNumber = 1;
		  Vector<String> colNames = null;
		    
		  // Step 1: Build column name array
		  char separator = 'X';
		  Reader reader = null; 
		  try { 
		    separator = approxSepChar(provider);
		    if (useUTF8){
				if (provider.startsWith("http")){
			        URLConnection connection = new URL(provider).openConnection();
			        reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
				} else {		    	
					reader = new InputStreamReader(new FileInputStream(provider), "UTF-8");
				}
		    } else {
		    	if (provider.startsWith("http")){
			        URLConnection connection = new URL(provider).openConnection();
			        reader = new InputStreamReader(connection.getInputStream());
				} else {		    	
					reader = new FileReader(provider);
				}
		    }		    
		    colNames = parseCSVline(reader,separator);  
		  } 
		  catch ( IOException e ) { 
		    System.err.println( "Error reading file \""+provider+"\":"  + e.getMessage() ); 
		  }
		  if (reader==null){
			  System.out.println("CSVParser.parse() Error reader is null");
			  return;
		  }
		  try { reader.close(); } catch (IOException e) {/* file gone? no closing necessary!  */}
		  if (colNames==null){
			  System.out.println("CSVParser.parse() Error no colNames");
			  return;
		  }
//		  loadObject(sourceName, "root", sourceName, null, namespace, null, elemType, null,null);
//		  sourceId, accession, name, type, typespace, kind, comment, synonyms
		  int root_id = insertObject(source_id,  "root", sourceName,  Element.KIND_GLOBELEM);
		  
		  
//		  // the following information is for csv files not explicitly given:
//		  String type=null;      // can be estimated by analyzing instance data
//		  String comment=null;   // may be present in second header line
		  
		  // Step 2: loadObject on each entry in the column name array
		  for (String colName: colNames) {
	          String colAcc = "col" + ("000"+colNumber).substring((colNumber++ +"").length());
			  
//			  loadObject(sourceName, colAcc, colName, type, namespace, typespace, elemType, comment,null);
			  int current_id = insertObject(source_id,  colAcc,  colName,  Element.KIND_ELEMENT);
			  
//			  loadLink(sourceName, colAcc, sourceName, "root", SourceRelationship.REL_IS_A, null);
			  insertLink(sourcerel_id, root_id, current_id);
		  }
		  		  
		  // DOES NOT OCCUR IN A SINGLE CSV-FILE
		  // for each sub node 
		  // sourceParser.loadLink(sourceName, idStr, sourceName, parentIdStr, SourceRelationship.REL_IS_A, null);
	  }
	  
	
	  /**
	 * @param reader - The file reader, to be advanced a full line's length.
	 * @param sepchar - The separator character (in most cases a ',')
	 * @return - A Vector filled with the single line's field values
	 * @throws IOException
	 */
	public static Vector<String> parseCSVline (Reader reader, char sepchar) throws IOException {
		  return parseCSVline(reader, sepchar, false);
	  }
	
	  /**
	 * @param reader - The file reader, to be advanced a full line's length.
	 * @param sepchar - The separator character (in most cases a ',')
	 * @param errorAware - Turns the parse error awareness on 
	 * @return - A Vector filled with the single line's field values or null if a parse error occured
	 * @throws IOException
	 */
	static Vector<String> parseCSVline (Reader reader, char sepchar, boolean errorAware) throws IOException {
		    Vector<String> output = new Vector<String>();
		    StringBuilder sb = new StringBuilder();
		    
		    boolean stillReading = true;
		    boolean escaped = false;
		    boolean fieldBegun = false;
		    int c=0;
		    int lastc=0;
		    
		    while (stillReading && (c = reader.read())>0 ) {
//		    	String s = new String(x.getBytes("Cp1252"),"utf8"); 
		      if (fieldBegun) {
		    	  if (escaped) {
		    		  if (c == '"' 
		    		      // if a string contains information surrounded by \" -> ignore
		    			  && lastc != '\\') {
		    			  escaped = false;}
//		    		  if (c == '"' && sb.length()==0) {escaped = false;}
		    		  else { sb.append((char) c); }
		    	  }
		    	  else {//!escaped
		    		  if (errorAware && c == '"') return null; // Parse Error!
		    		  if (c == sepchar) {
//		    			  String s = new String(sb.toString().getBytes("utf8"),"Cp1252"); 				    		  
		    			  fieldBegun = false;
		    			  output.add(sb.toString());
		    			  sb.setLength(0); }
		    		  else if (c == '\r' || c == '\n') {
//		    			  String s = new String(sb.toString().getBytes("utf8"),"Cp1252"); 
//		    			  String s2 = new String(sb.toString().getBytes("Cp1252"),"utf8");
//		    			  String s3 = new String(sb.toString().getBytes(),"Cp1252");
//		    			  String s4 = new String(sb.toString().getBytes(),"utf8");
		    			  output.add(sb.toString());
		    			  sb.setLength(0);
		    			  if (c=='\r') reader.read();
		    			  stillReading = false; }
		    		  else sb.append((char) c);
		    	  }
		      }
		      else { //!begun
		    	  if (c == sepchar) output.add("");
		    	  else if (c == '\r' || c == '\n') {
		    		  sb.setLength(0);
		    		  if (c=='\r') reader.read();
		    		  stillReading = false; }
		    	  else if (c == '"') {fieldBegun = true; escaped=true;}
		    	  else if (c == ' ' || (c == '\t' && sepchar != '\t')) {}
		    	  else { fieldBegun = true; sb.append((char) c);}
		      }
		      lastc = c;
		    }
		    if (c<=0) {
		    	output.add(sb.toString()); //Get the last field before EOF, too
		    }
		    return output;
	  }

	public static char approxSepChar (String fileName) throws IOException {
		char[] sepCharCandidates =   {',',':','|','\t','&','/','\\',';'};
		int[] columnsPerCandidate =  { 0 , 0 , 0 , 0  , 0 , 0 , 0  , 0 };
		int[] errorsPerCandidate =   { 0 , 0 , 0 , 0  , 0 , 0 , 0  , 0 };
		int candidateIndex = 0;
		char currentCandidate;
		
		Reader reader;
		// This can most probably be done better - but it should work sufficient by now.
		// Also remeber:
		// Heuristics are bug ridden by definition. If they didn't have bugs, then they'd be algorithms.
		while (candidateIndex<sepCharCandidates.length) {
			currentCandidate = sepCharCandidates[candidateIndex];
		
			
			if (fileName.startsWith("http")){
		        URLConnection connection = new URL(fileName).openConnection();
		        reader = new InputStreamReader(connection.getInputStream());
			} else {
				reader = new FileReader(fileName); 
			}			
			
			Vector<String> line = parseCSVline(reader, currentCandidate); // read the first line
			Vector<String> instanceline; 
			
			if (line == null) errorsPerCandidate[candidateIndex] = Integer.MAX_VALUE; // read error in the first line ?!!
			else {
				columnsPerCandidate[candidateIndex] = line.size(); // store the column count
			
				line = parseCSVline(reader, currentCandidate); // read the second line
				
				if (line == null) { // apparently there is no instance data...
					if (columnsPerCandidate[candidateIndex] < 2) errorsPerCandidate[candidateIndex] = Integer.MAX_VALUE / 2; // would be a rather useless schema
				}
				else { // precious instance data!
					if (columnsPerCandidate[candidateIndex] < 2) errorsPerCandidate[candidateIndex] = Integer.MAX_VALUE / 4; // would be nearly as useless...
					
					int readlines=0;
					while (reader.ready() && readlines++ < 100) {
						instanceline = parseCSVline(reader, currentCandidate,true);
						if(instanceline == null) {errorsPerCandidate[candidateIndex]+=10;} // parse error! panic!
						else {
							if(instanceline.size() < 2) errorsPerCandidate[candidateIndex] += 3; // nearly empty columns? bad thing! (accumulates with the next one!)
							if(instanceline.size() < columnsPerCandidate[candidateIndex]) errorsPerCandidate[candidateIndex]++ ; // too few data? also a bad thing! 					
							if(instanceline.size() > columnsPerCandidate[candidateIndex]) errorsPerCandidate[candidateIndex] += 2; // more data than columns? really bad thing!
						}
					}
				}
			}
			
			reader.close();
			candidateIndex++;
		}
		
		int bestSepCharIndex = 0;
		
		// Statistical methods for finding the perfect sepChar
		// get the indexes of the first occurence of min/max values
		int maxColInd=0;
		int minColInd=0;
		for (int i=0; i < columnsPerCandidate.length; i++) {
			if (columnsPerCandidate[i]>columnsPerCandidate[maxColInd]) maxColInd = i;
			if (columnsPerCandidate[i]<columnsPerCandidate[minColInd]) minColInd = i;
			}
		int maxErrInd=0;
		int minErrInd=0;
		for (int i=0; i < errorsPerCandidate.length; i++) {
			if (errorsPerCandidate[i]>errorsPerCandidate[maxErrInd]) maxErrInd = i;
			if (errorsPerCandidate[i]<errorsPerCandidate[minErrInd]) minErrInd = i;
			}
		
		// "rules" for finding the sepChar
		if (errorsPerCandidate[maxColInd] == 0) bestSepCharIndex = maxColInd;  
		else if (maxColInd == minErrInd) bestSepCharIndex = maxColInd;
		else if (columnsPerCandidate[minErrInd] > 1) bestSepCharIndex = minErrInd;
		else bestSepCharIndex = 0; // Fall back to ','
		
		return sepCharCandidates[bestSepCharIndex];
	} 
	
}
