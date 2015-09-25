package de.wdilab.ml.impl.matcher.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class FactorSet<X> {
	Set<X> set;

	Map<X, HashSet<X>> equivalenceClasses;

	/**
	 * Builds an initial factorset, given the domain set.
	 * 
	 * @param set
	 *            domain set.
	 */
	public FactorSet(Set<X> set) {
		this.set = set;
		equivalenceClasses = new HashMap<X, HashSet<X>>();
		for (X x : set) {
			HashSet<X> s = new HashSet<X>();
			s.add(x);
			equivalenceClasses.put(x, s);
		}
	}

	/**
	 * Merges equivalence classes for two elements
	 * 
	 * @param x1
	 *            first element
	 * @param x2
	 *            second element
	 */
	public void merge(X x1, X x2) {
		HashSet<X> class1 = equivalenceClasses.get(x1);
		HashSet<X> class2 = equivalenceClasses.get(x2);
		HashSet<X> merged = new HashSet<X>(class1);
		merged.addAll(class2);
		for (X x3 : merged) {
			equivalenceClasses.put(x3, merged);
		}
	}

	/**
	 * @return the latest version of factorset built here.
	 */
	public HashSet<HashSet<X>> factorset() {
		return new HashSet<HashSet<X>>(equivalenceClasses.values());
	}

	/**
	 * @return the function from the domain set to the factorset.
	 */
	public Function<X, HashSet<X>> asFunction() {
		return Functions.forMap(equivalenceClasses);
	}

	/**
	 * @return the domain set.
	 */
	public Set<X> domain() {
		return set;
	}
}
