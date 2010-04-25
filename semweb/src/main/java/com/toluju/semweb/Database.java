package com.toluju.semweb;

import com.toluju.util.*;

import java.util.*;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.tdb.*;

public class Database {
  private static final Log log = new Log(Database.class);

  protected static Model model;

  public Database() {
    synchronized (Database.class) {
      if (model == null) {
        model = TDBFactory.createModel("/home/tjungen/prj/semweb/db/database");
      }
    }
  }

  public static void main(String[] args) {
    Database db = new Database();
  }
}
