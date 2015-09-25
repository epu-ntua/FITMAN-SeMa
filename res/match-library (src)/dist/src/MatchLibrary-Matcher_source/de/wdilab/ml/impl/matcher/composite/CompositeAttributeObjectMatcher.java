/**
 * (C) Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.composite;

import org.apache.log4j.Logger;

import de.wdilab.ml.impl.mapping.mainmemory.MainMemoryMapping;
import de.wdilab.ml.impl.matcher.AbstractObjectMatcher;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;
import de.wdilab.ml.interfaces.matcher.IThresholdedObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Builds a matcher cascade.
 * <p>
 * Date: 19.05.2010
 * </p>
 * 
 * @author Nico Heller
 */
public class CompositeAttributeObjectMatcher extends AbstractSimpleAttributeObjectMatcher
{
  protected static final Logger log = Logger.getLogger( CompositeAttributeObjectMatcher.class);

  final IObjectMatcher[]        matchers;

  /**
   * @param attr1
   * @param attr2
   * @param matchers
   */
  public CompositeAttributeObjectMatcher( final String attr1, final String attr2, final IObjectMatcher... matchers)
  {
    super( attr1, attr2);
    this.matchers = matchers;
  }

  /**
   * @param attr1
   * @param matchers
   */
  public CompositeAttributeObjectMatcher( final String attr1, final IObjectMatcher... matchers)
  {
    super( attr1);
    this.matchers = matchers;
  }

  /**
   * @param matchers
   */
  public CompositeAttributeObjectMatcher( final IObjectMatcher... matchers)
  {
    this.matchers = matchers;
  }

  /**
   * @param attr1
   * @param threshold
   * @param matchers
   */
  public CompositeAttributeObjectMatcher( final String attr1, final float threshold, final IObjectMatcher... matchers)
  {
    super( attr1, threshold);
    this.matchers = matchers;
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   * @param matchers
   */
  public CompositeAttributeObjectMatcher( final String attr1, final String attr2, final float threshold,
    final IObjectMatcher... matchers)
  {
    super( attr1, attr2, threshold);
    this.matchers = matchers;
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
    final int cntMatchers = matchers.length - 1;

    // First Match
    IObjectMatcher matcher = matchers[0];
    MainMemoryMapping mmm = new MainMemoryMapping();
    matcher.match( oip1, oip2, mmm);

    log.info( "First pass size: " + mmm.getSize());

    // Middle
    for( int i = 1; i < cntMatchers; i++)
    {
      final MainMemoryMapping mmmTempResult = new MainMemoryMapping();
      matcher = matchers[i];
      matcher.match( mmm, mmmTempResult);
      mmm = mmmTempResult;
    }

    // Last
    matcher = matchers[cntMatchers];
    matcher.match( mmm, mrs);
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    final int cntMatchers = matchers.length - 1;

    // First Match
    IObjectMatcher matcher = matchers[0];
    MainMemoryMapping mmm = new MainMemoryMapping();
    matcher.match( oip, mmm);

    log.info( "First pass size: " + mmm.getSize());

    // Middle
    for( int i = 1; i < cntMatchers; i++)
    {
      final MainMemoryMapping mmmTempResult = new MainMemoryMapping();
      matcher = matchers[i];
      matcher.match( mmm, mmmTempResult);
      mmm = mmmTempResult;
    }

    // Last
    if( cntMatchers > 1)
    {
      matcher = matchers[cntMatchers];
      matcher.match( mmm, mrs);
    }
    else
    {
      for( IMappingEntry me : mmm)
      {
        mrs.add( me);
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
    final int cntMatchers = matchers.length - 1;

    // First Match
    IObjectMatcher matcher = matchers[0];
    MainMemoryMapping mmm = new MainMemoryMapping();
    matcher.match( mp, mmm);

    log.info( "First pass size: " + mmm.getSize());

    // Middle
    for( int i = 1; i < cntMatchers; i++)
    {
      final MainMemoryMapping mmmTempResult = new MainMemoryMapping();
      matcher = matchers[i];
      matcher.match( mmm, mmmTempResult);
      mmm = mmmTempResult;
    }

    // Last
    matcher = matchers[cntMatchers];
    matcher.match( mmm, mrs);
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher#setAttributeNameLeft()
   */
  @Override
  public void setAttributeNameLeft( final String attrLinks)
  {
    super.setAttributeNameLeft( attrLinks);

    for( final IObjectMatcher matcher : matchers)
    {
      if( matcher instanceof IAttributeObjectMatcher)
      {
        final IAttributeObjectMatcher aom = (IAttributeObjectMatcher) matcher;
        aom.setAttributeNameLeft( attrLinks);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher#setAttributeNameRight()
   */
  @Override
  public void setAttributeNameRight( final String attrRechts)
  {
    super.setAttributeNameRight( attrRechts);

    for( final IObjectMatcher matcher : matchers)
    {
      if( matcher instanceof IAttributeObjectMatcher)
      {
        final IAttributeObjectMatcher aom = (IAttributeObjectMatcher) matcher;
        aom.setAttributeNameRight( attrRechts);
      }
    }
  }

  @Override
  public void setAttributeName( final String attrName)
  {
    super.setAttributeName( attrName);

    for( final IObjectMatcher matcher : matchers)
    {
      if( matcher instanceof IAttributeObjectMatcher)
      {
        final IAttributeObjectMatcher aom = (IAttributeObjectMatcher) matcher;
        aom.setAttributeName( attrName);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IThresholdedObjectMatcher#setThreshold()
   */
  @Override
  public void setThreshold( final float threshold)
  {
    super.setThreshold( threshold);

    for( final IObjectMatcher matcher : matchers)
    {
      if( matcher instanceof IThresholdedObjectMatcher)
      {
        final IThresholdedObjectMatcher tom = (IThresholdedObjectMatcher) matcher;
        tom.setThreshold( threshold);
      }
    }
  }

  /**
   * @return composed name
   */
  public String getCompositeMatcherNames()
  {
    final StringBuilder sb = new StringBuilder();

    for( final IObjectMatcher matcher : matchers)
    {
      if( matcher instanceof AbstractObjectMatcher)
        sb.append( ((AbstractObjectMatcher) matcher).getParamedName()).append( "->");
      else
        sb.append( matcher.getClass().getSimpleName()).append( "->");
    }
    return sb.toString();
  }

  @Override
  public String getParamedName()
  {
    return getCompositeMatcherNames();
  }
}
