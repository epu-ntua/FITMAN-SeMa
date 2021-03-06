/**
 * 
 */
package de.wdilab.ml.impl.matcher.simple.cosine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import algorithms.ppjoinplus.Correspondence;
import algorithms.ppjoinplus.PPJoinPlus;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.impl.oi.enhancement.persistence.MainMemoryFullyCachedObjectInstanceProvider;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.EOIProviderCapabilityHint;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Cosine Matcher fully cached.
 * 
 * @author Nico Heller
 */
public class CosineFullyCachedPPJoinPlus extends AbstractSimpleAttributeObjectMatcher implements
    IAttributeObjectMatcher
{
  protected static final Logger log = Logger.getLogger( CosineFullyCachedPPJoinPlus.class);

  /**
   * @param attr1
   */
  public CosineFullyCachedPPJoinPlus( final String attr1)
  {
    super( attr1);
  }

  /**
   * @param attr1
   * @param threshold
   */
  public CosineFullyCachedPPJoinPlus( final String attr1, final float threshold)
  {
    super( attr1, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   */
  public CosineFullyCachedPPJoinPlus( final String attr1, final String attr2)
  {
    super( attr1, attr2, 0f);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public CosineFullyCachedPPJoinPlus( final String attr1, final String attr2, final float threshold)
  {
    super( attr1, attr2, threshold);
  }

  /**
   * 
   */
  public CosineFullyCachedPPJoinPlus()
  {
    super();
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
    IObjectInstanceProvider mmp1;
    // Wrapping für HighSpeed getInsatnce
    if( oip1.hasCapability( EOIProviderCapabilityHint.MEMORY))
      mmp1 = oip1;
    else
      mmp1 = new MainMemoryFullyCachedObjectInstanceProvider( oip1);
    //
    final ArrayList<String> ids = new ArrayList<String>();
    final ArrayList<String> values = new ArrayList<String>();
    int startIndex = 0;
    //
    // final StopWatch sw = new StopWatch();
    //
    for( final IObjectInstance oiLinks : mmp1)
    {
      ids.add( oiLinks.getId());
      values.add( oiLinks.getStringValue( attrLinks));
      startIndex++;
    }

    IObjectInstanceProvider mmp2;
    // Wrapping für HighSpeed getInsatnce
    if( oip2.hasCapability( EOIProviderCapabilityHint.MEMORY))
      mmp2 = oip2;
    else
      mmp2 = new MainMemoryFullyCachedObjectInstanceProvider( oip2);

    for( final IObjectInstance oiRechts : mmp2)
    {
      ids.add( oiRechts.getId());
      values.add( oiRechts.getStringValue( attrRechts));
    }

    final String[] strings = new String[values.size()];
    values.toArray( strings);

    final LinkedList<Correspondence> result = PPJoinPlus.start( 'c', threshold, 2, strings);
    final Iterator<Correspondence> iter = result.iterator();
    while( iter.hasNext())
    {
      final Correspondence c = iter.next();

      if( c.getFirstObject() < startIndex && c.getSecondObject() >= startIndex)
      {
        mrs.add( mmp1.getInstance( ids.get( c.getFirstObject())), mmp2.getInstance( ids.get( c.getSecondObject())),
          new Similarity( c.getSimilarity()));
      }
      else if( c.getFirstObject() >= startIndex && c.getSecondObject() < startIndex)
      {
        mrs.add( mmp1.getInstance( ids.get( c.getSecondObject())), mmp2.getInstance( ids.get( c.getFirstObject())),
          new Similarity( c.getSimilarity()));
      }/*
        * else{
        * firstObject and secondObject in the same IObjectInstanceProvider
        * }
        */
    }
    // // final Iterator<Correspondence> iter = result.iterator();
    // for( final Correspondence c : PPJoinPlus.start( 'c', threshold, 2, strings))
    // {
    // mrs.add( mp1.getInstance( ids.get( c.getFirstObject())), mp1.getInstance( ids.get(
    // c.getSecondObject())),
    // new Similarity( c.getSimilarity()));
    //
    // }
    // throw new UnsupportedOperationException();
  }

  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    log.info( "Match Start.");

    IObjectInstanceProvider mmp;
    // Wrapping für HighSpeed getInsatnce
    if( oip.hasCapability( EOIProviderCapabilityHint.MEMORY))
      mmp = oip;
    else
      mmp = new MainMemoryFullyCachedObjectInstanceProvider( oip);

    final ArrayList<String> ids = new ArrayList<String>();
    final ArrayList<String> values = new ArrayList<String>();

    // final StopWatch sw = new StopWatch();
    // sw.start();
    for( final IObjectInstance oiLinks : mmp)
    {
    	String value = oiLinks.getStringValue( attrLinks);
    	if (value != null) {
      ids.add( oiLinks.getId());
      values.add( oiLinks.getStringValue( attrLinks));
    	}
    }

    final String[] strings = new String[values.size()];
    values.toArray( strings);
    // sw.stop();
    // log.info( "SetupTime: " + sw.toString() + " for " + ids.size());
    // sw.reset();
    // sw.start();
    final LinkedList<Correspondence> result = PPJoinPlus.start( 'c', threshold, 2, strings);
    // sw.stop();
    // log.info( "Matched for " + result.size() + " in " + sw.toString());
    // sw.reset();
    // sw.start();
    log.info( "Fill MappingStore: " + result.size());
    for( final Correspondence c : result)
    {
      mrs.add( mmp.getInstance( ids.get( c.getFirstObject())), mmp.getInstance( ids.get( c.getSecondObject())),
        new Similarity( c.getSimilarity()));
    }
    // sw.stop();
    // log.info( "Machts added in " + sw.toString());
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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }
}