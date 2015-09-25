/**
 *
 */
package de.wdilab.ml.impl.matcher.blocking;

import java.io.IOException;
import java.io.File;
import java.util.*;
import java.math.BigInteger;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;

import de.wdilab.ml.impl.matcher.simple.AbstractSimpleOneSourceAttributeObjectMatcher;
import de.wdilab.ml.impl.matcher.blocking.analyzer.WhitespaceAnalyzerWOTokens;
import de.wdilab.ml.impl.matcher.blocking.analyzer.NGramAnalyzer;
import de.wdilab.ml.impl.matcher.blocking.CombinationGenerator;
import de.wdilab.ml.impl.oi.enhancement.persistence.MainMemoryFullyCachedObjectInstanceProvider;
import de.wdilab.ml.impl.*;
import de.wdilab.ml.impl.Similarity;
import de.wdilab.ml.interfaces.mapping.IMappingStore;
import de.wdilab.ml.interfaces.mapping.MappingStoreException;
import de.wdilab.ml.interfaces.mapping.IMappingProvider;
import de.wdilab.ml.interfaces.mapping.ISimilarity;
import de.wdilab.ml.interfaces.oi.EOIProviderCapabilityHint;
import de.wdilab.ml.interfaces.oi.IObjectInstance;
import de.wdilab.ml.interfaces.oi.IObjectInstanceProvider;
import de.wdilab.ml.interfaces.matcher.IAttributeObjectMatcher;

/**
 * Lucene BiGramIndexingBlocker
 *
 * @author wurdinger
 */
