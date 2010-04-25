package com.toluju.server;

import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.toluju.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.servlet.GzipFilter;

public class Server {
  private static final Log log = new Log(Server.class);

  protected org.mortbay.jetty.Server server;

  protected int port = 8080;
  protected boolean enableGzip = true;
  protected File filePath = new File("www");
  protected File templatePath = new File("templates");
  protected org.mortbay.jetty.Server jettyServer;
  protected Set<Class> resourceClasses = new HashSet<Class>();

  public Server() {
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setFilePath(File filePath) {
    this.filePath = filePath;
  }

  public void setTemplatePath(File templatePath) {
    this.templatePath = templatePath;
  }

  public void addResource(Class klass) {
    resourceClasses.add(klass);
  }

  public void setEnableGzip(boolean enableGzip) {
    this.enableGzip = enableGzip;
  }

  public void start() throws Exception {
    ServletHolder sh = new ServletHolder(ServletContainer.class);

    sh.setInitParameter(ServletContainer.RESOURCE_CONFIG_CLASS,
                        ClassNamesResourceConfig.class.getName());
    sh.setInitParameter("com.toluju.server.filePath", filePath.getAbsolutePath());

    resourceClasses.add(FreemarkerTemplateProvider.class);
    List<String> classNames = new ArrayList<String>();
    for (Class klass : resourceClasses) classNames.add(klass.getName());
    sh.setInitParameter(ClassNamesResourceConfig.PROPERTY_CLASSNAMES,
                        StringUtils.join(classNames, ";"));

    jettyServer = new org.mortbay.jetty.Server(port);

    Context context = new Context(jettyServer, "/", true, false);
    context.getInitParams().put("com.toluju.server.templatePath",
                                templatePath.getAbsolutePath());

    if (enableGzip) {
      context.addFilter(GzipFilter.class, "/*", Handler.ALL);
    }
    
    context.addServlet(sh, "/*");

    jettyServer.start();
    String host = InetAddress.getLocalHost().getHostName();
    log.info("Server started on http://{0}:{1}/", host, Integer.toString(port));
  }

  public void stop() throws Exception {
    jettyServer.stop();
  }

  public static class ServletContainer
         extends com.sun.jersey.spi.container.servlet.ServletContainer {
    protected FileTypeMap mimeTypes = new MimetypesFileTypeMap();
    protected File rootFile;

    @Override
    public void init(ServletConfig config) throws ServletException {
      super.init(config);
      rootFile = new File(config.getInitParameter("com.toluju.server.filePath"));
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
      String path = request.getPathInfo();

      if (path.length() > 1) {
        path = path.substring(1);
        File file = new File(rootFile, path);

        if (file.exists()) {
          response.setContentType(mimeTypes.getContentType(path));
          InputStream stream = new FileInputStream(file);
          IOUtils.copy(stream, response.getOutputStream());
          stream.close();
        }
        else {
          super.service(request, response);
        }
      }
      else {
        super.service(request, response);
      }
    }
  }
}
