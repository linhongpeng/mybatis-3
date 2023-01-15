package org.apache.ibatis.debug;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.debug.entity.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * jdbc操作数据库
 * <p>Statement与PreparedStatement</p>
 *
 * @author linhongpeng
 * @date 2022-09-13 22:35:11
 */
public class JdbcTest {

  private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
  private static final String URL = "jdbc:mysql://localhost:3306/test";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "123456";

  public static void main(String[] args) {
    System.out.println("Statement操作数据库----->start----->");
    testStatement();
    System.out.println("Statement操作数据库----->end----->");

    System.out.println();

    System.out.println("PreparedStatement操作数据库----->start----->");
    testPreparedStatement();
    System.out.println("PreparedStatement操作数据库----->end----->");

    System.out.println();

    System.out.println("使用DataSource获取数据库连接----->start----->");
    testDataSource();
    System.out.println("使用DataSource获取数据库连接----->end----->");
  }

  /**
   * 方式一：Statement操作数据库
   */
  private static void testStatement() {
    // 1.加载驱动
    try {
      Class.forName(DRIVER_NAME);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;
    List<User> userList = new ArrayList<>();
    try {
      // 2.建立数据库连接
      connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

      // 3.获取Statement
      statement = connection.createStatement();

      // 4.执行sql
      resultSet = statement.executeQuery("select * from t_user");

      // 5.处理结果集
      while (resultSet.next()) {
        System.out.println("id:" + resultSet.getInt("id") + ",name:" + resultSet.getString("name") + ",age:" + resultSet.getInt("age"));
        userList.add(new User(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("age")));
      }
      System.out.println("userList = " + JSON.toJSONString(userList));
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      // 6.关闭连接
      if (resultSet != null) {
        try {
          resultSet.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * 方式二：PreparedStatement操作数据库
   * <pre>
   *   PreparedStatement和Statement的区别：
   *   1.PreparedStatement是Statement的子接口，PreparedStatement继承了Statement的所有方法，PreparedStatement是Statement的预编译版本，PreparedStatement可以防止SQL注入。
   *   2.PreparedStatement可以防止SQL注入，Statement不可以防止SQL注入。
   *   3.PreparedStatement可以使用占位符，Statement不可以使用占位符。
   *   4.PreparedStatement可以使用批处理，Statement不可以使用批处理。
   *   5.PreparedStatement可以使用元数据，Statement不可以使用元数据。
   *   6.PreparedStatement可以使用游标，Statement不可以使用游标。
   *   7.PreparedStatement可以使用参数化的元数据，Statement不可以使用参数化的元数据。
   *   8.PreparedStatement可以使用自定义的游标，Statement不可以使用自定义的游标。
   *   9.PreparedStatement可以使用自定义的结果集，Statement不可以使用自定义的结果集。
   *   10.PreparedStatement可以使用自定义的参数，Statement不可以使用自定义的参数。
   * </pre>
   */
  private static void testPreparedStatement() {
    // 1.加载驱动
    try {
      Class.forName(DRIVER_NAME);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    List<User> userList = new ArrayList<>();
    try {
      // 2.建立数据库连接
      connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

      // 3.获取PreparedStatement
      preparedStatement = connection.prepareStatement("select * from t_user where id = ? and name = ?");
      preparedStatement.setObject(1, 2);
      preparedStatement.setObject(2, "李四");

      // 4.执行sql
      resultSet = preparedStatement.executeQuery();

      // 5.处理结果集
      while (resultSet.next()) {
        System.out.println("id:" + resultSet.getInt("id") + ",name:" + resultSet.getString("name") + ",age:" + resultSet.getInt("age"));
        userList.add(new User(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("age")));
      }
      System.out.println("userList = " + JSON.toJSONString(userList));
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      // 6.关闭连接
      if (resultSet != null) {
        try {
          resultSet.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (preparedStatement != null) {
        try {
          preparedStatement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * 使用DataSource获取数据库连接
   * <pre>
   *   DataSource是JDBC 2.0中新增的接口，它是一个连接池的管理接口，DataSource接口的实现类有很多，比如：C3P0、DBCP、Proxool等。
   *   DataSource接口的实现类都是连接池的实现类，DataSource接口的实现类都是线程安全的，DataSource接口的实现类都是单例的。
   * </pre>
   *
   * <pre>
   *   DataSource与DriverManager的区别：
   *   1.DataSource是JDBC 2.0中新增的接口，DriverManager是JDBC 1.0中的接口。
   *   2.DataSource是连接池的管理接口，DriverManager是驱动管理接口。
   *   3.DataSource是线程安全的，DriverManager不是线程安全的。
   *   4.DataSource是单例的，DriverManager不是单例的。
   *   5.DataSource是接口，DriverManager是类。
   *   6.DataSource是连接池的实现类，DriverManager是驱动的实现类。
   *   7.DataSource是数据库厂商提供的，DriverManager是JDBC规范提供的。
   * </pre>
   */
  private static void testDataSource() {
    // 1.创建数据源
    PooledDataSource dataSource = new PooledDataSource();

    // 2.设置数据源属性
    dataSource.setDriver(DRIVER_NAME);
    dataSource.setUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    List<User> userList = new ArrayList<>();
    try {
      // 2.建立数据库连接
      connection = dataSource.getConnection();

      // 3.获取PreparedStatement
      preparedStatement = connection.prepareStatement("select * from t_user where id = ? and name = ?");
      preparedStatement.setObject(1, 2);
      preparedStatement.setObject(2, "李四");

      // 4.执行sql
      resultSet = preparedStatement.executeQuery();

      // 5.处理结果集
      while (resultSet.next()) {
        System.out.println("id:" + resultSet.getInt("id") + ",name:" + resultSet.getString("name") + ",age:" + resultSet.getInt("age"));
        userList.add(new User(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getInt("age")));
      }
      System.out.println("userList = " + JSON.toJSONString(userList));
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      // 6.关闭连接
      if (resultSet != null) {
        try {
          resultSet.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (preparedStatement != null) {
        try {
          preparedStatement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
