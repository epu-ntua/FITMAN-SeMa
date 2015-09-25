/**
 * Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher;

import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.matcher.IMatcherConfiguration;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;
import de.wdilab.ml.interfaces.matcher.IThresholdedObjectMatcher;

/**
 * Date: 04.08.2010
 * 
 * @author Nico Heller
 */
public class MatcherConfiguration implements IMatcherConfiguration
{
  private static final long                      serialVersionUID = 1L;

  private String                                 userName         = null;

  private final Class< ? extends IObjectMatcher> matcherClass;

  private final Float                            threshold;

  private final String                           rightAttributeName;

  private final String                           leftAttributeName;

  /**
   * @param matcher
   */
  public MatcherConfiguration( final IAttributeObjectMatcher matcher)
  {
    this.matcherClass = matcher.getClass();
    this.threshold = matcher.getThreshold();
    this.rightAttributeName = matcher.getAttributeNameRight();
    this.leftAttributeName = matcher.getAttributeNameLeft();
  }

  /**
   * @param matcher
   */
  public MatcherConfiguration( final IThresholdedObjectMatcher matcher)
  {
    this.matcherClass = matcher.getClass();
    this.threshold = matcher.getThreshold();
    this.rightAttributeName = null;
    this.leftAttributeName = null;
  }

  /**
   * @param matcher
   */
  public MatcherConfiguration( final IObjectMatcher matcher)
  {
    this.matcherClass = matcher.getClass();
    this.threshold = null;
    this.rightAttributeName = null;
    this.leftAttributeName = null;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IMatcherConfiguration#getMatcherClass()
   */
  @Override
  public Class< ? extends IObjectMatcher> getMatcherClass()
  {
    return matcherClass;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IMatcherConfiguration#getThreshold()
   */
  @Override
  public Float getThreshold()
  {
    return threshold;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IMatcherConfiguration#getLeftAttributeName()
   */
  @Override
  public String getLeftAttributeName()
  {
    return leftAttributeName;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IMatcherConfiguration#getRightAttributeName()
   */
  @Override
  public String getRightAttributeName()
  {
    return rightAttributeName;
  }

  /**
   * @param userName
   *          the userName to set
   */
  public void setUserName( final String userName)
  {
    this.userName = userName;
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IMatcherConfiguration#getName()
   */
  @Override
  public String getName()
  {
    return userName == null ? toString() : userName;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append( "MatcherConfiguration [");
    if( userName != null) builder.append( "userName=").append( userName).append( ", ");
    if( matcherClass != null) builder.append( "matcherClass=").append( matcherClass).append( ", ");
    if( threshold != null) builder.append( "threshold=").append( threshold).append( ", ");
    if( rightAttributeName != null) builder.append( "rightAttributeName=").append( rightAttributeName).append( ", ");
    if( leftAttributeName != null) builder.append( "leftAttributeName=").append( leftAttributeName);
    builder.append( "]");
    return builder.toString();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (leftAttributeName == null ? 0 : leftAttributeName.hashCode());
    result = prime * result + (matcherClass == null ? 0 : matcherClass.hashCode());
    result = prime * result + (rightAttributeName == null ? 0 : rightAttributeName.hashCode());
    result = prime * result + (threshold == null ? 0 : threshold.hashCode());
    result = prime * result + (userName == null ? 0 : userName.hashCode());
    return result;
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
    final MatcherConfiguration other = (MatcherConfiguration) obj;
    if( leftAttributeName == null)
    {
      if( other.leftAttributeName != null) return false;
    }
    else if( !leftAttributeName.equals( other.leftAttributeName)) return false;
    if( matcherClass == null)
    {
      if( other.matcherClass != null) return false;
    }
    else if( !matcherClass.equals( other.matcherClass)) return false;
    if( rightAttributeName == null)
    {
      if( other.rightAttributeName != null) return false;
    }
    else if( !rightAttributeName.equals( other.rightAttributeName)) return false;
    if( threshold == null)
    {
      if( other.threshold != null) return false;
    }
    else if( !threshold.equals( other.threshold)) return false;
    if( userName == null)
    {
      if( other.userName != null) return false;
    }
    else if( !userName.equals( other.userName)) return false;
    return true;
  }
}
