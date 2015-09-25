package de.wdilab.ml.impl.matcher.simple.levenshtein;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import algorithms.Correspondence;
import algorithms.edjoin.EdJoin;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.impl.matcher.simple.jaccard.JaccardPPJoinPlus;
import de.wdilab.ml.impl.oi.enhancement.persistence.MainMemoryFullyCachedObjectInstanceProvider;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.EOIProviderCapabilityHint;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * @author kotlarz
 */
public class EDJoin extends AbstractSimpleAttributeObjectMatcher implements IAttributeObjectMatcher
{

  protected static final Logger log = Logger.getLogger( JaccardPPJoinPlus.class);

  private int                   q   = 5;

  private int                   tau = 1;

  /**
   * 
   */
  public EDJoin()
  {
    super();
  }

  /**
   * @param q
   * @param tau
   */
  public EDJoin( final int q, final int tau)
  {
    super();
    this.q = q;
    this.tau = tau;
  }

  /**
   * @param attr1
   */
  public EDJoin( final String attr1)
  {
    super( attr1);
  }

  /**
   * @param attr1
   * @param threshold
   * @param q
   */
  public EDJoin( final String attr1, final float threshold, final int q)
  {
    super( attr1, threshold);
    this.q = q;
  }

  /**
   * @param attr1
   * @param attr2
   */
  public EDJoin( final String attr1, final String attr2)
  {
    super( attr1, attr2, 0f);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   * @param q
   */
  public EDJoin( final String attr1, final String attr2, final float threshold, final int q)
  {
    super( attr1, attr2, threshold);
    this.q = q;
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
    log.info( "Match Start.");

    IObjectInstanceProvider mmp1;
    // Wrapping für HighSpeed getInsatnce
    if( oip1.hasCapability( EOIProviderCapabilityHint.MEMORY))
      mmp1 = oip1;
    else
      mmp1 = new MainMemoryFullyCachedObjectInstanceProvider( oip1);

    final ArrayList<String> ids = new ArrayList<String>();
    final ArrayList<String> values = new ArrayList<String>();
    int startIndex = 0;

    for( final IObjectInstance oiLinks : mmp1)
    {
      ids.add( oiLinks.getId());
      String value = "";
      if (oiLinks.getStringValue( attrRechts) != null) {
    	  value = oiLinks.getStringValue( attrLinks);
      }
      values.add( value);;
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
      String value = "";
      if (oiRechts.getStringValue( attrRechts) != null) {
    	  value = oiRechts.getStringValue( attrRechts);
      }
      values.add( value);
    }

    final String[] strings = new String[values.size()];
    values.toArray( strings);

    final LinkedList<Correspondence> result = EdJoin.start( Math.round( threshold), q, strings);
    final Iterator<Correspondence> iter = result.iterator();

    while( iter.hasNext())
    {
      final Correspondence c = iter.next();
      String attr1 = values.get( c.getFirstObject());
      if( attr1 == null) attr1 = "";
      String attr2 = values.get( c.getSecondObject());
      if( attr2 == null) attr2 = "";
      final double sim = 1 - c.getSimilarity() / Math.max( attr1.length(), attr2.length());
      if( sim >= threshold)
      {
        if( c.getFirstObject() < startIndex && c.getSecondObject() >= startIndex)
        {
          mrs.add( mmp1.getInstance( ids.get( c.getFirstObject())), mmp2.getInstance( ids.get( c.getSecondObject())),
            new Similarity( sim));
        }
        else if( c.getFirstObject() >= startIndex && c.getSecondObject() < startIndex)
        {
          mrs.add( mmp1.getInstance( ids.get( c.getSecondObject())), mmp2.getInstance( ids.get( c.getFirstObject())),
            new Similarity( sim));
        }
        /*
         * else{
         * firstObject and secondObject in the same IObjectInstanceProvider
         * }
         */
      }
    }

    log.info( "Match Ended.");
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

    for( final IObjectInstance oiLinks : mmp)
    {
      ids.add( oiLinks.getId());
      values.add( oiLinks.getStringValue( attrLinks));
    }

    final String[] strings = new String[values.size()];
    values.toArray( strings);

    final LinkedList<Correspondence> result = EdJoin.start( tau, q, strings);

    for( final Correspondence c : result)
    {
      String attr1 = values.get( c.getFirstObject());
      if( attr1 == null) attr1 = "";
      String attr2 = values.get( c.getSecondObject());
      if( attr2 == null) attr2 = "";
      final double sim = 1 - c.getSimilarity() / Math.max( attr1.length(), attr2.length());
      if( sim >= threshold)
        mrs.add( mmp.getInstance( ids.get( c.getFirstObject())), mmp.getInstance( ids.get( c.getSecondObject())),
          new Similarity( sim));
    }

    log.info( "Match Ended.");
  }

  /**
   * @return q
   */
  public int getQ()
  {
    return q;
  }

  /**
   * @return tau
   */
  public int getTau()
  {
    return tau;
  }

  /**
   * @param tau
   */
  public void setTau( final int tau)
  {
    this.tau = tau;
  }

  /**
   * @param q
   */
  public void setQ( final int q)
  {
    this.q = q;
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

	    HashSet<IObjectInstance> oiLeft = new HashSet<IObjectInstance>(mp.getSize());
		HashSet<IObjectInstance> oiRight = new HashSet<IObjectInstance>(mp.getSize());
		HashSet<Integer> mids = new HashSet<Integer>();
		for (IMappingEntry e : mp) {
			oiLeft.add(e.getLeft());
			oiRight.add(e.getRight());
			mids.add(e.getLeft().getId().hashCode()
					+ e.getRight().getId().hashCode());
		}

	    final ArrayList<IObjectInstance> ids = new ArrayList<IObjectInstance>();
	    final ArrayList<String> values = new ArrayList<String>();
	    int startIndex = 0;

	    for( final IObjectInstance oiLinks : oiLeft)
	    {
	      ids.add( oiLinks);
	      String value = "";
	      if (oiLinks.getStringValue( attrRechts) != null) {
	    	  value = oiLinks.getStringValue( attrLinks);
	      }
	      values.add( value);;
	    }

	    

	    for( final IObjectInstance oiRechts : oiRight)
	    {
	      ids.add( oiRechts);
	      String value = "";
	      if (oiRechts.getStringValue( attrRechts) != null) {
	    	  value = oiRechts.getStringValue( attrRechts);
	      }
	      values.add( value);
	    }

	    final String[] strings = new String[values.size()];
	    values.toArray( strings);

	    final LinkedList<Correspondence> result = EdJoin.start( Math.round( threshold), q, strings);
	    final Iterator<Correspondence> iter = result.iterator();

	    while( iter.hasNext())
	    {
	      final Correspondence c = iter.next();
	      String attr1 = values.get( c.getFirstObject());
	      if( attr1 == null) attr1 = "";
	      String attr2 = values.get( c.getSecondObject());
	      if( attr2 == null) attr2 = "";
	      final double sim = 1 - c.getSimilarity() / Math.max( attr1.length(), attr2.length());
	      if( sim >= threshold)
	      {
	        if( c.getFirstObject() < startIndex && c.getSecondObject() >= startIndex)
	        {
	        	IObjectInstance oiL = ids.get(c.getFirstObject());
				IObjectInstance oiR = ids.get(c.getSecondObject());
				if (mids.contains(oiL.getId().hashCode()
						+ oiR.getId().hashCode())) {
					mrs.add(oiL, oiR, new Similarity(c
							.getSimilarity()));
				}
	        }
	        else if( c.getFirstObject() >= startIndex && c.getSecondObject() < startIndex)
	        {
	        	IObjectInstance oiL = ids.get(c.getSecondObject());
				IObjectInstance oiR = ids.get(c.getFirstObject());
				if (mids.contains(oiL.getId().hashCode()
						+ oiR.getId().hashCode())) {
					mrs.add(oiL, oiR, new Similarity(c
							.getSimilarity()));
				}
	        }
	        /*
	         * else{
	         * firstObject and secondObject in the same IObjectInstanceProvider
	         * }
	         */
	      }
	    }

	    log.info( "Match Ended.");
  }

  @Override
  public String getParamedName()
  {
    return "EDJoin q=" + getQ() + "; tau=" + getTau();
  }
}
