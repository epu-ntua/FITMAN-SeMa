/**
 * 
 */
package de.wdilab.ml.impl.matcher.simple.secondstring;

import com.wcohen.ss.AbstractStringDistance;
import com.wcohen.ss.api.StringWrapper;

import de.wdilab.ml.interfaces.oi.IObjectInstance;

/**
 * @author Nico Heller
 */
public class WordStringWrapper
{
  public IObjectInstance oi;

  public String          word;

  public StringWrapper   sw;

  /**
   * @param oi
   * @param word
   * @param asd
   */
  public WordStringWrapper( final IObjectInstance oi, final String word, final AbstractStringDistance asd)
  {
    this.word = word;
    this.oi = oi;
    this.sw = asd.prepare( word);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return oi.hashCode();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( final Object obj)
  {
    if( this == obj) return true;
    if( obj == null) return false;
    if( getClass() != obj.getClass()) return false;
    final WordStringWrapper other = (WordStringWrapper) obj;
    if( oi == null)
    {
      if( other.oi != null) return false;
    }
    else if( !oi.equals( other.oi)) return false;
    return true;
  }

}
