package net.betaheads.utils.db.datasources;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import net.betaheads.BetaheadsStats.Config;
import net.betaheads.utils.PluginLogger;
import net.betaheads.utils.db.Datasource;
import net.betaheads.utils.db.entities.ActivityStatEntity;
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
      statement = conn.prepareStatement(
          "SELECT id, name, played_ms, first_login_at, last_login_at, last_seen_at, login_count FROM users WHERE name = ?;");

      statement.setString(1, username);

      ResultSet rs = statement.executeQuery();

      if (!rs.next()) {
        return null;
      }

      UserEntity user = new UserEntity();

      user.id = rs.getLong("id");
      user.name = rs.getString("name");
      user.played_ms = rs.getLong("played_ms");
      user.first_login_at = rs.getTimestamp("first_login_at");
      user.last_login_at = rs.getTimestamp("last_login_at");
      user.last_seen_at = rs.getTimestamp("last_seen_at");
      user.login_count = rs.getLong("login_count");

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
          .prepareStatement(
              "INSERT INTO users(name, display_name, played_ms, first_login_at, last_login_at, last_seen_at, login_count) VALUES(?, ?, ?, ?, ?, ?, ?);");

      statement.setString(1, user.name);
      statement.setString(2, user.display_name);
      statement.setLong(3, user.played_ms);
      statement.setTimestamp(4, user.first_login_at);
      statement.setTimestamp(5, user.last_login_at);
      statement.setTimestamp(6, user.last_seen_at);
      statement.setLong(7, user.login_count);

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
          .prepareStatement("UPDATE users SET played_ms = ?, last_seen_at = ? WHERE name = ?;");

      for (UserEntity user : users) {
        statement.setLong(1, user.played_ms);
        statement.setTimestamp(2, user.last_seen_at);
        statement.setString(3, user.name);

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
          .prepareStatement("UPDATE users SET played_ms = ?, last_seen_at = ? WHERE name = ?;");

      statement.setLong(1, user.played_ms);
      statement.setTimestamp(2, user.last_seen_at);
      statement.setString(3, user.name);

      return statement.executeUpdate();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());

      return -1;
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public int updateUserLogin(UserEntity user) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn
          .prepareStatement("UPDATE users SET last_login_at = ?, login_count = ? WHERE name = ?;");

      statement.setTimestamp(1, user.last_login_at);
      statement.setLong(2, user.login_count);
      statement.setString(3, user.name);

      return statement.executeUpdate();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());

      return -1;
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public void addUserLoginColumns() {
    Connection conn = null;
    Statement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.createStatement();

      statement.execute(
          "ALTER TABLE users" +
              " ADD COLUMN first_login_at DATETIME NULL AFTER played_ms," +
              " ADD COLUMN last_login_at DATETIME NULL AFTER first_login_at," +
              " ADD COLUMN login_count BIGINT NOT NULL DEFAULT 0 AFTER last_login_at;");
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public void addLastSeenAtColumn() {
    Connection conn = null;
    Statement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.createStatement();

      // the column may already exist on DBs migrated by an early build
      ResultSet rs = statement.executeQuery("SHOW COLUMNS FROM users LIKE 'last_seen_at';");

      if (rs.next()) {
        return;
      }

      statement.execute("ALTER TABLE users ADD COLUMN last_seen_at DATETIME NULL AFTER last_login_at;");
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
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
  public void saveBatchBlockStats(ArrayList<BlockStatEntity> blockStats) {
    ArrayList<BlockStatEntity> statsToInsert = new ArrayList<>();
    ArrayList<BlockStatEntity> statsToUpdate = new ArrayList<>();

    for (BlockStatEntity stat : blockStats) {
      if (stat.id == 0) {
        statsToInsert.add(stat);
      } else {
        statsToUpdate.add(stat);
      }
    }

    if (!statsToInsert.isEmpty()) {
      insertBatchBlockStats(statsToInsert);
      assignBlockStatsIds(statsToInsert);
    }

    if (!statsToUpdate.isEmpty()) {
      updateBatchBlockStatsCounts(statsToUpdate);
    }
  }

  private void insertBatchBlockStats(ArrayList<BlockStatEntity> blockStats) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      conn.setAutoCommit(false);

      statement = conn
          .prepareStatement("INSERT INTO block_stats(user_id, block, action, count) VALUES(?, ?, ?, ?)" +
              " ON DUPLICATE KEY UPDATE count = VALUES(count);");

      for (BlockStatEntity stat : blockStats) {
        statement.setLong(1, stat.user_id);
        statement.setString(2, stat.block);
        statement.setString(3, stat.action);
        statement.setLong(4, stat.count);

        statement.addBatch();
      }

      statement.executeBatch();

      conn.commit();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  private void assignBlockStatsIds(ArrayList<BlockStatEntity> blockStats) {
    HashSet<Long> userIds = new HashSet<>();

    for (BlockStatEntity stat : blockStats) {
      userIds.add(stat.user_id);
    }

    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.prepareStatement(
          "SELECT id, user_id, block, action FROM block_stats WHERE user_id IN (" + buildPlaceholders(userIds.size())
              + ");");

      int paramIndex = 1;
      for (Long userId : userIds) {
        statement.setLong(paramIndex++, userId);
      }

      ResultSet rs = statement.executeQuery();

      HashMap<String, Long> idsMap = new HashMap<>();

      while (rs.next()) {
        String key = rs.getLong("user_id") + ":" + rs.getString("action") + ":" + rs.getString("block");

        idsMap.put(key, rs.getLong("id"));
      }

      for (BlockStatEntity stat : blockStats) {
        Long id = idsMap.get(stat.user_id + ":" + stat.action + ":" + stat.block);

        if (id != null) {
          stat.id = id;
        }
      }
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  private void updateBatchBlockStatsCounts(ArrayList<BlockStatEntity> blockStats) {
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

      statement.executeBatch();

      conn.commit();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  private String buildPlaceholders(int count) {
    StringBuilder placeholders = new StringBuilder();

    for (int i = 0; i < count; i++) {
      if (i > 0) {
        placeholders.append(", ");
      }

      placeholders.append("?");
    }

    return placeholders.toString();
  }

  @Override
  public ArrayList<BlockStatEntity> getUserBlockStats(Long userId) {
    Connection conn = null;
    PreparedStatement statement = null;

    ArrayList<BlockStatEntity> result = null;

    try {
      conn = pool.getConnection();

      statement = conn.prepareStatement("SELECT id, user_id, block, action, count FROM block_stats WHERE user_id = ?;");

      statement.setLong(1, userId);

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

  @Override
  public void addDisplayNameColumn() {
    Connection conn = null;
    Statement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.createStatement();

      statement.execute("ALTER TABLE users ADD COLUMN display_name VARCHAR(255) AFTER name;");
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public void createActivityStatsTable() {
    Connection conn = null;
    Statement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.createStatement();

      statement.execute(
          "CREATE TABLE activity_stats (" +
              "id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
              "user_id INT NOT NULL," +
              "activity VARCHAR(255) NOT NULL," +
              "type VARCHAR(50) NOT NULL," +
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
  public void saveBatchActivityStats(ArrayList<ActivityStatEntity> activityStats) {
    ArrayList<ActivityStatEntity> statsToInsert = new ArrayList<>();
    ArrayList<ActivityStatEntity> statsToUpdate = new ArrayList<>();

    for (ActivityStatEntity stat : activityStats) {
      if (stat.id == 0) {
        statsToInsert.add(stat);
      } else {
        statsToUpdate.add(stat);
      }
    }

    if (!statsToInsert.isEmpty()) {
      insertBatchActivityStats(statsToInsert);
      assignActivityStatsIds(statsToInsert);
    }

    if (!statsToUpdate.isEmpty()) {
      updateBatchActivityStatsCounts(statsToUpdate);
    }
  }

  private void insertBatchActivityStats(ArrayList<ActivityStatEntity> activityStats) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      conn.setAutoCommit(false);

      statement = conn
          .prepareStatement("INSERT INTO activity_stats(user_id, activity, type, count) VALUES(?, ?, ?, ?)" +
              " ON DUPLICATE KEY UPDATE count = VALUES(count);");

      for (ActivityStatEntity stat : activityStats) {
        statement.setLong(1, stat.user_id);
        statement.setString(2, stat.activity);
        statement.setString(3, stat.type);
        statement.setLong(4, stat.count);

        statement.addBatch();
      }

      statement.executeBatch();

      conn.commit();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  private void assignActivityStatsIds(ArrayList<ActivityStatEntity> activityStats) {
    HashSet<Long> userIds = new HashSet<>();

    for (ActivityStatEntity stat : activityStats) {
      userIds.add(stat.user_id);
    }

    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.prepareStatement(
          "SELECT id, user_id, activity, type FROM activity_stats WHERE user_id IN ("
              + buildPlaceholders(userIds.size()) + ");");

      int paramIndex = 1;
      for (Long userId : userIds) {
        statement.setLong(paramIndex++, userId);
      }

      ResultSet rs = statement.executeQuery();

      HashMap<String, Long> idsMap = new HashMap<>();

      while (rs.next()) {
        String key = rs.getLong("user_id") + ":" + rs.getString("type") + ":" + rs.getString("activity");

        idsMap.put(key, rs.getLong("id"));
      }

      for (ActivityStatEntity stat : activityStats) {
        Long id = idsMap.get(stat.user_id + ":" + stat.type + ":" + stat.activity);

        if (id != null) {
          stat.id = id;
        }
      }
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  private void updateBatchActivityStatsCounts(ArrayList<ActivityStatEntity> activityStats) {
    Connection conn = null;
    PreparedStatement statement = null;

    try {
      conn = pool.getConnection();

      conn.setAutoCommit(false);

      statement = conn
          .prepareStatement("UPDATE activity_stats SET count = ? WHERE id = ?;");

      for (ActivityStatEntity stat : activityStats) {
        statement.setLong(1, stat.count);
        statement.setLong(2, stat.id);

        statement.addBatch();
      }

      statement.executeBatch();

      conn.commit();
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public void addStatsUniqueIndexes() {
    Connection conn = null;
    Statement statement = null;

    try {
      conn = pool.getConnection();

      statement = conn.createStatement();

      // merge counts of possible duplicates into the row with the lowest id,
      // then drop the extra rows, so the unique indexes can be created
      statement.executeUpdate(
          "UPDATE activity_stats s" +
              " JOIN (SELECT MIN(id) AS keep_id, user_id, type, activity, SUM(count) AS total" +
              "   FROM activity_stats GROUP BY user_id, type, activity HAVING COUNT(*) > 1) d" +
              " ON s.id = d.keep_id" +
              " SET s.count = d.total;");

      statement.executeUpdate(
          "DELETE s FROM activity_stats s" +
              " JOIN (SELECT MIN(id) AS keep_id, user_id, type, activity" +
              "   FROM activity_stats GROUP BY user_id, type, activity HAVING COUNT(*) > 1) d" +
              " ON s.user_id = d.user_id AND s.type = d.type AND s.activity = d.activity AND s.id <> d.keep_id;");

      statement.execute(
          "ALTER TABLE activity_stats ADD UNIQUE INDEX UQ_user_type_activity (user_id, type, activity(100));");

      statement.executeUpdate(
          "UPDATE block_stats s" +
              " JOIN (SELECT MIN(id) AS keep_id, user_id, action, block, SUM(count) AS total" +
              "   FROM block_stats GROUP BY user_id, action, block HAVING COUNT(*) > 1) d" +
              " ON s.id = d.keep_id" +
              " SET s.count = d.total;");

      statement.executeUpdate(
          "DELETE s FROM block_stats s" +
              " JOIN (SELECT MIN(id) AS keep_id, user_id, action, block" +
              "   FROM block_stats GROUP BY user_id, action, block HAVING COUNT(*) > 1) d" +
              " ON s.user_id = d.user_id AND s.action = d.action AND s.block = d.block AND s.id <> d.keep_id;");

      statement.execute(
          "ALTER TABLE block_stats ADD UNIQUE INDEX UQ_user_action_block (user_id, action, block(100));");
    } catch (Exception e) {
      PluginLogger.error(e.getMessage());
    } finally {
      closeQuery(conn, statement);
    }
  }

  @Override
  public ArrayList<ActivityStatEntity> getUserActivityStats(Long userId) {
    Connection conn = null;
    PreparedStatement statement = null;

    ArrayList<ActivityStatEntity> result = null;

    try {
      conn = pool.getConnection();

      statement = conn.prepareStatement("SELECT id, user_id, activity, type, count FROM activity_stats WHERE user_id = ?;");

      statement.setLong(1, userId);

      ResultSet rs = statement.executeQuery();

      result = new ArrayList<ActivityStatEntity>();

      while (rs.next()) {
        ActivityStatEntity entity = new ActivityStatEntity();

        entity.id = rs.getLong("id");
        entity.user_id = rs.getLong("user_id");
        entity.activity = rs.getString("activity");
        entity.type = rs.getString("type");
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
