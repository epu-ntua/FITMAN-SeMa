/**
 * (C) Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.blocking.sortedneighborhood;

import org.apache.commons.lang.StringUtils;

import de.wdilab.ml.interfaces.oi.IObjectInstance;

/**
 * Date: 18.05.2010
 * 
 * @author Nico Heller
 */
public class AttributeKeyGenerator extends KeyGenComparator
{
  private final String[] attrs;

  private final int      len;

  /**
   * @param len
   * @param attrs
   */
  public AttributeKeyGenerator( final int len, final String... attrs)
  {
    this.len = len;
    this.attrs = attrs;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.wdilab.ml.impl.matcher.blocking.sortedneighborhood.KeyGenComparator#makeKey(de.wdilab.ml
   * .interfaces.oi.IObjectInstance)
   */
  @Override
  public String makeKey( final IObjectInstance oi)
  {
    final StringBuilder sb = new StringBuilder();
    Object val;
    for( final String attr : attrs)
    {
      val = oi.getValue( attr);
      if( val == null)
        sb.append( StringUtils.leftPad( "", len, '_'));
      else
        sb.append( StringUtils.leftPad( StringUtils.left( val.toString().toLowerCase(), len), len, '_'));
    }
    return sb.toString();
  }

  /**
   * @return Attrs
   */
  public String[] getAttrs()
  {
    return attrs;
  }

  /**
   * @return length of the key
   */
  public int getLen()
  {
    return len;
  }
}
