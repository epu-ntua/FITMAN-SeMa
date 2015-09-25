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

/**
 * This class contains all Strings used for the examples.
 * 
 * @author Sabine Massmann
 */
public class Strings {
	// ************************************************************
	// Strategies
	public static final String STRAT_CONTEXT = "Context";
	public static final String STRAT_NODES = "Nodes";	
//	public static final String STRAT_ALLCONTEXT = Manager
//			.matchStratToString(Manager.MATCH_STRAT_ALLCONT);
//	public static final String STRAT_FILTEREDCONTEXT = Manager
//			.matchStratToString(Manager.MATCH_STRAT_FILTCONT);
	public static final String STRAT_FRAGMENT = "Fragment";
	// not used: taxonomy
//	public static final String STRAT_TAXONOMY = "Taxonomy";
	public static final String STRAT_REUSE = "Reuse";
	public static final String STRAT_COMBINEDREUSE = "Combined Reuse";
	//	// **************************************
	//	// Reuse => internal Match Results
	public static final String INTERNAL_SIM = "INTERNAL_SIM";
	// **************************************
	// properties
	public static final String PROPERTY_FILE = "coma.properties";
	// **************************************
	// abbreviation and synonyms
	public static final String FILE_SYN_DEFAULT = "PO_syns.txt";
	public static final String FILE_ABB_DEFAULT = "PO_abbrevs.txt";
//	public static final String FILE_SYN = "SHORTPO_syns.txt";
//	public static final String FILE_ABB = "SHORTPO_abbrevs.txt";
	// ************************************************************
	// File endings
	public static final String XSD = ".xsd";
	public static final String XDR = ".xdr";
	public static final String OWL = ".owl";
	public static final String RDF = ".rdf";
	public static final String ASC = ".asc";
	public static final String TXT = ".txt";
	public static final String CSV = ".csv";
	//hung
	public static final String SQL = ".sql";
	// ************************************************************
	// Numbers
	public static final String Number_0 = "0";
	public static final String Number_0_0 = "0.0";
	public static final String Number_0_01 = "0.01";
	public static final String Number_0_1 = "0.1";
	public static final String Number_0_5 = "0.5";
	
		
	
	// ************************************************************
	// default schemas / matchresult
	public static final String[] DEFAULT_ALL_SHORT = {
		"PurchaseOrder small", 
		"PurchaseOrder  medium/large",
		"OAEI Benchmark", 
		"Web Directories",
		"Web Directories large",		
		"other (Spicy+University)",
		"OAEI Anatomy",
	};
	public static final String[] DEFAULT_ALL_EXPLANATION = {
		"5 Schemas, 10 Matchresults", 
		"17 Schemas, 1 + 5 (PO small needed) Matchresults", // , Instances
		"50 Ontologies, 50 Matchresults", //, Instances
		"4 Ontologies, 2 Matchresults", // , Instances
		"4 Ontologies, 6 Matchresults", // , Instances
		"6 Schemas, 3 Matchresults",
		"2 Ontologies, 2 Matchresults",
	};
	
		
	public static final String[] DEFAULT_SCHEMAS_SHORT = {"BMECat", "OpenTrans",
			"Xcbl", "Xcbl35", "PO", 
			"OAEI Benchmark", "Web Directories", "Large Web Directories", 
			"other", "OAEI Anatomy"};
	public static final String[][] DEFAULT_SCHEMAS = {{"BMECat", "BMECat"},
			{"BMECatAll", null}, {"BMECatAll", "BMECatAll-2"},
			{"OpenTrans", "OpenTrans"}, {"OpenTransAll", null},
			{"OpenTransAll", "OpenTransAll-2"}, {"XcblCore", "Xcbl"},
			{"XcblOrder", "Xcbl"}, {"XcblCatalog", "Xcbl"},
			{"Xcbl35", "Xcbl35"}, {"Po_xdr", null},
			{"webdirectories/dmoz.Freizeit.owl","dmoz_Freizeit"},
			{"webdirectories/Google.Freizeit.owl","Google_Freizeit"},
			{"webdirectories/Google.Lebensmittel.owl","Google_Lebensmittel"},
			{"webdirectories/web.Lebensmittel.owl","web_Lebensmittel"},
			
			{"webdirectory/dmoz.owl","webdirectory_dmoz_owl"},
			{"webdirectory/google.owl","webdirectory_google_owl"},
			{"webdirectory/web.owl","webdirectory_web_owl"},
			{"webdirectory/yahoo.small.owl","webdirectory_yahoo_small_owl"},
			
			{"Spicy", null},
			{"University", null},
			
			{"anatomy/mouse_2010.owl", "mouse"},
			{"anatomy/nci_2010.owl", "nci"},
			};

