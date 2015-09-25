/**
 * Nico Heller
 * 2010
 */
package de.wdilab.ml.impl.matcher.concurrent;

import java.util.Vector;

import org.apache.log4j.Logger;

import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IObjectMatcher;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * A ConcurrentMatcherExecuter.
 * Add match tasks. After all Match Taks are added call the methode {@link #waitAllMatchTaksDone()}
 * to wait till all Tasks are Done!
 * <p>
 * Date: 19.10.2010
 * </p>
 *
 * @author Nico Heller
 * @see #waitAllMatchTaksDone
 * @see #addMatchTask(IObjectMatcher, IMappingProvider, IMappingStore)
 * @see #addMatchTask(IObjectMatcher, IObjectInstanceProvider, IObjectInstanceProvider,
 *      IMappingStore)
 * @see #addMatchTask(IObjectMatcher, IObjectInstanceProvider, IMappingStore)
 */
public class ConcurrentMatcherExecuter
{
  protected static final Logger               log        = Logger.getLogger( ConcurrentMatcherExecuter.class);

  private final Vector<Thread>                threads    = new Vector<Thread>();

  private final Vector<MappingStoreException> exceptions = new Vector<MappingStoreException>();

  /**
   * Adds a match task. After all Match Taks are added call the methode
   * {@link #waitAllMatchTaksDone()} to wait till all Tasks are Done!
   *
   * @param matcher
   * @param mp
   * @param ms
   * @see #waitAllMatchTaksDone
   */
  public void addMatchTask( final IObjectMatcher matcher, final IMappingProvider mp, final IMappingStore ms)
  {
    log.info( "Add Match Task: " + matcher.getClass());
    final Thread t = new Thread( new Runnable() {
      @Override
      public void run()
      {
        try
        {
          matcher.match( mp, ms);
          log.info( "End Match Task: " + matcher.getClass());
        } catch( final MappingStoreException e)
        {
          exceptions.add( e);
          log.error( e, e);
        }
      }
    });

    t.start();

    threads.add( t);
  }

  /**
   * Adds a match task. After all Match Taks are added call the methode
   * {@link #waitAllMatchTaksDone()} to wait till all Tasks are Done!
   *
   * @param matcher
   * @param oip1
   * @param oip2
   * @param ms
   * @see #waitAllMatchTaksDone()
   */
  public void addMatchTask( final IObjectMatcher matcher, final IObjectInstanceProvider oip1,
    final IObjectInstanceProvider oip2, final IMappingStore ms)
  {
    log.info( "Add Match Task: " + matcher.getClass());
    final Thread t = new Thread( new Runnable() {
      @Override
      public void run()
      {
        try
        {
          matcher.match( oip1, oip2, ms);
          log.info( "End Match Task: " + matcher.getClass());
        } catch( final MappingStoreException e)
        {
          exceptions.add( e);
          log.error( e, e);
        }
      }
    });

    t.start();

    threads.add( t);
  }

  /**
   * Adds a match task. After all Match Taks are added call the methode
   * {@link #waitAllMatchTaksDone()} to wait till all Tasks are Done!
   *
   * @param matcher
   * @param oip
   * @param ms
   * @see #waitAllMatchTaksDone()
   */
  public void addMatchTask( final IObjectMatcher matcher, final IObjectInstanceProvider oip, final IMappingStore ms)
  {
    log.info( "Add Match Task: " + matcher.getClass());
    final Thread t = new Thread( new Runnable() {
      @Override
      public void run()
      {
        try
        {
          matcher.match( oip, ms);
          ms.closeStore();
          log.info( "End Match Task: " + matcher.getClass());
        } catch( final MappingStoreException e)
        {
          exceptions.add( e);
          log.error( e, e);
        }
      }
    });

    t.start();

    threads.add( t);
  }

  /**
   * After all Match Taks are added call this methode to wait till all Tasks are Done!
   *
   * @throws InterruptedException
   * @throws MappingStoreException
   */
  public void waitAllMatchTaksDone() throws InterruptedException, MappingStoreException
  {
    for( final Thread t : threads)
    {
      t.join();
    }

    threads.clear();
    if( !exceptions.isEmpty()) throw exceptions.get( 0);
  }

  /**
   * @return all the exceptions
   */
  public Vector<MappingStoreException> getExceptions()
  {
    return exceptions;
  }
}
