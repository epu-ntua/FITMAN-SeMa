package de.wdilab.ml.impl.matcher.simple.numeric;

import org.apache.log4j.Logger;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * @author Tobias Groeger
 * 
 */
public class NumericAbsoluteDistanceMatcher extends AbstractSimpleAttributeObjectMatcher {

	protected static final Logger log = Logger.getLogger(NumericAbsoluteDistanceMatcher.class);

	double absoluteDistance = 0.5;

	public NumericAbsoluteDistanceMatcher() {
		super();
	}

	/**
	 * Constructor to also set the abs. distance.
	 * 
	 * @param absoluteDistance
	 */
	public NumericAbsoluteDistanceMatcher(double absoluteDistance) {
		super();
		this.absoluteDistance = absoluteDistance;
	}

	/**
	 * @param attr1
	 */
	public NumericAbsoluteDistanceMatcher(final String attr1) {
		super(attr1);
	}

	/**
	 * @param attr1
	 * @param absoluteDistance
	 */
	public NumericAbsoluteDistanceMatcher(final String attr1, double absoluteDistance) {
		super(attr1);
		this.absoluteDistance = absoluteDistance;
	}

	/**
	 * @param attr1
	 * @param threshold
	 */
	public NumericAbsoluteDistanceMatcher(final String attr1, final float threshold) {
		super(attr1, threshold);
	}

	/**
	 * @param attr1
	 * @param threshold
	 * @param absoluteDistance
	 */
	public NumericAbsoluteDistanceMatcher(final String attr1, final float threshold, double absoluteDistance) {
		super(attr1, threshold);
		this.absoluteDistance = absoluteDistance;
	}

	/**
	 * @param attr1
	 * @param attr2
	 */
	public NumericAbsoluteDistanceMatcher(final String attr1, final String attr2) {
		super(attr1, attr2, 0f);
	}

	/**
	 * @param attr1
	 * @param attr2
	 * @param absoluteDistance
	 */
	public NumericAbsoluteDistanceMatcher(final String attr1, final String attr2, double absoluteDistance) {
		super(attr1, attr2, 0f);
		this.absoluteDistance = absoluteDistance;
	}

	/**
	 * @param attr1
	 * @param attr2
	 * @param threshold
	 */
	public NumericAbsoluteDistanceMatcher(final String attr1, final String attr2, final float threshold) {
		super(attr1, attr2, threshold);
	}

	/**
	 * @param attr1
	 * @param attr2
	 * @param threshold
	 * @param absoluteDistance
	 */
	public NumericAbsoluteDistanceMatcher(final String attr1, final String attr2, final float threshold, double absoluteDistance) {
		super(attr1, attr2, threshold);
		this.absoluteDistance = absoluteDistance;
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
	public void match(final IObjectInstanceProvider oip1, final IObjectInstanceProvider oip2, final IMappingStore mrs) throws MappingStoreException

	{
//		for (final IObjectInstance oiLinks : oip1) {
//
//			final double p1 = Double.parseDouble(oiLinks.getValue(attrLinks).toString());
//
//			for (final IObjectInstance oiRechts : oip2) {
//				final double p2 = Double.parseDouble(oiRechts.getValue(attrRechts).toString());
//
//				double sim = 0.0;
//
//				if (Math.abs(p1 - p2) <= absoluteDistance)
//					sim = 1.0;
//
//				if (sim >= threshold)
//					mrs.add(oiLinks, oiRechts, new Similarity(sim));
//			}
//		}
		ISimilarity sim;
		for (IObjectInstance oi1 : oip1) {
			for (IObjectInstance oi2 : oip2) {
				if (oi1.getId().equals(oi2.getId()) ) {
					continue;
				}
				else {
					sim = getSimilarity(oi1, oi2);
					if (sim.getSim() <= threshold) {
						continue;
					}
					if (sim.getSim() < 0 ) {
						mrs.add(oi1, oi2, new Similarity(0.0 ) );
					}
					else if (sim.getSim() > 1) {
						mrs.add(oi1, oi2, new Similarity(1.0 ) );
					}
					else {
						mrs.add(oi1, oi2, sim );
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
	 * .oi. IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException {
		ISimilarity sim;
		for (IObjectInstance oi1 : oip) {
			for (IObjectInstance oi2 : oip) {
				if (oi1.getId().equals(oi2.getId()) ) {
					continue;
				}
				else {
					sim = getSimilarity(oi1, oi2);
					if (sim.getSim() < threshold) {
						continue;
					}
					if (sim.getSim() < 0 ) {
						mrs.add(oi1, oi2, new Similarity(0.0 ) );
					}
					else if (sim.getSim() > 1) {
						mrs.add(oi1, oi2, new Similarity(1.0 ) );
					}
					else {
						mrs.add(oi1, oi2, sim );
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.
	 * interfaces.mapping. IMappingProvider,
	 * de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException {
		ISimilarity sim;
		for (IMappingEntry correspondence : mp) {
			sim = getSimilarity(correspondence.getLeft(), correspondence.getRight() );
			if (sim.getSim() <= threshold) {
				continue;
			}
			if (sim.getSim() < 0 ) {
				mrs.add(correspondence.getLeft(), correspondence.getRight(), new Similarity(0.0 ) );
			}
			else if (sim.getSim() > 1) {
				mrs.add(correspondence.getLeft(), correspondence.getRight(), new Similarity(1.0 ) );
			}
			else {
				mrs.add(correspondence.getLeft(), correspondence.getRight(), sim );
			}
		}
	}
	
	/**
	 * @param oi1
	 * @param oi2
	 * @return
	 */
	// no mixed types or weird date formats, only Long,Float, parseable String,Double
	private ISimilarity getSimilarity(IObjectInstance oi1, IObjectInstance oi2) {
		
		Object value1  = oi1.getValue(attrLinks);
		Object value2  = oi2.getValue(attrRechts);
		Double number1;
		Double number2;
		
	   if (value1 instanceof Double && value2 instanceof Double) {
			number1 = (Double) value1;
			number2 = (Double) value2;
			if (Math.abs(number1 - number2) <= absoluteDistance ) return new Similarity(1d);
			else return new Similarity(0d);
		}
	   else if (value1 instanceof String && value2 instanceof String) {
			number1 = Double.parseDouble((String) value1);
			number2 = Double.parseDouble((String) value2);
			if (Math.abs(number1 - number2) <= absoluteDistance ) return new Similarity(1d);
			else return new Similarity(0d);
		}
		else if (value1 instanceof Float && value2 instanceof Float) {
			number1 = ((Double) value1).doubleValue();
			number2 = ((Double) value2).doubleValue();
			if (Math.abs(number1 - number2) <= absoluteDistance ) return new Similarity(1d);
			else return new Similarity(0d);
		}
		else if (value1 instanceof Long && value2 instanceof Long) {
			number1 = ((Long) value1).doubleValue();
			number2 = ((Long) value2).doubleValue();
			if (Math.abs(number1 - number2) <= absoluteDistance ) return new Similarity(1d);
			else return new Similarity(0d);
		}
		else {
			try {
				number1 = Double.parseDouble(value1.toString() );
				number2 = Double.parseDouble(value2.toString() );
				if (Math.abs(number1 - number2) <= absoluteDistance ) {
					return new Similarity(1d);
				}
				else {
					return new Similarity(0d);
				}
			}
			catch (Exception e) {
				throw new UnsupportedOperationException("implemet a way to handle this time format");
			}
		}
	}
}