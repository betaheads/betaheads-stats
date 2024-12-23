package net.betaheads.utils;

import java.util.logging.Logger;

public class PluginLogger {
  private static final String pluginPrefix = "[Betaheads] ";

  private static Logger logger = null;

  public static void setLogger(Logger localLogger) {
    logger = localLogger;
  }

  protected static Logger getLogger() {
    return logger;
  }

  public static void info(String log) {
    logger.info(pluginPrefix + log);
  }

  public static void warn(String log) {
    logger.warning(pluginPrefix + log);
  }

  public static void severe(String log) {
    logger.severe(pluginPrefix + log);
  }
}
