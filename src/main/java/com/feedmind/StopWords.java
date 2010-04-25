package com.feedmind;

import com.toluju.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Toby
 */
public enum StopWords {
  INSTANCE;

  private static final Log log = new Log(StopWords.class);

  protected Set<String> stopWords = new HashSet<String>();

  StopWords() {
    init();
  }

  protected void init() {
    try {
      InputStream is = StopWords.class.getResourceAsStream("/com/feedmind/stopwords.txt");
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));

      String line = null;
      while ((line = reader.readLine()) != null) {
        stopWords.add(line.trim().toLowerCase());
      }

      reader.close();
    }
    catch (Exception e) {
      log.fatal("Exception loading stopwords", e);
    }
  }

  public static boolean contains(String word) {
    return INSTANCE.stopWords.contains(word.toLowerCase());
  }
}
