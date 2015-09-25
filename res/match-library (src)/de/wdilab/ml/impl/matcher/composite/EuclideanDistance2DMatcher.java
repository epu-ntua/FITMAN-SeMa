/**
 * 
 */
package de.wdilab.ml.impl.matcher.composite;

import java.awt.geom.Point2D;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.AbstractObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IThresholdedObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Distance matcher <br/>
 * The distance is calculated as follows:
 * 
 * <pre>
 * dist = Math.min( maxLen, p1.distance( p2));
 * sim = (float) (dist == 0 ? 1 : 1 - dist / maxLen);
 * </pre>
 * 
 * @author Nico Heller
 */
public class EuclideanDistance2DMatcher extends AbstractObjectMatcher implements IThresholdedObjectMatcher
{
  final IPointExctrator peLeft;

  final IPointExctrator peRight;

  final double          maxLen;

  float                 threshold;

  /**
   * @param peLeft
   *          PointExtractor for the left source
   * @param peRight
   *          PointExtractor for the right source
   * @param maxLen
   *          norm
   */
  public EuclideanDistance2DMatcher( final IPointExctrator peLeft, final IPointExctrator peRight, final double maxLen)
  {
    this( peLeft, peRight, maxLen, 0f);
  }

  /**
   * @param peLeft
   *          PointExtractor for the left source
   * @param peRight
   *          PointExtractor for the right source
   * @param maxLen
   *          norm
   * @param threshold
   */
  public EuclideanDistance2DMatcher( final IPointExctrator peLeft, final IPointExctrator peRight, final double maxLen,
    final float threshold)
  {
    this.peLeft = peLeft;
    this.peRight = peRight;
    this.maxLen = maxLen;

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
    for( final IObjectInstance oiLinks : oip1)
    {
      final Point2D p1 = peLeft.getPoint( oiLinks);

      for( final IObjectInstance oiRechts : oip2)
      {
        final Point2D p2 = peRight.getPoint( oiRechts);
        final double dist = Math.min( maxLen, p1.distance( p2));
        final float sim = (float) (dist == 0 ? 1 : 1 - dist / maxLen);

        if( sim >= threshold) mrs.add( oiLinks, oiRechts, new Similarity( sim));
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    match( oip, oip, mrs);
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IObjectMatcher#getThreshold()
   */
  @Override
  public float getThreshold()
  {
    return threshold;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IObjectMatcher#setThreshold(float)
   */
  @Override
  public void setThreshold( final float threshold)
  {
    this.threshold = threshold;
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
