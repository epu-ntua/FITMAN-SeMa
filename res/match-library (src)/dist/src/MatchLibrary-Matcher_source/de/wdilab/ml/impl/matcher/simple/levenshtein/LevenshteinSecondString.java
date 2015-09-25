/**
 *
 */
package de.wdilab.ml.impl.matcher.simple.levenshtein;

import org.apache.log4j.Logger;

import com.wcohen.ss.Levenstein;

import de.wdilab.ml.impl.matcher.simple.secondstring.AbstractSecondStringMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * @author Nico Heller
 */
public class LevenshteinSecondString extends AbstractSecondStringMatcher
{
  /**
   *
   */
  public LevenshteinSecondString()
  {
    super();
  }

  /**
   * @param attr1
   * @param threshold
   */
  public LevenshteinSecondString( final String attr1, final float threshold)
  {
    super( attr1, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public LevenshteinSecondString( final String attr1, final String attr2, final float threshold)
  {
    super( attr1, attr2, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   */
  public LevenshteinSecondString( final String attr1, final String attr2)
  {
    super( attr1, attr2);
  }

  /**
   * @param attr1
   */
  public LevenshteinSecondString( final String attr1)
  {
    super( attr1);
  }

  protected static final Logger log = Logger.getLogger( LevenshteinSecondString.class);

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
    log.info( "Match Start.");

    // NOTE We don't need a CachingProvider because <code>AbstractSecondStringMatcher</code> does it
    // for us
    final Levenstein sim = new Levenstein();

    doAbstractStringDistance( oip1, oip2, sim, mrs);

    log.info( "Match Ended.");
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    log.info( "Match Start.");

    // NOTE We don't need a CachingProvider because <code>AbstractSecondStringMatcher</code> does it
    // for us
    final Levenstein sim = new Levenstein();

    doAbstractStringDistance( oip, sim, mrs);

    log.info( "Match Ended.");
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.mapping.
   * IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException
  {
    log.info( "Match Start.");

    // NOTE We don't need a CachingProvider because <code>AbstractSecondStringMatcher</code> does it
    // for us
    final Levenstein sim = new Levenstein();

    doAbstractStringDistance( mp, sim, mrs);

    log.info( "Match Ended.");
  }
}
