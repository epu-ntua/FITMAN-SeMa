package de.wdilab.ml.impl.matcher.simple.numeric;

import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import org.apache.log4j.Logger;

/**
 * @author Tobias Groeger
 * 
 */
public class NumericDistanceMatcher extends
AbstractSimpleAttributeObjectMatcher implements
		IAttributeObjectMatcher {
	protected static final Logger log = Logger
			.getLogger(NumericDistanceMatcher.class);

	public NumericDistanceMatcher() {
		super();
	}

	/**
	 * @param attr1
	 */
	public NumericDistanceMatcher(final String attr1) {
		super(attr1);
	}

	/**
	 * @param attr1
	 * @param threshold
	 */
	public NumericDistanceMatcher(final String attr1, final float threshold) {
		super(attr1, threshold);
	}

	/**
	 * @param attr1
	 * @param attr2
	 */
	public NumericDistanceMatcher(final String attr1, final String attr2) {
		super(attr1, attr2, 0f);
	}

	/**
	 * @param attr1
	 * @param attr2
	 * @param threshold
	 */
	public NumericDistanceMatcher(final String attr1, final String attr2, final float threshold) {
		super(attr1, attr2, threshold);
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
			throws MappingStoreException

	{
		for (final IObjectInstance oiLinks : oip1) {
			
			//System.out.println(attrLinks);
			//System.out.println(oiLinks.toString());
			//System.out.println(oiLinks.getValue(attrLinks));

			
			final double p1 = Double.parseDouble(oiLinks.getValue(attrLinks).toString());
			
			

			for (final IObjectInstance oiRechts : oip2) {
				final double p2 = Double.parseDouble(oiRechts.getValue(attrRechts).toString());

				final double sim = 1 - (2 * Math.abs(p1 - p2) / (p1 + p2));

				if (sim >= threshold)
					mrs.add(oiLinks, oiRechts, new Similarity(sim));
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
	public void match(final IObjectInstanceProvider oip, final IMappingStore mrs)
			throws MappingStoreException {
		for (IObjectInstance oi1 : oip) {
			double number1 = Double.parseDouble(oi1.getValue(attrLinks).toString());
			for(IObjectInstance oi2 : oip) {
				if (oi1.getId().equals(oi2.getId() )) {
					continue;
				}
				double number2 = Double.parseDouble(oi2.getValue(attrRechts).toString());
				double sim = 1 - (2 * Math.abs(number1 - number2) / (number1 + number2));

				if (sim >= threshold) {
					mrs.add(oi1, oi2, new Similarity(sim));
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
	public void match(final IMappingProvider mp, final IMappingStore mrs)
			throws MappingStoreException {

		for (IMappingEntry me : mp) {
			double number1 = Double.parseDouble(me.getLeft().getValue(attrLinks).toString());
			double number2 = Double.parseDouble(me.getRight().getValue(attrRechts).toString());
			double sim = 1 - (2 * Math.abs(number1 - number2) / (number1 + number2));

			if (sim >= threshold) {
				mrs.add(me.getLeft(), me.getRight(), new Similarity(sim));
			}
		}
	}
	
}