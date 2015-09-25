/**
 * 
 */
package de.wdilab.ml.impl.matcher.compose.pathfunction;

import java.util.Arrays;

import de.wdilab.ml.impl.util.GHashBag;
import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.matcher.ISimMappingPathFunction;
import de.wdilab.ml.interfaces.matcher.MappingPath;

/**
 * Abstract Adapter class witch redirects the var-Arg-call to the collection.
 * 
 * @author Nico Heller
 */
public abstract class AdapterSimMappingPathFunction implements ISimMappingPathFunction
{

  /*
   * (non-Javadoc)
   * @see
   * de.wdilab.ml.interfaces.matcher.ISimMappingPathFunction#calc(de.wdilab.ml.impl.util.GHashBag,
   * de.wdilab.ml.impl.util.GHashBag, de.wdilab.ml.interfaces.matcher.MappingPath[])
   */
  @Override
  public ISimilarity calc( final GHashBag<String> occA, final GHashBag<String> occD, final MappingPath... paths)
  {
    return calc( occA, occD, Arrays.asList( paths));
  }
}
