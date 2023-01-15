package org.apache.ibatis.debug;

import org.apache.ibatis.debug.entity.User;
import org.apache.ibatis.debug.mapper.UserMapper;
import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ReuseExecutor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linhongpeng
 * @date 2022-09-13 22:34:54
 */
public class ExecutorTest {

  private SqlSessionFactory sqlSessionFactory;
  private Configuration configuration;
  private Connection connection;
  private JdbcTransaction jdbcTransaction;

  private static final String URL = "jdbc:mysql://localhost:3306/test";
  private static final String USERNAME = "root";
  private static final String PASSWORD = "123456";

  @Before
  public void setUp() throws IOException, SQLException {
    String resource = "org/apache/ibatis/debug/mybatis-config.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    configuration = sqlSessionFactory.getConfiguration();
    connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    jdbcTransaction = new JdbcTransaction(connection);
  }

  /**
   * SimpleExecutor：简单执行器。无论SQL是否一样，每次都会进行预编译
   *
   * @throws SQLException
   */
  @Test
  public void testSimpleExecutor() throws SQLException {
    SimpleExecutor executor = new SimpleExecutor(configuration, jdbcTransaction);
    MappedStatement ms = configuration.getMappedStatement("org.apache.ibatis.debug.mapper.UserMapper.selectUser");
    List<Object> list = executor.doQuery(ms, 2, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(2));
    // 相同SQL，还是进行了预编译
    executor.doQuery(ms, 2, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(2));
    System.out.println("list = " + list);
  }

  /**
   * ReuseExecutor：可重用执行器。相同的SQL语句只预编译一次
   *
   * @throws SQLException
   */
  @Test
  public void testReuseExecutor() throws SQLException {
    ReuseExecutor executor = new ReuseExecutor(configuration, jdbcTransaction);
    MappedStatement ms = configuration.getMappedStatement("org.apache.ibatis.debug.mapper.UserMapper.selectUser");
    List<Object> list = executor.doQuery(ms, 2, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(2));
    // 相同SQL，只预编译一次
    executor.doQuery(ms, 2, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(2));
    System.out.println("list = " + list);
  }

  /**
   * BatchExecutor：批处理执行器。只针对增删改操作，如果是查询操作的话，跟SimpleExecutor是没有任何区别的
   *
   * @throws SQLException
   */
  @Test
  public void testBatchExecutor1() throws SQLException {
    BatchExecutor executor = new BatchExecutor(configuration, jdbcTransaction);
    MappedStatement ms = configuration.getMappedStatement("org.apache.ibatis.debug.mapper.UserMapper.selectUser");
    List<Object> list = executor.doQuery(ms, 2, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(2));
    // 相同查询SQL，还是进行了预编译
    executor.doQuery(ms, 2, RowBounds.DEFAULT, SimpleExecutor.NO_RESULT_HANDLER, ms.getBoundSql(2));
    System.out.println("list = " + list);
  }

  /**
   * BatchExecutor：批处理执行器。相同的增删改SQL语句，只预编译一次
   *
   * @throws SQLException
   */
  @Test
  public void testBatchExecutor2() throws SQLException {
    BatchExecutor executor = new BatchExecutor(configuration, jdbcTransaction);
    MappedStatement ms = configuration.getMappedStatement("org.apache.ibatis.debug.mapper.UserMapper.updateName");
    Map<String, Object> param = new HashMap<>();
    param.put("id", 1);
    param.put("name", "张三10086");
    int updateCount = executor.doUpdate(ms, param);
    // 相同update SQL，这里只预编译一次
    executor.doUpdate(ms, param);
    executor.doUpdate(ms, param);
    // FIXME 批处理操作，尽管是设置自动提交的，也必须要手动刷新才会真正提交；上面的executor.doUpdate()可以认为是在设置参数
    executor.doFlushStatements(false);
  }

  /**
   * 总结：
   * SimpleExecutor：简单执行器。每次都会创建一个新的预处理器（PreparedStatement）
   * ReuseExecutor：可重用执行器。相同的SQL只进行一次预处理
   * BatchExecutor：批处理执行器。只针对增删改操作；批处理提交修改，必须执行doFlushStatements()才会生效
   */

  /**
   * SimpleExecutor、ReuseExecutor、BatchExecutor公共的方法交给父类：一级缓存、获取连接
   */

  /**
   * 上面的查询测试方法，都是直接调具体子类的doQuery()方法，这实际上是没有走一级缓存的，因为一级缓存写在父类BaseExecutor的query()方法里。
   * 接下去将改成调用父类BaseExecutor的query()方法，调试下SQL是否只执行一次。
   */

  /**
   * 测试一级缓存
   *
   * @throws SQLException
   */
  @Test
  public void testBaseExecutor() throws SQLException {
    /**
     * 这里不管是用SimpleExecutor或ReuseExecutor，都会命中一级缓存
     */
    SimpleExecutor executor = new SimpleExecutor(configuration, jdbcTransaction);
//    ReuseExecutor executor = new ReuseExecutor(configuration, jdbcTransaction);
    MappedStatement ms = configuration.getMappedStatement("org.apache.ibatis.debug.mapper.UserMapper.selectUser");
    executor.query(ms, 2, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
    /**
     * 下面两行命中了一级缓存，所以不会执行SQL，直接返回
     */
    executor.query(ms, 2, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
    executor.query(ms, 2, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
  }

  /**
   * 测试二级缓存
   *
   * @throws SQLException
   */
  @Test
  public void testCachingExecutor() throws SQLException {
    Executor executor = new SimpleExecutor(configuration, jdbcTransaction);
    CachingExecutor cachingExecutor = new CachingExecutor(executor);
    MappedStatement ms = configuration.getMappedStatement("org.apache.ibatis.debug.mapper.UserMapper.selectUser");
    cachingExecutor.query(ms, 2, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
    /**
     * 一级缓存是执行完一级缓存的逻辑，缓存里面立马有数据；而二级缓存不一样，二级缓存必须是提交之后，缓存里才有数据。
     * 因为二级缓存会进行跨线程的调用，而一级缓存不会。
     * 所以下方调commit，让查询完成，数据就会提交到二级缓存了。
     */
    cachingExecutor.commit(true);
    cachingExecutor.query(ms, 2, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
    cachingExecutor.query(ms, 2, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
    cachingExecutor.query(ms, 2, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
  }

  /**
   * 接下来通过SqlSession的方式，来调用查询操作。SqlSession帮助降低调用的复杂性。
   */

  /**
   * 用SqlSession的方式，来调用整个Executor的查询操作
   */
  @Test
  public void testSqlSession() {
    // 这里没有指定ExecutorType，默认就是SimpleExecutor；可以通过调用其他重载的方法传入ExecutorType
    SqlSession sqlSession = sqlSessionFactory.openSession(true);
    List<Object> list = sqlSession.selectList("org.apache.ibatis.debug.mapper.UserMapper.selectUser", 2);
    System.out.println("list = " + list);
  }

  /**
   * 用Mapper的方式，来调用整个Executor的查询操作
   */
  @Test
  public void testMapper() {
    SqlSession sqlSession = sqlSessionFactory.openSession(true);
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    List<User> list = mapper.selectUser(2);
    System.out.println("list = " + list);
  }

}
