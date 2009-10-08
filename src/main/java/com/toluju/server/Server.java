package com.toluju.server;

import com.toluju.util.*;

import java.util.*;

import org.mortbay.jetty.servlet.*;
import org.apache.jasper.servlet.*;

public class Server {
  private static final Log log = new Log(Server.class);

  protected org.mortbay.jetty.Server server;

  public Server() {
    server = new org.mortbay.jetty.Server(8300);
    Context context = new Context(server, "/", true, true);
    context.setResourceBase("www");
    Holder holder = context.addServlet(DefaultServlet.class, "/");
    holder.setInitParameter("org.mortbay.jetty.servlet.Default.dirAllowed", "true");
    context.addServlet(JspServlet.class, "*.jsp");
  }

  public void start() {
    try {
      server.start();
    }
    catch (Exception e) {
      log.warn("Exception starting server", e);
    }
  }

  public static void main(String[] args) throws Exception {
    log.info("Starting server...");

    Server server = new Server();
    server.start();

    log.info("Server started!");
  }
}
