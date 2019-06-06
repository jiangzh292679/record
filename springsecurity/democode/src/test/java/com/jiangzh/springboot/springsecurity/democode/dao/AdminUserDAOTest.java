package com.jiangzh.springboot.springsecurity.democode.dao;

import com.jiangzh.springboot.springsecurity.democode.DemoCodeApplicationTests;
import com.jiangzh.springboot.springsecurity.democode.dao.pojo.AdminUser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.dao
 * @description : 测试AdminUser
 * @date : 2019-06-06 14:09
 **/
public class AdminUserDAOTest extends DemoCodeApplicationTests {

  @Autowired
  private AdminUserDAO adminUserDAO;

  @Test
  public void loginTest(){
    String username = "admin";
    String password = "admin";

    AdminUser login = adminUserDAO.login(username);

    System.out.println("loginUser="+login);

  }

}
