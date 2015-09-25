/**
 * 
 */
package de.wdilab.ml.impl.matcher.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import de.wdilab.ml.impl.mapping.mainmemory.StatisticOnlyMapping;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStoreProvider;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.EmptySimilarityFunction;

/**
 * @author Nico Heller
 */
public class Measure
{
  protected static final Logger log = Logger.getLogger( Measure.class);

  /**
   * @param som
   */
  public Measure( final IMappingProvider perfect, final StatisticOnlyMapping som)
  {
    this( perfect.getSize(), som.getCntToTest(), som.getCntRightCorrect(), som.getCntWrong(), som
      .getCntMissing());
  }

  /**
   * @param current
   * @param reference
   * @param wrong
   * @param missing
   * @param correct
   * @throws MappingStoreException
   */
  public Measure( final IMappingProvider current, final IMappingProvider reference, final IMappingStoreProvider wrong,
    final IMappingStoreProvider missing, final IMappingStoreProvider correct) throws MappingStoreException
  {
    this.current = current;
    this.reference = reference;
    this.wrong = wrong;
    this.missing = missing;
    this.correct = correct;
    log.info( "Intersect");
    current.intersect( reference, EmptySimilarityFunction.getInstance(), correct);
    this.cntRightCorrect = correct.getSize();
    this.cntCorrect = cntRightCorrect;              // == Number of True Positives
    log.info( cntRightCorrect);
    log.info( "Diff: Wrong");
    current.diff( reference, wrong);
    this.cntWrong = wrong.getSize();                // == Number of False Positives
    log.info( cntWrong);
    log.info( "Diff: Missing");
    reference.diff( current, missing);
    this.cntMissing = missing.getSize();            // == Number of False Negatives
    log.info( cntMissing);
    log.info("TP: " + cntRightCorrect + " FP: " + cntWrong + " FN: " + cntMissing);  

    this.cntPerfect = reference.getSize();
    this.cntToTest = current.getSize();
  }

  /**
   * @param correct
   * @param wrong
   * @param missing
   */
  public Measure( final long correct, final long wrong, final long missing)
  {
    this.current = null;
    this.reference = null;
    this.wrong = null;
    this.missing = null;
    this.correct = null;

    this.cntRightCorrect = correct;
    this.cntCorrect = cntRightCorrect;
    this.cntWrong = wrong;
    this.cntMissing = missing;
  }

  /**
   * @param cntPerfect
   * @param cntToTest
   * @param correct
   * @param wrong
   * @param missing
   */
  public Measure( final long cntPerfect, final long cntToTest, final long correct, final long wrong, final long missing)
  {
    this.current = null;
    this.reference = null;
    this.wrong = null;
    this.missing = null;
    this.correct = null;

    this.cntRightCorrect = correct;
    this.cntCorrect = cntRightCorrect;
    this.cntWrong = wrong;
    this.cntMissing = missing;
    this.cntToTest = cntToTest;
    this.cntPerfect = cntPerfect;
  }

  /**
   * @return precision
   */
  public double calcPrecision()
  {
    if( cntToTest == 0) return 0;
    return cntCorrect / cntToTest;
  }

  /**
   * @return recall
   */
  public double calcRecall()
  {
    if( cntPerfect == 0) return 0;
    return cntCorrect / cntPerfect;
  }

  /**
   * @return F1Measure
   */
  public double calcFMeasure()
  {
    return 2 * cntCorrect / (cntPerfect + cntToTest);
  }
  
