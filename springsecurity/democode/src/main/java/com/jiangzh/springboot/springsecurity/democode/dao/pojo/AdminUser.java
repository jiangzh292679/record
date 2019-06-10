package com.jiangzh.springboot.springsecurity.democode.dao.pojo;

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
