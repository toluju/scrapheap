package com.toluju.nlp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author Toby
 */
public class Document implements Iterable<Token> {
  protected String rawString;
  protected String id;
  protected List<Token> tokens = new ArrayList<Token>();

  public Document(String rawString) {
    this(rawString, null);
  }
  
  public Document(String rawString, String id) {
    this.rawString = rawString;
    
    if (id == null) id = UUID.randomUUID().toString();
  }

  @Override
  public Iterator<Token> iterator() {
    return tokens.iterator();
  }

  public String getRawString() {
    return rawString;
  }

  public String getId() {
    return id;
  }

  public void addToken(Token token) {
    token.setIndex(tokens.size());
    tokens.add(token);
  }
}
