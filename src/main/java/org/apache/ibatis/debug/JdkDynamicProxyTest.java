package org.apache.ibatis.debug;

import org.apache.ibatis.debug.entity.User;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

/**
 * jdk动态代理测试
 *
 * @author linhongpeng
 * @date 2022-09-26 21:19:03
 */
public class JdkDynamicProxyTest {

  public static void main(String[] args) {
    UserMapper userMapper = (UserMapper) Proxy.newProxyInstance(JdkDynamicProxyTest.class.getClassLoader(), new Class[]{UserMapper.class}, new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) {
        System.out.println("进入动态代理方法，方法名为" + method.getName() + "，参数为" + Arrays.toString(args));
        Class<?> returnType = method.getReturnType();
        if (returnType == String.class) {
          return new User(1, "王五", 18);
        } else if (returnType == List.class) {
          return Arrays.asList(new User(2, "张三", 18), new User(3, "李四", 19));
        } else {
          return null;
        }
      }
    });
    List<User> users = userMapper.listUser();
    System.out.println(users);
    User user = userMapper.getUser();
    System.out.println(user);
  }

  /**
   * 任意定义一个接口
   */
  interface UserMapper {
    List<User> listUser();

    User getUser();
  }

}
