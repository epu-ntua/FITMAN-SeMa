/**
 * (C) Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.simple.trigram;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.mapping.enhancement.SynchronizedMappingStore;
import de.wdilab.ml.impl.matcher.simple.AbstractSimpleAttributeObjectMatcher;
import de.wdilab.ml.impl.util.GHashBag;
import de.wdilab.ml.interfaces.mapping.IMappingEntry;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

import static de.wdilab.ml.impl.util.string.NGramFunctions.*;

/**
 * Date: 12.05.2010
 * 
 * @author Nico Heller
 */
public class MultithreadedTrigram extends AbstractSimpleAttributeObjectMatcher
{
  protected static final Logger log = Logger.getLogger( MultithreadedTrigram.class);

  protected final int           threads;

  /**
   * @param attr1
   * @param attr2
   * @param threads
   */
  public MultithreadedTrigram( final String attr1, final String attr2, final int threads)
  {
    super( attr1, attr2);
    this.threads = threads;
  }

  /**
   * @param attr1
   * @param threads
   */
  public MultithreadedTrigram( final String attr1, final int threads)
  {
    super( attr1);
    this.threads = threads;
  }

  /**
   * @param threads
   */
  public MultithreadedTrigram( final int threads)
  {
    this.threads = threads;
  }

  /**
   * threads = 16
   */
  public MultithreadedTrigram()
  {
    this( 16);
  }

  /**
   * @param attr1
   * @param threshold
   * @param threads
   */
  public MultithreadedTrigram( final String attr1, final float threshold, final int threads)
  {
    super( attr1, threshold);
    this.threads = threads;
  }

  /**
   * @param attr1
   * @param attr2
   * @param threshold
   * @param threads
   */
  public MultithreadedTrigram( final String attr1, final String attr2, final float threshold, final int threads)
  {
    super( attr1, attr2, threshold);
    this.threads = threads;
  }

  /**
   * @author Nico Heller
   */
  public static class TrigramBuilder implements Runnable
  {
    private final String                      attr;

    private final Collection<IObjectInstance> oip;

    private final List<WordBagTrigram>        syncedWordTrigramList;

    /**
     * @param partWordList
     * @param syncedWordTrigramList
     *          should be thread-safe!
     */
    TrigramBuilder( final Collection<IObjectInstance> oip, final String attr,
      final List<WordBagTrigram> syncedWordTrigramList)
    {
      this.oip = oip;
      this.attr = attr;
      this.syncedWordTrigramList = syncedWordTrigramList;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
      for( final IObjectInstance oi : oip)
        syncedWordTrigramList.add( new WordBagTrigram( oi.getStringValue( attr), oi));
    }

  }

  /**
   * @param threads
   * @param oip
   * @param wordTrigramList
   *          must not be thread-safe
   */
  protected static void buildTrigramsParallel( final int threads, final IObjectInstanceProvider oip, final String attr,
    final List<WordBagTrigram> wordTrigramList)
  {
    log.info( "buildTrigramsParallel");
    final List<List<IObjectInstance>> partWordList = new ArrayList<List<IObjectInstance>>( threads);
    final int cntWords = oip.size();
    // 0. step prepare partWordList
    for( int i = 0; i < threads; i++)
      partWordList.add( new ArrayList<IObjectInstance>( cntWords / threads + 1));

    // 1. step divide wordlist
    int counter = 0;
    for( final IObjectInstance oi : oip)
    {
      partWordList.get( counter++ % threads).add( oi);
    }

    final List<WordBagTrigram> syncedWordTrigramList = Collections.synchronizedList( wordTrigramList);

    final Thread[] threadArray = new Thread[threads];
    // 2. step start working
    for( int i = 0; i < threads; i++)
    {
      threadArray[i] = new Thread( new TrigramBuilder( partWordList.get( i), attr, syncedWordTrigramList));
      threadArray[i].start();
    }
    // 3. wait
    for( int i = 0; i < threads; i++)
    {
      try
      {
        threadArray[i].join();
      } catch( final InterruptedException e)
      {
        log.error( e, e);
      }
    }
    log.info( "END buildTrigramsParallel");
  }

  /**
   * @param threads
   * @param wl
   * @param wordTrigramList
   *          must not be thread-safe
   * @throws InterruptedException
   */
  protected static void matchTrigramParallel( final int threads, final List<WordBagTrigram> wordTrigramListOne,
    final List<WordBagTrigram> wordTrigramListTwo, final IMappingStore ms, final double threshold)
    throws InterruptedException
  {
    log.info( "matchTrigramParallel");
    final List<List<WordBagTrigram>> partWordTrigramList = new ArrayList<List<WordBagTrigram>>( threads);
    final int cntWords = wordTrigramListOne.size();
    // 0. step prepare partWordtrigramLists
    for( int i = 0; i < threads; i++)
      partWordTrigramList.add( new ArrayList<WordBagTrigram>( cntWords / threads + 1));

    // 1. step divide the first wordTrigramList
    int counter = 0;
    for( final WordBagTrigram wt : wordTrigramListOne)
    {
      partWordTrigramList.get( counter++ % threads).add( wt);
    }

    final IMappingStore syncedMs = new SynchronizedMappingStore( ms);

    final Thread[] threadArray = new Thread[threads];
    // 2. step start working
    for( int i = 0; i < threads; i++)
    {
      threadArray[i] =
        new Thread( new TrigramMatcher( partWordTrigramList.get( i), wordTrigramListTwo, syncedMs, threshold));
      threadArray[i].start();
    }
    // 3. wait
    for( int i = 0; i < threads; i++)
    {
      threadArray[i].join();
    }
    log.info( "END. matchTrigramParallel");
  }

