/**
 * 
 */
package de.wdilab.ml.impl.matcher.composite.lucene;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.impl.matcher.AbstractThresholdedObjectMatcher;
import de.wdilab.ml.impl.oi.enhancement.persistence.MainMemoryFullyCachedObjectInstanceProvider;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.matcher.IThresholdedObjectMatcher;
import de.wdilab.ml.interfaces.oi.EOIProviderCapabilityHint;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;

/**
 * Lucene MultiAttribute Matcher fully cached.
 * 
 * @author Nico Heller
 */
public class LuceneMultiFieldOrFullyCached extends AbstractThresholdedObjectMatcher implements
    IThresholdedObjectMatcher
{
  protected static final Logger log = Logger.getLogger( LuceneMultiFieldOrFullyCached.class);

  /**
   */
  public LuceneMultiFieldOrFullyCached()
  {
    super();
  }

  /**
   * @param threshold
   */
  public LuceneMultiFieldOrFullyCached( final float threshold)
  {
    super( threshold);
  }

  /*
   * (non-Javadoc)
   * @see
   * de.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces
   * .oi. IObjectInstanceProvider,
   * de.wdilab.ml.interfaces.oi.IObjectInstanceProvider,
   * de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IObjectInstanceProvider oip1, final IObjectInstanceProvider oip2, final IMappingStore mrs)
  {
    // final MainMemoryFullyCachedObjectInstanceProvider mp1 = new
    // MainMemoryFullyCachedObjectInstanceProvider( oip1);
    //
    // final ArrayList<String> ids = new ArrayList<String>();
    // final ArrayList<String> values = new ArrayList<String>();
    //
    // final StopWatch sw = new StopWatch();
    //
    // for( final IObjectInstance oiLinks : mp1)
    // {
    // ids.add( oiLinks.getId());
    // values.add( oiLinks.getStringValue( attrLinks));
    // }
    //
    // final String[] strings = new String[values.size()];
    // values.toArray( strings);
    //
    // // final LinkedList<Correspondence> result = PPJoinPlus.start( 'c',
    // threshold, 2, strings);
    // // final Iterator<Correspondence> iter = result.iterator();
    // // while( iter.hasNext())
    // // {
    // // final Correspondence c = iter.next();
    // // mrs.add( mp1.getInstance( ids.get( c.getFirstObject())),
    // mp1.getInstance( ids.get(
    // // c.getSecondObject())),
    // // new Similarity( c.getSimilarity()));
    // //
    // // }
    // // final Iterator<Correspondence> iter = result.iterator();
    // for( final Correspondence c : PPJoinPlus.start( 'c', threshold, 2,
    // strings))
    // {
    // mrs.add( mp1.getInstance( ids.get( c.getFirstObject())),
    // mp1.getInstance( ids.get(
    // c.getSecondObject())),
    // new Similarity( c.getSimilarity()));
    //
    // }
    throw new UnsupportedOperationException();
  }

  @Override
  public void match( final IObjectInstanceProvider oip, final IMappingStore mrs) throws MappingStoreException
  {
    log.info( "Match Start.");

    IObjectInstanceProvider mmp;
    // Wrapping für HighSpeed getInsatnce
    if( oip.hasCapability( EOIProviderCapabilityHint.MEMORY))
      mmp = oip;
    else
      mmp = new MainMemoryFullyCachedObjectInstanceProvider( oip);

    final Analyzer analyzer = new StandardAnalyzer( Version.LUCENE_30);
    final Set<String> fieldNames = mmp.getMetaData().getAttributes();
    Directory directory = null;
    try
    {
      directory = new RAMDirectory();
      try
      {
        final IndexWriter writer = new IndexWriter( directory, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);

        for( final IObjectInstance oi : mmp)
        {
          final Document document = new Document();
          document.add( new Field( "id", oi.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
          for( final String attr : fieldNames)
          {
            if( "id".equals( attr)) continue;
            document.add( new Field( attr, oi.getStringValue( attr), Field.Store.NO, Field.Index.ANALYZED));
          }

          writer.addDocument( document);
        }
        writer.optimize();
        writer.close();
      } catch( final Exception e)
      {
        log.info( e);
      }
    } catch( final Exception e)
    {
      log.info( e);
    }

    IndexReader reader = null;
    try
    {
      reader = IndexReader.open( directory, true);
    } catch( final CorruptIndexException e)
    {
      log.error( e, e);
    } catch( final IOException e)
    {
      log.error( e, e);
    } // only searching, so read-only=true

    final Searcher searcher = new IndexSearcher( reader);
    // searcher.setSimilarity(new TFIDFSimilarity());
    // final QueryParser parser =
    // new MultiFieldQueryParser( Version.LUCENE_30, fieldNames.toArray( new String[0]), analyzer);

    try
    {
      count = reader.numDocs();
      for( int i = 0; i < count; i++)
      {
        progress.incrementAndGet();
        final Document d1 = reader.document( i);
        final String id1 = d1.get( "id");
        final IObjectInstance oi1 = mmp.getInstance( id1);

        final BooleanQuery bq = new BooleanQuery();
        for( final String attr : fieldNames)
        {
          if( "id".equals( attr)) continue;
          final String attrValue = d1.get( attr);
          if( attrValue == null || attrValue.length() < 1) continue;
          final TermQuery tq =
            new TermQuery( new Term( attr, attrValue.replaceAll( "\\p{Punct}", "").replaceAll( "OR", " \"OR\"")));
          bq.add( tq, BooleanClause.Occur.SHOULD);
        }

        final TopScoreDocCollector collector = TopScoreDocCollector.create( count, true);
        searcher.search( bq, collector);
        final ScoreDoc[] hits = collector.topDocs().scoreDocs;

        final int cnt = hits.length;
        if( cnt < 1) continue;
        final double maxScore = hits[0].score;
        for( int j = 0; j < cnt; j++)
        {
          final ScoreDoc doc = hits[j];
          final int docId = doc.doc;
          final Document d = searcher.doc( docId);

          if( i < docId)
          {
            final double score = doc.score / maxScore;

            if( score >= threshold)
            {
              final String id2 = d.get( "id");

              final IObjectInstance oi2 = mmp.getInstance( id2);
              if( log.isDebugEnabled())
                log.debug( "id2 " + docId + ": " + d.get( "content") + " " + score + " " + doc.score);

              mrs.add( oi1, oi2, new Similarity( score));
            }
          }

        }

      }

    } catch( final CorruptIndexException e)
    {
      log.error( e, e);
    } catch( final IOException e)
    {
      log.error( e, e);
    }

    try
    {
      searcher.close();
      directory.close();
    } catch( final IOException e)
    {
      log.error( e, e);
    }

    log.info( "Match Ended.");
  }

  /*
   * (non-Javadoc)
   * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.mapping.
   * IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
   */
  @Override
  public void match( final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException
  {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }
}