package com.toluju.nlp;

/**
 * @author Toby
 */
public class Token {
  protected Document document;
  protected int index = -1;
  protected int startOffset;
  protected int endOffset;
  protected POSTag tag;

  public Token(Document doc, int startOffset, int endOffset) {
    this.document = doc;
    this.startOffset = startOffset;
    this.endOffset = endOffset;
  }

  public Document getDocument() {
    return document;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public int getStartOffset() {
    return startOffset;
  }

  public int getEndOffset() {
    return endOffset;
  }

  public POSTag getPOSTag() {
    return tag;
  }

  public void setPOSTag(POSTag tag) {
    this.tag = tag;
  }

  public String asString() {
    return document.getRawString().substring(startOffset, endOffset);
  }

  @Override public String toString() {
    return asString() + " [" + startOffset + ", " + endOffset + "; " + tag + "]";
  }
}
