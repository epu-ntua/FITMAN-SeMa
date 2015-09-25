/**
 *
 */
package de.wdilab.ml.impl.matcher.composite.separation;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Multimap;

import de.wdilab.ml.impl.matcher.AbstractObjectMatcher;
import de.wdilab.ml.impl.oi.enhancement.persistence.MainMemoryFullyCachedObjectInstanceProvider;
import de.wdilab.ml.impl.oi.split.AttributeValueFilterObjectInstanceProvider;
import de.wdilab.ml.impl.oi.split.SplitAttributeValuePartitioner;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Date: 03.06.2010
 * 
 * @author Hanna Köpcke
 * @author Nico Heller
 */
public class SeparatingObjectMatcher extends AbstractObjectMatcher
{
  protected static final Logger            log = Logger.getLogger( SeparatingObjectMatcher.class);

  protected final Multimap<String, String> seperationSameMapping;

  protected final String                   seperationAttrLeft;

  protected final String                   seperationAttrRight;

  protected final IObjectMatcher           matcher;

  /**
   * @param matcher
   * @param seperationSameMapping
   * @param seperationAttrLeft
   * @param seperationAttrRight
   */
  public SeparatingObjectMatcher( final IObjectMatcher matcher, final Multimap<String, String> seperationSameMapping,
    final String seperationAttrLeft, final String seperationAttrRight)
  {
    this.matcher = matcher;
    this.seperationSameMapping = seperationSameMapping;
    this.seperationAttrLeft = seperationAttrLeft;
    this.seperationAttrRight = seperationAttrRight;
  }
  
  public SeparatingObjectMatcher( final IObjectMatcher matcher, 
		    final String seperationAttrLeft)
		  {
		    this.matcher = matcher;
		    this.seperationSameMapping = null;
		    this.seperationAttrLeft = seperationAttrLeft;
		    this.seperationAttrRight = seperationAttrLeft;
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
    // Seperation nach attrSameLeft und attrSameRight
    final SplitAttributeValuePartitioner savp = new SplitAttributeValuePartitioner( seperationAttrLeft);
    final List<AttributeValueFilterObjectInstanceProvider> leftParted = savp.split( oip1);

    savp.setAttr( seperationAttrRight);
    final Map<Object, AttributeValueFilterObjectInstanceProvider> rightParted = savp.splitToMap( oip2);

    // Left=product, right=offer
    // Seperation over the right seperationAttr
    for( final AttributeValueFilterObjectInstanceProvider avfLeftOIP : leftParted)
    {
      final IObjectInstanceProvider oipLeft = new MainMemoryFullyCachedObjectInstanceProvider( avfLeftOIP);
      final String splitValLeft = (String) avfLeftOIP.getValue();
      final Collection<String> splitValRight = seperationSameMapping.get( splitValLeft);
      // Equal things on right
      for( final String valToRight : splitValRight)
      {
        final AttributeValueFilterObjectInstanceProvider avfRightOIP = rightParted.get( valToRight);
        if( avfRightOIP == null)
        {
          log.warn( "No right OIP for: " + valToRight);
          continue;
        }
        final IObjectInstanceProvider oipRight = new MainMemoryFullyCachedObjectInstanceProvider( avfRightOIP);
        matcher.match( oipLeft, oipRight, mrs);
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
	  final SplitAttributeValuePartitioner savp = new SplitAttributeValuePartitioner( seperationAttrLeft);
	  final List<AttributeValueFilterObjectInstanceProvider> leftParted = savp.split( oip);
	  for( final AttributeValueFilterObjectInstanceProvider avfLeftOIP : leftParted)
	    {
		  final IObjectInstanceProvider oipLeft = new MainMemoryFullyCachedObjectInstanceProvider( avfLeftOIP);
		  matcher.match( oipLeft, mrs);
	    }

	  log.info("End");
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

  }

}