public class BigramIndexing extends AbstractSimpleOneSourceAttributeObjectMatcher implements
        IAttributeObjectMatcher{
    protected static final Logger log = Logger.getLogger(BigramIndexing.class);

    /**
     */
    public BigramIndexing() {
        super();
    }

    /**
     * @param attr      for blocking
     * @param attrId    for recognizing the id of the original record
     * @param threshold threshold for blocking
     */
    public BigramIndexing(final String attr, final String attrId, final float threshold) {
        super(attr, attrId, threshold);

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
    public void match(final IObjectInstanceProvider oip1, final IObjectInstanceProvider oip2, final IMappingStore mrs) {
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

    /*
    * (non-Javadoc)
    * @seede.wdilab.ml.interfaces.matcher.IObjectMatcher#match(de.wdilab.ml.interfaces.mapping.
    * IMappingProvider, de.wdilab.ml.interfaces.mapping.IMappingStore)
    */
    @Override
    public void match(final IMappingProvider mp, final IMappingStore mrs) throws MappingStoreException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    /*
    attrRechts = Id of original record (needed for recognition of a record for a block)
    attrLinks = Attribut chosen for blocking
     */

    @Override
    public void match(final IObjectInstanceProvider oip1, final IMappingStore mrs) throws MappingStoreException {
        log.info("Blocking started.");
        long now1 = System.currentTimeMillis();

        IObjectInstanceProvider mmp;
        // Wrapping für HighSpeed getInstance
        if (oip1.hasCapability(EOIProviderCapabilityHint.MEMORY))
            mmp = oip1;
        else
            mmp = new MainMemoryFullyCachedObjectInstanceProvider(oip1);

        final Analyzer analyzerBigram = new NGramAnalyzer(2, 2);
        final Analyzer analyzerCombi = new WhitespaceAnalyzerWOTokens();
        final Set<String> fieldNames = mmp.getMetaData().getAttributes();
        Directory directoryBigram = null;
        Directory directoryCombi = null;
        try {
            //directoryBigram = new RAMDirectory();
            // switch here to filebased index in case the index grows too much
            File fileindexBigram = new File("C:\\workspace\\wdi\\lucene1\\");
            directoryBigram = FSDirectory.open(fileindexBigram);

            final IndexWriter writerBigram = new IndexWriter(directoryBigram, analyzerBigram, true, IndexWriter.MaxFieldLength.UNLIMITED);
            try {
                for (final IObjectInstance oi : mmp) {
                    Document documentBigram = new Document();
                    documentBigram.add(new Field(attrRechts, oi.getValue(attrRechts).toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                    documentBigram.add(new Field(attrLinks, oi.getStringValue(attrLinks), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.YES));
                    writerBigram.addDocument(documentBigram);
                }
                writerBigram.optimize();
                writerBigram.close();
            } catch (final Exception e) {
                log.info(e);
            }
        } catch (final Exception e) {
            log.info(e);
        }
        log.info("Bigram indexed.");
        long now2 = System.currentTimeMillis();
        IndexReader readerBigram = null;
        try {
            readerBigram = IndexReader.open(directoryBigram, true);
        } catch (final CorruptIndexException e) {
            log.error(e, e);
        } catch (final IOException e) {
            log.error(e, e);
        } // only searching, so read-only=true


        ArrayList<String> attributeNames;
        ArrayList<Object> attributeValues = new ArrayList<Object>();
        attributeNames = new ArrayList<String>();
        attributeNames.add(attrRechts);
        attributeNames.add(attrLinks);
        log.info("Combinations started.");
        long now3 = System.currentTimeMillis();
        long now4 = now3;
        try {
            count = readerBigram.numDocs();
            //directoryCombi = new RAMDirectory();
            // switch here to filebased index in case the index grows too much
            File fileindexCombi = new File("C:\\workspace\\wdi\\lucene2\\");
            directoryCombi = FSDirectory.open(fileindexCombi);

            final IndexWriter writerCombi = new IndexWriter(directoryCombi, analyzerCombi, true, IndexWriter.MaxFieldLength.UNLIMITED);
            for (int i = 0; i < count; i++) {
                TermFreqVector documentTokens = readerBigram.getTermFreqVector(i, attrLinks);
                int lenElements = documentTokens.getTerms().length;
                float lenForCombinations = lenElements * this.threshold;
                int intLenForCombinations = Math.round(lenForCombinations);
                String id = readerBigram.document(i).getField(attrRechts).stringValue();
                Document documentCombi = new Document();
                String[] elements = documentTokens.getTerms();
                int[] indices;
                CombinationGenerator x = new CombinationGenerator(lenElements, intLenForCombinations);
                StringBuffer combination;
                documentCombi.add(new Field(attrRechts, id, Field.Store.YES, Field.Index.NOT_ANALYZED));
                BigInteger writingLimit1 = new BigInteger ("100000");
                BigInteger writingLimit2 = BigInteger.ZERO;
                while (x.hasMore()) {
                    combination = new StringBuffer();
                    indices = x.getNext();
                    for (int m = 0; m < indices.length; m++) {
                        combination.append(elements[indices[m]]);
                    }
                    documentCombi.add(new Field("blockingIndex", combination.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED, Field.TermVector.YES));
                    if ((x.getNumLeft().mod(writingLimit1).equals(writingLimit2)) && !(x.getNumLeft().equals(writingLimit2))) {
                        writerCombi.addDocument(documentCombi);
                    }
                }
                writerCombi.addDocument(documentCombi);
            }
            writerCombi.optimize();
            writerCombi.close();
            IndexReader readerCombi = null;
            String StrITermCount;
            String blockId = null;
            ISimilarity dummySim = new Similarity(1.0f);
            IObjectInstance oiCombi;
            ArrayList<IObjectInstance> block = new ArrayList<IObjectInstance>();
            ArrayList<IObjectInstance> block2 = new ArrayList<IObjectInstance>();

            try {
                readerCombi = IndexReader.open(directoryCombi, true);
                final Searcher searcherCombi = new IndexSearcher(readerCombi);
                final Searcher searcherBigram = new IndexSearcher(readerBigram);
                Term terms = new Term("blockingIndex", "");
                TermEnum termEnum4TermCount = readerCombi.terms(terms);
                int iTermCount = 0;
                StrITermCount = "" + iTermCount;
                if ((termEnum4TermCount.term().field().equals("blockingIndex")) && (termEnum4TermCount.docFreq() > 1)) {
                       String origId;
                        blockId = termEnum4TermCount.term().text();
                        try {
                            String q1 = escapingSpecialCharacters(termEnum4TermCount.term().text());
                            Query q = new QueryParser(Version.LUCENE_CURRENT, "blockingIndex", analyzerCombi).parse(q1);
                            TopScoreDocCollector collectorCombi = TopScoreDocCollector.create(readerCombi.numDocs(), true);
                            searcherCombi.search(q, collectorCombi);
                            ScoreDoc[] hitsCombi = collectorCombi.topDocs().scoreDocs;
                            iTermCount++;
                            block.clear();
                            for (int i = 0; i < collectorCombi.getTotalHits(); i++) {
                                origId = readerCombi.document(hitsCombi[i].doc).getField(attrRechts).stringValue();
                                attributeValues.clear();
                                Query qBigram = new QueryParser(Version.LUCENE_CURRENT, attrRechts, analyzerCombi).parse(origId);
                                TopScoreDocCollector collectorBigram = TopScoreDocCollector.create(readerBigram.numDocs(), true);
                                searcherBigram.search(qBigram, collectorBigram);
                                ScoreDoc[] hitsBigram = collectorBigram.topDocs().scoreDocs;
                                attributeValues.add(readerCombi.document(hitsCombi[i].doc).getField(attrRechts).stringValue());
                                attributeValues.add(readerBigram.document(hitsBigram[0].doc).getField(attrLinks).stringValue());
                                oiCombi =
                                        new ObjectInstance(attrRechts, attributeValues, attributeNames);
                                block.add(oiCombi);
                            }
                        } catch (Exception e) {
                            log.error(e, e);
                        }
                }

                while (termEnum4TermCount.next()) {
                    StrITermCount = "" + iTermCount;
                    if ((termEnum4TermCount.term().field().equals("blockingIndex")) && (termEnum4TermCount.docFreq() > 1)) {
                        String origId;
                        blockId = termEnum4TermCount.term().text();
                        try {
                            String q1 = escapingSpecialCharacters(termEnum4TermCount.term().text());
                            Query q = new QueryParser(Version.LUCENE_CURRENT, "blockingIndex", analyzerCombi).parse(q1);
                            TopScoreDocCollector collectorCombi = TopScoreDocCollector.create(readerCombi.numDocs(), true);
                            searcherCombi.search(q, collectorCombi);
                            ScoreDoc[] hitsCombi = collectorCombi.topDocs().scoreDocs;
                            iTermCount++;
                            block.clear();
                            for (int i = 0; i < collectorCombi.getTotalHits(); i++) {
                                origId = readerCombi.document(hitsCombi[i].doc).getField(attrRechts).stringValue();
                                attributeValues.clear();
                                Query qBigram = new QueryParser(Version.LUCENE_CURRENT, attrRechts, analyzerCombi).parse(origId);
                                TopScoreDocCollector collectorBigram = TopScoreDocCollector.create(readerBigram.numDocs(), true);
                                searcherBigram.search(qBigram, collectorBigram);
                                ScoreDoc[] hitsBigram = collectorBigram.topDocs().scoreDocs;
                                attributeValues.add(readerCombi.document(hitsCombi[i].doc).getField(attrRechts).stringValue());
                                attributeValues.add(readerBigram.document(hitsBigram[0].doc).getField(attrLinks).stringValue());
                                oiCombi =
                                        new ObjectInstance(attrRechts, attributeValues, attributeNames);
                                block.add(oiCombi);
                            }
                        } catch (Exception e) {
                            log.error(e, e);
                        }
                        block2.clear();
                        block2.addAll(block);
                        for (int i = 0; i < block.size(); i++) {
                            for (int j = 1; j < block.size(); j++) {

                                if (!(block.get(i).equals(block2.get(j)))) {
                                    if (!(i > j)) {
                                        mrs.add(block.get(i), block2.get(j), dummySim);
                                    }
                                }
                            }
                        }
                    }
                }
                termEnum4TermCount.close();
                searcherCombi.close();
                searcherBigram.close();
                readerBigram.close();
                readerCombi.close();
                directoryCombi.close();
            log.info("Combinations finished.");
            now4 = System.currentTimeMillis();
            } catch (final CorruptIndexException e) {
                log.error(e, e);
            } catch (final IOException e) {
                log.error(e, e);
            } // only searching, so read-only=true

        } catch (final CorruptIndexException e) {
            log.error(e, e);
        } catch (final IOException e) {
            log.error(e, e);
        }

        try {
            directoryBigram.close();
        } catch (final IOException e) {
            log.error(e, e);
        }
        log.info("Blocking finished.");
        String info =  "Combinatorical part needed: " + (now4-now3) + " ms.";
        log.info(info);
        info = "Indexing part needed: " + (now3 - now1) + " ms.";
        log.info(info);

    }

    /* method is necessary for a correct parsing of the query */
    private String escapingSpecialCharacters(String q1) {
        q1 = q1.replace("&", "\\&");
        q1 = q1.replace("|", "\\|&");
        q1 = q1.replace("+", "\\+");
        q1 = q1.replace("-", "\\-");
        q1 = q1.replace("!", "\\!");
        q1 = q1.replace("(", "\\(");
        q1 = q1.replace(")", "\\)");
        q1 = q1.replace("{", "\\{");
        q1 = q1.replace("}", "\\}");
        q1 = q1.replace("[", "\\[");
        q1 = q1.replace("]", "\\}");
        q1 = q1.replace("^", "\\^");
        q1 = q1.replace("~", "\\~");
        q1 = q1.replace("*", "\\*");
        q1 = q1.replace("?", "\\?");
        q1 = q1.replace(":", "\\:");
        q1 = q1.replace(" ", "\\ ");
        //  q1 = q1.replace("\","\\");
        //  q1 = q1.replace(""","\\"");

        return q1;

    }
}