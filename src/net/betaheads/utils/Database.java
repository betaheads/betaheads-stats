package net.betaheads.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.betaheads.BetaheadsStats.Config;

public class Database {
  private static Logger log;
  private static MySQLConnectionPool pool;

  private static String dbUser;
  private static String dbPass;
  private static String dbHost;
  private static String dbPort;
  private static String dbName;

  // move logger to another class to make [NAMEDLOG]
  public static void init(Logger logger) {
    log = logger;

    dbUser = Config.getMysqlUsername();
    dbPass = Config.getMysqlPassword();
    dbHost = Config.getMysqlHost();
    dbPort = Config.getMysqlPort();
    dbName = Config.getMysqlDb();

    try {
      pool = new MySQLConnectionPool(buildConnectionString(),
          dbUser, dbPass);
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }

  public static ArrayList<ArrayList<Object>> executeQuery(String query, boolean withResult, int columnCount) {
    Connection conn = null;
    Statement statement = null;

    ArrayList<ArrayList<Object>> result = null;

    try {

      conn = pool.getConnection();

      statement = conn.createStatement();

      ResultSet data = statement.executeQuery(query);

      if (!withResult) {
        return result;
      }

      result = new ArrayList<ArrayList<Object>>();

      if (data.next()) {
        ArrayList<Object> row = new ArrayList<Object>();

        for (int i = 1; i <= columnCount; i++) {
          Object cell = data.getObject(i);

          row.add(cell);
        }

        result.add(row);
      }

      return result;

    } catch (Exception e) {
      log.info(e.getMessage());

      return null;
    } finally {
      closeQuery(conn, statement);
    }
  }

  static void closeQuery(Connection connection, Statement statement) {
    try {
      statement.close();
      connection.close();
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }

  static String buildConnectionString() {
    return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?characterEncoding=utf8";
  }
}
