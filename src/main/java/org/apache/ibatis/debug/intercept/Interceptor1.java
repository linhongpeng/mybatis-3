package org.apache.ibatis.debug.intercept;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * @author linhongpeng
 * @date 2022-11-01 20:21:23
 */
@Intercepts(value = {
  @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class Interceptor1 implements Interceptor {

  private static final Log log = LogFactory.getLog(Interceptor1.class);

  private Properties properties;

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    System.out.println("拦截方法 -----> Executor#query(MappedStatement, Object, RowBounds, ResultHandler)");
    System.out.println("解析 -----> properties:" + properties);
    return invocation.proceed();
  }

  @Override
  public Object plugin(Object target) {
    // 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身，减少目标被代理的次数
    if (target instanceof Executor) {
      return Plugin.wrap(target, this);
    } else {
      return target;
    }
  }

  /**
   * 测试填充拦截器的properties
   *
   * @param properties
   */
  @Override
  public void setProperties(Properties properties) {
    this.properties = properties;
  }

}
