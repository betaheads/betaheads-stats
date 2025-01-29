package net.betaheads.BetaheadsStats;

import java.io.File;

import org.bukkit.util.config.Configuration;

public class Config {
  static private Configuration configuration = null;

  static private String CONFIGURATION_FILE_PATH = "./plugins/BetaheadsStats/config.yml";

  static void loadConfigFile() {
    File file = new File(CONFIGURATION_FILE_PATH);

    configuration = new Configuration(file);

    configuration.load();

    setDefaults();
  }

  static void setDefaults() {
    getMysqlUsername();
    getMysqlPassword();
    getMysqlHost();
    getMysqlPort();
    getMysqlDb();
    getMysqlNewAuthMethod();
    writeMySqlNewAuthInfo();

    configuration.save();
  }

  public static String getMysqlUsername() {
    return configuration.getString("mysql.username", "user");
  }

  public static String getMysqlPassword() {
    return configuration.getString("mysql.password", "pass");
  }

  public static String getMysqlHost() {
    return configuration.getString("mysql.host", "localhost");
  }

  public static String getMysqlPort() {
    return configuration.getString("mysql.port", "3306");
  }

  public static String getMysqlDb() {
    return configuration.getString("mysql.database", "headstats");
  }

  public static Boolean getMysqlNewAuthMethod() {
    return configuration.getBoolean("mysql.newAuthMethod", false);
  }

  private static void writeMySqlNewAuthInfo() {
    configuration.getString("mysql.newAuthMethodInfo",
        "If you use 'newAuthMethod: true' you need to download mysql connector jar to your /lib folder and name it 'mysql-connector.jar'");
  }
}
