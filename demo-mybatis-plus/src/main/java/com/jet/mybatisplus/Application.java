package com.jet.mybatisplus;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.apache.naming.factory.BeanFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jet.mybatisplus.mapper")
@NacosPropertySource(dataId = "dev", autoRefreshed = true, type = ConfigType.YAML)
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
    /**
     * 监听Nacos加载
     *
     * @param config
     */
//    @NacosConfigListener(dataId = "dev", type = ConfigType.YAML)
//    public void onMessage(String config) {
//        System.out.println(config);
//    }

}
