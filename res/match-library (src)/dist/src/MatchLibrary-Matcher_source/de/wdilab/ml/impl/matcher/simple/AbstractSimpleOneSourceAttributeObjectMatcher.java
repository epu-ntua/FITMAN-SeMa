/**
 * 
 */
package de.wdilab.ml.impl.matcher.simple;

import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Redirects a one-provider-call to a two-provider-call.
 * Forms a frame for GETTERs/SETTERs of attribute names and threshold
 * 
 * @author Nico Heller
 */
public abstract class AbstractSimpleOneSourceAttributeObjectMatcher extends AbstractSimpleAttributeObjectMatcher
{

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public AbstractSimpleOneSourceAttributeObjectMatcher( final String attr1, final String attr2, final float threshold)
  {
    super( attr1, attr2, threshold);
  }

  /**
   * @param attr1
   * @param attr2
   */
  public AbstractSimpleOneSourceAttributeObjectMatcher( final String attr1, final String attr2)
  {
    super( attr1, attr2);
  }

  /**
   * @param attr1
   */
  public AbstractSimpleOneSourceAttributeObjectMatcher( final String attr1, final float threshold)
  {
    super( attr1, threshold);
  }

  /**
   * @param attr1
   */
  public AbstractSimpleOneSourceAttributeObjectMatcher( final String attr1)
  {
    super( attr1);
  }

  public AbstractSimpleOneSourceAttributeObjectMatcher() {
	super();
}

/*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  /**
   * match( oip, oip, mrs);
   * 
   * @throws MappingStoreException
   */
  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    match( oip, oip, mrs);
  }

}
