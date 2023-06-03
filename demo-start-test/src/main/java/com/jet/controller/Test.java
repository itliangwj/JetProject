package com.jet.controller;

import com.jet.bean.MyTestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jet
 * @version 1.0
 * @description: TODO
 * @date 2023/6/3 14:40
 */
@RestController
public class Test {

    @Autowired
   private MyTestBean myTestBean;

    @GetMapping("/test")
    public String test(){
        myTestBean.doSomeThing();
        return "Jet";
    }
}
