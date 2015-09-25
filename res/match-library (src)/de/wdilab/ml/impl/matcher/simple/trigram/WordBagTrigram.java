/**
 * 
 */
package de.wdilab.ml.impl.matcher.simple.trigram;

import de.wdilab.ml.impl.util.GHashBag;
import de.wdilab.ml.interfaces.oi.IObjectInstance;

import static de.wdilab.ml.impl.util.string.NGramFunctions.*;

/**
 * @author Nico Heller
 */
public class WordBagTrigram
{
  final IObjectInstance  oi;

  final String           word;

  final GHashBag<String> trigrams;

  final int              hashCode;

  /**
   * @param word
   * @param oi
   */
  public WordBagTrigram( final String word, final IObjectInstance oi)
  {
    this.oi = oi;
    this.word = word;
    this.trigrams = generateTriGramHashBag( word != null ? word.toLowerCase() : word);

    if( word == null)
      hashCode = 0;
    else
      hashCode = word.hashCode();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    // final int prime = 31;
    // int result = 1;
    // result = prime * result + (word == null ? 0 : word.hashCode());
    // return result;

    return hashCode;
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
    final WordBagTrigram other = (WordBagTrigram) obj;
    if( word == null)
    {
      if( other.word != null) return false;
    }
    else
    {
      // Fast Fail
      if( word.hashCode() != other.word.hashCode()) return false;
      if( !word.equals( other.word)) return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append( "WordTrigram [");
    if( trigrams != null) builder.append( "trigrams=").append( trigrams).append( ", ");
    if( word != null) builder.append( "word=").append( word);
    builder.append( "]");
    return builder.toString();
  }

  /**
   * @param wbt2
   * @return sim
   */
  public float sim( final WordBagTrigram wbt2)
  {
    return computeTriGramSimilarity( trigrams, wbt2.trigrams);
  }
};
