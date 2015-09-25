package de.wdilab.ml.impl.matcher.simple;

import uk.ac.shef.wit.simmetrics.similaritymetrics.SmithWaterman;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import org.apache.log4j.Logger;

/**
 * SmithWaterman matcher, library Simmetrics
 *
 * @author wurdinger
 *         Date: 30.06.2010
 *         Time: 14:06:00
 *         To change this template use File | Settings | File Templates.
 */
public class SmithWatermanSimmetrics extends AbstractSimpleOneSourceAttributeObjectMatcher implements IAttributeObjectMatcher
{
  protected static final Logger log = Logger.getLogger( SmithWatermanSimmetrics.class);

    public SmithWatermanSimmetrics() {
        super();
    }

    /**
   * @param attr1
   */
  public SmithWatermanSimmetrics( final String attr1)
  {
    super( attr1);
  }

  /**
   * @param attr1
   * @param threshold
   */
  public SmithWatermanSimmetrics( final String attr1, final float threshold)
  {
    super( attr1, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   */
  public SmithWatermanSimmetrics( final String attr1, final String attr2)
  {
    super( attr1, attr2, 0f);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public SmithWatermanSimmetrics( final String attr1, final String attr2, final float threshold)
  {
    super( attr1, attr2, threshold);
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
    final SmithWaterman simSW = new SmithWaterman();
    for( final IObjectInstance oiLinks : oip1)
    {
      final String p1 = oiLinks.getValue( attrLinks).toString();

      for( final IObjectInstance oiRechts : oip2)
      {
        final String p2 = oiRechts.getValue( attrRechts).toString();
        final double sim = simSW.getSimilarity( p1, p2);

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