/**
 * 
 */
package de.wdilab.ml.impl.matcher.blocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.mahout.clustering.canopy.Canopy;
import org.apache.mahout.clustering.canopy.CanopyClusterer;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.math.Vector;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IMatcherConfiguration;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * @author koepcke1
 */
public class CanopyClustering implements IObjectMatcher
{
  protected static final Logger         log = Logger.getLogger( CanopyClustering.class);

  private final DistanceMeasure         distanceMeasure;

  private final double                  t1;

  private final double                  t2;

  private final HashMap<String, Vector> vectors;

  private final List<Vector>            points;
  
  private int numberOfComparisons;

  public CanopyClustering( final DistanceMeasure distanceMeasure, final double t1, final double t2,
    final HashMap<String, Vector> vectors)
  {
    super();
    this.distanceMeasure = distanceMeasure;
    this.t1 = t1;
    this.t2 = t2;
    this.vectors = vectors;
    points = new ArrayList( vectors.values());
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IObjectMatcher#getProgress()
   */
  @Override
  public double getProgress()
  {
    // TODO Auto-generated method stub
    return 0;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
   * .oi.IObjectInstanceProvider,
   * de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
   * de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip1, final IObjectInstanceProvider oip2, final IMappingStore mrs)
    throws MappingStoreException
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * @see
   * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
   * .oi.IObjectInstanceProvider,
   * de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    final HashMap<Integer, java.util.Vector<String>> clusters = new HashMap<Integer, java.util.Vector<String>>();

    final List<Canopy> canopies = CanopyClusterer.createCanopies( points, distanceMeasure, t1, t2);

    for( final IObjectInstance oi : oip)
    {

      final String oId = oi.getId();
      final Vector result = vectors.get( oId);

      int clId;
      for( final Canopy canopy : canopies)
      {
        final double dist =
          distanceMeasure.distance( canopy.getCenter().getLengthSquared(), canopy.getCenter(), result);

        if( dist < t1)
        {

          clId = canopy.getId();
          if( clusters.containsKey( clId))
          {
            final java.util.Vector<String> p = clusters.get( clId);
            p.add( oId);
            clusters.put( clId, p);
          }
          else
          {
            final java.util.Vector<String> p = new java.util.Vector<String>();
            p.add( oId);
            clusters.put( clId, p);

          }

          if( dist < t2)
          {
            break;
          }
        }

      }
    }
    log.info( "# canopies: " + canopies.size());
    for( final Canopy canopy : canopies)
    {

      final int clId = canopy.getId();

      if( clusters.containsKey( clId))
      {
        final java.util.Vector<String> p = clusters.get( clId);
        final int s = p.size();
        for( int i = 0; i < s - 1; i++)
        {
          final String id1 = p.get( i);

          for( int j = i + 1; j < s; j++)
          {
            final String id2 = p.get( j);

            mrs.add( oip.getInstance( id1), oip.getInstance( id2), new Similarity( 1));

          }

        }

      }
    }

  }

  /*
   * (non-Javadoc)
   * @see
   * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
   * .mapping.IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException
  {
    // TODO Auto-generated method stub

  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IObjectMatcher#getConfiguration()
   */
  @Override
  public IMatcherConfiguration getConfiguration()
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }
  
  public int getNumberOfComparisons() {
	  return numberOfComparisons;
  }

}
