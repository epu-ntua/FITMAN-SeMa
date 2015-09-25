package de.wdilab.ml.impl.matcher.simple.datetime;

import java.util.Date;

import de.wdilab.ml.impl.MappingEntry;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.AbstractObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * @author Christian Wartner
 * 
 */
public class DateDistance extends AbstractObjectMatcher implements IAttributeObjectMatcher {

	private float threshold = 0f;
	private String leftAttributeName;
	private String rightAttributeName;
	private int day = 0;
	private int month = 0;
	private int year = 0;
	private long maxDistance = 0;

	public DateDistance(int year, int month, int day) {
		setYear(year);
		setMonth(month);
		setDay(day);

		maxDistance = day * 24 * 60 * 60 * 1000 + month * 30 * 24 * 60 * 60 * 1000 + year * 12 * 30 * 24 * 60 * 60 * 1000;

	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getYear() {
		return year;
	}

	public void setMonth(int month) {
		assert (0 < month && month < 13) : "invalid month";
		this.month = month;
	}

	public int getMonth() {
		return month;
	}

	public void setDay(int day) {
		assert (0 < day && day < 32) : "invalid day";
		this.day = day;
	}

	public int getDay() {
		return day;
	}

	@Override
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	@Override
	public float getThreshold() {
		return threshold;
	}

	@Override
	public void match(IObjectInstanceProvider oip1, IObjectInstanceProvider oip2, IMappingStore mrs) throws MappingStoreException {
		for (IObjectInstance oiLeft : oip1) {
			for (IObjectInstance oiRight : oip2) {
				ISimilarity similarity = calculateSimilarity(oiLeft, oiRight);
				if (similarity.getSim() < threshold) {
					continue;
				}
				else if (similarity.getSim() <= 1) {
					mrs.add(new MappingEntry(oiLeft, oiRight, new Similarity(similarity.getSim())));
				}
				else {
					mrs.add(new MappingEntry(oiLeft, oiRight, new Similarity(0)));
				}
			}
		}
	}

	// no self matches
	@Override
	public void match(IObjectInstanceProvider oip, IMappingStore mrs) throws MappingStoreException {
		for (IObjectInstance oiLeft : oip) {
			for (IObjectInstance oiRight : oip) {
				ISimilarity similarity = calculateSimilarity(oiLeft, oiRight);
				if (oiLeft.getId().equals(oiRight.getId())) {
					continue;
				}
				else if (similarity.getSim() < threshold) {
					continue;
				}
				else if (similarity.getSim() <= 1) {
					mrs.add(new MappingEntry(oiLeft, oiRight, new Similarity(similarity.getSim())));
				}
				else {
					mrs.add(new MappingEntry(oiLeft, oiRight, new Similarity(0)));
				}
			}
		}
	}

	@Override
	public void match(IMappingProvider mp, IMappingStore mrs) throws MappingStoreException {
		for (IMappingEntry me : mp) {
			ISimilarity similarity = calculateSimilarity(me.getLeft(), me.getRight());
			if (similarity.getSim() < threshold) {
				continue;
			}
			else if (similarity.getSim() <= 1) {
				mrs.add(new MappingEntry(me.getLeft(), me.getRight(), new Similarity(similarity.getSim())));
			}
			else {
				mrs.add(new MappingEntry(me.getLeft(), me.getRight(), new Similarity(0)));
			}
		}
	}

	/**
	 * Calculate the distance.
	 * 
	 * @return Similarity
	 */
	@SuppressWarnings("deprecation")
	private Similarity calculateSimilarity(IObjectInstance left, IObjectInstance right) {

		// get the dates
		Object dateLeft = left.getValue(leftAttributeName);
		Object dateRight = right.getValue(rightAttributeName);
		long distanceInSeconds;

		if (dateLeft instanceof Date && dateRight instanceof Date) {
			distanceInSeconds = Math.abs(((Date) dateLeft).getTime() - ((Date) dateRight).getTime());
		}
		else if (dateLeft instanceof String && dateRight instanceof String) {
			distanceInSeconds = Math.abs(Date.parse(((String) dateLeft)) - Date.parse(((String) dateRight)));
		}
		else if (dateLeft instanceof Long && dateRight instanceof Long) {
			distanceInSeconds = Math.abs((Long) dateLeft - (Long) dateRight);
		}
		else {
			try {
				distanceInSeconds = Math.abs(Date.parse(dateLeft.toString()) - Date.parse(dateRight.toString()) );
			}
			catch (Exception e) {
				throw new UnsupportedOperationException("implemet an action for that date format");
			}
		}

		// calculate distance
		float distance = distanceInSeconds / maxDistance;

		if (distance <= 1 && distance >= 0) {
			return new Similarity(1.0 - distance);
		}
		else if (distance > 1) {
			return new Similarity(1d);
		}
		else {
			return new Similarity(0d);
		}
	}

	@Override
	public String getAttributeNameLeft() {
		return leftAttributeName;
	}

	@Override
	public String getAttributeNameRight() {
		return rightAttributeName;
	}

	@Override
	public void setAttributeNameLeft(String attrLinks) {
		this.leftAttributeName = attrLinks;
	}

	@Override
	public void setAttributeNameRight(String attrRechts) {
		this.rightAttributeName = attrRechts;
	}

	@Override
	public void setAttributeName(String attrName) {
		this.leftAttributeName = attrName;
		this.rightAttributeName = attrName;
	}

}
