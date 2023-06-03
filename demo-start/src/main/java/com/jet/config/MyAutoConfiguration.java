package com.jet.config;

import com.jet.bean.MyTestBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jet
 * @version 1.0
 * @description: TODO
 * @date 2023/6/3 14:24
 */
@Configuration
public class MyAutoConfiguration {
    @Bean
    public MyTestBean instanceMyTest(){
        return new MyTestBean();
    }
}
