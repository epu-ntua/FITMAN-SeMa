/**
 * (C) Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.simple;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.AbstractObjectMatcher;
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
public class CrossProductMatcher extends AbstractObjectMatcher
{
  final protected static ISimilarity SIM_ONE = new Similarity( 1);

  /**
   * 
   */
  public CrossProductMatcher()
  {
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
    for( final IObjectInstance oiLeft : oip1)
    {
      for( final IObjectInstance oiRight : oip2)
      {
        mrs.add( oiLeft, oiRight, SIM_ONE);
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
    match( oip, oip, mrs);
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
    throw new UnsupportedOperationException( "TODO");
  }

}
