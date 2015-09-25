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

package de.wdilab.coma.matching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.wdilab.ml.impl.matcher.simple.cosine.CosineFullyCachedPPJoinPlus;
import de.wdilab.ml.impl.matcher.simple.cosine.CosinePPJoinPlus;
import de.wdilab.ml.impl.matcher.simple.jaccard.JaccardPPJoinJavaFullyCached;
import de.wdilab.ml.impl.matcher.simple.jarowinkler.JaroWinklerLucene;
import de.wdilab.ml.impl.matcher.simple.levenshtein.EDJoin;
import de.wdilab.ml.impl.matcher.simple.levenshtein.LevenshteinLucene;
import de.wdilab.ml.impl.matcher.simple.meta.DatatypeMatcher;
import de.wdilab.ml.impl.matcher.simple.meta.VectorMatcher;
import de.wdilab.ml.impl.matcher.simple.tfidf.LuceneTFIDFFullyCachedAlternative;
import de.wdilab.ml.impl.matcher.simple.trigram.ComaTrigram2;
import de.wdilab.ml.impl.matcher.simple.trigram.IFuiceTrigram;
import de.wdilab.ml.impl.matcher.simple.trigram.LowMemoryTrigram;
import de.wdilab.ml.impl.matcher.simple.trigram.TrigramOpt;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;

/**
 * This class represent the similarity measures which are the match 
 * algorithms to e.g. compare strings or vectors. These match 
 * algorithms are implemented in the match library (dependency to
 * additional match library).
 * 
 * part of the grammar
 * 
 * @author Hong Hai Do, Sabine Massmann
 */
public class SimilarityMeasure{
	 String name= null;
	 int id = -1;

	  // start counting 1000
	  
	  //unique similarity measures
	  public static final int SIM_VECT_FEATURES    = Constants.MEAS_CNT + 1;
	  //String similarity measures
	  public static final int SIM_DATATYPE     = Constants.MEAS_CNT + 2;
	  public static final int SIM_STR_USERSYN      = Constants.MEAS_CNT + 3;
	//not used: taxonomy
	//  public static final int SIM_STR_USERTAX      = Manager.MEAS_CNT + 4; //taxSim
	  
	  // in matcher not used and not yet supported by the MatchLibrary
	  // XXX: add later if needed
//	  public static final int SIM_STR_PREFIX       = Manager.MEAS_CNT + 11;
//	  public static final int SIM_STR_SUFFIX       = Manager.MEAS_CNT + 12;
//	  public static final int SIM_STR_AFFIX        = Manager.MEAS_CNT + 13; // max from prefix and suffix	  
//	  public static final int SIM_STR_CHARFREQ     = Manager.MEAS_CNT + 14;	  
//	  public static final int SIM_STR_DIGRAM       = Manager.MEAS_CNT + 15; 
//	  public static final int SIM_STR_SOUNDEX      = Manager.MEAS_CNT + 17;
//	  public static final int SIM_STR_SINGLEERR    = Manager.MEAS_CNT + 19;
	  
	  // not clear what it stands for in version 2008
//	  public static final int SIM_STR_NAME         = Manager.MEAS_CNT + 20;
	  
	// DEFAULT
	  public static final int SIM_STR_EQUAL        = Constants.MEAS_CNT + 5;  
	  
	  // DEFAULT
	  public static final int SIM_STR_EDITDIST     			= Constants.MEAS_CNT + 10;
	  // VERSIONS
	  public static final int SIM_STR_EDJOIN     			= Constants.MEAS_CNT + 11;
	  public static final int SIM_STR_LEVENSHTEIN_LUCENE    = Constants.MEAS_CNT + 12;
	  
	  // DEFAULT
	  public static final int SIM_STR_COSINE        		= Constants.MEAS_CNT + 15;
	  // VERSIONS	  
	  public static final int SIM_STR_COSINE_PPJOINPLUSFULLYCACHED        = Constants.MEAS_CNT + 16;
	  public static final int SIM_STR_COSINE_PPJOINPLUS     = Constants.MEAS_CNT+ 17;

