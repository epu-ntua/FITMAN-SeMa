/**
 * (C) Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.blocking.sortedneighborhood;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.AbstractObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Date: 17.05.2010
 * 
 * @author Nico Heller
 */
public class SortedNeighborhoodMainMemory extends AbstractObjectMatcher
{
  protected static final Logger       log = Logger.getLogger( SortedNeighborhoodMainMemory.class);

  private final AttributeKeyGenerator keyComparator;

  private final int                   window;

  /**
   * @param keyComparator
   * @param window
   */
  public SortedNeighborhoodMainMemory( final AttributeKeyGenerator keyComparator, final int window)
  {
    this.keyComparator = keyComparator;
    this.window = window;
  }

  protected final String LEFT_MARKER = "SNMM_LEFT_MARKER";

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
   * de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip1, final IObjectInstanceProvider oip2, final IMappingStore mrs)
    throws MappingStoreException
  {
    log.info( "Copy Data");
    final IObjectInstance[] array = new IObjectInstance[oip1.size() + oip2.size()];
    int k = 0;
    for( final IObjectInstance oi : oip1)
    {
      oi.putProgramDataValue( LEFT_MARKER, Boolean.TRUE);
      array[k++] = oi;
    }
    for( final IObjectInstance oi : oip2)
    {
      oi.putProgramDataValue( LEFT_MARKER, Boolean.FALSE);
      array[k++] = oi;
    }
    log.info( k + " copied.");

    log.info( "Sort Data");
    Arrays.sort( array, keyComparator);
    log.info( "END Sort Data");

    log.info( "Sliding window");
    IObjectInstance first, second;
    int end;
    for( int i = 0; i < window; i++)
    {
      first = array[i];
      for( int j = i + 1; j < window; j++)
      {
        second = array[j];
        if( !first.getProgramDataValue( LEFT_MARKER).equals( second.getProgramDataValue( LEFT_MARKER)))
        {
          final IObjectInstance left = Boolean.TRUE.equals( first.getProgramDataValue( LEFT_MARKER)) ? first : second;
          final IObjectInstance right = Boolean.TRUE.equals( first.getProgramDataValue( LEFT_MARKER)) ? second : first;

          mrs.add( left, right, new Similarity( 1));
        }
      }
    }

    final int normSlidingEnd = k - window;
    for( int i = 1; i <= normSlidingEnd; i++)
    {
      end = i + window - 1;
      first = array[end];
      for( int j = i; j < end; j++)
      {
        second = array[j];
        if( !first.getProgramDataValue( LEFT_MARKER).equals( second.getProgramDataValue( LEFT_MARKER)))
        {
          final IObjectInstance left = Boolean.TRUE.equals( first.getProgramDataValue( LEFT_MARKER)) ? first : second;
          final IObjectInstance right = Boolean.TRUE.equals( first.getProgramDataValue( LEFT_MARKER)) ? second : first;

          mrs.add( left, right, new Similarity( 1));
        }
      }
    }
    log.info( "END Sliding window");
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    log.info( "Copy Data");
    final IObjectInstance[] array = new IObjectInstance[oip.size()];
    int k = 0;
    for( final IObjectInstance oi : oip)
    {
      array[k++] = oi;
    }
    log.info( k + " copied.");

    log.info( "Sort Data");
    Arrays.sort( array, keyComparator);
    log.info( "END Sort Data");

    log.info( "Sliding window: " + window);
    IObjectInstance first, second;
    int cntAdds = 0;
    int end;
    for( int i = 0; i < window; i++)
    {
      first = array[i];
      for( int j = i + 1; j < window; j++)
      {
        second = array[j];
        mrs.add( first, second, new Similarity( 1));
        cntAdds++;
      }
    }

    final int normSlidingEnd = k - window;
    for( int i = 1; i <= normSlidingEnd; i++)
    {
      end = i + window - 1;
      first = array[end];
      for( int j = i; j < end; j++)
      {
        second = array[j];
        mrs.add( first, second, new Similarity( 1));
        cntAdds++;
      }
    }
    log.info( "END Sliding window: " + cntAdds);
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.mapping.
   * IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getParamedName()
  {
    final String keys = StringUtils.join( keyComparator.getAttrs(), ", ");
    return "SortedNeighborhoodMainMemory; w=" + window + "; keys=" + keys + "; len=" + keyComparator.getLen();
  }
}
