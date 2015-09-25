/**
 * (C) Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.oi.enhancement.persistence.MainMemoryFullyCachedObjectInstanceProvider;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.oi.EOIProviderCapabilityHint;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Result are a mapping with only match-pairs (sim=1).
 * Date: 03.05.2010
 * 
 * @author Nico Heller
 */
public class AttributeEqualsObjectMatcher extends AbstractSimpleAttributeObjectMatcher
{
  protected static final Logger log                   = Logger.getLogger( AttributeEqualsObjectMatcher.class);

  private boolean               treatNoMatchAsSimZero = false;

  final boolean                 treatNullValuesAsSimZero;

  /**
   * @param attr1
   * @param attr2
   * @param treatNullValuesAsSimZero
   */
  public AttributeEqualsObjectMatcher( final String attr1, final String attr2, final boolean treatNullValuesAsSimZero)
  {
    super( attr1, attr2);
    this.treatNullValuesAsSimZero = treatNullValuesAsSimZero;
  }

  /**
   * @param attr1
   * @param treatNullValuesAsSimZero
   */
  public AttributeEqualsObjectMatcher( final String attr1, final boolean treatNullValuesAsSimZero)
  {
    super( attr1);
    this.treatNullValuesAsSimZero = treatNullValuesAsSimZero;
  }

  /**
   * @param treatNullValuesAsSimZero
   */
  public AttributeEqualsObjectMatcher( final boolean treatNullValuesAsSimZero)
  {
    this.treatNullValuesAsSimZero = treatNullValuesAsSimZero;
  }

