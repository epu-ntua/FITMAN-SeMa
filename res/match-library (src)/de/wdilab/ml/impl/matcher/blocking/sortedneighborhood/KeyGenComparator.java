/**
 * (C) Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.blocking.sortedneighborhood;

import java.util.Comparator;

import de.wdilab.ml.interfaces.oi.IObjectInstance;

/**
 * Date: 18.05.2010
 * 
 * @author Nico Heller
 */
public abstract class KeyGenComparator implements Comparator<IObjectInstance>
{
  /*
   * (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare( final IObjectInstance o1, final IObjectInstance o2)
  {
    return makeKey( o1).compareTo( makeKey( o2));
  }

  /**
   * @param oi
   * @return the key as a String
   */
  public abstract String makeKey( IObjectInstance oi);
}
