/**
 * (C) Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.simple;

import java.util.Random;

import org.apache.log4j.Logger;

import de.wdilab.ml.impl.MappingEntry;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.AbstractObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Date: 14.06.2010
 * 
 * @author Nico Heller
 */
public class NotInSamplerMatcher extends AbstractObjectMatcher
{
  protected static final Logger      log     = Logger.getLogger( NotInSamplerMatcher.class);

  final protected static ISimilarity SIM_ONE = new Similarity( 1);

  final protected Random             rnd     = new Random();

  final protected IMappingProvider   refMp;

  final protected int                cntToChoose;

  /**
   * @param mp
   * @param cntToChoose
   */
  public NotInSamplerMatcher( final IMappingProvider mp, final int cntToChoose)
  {
    this.refMp = mp;
    this.cntToChoose = cntToChoose;
  }

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
    log.info( "oip1.size:" + oip1.size() + "; oip2.size():" + oip2.size());
    final double r = (double) cntToChoose / ((double) oip1.size() * (double) oip2.size());
    log.info( "Ratio: " + r);
    for( final IObjectInstance oiLeft : oip1)
    {
      for( final IObjectInstance oiRight : oip2)
      {
        final MappingEntry me = new MappingEntry( oiLeft, oiRight, SIM_ONE);
        if( refMp.getMappingEntry( me) != null) continue;
        if( rnd.nextDouble() < r) mrs.add( oiLeft, oiRight, SIM_ONE);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException( "TODO");
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.mapping.
   * IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException
  {
    log.info( "mp.size:" + mp.getSize());
    final double r = (double) cntToChoose / ((double) mp.getSize());
    log.info( "Ratio: " + r);
    for( IMappingEntry me : mp)
    {
      if( refMp.getMappingEntry( me) != null) continue;
      if( rnd.nextDouble() < r) mrs.add( me);
    }
  }

}
