package com.toluju.nlp;

import com.toluju.util.Log;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
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
        if (start < end) addTokens(str, start, end, doc);
        start = end + 1;
      }
    }

    if (start < end) addTokens(str, start, end, doc);
  }

  protected Character nextNonWhitespace(String str, int offset) {
    for (; offset < str.length(); ++offset) {
      if (!Character.isWhitespace(str.charAt(offset)))
        return str.charAt(offset);
    }

    return null;
  }

  public static Set<String> COMMONABBRS = new HashSet<String>() {{
    add("Mrs."); add("Mr."); add("Ms."); add("Dr.");
  }};

  public static Set<String> TERMINATORS = new HashSet<String>() {{
    add("'"); add("'s"); add("n't"); add("'re"); add("'ve");
    add("'ll"); add("'d"); add("'m");
  }};

  protected Set<String> specialTokens = new HashSet<String>() {{
    add("''");
  }};

  protected void addTokens(String str, int start, int end, Document doc) {
    String subStr = str.substring(start, end);
    char lastChar = str.charAt(end - 1);

    if (specialTokens.contains(subStr)) {
      Token token = new Token(doc, start, end);
      doc.addToken(token);
      return;
    }

    for (String term : TERMINATORS) {
      if (subStr.endsWith(term)) {
        if (start < (end - term.length())) {
          Token token = new Token(doc, start, end - term.length());
          doc.addToken(token);
        }
        Token token = new Token(doc, end - term.length(), end);
        doc.addToken(token);
        return;
      }
    }

    if (lastChar == '\'' || lastChar == ',' ||
        lastChar == '!' || lastChar == '?') {
      if (start < (end - 1)) {
        Token token = new Token(doc, start, end - 1);
        doc.addToken(token);
      }
      Token token = new Token(doc, end - 1, end);
      doc.addToken(token);
      return;
    }

    if (lastChar == '.') {
      Character nextNonWS = nextNonWhitespace(str, end);

      if (COMMONABBRS.contains(subStr) || nextNonWS == null ||
          !Character.isUpperCase(nextNonWS)) {
        Token token = new Token(doc, start, end);
        doc.addToken(token);
      }
      else {
        if (start < (end - 1)) {
          Token token = new Token(doc, start, end - 1);
          doc.addToken(token);
        }
        Token token = new Token(doc, end - 1, end);
        doc.addToken(token);
      }

      return;
    }

    Token token = new Token(doc, start, end);
    doc.addToken(token);
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
