/**
 * 
 */
package de.wdilab.ml.impl.matcher.blocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.AbstractObjectMatcher;
import de.wdilab.ml.impl.matcher.blocking.sortedneighborhood.KeyGenComparator;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * @author koepcke1
 * 
 */
public class StandardBlocking extends AbstractObjectMatcher {
	protected static final Logger log = Logger
			.getLogger(StandardBlocking.class);

	private final KeyGenComparator keyComparator;

	public StandardBlocking(KeyGenComparator keyComparator) {
		super();
		this.keyComparator = keyComparator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
	 * .oi.IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(IObjectInstanceProvider oip1,
			IObjectInstanceProvider oip2, IMappingStore mrs)
			throws MappingStoreException {
		HashMap<String, HashSet<IObjectInstance>> l = new HashMap<String, HashSet<IObjectInstance>>();
		for (IObjectInstance oi : oip1) {
			String k = this.keyComparator.makeKey(oi);
			if (k != null) {
				if (l.containsKey(k)) {
					HashSet<IObjectInstance> ois = l.get(k);
					ois.add(oi);
					l.put(k, ois);
				} else {
					HashSet<IObjectInstance> ois = new HashSet<IObjectInstance>();
					ois.add(oi);
					l.put(k, ois);
				}
			}
		}
		HashMap<String, HashSet<IObjectInstance>> r = new HashMap<String, HashSet<IObjectInstance>>();
		for (IObjectInstance oi : oip2) {
			String k = this.keyComparator.makeKey(oi);
			if (k != null) {
				if (r.containsKey(k)) {
					HashSet<IObjectInstance> ois = r.get(k);
					ois.add(oi);
					r.put(k, ois);
				} else {
					HashSet<IObjectInstance> ois = new HashSet<IObjectInstance>();
					ois.add(oi);
					r.put(k, ois);
				}
			}
		}
		Set<String> keys = l.keySet();
		for (String k : keys) {
			HashSet<IObjectInstance> lOis = l.get(k);
			if (r.containsKey(k)) {
				HashSet<IObjectInstance> rOis = r.get(k);
				for (IObjectInstance oiLeft : lOis) {
					for (IObjectInstance oiRight : rOis) {
						mrs.add(oiLeft, oiRight, new Similarity(1));
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
	 * .oi.IObjectInstanceProvider,
	 * de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(IObjectInstanceProvider oip, IMappingStore mrs)
			throws MappingStoreException {

		HashMap <String, ArrayList<IObjectInstance>> map = new HashMap<String, ArrayList<IObjectInstance>>();
		for (IObjectInstance oi : oip) {
			String key = keyComparator.makeKey(oi);
			if (map.containsKey(key)) {
				map.get(key).add(oi);
			}
			else {
				ArrayList<IObjectInstance> list = new ArrayList<IObjectInstance>();
				list.add(oi);
				map.put(key, list);
			}
		}
		
		for (Entry<String, ArrayList<IObjectInstance>> entry : map.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); i++) {
				for (int j = 1; j <= i; j++) {
					mrs.add(entry.getValue().get(i),entry.getValue().get(i - j),new Similarity(1.0));
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
	 * .mapping.IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
	 */
	@Override
	public void match(IMappingProvider mp, IMappingStore mrs)
			throws MappingStoreException {
		// TODO Auto-generated method stub

	}

}
