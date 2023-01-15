package org.apache.ibatis.debug.entity;

import java.io.Serializable;

/**
 * <pre>
 *    CREATE TABLE t_user ( id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键id', NAME VARCHAR ( 32 ) COMMENT '姓名', age INT COMMENT '年龄', PRIMARY KEY ( id ) ) COMMENT = '用户表';
 *
 *    insert into `t_user` (`id`, `name`, `age`) values ('1', '张三', 11);
 *    insert into `t_user` (`id`, `name`, `age`) values ('2', '李四', 22);
 *    insert into `t_user` (`id`, `name`, `age`) values ('3', '王五', 33);
 * </pre>
 *
 * @author linhongpeng
 * @date 2022-09-13 22:27:47
 */
public class User implements Serializable {

  private static final long serialVersionUID = -6468599516346399157L;

  private Integer id;

  private String name;

  private Integer age;

  public User() {
  }

  public User(Integer id, String name, Integer age) {
    this.id = id;
    this.name = name;
    this.age = age;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", age=" + age +
      '}';
  }

}
