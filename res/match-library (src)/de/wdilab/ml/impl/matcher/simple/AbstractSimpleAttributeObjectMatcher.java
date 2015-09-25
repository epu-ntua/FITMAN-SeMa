/**
 * Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.simple;

import de.wdilab.ml.impl.matcher.AbstractThresholdedObjectMatcher;
import de.wdilab.ml.impl.matcher.MatcherConfiguration;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.matcher.IMatcherConfiguration;

/**
 * Forms a frame for GETTERs/SETTERs of attribute names and threshold
 * 
 * @author Nico Heller
 */
public abstract class AbstractSimpleAttributeObjectMatcher extends AbstractThresholdedObjectMatcher implements
    IAttributeObjectMatcher
{
  protected String attrLinks;

  protected String attrRechts;
  
  protected int numberOfComparisons;

  /**
   * @param attr1
   * @param attr2
   */
  public AbstractSimpleAttributeObjectMatcher( final String attr1, final String attr2)
  {
    this( attr1, attr2, 0f);
  }

  /**
   * @param attr1
   */
  public AbstractSimpleAttributeObjectMatcher( final String attr1)
  {
    this( attr1, attr1, 0f);
  }

  /**
   * 
   */
  public AbstractSimpleAttributeObjectMatcher()
  {
    this( null, null, 0f);
  }

  /**
   * @param attr1
   * @param threshold
   */
  public AbstractSimpleAttributeObjectMatcher( final String attr1, final float threshold)
  {
    this( attr1, attr1, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public AbstractSimpleAttributeObjectMatcher( final String attr1, final String attr2, final float threshold)
  {
    super( threshold);
    this.attrLinks = attr1;
    this.attrRechts = attr2;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher#getAttributeNameLeft()
   */
  @Override
  public String getAttributeNameLeft()
  {
    return attrLinks;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher#getAttributeNameRight()
   */
  @Override
  public String getAttributeNameRight()
  {
    return attrRechts;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher#setAttributeNameLeft()
   */
  @Override
  public void setAttributeNameLeft( final String attrLinks)
  {
    this.attrLinks = attrLinks;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher#setAttributeNameRight()
   */
  @Override
  public void setAttributeNameRight( final String attrRechts)
  {
    this.attrRechts = attrRechts;
  }

  @Override
  public void setAttributeName( final String attrName)
  {
    this.attrLinks = attrName;
    this.attrRechts = attrName;
  }

  @Override
  public IMatcherConfiguration getConfiguration()
  {
    return new MatcherConfiguration( this);
  }
  @Override
  public int getNumberOfComparisons() {
	  return numberOfComparisons;
  }
}
