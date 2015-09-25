package de.wdilab.ml.impl.matcher.simple.strongweaksim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;

public class StrongSim {

	public static HashMap<String, HashMap<String, Double>>[] createQGramsStrong (Directory lucene[], int q, HashMap<String, String>[] cand ) throws Exception {
	
			@SuppressWarnings("unchecked")
			HashMap<String, HashMap<String, Double>>[] result = new HashMap[2];
	//		System.out.print("Creating qgrams ... ");
			
			for (int x=0; x<2; x++) {
	
				result[x] = new HashMap<String, HashMap<String, Double>>();
				IndexReader reader = new IndexSearcher(lucene[x]).getIndexReader();
	
				for (String id: cand[x].keySet()) {
					
					HashMap<String, Double> qgrams = new HashMap<String, Double>();
					
					String[] v = cand[x].get(id).trim().split(" ");
					for (int k=0; k<v.length; k++) {
						WeakSim.addQGrams(qgrams, v[k], reader, q);
					}
					
					result[x].put(id, qgrams);
				}
			}
	
	//		System.out.println("done.");
			return result;
		}

	public static int[][] generateMatching (Set<String>[][] qgram, double jaccard) {
			
			// initialize similarity matrix (must be squared) [for some reason the Hungarian Algo changes matrix; therefore an additional matrixCopy is used  
			float matrix[][] = new float[Math.max(qgram[0].length,qgram[1].length)][Math.max(qgram[0].length,qgram[1].length)];
			float matrixCopy[][] = new float[Math.max(qgram[0].length,qgram[1].length)][Math.max(qgram[0].length,qgram[1].length)];
			for (int i=0; i<matrix.length; i++) {
				for (int j=0; j<matrix.length; j++) {
					matrix[i][j]=0f;
					matrixCopy[i][j]=0f;
				}
			}
			
			// add similarties if above a threshold (Jaccard); since the Hungarian Algo minimizes the weight the negative sim is used for matrix
			for (int i=0; i<qgram[0].length; i++) {
				for (int j=0; j<qgram[1].length; j++) {
					HashSet<String> temp = new HashSet<String>(qgram[0][i]);
					temp.retainAll(qgram[1][j]);
					double jaccsim = ((double)temp.size())/((double)(qgram[0][i].size()+qgram[1][j].size()-temp.size()));
					if (jaccsim>jaccard) {
	//					System.out.print(i + "=" + j + "; ");
						matrix[i][j]=(float) -jaccsim;
						matrixCopy[i][j]=(float) -jaccsim;
					}
				}
				
			}
	//		System.out.println("\n");
			
			// run the Hungarian Algo to achieve the assignment
			int[][] assignment = new HungarianAlgorithm().computeAssignments(matrixCopy);
			for (int i=0; i<assignment.length; i++) {
	//			System.out.print(assignment[i][0] + "~" + assignment[i][1] + "; ");
			}
	//		System.out.println("\n----");
	
			// transform the assigned (list of correspondences) into two ordered lists (one for each string)
			int[][] result = new int[2][];
			for (int x=0; x<2; x++) {
				result[x] = new int[qgram[x].length];
				for (int i=0; i<result[x].length; i++) {
					
					for (int k=0; k<assignment.length; k++) {
						if (assignment[k][x]==i) {
							result[x][i] = (matrix[assignment[k][0]][assignment[k][1]]==0f) ? -1 : assignment[k][1-x];
							break;
						}
					}
				}
			}
			
			return result;
		}

	public static String generateMatchingPattern (int[][] matching) {
			
			ArrayList<int[]>[] segment = new ArrayList[2];
			for (int x=0; x<2; x++) {
				segment[x] = new ArrayList<int[]>();
				int lastType = 0;
				int lastMatchStart = 0;
				int lastMatchEnd = 0;
				int currentType = 0;
				for (int k=0; k<matching[x].length; k++) {
					
					currentType = (matching[x][k]==-1) ? -1 : 1;
					if (currentType == lastType) {
						
						if (currentType == -1) {
							lastMatchEnd = k;
						} else {
							
							if (matching[x][k] == matching[x][lastMatchEnd] + 1) {
								lastMatchEnd = k;
							} else {
								segment[x].add(new int[]{lastMatchStart, lastMatchEnd});
								lastMatchStart = k;
								lastMatchEnd = k;
							}
							
							
						}
						
					} else {
						// add last segment
						if (k>0) {
							segment[x].add(new int[]{lastMatchStart, lastMatchEnd});
						}
						
						lastMatchStart = k;
						lastMatchEnd = k;
						lastType = currentType;
					}
				}
				
				segment[x].add(new int[]{lastMatchStart, lastMatchEnd});
				
				
				for (int[] s: segment[x]) {
	//				System.out.print("[" + s[0] + "." + s[1] + "]; ");
				}
	//			System.out.println();
			}
			
			
			String result = "";
			for (int x=0; x<2; x++) {
				for (int[] s: segment[x]) {
					
					if (matching[x][s[0]]==-1) {
						result += "-1;";
					} else {
						for (int i=0; i<segment[1-x].size(); i++) {
							if (segment[1-x].get(i)[0]==matching[x][s[0]]) {
								result += i + ";";
								break;
							}
						}
					}
				}
				result += StrongWeakSim.concat;
			}
			
	//		System.out.println("Pattern: " + result);
			return result;
			
		}