	  // DEFAULT
	  public static final int SIM_STR_JACCARD      			= Constants.MEAS_CNT + 20;

	  // DEFAULT
	  public static final int SIM_STR_JAROWINKLER   = Constants.MEAS_CNT + 25;		
	  
	  // DEFAULT
	  public static final int SIM_STR_TRIGRAM      			= Constants.MEAS_CNT + 30; 
	  // VERSIONS	 
	  public static final int SIM_STR_TRIGRAM_COMA       	= Constants.MEAS_CNT + 31;
	  public static final int SIM_STR_TRIGRAM_IFUICE  		= Constants.MEAS_CNT + 32;
	  public static final int SIM_STR_TRIGRAM_LOWMEM      	= Constants.MEAS_CNT + 33;
	  public static final int SIM_STR_TRIGRAM_OPT       	= Constants.MEAS_CNT + 34;
	  
	  // DEFAULT
	  public static final int SIM_DOC_TFIDF      			= Constants.MEAS_CNT + 40;


	  // 0.0f 0.01f 0.1f 0.2f 0.3f 0.4f 0.5f 0.6f
		public static float DEFAULT_THRESHOLD = 0.01f; // default
	  
		public static final Integer[] SIMMEASURE = {
			SIM_VECT_FEATURES, SIM_DATATYPE, // SIM_STR_USERSYN,
			SIM_STR_TRIGRAM, 
			SIM_STR_EDITDIST, 
			SIM_STR_EQUAL, 
			SIM_STR_COSINE, SIM_STR_JACCARD, SIM_STR_JAROWINKLER,
			SIM_DOC_TFIDF,
		};
		public static final List<Integer> SIMMEASURE_LIST = Arrays.asList(SIMMEASURE);
		
		public static final Integer[] SIMMEASURE_STRING_GENERAL = {
			SIM_STR_TRIGRAM, 
			SIM_STR_EDITDIST, SIM_STR_EQUAL, 
			SIM_STR_COSINE, SIM_STR_JACCARD, SIM_STR_JAROWINKLER,
			SIM_DOC_TFIDF,
		};
		
		public static final Integer[] SIMMEASURE_STRING = {
			SIM_STR_TRIGRAM_COMA, 
			SIM_STR_TRIGRAM_IFUICE,
			SIM_STR_TRIGRAM_LOWMEM,
			SIM_STR_TRIGRAM_OPT,
			SIM_STR_COSINE_PPJOINPLUSFULLYCACHED,
			SIM_STR_COSINE_PPJOINPLUS,
			SIM_STR_LEVENSHTEIN_LUCENE,
		};
		
		public static final Integer[] SIMMEASURE_SHORT_STRING = {
			SIM_STR_TRIGRAM_COMA, 
			SIM_STR_TRIGRAM_IFUICE,
			SIM_STR_TRIGRAM_LOWMEM,
			SIM_STR_TRIGRAM_OPT,
			SIM_STR_JAROWINKLER,
			SIM_STR_LEVENSHTEIN_LUCENE,
			SIM_STR_EDITDIST,
			SIM_STR_EQUAL,
//			SIM_STR_EDJOIN // onyl 0
		};

		public static final Integer[] SIMMEASURE_LONG_STRING = {
			SIM_STR_COSINE_PPJOINPLUSFULLYCACHED, 
			SIM_STR_COSINE_PPJOINPLUS,
			SIM_STR_JACCARD,
			SIM_DOC_TFIDF,	
		};

		

	    public SimilarityMeasure (int simmeasure){
			id = simmeasure;
			this.name=measureToString(simmeasure);
	    }
	    
