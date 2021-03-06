package com.toluju.nlp;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.toluju.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

/**
 * @author Toby
 */
public class POSTagger {
  private static final Log log = new Log(POSTagger.class);

  protected Multimap<String, POSTag> lexiconMap = LinkedHashMultimap.create(100000, 3);

  public POSTagger() {

  }

  public void load() throws IOException {
    log.info("Loading lexicon...");
    File dataFile = new File("data/lexicon.txt.gz");
    Reader reader = new BufferedReader(new InputStreamReader(
                    new GZIPInputStream(new FileInputStream(dataFile))));

    LineIterator iterator = IOUtils.lineIterator(reader);

    while (iterator.hasNext()) {
      String line = iterator.nextLine();
      String[] splits = line.split("\\s");
      for (int x = 1; x < splits.length; ++x) {
        POSTag tag = POSTag.fromString(splits[x]);
        if (tag == null)
          log.warn("Unknown tag: {0}", splits[x]);
        else
          lexiconMap.put(splits[0], tag);
      }
    }

    iterator.close();
    log.info("Lexicon loaded!");
  }

  public void process(Document doc) {
    for (Token token : doc) {
      String tokenStr = token.asString();
      Collection<POSTag> possibleTags = lexiconMap.get(tokenStr);

      if (possibleTags.isEmpty()) {
        if (tokenStr.length() > 0 && Character.isUpperCase(tokenStr.charAt(0))) {
          token.setPOSTag(POSTag.NNP);
        }
        else {
          token.setPOSTag(POSTag.UNKNOWN);
        }
      }
      else {
        token.setPOSTag(possibleTags.iterator().next());
      }
    }
  }

  public static void main(String[] args) throws Exception {
    File testFile = new File(args[0]);

    Reader reader = new FileReader(testFile);
    Document doc = new Document(IOUtils.toString(reader));
    reader.close();

    Tokenizer tokenizer = new Tokenizer();
    tokenizer.tokenize(doc);

    POSTagger tagger = new POSTagger();
    tagger.load();
    tagger.process(doc);

    for (Token token : doc) {
      log.info("Token: {0}", token);
    }
  }
}