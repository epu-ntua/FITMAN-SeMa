/**
 * 
 */
package de.wdilab.ml.impl.matcher.mapping;

/**
 * @author koepcke1
 *
 */
public class ConstantCostFunction implements CostFunction {

	/**
	 * 
	 */
	public ConstantCostFunction() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see de.wdilab.ml.impl.matcher.mapping.CostFunction#calculateCost(int, int)
	 */
	@Override
	public double calculateCost(int x, int y) {
		
		return 1;
	}

}
