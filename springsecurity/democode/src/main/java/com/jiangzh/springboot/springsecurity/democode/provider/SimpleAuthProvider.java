package com.jiangzh.springboot.springsecurity.democode.provider;

import com.jiangzh.springboot.springsecurity.democode.dao.pojo.AdminUser;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.provider
 * @description : 最简单的Provider实现
 * @date : 2019-06-06 17:32
 **/
@Component
public class SimpleAuthProvider implements AuthenticationProvider {

  private AdminUser adminUser;


  public SimpleAuthProvider() {
    adminUser =
        AdminUser.builder()
            .id(100)
            .username("jiangzh")
            .password("jiangzh")
            .role("MANAGER")
            .realname("蒋征")
            .mobile("18500000000")
            .state(0)
            .info("宇宙级的厚脸皮")
            .build();
  }

  /** 
  * @Description: 认证信息方法 
  * @Param: [authentication] 
  * @return: org.springframework.security.core.Authentication 
  * @Author: jiangzh 
  * @Date: 2019/6/6 
  */ 
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    // 初始化role集合
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    grantedAuthorities.add(new SimpleGrantedAuthority(adminUser.getRole()));

    if(checkAuthInfo(authentication)){
      return new UsernamePasswordAuthenticationToken(adminUser,authentication.getCredentials(),grantedAuthorities);
    }
    // 返回null，SpringSecurity会继续寻找下一个Provider进行处理，抛异常则直接结束
    return null;
  }

  /**
  * @Description: 判断是否适合该Provider进行验证
  * @Param: [aClass]
  * @return: boolean
  * @Author: jiangzh
  * @Date: 2019/6/6
  */
  @Override
  public boolean supports(Class<?> aClass) {
    return true;
  }

  /**
  * @Description: 验证SpringSecurity传入的认证信息与用户信息是否匹配
  * @Param: [authentication]
  * @return: boolean
  * @Author: jiangzh
  * @Date: 2019/6/6
  */ 
  private boolean checkAuthInfo(Authentication authentication){
    if(authentication.getName().equals(adminUser.getUsername()) && authentication.getCredentials().equals(adminUser.getPassword())){
      return true;
    }else{
      return false;
    }
  }

}
