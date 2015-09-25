/**
 * Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.simple.trigram;

import java.util.HashMap;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleOneSourceAttributeObjectMatcher;
import de.wdilab.ml.impl.util.GHashBag;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

import static de.wdilab.ml.impl.util.string.NGramFunctions.*;

/**
 * @author Nico Heller
 */
public class LowMemoryTrigram extends AbstractSimpleOneSourceAttributeObjectMatcher implements IObjectMatcher
{
  /**
   * @param attr1
   * @param attr2
   */
  public LowMemoryTrigram( final String attr1, final String attr2)
  {
    super( attr1, attr2, 0f);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public LowMemoryTrigram( final String attr1, final String attr2, final float threshold)
  {
    super( attr1, attr2, threshold);
  }

  /**
   * @param attr1
   */
  public LowMemoryTrigram( final String attr1)
  {
    super( attr1, 0f);
  }

  /**
   * @param attr1
   * @param threshold
   */
  public LowMemoryTrigram( final String attr1, final float threshold)
  {
    super( attr1, threshold);
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
      String value = oiLinks.getStringValue( attrLinks);
      if( value != null) value = value.toLowerCase();
      final GHashBag<String> trigrammeLinks = generateTriGramHashBag( value);

      for( final IObjectInstance oiRechts : oip2)
      {
        value = oiRechts.getStringValue( attrRechts);
        if( value != null) value = value.toLowerCase();
        final GHashBag<String> trigrammeRechts = generateTriGramHashBag( value);

        final float sim = computeTriGramSimilarity( trigrammeLinks, trigrammeRechts);

        if( sim >= threshold) mrs.add( oiLinks, oiRechts, new Similarity( sim));
      }
    }
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.mapping.
   * IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException
  {
    final HashMap<String, GHashBag<String>> gramme = new HashMap<String, GHashBag<String>>();

    // Matchprocess
    for( final IMappingEntry me : mp)
    {
      // Left
      final IObjectInstance left_oi = me.getLeft();
      String value = left_oi.getStringValue( attrLinks);
      if( value != null)
        value = value.toLowerCase();
      else
        value = "";
      GHashBag<String> left_gram = gramme.get( value);
      if( left_gram == null)
      {
        left_gram = generateTriGramHashBag( value);
        gramme.put( value, left_gram);
      }
      // Right
      final IObjectInstance right_oi = me.getRight();
      value = right_oi.getStringValue( attrRechts);
      if( value != null)
        value = value.toLowerCase();
      else
        value = "";
      GHashBag<String> right_gram = gramme.get( value);
      if( right_gram == null)
      {
        right_gram = generateTriGramHashBag( value);
        gramme.put( value, right_gram);
      }
      // Match
      final float sim = computeTriGramSimilarity( left_gram, right_gram);

      if( sim >= threshold) mrs.add( left_oi, right_oi, new Similarity( sim));
    }

  }

}
