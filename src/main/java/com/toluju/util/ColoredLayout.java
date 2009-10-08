package com.toluju.util;

import java.util.*;

import org.apache.log4j.*;
import org.apache.log4j.spi.*;

public class ColoredLayout extends SimpleLayout {
  public ColoredLayout() {
    super();
  }

  @Override
  public String format(LoggingEvent event) {
    StringBuilder builder = new StringBuilder();

    if (event.getLevel().equals(Level.INFO)) {
      builder.append("\033[32m");
    }
    else if (event.getLevel().equals(Level.WARN)) {
      builder.append("\033[31m");
    }

    builder.append("[");
    builder.append(event.getLoggerName());
    builder.append("]");

    if (event.getLevel().equals(Level.INFO)) {
      builder.append("\033[0m");
    }
    else if (event.getLevel().equals(Level.WARN)) {
      builder.append("\033[0m");
    }

    builder.append(" ");
    builder.append(event.getMessage());

    builder.append("\n");

    return builder.toString();
  }
}
