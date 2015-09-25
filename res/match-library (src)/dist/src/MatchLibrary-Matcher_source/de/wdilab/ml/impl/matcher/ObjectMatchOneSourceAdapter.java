/**
 * 
 */
package de.wdilab.ml.impl.matcher;

import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Redirects a one-provider-call to a two-provider-call
 * 
 * @author Nico Heller
 */
public abstract class ObjectMatchOneSourceAdapter implements IObjectMatcher
{
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
