package com.sitaluo.demo.controller;

import com.sitaluo.mvc.annotation.Controller;
import com.sitaluo.mvc.annotation.RequestMapping;

/**
 * @author sitaluo
 * date 2019-10-18
 */
@Controller("userController")
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/list")
    public void list(){
        System.out.println("userController list..");
    }
}
