package de.wdilab.ml.impl.matcher.simple.levenshtein;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.uni_leipzig.simba.cache.MemoryCache;
import de.uni_leipzig.simba.metricfactory.SimpleMetricFactory;
import de.uni_leipzig.simba.organizer.LimesOrganizer;
import de.uni_leipzig.simba.organizer.Organizer;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

public class LimesLevenshtein extends AbstractSimpleAttributeObjectMatcher
		implements IAttributeObjectMatcher {

	protected int exemplars = 0;

	public LimesLevenshtein(final String attr1, final String attr2,
			int exemplars) {
		super(attr1, attr2, 0f);
		this.exemplars = exemplars;
	}

	public LimesLevenshtein(final String attr, int exemplars) {
		super(attr, 0f);
		this.exemplars = exemplars;
	}

	public LimesLevenshtein(final int exemplars) {
		this.exemplars = exemplars;
	}

	public LimesLevenshtein() {
		super();
	}

	public LimesLevenshtein(final String attr1, final String attr2,
			final float threshold, int exemplars) {
		super(attr1, attr2, threshold);
		this.exemplars = exemplars;
	}

	public LimesLevenshtein(final String attr, final float threshold,
			int exemplars) {
		super(attr, threshold);
		this.exemplars = exemplars;
	}

	public void match(final IObjectInstanceProvider oip1,
			final IObjectInstanceProvider oip2, final IMappingStore mrs)
			throws MappingStoreException {

		MemoryCache source = new MemoryCache();

		for (final IObjectInstance oi : oip1) {

			String value = oi.getStringValue(attrLinks);

			if (value != null)
				// keine Sprachangabe
				if (!value.contains("@")) {
					source.addTriple(oi.getId(), attrLinks, value);
				} else {
					// falls Sprachangabe, dann Englisch
					if (value.contains("@en")) {
						value = value.substring(0, value.indexOf("@en"));
						source.addTriple(oi.getId(), attrLinks, value);
					}
				}
		}

		MemoryCache target = new MemoryCache();

		for (final IObjectInstance oi : oip2) {

			String value = oi.getStringValue(attrRechts);

			if (value != null)
				// keine Sprachangabe
				if (!value.contains("@")) {
					target.addTriple(oi.getId(), attrRechts, value);
				} else {
					// falls Sprachangabe, dann Englisch
					if (value.contains("@en")) {
						value = value.substring(0, value.indexOf("@en"));
						target.addTriple(oi.getId(), attrRechts, value);
					}
				}
		}

		String metricExpression = "levenshtein("
				+ oip1.getMetaData().getIdAttribute() + "." + attrLinks + ", "
				+ oip2.getMetaData().getIdAttribute() + "." + attrRechts + ")";

		SimpleMetricFactory mf = new SimpleMetricFactory();
		mf.setExpression(metricExpression);

		SimpleMetricFactory organizerMf = new SimpleMetricFactory();
		organizerMf.setExpression(mf.foldExpression(metricExpression,
				attrRechts, oip1.getMetaData().getIdAttribute()));

		this.setExemplars((int) Math.sqrt((double) target.size()));

		if (exemplars == 0) {
			this.setExemplars((int) Math.sqrt((double) target.size()));
		}
		Organizer organizer = new LimesOrganizer();

		if (exemplars < 2)
			organizer.computeExemplars(target, organizerMf);
		else
			organizer.computeExemplars(target, organizerMf, exemplars);

		ArrayList<String> uris = source.getAllUris();

		HashMap<String, Float> results;
		Iterator<String> resultIterator;
		String s;

		for (int i = 0; i < uris.size(); i++) {
			results = organizer.getSimilarInstances(
					source.getInstance(uris.get(i)), threshold, mf);

			resultIterator = results.keySet().iterator();

			while (resultIterator.hasNext()) {
				s = resultIterator.next();
				if (results.get(s) >= threshold) {
					// sehr langsam
					IObjectInstance oi1 = oip1.getInstance(uris.get(i));

					IObjectInstance oi2 = oip2.getInstance(s);

					mrs.add(oi1, oi2, new Similarity(results.get(s)));
//					System.out.println(oi1);
//					System.out.println(oi2);

				}
			}
		}

	}

	public void match(final IObjectInstanceProvider oip, final IMappingStore mrs)
			throws MappingStoreException {

		MemoryCache cache = new MemoryCache();

		for (final IObjectInstance oi : oip) {

			String value = oi.getStringValue(attrLinks);

			if (value != null)
				// keine Sprachangabe
				if (!value.contains("@")) {
					cache.addTriple(oi.getId(), attrLinks, value);
				} else {
					// falls Sprachangabe, dann Englisch
					// meine persoenliche meinung gehoert nicht hin, sondern in
					// die Anfrage
					if (value.contains("@en")) {
						value = value.substring(0, value.indexOf("@en"));
						cache.addTriple(oi.getId(), attrLinks, value);
					}
				}
		}

		String metricExpression = "levenshtein("
				+ oip.getMetaData().getIdAttribute() + "." + attrLinks + ", "
				+ oip.getMetaData().getIdAttribute() + "." + attrRechts + ")";

		SimpleMetricFactory mf = new SimpleMetricFactory();
		mf.setExpression(metricExpression);

		SimpleMetricFactory organizerMf = new SimpleMetricFactory();
		organizerMf.setExpression(mf.foldExpression(metricExpression,
				attrRechts, oip.getMetaData().getIdAttribute()));

		if (exemplars == 0) {
			this.setExemplars((int) Math.sqrt((double) cache.size()));
		}

		Organizer organizer = new LimesOrganizer();

		if (exemplars < 2)
			organizer.computeExemplars(cache, organizerMf);
		else
			organizer.computeExemplars(cache, organizerMf, exemplars);

		ArrayList<String> uris = cache.getAllUris();

		HashMap<String, Float> results;
		Iterator<String> resultIterator;
		String s;

		for (int i = 0; i < uris.size(); i++) {
			results = organizer.getSimilarInstances(
					cache.getInstance(uris.get(i)), threshold, mf);

			resultIterator = results.keySet().iterator();

			while (resultIterator.hasNext()) {
				s = resultIterator.next();
				// ist hier komisch gemacht --> nico fragen
				if (results.get(s) >= threshold && !uris.get(i).equals(s)) {

					IObjectInstance oi1 = oip.getInstance(uris.get(i));

					IObjectInstance oi2 = oip.getInstance(s);

					mrs.add(oi1, oi2, new Similarity(results.get(s)));

				}
			}
		}

	}

	public void match(final IMappingProvider mp, final IMappingStore mrs)
			throws MappingStoreException {

		throw new UnsupportedOperationException();

	}

	public void setExemplars(int exemplars) {
		this.exemplars = exemplars;
	}

	public int getExemplars() {
		return this.exemplars;
	}
}
