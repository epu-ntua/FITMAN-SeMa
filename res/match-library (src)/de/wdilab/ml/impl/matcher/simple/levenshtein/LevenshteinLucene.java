package de.wdilab.ml.impl.matcher.simple.levenshtein;

import org.apache.log4j.Logger;
import org.apache.lucene.search.spell.LevensteinDistance;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleOneSourceAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Levenshtein matcher, library Lucene.search.spell
 *
 * @author wurdinger
 * @author Nico Heller
 *         Date: 23.03.2010
 *         Time: 13:31:25
 *         To change this template use File | Settings | File Templates.
 */
public class LevenshteinLucene extends AbstractSimpleOneSourceAttributeObjectMatcher implements IAttributeObjectMatcher
{
  protected static final Logger log = Logger.getLogger( LevenshteinLucene.class);

    public LevenshteinLucene() {
        super();
    }

  /**
   * @param attr1
   */
  public LevenshteinLucene( final String attr1)
  {
    super( attr1);
  }

  /**
   * @param attr1
   * @param threshold
   */
  public LevenshteinLucene( final String attr1, final float threshold)
  {
    super( attr1, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   */
  public LevenshteinLucene( final String attr1, final String attr2)
  {
    super( attr1, attr2, 0f);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public LevenshteinLucene( final String attr1, final String attr2, final float threshold)
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
    final LevensteinDistance simLev = new LevensteinDistance();
    for( final IObjectInstance oiLinks : oip1)
    {
      final String p1 = oiLinks.getValue( attrLinks).toString();

      for( final IObjectInstance oiRechts : oip2)
      {
        final String p2 = oiRechts.getValue( attrRechts).toString();
        final double sim = simLev.getDistance( p1, p2);

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
    final LevensteinDistance simLev = new LevensteinDistance();

    for( final IMappingEntry me : mp)
    {
      final String p1 = me.getLeft().getValue( attrLinks).toString();
      final String p2 = me.getRight().getValue( attrRechts).toString();
      final double sim = simLev.getDistance( p1, p2);

      if( sim >= threshold) mrs.add( me.getLeft(), me.getRight(), new Similarity( sim));
    }
  }
}