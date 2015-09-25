/**
 *
 */
package de.wdilab.ml.impl.matcher.simple.secondstring;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.wcohen.ss.AbstractStatisticalTokenDistance;
import com.wcohen.ss.AbstractStringDistance;
import com.wcohen.ss.api.StringWrapperIterator;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Frame for the SecondString-Library.
 * Use one of the Methodes:
 * <ul>
 * <li>doAbstractStatisticalTokenDistance</li>
 * </ul>
 * Example:
 *
 * <pre>
 * final Tokenizer tokenizer = new SimpleTokenizer( true, true);
 * final TFIDF tfidf = new TFIDF( tokenizer);
 *
 * doAbstractStatisticalTokenDistance( oip, tfidf, mrs);
 * </pre>
 *
 * @author Nico Heller
 */
public abstract class AbstractSecondStringMatcher extends AbstractSimpleAttributeObjectMatcher
{
  /**
   *
   */
  public AbstractSecondStringMatcher()
  {
    super();
  }

  /**
   * @param attr1
   * @param threshold
   */
  public AbstractSecondStringMatcher( final String attr1, final float threshold)
  {
    super( attr1, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public AbstractSecondStringMatcher( final String attr1, final String attr2, final float threshold)
  {
    super( attr1, attr2, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   */
  public AbstractSecondStringMatcher( final String attr1, final String attr2)
  {
    super( attr1, attr2);
  }

  /**
   * @param attr1
   */
  public AbstractSecondStringMatcher( final String attr1)
  {
    super( attr1);
  }

  protected static final Logger log = Logger.getLogger( AbstractSecondStringMatcher.class);

  /**
   * @param oip
   * @param asd
   * @param mrs
   * @throws MappingStoreException
   */
  protected void doAbstractStatisticalTokenDistance( final IObjectInstanceProvider oip,
    final AbstractStatisticalTokenDistance asd, final IMappingStore mrs) throws MappingStoreException
  {
    final ArrayList<WordStringWrapper> pp = new ArrayList<WordStringWrapper>( 100000);

    for( final IObjectInstance oi : oip)
      pp.add( new WordStringWrapper( oi, oi.getStringValue( attrLinks), asd));

    final StringWrapperIterator swi = new WordStringWrapperIterator( pp.iterator());

    asd.train( swi);

    final HashSet<WordStringWrapper> secondList = new HashSet<WordStringWrapper>( pp);

    for( final WordStringWrapper first : pp)
    {
      secondList.remove( first);
      for( final WordStringWrapper second : secondList)
      {
        final double sim = asd.score( first.sw, second.sw);
        if( sim >= threshold) mrs.add( first.oi, second.oi, new Similarity( sim));
      }
    }
  }

  /**
   * @param oip1
   * @param oip2
   * @param asd
   * @param mrs
   * @throws MappingStoreException
   */
  protected void doAbstractStatisticalTokenDistance( final IObjectInstanceProvider oip1,
    final IObjectInstanceProvider oip2, final AbstractStatisticalTokenDistance asd, final IMappingStore mrs)
    throws MappingStoreException
  {
    final ArrayList<WordStringWrapper> pp1 = new ArrayList<WordStringWrapper>( 100000);
    final ArrayList<WordStringWrapper> pp2 = new ArrayList<WordStringWrapper>( 100000);

    for( final IObjectInstance oi : oip1)
      pp1.add( new WordStringWrapper( oi, oi.getStringValue( attrLinks), asd));

    for( final IObjectInstance oi : oip2)
      pp2.add( new WordStringWrapper( oi, oi.getStringValue( attrRechts), asd));

    final StringWrapperIterator swi1 = new WordStringWrapperIterator( pp1.iterator());
    final StringWrapperIterator swi2 = new WordStringWrapperIterator( pp2.iterator());

    asd.train( swi1);
    asd.train( swi2);

    for( final WordStringWrapper first : pp1)
    {
      for( final WordStringWrapper second : pp2)
      {
        final double sim = asd.score( first.sw, second.sw);
        if( sim >= threshold) mrs.add( first.oi, second.oi, new Similarity( sim));
      }
    }
  }

  /**
   * @param mp
   * @param asd
   * @param mrs
   * @throws MappingStoreException
   */
  protected void doAbstractStatisticalTokenDistance( final IMappingProvider mp,
    final AbstractStatisticalTokenDistance asd, final IMappingStore mrs) throws MappingStoreException
  {
    // Match Preprocess
    final ArrayList<WordStringWrapper> pp_left = new ArrayList<WordStringWrapper>( 100000);
    final ArrayList<WordStringWrapper> pp_right = new ArrayList<WordStringWrapper>( 100000);
    int idx = 0;
    for( final IMappingEntry me : mp)
    {
      final IObjectInstance left_oi = me.getLeft();
      String value = left_oi.getStringValue( attrLinks);
      if( value != null)
        value = value.toLowerCase();
      else
        value = "";
      pp_left.add( new WordStringWrapper( left_oi, value, asd));
      final IObjectInstance right_oi = me.getRight();
      value = right_oi.getStringValue( attrRechts);
      if( value != null)
        value = value.toLowerCase();
      else
        value = "";
      pp_right.add( new WordStringWrapper( right_oi, value, asd));
      idx++;
    }

    // Train
    final StringWrapperIterator swi_left = new WordStringWrapperIterator( pp_left.iterator());
    asd.train( swi_left);
    final StringWrapperIterator swi_right = new WordStringWrapperIterator( pp_right.iterator());
    asd.train( swi_right);

    // Match
    for( int i = 0; i < idx; i++)
    {
      final WordStringWrapper left = pp_left.get( i);
      final WordStringWrapper right = pp_right.get( i);
      final double sim = asd.score( left.sw, right.sw);
      if( sim >= threshold) mrs.add( left.oi, right.oi, new Similarity( sim));
    }
  }

  /**
   * @param oip
   * @param asd
   * @param mrs
   * @throws MappingStoreException
   */
  protected void doAbstractStringDistance( final IObjectInstanceProvider oip,
    final AbstractStringDistance asd, final IMappingStore mrs) throws MappingStoreException
    {
    final ArrayList<WordStringWrapper> pp = new ArrayList<WordStringWrapper>( 100000);

    for( final IObjectInstance oi : oip)
      pp.add( new WordStringWrapper( oi, oi.getStringValue( attrLinks), asd));

    final HashSet<WordStringWrapper> secondList = new HashSet<WordStringWrapper>( pp);

    for( final WordStringWrapper first : pp)
    {
      secondList.remove( first);
      for( final WordStringWrapper second : secondList)
      {
        final double sim = asd.score( first.sw, second.sw);
        if( sim >= threshold) mrs.add( first.oi, second.oi, new Similarity( sim));
      }
    }
    }

  /**
   * @param oip1
   * @param oip2
   * @param asd
   * @param mrs
   * @throws MappingStoreException
   */
  protected void doAbstractStringDistance( final IObjectInstanceProvider oip1,
    final IObjectInstanceProvider oip2, final AbstractStringDistance asd, final IMappingStore mrs)
  throws MappingStoreException
  {
    final ArrayList<WordStringWrapper> pp1 = new ArrayList<WordStringWrapper>( 100000);
    final ArrayList<WordStringWrapper> pp2 = new ArrayList<WordStringWrapper>( 100000);

    for( final IObjectInstance oi : oip1)
      pp1.add( new WordStringWrapper( oi, oi.getStringValue( attrLinks), asd));

    for( final IObjectInstance oi : oip2)
      pp2.add( new WordStringWrapper( oi, oi.getStringValue( attrRechts), asd));

    for( final WordStringWrapper first : pp1)
    {
      for( final WordStringWrapper second : pp2)
      {
        final double sim = asd.score( first.sw, second.sw);
        if( sim >= threshold) mrs.add( first.oi, second.oi, new Similarity( sim));
      }
    }
  }

  /**
   * @param mp
   * @param asd
   * @param mrs
   * @throws MappingStoreException
   */
  protected void doAbstractStringDistance( final IMappingProvider mp,
    final AbstractStringDistance asd, final IMappingStore mrs) throws MappingStoreException
    {
    // Match Preprocess
    final ArrayList<WordStringWrapper> pp_left = new ArrayList<WordStringWrapper>( 100000);
    final ArrayList<WordStringWrapper> pp_right = new ArrayList<WordStringWrapper>( 100000);
    int idx = 0;
    for( final IMappingEntry me : mp)
    {
      final IObjectInstance left_oi = me.getLeft();
      String value = left_oi.getStringValue( attrLinks);
      if( value != null)
        value = value.toLowerCase();
      else
        value = "";
      pp_left.add( new WordStringWrapper( left_oi, value, asd));
      final IObjectInstance right_oi = me.getRight();
      value = right_oi.getStringValue( attrRechts);
      if( value != null)
        value = value.toLowerCase();
      else
        value = "";
      pp_right.add( new WordStringWrapper( right_oi, value, asd));
      idx++;
    }

    // Match
    for( int i = 0; i < idx; i++)
    {
      final WordStringWrapper left = pp_left.get( i);
      final WordStringWrapper right = pp_right.get( i);
      final double sim = asd.score( left.sw, right.sw);
      if( sim >= threshold) mrs.add( left.oi, right.oi, new Similarity( sim));
    }
    }
}
