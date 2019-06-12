> SpringSecurity构建Hello，演示基础环境构建，以前后端分离情况举例，请勿与一体化项目混淆

SpringSecurity基本流程图
![image-security-process]

## 1、环境构建
#### 1.1 初始化Springboot+SpringSecurity工程【使用https://start.spring.io/】
![image-project-init]

#### 1.2 引入相关依赖包

![image-project-dependencies_01]

pom依赖项内容展示：
![image-project-dependencies_02]

为了方便大家Copy，代码一起贴出来
```
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>2.1.5.RELEASE</version>
  <relativePath/> <!-- lookup parent from repository -->
</parent>

<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
  </dependency>

  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
  </dependency>
  <dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>
```

启动工程，然后随便访问一个页面，就可以看到Security登录页面了
![image-security-login-page]

----

## 2、自定义用户信息

#### 2.1 自定义用户表
```
CREATE TABLE `admin_user`(
`id` int(4) NOT NULL AUTO_INCREMENT,
`username` VARCHAR(100),
`password` VARCHAR(100),
`role` VARCHAR(100),
`realname` VARCHAR(100),
`mobile` VARCHAR(2000),
`state` BIT default 0,
`info` VARCHAR(200),
PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=300;
```

#### 2.2 完成DAO层开发【为了方便，所以就用datajpa做持久化框架了】
*引入依赖包*
```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <scope>runtime</scope>
</dependency>
```

在application.yml中配置数据库信息
```$xslt
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: YourMySQLURL
    username: YourMySQLUsername 
    password: YourMySQLPassword
```

生成数据实体【这里会使用Lombok简化代码】
```$xslt
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.dao.pojo
 * @description : 权限数据实体
 * @create : 2019-06-06 13:56
 **/
@Data
@RequiredArgsConstructor
@Entity
@Table(name = "admin_user")
public class AdminUser implements UserDetails {

  @Id
  @GeneratedValue
  private Integer id;

  private String username;
  private String password;
  private String role;
  private String realname;
  private String mobile;
  private int state;
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

编写DAO接口层
```$xslt
import com.jiangzh.springboot.springsecurity.democode.dao.pojo.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.dao
 * @description : 用户权限接口
 * @create : 2019-06-06 14:02
 **/
public interface AdminUserDAO extends JpaRepository<AdminUser,Integer> {

  @Query(
      value = " select u from AdminUser u where u.username=:username "
  )
  AdminUser login(@Param("username") String username);

}
```

定义逻辑层接口
```$xslt
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.service
 * @description : 权限逻辑层
 * @create : 2019-06-06 14:49
 **/
public interface AuthService extends UserDetailsService {

}
```

实现逻辑层
```$xslt
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
 * @create : 2019-06-06 14:50
 **/
@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private AdminUserDAO adminUserDAO;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AdminUser loginUser = adminUserDAO.login(username);
    if(null == loginUser){
      throw new UsernameNotFoundException(" username invalid ! ");
    }
    return loginUser;
  }
}

```

## 3 SpringSecurity配置
#### 3.1 自定义加密算法
```$xslt
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.utils
 * @description : 自定义密码加解密
 * @create : 2019-06-06 15:10
 **/
public class CustomizeEncoder implements PasswordEncoder {

  /** 
  * @Description: 不进行任何加密处理
  * @Param: [charSequence] 
  * @return: java.lang.String 
  * @Author: jiangzh 
  * @Date: 2019/6/6 
  */ 
  @Override
  public String encode(CharSequence charSequence) {
    return charSequence.toString();
  }

  @Override
  public boolean matches(CharSequence charSequence, String s) {
    return s.equals(charSequence.toString());
  }

}
```

3.2 定义SpringSecurity基础配置
```$xslt
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
 * @create : 2019-06-06 14:57
 **/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private AuthService authService;

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

```


以上内容都完成以后，就可以启动工程，访问/login页面了，SpringSecurity已经给我们提供了一个现成的登录页面
![image-security-login-page]
根据配置，当用户名和密码都写入成功以后，就会直接跳转到index请求了
![image-security-login-success]


## 4 核心内容解析
#### 4.1 UserDetails
UserDetails需要被安全认证实体所继承【讲解中被AdminUser所继承】，SpringSecurity只能识别UserDetails子类对象为安全实体类；
UserDetails默认提供了待实现接口，我在代码中添加了注释，各位可以看一下
另外，大家可以学习一下SpringSecurity的设计方法，这种模式更符合领域模式带给我们的启示

#### 4.2 UserDetailsService
UserDetailsService是SpringSecurity入口，里面提供的loadUserByUsername方法的返回值就是SpringSecurity验证的数据实体，该数据实体必须继承UserDetails
讲解中被AuthService接口所继承

#### 4.3 PasswordEncode
一个正常的权限系统，用户密码都是加密形式，往往我们都是通过对用户输入的密码进行对称加密，然后与数据库内容进行比较来验证密码填写是否正确
passwordEncode就是对用户输入的密码进行对称加密的类

#### 4.4 WebSecurityConfigurerAdapter
这个其实没啥特别可说的，就是一个SpringSecurity的配置类，在HelloWorld中，我们因为了一个比较简单的配置

以下代码可能会读起来有点费劲，我们大致解析一下
```$xslt
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
```

判断哪类请求被忽略，这里配置的是/,也就是/的请求不会被权限拦截
```$xslt
http.authorizeRequests().antMatchers("/").permitAll()
```

限制请求访问用户的role,这里的意思是/manage开头的访问用户，角色必须有ADMIN
```$xslt
antMatchers("/manage/**").hasRole("ADMIN").anyRequest().authenticated()
```

多个条件连接符
```$xslt
and()
```

设置登录的表单名称相关信息,看名字也差不多了，就不解释了
```$xslt
formLogin().loginProcessingUrl("/login").usernameParameter("username").passwordParameter("password").defaultSuccessUrl("/index")
```

其余内容都有注释了，HelloWorld我们就讲到这里了<br/>

---

git地址：https://github.com/jiangzh292679/record/tree/master/springsecurity<br/>
git代码问题：https://github.com/jiangzh292679/record/tree/master/springsecurity/democode<br/>
helloworld tag：v1.0




[image-base-path]:https://github.com/jiangzh292679/record/raw/master/
[image-project-init]:https://github.com/jiangzh292679/record/raw/master/springsecurity/helloword/images/project_init.png
[image-project-dependencies_01]:https://github.com/jiangzh292679/record/raw/master/springsecurity/helloword/images/project_dependencies_01.png
[image-project-dependencies_02]:https://github.com/jiangzh292679/record/raw/master/springsecurity/helloword/images/project_dependencies_02.png
[image-security-login-page]:https://github.com/jiangzh292679/record/raw/master/springsecurity/helloword/images/security_login_page.png
[image-security-login-success]:https://github.com/jiangzh292679/record/raw/master/springsecurity/helloword/images/security_login_success.png
[image-security-process]:https://github.com/jiangzh292679/record/raw/master/springsecurity/helloword/images/springsecurity_process.png