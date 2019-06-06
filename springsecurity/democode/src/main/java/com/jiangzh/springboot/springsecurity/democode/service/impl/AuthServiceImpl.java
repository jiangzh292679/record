package com.jiangzh.springboot.springsecurity.democode.service.impl;

import com.jiangzh.springboot.springsecurity.democode.dao.AdminUserDAO;
import com.jiangzh.springboot.springsecurity.democode.dao.pojo.AdminUser;
import com.jiangzh.springboot.springsecurity.democode.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.service.impl
 * @description : 权限逻辑层实现
 * @date : 2019-06-06 14:50
 **/
@Service
public class AuthServiceImpl implements AuthService {

  /** 用户数据接口 */
  private final AdminUserDAO adminUserDAO;

  /** 
  * @Description: 接口实例化构造方法 
  * @Param:  
  * @return:  
  * @Author: jiangzh 
  * @Date:  
  */ 
  @Autowired
  public AuthServiceImpl(AdminUserDAO adminUserDAO) {
    this.adminUserDAO = adminUserDAO;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AdminUser loginUser = adminUserDAO.login(username);
    if(null == loginUser){
      throw new UsernameNotFoundException(" username invalid ! ");
    }
    return loginUser;
  }
}
