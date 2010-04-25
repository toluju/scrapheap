package com.toluju.server;

import com.sun.jersey.api.view.Viewable;
import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * @author tjungen
 */
public class ServerTester {
  public static void main(String[] args) throws Exception {
    Server server = new Server();
    server.addResource(TestResource.class);
    server.setTemplatePath(new File("templ"));
    server.start();
  }

  @Path("/")
  public static class TestResource {
    @GET
    public String printHelloWorld() {
      return "Hello World";
    }

    @GET @Path("test")
    public Viewable printTest() {
      return new Viewable("/test.ftl", "Woo!");
    }

    @GET @Path("query")
    public String processQuery(@QueryParam("q") String query) {
      return "Hello, your query was " + query;
    }
  }
}