	    public static String measureToString(int measure) {
	        switch (measure) {
	          case SIM_VECT_FEATURES:    return "FeatVect";
	          case SIM_DATATYPE:     	 return "DatatypeSimilarity"; // DatatypeSimilarity DatatypeSim
	          case SIM_STR_USERSYN:      return "UserSyn";
	          case SIM_STR_TRIGRAM:      return "Trigram";
	          case SIM_STR_EDITDIST:     return "EditDist";
	          case SIM_STR_EQUAL:        return "Sim_Equal";
	          case SIM_STR_COSINE:       return "Cosine";
	          case SIM_STR_JACCARD:      return "Jaccard";
	          case SIM_STR_JAROWINKLER:  return "Jarowinkler";
	          case SIM_DOC_TFIDF:        return "TFIDF";
	          
	          //VERSIONS
	          case SIM_STR_TRIGRAM_COMA:        				return "TrigramComa";
	          case SIM_STR_TRIGRAM_IFUICE:      				return "TrigramIFuice";
	          case SIM_STR_TRIGRAM_LOWMEM:      				return "TrigramLowMem";
	          case SIM_STR_TRIGRAM_OPT:      					return "TrigramOpt";	             
	          case SIM_STR_EDJOIN:        						return "EDJoin";
	          case SIM_STR_LEVENSHTEIN_LUCENE:  				return "LevenshteinLucene";
	          case SIM_STR_COSINE_PPJOINPLUSFULLYCACHED:    	return "CosinePPJoin+FullyCached";
	          case SIM_STR_COSINE_PPJOINPLUS:   				return "CosinePPJoin";
	          	          
	          default: return "Undef";
	        }
	      }
	    
	    /**
	     * @param measure
	     * @return the id for the given string representation of a resolution
	     */
	    public static int stringToMeasure(String measure) {
	        if (measure==null) return Constants.UNDEF;
	        measure= measure.toLowerCase();
	        if (measure.equals("featvect"))      return SIM_VECT_FEATURES;
	        else if (measure.equals("datatypesimilarity"))  return SIM_DATATYPE;
	        else if (measure.equals("usersyn")) return SIM_STR_USERSYN;
	        else if (measure.equals("trigram"))  return SIM_STR_TRIGRAM;
	        else if (measure.equals("editdist"))  return SIM_STR_EDITDIST;
	        else if (measure.equals("sim_equal")) return SIM_STR_EQUAL;
	        else if (measure.equals("cosine")) return SIM_STR_COSINE;
	        else if (measure.equals("jaccard")) return SIM_STR_JACCARD;
	        else if (measure.equals("jarowinkler"))  return SIM_STR_JAROWINKLER;
	        else if (measure.equals("tfidf"))  return SIM_DOC_TFIDF;

	        // VERSIONS
	        else if (measure.equals("trigramcoma"))  return SIM_STR_TRIGRAM_COMA;
	        else if (measure.equals("trigramifuice"))  return SIM_STR_TRIGRAM_IFUICE;
	        else if (measure.equals("trigramlowmem"))  return SIM_STR_TRIGRAM_LOWMEM;
	        else if (measure.equals("trigramopt"))  return SIM_STR_TRIGRAM_OPT;
	        else if (measure.equals("edjoin"))  return SIM_STR_EDJOIN;
	        else if (measure.equals("levenshteinlucene"))  return SIM_STR_LEVENSHTEIN_LUCENE;	        
	        else if (measure.equals("cosineppjoin+fullycached"))  return SIM_STR_COSINE_PPJOINPLUSFULLYCACHED;
	        else if (measure.equals("cosineppjoin"))  return SIM_STR_COSINE_PPJOINPLUS;
	        return Constants.UNDEF;
	      }
	    
		public int getId() { return id; }
	    
		public String getName() {
			if (name==null){
				// generate Name
				name=toString();
			}
			return name; 
		}
	    
	    
	    /* (non-Javadoc)
	     * @see java.lang.Object#toString()
	     */
	    public String toString(){
	    	String s = measureToString(id);
	    	return s;
	    }