  /**
   * @author Nico Heller
   */
  public static class TrigramMatcher implements Runnable
  {
    private final Collection<WordBagTrigram> w1l;

    private final Collection<WordBagTrigram> w2l;

    private final double                     threshold;

    private final IMappingStore              syncedMs;

    /**
     * @param partWordList
     * @param syncedWordTrigramList
     *          should be thread-safe!
     */
    TrigramMatcher( final List<WordBagTrigram> w2l, final List<WordBagTrigram> w1l, final IMappingStore syncedMs,
      final double threshold)
    {
      this.w1l = w1l;
      this.w2l = w2l;

      this.threshold = threshold;

      this.syncedMs = syncedMs;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
      for( final WordBagTrigram first : w1l)
      {
        for( final WordBagTrigram second : w2l)
        {
          // gleiche nicht testen
          // if( first.word.hashCode() == second.word.hashCode() && first.word.equals( second.word))
          // continue;
          final float sim = first.sim( second);

          if( sim >= threshold) try
          {
            syncedMs.add( first.oi, second.oi, new Similarity( sim));
          } catch( final MappingStoreException e)
          {
            log.error( e, e);
            Thread.currentThread().interrupt();
          }
        }
      }

    }
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
    final ArrayList<WordBagTrigram> wordTrigramList1 = new ArrayList<WordBagTrigram>( oip1.size());
    final ArrayList<WordBagTrigram> wordTrigramList2 = new ArrayList<WordBagTrigram>( oip2.size());

    // 1. Pre Build Trigrams parallel
    buildTrigramsParallel( threads, oip1, attrLinks, wordTrigramList1);
    buildTrigramsParallel( threads, oip2, attrRechts, wordTrigramList2);

    // 2. Match parallel
    try
    {
      matchTrigramParallel( threads, wordTrigramList1, wordTrigramList2, mrs, threshold);
    } catch( final InterruptedException e)
    {
      log.error( e, e);
      throw new MappingStoreException( e);
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
    final ArrayList<WordBagTrigram> wordTrigramList = new ArrayList<WordBagTrigram>( oip.size());

    // 1. Pre Build Trigrams parallel
    buildTrigramsParallel( threads, oip, attrLinks, wordTrigramList);

    // 2. Match parallel
    try
    {
      matchTrigramParallel( threads, wordTrigramList, wordTrigramList, mrs, threshold);
    } catch( final InterruptedException e)
    {
      log.error( e, e);
      throw new MappingStoreException( e);
    }
  }

  static final int BLOCK_SIZE = 1000;

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.mapping.
   * IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException
  {
    final IMappingStore syncedMs = new SynchronizedMappingStore( mrs);
    int block_count = 0;
    final MappingTrigramSimThread[] threadArray = new MappingTrigramSimThread[threads];

    for( final IMappingEntry me : mp)
    {
      if( block_count == 0)
      {
        for( int i = 0; i < threads; i++)
        {
          threadArray[i] = new MappingTrigramSimThread( syncedMs);
        }
      }
      threadArray[block_count % threads].lme.add( me);

      block_count++;
      if( block_count == BLOCK_SIZE * threads)
      {
        block_count = 0;
        for( int i = 0; i < threads; i++)
        {
          threadArray[i].start();
        }
        for( int i = 0; i < threads; i++)
        {
          try
          {
            threadArray[i].join();
            threadArray[i].lme.clear();
          } catch( final InterruptedException e)
          {
            log.error( e);
            throw new MappingStoreException( e);
          }
        }
      }
    }
    // Lasts
    for( int i = 0; i < threads; i++)
    {
      threadArray[i].start();
    }
    for( int i = 0; i < threads; i++)
    {
      try
      {
        threadArray[i].join();
        threadArray[i].lme.clear();
      } catch( final InterruptedException e)
      {
        log.error( e);
        throw new MappingStoreException( e);
      }
    }
  }

  class MappingTrigramSimThread extends Thread
  {
    public ArrayList<IMappingEntry> lme           = new ArrayList<IMappingEntry>();

    public final IMappingStore      mrs;

    public Exception                lastException = null;

    MappingTrigramSimThread( final IMappingStore mrs)
    {
      this.mrs = mrs;
    }

    @Override
    public void run()
    {
      for( final IMappingEntry me : lme)
      {
        final IObjectInstance oiLeft = me.getLeft();
        final IObjectInstance oiRight = me.getRight();
        final String leftWord = oiLeft.getStringValue( attrLinks);
        final String rightWord = oiRight.getStringValue( attrRechts);

        if( leftWord == null || rightWord == null)
        {
          if( threshold > 0)
            continue;
          else
            try
            {
              mrs.add( oiLeft, oiRight, new Similarity( 0));
            } catch( final MappingStoreException e)
            {
              log.error( e);
              lastException = e;
              return;
            }
        }
        final GHashBag<String> left = generateTriGramHashBag( leftWord);
        final GHashBag<String> right = generateTriGramHashBag( rightWord);

        final float sim = computeTriGramSimilarity( left, right);
        if( sim >= threshold) try
        {
          mrs.add( oiLeft, oiRight, new Similarity( sim));
        } catch( final MappingStoreException e)
        {
          log.error( e);
          lastException = e;
          return;
        }
      }
    }
  }

  /**
   * @return count threads
   */
  public int getThreads()
  {
    return threads;
  }

  @Override
  public String getParamedName()
  {
    return "MultithreadedTrigram threads=" + getThreads();
  }

}
