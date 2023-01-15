/*
 *    Copyright 2009-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor;

/**
 * @author Clinton Begin
 */
public class ErrorContext {

  private static final String LINE_SEPARATOR = System.lineSeparator();
  private static final ThreadLocal<ErrorContext> LOCAL = ThreadLocal.withInitial(ErrorContext::new);

  /**
   * 存储仓库可以用来存储ErrorContext
   */
  private ErrorContext stored;
  /**
   * 如果是解析文件，比如xml,那么resource就是文件名，如果是请求网络，那就是url
   */
  private String resource;
  /**
   * 什么行为，是update还是insert还是什么
   */
  private String activity;
  /**
   * 存储哪个对象操作时发生异常
   * <p>如：### The error may involve defaultParameterMap</p>
   */
  private String object;
  /**
   * 存储异常的概览信息
   * <p>如：### Error querying database. Cause: java.sql.SQLSyntaxErrorException: Unknown column 'id2' in 'field list'</p>
   */
  private String message;
  /**
   * 存储发生日常的 SQL 语句
   * <p>如：### SQL: select id2, name, sex, phone from author where name = ?</p>
   */
  private String sql;
  /**
   * 存储详细的 Java 异常日志
   * <pre>
   *   如：### Cause: java.sql.SQLSyntaxErrorException: Unknown column 'id2' in 'field list'
   *                 at org.apache.ibatis.exceptions.ExceptionFactory.wrapException(ExceptionFactory.java:30)
   *                 at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:150)
   *                 at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:141)
   *                 at org.apache.ibatis.binding.MapperMethod.executeForMany(MapperMethod.java:139)
   *                 at org.apache.ibatis.binding.MapperMethod.execute(MapperMethod.java:76)
   * </pre>
   */
  private Throwable cause;

  private ErrorContext() {
  }

  public static ErrorContext instance() {
    return LOCAL.get();
  }

  public ErrorContext store() {
    ErrorContext newContext = new ErrorContext();
    newContext.stored = this;
    LOCAL.set(newContext);
    return LOCAL.get();
  }

  public ErrorContext recall() {
    if (stored != null) {
      LOCAL.set(stored);
      stored = null;
    }
    return LOCAL.get();
  }

  public ErrorContext resource(String resource) {
    this.resource = resource;
    return this;
  }

  public ErrorContext activity(String activity) {
    this.activity = activity;
    return this;
  }

  public ErrorContext object(String object) {
    this.object = object;
    return this;
  }

  public ErrorContext message(String message) {
    this.message = message;
    return this;
  }

  public ErrorContext sql(String sql) {
    this.sql = sql;
    return this;
  }

  public ErrorContext cause(Throwable cause) {
    this.cause = cause;
    return this;
  }

  public ErrorContext reset() {
    resource = null;
    activity = null;
    object = null;
    message = null;
    sql = null;
    cause = null;
    LOCAL.remove();
    return this;
  }

  @Override
  public String toString() {
    StringBuilder description = new StringBuilder();

    // message
    if (this.message != null) {
      description.append(LINE_SEPARATOR);
      description.append("### ");
      description.append(this.message);
    }

    // resource
    if (resource != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error may exist in ");
      description.append(resource);
    }

    // object
    if (object != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error may involve ");
      description.append(object);
    }

    // activity
    if (activity != null) {
      description.append(LINE_SEPARATOR);
      description.append("### The error occurred while ");
      description.append(activity);
    }

    // sql
    if (sql != null) {
      description.append(LINE_SEPARATOR);
      description.append("### SQL: ");
      description.append(sql.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim());
    }

    // cause
    if (cause != null) {
      description.append(LINE_SEPARATOR);
      description.append("### Cause: ");
      description.append(cause.toString());
    }

    return description.toString();
  }

}
