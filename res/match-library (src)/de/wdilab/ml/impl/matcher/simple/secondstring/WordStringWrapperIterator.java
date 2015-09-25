package de.wdilab.ml.impl.matcher.simple.secondstring;

import java.util.Iterator;

import com.wcohen.ss.api.StringWrapper;
import com.wcohen.ss.api.StringWrapperIterator;

/**
 * A simple StringWrapperIterator implementation.
 * 
 * @author Nico Heller
 */
public class WordStringWrapperIterator implements StringWrapperIterator
{
  private final Iterator<WordStringWrapper> myIterator;

  /**
   * @param i
   */
  public WordStringWrapperIterator( final Iterator<WordStringWrapper> i)
  {
    myIterator = i;
  }

  public boolean hasNext()
  {
    return myIterator.hasNext();
  }

  public Object next()
  {
    return myIterator.next().sw;
  }

  public StringWrapper nextStringWrapper()
  {
    return (StringWrapper) next();
  }

  public void remove()
  {
    myIterator.remove();
  }
}
