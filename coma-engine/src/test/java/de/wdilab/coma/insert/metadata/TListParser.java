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


import java.io.File;

import junit.framework.TestCase;
import de.wdilab.coma.insert.TestParser;
import de.wdilab.coma.insert.metadata.ListParser;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.repository.TRepository;

/**
 * @author Sabine Massmann
 */
public class TListParser extends TestCase {
	
	// coma-project\coma-engine\resources\
	String directory = (new File ("")).getAbsolutePath() + "\\resources\\";
	static Object[][] data= {
			{"PO_abbrevs.txt", 42, 22},
			{"PO_syns.txt", 191, 104},
	};
	
	public void test(){
		TRepository.setDatabaseProperties();
		
		Repository rep = new Repository();
		rep.createRepositorySchema();
		
		singleImport();
		TestParser.testData(rep, directory, data);
		
		singleImportSpecial();
		TestParser.testData(rep, directory, data);
		
		multipleImport();
		TestParser.testData(rep, directory, data);
	}
	
	
	public void singleImportSpecial(){
		ListParser par = new ListParser(true);
		
		par.parseAbbreviation(directory+(String) data[0][0]);		
		par.parseSynonym(directory+(String) data[1][0]);
	}
	
	public void singleImport(){
		ListParser par = new ListParser(true);
		for (int i = 0; i < data.length; i++) {		
			String file = directory+(String) data[i][0];
			par.parseSingleSource(file);			
		}
	}


	public void multipleImport(){
		ListParser par = new ListParser(true);
		String[] files = new String[data.length];
		for (int i = 0; i < data.length; i++) {		
			files[i] = directory+(String) data[i][0];			
		}
		par.parseMultipleSources(files);
	}
	
}
