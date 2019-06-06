package com.jiangzh.springboot.springsecurity.democode.controller.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.controller.common
 * @description : Controller公共父类
 * @date : 2019-06-06 15:43
 **/
public class BaseController {

  /** 
  * @Description: 获取返回值的通用方法 
  * @Param:  
  * @return:  
  * @Author: jiangzh 
  * @Date:  
  */ 
  protected Map<String,String> obtainResult(String message){
    Map<String,String> result = new HashMap<>(1);
    result.put("result",message);
    return result;
  }

}
