package com.cn.demo;

import com.cn.spring.annotation.Autowired;
import com.cn.spring.annotation.Component;

@Component
public class Demo1 {

    @Autowired
    private Demo2 demo2;

    public void each(){
        demo2.each();
    }
}
