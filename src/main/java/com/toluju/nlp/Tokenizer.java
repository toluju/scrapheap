package com.toluju.nlp;

import com.toluju.util.Log;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import org.apache.commons.io.IOUtils;

/**
 * @author Toby
 */
public class Tokenizer {
  private static final Log log = new Log(Tokenizer.class);

  public Tokenizer() {

  }

  public void tokenize(Document doc) {
    String str = doc.getRawString();
    int start = 0;
    int end = 0;

    for (; end < str.length(); ++end) {
      char c = str.charAt(end);

      if (Character.isWhitespace(c)) {
        if (start < end) {
          Token token = new Token(doc, start, end);
          doc.addToken(token);
        }

        start = end + 1;
      }
    }

    if (start < end) {
      Token token = new Token(doc, start, end);
      doc.addToken(token);
    }
  }

  public static void main(String[] args) throws Exception {
    File testFile = new File(args[0]);

    Reader reader = new FileReader(testFile);
    Document doc = new Document(IOUtils.toString(reader));
    reader.close();

    Tokenizer tokenizer = new Tokenizer();
    tokenizer.tokenize(doc);

    for (Token token : doc) {
      log.info("Token: {0}", token);
    }
  }
}
