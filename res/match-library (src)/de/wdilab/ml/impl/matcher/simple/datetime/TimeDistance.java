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
public class TimeDistance extends AbstractObjectMatcher implements IAttributeObjectMatcher {

	private float threshold = 0f;
	private String leftAttributeName;
	private String rightAttributeName;
	
	private int hours = 0;
	private int minutes = 0;
	private int seconds = 0;
	
	private long maxDistance = 0;

	public TimeDistance(int hours, int minutes, int seconds) {
		setHours(hours);
		setMinutes(minutes);
		setSeconds(seconds);
		
		maxDistance = hours * 60 * 60 + minutes * 60 + seconds;
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

		// get the times
		Object dateLeft = left.getValue(leftAttributeName);
		Object dateRight = right.getValue(rightAttributeName);
		long distanceMillies;

		// calculate similarity 
		if (dateLeft instanceof Date && dateRight instanceof Date) {
			distanceMillies = Math.abs(((Date) dateLeft).getTime() - ((Date) dateRight).getTime());
		}
		else if (dateLeft instanceof String && dateRight instanceof String) {
			distanceMillies = Math.abs(Date.parse(((String) dateLeft)) - Date.parse(((String) dateRight)));
		}
		else if (dateLeft instanceof Long && dateRight instanceof Long) {
			distanceMillies = Math.abs((Long) dateLeft - (Long) dateRight);
		}
		else {
			try {
				distanceMillies = Math.abs(Date.parse(dateLeft.toString()) - Date.parse(dateRight.toString()) );
			}
			catch (Exception e) {
				throw new UnsupportedOperationException("implemet an action for that date format");
			}
		}

		// calculate distance
		float distance = distanceMillies / maxDistance;

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

	public void setHours(int hour) {
		this.hours = hour;
	}

	public int getHours() {
		return hours;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	public int getSeconds() {
		return seconds;
	}

}
