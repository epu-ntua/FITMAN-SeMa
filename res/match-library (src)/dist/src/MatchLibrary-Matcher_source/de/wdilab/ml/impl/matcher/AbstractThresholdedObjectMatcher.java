/**
 * 
 */
package de.wdilab.ml.impl.matcher;

import java.util.concurrent.atomic.AtomicInteger;

import de.wdilab.ml.interfaces.matcher.IMatcherConfiguration;
import de.wdilab.ml.interfaces.matcher.IThresholdedObjectMatcher;

/**
 * An abstract ObjectMatcher provides the progress functionality.
 * Childs have to set <code>count</code> and increement <code>progress</code>
 * 
 * @author Nico Heller
 */
public abstract class AbstractThresholdedObjectMatcher extends AbstractObjectMatcher implements
    IThresholdedObjectMatcher
{
  protected float threshold;

  /**
   * @param threshold
   */
  public AbstractThresholdedObjectMatcher( final float threshold)
  {
    this.threshold = threshold;
  }

  /**
   * Default threshold of 0f
   */
  public AbstractThresholdedObjectMatcher()
  {
    this.threshold = 0f;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IThresholdedObjectMatcher#getThreshold()
   */
  @Override
  public float getThreshold()
  {
    return threshold;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IThresholdedObjectMatcher#setThreshold()
   */
  @Override
  public void setThreshold( final float threshold)
  {
    this.threshold = threshold;
  }

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

  @Override
  public IMatcherConfiguration getConfiguration()
  {
    return new MatcherConfiguration( this);
  }

  protected AtomicInteger progress = new AtomicInteger();

  protected int           count    = 0;
}
