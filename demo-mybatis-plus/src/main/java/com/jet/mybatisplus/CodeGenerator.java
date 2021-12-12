package com.jet.mybatisplus;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @author Jet
 * @version 1.0
 * @description: TODO
 * @date 2021/12/11 19:56
 */
public class CodeGenerator {

    public static void main(String[] args) {
        generator();
    }

    //用户名：SUPLIS 密码：plansuplis
    //用户：SUPPLAN 密码：plansup
    //用户：CSGX 密码：gxcs
    //用户：CSGXUSER 密码：usercsgx
    //用户：PLATFORM 密码：
    private static final String jdbcUserName = "root";
    private static final String jdbcPassword = "itlwj;123";
    private static final String jdbcUrl = "jdbc:mysql://8.134.39.10:3306/mybatis-plus?useSSL=false";
    //表名，可以设置多个，通过英文逗号分隔
    //数据库用户密码，必须使用表的所有者（Owner）
    private static final String tables = "se_activity";
    private static final String moduleName = "shardingSphere.";//模块名称，分业务，不要漏最后面一个点

    private static final String tablePrefix = "";//表名前缀

    /**
     * 代码生成器的配置常量
     */
    private static final String outPutDir = "/src/main/java";
    //Oracle：jdbc:oracle:thin:@192.168.110.2:1521:ORAPLAN
    //mysql：jdbc:mysql://localhost:3306/mydbone?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8&pinGlobalTxToPhysicalConnection=true&autoReconnect=true


    //oracle.jdbc.OracleDriver
    //oracle.jdbc.driver.OracleDriver
    //com.mysql.cj.jdbc.Driver
    private static final String jdbcDriverClassName = "com.mysql.jdbc.Driver";

    private static final String parentPackage = "com.jet.ss.biz";

    private static final String authorName = jdbcUserName;//作者

    private static final String mapperPattern = "%sDao";//dao文件命名格式
    private static final String mapperName = moduleName + "dao";
    private static final String xmlName = mapperName;

    private static final String serviceNamePattern = "%sService";//Service文件命名格式
    private static final String serviceName =  moduleName + "service";
    private static final String implName =  moduleName + "service.impl";
    private static final String pojoName =  moduleName + "entity";
    private static final String controllerName =  moduleName + "controller";


    // 当前工程路径   配合outPutDir使用，例如多模块开发 Demo/test1，Demo/test2
    // projectPath拿到的是Demo路径，把outPutDir设置成/test1即可
    private static final String projectPath = System.getProperty("user.dir");

    public static void generator() {
        FastAutoGenerator.create(jdbcUrl, jdbcUserName, jdbcPassword)
                .globalConfig(builder -> {
                    builder.author("jet") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D://aa//"); // 指定输出目录
                })
                .packageConfig(builder -> {
//                    builder.parent("com.baomidou.mybatisplus.samples.generator") // 设置父包名
//                            .moduleName("system") // 设置父包模块名
//                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D://")); // 设置mapperXml生成路径

                    builder.parent("com.baomidou.mybatisplus.samples.generator")
                            .moduleName("sys")
                            .entity("po")
                            .service("service")
                            .serviceImpl("service.impl")
                            .mapper("mapper")
                            .xml("mapper.xml")
//                            .mapperXml("mapper.xml")
                            .controller("controller")
                            .other("other")
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D://aa/"))
                            .build();
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables) // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }


}