	public static final String[] SPICY_SHORTNAMES = {"deptDB", "orgDB", "s3_source", "s3_target"};
	public static final String[] UNI_SHORTNAMES = {"CSDeptAust", "CSDeptUS"};
	
	public static final String MATCHRESULT_PO =  //".//" + InsertParser.SOURCE_DIR+ 
			"Mappings\\mappings-PO.txt";
	public static final String MATCHRESULT_PO_OP = //".//" + InsertParser.SOURCE_DIR+
			 "Mappings\\mappings-PO-OP.txt";
	public static final String MATCHRESULT_OP_XCBL = // ".//" + InsertParser.SOURCE_DIR+
			 "Mappings\\mappings-OP-Xcbl.txt";
	public static final String MATCHRESULT_OAEI = "OAEI Benchmark Alignments";	
	public static final String MATCHRESULT_WEBDIR = // ".//" + InsertParser.SOURCE_DIR+
	 "Mappings\\Mappings-WebDir.txt";
	public static final String MATCHRESULT_WEBDIR_LARGE = // ".//" + InsertParser.SOURCE_DIR+
		 "webdirectory\\webdirectory_Mappings.small.txt";
	public static final String[] DEFAULT_MATCHRESULTS = {"PO",
		"PO-OpenTrans", "OpenTrans-Xcbl", MATCHRESULT_OAEI, "Web Directories", "Large Web Directories",
		"other", "OAEI Anatomy"};	
	
	public static final String MATCHRESULT_SPICY = // ".//" + SourceParser.SOURCE_DIR+
	 "Spicy\\Spicy_Mapping.txt";
	public static final String MATCHRESULT_UNI =  //".//" + SourceParser.SOURCE_DIR+
	 "University\\CSDept.txt";
	
	public static final String MATCHRESULT_ANATOMY_PARTIAL =  //".//" + SourceParser.SOURCE_DIR+
		 "anatomy\\partial_2010.txt";
	public static final String MATCHRESULT_ANATOMY =  //".//" + SourceParser.SOURCE_DIR+
		 "anatomy\\pm_2010.txt";
	
	public static final String[] DEFAULT_INSTANCES = {"BMECat",
		"OpenTrans", "OAEI Benchmark", "Web Directories", "Large Web Directories"
	};		
	
	
//    public static String BENCHMARK_URL = "http://oaei.inrialpes.fr/2006/benchmarks/";
//    public static String BENCHMARK_URL = "http://oaei.ontologymatching.org/2006/benchmarks/";
//    public static String BENCHMARK_URL = "/Sources/oaei/2006/benchmarks/";
//    public static String BENCHMARK_YEAR = "2006";
//    public static String BENCHMARK_URL = "oaei/2007/benchmarks/"; // "/Sources/oaei/2007/benchmarks/";
//    public static String BENCHMARK_YEAR = "2007";
//    public static String BENCHMARK_URL = "oaei/2009/benchmarks/"; // "/Sources/oaei/2007/benchmarks/";
//    public static String BENCHMARK_YEAR = "2009";
    public static String BENCHMARK_URL = "oaei/2010/benchmarks/"; // "/Sources/oaei/2007/benchmarks/";
    public static String BENCHMARK_YEAR = "2010";
//    public static String BENCHMARK_URL = "/Sources/oaei/2008/benchmarks/";
//    public static String BENCHMARK_YEAR = "2008";
    
    public static String[] localContestOntos = {
            "101",
//            "102",
            "103",
            "104",
            "201",
            "202",
            "203",
            "204",
            "205",
            "206", //     "206/onto-utf8.rdf",
            "207", //     "207/onto-utf8.rdf",
            "208",
            "209",
            "210", // "210/onto-utf8.rdf",
            "221",
            "222",
            "223",
            "224",
            "225",
            "228",
            "230",
            "231",
            "232",
            "233",
            "236",
            "237",
            "238",
            "239",
            "240",
            "241",
            "246",
            "247",
            "248",
            "249",
            "250",
            "251",
            "252",
            "253",
            "254",
            "257",
            "258",
            "259",
            "260",
            "261",
            "262",
            "265",
            "266",
            "301",
            "302",
            "303",
            "304",
            };
}