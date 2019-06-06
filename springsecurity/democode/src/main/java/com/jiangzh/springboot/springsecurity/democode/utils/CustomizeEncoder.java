package com.jiangzh.springboot.springsecurity.democode.utils;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.utils
 * @description : 自定义密码加解密
 * @date : 2019-06-06 15:10
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
