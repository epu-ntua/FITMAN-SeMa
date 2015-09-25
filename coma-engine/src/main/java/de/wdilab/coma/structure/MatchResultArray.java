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

package de.wdilab.coma.structure;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MatchResultArray contains the correspondences (with similarity values)
 * between the objects of a source graph and a target graph - this
 * implementation keeps similarity values in an array (+ faster access, - limit
 * to main memory )
 * 
 * @author Hong Hai Do, Sabine Massmann
 * 
 */
public class MatchResultArray extends MatchResult {
	// variables
	/**
	 * similarity matrix contains similarity values for all combination of
	 * source and target objects
	 */
	float[][] simMatrix;
	/** The index to hash the source objects (@param hashing has to be true */
	HashMap<Object, Integer> srcObjectIndex;
	/** The index to hash the target objects (@param hashing has to be true */
	HashMap<Object, Integer> trgObjectIndex;
	
	
	// constructors
	/**
	 * Constructor which initiates internal settings
	 */
	public MatchResultArray() {
		super();
		if (hashing) {
			srcObjectIndex = new HashMap<Object, Integer>();
			trgObjectIndex = new HashMap<Object, Integer>();
		}
	}

	/**
	 * Constructor which initiates internal settings and add given objects
	 */
	public MatchResultArray(ArrayList<Object> srcObjects,
			ArrayList<Object> trgObjects) {
		this.srcObjects = srcObjects;
		this.trgObjects = trgObjects;
		if (hashing) {
			srcObjectIndex = new HashMap<Object, Integer>();
			for (int i=0; i<srcObjects.size(); i++) srcObjectIndex.put(srcObjects.get(i), new Integer(i));
			trgObjectIndex = new HashMap<Object, Integer>();
			for (int i=0; i<trgObjects.size(); i++) trgObjectIndex.put(trgObjects.get(i), new Integer(i));
		}
		simMatrix = new float[srcObjects.size()] [trgObjects.size()];
	}
	
	/**
	 * Constructor which initiates internal settings and add given objects
	 */
	public MatchResultArray(ArrayList<Object> srcObjects,
			ArrayList<Object> trgObjects, float defaultValue) {
		this.srcObjects = srcObjects;
		this.trgObjects = trgObjects;
		if (hashing) {
			srcObjectIndex = new HashMap<Object, Integer>();
			for (int i=0; i<srcObjects.size(); i++) srcObjectIndex.put(srcObjects.get(i), new Integer(i));
			trgObjectIndex = new HashMap<Object, Integer>();
			for (int i=0; i<trgObjects.size(); i++) trgObjectIndex.put(trgObjects.get(i), new Integer(i));
		}
		simMatrix = new float[srcObjects.size()] [trgObjects.size()];
		for (int i = 0; i < simMatrix.length; i++) {
			for (int j = 0; j < simMatrix[0].length; j++) {
				simMatrix[i][j]=defaultValue;
			}
		}
	}
	
	/**
	 * Constructor which initiates internal settings and sets potential match objects 
	 * (either nodes or paths) and their similarity values
	 * 
	 * @param srcObjects source objects (match candidates)
	 * @param trgObjects target objects (match candidates) 
	 */
	public MatchResultArray(ArrayList<Object> srcObjects,
			ArrayList<Object> trgObjects, float[][] simMatrix) {
		this.srcObjects = srcObjects;
		this.trgObjects = trgObjects;
		if (hashing) {
			srcObjectIndex = new HashMap<Object, Integer>();
			for (int i=0; i<srcObjects.size(); i++) srcObjectIndex.put(srcObjects.get(i), new Integer(i));
			trgObjectIndex = new HashMap<Object, Integer>();
			for (int i=0; i<trgObjects.size(); i++) trgObjectIndex.put(trgObjects.get(i), new Integer(i));
		}
		this.simMatrix = simMatrix;
	}

	// simple getter
	public float[][] getSimMatrix() {
		return simMatrix;
	}

	// simple setter
	public void setSimMatrix(float[][] simMatrix) {
		this.simMatrix = simMatrix;
	}

