package com.toluju.util;

import java.util.*;
import java.io.*;

public class Config {
  private static final Log log = new Log(Config.class); 

  protected static Properties properties = new Properties();

  private Config() {
    // Never instantiated
  }  

  public static void load(String name) throws IOException {
    synchronized (properties) {
      if (properties.isEmpty()) {
        InputStream stream = ClassLoader.getSystemResourceAsStream(name + ".cfg");
        
        if (stream != null) {
          properties.load(stream);
        }

        File file = new File(name + ".cfg");

        if (file.exists()) {
          Reader reader = new BufferedReader(new FileReader(file));
          properties.load(reader);
        }
      }
    }
  }

  public static String get(String name) {
    return properties.getProperty(name);
  }

  public static void main(String[] args) throws Exception {
    Config.load("util");
    log.info("Hello {0}!", Config.get("name"));
  }
}
