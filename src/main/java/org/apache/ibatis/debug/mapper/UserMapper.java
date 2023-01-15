package org.apache.ibatis.debug.mapper;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.debug.entity.User;

import java.util.List;

/**
 * @author linhongpeng
 * @date 2022-09-13 22:27:17
 */
//@CacheNamespace(blocking = true)
@CacheNamespace
public interface UserMapper {

  @Select("select * from `t_user` where id = #{id}")
  List<User> selectUser(Integer id);

  @Update("update `t_user` set name = #{name} where id = #{id}")
  int updateName(@Param("id") Integer id, @Param("name") String name);

  User getById(Integer id);

}
