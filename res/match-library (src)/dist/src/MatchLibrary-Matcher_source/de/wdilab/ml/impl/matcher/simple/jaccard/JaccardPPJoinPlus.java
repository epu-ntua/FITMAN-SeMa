package de.wdilab.ml.impl.matcher.simple.jaccard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import algorithms.Correspondence;
import algorithms.ppjoinplus.PPJoinPlus;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Jaccard matcher
 * 
 * @author kotlarz
 */
public class JaccardPPJoinPlus extends AbstractSimpleAttributeObjectMatcher implements IAttributeObjectMatcher
{

  protected static final Logger log = Logger.getLogger( JaccardPPJoinPlus.class);

  /**
   * @param attr1
   */
  public JaccardPPJoinPlus( final String attr1)
  {
    super( attr1);
  }

  /**
   * @param attr1
   * @param threshold
   */
  public JaccardPPJoinPlus( final String attr1, final float threshold)
  {
    super( attr1, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   */
  public JaccardPPJoinPlus( final String attr1, final String attr2)
  {
    super( attr1, attr2, 0f);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public JaccardPPJoinPlus( final String attr1, final String attr2, final float threshold)
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
    final ArrayList<String> ids = new ArrayList<String>();
    final ArrayList<String> values = new ArrayList<String>();
    int startIndex = 0;

    for( final IObjectInstance oiLinks : oip1)
    {
      ids.add( oiLinks.getId());
      String value = oiLinks.getStringValue( attrLinks);
      if (value == null)
    	  value = "";
      startIndex++;
    }

    for( final IObjectInstance oiRechts : oip2)
    {
      ids.add( oiRechts.getId());
      String value = oiRechts.getStringValue( attrRechts);
      if (value == null)
    	  value = "";
      values.add( value);
    }

    final String[] strings = new String[values.size()];
    values.toArray( strings);

    final LinkedList<Correspondence> result = PPJoinPlus.start( 'j', threshold, 2, strings);
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
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    final ArrayList<String> ids = new ArrayList<String>();
    final ArrayList<String> values = new ArrayList<String>();

    for( final IObjectInstance oiLinks : oip)
    {
      ids.add( oiLinks.getId());
      values.add( oiLinks.getStringValue( attrLinks));
    }

    final String[] strings = new String[values.size()];
    values.toArray( strings);

    final LinkedList<Correspondence> result = PPJoinPlus.start( 'j', threshold, 2, strings);

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
	  log.info("Match start");
		HashSet<IObjectInstance> oiLeft = new HashSet<IObjectInstance>(mp.getSize());
		HashSet<IObjectInstance> oiRight = new HashSet<IObjectInstance>(mp.getSize());
		HashSet<Integer> mids = new HashSet<Integer>();
		for (IMappingEntry e : mp) {
			oiLeft.add(e.getLeft());
			oiRight.add(e.getRight());
			mids.add(e.getLeft().getId().hashCode()
					+ e.getRight().getId().hashCode());
		}
		//
		final ArrayList<IObjectInstance> ids = new ArrayList<IObjectInstance>();
		final ArrayList<String> values = new ArrayList<String>();
		int startIndex = 0;
		//
		// final StopWatch sw = new StopWatch();
		//
		for (final IObjectInstance oiLinks : oiLeft) {
			ids.add(oiLinks);
			String value = oiLinks.getStringValue(attrLinks);
			if (value == null) {
				value = "";
			}
			values.add(value);
			startIndex++;
		}

		// final MainMemoryFullyCachedObjectInstanceProvider mp2 = new
		// MainMemoryFullyCachedObjectInstanceProvider( oip2);

		for (final IObjectInstance oiRechts : oiRight) {
			ids.add(oiRechts);
			String value = oiRechts.getStringValue(attrRechts);
			if (value == null) {
				value = "";
			}
			values.add(value);
		}

		final String[] strings = new String[values.size()];
		values.toArray(strings);

		log.info("end initialization");
		final LinkedList<Correspondence> result = PPJoinPlus.start('j',
				threshold, 2, strings);
		final Iterator<Correspondence> iter = result.iterator();
		while (iter.hasNext()) {
			final Correspondence c = iter.next();

			if (c.getFirstObject() < startIndex
					&& c.getSecondObject() >= startIndex) {
				IObjectInstance oiL = ids.get(c.getFirstObject());
				IObjectInstance oiR = ids.get(c.getSecondObject());
				if (mids.contains(oiL.getId().hashCode()
						+ oiR.getId().hashCode())) {
					mrs.add(oiL, oiR, new Similarity(c
							.getSimilarity()));
				}
			} else if (c.getFirstObject() >= startIndex
					&& c.getSecondObject() < startIndex) {
				IObjectInstance oiL = ids.get(c.getSecondObject());
				IObjectInstance oiR = ids.get(c.getFirstObject());
				if (mids.contains(oiL.getId().hashCode()
						+ oiR.getId().hashCode())) {
					mrs.add(oiL,oiR, new Similarity(c
							.getSimilarity()));
				}
			}/*
			 * else{ firstObject and secondObject in the same
			 * IObjectInstanceProvider }
			 */
		}
  }

}