	public double calcGMD(CostFunction costFunction) {

		HashSet<String> set = new HashSet<String>();
		for (IMappingEntry e : this.current) {
			set.add(e.getLeft().getId());
			set.add(e.getRight().getId());
		}

		for (IMappingEntry e : this.reference) {
			String idLeft = e.getLeft().getId();
			set.add(idLeft);

			String idRight = e.getRight().getId();
			set.add(idRight);

		}

		FactorSet<String> factorsetR = new FactorSet<String>(set);
		FactorSet<String> factorsetS = new FactorSet<String>(set);
		for (IMappingEntry e : this.current) {
			factorsetR.merge(e.getLeft().getId(), e.getRight().getId());
		}
		for (IMappingEntry e : this.reference) {
			factorsetS.merge(e.getLeft().getId(), e.getRight().getId());
		}
		HashSet<HashSet<String>> clustersR = factorsetR.factorset();
		HashMap<String, Integer> clusterAssignmentsR = new HashMap<String, Integer>();
		int clusterIndex = 0;
		int rSizes[] = new int[clustersR.size()];
		Iterator<HashSet<String>> clIt = clustersR.iterator();

		while (clIt.hasNext()) {
			// System.out.println("Cluster: " + clusterIndex);
			HashSet<String> c = clIt.next();
			Iterator<String> cIt = c.iterator();
			while (cIt.hasNext()) {
				String e = cIt.next();
				// System.out.println(e);

				clusterAssignmentsR.put(e, clusterIndex);
			}

			rSizes[clusterIndex] = c.size();
			clusterIndex++;

		}

		HashSet<HashSet<String>> clustersS = factorsetS.factorset();
		
		double cost = 0;
		Iterator<HashSet<String>> clItS = clustersS.iterator();

		while (clItS.hasNext()) {
			// System.out.println("Cluster: " + clusterIndex);
			HashMap<Integer, Integer> pMap = new HashMap<Integer, Integer>();
			HashSet<String> c = clItS.next();
			Iterator<String> cIt = c.iterator();
			while (cIt.hasNext()) {
				String e = cIt.next();
				int key = clusterAssignmentsR.get(e);
				if (!pMap.containsKey(key)) {
					pMap.put(key, 0);
				}
				// System.out.println(e);

				int count = pMap.get(key) + 1;
				pMap.put(key, count);
			}

			double siCost = 0;
			int totalRecs = 0;
			Set<Entry<Integer, Integer>> entrySet = pMap.entrySet();
			for (Entry<Integer, Integer> entry : entrySet) {
				int i = entry.getKey();
				int count = entry.getValue();
				if (rSizes[i] > count) {
					siCost = siCost
							+ costFunction.calculateCost(count, rSizes[i]
									- count);
				}
				rSizes[i] = rSizes[i] - count;
				if (totalRecs != 0) {
					siCost = siCost
							+ costFunction.calculateCost(count, totalRecs);
				}
				totalRecs = totalRecs + count;
			}
			cost = cost + siCost;

		}
		return cost;

	}

  long                         cntPerfect;

  long                         cntToTest;

  double                      cntCorrect;

  long                         cntRightCorrect;

  long                         cntWrong;

  long                         cntMissing;

  final IMappingProvider      current;

  final IMappingProvider      reference;

  final IMappingStoreProvider wrong;

  final IMappingStoreProvider missing;

  final IMappingStoreProvider correct;

  /**
   * @return the cntPerfect
   */
  public long getCntPerfect()
  {
    return cntPerfect;
  }

  /**
   * @return the cntToTest
   */
  public long getCntToTest()
  {
    return cntToTest;
  }

  /**
   * @return the cntCorrect
   */
  public long getCntCorrect()
  {
    return cntRightCorrect;
  }

  /**
   * @return the cntWrong
   */
  public long getCntWrong()
  {
    return cntWrong;
  }

  /**
   * @return the cntMissing
   */
  public long getCntMissing()
  {
    return cntMissing;
  }

  /**
   * @return the current
   */
  public IMappingProvider getCurrent()
  {
    return current;
  }

  /**
   * @return the reference
   */
  public IMappingProvider getReference()
  {
    return reference;
  }

  /**
   * @return the wrong
   */
  public IMappingStoreProvider getWrong()
  {
    return wrong;
  }

  /**
   * @return the missing
   */
  public IMappingStoreProvider getMissing()
  {
    return missing;
  }

  /**
   * @return the correct
   */
  public IMappingStoreProvider getCorrect()
  {
    return correct;
  }

  /**
   * @param other
   * @return quals in count of missing, wrong and correct
   */
  public boolean equalsCount( final Measure other)
  {
    if( cntMissing != other.cntMissing) return false;
    if( cntWrong != other.cntWrong) return false;
    if( cntRightCorrect != other.cntRightCorrect) return false;

    return true;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "Measure [cntCorrect=").append( cntCorrect).append( ", cntMissing=").append( cntMissing).append(
      ", cntPerfect=").append( cntPerfect).append( ", cntRightCorrect=").append( cntRightCorrect).append(
      ", cntToTest=").append( cntToTest).append( ", cntWrong=").append( cntWrong).append( ", calcFMeasure()=").append( calcFMeasure()).append( ", calcPrecision()=").append( calcPrecision())
      .append( ", calcRecall()=").append( calcRecall()).append( "]");
    return builder.toString();
  }

 



}
