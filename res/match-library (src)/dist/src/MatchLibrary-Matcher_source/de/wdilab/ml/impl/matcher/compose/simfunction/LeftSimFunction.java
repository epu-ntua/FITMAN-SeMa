/**
 * Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.compose.simfunction;

import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.matcher.ISimFunction;

/**
 * <p>
 * Date: 06.08.2010
 * </p>
 * 
 * @author Nico Heller
 */
public class LeftSimFunction implements ISimFunction
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
    return sims[0];
  }

}
