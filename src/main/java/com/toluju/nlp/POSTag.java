package com.toluju.nlp;

/**
 * @author Toby
 */
public enum POSTag {
  UNKNOWN,
  TICKQUOT,
  DBLDASH,
  COMMA,
  COLON,
  PERIOD,
  QUOT,
  DBLQUOT,
  LPAREN,
  RPAREN,
  $,
  HASH,
  CC,
  CD,
  DT,
  EX,
  FW,
  IN,
  JJ,
  JJR,
  JJS,
  JJSS,
  LS,
  MD,
  NN,
  NNP,
  NNPS,
  NNS,
  NP,
  NPS,
  PDT,
  POS,
  PP,
  PRP,
  PRP$,
  PRP$R,
  RB,
  RBR,
  RBS,
  RP,
  SYM,
  TO,
  UH,
  VB,
  VBD,
  VBG,
  VBN,
  VBP,
  VBZ,
  WDT,
  WP,
  WP$,
  WRB;

  public static POSTag fromString(String str) {
    POSTag tag = null;

    try {
      tag = valueOf(str);
    }
    catch (IllegalArgumentException e) {
      // do nothing
    }

    if (tag == null) {
      if (str.equals("``")) tag = TICKQUOT;
      else if (str.equals("--")) tag = DBLDASH;
      else if (str.equals(":")) tag = COLON;
      else if (str.equals(",")) tag = COMMA;
      else if (str.equals(".")) tag = PERIOD;
      else if (str.equals("''")) tag = QUOT;
      else if (str.equals("\"")) tag = DBLQUOT;
      else if (str.equals("(")) tag = LPAREN;
      else if (str.equals(")")) tag = RPAREN;
      else if (str.equals("#")) tag = HASH;
    }

    return tag;
  }
}