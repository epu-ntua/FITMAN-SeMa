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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import de.wdilab.coma.insert.InsertParser;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Source;

/**
 * This class executes the import of word lists. It is
 * used to import abbreviations and synonyms. 
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class ListParser extends InsertParser {	
	
	String kind = null;
	ArrayList<String> list1 = null, list2 = null;
	
	public ListParser(boolean dbInsert){
		super(dbInsert, Source.TYPE_INTERN);
	}
	
	@Override
	public int parseSingleSource(String filename, String schemaName, String author, String domain, String version, String comment) {
		beforeParse(schemaName, filename);
		// parse information into temp tables (parse_<tablename>)
		// TODO if dbInsert false
		parse(null);
	//	 additional: author, domain, version
		setInformation(author, domain, version, comment);
		// copy information into real tables (<tablename>), delete temp tables, close statements
		afterParse();

	    return source_id;
	}
	
	public ArrayList<String> getList1(){ return list1; }
	public ArrayList<String> getList2(){ return list2; }
	
	public void parseAbbreviation(String filename) {
		parseSingleSource(filename, Repository.SRC_ABBREV, null, null, null, null);
	}
	
	public void parseAbbreviation(String filename, String author, String domain, String version, String comment) {
		parseSingleSource(filename, Repository.SRC_ABBREV, author, domain, version, comment);
	}
	
	public void parseSynonym(String filename) {
		parseSingleSource(filename, Repository.SRC_SYNONYM, null, null, null, null);
	}
	
	public void parseSynonym(String filename, String author, String domain, String version, String comment) {
		parseSingleSource(filename, Repository.SRC_SYNONYM, author, domain, version, comment);
	}

	void parse(String kind) {
		try {
			// TODO if dbInsert false
			if (!dbInsert){
				list1 = new ArrayList<String>();
				list2 = new ArrayList<String>();
			}
			BufferedReader in = null;
			if (provider.startsWith("http")){
		        URLConnection connection = new URL(provider).openConnection();
		        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
		        in = new BufferedReader(reader);
			} else {
				in = new BufferedReader(new FileReader(provider));
			}
			String synLine;
			while ( (synLine = in.readLine()) != null) {
				int i = synLine.indexOf(",");			
				if (i != -1) {
					// each either a word or a phrase
					String value1 = synLine.substring(0, i).trim();
					String value2 = synLine.substring(i + 1, synLine.length()).trim();
					if (value1.equals(value2)){
						continue; // avoid links without effect (here: substitute a word with itself) 
					}
					if (dbInsert){
					  int id1 = insertObject(source_id,  value1,  value1,  Element.KIND_ELEMENT);
					  int id2 = insertObject(source_id,  value2,  value2,  Element.KIND_ELEMENT);
					  insertLink(sourcerel_id, id1, id2);
					} else{
						list1.add(value1);
						list2.add(value2);
					}
				}
			}
		}
		catch (IOException e) {
			System.out.println("AbbreviationParser.parseAbbreviation(): Error opening file " + provider + ": " + e.getMessage());
		}
	}
	
}
