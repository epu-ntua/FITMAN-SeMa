/**
 * Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.compose.simfunction;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.matcher.ISimFunction;

/**
 * <p>
 * Date: 19.04.2010
 * </p>
 * 
 * @author Nico Heller
 */
public class AverageSimFunction implements ISimFunction
{

  /*
   * (non-Javadoc)
   * @see
   * de.wdilab.ml.interfaces.matcher.ISimFunction#calc(de.wdilab.ml.interfaces.mapping.ISimilarity
   * [])
   */
  @Override
  public ISimilarity calc( final ISimilarity... sims)
  {
    double simSum = 0;
    for( final ISimilarity sim : sims)
    {
      simSum += sim.getSim();
    }
    return new Similarity( simSum / sims.length);
  }

}