	public static HashMap<String, Double> strongSim (Directory lucene[], HashMap<String, Double> match_weak, double t_weak, int q, double jaccard, double alpha) throws Exception {
		
			HashMap<String, HashSet<String>> allPat = new HashMap<String, HashSet<String>>();
			HashMap<String, Double> match_strong = new HashMap<String, Double>();
			HashMap<String, Double> result = new HashMap<String, Double>();
			
			HashMap<String, Double> cand = new HashMap<String, Double>();
			for (String k: match_weak.keySet()) {
				double s = match_weak.get(k); 
				result.put(k, s);
				if (s>=t_weak) {
					cand.put(k, s);
				}
			}
			
			IndexReader[] reader = new IndexReader[2];
			String[][] term = new String[2][];
			for (int x=0; x<2; reader[x]=new IndexSearcher(lucene[x++]).getIndexReader());
			
			for (String k: cand.keySet()) {
				String id[] = k.split(StrongWeakSim.concat);
				
				// create term list
				for (int x=0; x<2; x++) {
					TermDocs docs = reader[x].termDocs(new Term("id", id[x]));
					docs.next();
					TermPositionVector v = (TermPositionVector) reader[x].getTermFreqVector(docs.doc(), "value");
					HashMap<Integer, String> pos2Term = new HashMap<Integer, String>();
					for (int i=0; i<v.getTerms().length; i++) {
						for (int j=0; j<v.getTermPositions(i).length; j++) {
							pos2Term.put(v.getTermPositions(i)[j], v.getTerms()[i]);
						}
					}
					
					Integer[] pos = (Integer[]) pos2Term.keySet().toArray(new Integer[pos2Term.keySet().size()]);
					term[x] = new String[pos.length];
					for (int i=0; i<pos.length; i++) {
						term[x][i] = pos2Term.get(pos[i]);
	//					System.out.print(i + ":" + term[x][i] + "; ");
					}
	//				System.out.println();
				}
	
				// create qgrams
				@SuppressWarnings("unchecked")
				Set<String>[][] qgram = new Set[2][]; 
				for (int x=0; x<2; x++) {
					qgram[x] = new HashSet[term[x].length];
					
					for (int i=0; i<term[x].length; i++) {
						StringBuffer buf = new StringBuffer();
						for (int j=0; j<q-1; j++) buf.append("#");
						buf.append (term[x][i]);
						for (int j=0; j<q-1; j++) buf.append("#");
	
						qgram[x][i] = new HashSet<String>();
						for (int j=0; j<buf.length()-q; j++) {
							qgram[x][i].add(buf.substring(j, j+q));
						}
					}
				}
	
				
				int[][] matching = generateMatching (qgram, jaccard);
//				for (int x=0; x<2; x++) {
//					for (int i=0; i<matching[x].length; i++) {
//						System.out.print(i + ":" + matching[x][i] + "; ");
//					}
//					System.out.println("\n");
//				}
//				System.out.println("\n");

				
				/* compute string similarity */
				HashMap<String, String>[] matchPair = new HashMap[2];
				for (int x=0; x<2; x++) {
					matchPair[x] = new HashMap<String, String>();
					String value = "";
					for (int i=0; i<qgram[x].length; i++) {
						if (matching[x][i]>-1) value += term[x][i] + " ";
					}
					matchPair[x].put(id[x], value);
				}
				match_strong.putAll(WeakSim.cosine(createQGramsStrong(lucene, q, matchPair), t_weak));
	
				/* generate pattern */
				String matchingPattern = generateMatchingPattern(matching);
				HashSet<String> p = allPat.get(matchingPattern);
				if (p==null) p = new HashSet<String>();
				p.add(k);
				allPat.put(matchingPattern, p);
			}
	
			
			for (String p: allPat.keySet()) {
	//			System.out.println(p + " / " + ((double)(100*allPat.get(p).size())/((double)cand.keySet().size())) + "%");
				if (((double)(allPat.get(p).size())/((double)cand.keySet().size()))>alpha) {
					for (String k: allPat.get(p)) {
	//					System.out.println("boost from " + match_weak.get(k) + " to " + match_strong.get(k));
						result.put(k, match_strong.get(k));
					}
				}
			}
			
			return result;
		}

}
