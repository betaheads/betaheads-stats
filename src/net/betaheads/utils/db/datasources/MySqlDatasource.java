package net.betaheads.utils.db.datasources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import net.betaheads.BetaheadsStats.Config;
import net.betaheads.utils.PluginLogger;
import net.betaheads.utils.db.Datasource;
import net.betaheads.utils.db.entities.UserEntity;
import net.betaheads.utils.MySQLConnectionPool;

public class MySqlDatasource implements Datasource {
  private static MySQLConnectionPool pool;

  private static String dbUser;
  private static String dbPass;
  private static String dbHost;
  private static String dbPort;
  private static String dbName;

  @Override
  public DatasourceType getType() {
    return DatasourceType.MySQL;
  }

  @Override
  public void init() {
    dbUser = Config.getMysqlUsername();
    dbPass = Config.getMysqlPassword();
    dbHost = Config.getMysqlHost();
    dbPort = Config.getMysqlPort();
    dbName = Config.getMysqlDb();

    try {
      pool = new MySQLConnectionPool(buildConnectionString(),
          dbUser, dbPass);
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    }
  }

  private void closeQuery(Connection connection, Statement statement) {
    try {
      statement.close();
      connection.close();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    }
  }

  private String buildConnectionString() {
    return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?characterEncoding=utf8";
  }

  @Override
  public ArrayList<String> getAllWrittenMigrations() {
    Connection conn = null;
    Statement statement = null;

    ArrayList<String> result = null;

    try {
      conn = pool.getConnection();

      statement = conn.createStatement();

      ResultSet rs = statement.executeQuery("SELECT name FROM migrations;");

      result = new ArrayList<String>();

      while (rs.next()) {
        result.add(rs.getString("name"));
      }

      return result;
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());

      return null;
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public void writeMigration(String migrationName) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.prepareStatement("INSERT INTO migrations(name) VALUES(?);");

      statement.setString(1, migrationName);

      statement.executeUpdate();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public void createMigrationsTable() {
    Connection conn = null;
    Statement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.createStatement();

      statement.execute(
          "CREATE TABLE IF NOT EXISTS migrations (" +
              "    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY," +
              "    name VARCHAR(100)," +
              "    CONSTRAINT UQ_name UNIQUE (name)" +
              ");");
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public void createUsersTable() {
    Connection conn = null;
    Statement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.createStatement();

      statement.execute(
          "CREATE TABLE IF NOT EXISTS users (" +
              "    id INT AUTO_INCREMENT PRIMARY KEY," +
              "    name VARCHAR(255) UNIQUE," +
              "    played_ms BIGINT" +
              ");");
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public UserEntity getUser(String username) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();
      statement = conn.prepareStatement("SELECT id, name, played_ms FROM users WHERE name = ?;");

      statement.setString(1, username);

      ResultSet rs = statement.executeQuery();
      rs.next();

      UserEntity user = new UserEntity();

      user.id = rs.getLong("id");
      user.name = rs.getString("name");
      user.played_ms = rs.getLong("played_ms");

      return user;
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());

      return null;
    } finally {
      closeQuery(conn, statement);
    }
  }
}
