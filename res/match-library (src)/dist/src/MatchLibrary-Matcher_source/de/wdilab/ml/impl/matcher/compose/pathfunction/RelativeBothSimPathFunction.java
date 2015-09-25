/**
 * 
 */
package de.wdilab.ml.impl.matcher.compose.pathfunction;

import java.util.Collection;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.util.GHashBag;
import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.matcher.MappingPath;
import de.wdilab.ml.interfaces.oi.IObjectInstance;

/**
 * Average of the horizontal Similarity.
 * 
 * @author Nico Heller
 */
public class RelativeBothSimPathFunction extends AdapterSimMappingPathFunction
{
  protected final static MappingPathSimilarityComparator MPSC = new MappingPathSimilarityComparator();

  /*
   * (non-Javadoc)
   * @see
   * de.wdilab.ml.interfaces.matcher.ISimMappingPathFunction#calc(de.wdilab.ml.impl.util.GHashBag,
   * de.wdilab.ml.impl.util.GHashBag, java.util.Collection)
   */
  @Override
  public ISimilarity calc( final GHashBag<String> occA, final GHashBag<String> occD, final Collection<MappingPath> paths)
  {
    IObjectInstance a = null, b = null;
    double simSum = 0;
    for( final MappingPath mp : paths)
    {
      a = mp.getAbc().getLeft();
      b = mp.getBcd().getRight();
      simSum += mp.getHorAD().getSim();
    }
    return new Similarity( 2 * simSum / (occA.get( a.getId()).intValue() + occD.get( b.getId()).intValue()));
  }
}
