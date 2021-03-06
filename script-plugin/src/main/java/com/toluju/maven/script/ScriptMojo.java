package com.toluju.maven.script;

import java.util.*;
import java.io.*;

import org.apache.maven.plugin.*;
import org.apache.maven.project.*;

/**
 * @goal generate
 */
public class ScriptMojo extends AbstractMojo {

  /**
   * The Maven project object
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * @parameter
   */
  private Script[] scripts;

  /**
   * @parameter expression="${project.build.directory}/bin"
   * @required
   */
  private File scriptDirectory;

  public void execute() throws MojoExecutionException {
    StringBuilder builder = new StringBuilder();

    Set classPathElements = new LinkedHashSet();
    
    try {
      classPathElements.addAll(project.getRuntimeClasspathElements());
      classPathElements.addAll(project.getTestClasspathElements());
    }
    catch (Exception e) {
      throw new MojoExecutionException("Exception getting classpath elements", e);
    }

    Iterator iterator = classPathElements.iterator();

    while (iterator.hasNext()) {
      builder.append(iterator.next());
      builder.append(File.pathSeparator);
    }

    try {
      scriptDirectory.mkdirs();
    }
    catch (Exception e) {
      throw new MojoExecutionException("Exception creating script output directory", e);
    }

    if (scripts != null) {
      for (int x = 0; x < scripts.length; ++x) {
        Script script = scripts[x];
        File scriptFile = new File(scriptDirectory, script.getScriptName());

        try {
          scriptFile.setExecutable(true);
          Writer writer = new BufferedWriter(new FileWriter(scriptFile));
          writer.write("java -cp " + builder.toString() + " " + script.getClassName() + " $@");
          writer.close();
        }
        catch (Exception e) {
          throw new MojoExecutionException("Exception writing script file", e);
        }

        try {
          Runtime.getRuntime().exec("chmod +x " + scriptFile.getAbsolutePath());
        }
        catch (Exception e) {
          throw new MojoExecutionException("Exception setting execute permissions on script", e);
        }
      }

      getLog().info("Created " + scripts.length + " script files.");
    }
  }
}
