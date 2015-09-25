/**
 * Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.compose;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import de.wdilab.ml.impl.oi.set.DomainMainMemoryObjectInstanceProvider;
import de.wdilab.ml.impl.util.GHashBag;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.ISimFunction;
import de.wdilab.ml.interfaces.matcher.ISimMappingPathFunction;
import de.wdilab.ml.interfaces.matcher.MappingPath;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Composes a MatchResultStore.
 * <p>
 * Date: 19.04.2010
 * </p>
 * 
 * @author Nico Heller
 */
public class Composer
{
  /**
   * A, B
   */
  final IMappingProvider left;

  /**
   * C, D
   */
  final IMappingProvider right;

  /**
   * @param left
   * @param right
   */
  public Composer( final IMappingProvider left, final IMappingProvider right)
  {
    this.left = left;
    this.right = right;
  }

  /**
   * @param mrs
   * @param funcHor
   * @param funcVer
   * @throws MappingStoreException
   */
  public void compose( final IMappingStore mrs, final ISimFunction funcHor, final ISimMappingPathFunction funcVer)
    throws MappingStoreException
  {
    // compose: (A, B) x (C, D) = (A, D)
    final GHashBag<String> occA = new GHashBag<String>();
    final GHashBag<String> occD = new GHashBag<String>();

    // counting how often D is present in C,D
    for( final IMappingEntry me : right)
    {
      occD.add( me.getRight().getId());
    }
    // counting how often A is present in A,B
    for( final IMappingEntry me : left)
    {
      occA.add( me.getLeft().getId());
    }

    final IObjectInstanceProvider oisFromA = new DomainMainMemoryObjectInstanceProvider( left);

    /**
     * A
     */
    for( final IObjectInstance oiA : oisFromA)
    {
      /**
       * D->AB;CD
       */
      final Multimap<IObjectInstance, MappingPath> map = HashMultimap.create();
      for( final Iterator<IMappingEntry> it = left.valueIterator( oiA); it.hasNext();)
      {
        /**
         * A, B
         */
        final IMappingEntry ab = it.next();
        /**
         * C = B
         */
        for( final Iterator<IMappingEntry> itCD = right.valueIterator( ab.getRight()); itCD.hasNext();)
        {
          final IMappingEntry cd = itCD.next();
          map.put( cd.getRight(), new MappingPath( ab, cd, funcHor.calc( ab.getSimilarity(), cd.getSimilarity())));
        }
      }

      final Map<IObjectInstance, Collection<MappingPath>> colMap = map.asMap();

      for( final Entry<IObjectInstance, Collection<MappingPath>> e : colMap.entrySet())
      {
        /**
         * A-D over MappingPath: A,B; C,D
         */
        final Collection<MappingPath> paths = e.getValue();
        final ISimilarity sim = funcVer.calc( occA, occD, paths);
        mrs.add( oiA, e.getKey(), sim);
      }
    }
  }
}