	    public static IObjectMatcher getMatcher(int id, ArrayList<String> wordList, ArrayList<String> synonymList){

				 IObjectMatcher matcher = null;
				switch (id) {
				case SIM_VECT_FEATURES:
//					matcher =  new VectorMatcher("attr", "attr", DEFAULT_THRESHOLD);
					matcher =  new VectorMatcher("attr", "attr", 0f);
					break;
				case SIM_DATATYPE:				
//					matcher =  new DatatypeMatcher("attr", "attr", DEFAULT_THRESHOLD);
					matcher =  new DatatypeMatcher("attr", "attr", 0f);
					break;
				case SIM_STR_USERSYN:
					// TODO SIM_STR_USERSYN
					break;
				case SIM_STR_TRIGRAM://
				    // matcher =  new ComaTrigram2("attr", "attr", DEFAULT_THRESHOLD, wordList, synonymList);
					matcher =  new IFuiceTrigram("attr", "attr", DEFAULT_THRESHOLD);
					break;
				case SIM_STR_EDITDIST: //+
					// TODO check Levenshtein EDJoin
//					matcher =  new EDJoin("attr", "attr", 0.0f, 5);
					matcher =  new LevenshteinLucene("attr", "attr");
					break;
				case SIM_STR_EQUAL: //+
					// use threshold 1 to get only equal elements back
//					matcher =  new EDJoin("attr", "attr", 1f, 5);
					matcher =  new LevenshteinLucene("attr", "attr", 1f);
					break;
		        case SIM_STR_COSINE: //
		        	matcher =  new CosineFullyCachedPPJoinPlus("attr", "attr", DEFAULT_THRESHOLD);
//		        	matcher =  new CosinePPJoinPlus("attr", "attr", 0.0f);
//		        	matcher =  new CosineSimmetrics("attr", "attr", 0.0f);
		        	break;
		        case SIM_STR_JACCARD://+
		        	matcher =  new JaccardPPJoinJavaFullyCached("attr", "attr", DEFAULT_THRESHOLD);
		        	break;
		        case SIM_STR_JAROWINKLER://+
		        	matcher =  new JaroWinklerLucene("attr", "attr", DEFAULT_THRESHOLD);
		        	break;
				case SIM_DOC_TFIDF:// problem 	
					matcher = new LuceneTFIDFFullyCachedAlternative("attr", "attr", 0f);
					break;
				case SIM_STR_TRIGRAM_COMA://+
					matcher =  new ComaTrigram2("attr", "attr", DEFAULT_THRESHOLD, wordList, synonymList); 
					break;
				case SIM_STR_TRIGRAM_IFUICE://+
					matcher =  new IFuiceTrigram("attr", "attr", DEFAULT_THRESHOLD);
					break;
				case SIM_STR_TRIGRAM_LOWMEM://+
					matcher =  new LowMemoryTrigram("attr", "attr", DEFAULT_THRESHOLD); // <600 sec for anatomy
					break;				
				case SIM_STR_TRIGRAM_OPT://+
					matcher =  new TrigramOpt("attr", "attr", DEFAULT_THRESHOLD);
					break;			
				case SIM_STR_EDJOIN: // only 0
					matcher =  new EDJoin("attr", "attr", 0.0f, 5);
					break;
				case SIM_STR_LEVENSHTEIN_LUCENE://+
					matcher =  new LevenshteinLucene("attr", "attr", DEFAULT_THRESHOLD);							
					break;			
		        case SIM_STR_COSINE_PPJOINPLUSFULLYCACHED://+
		        	matcher =  new CosineFullyCachedPPJoinPlus("attr", "attr", DEFAULT_THRESHOLD);
		        	break;
		        case SIM_STR_COSINE_PPJOINPLUS://+
		        	matcher =  new CosinePPJoinPlus("attr", "attr", DEFAULT_THRESHOLD);
		        	break;
				}
				
				return matcher;	
			}
		
		  public static Class getMeasureInputType(int measure) {
			    switch (measure) {
			      case SIM_VECT_FEATURES:
			        return float[].class;
			        
			      case SIM_DATATYPE:
			      case SIM_STR_USERSYN:
			      case SIM_STR_TRIGRAM:
			      case SIM_STR_EDITDIST:
			      case SIM_STR_EQUAL:
			    	  
			      case SIM_STR_COSINE:
			      case SIM_STR_JACCARD:
			      case SIM_STR_JAROWINKLER:
			    	  
			      case SIM_STR_TRIGRAM_COMA:
			      case SIM_STR_TRIGRAM_IFUICE:
			      case SIM_STR_TRIGRAM_LOWMEM:
			    	  
			      case SIM_DOC_TFIDF:
			        return String.class;
			      default:
			        return null;
			    }
			  }

}
