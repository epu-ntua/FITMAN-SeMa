package de.wdilab.ml.impl.matcher.simple.trigram;

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
 * Trigram matcher
 * 
 * @author kotlarz
 */
public class TrigramPPJoinPlus extends AbstractSimpleAttributeObjectMatcher implements IAttributeObjectMatcher
{
  protected static final Logger log = Logger.getLogger( TrigramPPJoinPlus.class);

  /**
   */
  public TrigramPPJoinPlus()
  {
    super();
  }

  /**
   * @param attr1
   */
  public TrigramPPJoinPlus( final String attr1)
  {
    super( attr1);
  }

  /**
   * @param attr1
   * @param threshold
   */
  public TrigramPPJoinPlus( final String attr1, final float threshold)
  {
    super( attr1, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   */
  public TrigramPPJoinPlus( final String attr1, final String attr2)
  {
    super( attr1, attr2, 0f);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public TrigramPPJoinPlus( final String attr1, final String attr2, final float threshold)
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
  public void match( IObjectInstanceProvider oip1, IObjectInstanceProvider oip2, final IMappingStore mrs)
    throws MappingStoreException
  {
    // Wrapping für HighSpeed getInsatnce
    if( !oip1.hasCapability( EOIProviderCapabilityHint.MEMORY))
      oip1 = new MainMemoryFullyCachedObjectInstanceProvider( oip1);
    // Wrapping für HighSpeed getInsatnce
    if( !oip2.hasCapability( EOIProviderCapabilityHint.MEMORY))
      oip2 = new MainMemoryFullyCachedObjectInstanceProvider( oip2);

    final ArrayList<String> ids = new ArrayList<String>();
    final ArrayList<String> values = new ArrayList<String>();
    int startIndex = 0;

    String value;
    for( final IObjectInstance oiLinks : oip1)
    {
      ids.add( oiLinks.getId());
      value = oiLinks.getStringValue( attrLinks);
      if( value == null) value = "";
      values.add( value);
      startIndex++;
    }

    for( final IObjectInstance oiRechts : oip2)
    {
      ids.add( oiRechts.getId());
      value = oiRechts.getStringValue( attrRechts);
      if( value == null) value = "";
      values.add( value);
    }

    final String[] strings = new String[values.size()];
    values.toArray( strings);

    final LinkedList<Correspondence> result = PPJoinPlus.start( 't', threshold, 2, strings);
    final Iterator<Correspondence> iter = result.iterator();

    while( iter.hasNext())
    {
      final Correspondence c = iter.next();

      if( c.getFirstObject() < startIndex && c.getSecondObject() >= startIndex)
      {
        mrs.add( oip1.getInstance( ids.get( c.getFirstObject())), oip2.getInstance( ids.get( c.getSecondObject())),
          new Similarity( c.getSimilarity()));
      }
      else if( c.getFirstObject() >= startIndex && c.getSecondObject() < startIndex)
      {
        mrs.add( oip1.getInstance( ids.get( c.getSecondObject())), oip2.getInstance( ids.get( c.getFirstObject())),
          new Similarity( c.getSimilarity()));
      }/*
        * else{
        * firstObject and secondObject in the same IObjectInstanceProvider
        * }
        */
    }
  }

  @Override
  public void match( IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    // Wrapping für HighSpeed getInsatnce
    if( !oip.hasCapability( EOIProviderCapabilityHint.MEMORY))
      oip = new MainMemoryFullyCachedObjectInstanceProvider( oip);

    final ArrayList<String> ids = new ArrayList<String>();
    final ArrayList<String> values = new ArrayList<String>();

    String value;
    for( final IObjectInstance oiLinks : oip)
    {
      ids.add( oiLinks.getId());
      value = oiLinks.getStringValue( attrLinks);
      if( value == null) value = "";
      values.add( value);
    }

    final String[] strings = new String[values.size()];
    values.toArray( strings);

    final LinkedList<Correspondence> result = PPJoinPlus.start( 't', threshold, 2, strings);

    for( final Correspondence c : result)
    {
      mrs.add( oip.getInstance( ids.get( c.getFirstObject())), oip.getInstance( ids.get( c.getSecondObject())),
        new Similarity( c.getSimilarity()));
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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

}
