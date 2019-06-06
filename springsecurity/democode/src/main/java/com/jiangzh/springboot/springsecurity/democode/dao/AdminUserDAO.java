package com.jiangzh.springboot.springsecurity.democode.dao;

import com.jiangzh.springboot.springsecurity.democode.dao.pojo.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.dao
 * @description : 用户权限接口
 * @date : 2019-06-06 14:02
 **/
public interface AdminUserDAO extends JpaRepository<AdminUser,Integer> {

  @Query(
      value = " select u from AdminUser u where u.username=:username "
  )
  AdminUser login(@Param("username") String username);

}
