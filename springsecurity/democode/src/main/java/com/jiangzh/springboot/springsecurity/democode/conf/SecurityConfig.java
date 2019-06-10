package com.jiangzh.springboot.springsecurity.democode.conf;

import com.jiangzh.springboot.springsecurity.democode.provider.SimpleAuthProvider;
import com.jiangzh.springboot.springsecurity.democode.service.AuthService;
import com.jiangzh.springboot.springsecurity.democode.utils.CustomizeEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.conf
 * @description : SpringSecurityConfig配置
 * @date : 2019-06-06 14:57
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  /** 权限Service */
  private final AuthService authService;
  /** 提供第二种用户验证Provider */
  private final SimpleAuthProvider simpleAuthProvider;

  /**
  * @Description: 构造方法，主要注入一些属性
  * @Param:
  * @return:
  * @Author: jiangzh
  * @Date:
  */
  @Autowired
  public SecurityConfig(AuthService authService,SimpleAuthProvider simpleAuthProvider) {
    this.authService = authService;
    this.simpleAuthProvider = simpleAuthProvider;
  }

  /**
  * @Description: 提供自定义密码加密策略
  * @Param: []
  * @return: com.jiangzh.springboot.springsecurity.democode.utils.CustomizeEncoder
  * @Author: jiangzh
  * @Date: 2019/6/6
  */
  @Bean
  public CustomizeEncoder passwordEncoder() {
    return new CustomizeEncoder();
  }

  /*
    增加数据层获取用户信息
   */
  @Bean
  DaoAuthenticationProvider daoAuthenticationProvider(){
    DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
    daoAuthenticationProvider.setUserDetailsService(authService);
    return daoAuthenticationProvider;
  }


  /**
  * @Description: 提供默认UserDetailsService实现
  * @Param: []
  * @return: org.springframework.security.core.userdetails.UserDetailsService
  * @Author: jiangzh
  * @Date: 2019/6/6
  */
  @Override
  protected UserDetailsService userDetailsService() {
    return authService;
  }


  /**
   * @Description: 配置多个Provider内容
   * @Param: []
   * @return: org.springframework.security.authentication.AuthenticationManager
   * @Author: jiangzh
   * @Date: 2019/6/6
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(simpleAuthProvider).authenticationProvider(daoAuthenticationProvider());
  }

  /**
  * @Description: 权限控制的主要部分
  * @Param: [http]
  * @return: void
  * @Author: jiangzh
  * @Date: 2019/6/6
  */
  @Override
  protected void configure(HttpSecurity http)
      throws Exception {
    http.authorizeRequests().
        antMatchers("/")
        .permitAll()
        .antMatchers("/manage/**").hasRole("ADMIN")
        .anyRequest().authenticated().
        and().
        formLogin().
        loginProcessingUrl("/login").
        usernameParameter("username")
        .passwordParameter("password")
        .defaultSuccessUrl("/index")//成功后跳转界面
        .and()
        .logout().logoutUrl("/logout")//登出默认api
        .and()
        //开启cookie保存用户数据
        .rememberMe()
        //设置cookie有效期
        .tokenValiditySeconds(60 * 60 * 24 * 7)
        .and()
        .logout().permitAll()
        .and().csrf().disable();
  }

}
