package net.betaheads.utils.db.datasources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import net.betaheads.BetaheadsStats.Config;
import net.betaheads.utils.PluginLogger;
import net.betaheads.utils.db.Datasource;
import net.betaheads.utils.db.entities.BlockStatEntity;
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

      if (!rs.next()) {
        return null;
      }

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

  @Override
  public int saveUser(UserEntity user) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn
          .prepareStatement("INSERT INTO users(name, played_ms) VALUES(?, ?);");

      statement.setString(1, user.name);
      statement.setLong(2, user.played_ms);

      return statement.executeUpdate();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());

      return -1;
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public int[] updateUserBatch(ArrayList<UserEntity> users) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      conn.setAutoCommit(false);

      statement = conn
          .prepareStatement("UPDATE users SET played_ms = ? WHERE name = ?;");

      for (UserEntity user : users) {
        statement.setLong(1, user.played_ms);
        statement.setString(2, user.name);

        statement.addBatch();
      }

      int[] res = statement.executeBatch();

      conn.commit();

      return res;
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());

      return null;
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public int updateUser(UserEntity user) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn
          .prepareStatement("UPDATE users SET played_ms = ? WHERE name = ?;");

      statement.setLong(1, user.played_ms);
      statement.setString(2, user.name);

      return statement.executeUpdate();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());

      return -1;
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public void createBlockStatsTable() {
    Connection conn = null;
    Statement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.createStatement();

      statement.execute(
          "CREATE TABLE block_stats (" +
              "id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
              "user_id INT NOT NULL," +
              "block VARCHAR(255) NOT NULL," +
              "action VARCHAR(50) NOT NULL," +
              "count BIGINT NOT NULL DEFAULT 0," +
              "INDEX IDX_user_id (user_id)" +
              ");");
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public int saveBlockStat(BlockStatEntity blockStat) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn
          .prepareStatement("INSERT INTO block_stats(user_id, block, action, count) VALUES(?, ?, ?, ?);");

      statement.setLong(1, blockStat.user_id);
      statement.setString(2, blockStat.block);
      statement.setString(3, blockStat.action);
      statement.setLong(4, blockStat.count);

      return statement.executeUpdate();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());

      return -1;
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public int[] updateBatchBlockStatsCounts(ArrayList<BlockStatEntity> blockStats) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      conn.setAutoCommit(false);

      statement = conn
          .prepareStatement("UPDATE block_stats SET count = ? WHERE id = ?;");

      for (BlockStatEntity stat : blockStats) {
        statement.setLong(1, stat.count);
        statement.setLong(2, stat.id);

        statement.addBatch();
      }

      int[] res = statement.executeBatch();

      conn.commit();

      return res;
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());

      return null;
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public ArrayList<BlockStatEntity> getUserBlockStats(String userId) {
    Connection conn = null;
    PreparedStatement statement = null;

    ArrayList<BlockStatEntity> result = null;

    try {
      conn = pool.getConnection();

      statement = conn.prepareStatement("SELECT id, user_id, block, action, count FROM block_stats WHERE user_id = ?;");

      statement.setString(1, userId);

      ResultSet rs = statement.executeQuery();

      result = new ArrayList<BlockStatEntity>();

      while (rs.next()) {
        BlockStatEntity entity = new BlockStatEntity();

        entity.id = rs.getLong("id");
        entity.user_id = rs.getLong("user_id");
        entity.block = rs.getString("block");
        entity.action = rs.getString("action");
        entity.count = rs.getLong("count");

        result.add(entity);
      }

      return result;
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());

      return null;
    } finally {
      closeQuery(conn, statement);
    }
  }
}
