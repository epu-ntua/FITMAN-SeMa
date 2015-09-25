package de.wdilab.ml.impl.matcher.simple.cosine;

import uk.ac.shef.wit.simmetrics.similaritymetrics.CosineSimilarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleOneSourceAttributeObjectMatcher;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import org.apache.log4j.Logger;

/**
 * Cosine matcher, library Simmetrics
 * 
 * @author wurdinger
 *         Date: 12.03.2010
 *         Time: 14:06:00
 *         To change this template use File | Settings | File Templates.
 */
public class CosineSimmetrics extends AbstractSimpleOneSourceAttributeObjectMatcher implements IAttributeObjectMatcher
{
  protected static final Logger log = Logger.getLogger( CosineSimmetrics.class);


    public CosineSimmetrics() {
        super();
    }

  /**
   * @param attr1
   */
  public CosineSimmetrics( final String attr1)
  {
    super( attr1);
  }

  /**
   * @param attr1
   * @param threshold
   */
  public CosineSimmetrics( final String attr1, final float threshold)
  {
    super( attr1, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   */
  public CosineSimmetrics( final String attr1, final String attr2)
  {
    super( attr1, attr2, 0f);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public CosineSimmetrics( final String attr1, final String attr2, final float threshold)
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
    int workaround = 160;
    char nbsp = (char)workaround;
    int workaroundRepl = 20;
    char space = (char)workaroundRepl;
    
    final CosineSimilarity simCos = new CosineSimilarity();
    for( final IObjectInstance oiLinks : oip1)
    {
      String p1 = oiLinks.getValue( attrLinks).toString();
      p1 = p1.replace(nbsp,space); // work-around since library can't process non-breaking space in whitespace tokenizer

      for( final IObjectInstance oiRechts : oip2)
      {
        String p2 = oiRechts.getValue( attrRechts).toString();
        p2 = p2.replace(nbsp,space); // work-around since library can't process non-breaking space in whitespace tokenizer

        final double sim = simCos.getSimilarity( p1, p2);

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