  /**
   * @param attr1
   * @param threshold
   * @param treatNullValuesAsSimZero
   */
  public AttributeEqualsObjectMatcher( final String attr1, final float threshold, final boolean treatNullValuesAsSimZero)
  {
    super( attr1, threshold);
    this.treatNullValuesAsSimZero = treatNullValuesAsSimZero;
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   * @param treatNullValuesAsSimZero
   */
  public AttributeEqualsObjectMatcher( final String attr1, final String attr2, final float threshold,
    final boolean treatNullValuesAsSimZero)
  {
    super( attr1, attr2, threshold);
    this.treatNullValuesAsSimZero = treatNullValuesAsSimZero;
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
   * de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( IObjectInstanceProvider oip1, IObjectInstanceProvider oip2, final IMappingStore mrs)
    throws MappingStoreException
  {
    // Wrapping für HighSpeed
    if( !oip1.hasCapability( EOIProviderCapabilityHint.MEMORY))
      oip1 = new MainMemoryFullyCachedObjectInstanceProvider( oip1);
    if( !oip2.hasCapability( EOIProviderCapabilityHint.MEMORY))
      oip2 = new MainMemoryFullyCachedObjectInstanceProvider( oip2);

    log.info("Start Map Value to OIs");
    int sizeOIP1 = 0;
    final ArrayListMultimap<Object, IObjectInstance> alm1 = ArrayListMultimap.create();
    for( final IObjectInstance oi : oip1)
    {
    	sizeOIP1++;
      final Object val = oi.getValue( attrLinks);
      alm1.put( val, oi);
    }
    log.info( "OIP1 Size: " + sizeOIP1);
    log.info( "ALM1:");
    log.info( "Key size:" + alm1.keySet().size());
    if( log.isDebugEnabled())
    {
      log.debug( "Keys:");
      for( final Object key : alm1.keySet())
      {
        log.debug( key);
      }
    }
    int sizeOIP2 = 0;
    final ArrayListMultimap<Object, IObjectInstance> alm2 = ArrayListMultimap.create();
    for( final IObjectInstance oi : oip2)
    {
    	sizeOIP2++;
      final Object val = oi.getValue( attrRechts);
      alm2.put( val, oi);
    }
    log.info( "OIP2 Size: " + sizeOIP2);
    log.info( "ALM2:");
    log.info( "Key size:" + alm2.keySet().size());
    if( log.isDebugEnabled())
    {
      log.debug( "Keys:");
      for( final Object key : alm2.keySet())
      {
        log.debug( key);
      }
    }

    log.info( "Match");
    
    final Map<Object, Collection<IObjectInstance>> map1 = alm1.asMap();
    for( final Entry<Object, Collection<IObjectInstance>> matchEntry : map1.entrySet())
    {
      final Object key = matchEntry.getKey();
      final Collection<IObjectInstance> match = matchEntry.getValue();
      if( match.size() == 0) continue;
      // Später
      if( key == null) continue;
      // Matchs
      for( final IObjectInstance one : match)
      {
        final List<IObjectInstance> matchesFromAlm2 = alm2.get( key);
        for( final IObjectInstance two : matchesFromAlm2)
        {
          mrs.add( one, two, new Similarity( 1));
        }
      }
      // NoMatchs
      if( treatNoMatchAsSimZero)
      {
        final Map<Object, Collection<IObjectInstance>> map2 = alm2.asMap();
        for( final Entry<Object, Collection<IObjectInstance>> match2Entry : map2.entrySet())
        {
          final Object key2 = match2Entry.getKey();
          final Collection<IObjectInstance> match2 = match2Entry.getValue();
          if( key2 == null || key.equals( key2)) continue;

          for( final IObjectInstance one : match)
          {
            for( final IObjectInstance two : match2)
            {
              mrs.add( one, two, new Similarity( 0));
            }
          }
        }
      }
    }

    if( treatNullValuesAsSimZero)
    {
      for( final IObjectInstance one : alm1.get( null))
      {
        for( final IObjectInstance two : oip2)
        {
          mrs.add( one, two, new Similarity( 0));
        }
      }
      for( final IObjectInstance one : oip1)
      {
        for( final IObjectInstance two : alm2.get( null))
        {
          mrs.add( one, two, new Similarity( 0));
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    // Wrapping für HighSpeed
    if( !oip.hasCapability( EOIProviderCapabilityHint.MEMORY))
      oip = new MainMemoryFullyCachedObjectInstanceProvider( oip);

    final ArrayListMultimap<Object, IObjectInstance> alm = ArrayListMultimap.create();
    for( final IObjectInstance oi : oip)
    {
      final Object val = oi.getValue( attrLinks);
      alm.put( val, oi);
    }

    final Map<Object, Collection<IObjectInstance>> map = alm.asMap();
    for( final Entry<Object, Collection<IObjectInstance>> matchEntry : map.entrySet())
    {
      final Object key = matchEntry.getKey();
      final Collection<IObjectInstance> match = matchEntry.getValue();
      if( match.size() == 0) continue;
      // NoMatch
      if( match.size() == 1)
      {
        final IObjectInstance one = ((ArrayList<IObjectInstance>) match).get( 0);
        if( key == null && treatNullValuesAsSimZero || key != null && treatNoMatchAsSimZero)
        {
          for( final IObjectInstance oi : oip)
          {
            mrs.add( one, oi, new Similarity( 0));
          }
          continue;
        }
        continue;
      }
      // Match
      final ArrayList<IObjectInstance> list = new ArrayList<IObjectInstance>( match);
      while( list.size() > 1)
      {
        final Iterator<IObjectInstance> it = list.iterator();
        final IObjectInstance one = it.next();
        it.remove();
        while( it.hasNext())
        {
          final IObjectInstance next = it.next();
          mrs.add( one, next, new Similarity( 1));
        }
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
    for( final IMappingEntry me : mp)
    {
      final Object left = me.getLeft().getValue( attrLinks);
      final Object right = me.getRight().getValue( attrRechts);

      if( left != null && right != null)
      {
        if( left.equals( right))
          mrs.add( me.getLeft(), me.getRight(), new Similarity( 1));
        else if( treatNoMatchAsSimZero) mrs.add( me.getLeft(), me.getRight(), new Similarity( 0));
      }
      else
      {
        if( treatNullValuesAsSimZero || treatNoMatchAsSimZero)
          mrs.add( me.getLeft(), me.getRight(), new Similarity( 0));
      }
    }
  }

  /**
   * @return the treatNoMatchAsSimZero
   */
  public boolean isTreatNoMatchAsSimZero()
  {
    return treatNoMatchAsSimZero;
  }

  /**
   * @param treatNoMatchAsSimZero
   *          the treatNoMatchAsSimZero to set
   */
  public void setTreatNoMatchAsSimZero( final boolean treatNoMatchAsSimZero)
  {
    this.treatNoMatchAsSimZero = treatNoMatchAsSimZero;
  }
}
