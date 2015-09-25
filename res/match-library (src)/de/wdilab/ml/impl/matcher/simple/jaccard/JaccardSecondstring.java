package de.wdilab.ml.impl.matcher.simple.jaccard;

import com.wcohen.ss.Jaccard;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleOneSourceAttributeObjectMatcher;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import org.apache.log4j.Logger;

/**
 * Jaccard matcher, library Secondstring
 * 
 * @author wurdinger
 *         Date: 09.03.2010
 *         Time: 11:05:25
 *         To change this template use File | Settings | File Templates.
 */
public class JaccardSecondstring extends AbstractSimpleOneSourceAttributeObjectMatcher implements
    IAttributeObjectMatcher
{
  protected static final Logger log = Logger.getLogger( JaccardSecondstring.class);

    public JaccardSecondstring() {
        super();
    }

  /**
   * @param attr1
   */
  public JaccardSecondstring( final String attr1)
  {
    super( attr1);
  }

  /**
   * @param attr1
   * @param threshold
   */
  public JaccardSecondstring( final String attr1, final float threshold)
  {
    super( attr1, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   */
  public JaccardSecondstring( final String attr1, final String attr2)
  {
    super( attr1, attr2, 0f);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public JaccardSecondstring( final String attr1, final String attr2, final float threshold)
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
    final Jaccard simJacc = new Jaccard();
    for( final IObjectInstance oiLinks : oip1)
    {
      final String p1 = oiLinks.getValue( attrLinks).toString();

      for( final IObjectInstance oiRechts : oip2)
      {
        final String p2 = oiRechts.getValue( attrRechts).toString();
        final double sim = (float) simJacc.score( p1, p2);

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
	  final Jaccard simJacc = new Jaccard();
	  
	  for (IMappingEntry me : mp) {
		  final String p1 = me.getLeft().getValue( attrLinks).toString();
		  final String p2 = me.getRight().getValue( attrLinks).toString();
		  final double sim = (float) simJacc.score( p1, p2);
		  if( sim >= threshold) mrs.add( me.getLeft(), me.getRight(), new Similarity( sim));
	  }
	  
  }
}
