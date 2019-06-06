package com.jiangzh.springboot.springsecurity.democode.conf;

import com.jiangzh.springboot.springsecurity.democode.service.AuthService;
import com.jiangzh.springboot.springsecurity.democode.utils.CustomizeEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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

  /**
  * @Description: 构造方法，主要注入一些属性
  * @Param:
  * @return:
  * @Author: jiangzh
  * @Date:
  */
  @Autowired
  public SecurityConfig(AuthService authService) {
    this.authService = authService;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception{
    auth.userDetailsService(authService) // 获取待验证的对象【调用实现中的loadUserByUsername方法】
        .passwordEncoder(passwordEncoder());//采用自定义加密算法
  }

  @Bean
  public CustomizeEncoder passwordEncoder() {
    return new CustomizeEncoder();
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
