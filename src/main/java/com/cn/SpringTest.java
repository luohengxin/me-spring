package com.cn;

import com.cn.demo.Demo1;
import com.cn.spring.ApplicationContext;

public class SpringTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext();
        Demo1 demo1 = applicationContext.getObject("demo1");
        demo1.each();
    }
}
