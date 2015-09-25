/**
 * Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.compose.simfunction;

import java.util.Arrays;
import java.util.Collections;

import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.matcher.ISimFunction;

/**
 * <p>
 * Date: 19.04.2010
 * </p>
 * 
 * @author Nico Heller
 */
public class MaximumSimFunction implements ISimFunction
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
    return Collections.max( Arrays.asList( sims));
  }

}
