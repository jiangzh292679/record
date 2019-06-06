package com.jiangzh.springboot.springsecurity.democode.controller;

import com.jiangzh.springboot.springsecurity.democode.controller.common.BaseController;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : jiangzh
 * @program : com.jiangzh.springboot.springsecurity.democode.controller
 * @description : 根路径Controller
 * @date : 2019-06-06 15:37
 **/
@RestController
public class RootController extends BaseController {

  @RequestMapping(value = "/",method = RequestMethod.GET)
  public Map<String,String> root(){
    return obtainResult("业务成功");
  }

  @RequestMapping(value = "/index",method = RequestMethod.GET)
  public Map<String,String> index(){
    return obtainResult("index页面访问");
  }

}
