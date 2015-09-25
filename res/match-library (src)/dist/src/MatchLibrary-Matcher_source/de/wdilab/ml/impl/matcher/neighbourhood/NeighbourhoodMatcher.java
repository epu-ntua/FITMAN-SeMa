/**
 * 
 */
package de.wdilab.ml.impl.matcher.neighbourhood;

import java.util.Iterator;

import de.wdilab.ml.impl.mapping.mainmemory.MainMemoryMapping;
import de.wdilab.ml.impl.matcher.AbstractObjectMatcher;
import de.wdilab.ml.impl.oi.SingleObjectObjectInstanceProvider;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * <pre>
 * oip1 = 1
 * 
 * oip1ToMiddle = 1-2
 * middleToMiddle = 2-3
 * middleToOip2 = 3-4
 * 
 * oip2 = 4
 * 
 * oip1ToOip2 = Matcher for 1 - 4
 * </pre>
 * 
 * @author Nico Heller
 */
public class NeighbourhoodMatcher extends AbstractObjectMatcher implements IObjectMatcher
{
  /**
   * 1-2
   */
  final IMappingProvider oip1ToMiddle;

  /**
   * 2-3
   */
  final IMappingProvider middleToMiddle;

  /**
   * 3-4
   */
  final IMappingProvider middleToOip2;

  final IObjectMatcher   oip1ToOip2;

  final float            threshold;

  /**
   * @param oip1ToMiddle
   * @param middleToMiddle
   * @param middleToOip2
   * @param oip1ToOip2
   * @param threshold
   */
  public NeighbourhoodMatcher( final IMappingProvider oip1ToMiddle, final IMappingProvider middleToMiddle,
    final IMappingProvider middleToOip2, final IObjectMatcher oip1ToOip2, final float threshold)
  {
    this.oip1ToMiddle = oip1ToMiddle;
    this.middleToMiddle = middleToMiddle;
    this.middleToOip2 = middleToOip2;
    this.oip1ToOip2 = oip1ToOip2;
    this.threshold = threshold;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
   * de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip1, final IObjectInstanceProvider oip2, final IMappingStore mrs)
    throws MappingStoreException
  {
    final MainMemoryMapping mmTempToMatch = new MainMemoryMapping();
    // 1-2
    for( final IMappingEntry me1_2 : oip1ToMiddle)
    {
      // FIXME META_DATA evtl doch an OI???
      final SingleObjectObjectInstanceProvider soipResultLeft =
        new SingleObjectObjectInstanceProvider( me1_2.getLeft(), null);
      // 2-3
      for( final Iterator<IMappingEntry> itMtoM = middleToMiddle.valueIterator( me1_2.getRight()); itMtoM.hasNext();)
      {
        final IMappingEntry me2_3 = itMtoM.next();
        // 3-4
        for( final Iterator<IMappingEntry> itMto2 = middleToOip2.valueIterator( me2_3.getRight()); itMto2.hasNext();)
        {
          final IMappingEntry me3_4 = itMto2.next();
          final SingleObjectObjectInstanceProvider soipResultRight =
            new SingleObjectObjectInstanceProvider( me3_4.getRight(), null);

          oip1ToOip2.match( soipResultLeft, soipResultRight, mmTempToMatch);
          // TODO
          // final ISimilarity sim =
        }
      }

    }

  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs)
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.mapping.
   * IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }
}
