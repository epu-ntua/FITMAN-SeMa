package de.wdilab.ml.impl.matcher.blocking.analyzer;

/**
 * Created by IntelliJ IDEA.
 * User: wurdinger
 * Date: 14.04.2010
 * Time: 13:04:57
 * To change this template use File | Settings | File Templates.
 */

import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Analyzer;

import java.io.Reader;

public class NGramAnalyzer extends Analyzer {

  private int minGram;
  private int maxGram;

  public NGramAnalyzer(int minGram, int maxGram) {
    this.minGram = minGram;
    this.maxGram = maxGram;
  }

    @Override
  public TokenStream tokenStream(String fieldName, Reader reader) {
    return new NGramTokenizer(reader, minGram, maxGram);
  }
}
