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

package de.wdilab.coma.insert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import de.wdilab.coma.center.GraphPreprocessing;
import de.wdilab.coma.insert.metadata.CSVParser;
import de.wdilab.coma.insert.metadata.ListParser;
import de.wdilab.coma.insert.metadata.ODBCParser;
import de.wdilab.coma.insert.metadata.OWLParser_V3;
import de.wdilab.coma.insert.metadata.SQLParser;
import de.wdilab.coma.insert.metadata.XDRParser;
import de.wdilab.coma.insert.metadata.XSDParser;
import de.wdilab.coma.matching.execution.ExecWorkflow;
import de.wdilab.coma.repository.DataImport;
import de.wdilab.coma.repository.Repository;
import de.wdilab.coma.structure.Element;
import de.wdilab.coma.structure.Graph;
import de.wdilab.coma.structure.Source;
import de.wdilab.coma.structure.SourceRelationship;

/**
 * This class organizes the import of models, e.g. csv, xml and owl,
 * and relationships e.g. rdf alignment. It coordinates either the 
 * import into the database or the direct creation of a graph or matchresult.
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public abstract class InsertParser {

	// File endings
//	public static final String ASC = ".asc";
	public static final String CSV = ".csv";
	public static final String OWL = ".owl";
	public static final String RDF = ".rdf";
	public static final String SQL = ".sql";
	public static final String TXT = ".txt";
	public static final String XDR = ".xdr";
	public static final String XML = ".xml";
	public static final String XSD = ".xsd";
	
	public static final String[] FILETYPES = {
//		ASC, 
		CSV, OWL, RDF, SQL, TXT, XDR, XSD, XML
	};
	
	public String[] USE_FILETYPES = new String[1];
	public static final List<String> FILETYPES_LIST = Arrays.asList(FILETYPES);
	
//	public static final String SOURCE_DIR = "D:\\workspace\\COMA++2009\\Sources";
	public static final String SOURCE_DIR = "D:\\workspace_new\\coma-project\\coma-insert\\src\\test\\java\\resources\\Sources";
//	public static final String SOURCE_DIR = "Sources";
	public static final String PARSE_PREFIX = "Parse";
	public static final String PARSE_DIR = "Parsed";
	  
	protected DataImport dataImport;
	// if false create directly graph and do not import into database
	protected boolean dbInsert = false;
	public String sourceName = null;
	public int source_id = -1;
	public int sourcerel_id = -1;	
	protected int type = -1;
	protected String provider = null;
	protected String date = null;
	
	protected long startParse=0, endParse=0;
	protected PrintStream logging = null;
	
	HashSet<Integer> idsUsed = null;
	Graph graph = null;
	GraphPreprocessing prep = null;
	
	public InsertParser(boolean dbInsert, int type){
		this.dbInsert = dbInsert;
		this.type= type;
		if (dbInsert){
			dataImport = new DataImport();
		} else {
			prep = new GraphPreprocessing(null, new ExecWorkflow());
		}
	}
	
	/**
	 * Getter of the property <tt>dataImport</tt>
	 * 
	 * @return Returns the dataImport.
	 */
	public DataImport getDataImport() {
		return dataImport;
	}

	/**
	 * Setter of the property <tt>dataImport</tt>
	 * 
	 * @param dataImport
	 *            The dataImport to set.
	 */
	public void setDataImport(DataImport dataImport) {
		this.dataImport = dataImport;
	}

	public Graph getGraph(){
		return graph;
	}
	
	public void closeAll(){
		dataImport.closeStatements();
		dataImport.closeDatabaseConnection();
	}
	
	/**
		 */
	public static String createSourceName(String filename) {

//		  System.out.println("*************createSourceName: " + filename);
		  String sourceName = filename;
			if (sourceName.indexOf("://")>0){
	        	sourceName = sourceName.substring(sourceName.indexOf("://")+3);
	       	} else  if (sourceName.indexOf(":/")>0){
	        	sourceName = sourceName.substring(sourceName.indexOf(":/")+2);
	       	}
			
			String[] splits={
					 "Sources",
					 "localhost:8080/",
					 "localhost/",
					 "oaei.ontologymatching.org/",
					 "WeSiM/productcatalog/",
					 "productcatalog/",
					 "WeSiM/",
					 "oaei/",
					 "Eigene%20Dateien",
					 "Tmp",
					 "tmp",
					 "resources",
					 "data",
					 "test",
			};
			for (int i = 0; i < splits.length; i++) {
				String split = splits[i];
				if (sourceName.indexOf(split)>=0){
					sourceName = sourceName.substring(sourceName.indexOf(split)+split.length());
				}
			}
//
//	    	if (sourceName.lastIndexOf("\\")>=0){
//	    		sourceName = sourceName.substring(sourceName.lastIndexOf("\\")+1);
//	    	}
//	    	
////	    	if (sourceName.lastIndexOf("/")>=0){
////	    		sourceName = sourceName.substring(sourceName.lastIndexOf("/")+1);
////	    	}
//	    	if (sourceName.contains(".")){			
//	    		sourceName = sourceName.substring(sourceName.lastIndexOf("."));
//	    	}
//	    	if (sourceName.lastIndexOf("/")>=0){
//			sourceName = sourceName.substring(sourceName.lastIndexOf("/")+1);
//		}
			if (sourceName.indexOf("/")>=0){
				sourceName = sourceName.substring(sourceName.indexOf("/")+1);
	    	}
//	    	if (sourceName.lastIndexOf("\\")>=0){
//    		sourceName = sourceName.substring(sourceName.lastIndexOf("\\")+1);
//		}
	    	if (sourceName.indexOf("\\")>=0){
	    		sourceName = sourceName.substring(sourceName.indexOf("\\")+1);
	    	}
	       	sourceName = sourceName.replaceAll("[ -.:\\/~]", "_");
//	       	if (sourceName.length()>50){  // 100 possible now?
//	       		sourceName = sourceName.substring(sourceName.length()-50);
//	       		sourceName = sourceName.substring(sourceName.indexOf("_")+1);
//	       	}
	       	
	       	while (sourceName.startsWith("_")){
	       		sourceName = sourceName.substring(1);
	       	}
	       	
	       	return sourceName;
	}

	/**
				 */
	public void beforeParse(String schemaName, String fileName) {
		if (schemaName==null) {
			if (fileName.contains(".")){
				sourceName = createSourceName(fileName.substring(0, fileName.lastIndexOf('.')));
			} else {
				sourceName = createSourceName(fileName);
			}
		} else {
			sourceName = cleanString(schemaName);
		}
		this.provider = cleanProvider(fileName);
		beforeParse();
	}
	
	 void beforeParse() {	
		    startParse = System.currentTimeMillis();
		    
			date = new java.util.Date().toString();
			System.out.println("Parse source " + sourceName + " from " + provider + " as of " + date);
			
			// insert source, set status import_started
			if (dbInsert){
				source_id = dataImport.insertSource(sourceName, Source.typeToString(type), null, provider, date);		 
				dataImport.updateSource(source_id, Repository.STATUS_IMPORT_STARTED);
			
				// insert source_rel, set status import_started
				sourcerel_id = dataImport.insertSourceRel(source_id, source_id, SourceRelationship.REL_IS_A, null, null, null, Graph.PREP_LOADED, date);
				dataImport.updateSourceRel(sourcerel_id, Repository.STATUS_IMPORT_STARTED);
			} else {
				graph = new Graph();
				idsUsed = new HashSet<Integer>();
				int id = (int) System.currentTimeMillis(); 
				Source source = new Source(id, sourceName, Source.typeToString(type), null, provider, date);
				graph.setSource(source);
			}
		    String logfile = PARSE_PREFIX + "_" + sourceName + ".log";
		    try {
		    	if (logging!=null) logging = new PrintStream(new FileOutputStream(PARSE_DIR + "/" + logfile), true);
		    }
		    catch(FileNotFoundException e) {
		      System.out.println("Error opening file " + logfile + ": " + e.getMessage());
		    }
	 }
	

	/**
				 */
	public void beforeParse(String odbcEntry, String userName, String userPass, String schemaName) {
		if (schemaName==null) {
			sourceName = createSourceName(odbcEntry);
		} else {
			sourceName = cleanString(schemaName);
		}		
		this.provider = cleanProvider(ODBCParser.DB_URL + odbcEntry + ":" + sourceName);
		beforeParse();
	}
			


	/**
			 */
	public void afterParse() {		
	    endParse = System.currentTimeMillis();
	    System.out.println("Parse time: " + (endParse-startParse));
//	    dataImport.closeDatabaseConnection();
	    if (logging!=null) {
	    	logging.close();
	    }	    
	 // set status import_done for source and source_rel
	    if (dbInsert){
	    	dataImport.updateSource(source_id, Repository.STATUS_IMPORT_DONE);
	    	dataImport.updateSourceRel(sourcerel_id, Repository.STATUS_IMPORT_DONE);
	    } else {
	    	if (this instanceof ListParser){
	    		graph=null;
	    	} else {
		    // check cycles and preprocess
		    	graph.checkGraphCycles();
		    	prep.preprocessGraph(graph);
	    	}
	    }
	}
	
	/**
					 */
	public int insertObject(int sourceId, String accession, String name,
            String type, String typespace, int kind,
            String comment, String synonyms) {
		if (dbInsert){
			return dataImport.insertObject(sourceId, accession, name, type, typespace, kind, comment, synonyms);
		}
		int id = idsUsed.size()+1;
		while (idsUsed.contains(id)) id++;
		Element element = new Element(id, sourceId, name, accession, type, typespace, kind, comment, synonyms);
		graph.addVertex(element);
		idsUsed.add(id);
		return id;
	}
	
	public int insertObject(int sourceId, String accession, String name, int kind) {
		if (dbInsert){
			return dataImport.insertObject(sourceId, accession, name, null, null, kind, null, null);
		}
		int id = idsUsed.size()+1;
		while (idsUsed.contains(id)) id++;
		Element element = new Element(id, sourceId, name, accession, null, null, kind, null, null);
		graph.addVertex(element);	
		idsUsed.add(id);
		return id;
	}
	
	public void updateObject(int sourceId, int elementId, String accession, String name,
            String type, String typespace, int kind,
            String comment, String synonyms) {
		if (dbInsert){
			dataImport.updateObject(sourceId, elementId,
				accession, name, type, typespace, kind, comment, synonyms);
		} else {
			Element element = graph.getElementWithId(elementId);
			element.setAccession(accession);
			element.setLabel(name);
			element.setType(type);
			element.setTypespace(typespace);
			element.setKind(kind);
			element.setComment(comment);
			element.setSynonyms(synonyms);			
		}
	}
	
	public void updateObject(int sourceId, int elementId, String type, String typespace) {
		if (dbInsert){
			dataImport.updateObject(sourceId, elementId,
					null, null, type, typespace, Element.UNDEF, null, null);
		} else {
			Element element = graph.getElementWithId(elementId);
			element.setType(type);
			element.setTypespace(typespace);	
		}
	}
	
	
	public void updateSourceURL(int source_id, String url) {
		if (dbInsert){
			dataImport.updateSourceUrl(source_id, url);
		} else {
			graph.getSource().setUrl(url);
		}
	}
	
	public void insertLink(int sourceRelId, int object1Id, int object2Id) {
		insertLink(sourceRelId, object1Id, object2Id, null);
	}
	
	public void insertLink(int sourceRelId, int object1Id, int object2Id, String type) {
		if (dbInsert){
			dataImport.insertObjectRel(sourceRelId, object1Id, object2Id, -1, type) ;
		} else {
			Element element1 = graph.getElementWithId(object1Id);
			if (element1==null){
				System.out.println("InsertParser.insertLink Error element with that id not found " + object1Id);
				return;
			}
			Element element2 = graph.getElementWithId(object2Id);
			if (element2==null){
				System.out.println("InsertParser.insertLink Error element with that id not found " + object2Id);
				return;
			}
			graph.addEdge(element1, element2);
		}
	}

	
	public void setInformation(String author, String domain, String version, String comment) {
		if (dbInsert){
			dataImport.updateSource(source_id, author, domain, version, comment);
		} else {
			graph.getSource().setAuthor(author);
			graph.getSource().setDomain(domain);
			graph.getSource().setVersion(version);
			graph.getSource().setComment(comment);
		}
	}
	

	/** 
	 * remove from the given input string certain character 
	 */
	public String cleanString(String value) {
		value = value.replaceAll("://", "_");
		value = value.replaceAll(":/", "_");
		value = value.replaceAll("[ -.:/~]", "_");
	
		return value;
	}

	public String cleanProvider(String provider) {
		provider =provider.replaceAll("\\\\", "/");
		return provider;
	}
	
	public int  parseSingleSource(String filename){
		parseSingleSource(filename, null, null, null, null, null);
		return source_id;
	}
	
	public int parseSingleSource(String filename, String schemaName){
		parseSingleSource(filename, schemaName, null, null, null, null);
		return source_id;
	}
	
	/**
	 * parse multiple files which are independent from each other into the repository 
	 */
	public void parseMultipleSources(String[] filename, String[] schemaName, String author, String domain, String version, String comment) {
		 if (filename==null || filename.length==0) {
			 return;
		 }
		 if (dbInsert){
			 String parentName = getParentName(filename);
			 int parentId = dataImport.insertSource(parentName, Source.typeToString(Source.UNDEF), null, null, date);	
			 dataImport.updateSource(parentId, Repository.STATUS_IMPORT_STARTED);
			 for (int i = 0; i < filename.length; i++) {
				 if (schemaName!=null){
					 parseSingleSource(filename[i], schemaName[i], author, domain, version, comment);
				 } else {
					 parseSingleSource(filename[i], null, author, domain, version, comment);
				 }
				 int rel_id =  dataImport.insertSourceRel(parentId, source_id, SourceRelationship.REL_CONTAINS, null, null, null, Graph.PREP_LOADED, date);
				 dataImport.updateSourceRel(rel_id, Repository.STATUS_IMPORT_DONE);
			 }
			 dataImport.updateSource(parentId, Repository.STATUS_IMPORT_DONE);
		 } else {
			 // TODO support parseMultipleSources without database
			 System.out.println("parseMultipleSources() not available for dbInsert=false");
		 }
	}
	
	private String getParentName(String[] filename){
		String parentName = filename[0];

		for (int i = 0; i < filename.length; i++) {
			if (i==0){
				 parentName = filename[i];
				 parentName = shorten(parentName);
				 continue;
			}
			String file = shorten( filename[i]);
			if (parentName.equals(file)){
				continue;
			}
			if (file.startsWith(parentName)){
				continue;
			}
			while (!file.startsWith(parentName)){
				parentName = shorten(parentName);

				if (parentName==null){
					parentName = System.currentTimeMillis()+"";
					break;
				}
			}
		}
		parentName = createSourceName(parentName);
		return parentName;		
	}
	
	private String shorten(String name){
		int lastSlash = name.lastIndexOf("/");
		int lastBackslash = name.lastIndexOf("\\");
		if (lastSlash>-1 || lastBackslash>-1){
			name = name.substring(0, Math.max(lastSlash, lastBackslash));
			return name;
		}
		return null;
	}
	
	/**
	 * parse multiple files which are independent from each other into the repository 
	 */
	public void parseMultipleSources(String[] filenames) {
		parseMultipleSources(filenames, null, null, null, null, null);
	}
	
	public void parseMultipleSources(String directory) {
		parseMultipleSources(directory, null);
	}
	
	static public int parseSingleSource(String filename, boolean dbInsert) {
		String fileLow = filename.toLowerCase();
		InsertParser par = null;
		int source_id =-1;
		
	    if (fileLow.endsWith(XDR) ){
	    	par = new XDRParser(dbInsert);
	    } else if (fileLow.endsWith(RDF) || fileLow.endsWith(OWL)){
	    	par = new OWLParser_V3(dbInsert);
	    } else if (fileLow.endsWith(XSD)){
	    	par = new XSDParser(dbInsert);
	    } else if (fileLow.endsWith(CSV)){
	    	par = new CSVParser(dbInsert);
	    } else if (fileLow.endsWith(SQL)){
	    	par = new SQLParser(dbInsert);
	    } else if (fileLow.endsWith(TXT)){
	    	par = new ListParser(dbInsert);
	    } else {
	    	int i = fileLow.lastIndexOf(".");
	    	if (i<(fileLow.length()-4)){
	    		// folder not file
	    		XSDParser par2 = new XSDParser(dbInsert);
	    		par2.parseCompositeSources(filename, null);
	    		par2.closeAll();
	    		return source_id;
	    	}
	    }
	    
	    source_id = par.parseSingleSource(filename, null, null, null, null, null);
		par.closeAll();
	    return source_id;
	}
	
	/**
	 * parse files in a directory which are independent from each other into the repository 
	 */
	public void parseMultipleSources(String directory, String[] schemaNames,String author, String domain, String version, String comment) {
	    File currentDir = new File(directory);
	    if (! currentDir.exists()) {
	      System.out.println("parseMultipleSources(): Directory " + directory + " does not exist!");
	      return;
	    }

	    if (this instanceof XDRParser){
	    	USE_FILETYPES[0]=XDR;
	    } else if (this instanceof OWLParser_V3){
	    	 USE_FILETYPES = new String[2];
	    	 USE_FILETYPES[0]=RDF;
	    	 USE_FILETYPES[1]=OWL;
	    } else if (this instanceof XSDParser){
	    	USE_FILETYPES[0]=XSD;
	    } else if (this instanceof CSVParser){
	    	USE_FILETYPES[0]=CSV;
	    } else if (this instanceof SQLParser){
	    	USE_FILETYPES[0]=SQL;
	    }	    
	    
	    FilenameFilter filter = new FilenameFilter() {
	      public boolean accept(File dir, String name) {
	    	  for (int i = 0; i < USE_FILETYPES.length; i++) {
				String type = USE_FILETYPES[i];
				if (name.toLowerCase().endsWith(type)){
					return true;
				}
			}
	        return false;
	      }
	    };
	    String[] infiles = currentDir.list(filter);
	    for (int i = 0; i < infiles.length; i++) {
	    	infiles[i] = directory+"/" + infiles[i];
		}
		parseMultipleSources(infiles, schemaNames, author, domain, version, comment);
	}
	
	/**
	 * parse files in a directory which are independent from each other into the repository 
	 */
	public void parseMultipleSources(String directory, String[] schemaNames) {
		parseMultipleSources(directory, schemaNames, null, null, null, null);
	}
	
	
	// abstract function
	
	/**
	 * parse a single file into the repository
	 */
	public abstract int parseSingleSource(String filename, String schemaName, String author, String domain, String version, String comment);
		
	
}
