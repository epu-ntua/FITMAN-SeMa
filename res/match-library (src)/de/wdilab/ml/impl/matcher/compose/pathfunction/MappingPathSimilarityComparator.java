/**
 * 
 */
package de.wdilab.ml.impl.matcher.compose.pathfunction;

import java.util.Comparator;

import de.wdilab.ml.interfaces.matcher.MappingPath;

/**
 * A Comparator witch comares the underlying similarity (getHorAD).
 * 
 * @author Nico Heller
 */
public class MappingPathSimilarityComparator implements Comparator<MappingPath>
{
  /*
   * (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare( final MappingPath o1, final MappingPath o2)
  {
    return o1.getHorAD().compareTo( o2.getHorAD());
  }

}