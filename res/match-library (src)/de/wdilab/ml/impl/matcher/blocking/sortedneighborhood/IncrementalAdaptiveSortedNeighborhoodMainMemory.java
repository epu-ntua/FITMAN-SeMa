/**
 * (C) Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.blocking.sortedneighborhood;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.mapping.mainmemory.MainMemoryMapping;
import de.wdilab.ml.impl.matcher.AbstractObjectMatcher;
import de.wdilab.ml.impl.oi.SingleObjectObjectInstanceProvider;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Date: 17.05.2010
 * 
 * @author Nico Heller
 */
public class IncrementalAdaptiveSortedNeighborhoodMainMemory extends AbstractObjectMatcher
{
  protected static final Logger         log          =
                                                       Logger
                                                         .getLogger( IncrementalAdaptiveSortedNeighborhoodMainMemory.class);

  private final AttributeKeyGenerator   keyComparator;

  private final float                   threshold;

  private final IAttributeObjectMatcher dist;

  protected static final String         BLOCKING_KEY = "key";

  /**
   * @param keyComparator
   * @param threshold
   * @param dist
   *          threshold must be setted to zero!
   */
  public IncrementalAdaptiveSortedNeighborhoodMainMemory( final AttributeKeyGenerator keyComparator,
    final float threshold, final IAttributeObjectMatcher dist)
  {
    this.keyComparator = keyComparator;
    this.threshold = threshold;
    this.dist = dist;
    this.dist.setThreshold( 0);
    // this.dist.setAttributeName( BLOCKING_KEY);
  }

  protected final String LEFT_MARKER = "IA_SNMM_LEFT_MARKER";

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
    int n = 0;
    for( final IObjectInstance oi : oip)
    {
      array[n++] = oi;
    }
    log.info( n + " copied.");

    log.info( "Sort Data");
    Arrays.sort( array, keyComparator);
    log.info( "END Sort Data");

    log.info( "Sliding window");

    int wndSizeSume = 0;
    int cntWnd = 0;

    int window = 2;
    int newWindow = 2;
    IObjectInstance first, last;
    int start = 0;
    int end = 0;
    while( start + window < n)
    {
      // log.info( "Enlarge " + start);
      /* enlargement */
      do
      {
        end = start + window - 1;
        if( end >= n)
        {
          end = n - 1;
          break;
        }
        first = array[start];
        last = array[end];
        double dist = distByObject( first, last);
        if( dist >= threshold) break;
        dist = Math.max( 0.1, dist);
        newWindow = (int) Math.ceil( threshold * window / dist);
        if( newWindow != window)
          window = newWindow;
        else
          break;
      } while( true);

      /* retrenchment */
      end = start + window - 1;
      end = Math.min( end, n - 1);
      // log.info( "Retrenchment " + start + " - " + end);
      first = array[start];
      last = array[end];
      final double dist = distByObject( first, last);
      if( dist > threshold)
      {
        window = (int) (threshold * window / dist);
        // innerhalb des aktuellen Fensters suchen wir nun ein nebeneinanderliegendes Paar, dessen
        // Abstand größer ist als
        // threshold
        // Autoren schlagen eine BinärSuche vor? Was fehlerhaft sein kann!

        final int bsEnd = binaryComparision( array, start, end);
        if( bsEnd > -1)
        {
          end = bsEnd;
          // log.info( "binaryComparision: " + start + " - " + bsEnd);
        }
      }

      doBlocking( array, start, end, mrs);
      window = end - start + 1;
      log.debug( "Final Windnow " + window);
      wndSizeSume += window;
      cntWnd++;
      /* Reset and Respos Window */
      window = 2;
      start = end + 1;
    }

    log.info( "END Sliding window");
    log.info( "AvgWndSze: " + (float) wndSizeSume / cntWnd);
  }

  protected void doBlocking( final IObjectInstance[] array, final int low, final int high, final IMappingStore mrs)
    throws MappingStoreException
  {
    // log.info( "Block: " + low + " - " + high);
    IObjectInstance first, second;
    final int end = high + 1;
    for( int i = low; i < end; i++)
    {
      first = array[i];
      for( int j = i + 1; j < end; j++)
      {
        second = array[j];
        mrs.add( first, second, new Similarity( 1));
      }
    }
  }

  protected int binaryComparision( final IObjectInstance[] array, final int low, final int high)
    throws MappingStoreException
  {
    if( high < low) return -1; // not found
    final IObjectInstance first = array[low], last = array[high];
    if( low + 1 == high)
    {
      if( distByObject( first, last) > threshold)
        return low;
      else
        return -1;
    }
    final int mid = low + (high - low) / 2;

    final IObjectInstance middle = array[mid];
    final double distLeft = distByObject( first, middle);
    final double distRight = distByObject( middle, last);

    // Pref larger Windwos
    if( distRight > threshold)
    {
      if( low + 1 == mid) return low;
      final int idx = binaryComparision( array, mid + 1, high);
      if( idx > -1) return idx;
    }
    if( distLeft > threshold)
    {
      if( mid + 1 == high) return mid;
      return binaryComparision( array, low, mid - 1);
    }
    else
      return -1;
  }

  // /**
  // * Distance by Key
  // *
  // * @param first
  // * @param last
  // * @return
  // * @throws MappingStoreException
  // */
  // private double distByKey( final IObjectInstance first, final IObjectInstance last) throws
  // MappingStoreException
  // {
  // final String firstKey = keyComparator.makeKey( first);
  // final String lastKey = keyComparator.makeKey( last);
  // final ObjectInstance oiFirst = new ObjectInstance( BLOCKING_KEY, firstKey, BLOCKING_KEY,
  // firstKey);
  // final ObjectInstance oiLast = new ObjectInstance( BLOCKING_KEY, lastKey, BLOCKING_KEY,
  // lastKey);
  //
  // final MainMemoryMapping mmm = new MainMemoryMapping();
  // dist.match( new SingleObjectObjectInstanceProvider( oiFirst, null), new
  // SingleObjectObjectInstanceProvider( oiLast,
  // null), mmm);
  // if( mmm.size() != 1) throw new MappingStoreException( "Cant calc distance!");
  //
  // final Iterator<IMappingEntry> it = mmm.iterator();
  // it.hasNext();
  // final IMappingEntry me = it.next();
  //
  // return 1 - me.getSimilarity().getSim();
  // }

  /**
   * @param first
   * @param last
   * @return
   * @throws MappingStoreException
   */
  private double distByObject( final IObjectInstance first, final IObjectInstance last) throws MappingStoreException
  {
    final MainMemoryMapping mmm = new MainMemoryMapping();
    dist.match( new SingleObjectObjectInstanceProvider( first, null), new SingleObjectObjectInstanceProvider( last,
      null), mmm);
    if( mmm.size() != 1) throw new MappingStoreException( "Cant calc distance!");

    final Iterator<IMappingEntry> it = mmm.iterator();
    it.hasNext();
    final IMappingEntry me = it.next();

    return 1 - me.getSimilarity().getSim();
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
    return "IASortedNeighborhoodMainMemory; t=" + threshold + "; keys=" + keys + "; len=" + keyComparator.getLen();
  }
}