	// implemented abstract functions of parent class

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wdilab.coma.structure.MatchResult#getSrcMatchObjects()
	 */
	@Override
	public ArrayList<Object> getSrcMatchObjects() {
		if (simMatrix == null)
			return null;
		ArrayList<Object> srcMatchObjs = new ArrayList<Object>();
		for (int i = 0; i < simMatrix.length; i++) {
			for (int j = 0; j < simMatrix[0].length; j++) {
				if (simMatrix[i][j] > 0) {
					// add source object if at least one similarity larger than
					// 0
					srcMatchObjs.add(srcObjects.get(i));
					break;
				}
			}
		}
		if (srcMatchObjs.isEmpty())
			return null;
		return srcMatchObjs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wdilab.coma.structure.MatchResult#getSrcMatchObjects(java.lang.Object)
	 */
	@Override
	public ArrayList<Object> getSrcMatchObjects(Object trgObj) {
		if (trgObj == null)
			return null;
		int trgInd = indexOfTrgObjectIfExist(trgObj);
		ArrayList<Object> srcMatchObjs = new ArrayList<Object>();
		if (trgInd != -1) {
			for (int i = 0; i < srcObjects.size(); i++) {
				// add source object only if similarity to given target object
				// larger than 0
				if (simMatrix[i][trgInd] > 0)
					srcMatchObjs.add(srcObjects.get(i));
			}
		}
		if (srcMatchObjs.isEmpty())
			return null;
		return srcMatchObjs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wdilab.coma.structure.MatchResult#getTrgMatchObjects()
	 */
	@Override
	public ArrayList<Object> getTrgMatchObjects() {
		if (simMatrix == null || simMatrix.length == 0)
			return null;
		ArrayList<Object> trgMatchObjs = new ArrayList<Object>();
		for (int j = 0; j < simMatrix[0].length; j++) {
			for (int i = 0; i < simMatrix.length; i++) {
				if (simMatrix[i][j] > 0) {
					// add target object if at least one similarity larger than
					// 0
					trgMatchObjs.add(trgObjects.get(j));
					break;
				}
			}
		}
		if (trgMatchObjs.isEmpty())
			return null;
		return trgMatchObjs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wdilab.coma.structure.MatchResult#getTrgMatchObjects(java.lang.Object)
	 */
	@Override
	public ArrayList<Object> getTrgMatchObjects(Object srcObj) {
		if (srcObj == null)
			return null;
		int srcInd = indexOfSrcObjectIfExist(srcObj);
		ArrayList<Object> trgMatchObjs = new ArrayList<Object>();
		if (srcInd != -1) {
			for (int j = 0; j < trgObjects.size(); j++) {
				// add target object only if similarity to given source object
				// larger than 0
				if (simMatrix[srcInd][j] > 0)
					trgMatchObjs.add(trgObjects.get(j));
			}
		}
		if (trgMatchObjs.isEmpty())
			return null;
		return trgMatchObjs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wdilab.coma.structure.MatchResult#getMatchCount()
	 */
	@Override
	public int getMatchCount() {
		int matchCnt = 0;
		if (simMatrix == null)
			return matchCnt;
		for (int i = 0; i < simMatrix.length; i++) {
			for (int j = 0; j < simMatrix[0].length; j++) {
				if (simMatrix[i][j] > 0) {
					// count each pair that has a similarity larger than 0
					matchCnt++;
				}
			}
		}
		return matchCnt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wdilab.coma.structure.MatchResult#getSimilarity(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public float getSimilarity(Object srcObj, Object trgObj) {
		if (srcObj == null || trgObj == null)
			return SIM_UNDEF;
		float sim = SIM_UNDEF;
		int srcInd = indexOfSrcObjectIfExist(srcObj);
		int trgInd = indexOfTrgObjectIfExist(trgObj);
		// only if both objects are in the source/target list there is a
		// similarity
		if (srcInd != -1 && trgInd != -1)
			sim = simMatrix[srcInd][trgInd];
		return sim;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wdilab.coma.structure.MatchResult#append(java.lang.Object,
	 * java.lang.Object, float)
	 */
	@Override
	public void append(Object srcObj, Object trgObj, float sim) {
		if (srcObj == null || trgObj == null || sim==0)
			return;
		int srcOldCnt = srcObjects.size();
		int trgOldCnt = trgObjects.size();
		int srcInd = indexOfSrcObjectIfExist(srcObj);
		int trgInd = indexOfTrgObjectIfExist(trgObj);

		if (srcInd != -1 && trgInd != -1) { // be careful, sim might be 0!!!
			if (simMatrix[srcInd][trgInd] < sim // max strategy
					|| sim>1 // exception: merge similarity are for a workaround larger 1
					) 
				simMatrix[srcInd][trgInd] = sim;
		} else {
			if (srcInd == -1) {
				srcObjects.add(srcObj);
				srcInd = srcObjects.size() - 1;
				if (hashing)
					srcObjectIndex.put(srcObj, new Integer(srcInd));
			}
			if (trgInd == -1) {
				trgObjects.add(trgObj);
				trgInd = trgObjects.size() - 1;
				if (hashing)
					trgObjectIndex.put(trgObj, new Integer(trgInd));
			}
			int aNewCnt = srcObjects.size();
			int bNewCnt = trgObjects.size();
			float[][] newSimMatrix = new float[aNewCnt][bNewCnt];
			for (int i = 0; i < aNewCnt; i++) {
				for (int j = 0; j < bNewCnt; j++) {
					if (i < srcOldCnt && j < trgOldCnt)
						newSimMatrix[i][j] = simMatrix[i][j];
					else
						newSimMatrix[i][j] = SIM_MIN; // SIM_UNDEF or SIM_MIN 
				}
			}
			newSimMatrix[srcInd][trgInd] = sim;
			simMatrix = newSimMatrix;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.wdilab.coma.structure.MatchResult#setSimilarity(java.lang.Object, java.lang.Object, float)
	 */
	public void setSimilarity(Object srcObj, Object trgObj, float sim) {
		if (srcObj == null || trgObj == null)
			return;
		int srcInd = indexOfSrcObjectIfExist(srcObj);
		int trgInd = indexOfTrgObjectIfExist(trgObj);

		if (srcInd != -1 && trgInd != -1) { // be careful, sim might be 0!!!
				simMatrix[srcInd][trgInd] = sim;
		} 
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.wdilab.coma.structure.MatchResult#clone()
	 */
	@Override
	public MatchResult clone() {
		float[][] simMatrixNew = null;
		if (simMatrix!=null){
			int m = simMatrix.length;
			if (m == 0 || simMatrix[0] == null) {
				return null;
			}
			int n = simMatrix[0].length;

			// Make a new copy of input simMatrix for selection
			simMatrixNew = new float[m][n];
			for (int i = 0; i < m; i++)
				System.arraycopy(simMatrix[i], 0, simMatrixNew[i], 0, n);
		}

		MatchResultArray resultNew = new MatchResultArray(srcObjects,
				trgObjects, simMatrixNew);
		return resultNew;
	}

	// functions
	/**
	 * Returns the index of a target object in the target object list
	 * @param trgObj target object
	 * @return index of target object
	 * returns -1 if object is not in the list
	 */
	int indexOfTrgObjectIfExist(Object trgObj) {
		if (hashing) {
			Integer ind = trgObjectIndex.get(trgObj);
			if (ind == null)
				return -1; // target object is not in the list
			else return ind.intValue();
		} else return trgObjects.indexOf(trgObj);
	}

	/**
	 * Returns the index of a source object in the source object list
	 * - it adds the object if necessary
	 * @param trgObj source object
	 * @return index of source object
	 * returns -1 if object is not in the list
	 */
	int indexOfTrgObject(Object trgObj) {
		if (hashing) {
			Integer ind = trgObjectIndex.get(trgObj);
			if (ind == null){
				// target object is not in the list -> add it now
				trgObjects.add(trgObj);
				 ind = new Integer(trgObjects.size()-1);
				 trgObjectIndex.put(trgObj, ind);
			} 
			return ind.intValue();
		} else{
			int ind =  trgObjects.indexOf(trgObj);
			if (ind == -1){
				trgObjects.add(trgObj);
				return trgObjects.indexOf(trgObj);
			} else return ind;
		}
	}
	
	/**
	 * Returns the index of a source object in the source object list
	 * @param srcObj source object
	 * @return index of source object
	 * returns -1 if object is not in the list
	 */
	int indexOfSrcObjectIfExist(Object srcObj) {
		if (hashing) {
			Integer ind = srcObjectIndex.get(srcObj);
			if (ind == null)
				return -1; // source object is not in the list
			else return ind.intValue();
		} else return srcObjects.indexOf(srcObj);
	}
	
	/**
	 * Returns the index of a source object in the source object list
	 * - it adds the object if necessary
	 * @param srcObj source object
	 * @return index of source object
	 * returns -1 if object is not in the list
	 */
	int indexOfSrcObject(Object srcObj) {
		if (hashing) {			
			Integer ind = srcObjectIndex.get(srcObj);
			if (ind == null){
				// source object is not in the list -> add it now
				 srcObjects.add(srcObj);
				 ind = new Integer(srcObjects.size()-1);
				 srcObjectIndex.put(srcObj, ind);
			}
			return ind.intValue();
		} else{
			int ind =  srcObjects.indexOf(srcObj);
			if (ind == -1){
				srcObjects.add(srcObj);
				return srcObjects.indexOf(srcObj);
			} else return ind;
		}
	}
	
	
	/**
	 * @param result1
	 * @param result2
	 * @return the correspondences that appear in both match results if one of
	 *         the result is null return null if the result have different
	 *         graphs return null
	 */
	public static MatchResultArray intersect(MatchResultArray result1,
			MatchResultArray result2) {
		if (result1 == null || result2 == null)
			return null;

		if (!sameGraphs(result1, result2)) {
			return null;
		}

		MatchResultArray interResult = new MatchResultArray();
		ArrayList<Object> srcObjects = result1.getSrcObjects();
		ArrayList<Object> trgObjects = result1.getTrgObjects();
		float[][] simMatrix = result1.getSimMatrix();
		for (int i = 0; i < srcObjects.size(); i++) {
			Object srcObj = srcObjects.get(i);
			for (int j = 0; j < trgObjects.size(); j++) {
				if (simMatrix[i][j] > 0) {
					Object trgObj = trgObjects.get(j);
					if (result2.getSimilarity(srcObj, trgObj) > 0)
						interResult.append(srcObj, trgObj, simMatrix[i][j]);
				}
			}
		}
		if (interResult.getSrcObjects().isEmpty()
				|| interResult.getTrgObjects().isEmpty())
			return null;
		String matchInfo = OP_INTERSECT + " " + result1.getName() + " - "
				+ result2.getName();
		interResult.setMatchInfo(matchInfo);
		interResult.setGraphs(result1.getSourceGraph(),
				result1.getTargetGraph());
		return interResult;
	}

	/**
	 * @param result1
	 * @param result2
	 * @return the correspondences that appear in result1 but not in result2 if
	 *         one of the result is null return null if the result have
	 *         different graphs return null
	 */
	public static MatchResultArray diff(MatchResultArray result1,
			MatchResultArray result2) {
		if (result1 == null)
			return null;
		else if (result2 == null)
			return result1;

		if (!sameGraphs(result1, result2)) {
			return null;
		}

		MatchResultArray diffResult = new MatchResultArray();
		ArrayList<Object> srcObjects = result1.getSrcObjects();
		ArrayList<Object> trgObjects = result1.getTrgObjects();
		float[][] simMatrix = result1.getSimMatrix();
		for (int i = 0; i < srcObjects.size(); i++) {
			Object aObj = srcObjects.get(i);
			for (int j = 0; j < trgObjects.size(); j++) {
				Object bObj = trgObjects.get(j);
				if (simMatrix[i][j] > 0
						&& result2.getSimilarity(aObj, bObj) <= 0) {
					diffResult.append(aObj, bObj, simMatrix[i][j]);
				}
			}
		}
		if (diffResult.getSrcObjects().isEmpty()
				|| diffResult.getTrgObjects().isEmpty())
			return null;
		diffResult.setName(operationToString(OP_DIFF));
		diffResult.setMatchInfo(result1.getName() + "|" + result2.getName());
		diffResult
				.setGraphs(result1.getSourceGraph(), result1.getTargetGraph());
		return diffResult;
	}

	/**
	 * add a complete match result to another match result
	 * 
	 * @param result1
	 * @param result2
	 * @return merged match result returns null if the match result don't have
	 *         the same graphs
	 */
	public static MatchResultArray merge(MatchResultArray result1,
			MatchResultArray result2) {
		if (result1 == null)
			return result2;
		else if (result2 == null)
			return result1;
		if (!sameGraphs(result1, result2)) {
			return null;
		}
		MatchResultArray mergeResult = (MatchResultArray) result1.clone();

		// // version 1
		// MatchResultArray diff = diff(result2, result1);
		// if (diff==null) return result1;
		// ArrayList<Object> diffAObjects = diff.getSrcObjects();
		// ArrayList<Object> diffBObjects = diff.getTrgObjects();
		// float[][] diffSimMatrix = diff.getSimMatrix();
		// for (int i=0; i<diffAObjects.size(); i++) {
		// for (int j=0; j<diffBObjects.size(); j++) {
		// if (diffSimMatrix[i][j]>0)
		// mergeResult.append(diffAObjects.get(i), diffBObjects.get(j),
		// diffSimMatrix[i][j]);
		// }
		// }

		// version 2
		ArrayList<Object> addSrcObjects = result2.getSrcObjects();
		ArrayList<Object> addTrgObjects = result2.getTrgObjects();
		float[][] addSimMatrix = result2.getSimMatrix();
		if (addSrcObjects == null || addTrgObjects == null
				|| addSimMatrix == null)
			return mergeResult;
		int addACnt = addSrcObjects.size();
		int addBCnt = addTrgObjects.size();
		for (int i = 0; i < addACnt; i++) {
			Object srcObj = addSrcObjects.get(i);
			for (int j = 0; j < addBCnt; j++) {
				Object trgObj = addTrgObjects.get(j);
				if (addSimMatrix[i][j] > 0) {
					mergeResult.append(srcObj, trgObj, addSimMatrix[i][j]);
				}
			}
		}

		mergeResult.setName(operationToString(OP_MERGE));
		mergeResult.setMatchInfo(result1.getName() + "|" + result2.getName());
		mergeResult.setGraphs(result1.getSourceGraph(),
				result1.getTargetGraph());
		return mergeResult;
	}

	/**
	 * compose two matchresults with an overlapping graph to create a new
	 * matchresult
	 * 
	 * @param leftResult
	 * @param rightResult
	 * @return matchresult return null if either of the matchresults is null or
	 *         don't have a same graph
	 */
	public static MatchResultArray compose(MatchResultArray leftResult,
			MatchResultArray rightResult, int composition) {
		if (leftResult == null || rightResult == null)
			return null;
		boolean verbose = false;
		if (verbose) {
			System.out.println("Compose mappings: ");
			System.out.println("LeftMapping: "
					+ leftResult.getSourceGraph().getSource() + "<->"
					+ leftResult.getTargetGraph().getSource());
			System.out.println("RightMapping: "
					+ rightResult.getSourceGraph().getSource() + "<->"
					+ rightResult.getTargetGraph().getSource());
		}
		MatchResultArray compResult = new MatchResultArray();
		compResult.setName(operationToString(OP_COMPOSE));
		compResult.setMatchInfo(leftResult.getName() + "|"
				+ rightResult.getName());
		compResult.setGraphs(leftResult.getSourceGraph(),
				rightResult.getTargetGraph());

		// Left correspondences to drive composition
		ArrayList<Object> leftSrcObjects = leftResult.getSrcObjects();
		ArrayList<Object> leftTrgObjects = leftResult.getTrgObjects();
		float[][] leftSimMatrix = leftResult.getSimMatrix();
		// ArrayList rightSrcObjects = rightResult.getTrgObjects();
		ArrayList<Object> rightTrgObjects = rightResult.getTrgObjects();
		float[][] rightSimMatrix = rightResult.getSimMatrix();
		for (int i = 0; i < leftSrcObjects.size(); i++) {
			Object leftSrcObj = leftSrcObjects.get(i);
			for (int j = 0; j < leftTrgObjects.size(); j++) {
				Object leftTrgObj = leftTrgObjects.get(j);
				if (leftSimMatrix[i][j] > 0) { // its a correspondence
					if (verbose) {
						System.out.print("Left correspondence: ");
						if (leftSrcObj instanceof Element)
							System.out.print(((Element) leftSrcObj).getName());
						else if (leftSrcObj instanceof Path)
							System.out
									.print(((Path) leftSrcObj).toNameString());
						else
							System.out.print(leftSrcObj);
						System.out.print(" <-> ");
						if (leftTrgObj instanceof Element)
							System.out.println(((Element) leftTrgObj)
									.getName());
						else if (leftTrgObj instanceof Path)
							System.out.println(((Path) leftTrgObj)
									.toNameString());
						else
							System.out.println(leftTrgObj);
					}

					// int m = rightSrcObjects.indexOf(leftTrgObj);
					int m = rightResult.indexOfSrcObjectIfExist(leftTrgObj);
					if (m != -1) {
						for (int n = 0; n < rightTrgObjects.size(); n++) {
							Object rightBObj = rightTrgObjects.get(n);
							if (rightSimMatrix[m][n] > 0) {
								if (verbose) {
									System.out
											.print(" - Right matching object: ");
									if (rightBObj instanceof Element)
										System.out
												.println(((Element) rightBObj)
														.getName());
									else if (rightBObj instanceof Path)
										System.out.println(((Path) rightBObj)
												.toNameString());
									else
										System.out.println(rightBObj);
								}

								// compose similarities
								float sim = 0;
								 switch (composition) {
								 case COMPOSITION_MAX:
								 sim =
								 (leftSimMatrix[i][j]>rightSimMatrix[m][n])?leftSimMatrix[i][j]:rightSimMatrix[m][n];
								 break;
								 case COMPOSITION_MIN:
								 sim =
								 (leftSimMatrix[i][j]<rightSimMatrix[m][n])?leftSimMatrix[i][j]:rightSimMatrix[m][n];
								 break;
								 case COMPOSITION_SUM:
									 sim = (leftSimMatrix[i][j] + rightSimMatrix[m][n]);
									 break;
								 default: //COM_COMP_SIMAVERAGE
								sim = (leftSimMatrix[i][j] + rightSimMatrix[m][n]) / 2;
								 break;
								 }
								compResult.append(leftSrcObj, rightBObj, sim);
							}
						}
					}
				}
			}
		}
		if (verbose)
			System.out.println("Compose Mappings: DONE!");
		if (compResult.getSrcObjects().isEmpty()
				|| compResult.getTrgObjects().isEmpty())
			return null;
		return compResult;
	}

	/**
	 * @param result
	 * @return transposed match result -> source and target objects switched and
	 *         similarity reversed as well - optimized for matchresult with
	 *         array
	 */
	public static MatchResultArray transpose(MatchResultArray matchResult) {
		if (matchResult == null)
			return null;
		ArrayList<Object> aObjects = matchResult.getSrcObjects();
		ArrayList<Object> bObjects = matchResult.getTrgObjects();
		float[][] simMatrix = matchResult.getSimMatrix();
		float[][] transSimMatrix = transpose(simMatrix);

		MatchResultArray transResult = new MatchResultArray(bObjects, aObjects,
				transSimMatrix);
		transResult.setName(MatchResult.operationToString(OP_TRANSPOSE));
		transResult.setMatchInfo(matchResult.getName());
		transResult.setGraphs(matchResult.getTargetGraph(),
				matchResult.getSourceGraph());
		// // TODO: transpose UserObject as well
		// transResult.setUserObject(matchResult.getUserObject());
		return transResult;
	}

	/**
	 * @param result
	 * @return transposed similarity matrix
	 */
	static float[][] transpose(float[][] simMatrix) {
		if (simMatrix == null)
			return null;
		int m = simMatrix.length;
		int n = simMatrix[0].length;
		float[][] transMatrix = new float[n][m];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				transMatrix[j][i] = simMatrix[i][j];
		return transMatrix;
	}

	/**
	 * compare this mapping (the intended mapping) with another mapping (the
	 * test mapping)
	 * 
	 * @param testResult
	 * @return evaluation measures e.g. precision, recall, f-measure
	 */
	@Override
	public EvaluationMeasure compare(MatchResult testResult) {
		if (testResult instanceof MatchResultArray) {
			return compare((MatchResultArray) testResult);
		} else {
			return super.compare(testResult);
		}
	}

	/**
	 * - optimized version for array compare this mapping (the intended mapping)
	 * with another mapping (the test mapping)
	 * 
	 * @param testResult
	 * @return evaluation measures e.g. precision, recall, f-measure
	 */
	private EvaluationMeasure compare(MatchResultArray testResult) {
		if (testResult == null)
			return null;
		int I = 0; // # intended correspondences
		int T = 0; // # test correspondences
		int M = 0; // # true positives (correct matches in test)
		for (int i = 0; i < srcObjects.size(); i++) {
			Object srcObj = srcObjects.get(i);
			for (int j = 0; j < trgObjects.size(); j++) {
				if (simMatrix[i][j] > 0) {
					Object trgObj = trgObjects.get(j);
					I++; // an intended match
					if (testResult.getSimilarity(srcObj, trgObj) > 0)
						M++; // true positive, also found in test
				}
			}
		}
		// match found in testResult
		T = testResult.getMatchCount();
		EvaluationMeasure measure = new EvaluationMeasure(I, T, M);
		return measure;
	}

	/* (non-Javadoc)
	 * @see de.wdilab.coma.structure.MatchResult#remove(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void remove(Object srcObj, Object trgObj) {
		if (srcObj == null || trgObj == null)
			return;
		int srcInd = indexOfSrcObjectIfExist(srcObj);
		int trgInd = indexOfTrgObjectIfExist(trgObj);

		if (srcInd != -1 && trgInd != -1) {
				simMatrix[srcInd][trgInd] = 0;
		}
	}
	
	//Extract a portion of current result for the given a- and b-objects
	public static MatchResultArray restrict(MatchResultArray result,
//			ArrayList<Object> srcObjects, ArrayList<Object> trgObjects) {
		ArrayList<Object> resAObjects, ArrayList<Object> resBObjects) {
	
		    if (resAObjects==null && resBObjects==null){
		    	return (MatchResultArray) result.clone();
		    }
		    boolean domainAll=false, rangeAll=false;
		    if (resAObjects==null) {
		      resAObjects = result.getSrcObjects();
		      domainAll = true;
		    }
		    if (resBObjects==null) {
		      resBObjects = result.getTrgObjects();
		      rangeAll = true;
		    }

		    int resACnt = resAObjects.size();
		    int resBCnt = resBObjects.size();
		    float[][] resSimMatrix = new float[resACnt][resBCnt];

		    float[][] simMatrix =  result.getSimMatrix();
		    //Use hash index
		    for (int i=0; i<resACnt; i++) {
		      Object aObj = resAObjects.get(i);
		      int aInd = -1;
		      if (domainAll) aInd = i;
		      else aInd = result.indexOfSrcObjectIfExist(aObj);
		      for (int j=0; j<resBCnt; j++) {
		        Object bObj = resBObjects.get(j);
		        int bInd = -1;
		        if (rangeAll) bInd = j;
		        else bInd = result.indexOfTrgObjectIfExist(bObj);
		        if (aInd!=-1 && bInd!=-1)
		          resSimMatrix[i][j] = simMatrix[aInd][bInd];
		      }
		    }

		    MatchResultArray subResult = new MatchResultArray(resAObjects, resBObjects, resSimMatrix);
//		    subResult.setMatchInfo(getMatchInfo());
//		    subResult.setResultName(getResultName());
//		    subResult.setMatcherName(matcherName);
//		    subResult.setMatcherConfig(matcherConfig);
		    subResult.setName(result.getName());
		    subResult.setMatchInfo(result.getMatchInfo());		    
		    subResult.setGraphs(result.getSourceGraph(), result.getTargetGraph());
		    return subResult;
	}
	
	  //Trim the non-matched objects
	  public MatchResult trim() {
	    boolean[] aMatched = new boolean[srcObjects.size()];
	    int[] aMatchedInd = new int[srcObjects.size()];
	    boolean[] bMatched = new boolean[trgObjects.size()];
	    int[] bMatchedInd = new int[trgObjects.size()];

	    for (int i=0; i<aMatched.length; i++) aMatched[i] = false;
	    for (int i=0; i<aMatchedInd.length; i++) aMatchedInd[i] = -1;
	    for (int i=0; i<bMatched.length; i++) bMatched[i] = false;
	    for (int i=0; i<bMatchedInd.length; i++) bMatchedInd[i] = -1;
	    int aCnt=0, bCnt=0;

	    //Determine matched objects and index mappings
	    for (int i=0; i<aMatched.length; i++) {
	      for (int j=0; j<bMatched.length; j++) {
	        if (simMatrix[i][j]>0) {
	          aMatched[i] = true;  //ok, to be taken in the new matrix
	          aMatchedInd[i] = aCnt; //index in the new matrix
	          aCnt++;
	          break;
	        }
	      }
	    }
	    for (int i=0; i<bMatched.length; i++) {
	      for (int j=0; j<aMatched.length; j++) {
	        if (simMatrix[j][i]>0) {
	          bMatched[i] = true;  //ok, to be taken in the new matrix
	          bMatchedInd[i] = bCnt; //index in new matrix
	          bCnt++;
	          break;
	        }
	      }
	    }

	    ArrayList matchedAObjects = new ArrayList(aCnt);
	    ArrayList matchedBObjects = new ArrayList(bCnt);
	    float[][] matchedSimMatrix = new float[aCnt][bCnt];
	    for (int i=0; i<aMatched.length; i++) {
	      if (aMatched[i]) matchedAObjects.add(srcObjects.get(i));
	    }
	    for (int i=0; i<bMatched.length; i++) {
	      if (bMatched[i]) matchedBObjects.add(trgObjects.get(i));
	    }

	    for (int i=0; i<aMatched.length; i++) {
	      if (aMatched[i]) {
	        for (int j=0; j<bMatched.length; j++) {
	          if (bMatched[j]) {
	            Object a1 = srcObjects.get(i);
	            Object a2 = matchedAObjects.get(aMatchedInd[i]);
	            if (! a1.equals(a2)) System.out.println("Error mapping source obj: " + i + ":" + a1 + "->" + aMatchedInd[i] + ":" + a2);
	            Object b1 = trgObjects.get(j);
	            Object b2 = matchedBObjects.get(bMatchedInd[j]);
	            if (! b1.equals(b2)) System.out.println("Error mapping target obj: " + j + ":" + b1 + "->" + bMatchedInd[j] + ":" + b2);
	            matchedSimMatrix[aMatchedInd[i]][bMatchedInd[j]] = simMatrix[i][j];
	          }
	        }
	      }
	    }
	    MatchResultArray trimResult = new MatchResultArray(matchedAObjects, matchedBObjects, matchedSimMatrix);
	    trimResult.setMatchInfo(getMatchInfo());
	    trimResult.setName(getName());
//	    trimResult.setMatcherName(matcherName);
//	    trimResult.setEvidence(getEvidence());
//	    trimResult.setMatcherConfig(matcherConfig);
	    trimResult.setGraphs(sourceGraph, targetGraph);
	    trimResult.setUserObject(getUserObject());
	    return trimResult;
	  }
	  
	  //add a complete result to the current result
	  public void append(MatchResultArray addResult) {
	    if (addResult==null) return;

	    ArrayList addAObjects = addResult.getSrcObjects();
	    ArrayList addBObjects = addResult.getTrgObjects();
	    float[][] addSimMatrix = addResult.getSimMatrix();
	    if (addAObjects==null || addBObjects==null || addSimMatrix==null) return;
	    int addACnt = addAObjects.size();
	    int addBCnt = addBObjects.size();
	    for (int i=0; i<addACnt; i++) {
	      Object aObj = addAObjects.get(i);
	      for (int j=0; j<addBCnt; j++) {
	        Object bObj = addBObjects.get(j);
	        if (addSimMatrix[i][j]!=SIM_UNDEF) {
	          append(aObj, bObj, addSimMatrix[i][j]);
	        }
	      }
	    }
	  }
	  
	  /**
	   * @return number of correspondences of all sim/ sim=1
	   */
	  public int[] getMatchCountAll() {
		  // optimized for array
		  int matchCnt = 0;
		  int matchCnt1 = 0;
		  for (int i = 0; i < srcObjects.size(); i++) {
			  for (int j = 0; j < trgObjects.size(); j++) {
				  float sim = 	simMatrix[i][j];
				  if (sim > 0) {
					  matchCnt ++;
					  if (sim==1){
						  matchCnt1 ++;
					  }
				  }
			  }
		  }
		  int[] matchC = new int[2];
		  matchC[0]= matchCnt;
		  matchC[1]= matchCnt1;
		  return matchC;
	  }
	  
		/* (non-Javadoc)
		 * @see de.wdilab.coma.structure.MatchResult#divideBy(int)
		 */
		public void divideBy(int divide){
			for (int i = 0; i < srcObjects.size(); i++) {
				for (int j = 0; j < trgObjects.size(); j++) {
					float sim = 	simMatrix[i][j];
					if (sim > 0) {
						simMatrix[i][j] = sim/divide;
					}
				}
			}
		}

}
