> 本章节主要是讲述如何定义多个认证实现，在现实业务场景中这是很有必要的，我们常见到的包括数据库认证，缓存认证等等

> 学习这一部分的前提是需要大家看过HelloWorld，并且手工构建过HelloWorld，因为我们本次的项目是在上一次的基础上进行的修改

## 1、AuthenticationProvider介绍
#### 1.1 AuthenticationProvider是什么

AuthenticationProvider是SpringSecurity依赖的用户认证实现，AuthenticationProvider可以提供多种实现，并且最终形成一个认证实现链路，只要这个链路中有一个认证正常，则表示认证成功

#### 1.2 AuthenticationProvider给我们提供了什么
```aidl
public interface AuthenticationProvider {
  Authentication authenticate(Authentication var1) throws AuthenticationException;

  boolean supports(Class<?> var1);
}
```
该接口主要提供了两个方法，包括authenticate和supports
- authenticate<br/>
  实现该方法，提供默认的认证实现.<br/>
  其中入参和返回值都是Authentication，后面我们会有单独的章节来讲述这一部分内容
- supports<br/>
  如果返回true，则表示该次认证请求可以由当前provider认证<br/>
  如果返回false，则表示该次认证请求无法进行处理，则直接跳过当前provider
  

## 2、AuthenticationProvider实现

#### 2.1 自定义安全认证实体
在Helloworld基础上增加了一些注解，方便我们使用，可以直接替换之前的内容
```aidl
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.dao.pojo
 * @description : 权限数据实体
 * @date : 2019-06-06 13:56
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin_user")
public class AdminUser implements UserDetails {

  /** 数据实体主键 */
  @Id
  @GeneratedValue
  private Integer id;
  /** 用户账号 */
  private String username;
  /** 用户密码 */
  private String password;
  /** 用户角色 */
  private String role;
  /** 用户名称 */
  private String realname;
  /** 用户手机号 */
  private String mobile;
  /** 账号状态 */
  private int state;
  /** 账号描述 */
  private String info;

  /**
  * @Description: 角色列表
  * @Param: []
  * @return: java.util.Collection<? extends org.springframework.security.core.GrantedAuthority>
  * @Author: jiangzh
  * @Date: 2019/6/6
  */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    grantedAuthorities.add(new SimpleGrantedAuthority(role));
    return grantedAuthorities;
  }

  /**
  * @Description: 账户是否过期
  * @Param: []
  * @return: boolean
  * @Author: jiangzh
  * @Date: 2019/6/6
  */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
  * @Description: 账户是否被锁定
  * @Param: []
  * @return: boolean
  * @Author: jiangzh
  * @Date: 2019/6/6
  */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
  * @Description: 授权是否过期
  * @Param: []
  * @return: boolean
  * @Author: jiangzh
  * @Date: 2019/6/6
  */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
  * @Description: 账户是否被禁用
  * @Param: []
  * @return: boolean
  * @Author: jiangzh
  * @Date: 2019/6/6
  */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
```

#### 2.2 自定义Provider
```aidl
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
```

#### 2.3 修改SpringSecurity的相关配置，增加provider链路处理
```aidl
// 注入刚刚自定义的AuthenticationProvider
private final SimpleAuthProvider simpleAuthProvider;
```
```aidl
// 提供一个SpringSecurity默认提供的数据库认证方式
@Bean
DaoAuthenticationProvider daoAuthenticationProvider(){
  DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
  daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
  daoAuthenticationProvider.setUserDetailsService(authService);
  return daoAuthenticationProvider;
}
```
```aidl
// 配置AuthenticationProvider的处理链
@Override
protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  auth.authenticationProvider(simpleAuthProvider).authenticationProvider(daoAuthenticationProvider());
}
```

以上是代码片段，下面贴出修改以后的全部配置
```aidl
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
```
