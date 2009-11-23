package com.toluju.nlp;

import com.toluju.util.Log;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.io.IOUtils;

/**
 * @author Toby
 */
public class BrownPOSScorer {
  private static final Log log = new Log(BrownPOSScorer.class);

  protected File brownFile;

  public BrownPOSScorer(File brownFile) {
    this.brownFile = brownFile;
  }

  protected POSTag convertBrownTag(String str) {
    POSTag tag = POSTag.fromString(str);

    if (tag == null) {

        // TODO: review these
      if (str.equals("AT")) tag = POSTag.DT;
      else if (str.equals("PP$")) tag = POSTag.PRP$;
      else if (str.equals("PPS")) tag = POSTag.PRP;
      else if (str.equals("PPSS")) tag = POSTag.PRP;
      else if (str.equals("PPLS")) tag = POSTag.PRP;
      else if (str.equals("CS")) tag = POSTag.DT;
      else if (str.equals("BEDZ")) tag = POSTag.VBD;
      else if (str.equals("BEG")) tag = POSTag.VBG;
      else if (str.equals("HVD")) tag = POSTag.VBD;
      else if (str.equals("WPS")) tag = POSTag.WP;
      else if (str.equals("NN$")) tag = POSTag.NN;
      else if (str.equals("NP$")) tag = POSTag.NP;
      else if (str.equals("BED")) tag = POSTag.VBD;
      else if (str.equals("DTS")) tag = POSTag.DT;
      else if (str.equals("BE")) tag = POSTag.VB;
      else if (str.equals("BEZ")) tag = POSTag.VB;
      else if (str.equals("BEN")) tag = POSTag.VBD;
      else if (str.equals("IN-TL")) tag = POSTag.IN;
      else if (str.equals("PPO")) tag = POSTag.PRP;
      else if (str.equals("PPL")) tag = POSTag.PRP;
      else if (str.equals("BER")) tag = POSTag.VBP;
      else if (str.equals("QLP")) tag = POSTag.RB;
      else if (str.equals("*")) tag = POSTag.RB;
      else if (str.equals("OD")) tag = POSTag.JJ;
      else if (str.equals("AP")) tag = POSTag.JJ;
      else if (str.equals("QL")) tag = POSTag.JJR;
      else if (str.equals("DOD")) tag = POSTag.VBD;
      else if (str.equals("DOD*")) tag = POSTag.VBD;
      else if (str.equals("MD*")) tag = POSTag.MD;
      else if (str.equals("HV")) tag = POSTag.VBP;
      else if (str.equals("NR")) tag = POSTag.NN;
      else if (str.equals("DTI")) tag = POSTag.DT;
      else if (str.equals("ABN")) tag = POSTag.RB;
      else if (str.equals("PN")) tag = POSTag.NN;
      else if (str.equals("ABX")) tag = POSTag.DT;
      else if (str.equals("ABL")) tag = POSTag.JJ;
      else if (str.equals("HVG")) tag = POSTag.VBG;
    }

    return tag;
  }
  
  protected void tagBrownToken(Document doc, String str, String tagStr, int offset) {
    POSTag tag = convertBrownTag(tagStr);
      
    if (tag == null) {
      log.warn("POSTag on control document is null! {0}/{1}", str, tagStr);
    }

    Token token = new Token(doc, offset, offset + str.length());
    token.setPOSTag(tag);
    doc.addToken(token);
  }

  protected Set<String> possessiveSet = new HashSet<String>() {{
    add("NNS$");
    add("NP$");
  }};

  public void score() throws IOException {
    Reader reader = new FileReader(brownFile);
    String rawDocStr = IOUtils.toString(reader);
    reader.close();

    StringBuilder buffer = new StringBuilder();
    String[] splits = rawDocStr.split("\\s+");

    for (int x = 0; x < splits.length; ++x) {
      String tok = splits[x];
      String[] tokSplit = tok.split("/");
      if (tokSplit[0].length() > 0) {
        buffer.append(tokSplit[0]);
        buffer.append(" ");
      }
    }

    Document controlDoc = new Document(buffer.toString());

    int offset = 0;
    for (int x = 0; x < splits.length; ++x) {
      String tok = splits[x];
      String[] tokSplit = tok.split("/");
      if (tokSplit.length != 2) {
        log.warn("Invalid token: {0}", tok);
        continue;
      }

      String str = tokSplit[0];

      if (str.length() == 0) {
        log.warn("Zero-length token");
        continue;
      }

      String tagStr = tokSplit[1].toUpperCase();

      if (tagStr.endsWith("-TL") || tagStr.endsWith("-HL")) {
        tagStr = tagStr.substring(0, tagStr.length() - 3);
      }
      else if (tagStr.startsWith("FW-")) {
        tagStr = tagStr.substring(3);
      }

      boolean handled = false;

      if (str.equals("''") || str.equals("'")) {
        tagBrownToken(controlDoc, str, tagStr, offset);
        offset += str.length() + 1;
        handled = true;
      }

      if (!handled) {
        for (String terminator : Tokenizer.TERMINATORS) {
          if (str.endsWith(terminator)) {
            String tag1 = null;
            String tag2 = null;

            if (tagStr.endsWith("$")) {
              tag1 = tagStr.substring(0, tagStr.length() - 1);
              tag2 = "POS";
            }
            else if (tagStr.endsWith("*")) {
              tag1 = tagStr.substring(0, tagStr.length() - 1);
              tag2 = "RB";
            }
            else if (tagStr.contains("+")) {
              String[] tagSplits = tagStr.split("\\+");
              tag1 = tagSplits[0];
              tag2 = tagSplits[1];
            }
            
            else {
              log.warn("Cannot split brown POS tag: {0} ({1})", tagStr, str);
            }

            if (tag1 != null && tag2 != null) {
              tagBrownToken(controlDoc, str.substring(0, str.length() - terminator.length()),
                            tag1, offset);
              offset += str.length() - terminator.length();
              tagBrownToken(controlDoc, terminator, tag2, offset);
              offset += terminator.length() + 1;
              handled = true;
              break;
            }
          }
        }
      }

      if (!handled) {
        tagBrownToken(controlDoc, str, tagStr, offset);
        offset += str.length() + 1;
      }
    }

    Document testDoc = new Document(buffer.toString());
    Tokenizer tokenizer = new Tokenizer();
    tokenizer.tokenize(testDoc);
    POSTagger tagger = new POSTagger();
    tagger.load();
    tagger.process(testDoc);

    Iterator<Token> controlIt = controlDoc.iterator();
    Iterator<Token> testIt = testDoc.iterator();

    int match = 0;
    int total = 0;
    while (controlIt.hasNext() && testIt.hasNext()) {
      Token controlTok = controlIt.next();
      Token testTok = testIt.next();

      if (controlTok == null || testTok == null) {
        log.warn("Null token in doc!");
        continue;
      }

      if (testTok.getPOSTag() == null) {
        log.warn("Tagger left a null token! {0}", testTok.asString());
        continue;
      }

      if (!controlTok.asString().equals(testTok.asString())) {
        log.warn("Token strings don''t match: \"{0}\" != \"{1}\" (POS: {2})",
                 controlTok.asString(), testTok.asString(), controlTok.getPOSTag());
        break;
      }

      if (testTok.getPOSTag().equals(controlTok.getPOSTag()))
        ++match;
      ++total;
    }

    log.info("Score: {0}%", (100.0 * ((double) match / (double) total)));
  }

  public static void main(String[] args) throws Exception {
    File brownFile = new File(args[0]);

    BrownPOSScorer scorer = new BrownPOSScorer(brownFile);
    scorer.score();
  }
}
