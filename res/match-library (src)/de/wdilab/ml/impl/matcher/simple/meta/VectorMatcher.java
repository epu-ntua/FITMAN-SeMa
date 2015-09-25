/**
 * Match Datatypes using a predefined similarity matrix
 * (based on FeatureVectorSimilarity from COMA++ 2008)
 * 
 * @author do, massmann
 */
package de.wdilab.ml.impl.matcher.simple.meta;

import org.apache.log4j.Logger;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

public class VectorMatcher extends AbstractSimpleAttributeObjectMatcher implements
		IAttributeObjectMatcher {

	protected static final Logger log = Logger.getLogger(VectorMatcher.class);
	
	/**
	 * @param attr1
	 * @param attr2
	 */
	public VectorMatcher(final String attr1, final String attr2) {
		super(attr1, attr2, 0f);
	}

	/**
	 * @param attr
	 */
	public VectorMatcher(final String attr) {
		super(attr, 0f);
	}

	/**
   * 
   */
	public VectorMatcher() {
		super();
	}

	/**
	 * @param attr1
	 * @param attr2
	 * @param threshold
	 */
	public VectorMatcher(final String attr1, final String attr2,
			final float threshold) {
		super(attr1, attr2, threshold);
	}

	/**
	 * @param attr
	 * @param threshold
	 */
	public VectorMatcher(final String attr, final float threshold) {
		super(attr, threshold);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
	 * .oi. IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(final IObjectInstanceProvider oip1,
			final IObjectInstanceProvider oip2, final IMappingStore mrs)
			throws MappingStoreException {

		log.info("Match Start.");

		for (final IObjectInstance oiLeft : oip1) {
			int[] valueL = (int[]) oiLeft.getValue(attrLinks);
			for (final IObjectInstance oiRight : oip2) {
				int[] valueR = (int[]) oiRight.getValue(attrRechts);
//				double sim = 0; 
//				if (valueL==null || valueR==null){ sim=0;
//				} else if (valueL.equals(valueR)){	sim=1;
//				}
				float sim = computeFeatureVectorSimilarity(valueL, valueR);
				mrs.add(oiLeft, oiRight, new Similarity(sim));
			}
		}
	}

	@Override
	public void match(final IObjectInstanceProvider oip, final IMappingStore mrs)
			throws MappingStoreException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void match(final IMappingProvider mp, final IMappingStore mrs)
			throws MappingStoreException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	private static double calculateDistance(int[] vect1, int[] vect2,
			double[] weight, boolean optimistic) {
		double result = -1;
		if (vect1.length != vect2.length)
			return result;
		else
			result = 0;

		for (int i = 0; i < vect1.length; i++) {
			// original euklid distance
			// double x = frag1[i] - frag2[i];

			// standardized => to get a result between 0 and 1 (optimistic)
			// or -1 and 1 (pessimistic)
			double x = standardize(vect1[i], vect2[i], optimistic);
			result += weight[i] * Math.pow(x, 2);
		}
		if (optimistic)
			result = 1 - Math.sqrt(result);
		else
			result = 1 - Math.sqrt(result);

		// result for optimistic between 0 (totally different) and 1 (the same)
		// result for pessimistic between -1 (totally different) and 1 (the
		// same)

		// happens only for pessimistic => we don't want negativ results
		if (result < 0)
			result = 0;

		return result;
	}

	private static double standardize(double a, double b, boolean optimistic) {
		if (a == b)
			return 0;
		double result = a + b;
		if (optimistic == true)
			// |a-b| / (a+b)=> optimistisch
			result = Math.abs(a - b) / result;
		else
			// oder 2|a-b| / (a+b) => pessimistisch
			result = 2 * Math.abs(a - b) / result;
		return result;
	}

	public static float computeFeatureVectorSimilarity(int[] v1, int[] v2,
			boolean optimistic) {
		double same = (double) 1 / v1.length;
		double[] allSameWeight = new double[v1.length];
		for (int i = 0; i < allSameWeight.length; i++)
			allSameWeight[i] = same;
		float sim = (float) calculateDistance(v1, v2, allSameWeight, optimistic);
		return sim;
	}

	public static float computeFeatureVectorSimilarity(int[] v1, int[] v2) {
		// boolean optimistic = true;
		boolean optimistic = false;
		double same = (double) 1 / v1.length;
		double[] allSameWeight = new double[v1.length];
		for (int i = 0; i < allSameWeight.length; i++)
			allSameWeight[i] = same;
		float sim = (float) calculateDistance(v1, v2, allSameWeight, optimistic);
		return sim;
	}

}
