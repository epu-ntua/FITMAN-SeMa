/**
 * 
 */
package de.wdilab.ml.impl.matcher;

import java.util.concurrent.atomic.AtomicInteger;

import de.wdilab.ml.interfaces.matcher.IMatcherConfiguration;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;

/**
 * An abstract ObjectMatcher provides the progress functionality.
 * Childs have to set <code>count</code> and increement <code>progress</code>
 * 
 * @author Nico Heller
 */
public abstract class AbstractObjectMatcher implements IObjectMatcher
{
  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IObjectMatcher#getProgress()
   */
  @Override
  public double getProgress()
  {
    if( count == 0) return 0;
    return (progress.get() - 1) / (double) count * 100;
  }

  /**
   * @return a name with parameters
   */
  public String getParamedName()
  {
    return getClass().getSimpleName();
  }

  public IMatcherConfiguration getConfiguration()
  {
    return new MatcherConfiguration( this);
  }
  
  @Override
  public int getNumberOfComparisons() {
	  return numberOfComparisons;
  }

  protected AtomicInteger progress = new AtomicInteger();

  protected int           count    = 0;
  protected int numberOfComparisons;
}
