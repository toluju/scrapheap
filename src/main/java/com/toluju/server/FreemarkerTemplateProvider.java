package com.toluju.server;

import com.sun.jersey.spi.template.TemplateProcessor;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.ext.Provider;
import com.toluju.util.Log;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

/**
 * Basic ideas taken from http://github.com/cwinters/jersey-freemarker
 * Modified heavily for my purposes.
 *
 * @author Chris Winters <chris@cwinters.com>
 * @author Toby Jungen
 */
@Provider
public class FreemarkerTemplateProvider implements TemplateProcessor {
  private static final Log log = new Log(FreemarkerTemplateProvider.class);

  public static final String DEFAULT_EXTENSION = ".ftl";
  private Configuration freemarkerConfig;

  @Override
  public String resolve(String path) {
    // accept both '/path/to/template' and '/path/to/template.ftl'
    return path.endsWith(DEFAULT_EXTENSION) ? path : path + DEFAULT_EXTENSION;
  }

  @Override
  public void writeTo(String resolvedPath, Object model, OutputStream out) throws IOException {
    out.flush(); // send status + headers

    Template template = freemarkerConfig.getTemplate(resolvedPath);
    Map<String, Object> vars = new HashMap<String, Object>();

    if (model instanceof Map) {
      vars.putAll((Map<String, Object>) model);
    }
    else {
      vars.put("it", model);
    }

    OutputStreamWriter writer = new OutputStreamWriter(out);

    try {
      template.process(vars, writer);
    }
    catch (Throwable t) {
      log.warn("Error processing template", t);
    }
  }

  @Context
  public void setServletContext(ServletContext context) {
    try {
      freemarkerConfig = new Configuration();
      String templatePath = context.getInitParameter("com.toluju.server.templatePath");
      File rootPath = new File(templatePath == null ? "templates" : templatePath);
      freemarkerConfig.setTemplateLoader(new FileTemplateLoader(rootPath));
      freemarkerConfig.setNumberFormat("0"); // don't always put a ',' in numbers
      freemarkerConfig.setLocalizedLookup(false); // don't look for list.en.ftl when list.ftl requested
      freemarkerConfig.setTemplateUpdateDelay(0); // don't cache
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}