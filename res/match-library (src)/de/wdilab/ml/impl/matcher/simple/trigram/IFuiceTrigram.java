/**
 * 
 */
package de.wdilab.ml.impl.matcher.simple.trigram;

import java.util.ArrayList;
import java.util.Arrays;

import de.ifuice.utils.StringSimilarity;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Trigram matcher from iFuice.
 * <p>
 * Note: Does NOT keep the total amount of <code>IObjectInstance</code>s in main memory. Instead of
 * this it keeps the index->ID. ObjectInstanceProvider (OIP) should implement getInstance fastly!
 * hasCapability( EOIProviderCapabilityHint.FAST_GET_INSTANCE) should be TRUE.
 * </p>
 * 
 * @author Nico Heller
 */
public class IFuiceTrigram extends AbstractSimpleAttributeObjectMatcher implements IAttributeObjectMatcher
{
  /**
   * @param attr1
   * @param attr2
   */
  public IFuiceTrigram( final String attr1, final String attr2)
  {
    super( attr1, attr2, 0f);
  }

  /**
   * @param attr
   */
  public IFuiceTrigram( final String attr)
  {
    super( attr, 0f);
  }

  /**
   * 
   */
  public IFuiceTrigram()
  {
    super();
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   */
  public IFuiceTrigram( final String attr1, final String attr2, final float threshold)
  {
    super( attr1, attr2, threshold);
  }

  /**
   * @param attr
   * @param threshold
   */
  public IFuiceTrigram( final String attr, final float threshold)
  {
    super( attr, threshold);
  }

  /*
   * (non-Javadoc)
   * @see de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.oi.
   * IObjectInstanceProvider, de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
   * de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip1, final IObjectInstanceProvider oip2, final IMappingStore mrs)
    throws MappingStoreException
  {
    final ArrayList<String[]> leftnGrams = new ArrayList<String[]>();
    final ArrayList<int[]> leftnGramsId = new ArrayList<int[]>();
    final ArrayList<String> leftStrings = new ArrayList<String>();
    final ArrayList<String> leftOIs = new ArrayList<String>();

    String string_left;
    int left = 0;
    for( final IObjectInstance left_oi : oip1)
    {
      leftOIs.add( left_oi.getId());
      string_left = left_oi.getStringValue( attrLinks);
      if( string_left != null)
      {
        string_left = string_left.toUpperCase();
        leftStrings.add( string_left);
        final String trgs[] = StringSimilarity.generateNGrams( string_left, 3);
        if( trgs == null)
        {
          leftnGrams.add( NULL_TRGS);
          leftnGramsId.add( NULL_IDS);
        }
        else
        {
          leftnGrams.add( trgs);
          int[] leftnGramsIds = StringSimilarity.generateNGramId( trgs);
          Arrays.sort(leftnGramsIds);
          leftnGramsId.add(leftnGramsIds);
        }
      }
      else
      {
        leftStrings.add( NULL);
        leftnGrams.add( NULL_TRGS);
        leftnGramsId.add( NULL_IDS);
      }

      left++;
    }

    final ArrayList<String[]> rightnGrams = new ArrayList<String[]>();
    final ArrayList<int[]> rightnGramsId = new ArrayList<int[]>();
    final ArrayList<String> rightStrings = new ArrayList<String>();
    final ArrayList<String> rightOIs = new ArrayList<String>();

    String string_right;
    int right = 0;
    for( final IObjectInstance right_oi : oip2)
    {
      rightOIs.add( right_oi.getId());
      string_right = right_oi.getStringValue( attrRechts);
      if( string_right != null)
      {
        string_right = string_right.toUpperCase();
        rightStrings.add( string_right);
        final String trgs[] = StringSimilarity.generateNGrams( string_right, 3);
        if( trgs == null)
        {
          rightnGrams.add( NULL_TRGS);
          rightnGramsId.add( NULL_IDS);
        }
        else
        {
          rightnGrams.add( trgs);
          int[] rightnGramsIds = StringSimilarity.generateNGramId( trgs);
          Arrays.sort(rightnGramsIds);
          rightnGramsId.add(rightnGramsIds);
          
        }
      }
      else
      {
        rightStrings.add( NULL);
        rightnGrams.add( NULL_TRGS);
        rightnGramsId.add( NULL_IDS);
      }

      right++;
    }

    final int inputSize = leftStrings.size();
    final int outputSize = rightStrings.size();
    for( left = 0; left < inputSize; left++)
    {
      final String left_string = leftStrings.get( left);
      if( left_string == NULL) continue;

      final int[] leftnGramsIds = leftnGramsId.get( left);
      final String keyLeft = leftOIs.get( left);
      final IObjectInstance oiLeft = oip1.getInstance( keyLeft);

      for( right = 0; right < outputSize; right++)
      {
        final String right_string = rightStrings.get( right);
        if( right_string == NULL) continue;

        final float sim =
        	this.computeSortedNGramSimilarity(leftnGramsIds,
            rightnGramsId.get( right));
        this.numberOfComparisons++;
        final String keyRight = rightOIs.get( right);
        if( sim >= threshold)
        {
          mrs.add( oiLeft, oip2.getInstance( keyRight), new Similarity( sim));
        }
      }
    }
  }

  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
	  this.numberOfComparisons = 0;
    final ArrayList<String[]> leftnGrams = new ArrayList<String[]>();
    final ArrayList<int[]> leftnGramsId = new ArrayList<int[]>();
    final ArrayList<String> leftStrings = new ArrayList<String>();
    final ArrayList<String> leftOIs = new ArrayList<String>();

    String string_left;
    int left = 0;
    for( final IObjectInstance left_oi : oip)
    {
      leftOIs.add( left_oi.getId());
      string_left = left_oi.getStringValue( attrLinks);
      if( string_left != null)
      {
        string_left = string_left.toUpperCase();
        leftStrings.add( string_left);
        final String trgs[] = StringSimilarity.generateNGrams( string_left, 3);
        if( trgs == null)
        {
          leftnGrams.add( NULL_TRGS);
          leftnGramsId.add( NULL_IDS);
        }
        else
        {
          leftnGrams.add( trgs);
          int[] leftnGramsIds = StringSimilarity.generateNGramId( trgs);
          Arrays.sort(leftnGramsIds);
          leftnGramsId.add(leftnGramsIds);
        }
      }
      else
      {
        leftStrings.add( NULL);
        leftnGrams.add( NULL_TRGS);
        leftnGramsId.add( NULL_IDS);
      }

      left++;
    }

    final int leftSize = leftStrings.size();
    count = leftSize;
    for( left = 0; left < leftSize - 1; left++)
    {
      progress.incrementAndGet();
      final String left_string = leftStrings.get( left);
      if( left_string == NULL) continue;

      final int[] leftnGramsIds = leftnGramsId.get( left);
      final String keyLeft = leftOIs.get( left);
      final IObjectInstance oiLeft = oip.getInstance( keyLeft);

      for( int right = left + 1; right < leftSize; right++)
      {
    	  
        final String right_string = leftStrings.get( right);
        if( right_string == NULL) continue;
        
        final float sim =
          this.computeSortedNGramSimilarity(leftnGramsIds,
            leftnGramsId.get( right));
        this.numberOfComparisons++;
        final String keyRight = leftOIs.get( right);
        this.numberOfComparisons++;
        if( sim >= threshold) mrs.add( oiLeft, oip.getInstance( keyRight), new Similarity( sim));
      }
    }
  }

  /**
   * 
   */
  public static final String NGRAM_KEY = "ifuice_ngram_key";

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.mapping.
   * IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException
  {
    for( final IMappingEntry me : mp)
    {
      // Left
      final IObjectInstance left_oi = me.getLeft();
      String[] trgs_left = (String[]) left_oi.getProgramDataValue( NGRAM_KEY);

      if( trgs_left == null)
      {
        String string_left = left_oi.getStringValue( attrLinks);
        if( string_left != null)
        {
          string_left = string_left.toUpperCase();
          trgs_left = StringSimilarity.generateNGrams( string_left, 3);

          if( trgs_left == null) trgs_left = NULL_TRGS;
        }
        else
        {
          trgs_left = NULL_TRGS;
        }
        left_oi.putProgramDataValue( NGRAM_KEY, trgs_left);
      }

      // Right
      final IObjectInstance right_oi = me.getRight();
      String[] trgs_right = (String[]) right_oi.getProgramDataValue( NGRAM_KEY);
      if( trgs_right == null)
      {
        String string_right = right_oi.getStringValue( attrRechts);
        if( string_right != null)
        {
          string_right = string_right.toUpperCase();
          trgs_right = StringSimilarity.generateNGrams( string_right, 3);

          if( trgs_right == null) trgs_right = NULL_TRGS;
        }
        else
        {
          trgs_right = NULL_TRGS;
        }
        right_oi.putProgramDataValue( NGRAM_KEY, trgs_right);
      }

      float sim = 0;
      if( trgs_left != NULL_TRGS && trgs_right != NULL_TRGS) {
        sim = StringSimilarity.computeNGramSimilarity( null, null, 3, trgs_left, trgs_right);

        this.numberOfComparisons++;
      }
      if( sim >= threshold)
      {
        mrs.add( left_oi, right_oi, new Similarity( sim));
      }
    }
  }

  private float computeSortedNGramSimilarity(int[] gramsId1, int[] gramsId2) {
      // merge-sort-aehnlicher Vergleich der Trigramlisten fuer
      // Overlapberechnung (Dice)
	  if (gramsId1.length == 0 || gramsId2.length == 0)
		  return 0;
      int count = 0;
      int left = 0, right = 0;
      while (left < gramsId1.length && right < gramsId2.length) {
          if (gramsId1[left] == gramsId2[right]) {
              count++;
              left++;
          } else if (gramsId1[left] < gramsId2[right]) {
              left++;
          } else {
              right++;
          }
      }
      if (right == gramsId2.length) {
          left++;
          while (left < gramsId1.length - 1) {
              if (gramsId1[left] == gramsId2[gramsId2.length - 1]) {
                  count++;
              }
              left++;
          }
      }
      if (left == gramsId1.length) {
          right++;
          while (right < gramsId2.length) {
              if (gramsId2[right] == gramsId1[gramsId1.length - 1]) {
                  count++;
              }
              right++;
          }
      }

      float sim = (float) 2 * count / (gramsId1.length + gramsId2.length); // Dice-Coefficient
      return sim;
  } 
  /**
   * A <code>NULL</code>-String
   */
  public static final String      NULL      = "<NULL>";

  protected static final String[] NULL_TRGS = new String[0];

  protected static final int[]    NULL_IDS  = new int[0];
}
